package pt.isec.pd.a2018020733.trabalhopratico.server.communication;

import pt.isec.pd.a2018020733.trabalhopratico.rmi_client.RmiClientRemoteInterface;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Heartbeat;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.ServerTCPConnection;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

public class ThreadReceiveUDPClients extends Thread {

    private final List<Heartbeat> listaServidores;
    List<RmiClientRemoteInterface> clientsRmi;
    private final int UDP_PORT;

    public ThreadReceiveUDPClients(List<Heartbeat> listaServidores, int UDP_PORT, List<RmiClientRemoteInterface> clientsRmi) {
        this.listaServidores = listaServidores;
        this.UDP_PORT = UDP_PORT;
        this.clientsRmi = clientsRmi;
    }

    @Override
    public void run() {

        try (DatagramSocket ds = new DatagramSocket(UDP_PORT)) {

            DatagramPacket dpRec = new DatagramPacket(new byte[256], 0, 256);

            while (true) {

                ds.receive(dpRec);

                for (var clientRmi : clientsRmi) {
                    clientRmi.receiveNotificationAsync(
                            "Novo cliente via UDP " + dpRec.getAddress().getHostAddress() + ":" + dpRec.getPort());
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);

                ArrayList<ServerTCPConnection> listaServidoresAEnviar = new ArrayList<>();

                synchronized (listaServidores) {

                    for (Heartbeat h : listaServidores)
                        if (h.isDISPONIVEL())
                            listaServidoresAEnviar.add(
                                    new ServerTCPConnection(h.getIpServer(), h.getTCP_PORT())
                            );
                }

                oos.writeObject(listaServidoresAEnviar);

                byte[] msgBytes = baos.toByteArray();

                DatagramPacket dpSend = new DatagramPacket(
                        msgBytes,
                        msgBytes.length,
                        dpRec.getAddress(),
                        dpRec.getPort()
                );

                ds.send(dpSend);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
