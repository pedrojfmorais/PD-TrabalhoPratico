package pt.isec.pd.a2018020733.trabalhopratico.rmi_client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiClientRemoteInterface extends Remote {

    //VM OPTIONS: -Djava.rmi.server.hostname=127.0.0.1

    void receiveNotificationAsync(String info) throws RemoteException;
}
