package server.communication;

import server.model.data.*;
import server.model.jdbc.ConnDB;

import java.io.IOException;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static server.model.data.Constants.TIMEOUT_HEARTBEAT_MILLISECONDS;

public class ThreadVerificaVersaoDB extends Thread {

    private final ConnDB connDB;
    private final List<ThreadReceiveTCPMsg> listaClientes;
    private final List<Heartbeat> listaServidores;
    private final Heartbeat serverData;
    private final ThreadSendHeartbeat sendHeartbeat;

    public ThreadVerificaVersaoDB(ConnDB connDB, List<ThreadReceiveTCPMsg> listaClientes, List<Heartbeat> listaServidores,
                                  Heartbeat serverData, ThreadSendHeartbeat sendHeartbeat) {
        this.connDB = connDB;
        this.listaClientes = listaClientes;
        this.listaServidores = listaServidores;
        this.serverData = serverData;
        this.sendHeartbeat = sendHeartbeat;
    }

    @Override
    public void run() {

        do {
            try {
                Thread.sleep(TIMEOUT_HEARTBEAT_MILLISECONDS);

                UpdateDatabaseOnStartup.sortListaServidores(listaServidores);
                if (connDB.getVersionDB() < listaServidores.get(listaServidores.size() - 1).getLOCAL_DB_VERSION())
                    updateDatabase();

            } catch (InterruptedException | SQLException | IOException e) {
                throw new RuntimeException(e);
            }

        } while (true);
    }

    private void updateDatabase() throws IOException {

        System.out.println("A dar UPDATE");
        synchronized (serverData) {
            serverData.setDISPONIVEL(false);
        }

        Collections.sort(listaServidores);

        List<ServerTCPConnection> listaServidoresAEnviar = new ArrayList<>();
        synchronized (listaServidores) {

            for (Heartbeat h : listaServidores)
                if (h.isDISPONIVEL())
                    listaServidoresAEnviar.add(
                            new ServerTCPConnection(h.getIpServer(), h.getTCP_PORT())
                    );
        }

        synchronized (listaClientes) {
            for (var cliente : listaClientes) {
                try {
                    cliente.sendMsg(new MsgTcp(
                            TypeMsgTCP.SERVER_ASYNC,
                            MessagesTCPOperation.SERVER_ASYNC_RESET_CONNECTION,
                            List.of(listaServidoresAEnviar)
                    ));
                    cliente.close();

                } catch (SocketException ignored) {
                }
            }
        }

        sendHeartbeat.enviaHeartBeat();

        UpdateDatabaseOnStartup udos = new UpdateDatabaseOnStartup(listaServidores, connDB);
        try {
            if (!udos.updateDatabase())
                throw new IOException();
        } catch (IOException | SQLException e) {
            System.out.println("Erro a estabelecer conexão TCP ou a atualizar a base de dados");
            throw new RuntimeException(e);
        }

        synchronized (serverData) {
            serverData.setDISPONIVEL(true);
        }
    }
}
