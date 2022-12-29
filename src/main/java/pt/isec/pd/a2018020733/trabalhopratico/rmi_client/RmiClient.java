package pt.isec.pd.a2018020733.trabalhopratico.rmi_client;

import pt.isec.pd.a2018020733.trabalhopratico.client.utils.PAInput;
import pt.isec.pd.a2018020733.trabalhopratico.server.rmi.RmiServerRemoteInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiClient extends UnicastRemoteObject implements RmiClientRemoteInterface {
    protected RmiClient() throws RemoteException {
    }

    @Override
    public void receiveNotificationAsync(String info) throws RemoteException {
        System.out.println("\n\n---------------Notificação assíncrona---------------");
        System.out.println(info);
        System.out.println("----------------------------------------------------");
        System.out.println("Clique enter para atualizar a consola!\n");
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        //VM OPTIONS: -Djava.rmi.server.hostname=127.0.0.1
        if (args.length != 2) {
            System.out.println("Número de argumentos inválidos. (IP PORTO)");
            return;
        }

        RmiClient rmiClient = new RmiClient();

        String serverRegistration = "rmi://" + RmiServerRemoteInterface.SERVER_NAME_PREFIX + Integer.parseInt(args[1]) + "/";

        Registry r = LocateRegistry.getRegistry(args[0], Registry.REGISTRY_PORT);

        RmiServerRemoteInterface remoteRefGetActiveServer = (RmiServerRemoteInterface) r.lookup(serverRegistration + RmiServerRemoteInterface.REGISTRY_BIND_NAME_LIST_SERVERS);
        RmiServerRemoteInterface remoteRefAddListener = (RmiServerRemoteInterface) r.lookup(serverRegistration + RmiServerRemoteInterface.REGISTRY_BIND_NAME_ADD_LISTENER);
        RmiServerRemoteInterface remoteRefRemoveListener = (RmiServerRemoteInterface) r.lookup(serverRegistration + RmiServerRemoteInterface.REGISTRY_BIND_NAME_REMOVE_LISTENER);

        boolean finish = false;

        do {

            System.out.println("O que deseja fazer fazer:");
            int option = PAInput.chooseOption("O que deseja fazer fazer:",
                    "Lista de servidores ativos",
                    "Receber notificações assincronas",
                    "Deixar de receber notificações assincronas",
                    "Sair");

            switch (option) {
                case 1 -> {
                    for (var server : remoteRefGetActiveServer.getActiveServer())
                        System.out.println(server + System.lineSeparator());
                }
                case 2 -> remoteRefAddListener.addListenerAsyncNotifications(rmiClient);
                case 3 -> remoteRefRemoveListener.removeListenerAsyncNotifications(rmiClient);
                case 4 -> finish = true;
            }

        } while (!finish);

        remoteRefRemoveListener.removeListenerAsyncNotifications(rmiClient);

        System.exit(0);
    }
}
