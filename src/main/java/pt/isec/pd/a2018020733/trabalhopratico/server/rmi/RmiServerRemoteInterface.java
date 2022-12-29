package pt.isec.pd.a2018020733.trabalhopratico.server.rmi;

import pt.isec.pd.a2018020733.trabalhopratico.rmi_client.RmiClientRemoteInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public interface RmiServerRemoteInterface extends Remote {

    //VM OPTIONS: -Djava.rmi.server.hostname=127.0.0.1

    String SERVER_NAME_PREFIX = "SHOW_SERVICE_";
    String REGISTRY_BIND_NAME_LIST_SERVERS = "listServers";
    String REGISTRY_BIND_NAME_ADD_LISTENER = "addListener";
    String REGISTRY_BIND_NAME_REMOVE_LISTENER = "removeListener";

    List<ServerInfoRmi> getActiveServer() throws RemoteException;
    void addListenerAsyncNotifications(RmiClientRemoteInterface clientRef) throws RemoteException;
    void removeListenerAsyncNotifications(RmiClientRemoteInterface clientRef) throws RemoteException;

}
