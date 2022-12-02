package server.communication;

import server.model.data.Heartbeat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import static server.model.data.Constants.*;

public class ThreadSendHeartbeat extends Thread{

    private final Heartbeat serverData;
    DatagramSocket ds;

    public ThreadSendHeartbeat(Heartbeat serverData) {
        this.serverData = serverData;

        try {
            ds = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {

        try {
            while (true) {
                enviaHeartBeat();
                Thread.sleep(TIMEOUT_HEARTBEAT_MILLISECONDS);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    protected void enviaHeartBeat() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        synchronized (serverData) {
            oos.writeObject(serverData);
        }

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
