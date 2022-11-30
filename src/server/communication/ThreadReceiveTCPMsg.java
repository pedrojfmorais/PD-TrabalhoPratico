package server.communication;

import server.model.data.LoginStatus;
import server.model.data.MsgTcp;
import server.model.data.TypeMsgTCP;
import server.model.jdbc.ConnDB;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ThreadReceiveTCPMsg extends Thread{

    private ConnDB connDB;
    private Socket cliSocket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean threadContinue = true;

    public ThreadReceiveTCPMsg(Socket cliSocket, ConnDB connDB) throws IOException {
        this.cliSocket = cliSocket;
        this.connDB = connDB;

        oos = new ObjectOutputStream(cliSocket.getOutputStream());
        ois = new ObjectInputStream(cliSocket.getInputStream());
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

            try {
                tratarMensagem(msgRec);
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        }while (threadContinue);

        try {
            cliSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close(){
        threadContinue = false;
    }

    public void tratarMensagem(MsgTcp msg) throws SQLException, IOException {
        switch (msg.getMSG_TYPE()){
            case CLIENT  -> tratarMensagemCliente(msg);
            case CREATE_DB_COPY -> {}//TODO:
            default -> new MsgTcp(TypeMsgTCP.REPLY_SERVER, null, null);
        }
    }

    public void tratarMensagemCliente(MsgTcp msg) throws SQLException, IOException {
        String operation = msg.getOperation();
        if(operation.equals("hello")) {
            sendMsg(new MsgTcp(TypeMsgTCP.REPLY_SERVER, "hello", List.of("SERVER_OK")));
            close();
            return;
        }

        switch(operation){
            case "login" -> {
                LoginStatus ls = LoginStatus.WRONG_CREDENTIALS;
                if (msg.getMsg().get(0) instanceof String username
                        && msg.getMsg().get(1) instanceof String password) {
                    ls = connDB.verifyLogin(username, password);
                }
                sendMsg(
                        new MsgTcp(
                                TypeMsgTCP.REPLY_SERVER,
                                "login",
                                List.of(ls, msg.getMsg().get(0))
                                )
                );
            }
            case "register" -> {

                boolean insertUser = false;

                if (msg.getMsg().get(0) instanceof String username
                        && msg.getMsg().get(1) instanceof String nome
                        && msg.getMsg().get(2) instanceof String password) {

                    if (!connDB.verifyUserExists(username, nome))
                        insertUser = connDB.insertUser(username, nome, password);

                    sendMsg(
                            new MsgTcp(
                                    TypeMsgTCP.REPLY_SERVER,
                                    "register",
                                    List.of(insertUser)
                            )
                    );
                }
            }
        }
    }

    public void sendMsg(MsgTcp msgSend) throws IOException {
        oos.reset();
        oos.writeUnshared(msgSend);
    }
}
