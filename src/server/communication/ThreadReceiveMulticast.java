package server.communication;

import server.model.data.Constants;
import server.model.data.Heartbeat;
import server.model.data.syncDB.Abort;
import server.model.data.syncDB.Commit;
import server.model.data.syncDB.Prepare;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.sql.SQLOutput;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static server.model.data.Constants.IP_MULTICAST;
import static server.model.data.Constants.PORT_MULTICAST;

public class ThreadReceiveMulticast extends Thread {

    private final HashMap<Integer, Prepare> dbSync;
    private final List<Heartbeat> listaServidores;
    private SendListaServidoresClientesTCP atualizaClientes;

    public ThreadReceiveMulticast(List<Heartbeat> listaServidores, SendListaServidoresClientesTCP atualizaClientes) {
        this.listaServidores = listaServidores;
        this.atualizaClientes = atualizaClientes;
        this.dbSync = new HashMap<>();
    }

    @Override
    public void run() {
        try (MulticastSocket ms = new MulticastSocket(PORT_MULTICAST)) {

            InetAddress address = InetAddress.getByName(IP_MULTICAST);

            SocketAddress sa = new InetSocketAddress(address, PORT_MULTICAST);
            NetworkInterface ni = NetworkInterface.getByName(Constants.NETWORK_INTERFACE_NAME);

            ms.joinGroup(sa, ni);

            while (true) {
                DatagramPacket dpRec = new DatagramPacket(new byte[4096], 0, 4096);

                ms.receive(dpRec);

                ByteArrayInputStream bais = new ByteArrayInputStream(dpRec.getData());
                ObjectInputStream ois = new ObjectInputStream(bais);

                Object msg = ois.readObject();

                if (msg instanceof Heartbeat h) {
                    h.setIpServer(dpRec.getAddress().getHostAddress());
                    h.setReceivedAt(new Date());
                    recebeHeartBeat(h);
                } else if(msg instanceof Prepare p){
                    System.out.println("PREPARE LEITURA");
                    dbSync.put(p.getIdPrepare(), p);
                    enviaConfirm(dpRec.getAddress().getHostAddress(), p.getPorto());
                } else if(msg instanceof Abort a){
                    System.out.println("ABORT LEITURA");
                    dbSync.remove(a.getIdPrepare());
                }else if(msg instanceof Commit c){
                    System.out.println("COMMIT LEITURA");
                }

            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void enviaConfirm(String ip, int porto) throws IOException {
        DatagramSocket ds = new DatagramSocket();
        byte[] msgBytes = "confirm".getBytes();


        InetAddress ipServer = InetAddress.getByName(ip);

        DatagramPacket dpSend = new DatagramPacket(
                msgBytes,
                msgBytes.length,
                ipServer,
                porto
        );

        ds.send(dpSend);
    }

    void recebeHeartBeat(Heartbeat h) {
        synchronized (listaServidores) {
            // tenta remover a versão anterior deste servidor da lista de servidores, se ele já lá estiver
            listaServidores.remove(h);

            if (h.isDISPONIVEL())
                listaServidores.add(h);

            Collections.sort(listaServidores);

            //TODO: Testing
            for (var a : listaServidores)
                System.out.println(a.getIpServer() + " " + a.getTCP_PORT()
                        + " " + a.getNUMERO_LIGACOES_TCP() + " " + a.getLOCAL_DB_VERSION());
            System.out.println();
            //TODO: Fim Testing
        }
        atualizaClientes.enviarLista();
    }
}
