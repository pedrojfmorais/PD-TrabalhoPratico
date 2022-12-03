package server.communication;

import server.model.data.Constants;
import server.model.data.Heartbeat;
import server.model.data.TCP.MessagesTCPOperation;
import server.model.data.TCP.MsgTcp;
import server.model.data.TCP.ServerTCPConnection;
import server.model.data.TCP.TypeMsgTCP;

import java.util.ArrayList;
import java.util.List;

public class SendListaServidoresClientesTCP {

    private final List<Heartbeat> listaServidores;
    private final List<ThreadReceiveTCPMsg> listaClientes;

    public SendListaServidoresClientesTCP(List<Heartbeat> listaServidores, List<ThreadReceiveTCPMsg> listaClientes) {
        this.listaServidores = listaServidores;
        this.listaClientes = listaClientes;
    }

    public void enviarLista(){

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ArrayList<ServerTCPConnection> listaServidoresAEnviar = new ArrayList<>();

        synchronized (listaServidores) {
            for (Heartbeat h : listaServidores)
                if (h.isDISPONIVEL())
                    listaServidoresAEnviar.add(
                            new ServerTCPConnection(h.getIpServer(), h.getTCP_PORT())
                    );
        }

        synchronized (listaClientes) {
            for (var threadCliente : listaClientes)
                if (threadCliente.isAlive())
                    threadCliente.sendMsg(new MsgTcp(
                            TypeMsgTCP.SERVER_ASYNC,
                            MessagesTCPOperation.SERVER_ASYNC_UPDATE_SERVER_LIST,
                            List.of(listaServidoresAEnviar)
                    ));
        }
    }
}
