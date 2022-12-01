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

    ClientContext fsm;
    Socket serverSocket;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    public ThreadTCPWithServer(ClientContext fsm, Socket serverSocket) throws IOException {

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

        if(msg.getOperation().equals("hello") && msg.getMsg().equals("SERVER_OK"))
            return;

        switch (msg.getOperation()){
            case CLIENT_SERVER_LOGIN -> {
                if(msg.getMsg().size() == 2 && msg.getMsg().get(0) instanceof LoginStatus ls){
                    if(ls == LoginStatus.WRONG_CREDENTIALS) {
                        fsm.getData().getUser().setStatus(LoginStatus.WRONG_CREDENTIALS);
                        fsm.getData().getUser().setUsername(null);

                        ClientUI.showMessage("Credenciais Incorretas", false);
                    } else {
                        if(msg.getMsg().get(1) instanceof String username) {
                            fsm.getData().getUser().setStatus(ls);
                            fsm.getData().getUser().setUsername(username);
                            fsm.changeState(ClientState.CONSULTA_PESQUISA_ESPETACULOS.createState(fsm, fsm.getData()));
                            ClientUI.showMessage("Bem vindo " + username, true);
                        }
                    }
                }else
                    ClientUI.showMessage("Erro na comunicação com o servidor!", false);
            }
            case CLIENT_SERVER_REGISTER -> {
                if(msg.getMsg().get(0) instanceof Boolean b && b)
                    ClientUI.showMessage("Utilizador inserido com sucesso", false);
                else
                    ClientUI.showMessage("Erro a inserir o utilizador", false);
            }
        }
    }

    public void sendMsg(MsgTcp msgSend) throws IOException, ClassNotFoundException {
        oos.writeUnshared(msgSend);
    }

    public void close() throws IOException {
        serverSocket.close();
    }
}
