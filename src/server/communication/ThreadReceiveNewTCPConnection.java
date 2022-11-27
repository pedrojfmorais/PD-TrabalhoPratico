package server.communication;

import server.model.data.Heartbeat;
import server.model.jdbc.ConnDB;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ThreadReceiveNewTCPConnection extends Thread{

    private List<Thread> listThreadsTCPConnections;
    private Heartbeat heartbeat;
    private ServerSocket ss;
    private ConnDB connDB;

    public ThreadReceiveNewTCPConnection(Heartbeat heartbeat, ConnDB connDB) {
        this.heartbeat = heartbeat;
        this.connDB = connDB;
        listThreadsTCPConnections = new ArrayList<>();
    }

    @Override
    public void run() {

        try {

            ss = new ServerSocket(0);

            synchronized (heartbeat){
                heartbeat.setTCP_PORT(ss.getLocalPort());
            }

            while(true) {

                Socket cliSocket = ss.accept();

                Thread t = new ThreadReceiveTCPMsg(cliSocket, connDB);
                t.start();

                listThreadsTCPConnections.add(t);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
