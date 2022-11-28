package client.model.communication;

import client.model.Client;
import client.model.fsm.ClientContext;
import client.model.fsm.ClientState;
import client.model.fsm.concreteStates.InicioState;
import client.ui.text.ClientUI;
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
    ClientContext fsm;
    Socket serverSocket;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    public ThreadTCPWithServer(ClientContext fsm, Socket serverSocket) throws IOException {
        lastMsgSend = null;

        this.fsm = fsm;
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
                    fsm.getData().getUser().setStatus(LoginStatus.WRONG_CREDENTIALS);
                    fsm.getData().getUser().setUsername(null);

                    ClientUI.showMessage("Credenciais Incorretas", false);
                } else {
                    fsm.getData().getUser().setStatus(LoginStatus.valueOf(msg.getMsg()));
                    fsm.getData().getUser().setUsername(lastMsgSend.getMsg().split(",")[1]);

                    fsm.changeState(ClientState.CONSULTA_PESQUISA_ESPETACULOS.createState(fsm, fsm.getData()));

                    ClientUI.showMessage("Bem vindo " + lastMsgSend.getMsg().split(",")[1], true);
                }
            }
            case "register" -> {
                if(Boolean.parseBoolean(msg.getMsg()))
                    ClientUI.showMessage("Utilizador inserido com sucesso", false);
                else
                    ClientUI.showMessage("Erro a inserir o utilizador", false);
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
