package pt.isec.pd.a2018020733.trabalhopratico.server.communication;

import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Heartbeat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import static pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Constants.*;

public class ThreadSendHeartbeat extends Thread{

    private final Heartbeat serverData;
    static DatagramSocket ds;

    static {
        try {
            ds = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public ThreadSendHeartbeat(Heartbeat serverData) {
        this.serverData = serverData;
    }

    @Override
    public void run() {

        try {
            while (true) {
                enviaHeartBeat(serverData);
                Thread.sleep(TIMEOUT_HEARTBEAT_MILLISECONDS);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public static void enviaHeartBeat(Heartbeat serverData) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(serverData);


        byte[] msgBytes = baos.toByteArray();

        InetAddress ipServer = InetAddress.getByName(IP_MULTICAST);

        DatagramPacket dpSend = new DatagramPacket(
                msgBytes,
                msgBytes.length,
                ipServer,
                PORT_MULTICAST
        );

        ds.send(dpSend);
    }

    public void close(){
        ds.close();
    }
}
