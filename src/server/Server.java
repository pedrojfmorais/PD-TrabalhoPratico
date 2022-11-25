package server;

import server.communication.ThreadReceiveMulticast;
import server.communication.ThreadReceiveUDPClients;
import server.communication.ThreadSendHeartbeat;
import server.model.data.Heartbeat;
import server.model.jdbc.ConnDB;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static server.model.data.Constants.INVALID_NUMBER_OF_ARGUMENTS;


public class Server {
    public static void main(String[] args) throws SQLException {

        if(args.length != 2)
            System.exit(INVALID_NUMBER_OF_ARGUMENTS);

        final int UDP_PORT = Integer.parseInt(args[0]);
        final String DB_PATH = args[1];
        int myTCPPort = 0;

        ConnDB connDB = new ConnDB();
        System.out.println(connDB.getVersionDB());

//        connDB.addUser("pedrojfmorais", "pedro", "password");
//        connDB.updateUser(2, "pedrojfmorais", "pedro", "pedro");

        System.out.println(connDB.getUserInformation("pedro"));
        System.out.println(connDB.getUserInformation("pedrojfmorais"));
        System.out.println(connDB.getUserInformation("admin"));

        System.out.println(connDB.verifyLogin("pedrojfmorais", "password"));
        System.out.println(connDB.verifyLogin("admin", "admin"));
        System.out.println(connDB.verifyLogin("pedrojfmorais", "123"));

        ArrayList<Thread> allThreads = new ArrayList<>();

        List<Heartbeat> listaServidores = new ArrayList<>();

        //TODO: Testing
//        Heartbeat serverData = new Heartbeat(myTCPPort, true,1,0);
//        Heartbeat serverData = new Heartbeat(myTCPPort, true,1,5);
        Heartbeat serverData = new Heartbeat(myTCPPort, true,1,2);
        //TODO: Fim Testing

        ThreadReceiveMulticast trm = new ThreadReceiveMulticast(listaServidores);
        trm.start();

        allThreads.add(trm);

        ThreadReceiveTCPConnection trtcpc = new ThreadReceiveTCPConnection(serverData);
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