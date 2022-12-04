package server.model;

import server.communication.*;
import server.otherThreads.ThreadRemoveOldServers;
import server.model.data.Constants;
import server.model.data.Heartbeat;
import server.model.jdbc.ConnDB;

import java.io.File;
import java.io.IOException;
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

    private SendListaServidoresClientesTCP atualizaClientes;

    public Server(int udp_port, String db_path){

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
        serverData = new Heartbeat(myTCPPort, false, localDbVersion,0);
        atualizaClientes = new SendListaServidoresClientesTCP(listaServidores, listaClientes);
    }

    public void start() throws InterruptedException {

        ThreadReceiveMulticast trm = new ThreadReceiveMulticast(listaServidores, atualizaClientes);
        trm.start();

        allThreads.add(trm);

        Thread.sleep(Constants.TIMEOUT_STARTUP_PHASE);

        boolean desatualizado = new File(BD_FILE).length() == 0;

        if(!desatualizado)
            for (var h : listaServidores)
                if(h.getLOCAL_DB_VERSION() > localDbVersion){
                    desatualizado = true;
                    break;
                }

        if(desatualizado && listaServidores.isEmpty()) {
            try {
                connDB.createDB();
            } catch (IOException | SQLException e) {
                System.out.println("!!! Erro a criar a base de dados. !!!");
                e.printStackTrace();
            }
        }
        else if(desatualizado) {
            UpdateDatabaseOnStartup udos = new UpdateDatabaseOnStartup(listaServidores, connDB);
            try {
                if(!udos.updateDatabase())
                    throw new IOException();
            } catch (IOException | SQLException e) {
                System.out.println("Erro a estabelecer conexão TCP ou a atualizar a base de dados");
                throw new RuntimeException(e);
            }
        }

        //Quando tudo estiver ok
        serverData.setDISPONIVEL(true);
        startThreads();
    }

    public void startThreads(){


        ThreadReceiveNewTCPConnection trtcpc = new ThreadReceiveNewTCPConnection(
                serverData, listaClientes, connDB, atualizaClientes, listaServidores
        );
        trtcpc.start();

        allThreads.add(trtcpc);

        ThreadSendHeartbeat tsh = new ThreadSendHeartbeat(serverData);
        tsh.start();

        allThreads.add(tsh);

        ThreadRemoveOldServers tros = new ThreadRemoveOldServers(listaServidores, atualizaClientes);
        tros.start();

        allThreads.add(tros);

        ThreadReceiveUDPClients trupdc = new ThreadReceiveUDPClients(listaServidores, UDP_PORT);
        trupdc.start();

        allThreads.add(trupdc);

        ThreadVerificaVersaoDB tvvdb = new ThreadVerificaVersaoDB(
                connDB, listaClientes, listaServidores, serverData, tsh
        );
        tvvdb.start();

        allThreads.add(tvvdb);
    }
}
