package server.model;

import server.communication.ThreadReceiveNewTCPConnection;
import server.ThreadRemoveOldServers;
import server.communication.ThreadReceiveMulticast;
import server.communication.ThreadReceiveUDPClients;
import server.communication.ThreadSendHeartbeat;
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
    private ArrayList<Thread> allThreads;
    private Heartbeat serverData;
    private List<Heartbeat> listaServidores;

    public Server(int udp_port, String db_path){

        UDP_PORT = udp_port;
        this.BD_FILE = db_path;
        try {
            connDB = new ConnDB(db_path);
        } catch (SQLException e) {
            connDB = null;
        }

        allThreads = new ArrayList<>();
        listaServidores = new ArrayList<>();
        serverData = new Heartbeat(myTCPPort, false, localDbVersion,0);
    }

    public void start() throws InterruptedException {

        ThreadReceiveMulticast trm = new ThreadReceiveMulticast(listaServidores);
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
        else if(desatualizado)
            //TODO: estabelece conexão TCP e atualiza base de dados
            System.out.println("Estabelece conexão TCP e atualiza base de dados");


        //Quando tudo estiver ok
        serverData.setDISPONIVEL(true);
        startThreads();
    }

    public void testes() throws SQLException {

        System.out.println(connDB.getVersionDB());

//        connDB.addUser("pedrojfmorais", "pedro", "password");
//        connDB.updateUser(2, "pedrojfmorais", "pedro", "pedro");

        System.out.println(connDB.getUserInformation("pedro"));
        System.out.println(connDB.getUserInformation("pedrojfmorais"));
        System.out.println(connDB.getUserInformation("admin"));

        System.out.println(connDB.verifyLogin("pedrojfmorais", "password"));
        System.out.println(connDB.verifyLogin("admin", "admin"));
        System.out.println(connDB.verifyLogin("pedrojfmorais", "123"));

    }

    public void startThreads(){


        ThreadReceiveNewTCPConnection trtcpc = new ThreadReceiveNewTCPConnection(serverData, connDB);
        trtcpc.start();

        allThreads.add(trtcpc);

        ThreadSendHeartbeat tsh = new ThreadSendHeartbeat(serverData);
        tsh.start();

        allThreads.add(tsh);

        ThreadRemoveOldServers tros = new ThreadRemoveOldServers(listaServidores);
        tros.start();

        allThreads.add(tros);

        ThreadReceiveUDPClients trupdc = new ThreadReceiveUDPClients(listaServidores, UDP_PORT);
        trupdc.start();

        allThreads.add(trupdc);
    }
}
