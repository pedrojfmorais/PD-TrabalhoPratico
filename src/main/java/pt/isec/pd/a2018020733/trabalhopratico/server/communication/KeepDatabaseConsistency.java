package pt.isec.pd.a2018020733.trabalhopratico.server.communication;

import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Constants;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Heartbeat;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.MsgTcp;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.syncDB.Abort;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.syncDB.Commit;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.syncDB.Prepare;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.jdbc.ConnDB;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Constants.*;

public class KeepDatabaseConsistency {

    public static boolean sendPrepare(MsgTcp msgTcp, int versionDB, List<Heartbeat> listaServidores
            , ConnDB connDB) throws IOException {

        DatagramSocket ds = new DatagramSocket(4006);
        Prepare prepare = new Prepare(msgTcp, versionDB, 4006);

        AtomicInteger counter = new AtomicInteger();

        DatagramSocket finalDs = ds;
        Runnable t = () -> {

            try {

                finalDs.setSoTimeout(TIMEOUT_DATABASE_CONSISTENCY);
                DatagramPacket dpRec = new DatagramPacket(new byte[256], 0, 256);

                while (true) {

                    finalDs.receive(dpRec);
                    counter.getAndIncrement();
                }
            } catch (SocketTimeoutException ignored){
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        DatagramSocket dsSend = new DatagramSocket();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(prepare);

        byte[] msgBytes = baos.toByteArray();

        InetAddress ipServer = InetAddress.getByName(IP_MULTICAST);

        DatagramPacket dpSend = new DatagramPacket(
                msgBytes,
                msgBytes.length,
                ipServer,
                PORT_MULTICAST
        );

        dsSend.send(dpSend);

        for (int i = 0; i < 2; i++){
            t.run();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(counter.get() == listaServidores.size())
                break;
        }

        if(counter.get() == listaServidores.size()){
            Commit commit = new Commit(prepare.getIdPrepare());

            oos.writeObject(commit);

            byte[] msgBytesAbort = baos.toByteArray();

            DatagramPacket dpCommit = new DatagramPacket(
                    msgBytesAbort,
                    msgBytesAbort.length,
                    ipServer,
                    PORT_MULTICAST);

            dsSend.send(dpCommit);
            dsSend.close();
            return true;
        }else{
            Abort abort = new Abort(prepare.getIdPrepare());

            oos.writeObject(abort);

            byte[] msgBytesAbort = baos.toByteArray();

            DatagramPacket dpAbort = new DatagramPacket(
                    msgBytesAbort,
                    msgBytesAbort.length,
                    ipServer,
                    PORT_MULTICAST);

            dsSend.send(dpAbort);
            dsSend.close();
            return false;
        }
    }
}
