package server.communication;

import server.model.data.MsgTcp;
import server.model.data.TypeMsgTCP;
import server.model.jdbc.ConnDB;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;

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
            default -> new MsgTcp(TypeMsgTCP.REPLY_SERVER, null);
        }
    }

    public void tratarMensagemCliente(MsgTcp msg) throws SQLException, IOException {
        String operation = msg.getMsg();
        if(operation.equals("hello")) {
            sendMsg(new MsgTcp(TypeMsgTCP.REPLY_SERVER, "SERVER_OK"));
            close();
            return;
        }

        String []args = operation.split(",");

        switch(args[0]){
            case "login" ->
                    sendMsg(
                            new MsgTcp(
                                    TypeMsgTCP.REPLY_SERVER,
                                    connDB.verifyLogin(args[1], args[2]).toString()
                            )
                    );
            case "register" -> {
                String msgToSend;
                if(!connDB.verifyUserExists(args[1], args[2])) {

                    connDB.insertUser(args[1], args[2], args[3]);
                    msgToSend = String.valueOf(connDB.getUserInformation(args[1]) != null);

                } else
                    msgToSend = "false";

                sendMsg(
                        new MsgTcp(
                                TypeMsgTCP.REPLY_SERVER,
                                msgToSend
                        )
                );
            }
        }
    }

    public void sendMsg(MsgTcp msgSend) throws IOException {
        oos.reset();
        oos.writeUnshared(msgSend);
    }
}
