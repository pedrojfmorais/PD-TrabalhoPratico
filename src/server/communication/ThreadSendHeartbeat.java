package server.communication;

import server.model.Heartbeat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static server.model.Constants.*;

public class ThreadSendHeartbeat extends Thread{

    private final Heartbeat serverData;

    public ThreadSendHeartbeat(Heartbeat serverData) {
        this.serverData = serverData;
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
        DatagramSocket ds = new DatagramSocket();

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

        ds.close();
    }
}
