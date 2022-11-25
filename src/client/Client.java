package client;

import server.model.data.MsgTcp;
import server.model.data.ServerTCPConnection;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Objects;

import static server.model.data.Constants.*;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        if(args.length != 2)
            System.exit(INVALID_NUMBER_OF_ARGUMENTS);

        final String IP_SERVER = args[0];
        final int PORT_UDP = Integer.parseInt(args[1]);

        ServerTCPConnection serverTCPConnected = null;

        ArrayList<ServerTCPConnection> listaServidoresAEnviar = getActiveServers(IP_SERVER, PORT_UDP);

        //TODO: o que acontece quando não consegue encontrar?, isto devia estar numa função a parte
        for (var server : listaServidoresAEnviar) {
            if(testTCPServer(server)){
                serverTCPConnected = server;
                break;
            }
        }

        System.out.println(serverTCPConnected.getIP() + ":" + serverTCPConnected.getPORT());
    }

    public static ArrayList<ServerTCPConnection> getActiveServers(String ip, int portUDP) throws IOException, ClassNotFoundException {

        DatagramSocket ds = new DatagramSocket();
        ds.setSoTimeout(TIMEOUT_WAIT_TCP_CONFIRMATION);//TODO: catch exception
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

        ds.receive(dpRec);

        ByteArrayInputStream bais = new ByteArrayInputStream(dpRec.getData());
        ObjectInputStream ois = new ObjectInputStream(bais);

        return (ArrayList<ServerTCPConnection>) ois.readObject();
    }

    public static boolean testTCPServer(ServerTCPConnection server) throws IOException, ClassNotFoundException {

        Socket cliSocket = new Socket(server.getIP(), server.getPORT());

        cliSocket.setSoTimeout(TIMEOUT_WAIT_TCP_CONFIRMATION);

        ObjectOutputStream oos = new ObjectOutputStream(cliSocket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(cliSocket.getInputStream());

        oos.writeObject(new MsgTcp(MSG_TCP_CLIENT_TRY_CONNECTION, null));

        MsgTcp msgRec = (MsgTcp) ois.readObject();

        return Objects.equals(msgRec.getMSG_TYPE(), MSG_TCP_CLIENT_CONFIRM_CONNECTION);
    }
}
