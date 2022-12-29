package pt.isec.pd.a2018020733.trabalhopratico.server.model;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContextException;
import pt.isec.pd.a2018020733.trabalhopratico.rmi_client.RmiClientRemoteInterface;
import pt.isec.pd.a2018020733.trabalhopratico.server.communication.*;
import pt.isec.pd.a2018020733.trabalhopratico.server.communication.*;
import pt.isec.pd.a2018020733.trabalhopratico.server.otherThreads.ThreadRemoveOldServers;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Constants;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Heartbeat;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.jdbc.ConnDB;
import pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.Application;
import pt.isec.pd.a2018020733.trabalhopratico.server.rmi.RmiServer;
import pt.isec.pd.a2018020733.trabalhopratico.server.rmi.RmiServerRemoteInterface;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final int UDP_PORT;
    private int myTCPPort = 0;
    private int localDbVersion = 0;
    private ConnDB connDB;
    private final String BD_FILE;
    private final ArrayList<Thread> allThreads;
    private Heartbeat serverData;
    private final List<Heartbeat> listaServidores;
    private final List<ThreadReceiveTCPMsg> listaClientes;
    private final List<RmiClientRemoteInterface> clientesRmi;

    private SendListaServidoresClientesTCP atualizaClientes;

    public Server(int udp_port, String db_path) {

        UDP_PORT = udp_port;
        this.BD_FILE = db_path;
        try {
            connDB = new ConnDB(db_path);
            localDbVersion = connDB.getVersionDB();
        } catch (SQLException e) {
            connDB = null;
        }

        allThreads = new ArrayList<>();
        listaServidores = new ArrayList<>();
        listaClientes = new ArrayList<>();
        serverData = new Heartbeat(myTCPPort, UDP_PORT, false, localDbVersion, 0);
        atualizaClientes = new SendListaServidoresClientesTCP(listaServidores, listaClientes);
        clientesRmi = new ArrayList<>();
        try {
            serverData.setIpServer(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() throws InterruptedException {

        ThreadReceiveMulticast trm = new ThreadReceiveMulticast(listaServidores, atualizaClientes);
        trm.start();

        allThreads.add(trm);

        System.out.println("STARTUP");
        Thread.sleep(Constants.TIMEOUT_STARTUP_PHASE);
        System.out.println("FIM STARTUP");
        boolean desatualizado = new File(BD_FILE).length() == 0;

        if (!desatualizado)
            for (var h : listaServidores)
                if (h.getLOCAL_DB_VERSION() > localDbVersion) {
                    desatualizado = true;
                    break;
                }

        if (desatualizado && listaServidores.isEmpty()) {
            try {
                connDB.createDB();
            } catch (IOException | SQLException e) {
                System.out.println("!!! Erro a criar a base de dados. !!!");
                e.printStackTrace();
            }
        } else if (desatualizado) {
            UpdateDatabaseOnStartup udos = new UpdateDatabaseOnStartup(listaServidores, connDB);
            try {
                if (!udos.updateDatabase())
                    throw new IOException();
            } catch (IOException | SQLException e) {
                System.out.println("Erro a estabelecer conexão TCP ou a atualizar a base de dados");
                throw new RuntimeException(e);
            }
        }

        //Quando tudo estiver ok
        serverData.setDISPONIVEL(true);
        listaServidores.add(serverData);

        // API REST
        Thread httpRestApi = new Thread(
                () -> {
                    try {
                        SpringApplication.run(Application.class, String.valueOf(UDP_PORT), BD_FILE);
                        Application.connDB = connDB;
                    } catch (ApplicationContextException e) {
                        System.out.println("PORTO DO SERVIDOR HTTP JÁ ESTÁ A SER UTILIZADO!");
                    }
                }
        );

        httpRestApi.start();

        // RMI
        String serverRegistration = "rmi://" + RmiServerRemoteInterface.SERVER_NAME_PREFIX + UDP_PORT + "/";
        Registry r = null;
        RmiServer rmiServer;
        try {

            rmiServer = new RmiServer(listaServidores, UDP_PORT, clientesRmi);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        try {
            r = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        } catch (RemoteException ignored) {
            try {
                r = LocateRegistry.getRegistry(Registry.REGISTRY_PORT);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            r.rebind(
                    serverRegistration + RmiServerRemoteInterface.REGISTRY_BIND_NAME_LIST_SERVERS,
                    rmiServer
            );
            r.rebind(
                    serverRegistration + RmiServerRemoteInterface.REGISTRY_BIND_NAME_ADD_LISTENER,
                    rmiServer
            );
            r.rebind(
                    serverRegistration + RmiServerRemoteInterface.REGISTRY_BIND_NAME_REMOVE_LISTENER,
                    rmiServer
            );
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        startThreads();
    }

    public void startThreads() {


        ThreadReceiveNewTCPConnection trtcpc = new ThreadReceiveNewTCPConnection(
                serverData, listaClientes, connDB, atualizaClientes, listaServidores, clientesRmi
        );
        trtcpc.start();

        allThreads.add(trtcpc);

        ThreadSendHeartbeat tsh = new ThreadSendHeartbeat(serverData);
        tsh.start();

        allThreads.add(tsh);

        ThreadRemoveOldServers tros = new ThreadRemoveOldServers(listaServidores, atualizaClientes);
        tros.start();

        allThreads.add(tros);

        ThreadReceiveUDPClients trupdc = new ThreadReceiveUDPClients(listaServidores, UDP_PORT, clientesRmi);
        trupdc.start();

        allThreads.add(trupdc);

        ThreadVerificaVersaoDB tvvdb = new ThreadVerificaVersaoDB(
                connDB, listaClientes, listaServidores, serverData, tsh
        );
        tvvdb.start();

        allThreads.add(tvvdb);
    }
}
