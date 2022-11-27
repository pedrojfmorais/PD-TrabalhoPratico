package client.model.fsm.concreteStates;

import client.model.Client;
import client.model.fsm.ClientContext;
import client.model.fsm.ClientState;
import server.model.data.LoginStatus;
import server.model.data.MsgTcp;
import server.model.data.TypeMsgTCP;

import java.io.IOException;

public class InicioState extends ClientAdapter {

    private static boolean res = false;

    public static void setRes(boolean res) {
        InicioState.res = res;
    }

    public InicioState(ClientContext context, Client data) {
        super(context, data);
    }

    @Override
    public boolean login(String username, String password) {
        String result = null;
        try {
            data.getTcpConnection().sendMsg(new MsgTcp(
                    TypeMsgTCP.CLIENT,
                    "login," + username + "," + password
            ));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(data.getUser().getStatus() == LoginStatus.WRONG_CREDENTIALS)
            return false;

        changeState(ClientState.CONSULTA_PESQUISA_ESPETACULOS);

        return true;
    }

    @Override
    public boolean register(String username, String nome, String password) {
        boolean result;
        try {
            data.getTcpConnection().sendMsg(new MsgTcp(
                    TypeMsgTCP.CLIENT,
                    "register," + username + "," + nome + "," + password
            ));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        result = res;
        res = false;

        return result;
    }

    @Override
    public ClientState getState() {
        return ClientState.INICIO;
    }
}
