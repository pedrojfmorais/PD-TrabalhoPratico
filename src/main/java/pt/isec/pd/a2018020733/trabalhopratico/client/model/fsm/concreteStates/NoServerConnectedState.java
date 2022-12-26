package pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm.concreteStates;

import pt.isec.pd.a2018020733.trabalhopratico.client.model.Client;
import pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm.ClientContext;
import pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm.ClientState;

import java.io.IOException;

public class NoServerConnectedState extends ClientAdapter{
    public NoServerConnectedState(ClientContext context, Client data) {
        super(context, data);
    }

    @Override
    public boolean tryConnectToServer(boolean changeState) throws IOException, ClassNotFoundException {
        boolean res = data.tryConnectToServer();

        if(res && changeState)
            changeState(ClientState.INICIO);

        return res;
    }

    @Override
    public ClientState getState() {
        return ClientState.NO_SERVER_CONNECTED;
    }
}
