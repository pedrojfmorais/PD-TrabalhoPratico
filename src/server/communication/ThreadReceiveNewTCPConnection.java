package server.communication;

import server.model.data.Heartbeat;
import server.model.jdbc.ConnDB;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ThreadReceiveNewTCPConnection extends Thread{

    private final List<Thread> listThreadsTCPConnections;
    private final List<ThreadReceiveTCPMsg> listaClientes;
    private final List<Heartbeat> listaServidores;
    private SendListaServidoresClientesTCP atualizaClientes;
    private final Heartbeat serverData;
    private ServerSocket ss;
    private final ConnDB connDB;

    public ThreadReceiveNewTCPConnection(Heartbeat serverData, List<ThreadReceiveTCPMsg> listaClientes,
                                         ConnDB connDB, SendListaServidoresClientesTCP atualizaClientes,
                                         List<Heartbeat> listaServidores) {
        this.serverData = serverData;
        this.listaClientes = listaClientes;
        this.connDB = connDB;
        this.atualizaClientes = atualizaClientes;
        this.listaServidores = listaServidores;

        listThreadsTCPConnections = new ArrayList<>();

    }

    @Override
    public void run() {

        try {

            ss = new ServerSocket(0);

            synchronized (serverData){
                serverData.setTCP_PORT(ss.getLocalPort());
            }

            while(true) {

                Socket cliSocket = ss.accept();

                ThreadReceiveTCPMsg t = new ThreadReceiveTCPMsg(cliSocket, connDB, serverData,
                        atualizaClientes, listaServidores);
                t.start();

                synchronized (listaClientes) {
                    listaClientes.removeIf(cliente -> !cliente.isAlive());
                    listaClientes.add(t);
                }
                synchronized (listThreadsTCPConnections) {
                    listThreadsTCPConnections.removeIf(cliente -> !cliente.isAlive());
                    listThreadsTCPConnections.add(t);
                }
                synchronized (serverData){
                    serverData.setNUMERO_LIGACOES_TCP(serverData.getNUMERO_LIGACOES_TCP() + 1);
                    ThreadSendHeartbeat.enviaHeartBeat(serverData);
                }
                atualizaClientes.enviarLista();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
