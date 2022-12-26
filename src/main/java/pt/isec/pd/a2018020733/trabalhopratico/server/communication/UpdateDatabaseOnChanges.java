package pt.isec.pd.a2018020733.trabalhopratico.server.communication;

import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Heartbeat;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.MsgTcp;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.syncDB.Prepare;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

import static pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Constants.IP_MULTICAST;
import static pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Constants.PORT_MULTICAST;

public class UpdateDatabaseOnChanges {
    private final List<Heartbeat> listaServidores;
    int versionDB;
    DatagramSocket ds;

    public UpdateDatabaseOnChanges(List<Heartbeat> listaServidores, int versionDB) {
        this.listaServidores = listaServidores;
        this.versionDB = versionDB;
        try {
            ds = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPrepareMessage(MsgTcp msgTcp) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        Prepare prepare = new Prepare(msgTcp, versionDB,PORT_MULTICAST);
        oos.writeObject(prepare);

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
}
