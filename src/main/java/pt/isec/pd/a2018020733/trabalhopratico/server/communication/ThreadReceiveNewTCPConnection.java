package pt.isec.pd.a2018020733.trabalhopratico.server.communication;

import pt.isec.pd.a2018020733.trabalhopratico.rmi_client.RmiClientRemoteInterface;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Heartbeat;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.jdbc.ConnDB;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ThreadReceiveNewTCPConnection extends Thread{

    private final List<Thread> listThreadsTCPConnections;
    private final List<ThreadReceiveTCPMsg> listaClientes;
    List<RmiClientRemoteInterface> clientsRmi;
    private final List<Heartbeat> listaServidores;
    private SendListaServidoresClientesTCP atualizaClientes;
    private final Heartbeat serverData;
    private ServerSocket ss;
    private final ConnDB connDB;

    public ThreadReceiveNewTCPConnection(Heartbeat serverData, List<ThreadReceiveTCPMsg> listaClientes,
                                         ConnDB connDB, SendListaServidoresClientesTCP atualizaClientes,
                                         List<Heartbeat> listaServidores,
                                         List<RmiClientRemoteInterface> clientsRmi) {
        this.serverData = serverData;
        this.listaClientes = listaClientes;
        this.connDB = connDB;
        this.atualizaClientes = atualizaClientes;
        this.listaServidores = listaServidores;

        listThreadsTCPConnections = new ArrayList<>();
        this.clientsRmi = clientsRmi;

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

                for (var clientRmi : clientsRmi) {
                    clientRmi.receiveNotificationAsync(
                            "Novo cliente via TCP "
                                    + cliSocket.getInetAddress().getHostAddress() + ":" + cliSocket.getPort());
                }

                ThreadReceiveTCPMsg t = new ThreadReceiveTCPMsg(cliSocket, connDB, serverData,
                        atualizaClientes, listaServidores, clientsRmi);
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
