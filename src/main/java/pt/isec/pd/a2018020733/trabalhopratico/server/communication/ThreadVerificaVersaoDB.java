package pt.isec.pd.a2018020733.trabalhopratico.server.communication;

import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.*;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.MessagesTCPOperation;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.MsgTcp;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.ServerTCPConnection;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.TypeMsgTCP;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.jdbc.ConnDB;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Constants.TIMEOUT_HEARTBEAT_MILLISECONDS;

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
                serverData.setLOCAL_DB_VERSION(connDB.getVersionDB());
                synchronized (listaServidores) {
                    UpdateDatabaseOnStartup.sortListaServidores(listaServidores);
                    if(listaServidores.size() > 0)
                        if (serverData.getLOCAL_DB_VERSION() < listaServidores.get(0).getLOCAL_DB_VERSION())
                            updateDatabase();
                }
            } catch (InterruptedException | SQLException | IOException e) {
                throw new RuntimeException(e);
            }

        } while (true);
    }

    private void updateDatabase() throws IOException {

        synchronized (serverData) {
            serverData.setDISPONIVEL(false);
        }

        Collections.sort(listaServidores);

        List<ServerTCPConnection> listaServidoresAEnviar = new ArrayList<>();

        for (Heartbeat h : listaServidores)
            if (h.isDISPONIVEL())
                listaServidoresAEnviar.add(
                        new ServerTCPConnection(h.getIpServer(), h.getTCP_PORT())
                );

        synchronized (listaClientes) {
            for (var cliente : listaClientes) {
                cliente.sendMsg(new MsgTcp(
                        TypeMsgTCP.SERVER_ASYNC,
                        MessagesTCPOperation.SERVER_ASYNC_RESET_CONNECTION,
                        List.of(listaServidoresAEnviar)
                ));
                cliente.close();

            }
        }

        ThreadSendHeartbeat.enviaHeartBeat(serverData);

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
            ThreadSendHeartbeat.enviaHeartBeat(serverData);
        }
    }
}
