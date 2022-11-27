package client.model.fsm.concreteStates;

import client.model.Client;
import client.model.fsm.ClientContext;
import client.model.fsm.ClientState;

import java.io.IOException;

public class NoServerConnectedState extends ClientAdapter{
    public NoServerConnectedState(ClientContext context, Client data) {
        super(context, data);
    }

    @Override
    public boolean tryConnectToServer() throws IOException, ClassNotFoundException {
        boolean res = data.tryConnectToServer();

        if(res)
            changeState(ClientState.INICIO);

        return res;
    }

    @Override
    public ClientState getState() {
        return ClientState.NO_SERVER_CONNECTED;
    }
}
