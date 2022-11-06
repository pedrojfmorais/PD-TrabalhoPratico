package server;

import java.util.ArrayList;
import java.util.List;

import static utils.Constants.INVALID_NUMBER_OF_ARGUMENTS;


public class Server {
    public static void main(String[] args) {

        if(args.length != 2)
            System.exit(INVALID_NUMBER_OF_ARGUMENTS);

        final int UDP_PORT = Integer.parseInt(args[0]);
        final String DB_PATH = args[1];

        ArrayList<Thread> allThreads = new ArrayList<>();

        List<Heartbeat> listaServidores = new ArrayList<>();

        //TODO: Testing
        Heartbeat serverData = new Heartbeat(1, true,1,0);
//        Heartbeat serverData = new Heartbeat(2, true,1,5);
//        Heartbeat serverData = new Heartbeat(3, true,1,2);
        //TODO: Fim Testing

        ThreadReceiveMulticast trm = new ThreadReceiveMulticast(listaServidores);
        trm.start();

        allThreads.add(trm);

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