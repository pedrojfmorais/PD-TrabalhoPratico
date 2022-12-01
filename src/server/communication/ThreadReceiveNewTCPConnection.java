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
    private List<ThreadReceiveTCPMsg> listaClientes;
    private Heartbeat heartbeat;
    private ServerSocket ss;
    private ConnDB connDB;

    public ThreadReceiveNewTCPConnection(Heartbeat heartbeat, List<ThreadReceiveTCPMsg> listaClientes, ConnDB connDB) {
        this.heartbeat = heartbeat;
        this.listaClientes = listaClientes;
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

                ThreadReceiveTCPMsg t = new ThreadReceiveTCPMsg(cliSocket, connDB, heartbeat);
                t.start();

                listaClientes.add(t);
                listThreadsTCPConnections.add(t);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
