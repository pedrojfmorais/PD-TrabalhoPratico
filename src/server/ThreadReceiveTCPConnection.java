package server;

import utils.MsgTcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static utils.Constants.MSG_TCP_CLIENT_CONFIRM_CONNECTION;
import static utils.Constants.MSG_TCP_CLIENT_TRY_CONNECTION;

public class ThreadReceiveTCPConnection extends Thread{

    private Heartbeat heartbeat;
    private ServerSocket ss;

    public ThreadReceiveTCPConnection(Heartbeat heartbeat) {
        this.heartbeat = heartbeat;
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

                ObjectOutputStream oos = new ObjectOutputStream(cliSocket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(cliSocket.getInputStream());

                MsgTcp msgRec = (MsgTcp) ois.readObject();

                MsgTcp msgSend = verifyMsg(msgRec);

                oos.reset();
                oos.writeUnshared(msgSend);

                cliSocket.close();
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public MsgTcp verifyMsg(MsgTcp msg){

        return switch (msg.getMSG_TYPE()){
            case MSG_TCP_CLIENT_TRY_CONNECTION  -> sendHello();
            default -> new MsgTcp("", null);
        };
    }

    public MsgTcp sendHello(){
        return new MsgTcp(MSG_TCP_CLIENT_CONFIRM_CONNECTION, null);
    }
}
