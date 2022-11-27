package client.model.communication;

import client.model.Client;
import client.model.fsm.concreteStates.InicioState;
import server.model.data.LoginStatus;
import server.model.data.MsgTcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;

public class ThreadTCPWithServer extends Thread{

    MsgTcp lastMsgSend;
    Client client;
    Socket serverSocket;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    public ThreadTCPWithServer(Client client, Socket serverSocket) throws IOException {
        lastMsgSend = null;

        this.client = client;
        this.serverSocket = serverSocket;

        this.oos = new ObjectOutputStream(serverSocket.getOutputStream());
        this.ois = new ObjectInputStream(serverSocket.getInputStream());
    }

    @Override
    public void run() {

        do{
            MsgTcp msgRec = null;
            try {
                msgRec = (MsgTcp) ois.readObject();
            } catch (SocketException e){
                //TODO: acabou conexão
                throw new RuntimeException(e);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            tratarMensagem(msgRec);
        }while(true);
    }

    public void tratarMensagem(MsgTcp msg){

        if(msg.getMsg().equals("SERVER_OK"))
            return;

        switch (lastMsgSend.getMsg().split(",")[0]){
            case "login" -> {
                if(msg.getMsg().equals(LoginStatus.WRONG_CREDENTIALS.toString())) {
                    client.getUser().setStatus(LoginStatus.WRONG_CREDENTIALS);
                    client.getUser().setUsername(null);
                } else {
                    client.getUser().setStatus(LoginStatus.valueOf(msg.getMsg()));
                    client.getUser().setUsername(lastMsgSend.getMsg().split(",")[1]);
                }
            }
            case "register" -> {
                InicioState.setRes(Boolean.parseBoolean(msg.getMsg()));
            }
        }
    }

    public void sendMsg(MsgTcp msgSend) throws IOException, ClassNotFoundException {
        lastMsgSend = msgSend;
        oos.writeUnshared(msgSend);
    }


    public void close() throws IOException {
        serverSocket.close();
    }
}
