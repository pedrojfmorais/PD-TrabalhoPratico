package client.model;

import client.model.communication.ThreadTCPWithServer;
import client.model.data.User;
import client.model.fsm.ClientContext;
import client.ui.text.ClientUI;
import server.model.data.LoginStatus;
import server.model.data.ServerTCPConnection;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import static client.model.communication.StartConnectionToServer.getActiveServers;
import static client.model.communication.StartConnectionToServer.testTCPServer;

public class Client {

    ClientContext fsm;
    private final User user;
    ThreadTCPWithServer tcpConnection;
    List<ServerTCPConnection> listaServidores;
    final String IP_SERVER;
    final int PORT_UDP;

    public Client(String IP_SERVER, int PORT_UDP, ClientContext fsm){
        user = new User(null, LoginStatus.WRONG_CREDENTIALS);
        this.IP_SERVER = IP_SERVER;
        this.PORT_UDP = PORT_UDP;
        this.fsm = fsm;

        try {
            listaServidores = getActiveServers(IP_SERVER, PORT_UDP);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public User getUser() {
        return user;
    }

    public ThreadTCPWithServer getTcpConnection() {
        return tcpConnection;
    }

    public boolean tryConnectToServer() throws IOException {
        ServerTCPConnection serverTCPConnected = null;
        for (var server : listaServidores) {
            try {
                if(testTCPServer(server)){
                    serverTCPConnected = server;
                    break;
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        if(serverTCPConnected == null)
            return false;

        tcpConnection = new ThreadTCPWithServer(
                fsm,
                new Socket(serverTCPConnected.getIP(), serverTCPConnected.getPORT()),
                listaServidores
        );
        tcpConnection.setDaemon(true);
        tcpConnection.start();

        ClientUI.showMessage(
                "Conectado ao servidor " + serverTCPConnected.getIP() + ":" + serverTCPConnected.getPORT(),
                true);

        return true;
    }

    public void exit(){
        try {
            tcpConnection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
