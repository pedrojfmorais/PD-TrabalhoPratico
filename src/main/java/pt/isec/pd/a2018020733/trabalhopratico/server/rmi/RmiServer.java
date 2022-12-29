package pt.isec.pd.a2018020733.trabalhopratico.server.rmi;

import pt.isec.pd.a2018020733.trabalhopratico.rmi_client.RmiClientRemoteInterface;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Heartbeat;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RmiServer extends UnicastRemoteObject implements RmiServerRemoteInterface {
    List<Heartbeat> servers;
    private final int UDP_PORT;
    List<RmiClientRemoteInterface> clientsRmi;

    public RmiServer(List<Heartbeat> servers, int UDP_PORT, List<RmiClientRemoteInterface> clientsRmi) throws RemoteException {
        this.servers = servers;
        this.UDP_PORT = UDP_PORT;
        this.clientsRmi = clientsRmi;
    }

    @Override
    public List<ServerInfoRmi> getActiveServer() throws RemoteException {

        List<ServerInfoRmi> serverInfoRmis = new ArrayList<>();

        for(var server : servers){
            serverInfoRmis.add(
                    new ServerInfoRmi(
                            server.getIpServer(),
                            server.getTCP_PORT(),
                            server.getUDP_PORT(),
                            server.getNUMERO_LIGACOES_TCP(),
                            UDP_PORT == server.getUDP_PORT() ? null : server.getReceivedAt()
                    )
            );
        }
        return serverInfoRmis;
    }

    @Override
    public void addListenerAsyncNotifications(RmiClientRemoteInterface clientRef) throws RemoteException {
        clientsRmi.add(clientRef);
    }

    @Override
    public void removeListenerAsyncNotifications(RmiClientRemoteInterface clientRef) throws RemoteException {
        clientsRmi.remove(clientRef);
    }
}
