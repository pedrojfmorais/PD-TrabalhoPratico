package pt.isec.pd.a2018020733.trabalhopratico.server.communication;

import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Heartbeat;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.MessagesTCPOperation;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.MsgTcp;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.TypeMsgTCP;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.jdbc.ConnDB;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UpdateDatabaseOnStartup {

    final List<Heartbeat> listaServidores;
    final ConnDB connDB;

    public UpdateDatabaseOnStartup(List<Heartbeat> listaServidores, ConnDB connDB) {
        this.listaServidores = listaServidores;
        this.connDB = connDB;
    }

    public boolean updateDatabase() throws IOException, SQLException {
        sortListaServidores(listaServidores);
        for (var server : listaServidores)
            if (establishConnectionTCP(server.getIpServer(), server.getTCP_PORT()))
                return true;
        return false;
    }


    private boolean establishConnectionTCP(String ip, int porto) throws IOException, SQLException {

        Socket otherServer = new Socket(ip, porto);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(otherServer.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(otherServer.getInputStream());

            oos.writeUnshared(new MsgTcp(
                    TypeMsgTCP.CREATE_DB_COPY,
                    MessagesTCPOperation.CREATE_DB_COPY_PEDIDO,
                    null)
            );

            MsgTcp msgRec;
            try {
                msgRec = (MsgTcp) ois.readObject();
            } catch (SocketException e) {
                return false;
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            if (msgRec.getMSG_TYPE() == TypeMsgTCP.CREATE_DB_COPY
                    && msgRec.getOperation().equals(MessagesTCPOperation.CREATE_DB_COPY_RESPOSTA)) {

                List<List<List<String>>> records = new ArrayList<>();

                for (var tabela : msgRec.getMsg())
                    records.add((List<List<String>>) tabela);
                connDB.clearDB();
                return connDB.importDB(records);
            }

            return false;

        } finally {
            otherServer.close();
        }
    }

    public static void sortListaServidores(List<Heartbeat> listaServidores) {
        listaServidores.sort((o1, o2) -> {
            int comparison = o2.getLOCAL_DB_VERSION() - o1.getLOCAL_DB_VERSION();

            if (comparison == 0)
                return o1.compareTo(o2);
            return comparison;
        });
    }
}
