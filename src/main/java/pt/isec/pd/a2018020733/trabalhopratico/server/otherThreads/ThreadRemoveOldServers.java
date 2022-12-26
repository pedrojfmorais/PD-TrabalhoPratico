package pt.isec.pd.a2018020733.trabalhopratico.server.otherThreads;

import pt.isec.pd.a2018020733.trabalhopratico.server.communication.SendListaServidoresClientesTCP;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Heartbeat;

import java.util.Date;
import java.util.List;

import static pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Constants.TIMEOUT_REMOVE_OLD_SERVERS_MILLISECONDS;

public class ThreadRemoveOldServers extends Thread{

    private final List<Heartbeat> listaServidores;
    private SendListaServidoresClientesTCP atualizaClientes;

    public ThreadRemoveOldServers(List<Heartbeat> listaServidores, SendListaServidoresClientesTCP atualizaClientes) {
        this.listaServidores = listaServidores;
        this.atualizaClientes = atualizaClientes;
    }

    @Override
    public void run() {
        try {

            while (true){
                Thread.sleep(TIMEOUT_REMOVE_OLD_SERVERS_MILLISECONDS);

                Date curTime = new Date();

                synchronized (listaServidores){
                    listaServidores.removeIf(
                            server ->
                                    ((curTime.getTime() - server.getReceivedAt().getTime())
                                            > TIMEOUT_REMOVE_OLD_SERVERS_MILLISECONDS)
                                     || !server.isDISPONIVEL());
                }
                atualizaClientes.enviarLista();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}
