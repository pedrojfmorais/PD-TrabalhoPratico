package server.communication;

import server.model.data.Heartbeat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.time.chrono.HijrahEra;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static server.model.data.Constants.IP_MULTICAST;
import static server.model.data.Constants.PORT_MULTICAST;

public class ThreadReceiveMulticast extends Thread {

    private final List<Heartbeat> listaServidores;

    public ThreadReceiveMulticast(List<Heartbeat> listaServidores) {
        this.listaServidores = listaServidores;
    }

    @Override
    public void run() {
        try {
            MulticastSocket ms = new MulticastSocket(PORT_MULTICAST);

            InetAddress address = InetAddress.getByName(IP_MULTICAST);

            ms.joinGroup(address);
            //TODO: alterar para versão comentada
            /*
            SocketAddress sa = new InetSocketAddress(address, PORT_MULTICAST);
            NetworkInterface ni = NetworkInterface.getByName("wlan1");

            ms.joinGroup(sa, ni);
            */

            while(true) {
                DatagramPacket dpRec = new DatagramPacket(new byte[256], 0, 256);

                ms.receive(dpRec);

                ByteArrayInputStream bais = new ByteArrayInputStream(dpRec.getData());
                ObjectInputStream ois = new ObjectInputStream(bais);

                Object msg = ois.readObject();

                if(msg instanceof Heartbeat h){
                    h.setIpServer(dpRec.getAddress().getHostAddress());
                    h.setReceivedAt(new Date());
                    recebeHeartBeat(h);
                }

            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    void recebeHeartBeat(Heartbeat h){
        synchronized (listaServidores) {
            // tenta remover a versão anterior deste servidor da lista de servidores, se ele já lá estiver
            listaServidores.remove(h);

            if(h.isDISPONIVEL())
                listaServidores.add(h);

            Collections.sort(listaServidores);

            //TODO: Testing
            for (var a : listaServidores)
                System.out.println(a.getIpServer() + " " + a.getTCP_PORT());
            System.out.println();
            //TODO: Fim Testing
        }
    }
}
