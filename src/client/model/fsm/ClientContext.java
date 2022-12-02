package client.model.fsm;

import client.model.Client;

import java.io.IOException;

public class ClientContext {

    private final Client data;
    private IClientState state;

    public ClientContext(String IP_SERVER, int PORT_UDP){
        this.data = new Client(IP_SERVER, PORT_UDP, this);
        this.state = ClientState.NO_SERVER_CONNECTED.createState(this, data);
    }

    public void changeState(IClientState state){
        this.state = state;
    }

    public Client getData() {
        return data;
    }

    public ClientState getState(){
        return state.getState() == null ? null : state.getState();
    }

    public boolean tryConnectToServer() throws IOException, ClassNotFoundException {
        return state.tryConnectToServer(true);
    }

    public void login(String username, String password){
        state.login(username, password);
    }

    public void register(String username, String nome, String password){
        state.register(username, nome, password);
    }

    public void exit(){data.exit();}

    //TODO: tudo

}
