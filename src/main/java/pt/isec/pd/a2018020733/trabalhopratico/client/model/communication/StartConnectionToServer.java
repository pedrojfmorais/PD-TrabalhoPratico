package pt.isec.pd.a2018020733.trabalhopratico.client.model.communication;

import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.MessagesTCPOperation;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.MsgTcp;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.ServerTCPConnection;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.TypeMsgTCP;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Objects;

import static pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Constants.TIMEOUT_WAIT_TCP_CONFIRMATION;

public final class StartConnectionToServer {
    private StartConnectionToServer() {
    }

    public static ArrayList<ServerTCPConnection> getActiveServers(String ip, int portUDP) throws IOException, ClassNotFoundException {
        try (DatagramSocket ds = new DatagramSocket()) {
            ds.setSoTimeout(TIMEOUT_WAIT_TCP_CONFIRMATION);
            InetAddress ipServer = InetAddress.getByName(ip);

            byte[] msg = new byte[0];

            DatagramPacket dpSend = new DatagramPacket(
                    msg,
                    msg.length,
                    ipServer,
                    portUDP
            );

            ds.send(dpSend);

            DatagramPacket dpRec = new DatagramPacket(new byte[40000], 40000);

            try {
                ds.receive(dpRec);
            } catch (SocketTimeoutException s) {
                return new ArrayList<>();
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(dpRec.getData());
            ObjectInputStream ois = new ObjectInputStream(bais);

            return (ArrayList<ServerTCPConnection>) ois.readObject();
        }
    }

    public static boolean testTCPServer(ServerTCPConnection server) throws IOException, ClassNotFoundException {

        Socket cliSocket;
        try {
            cliSocket = new Socket(server.getIP(), server.getPORT());
        } catch (IOException e) {
            return false;
        }

        cliSocket.setSoTimeout(TIMEOUT_WAIT_TCP_CONFIRMATION);

        ObjectOutputStream oos = new ObjectOutputStream(cliSocket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(cliSocket.getInputStream());

        oos.writeUnshared(new MsgTcp(TypeMsgTCP.CLIENT, MessagesTCPOperation.CLIENT_SERVER_HELLO, null));

        MsgTcp msgRec = (MsgTcp) ois.readObject();

        cliSocket.close();

        return Objects.equals(msgRec.getMsg().get(0), "SERVER_OK");
    }
}
