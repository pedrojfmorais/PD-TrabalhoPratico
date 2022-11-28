package client.model;

import client.model.communication.ThreadTCPWithServer;
import client.model.data.User;
import client.model.fsm.ClientContext;
import server.model.data.LoginStatus;
import server.model.data.ServerTCPConnection;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import static client.model.communication.StartConnectionToServer.getActiveServers;
import static client.model.communication.StartConnectionToServer.testTCPServer;

public class Client {

    ClientContext fsm;
    private User user;
    ThreadTCPWithServer tcpConnection;
    final String IP_SERVER;
    final int PORT_UDP;

    public Client(String IP_SERVER, int PORT_UDP, ClientContext fsm){
        user = new User(null, LoginStatus.WRONG_CREDENTIALS);
        this.IP_SERVER = IP_SERVER;
        this.PORT_UDP = PORT_UDP;
        this.fsm = fsm;
    }

    public User getUser() {
        return user;
    }

    public ThreadTCPWithServer getTcpConnection() {
        return tcpConnection;
    }

    public boolean tryConnectToServer() throws IOException, ClassNotFoundException {
        ServerTCPConnection serverTCPConnected = null;

        ArrayList<ServerTCPConnection> listaServidoresAEnviar = getActiveServers(IP_SERVER, PORT_UDP);
        for (var server : listaServidoresAEnviar) {
            if(testTCPServer(server)){
                serverTCPConnected = server;
                break;
            }
        }

        if(serverTCPConnected == null)
            return false;

        tcpConnection = new ThreadTCPWithServer(
                fsm,
                new Socket(serverTCPConnected.getIP(), serverTCPConnected.getPORT())
        );
        tcpConnection.start();

        //TODO: debug
        System.out.println(serverTCPConnected.getIP() + ":" + serverTCPConnected.getPORT());

        return true;
    }
}
