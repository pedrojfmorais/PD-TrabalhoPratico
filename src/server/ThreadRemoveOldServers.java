package server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static utils.Constants.TIMEOUT_REMOVE_OLD_SERVERS_MILLISECONDS;

public class ThreadRemoveOldServers extends Thread{

    private final List<Heartbeat> listaServidores;

    public ThreadRemoveOldServers(List<Heartbeat> listaServidores) {
        this.listaServidores = listaServidores;
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
                                    (curTime.getTime() - server.getReceivedAt().getTime())
                                            > TIMEOUT_REMOVE_OLD_SERVERS_MILLISECONDS);
                }

                //TODO: Testing
                System.out.println("Remover Antigos:");
                for (var a : listaServidores)
                    System.out.println(a.getIpServer() + " " + a.getTCP_PORT());
                System.out.println();
                //TODO: Fim Testing
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}
