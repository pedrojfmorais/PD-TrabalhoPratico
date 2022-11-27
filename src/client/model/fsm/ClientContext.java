package client.model.fsm;

import client.model.Client;

import java.beans.PropertyChangeSupport;
import java.io.IOException;

public class ClientContext {

    PropertyChangeSupport pcs;
    private Client data;
    private IClientState state;

    public ClientContext(String IP_SERVER, int PORT_UDP){
        this.data = new Client(IP_SERVER, PORT_UDP);
        this.state = ClientState.NO_SERVER_CONNECTED.createState(this, data);
        pcs = new PropertyChangeSupport(this);
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
        return state.tryConnectToServer();
    }

    public boolean login(String username, String password){
        return state.login(username, password);
    }

    public boolean register(String username, String nome, String password){
        return state.register(username, nome, password);
    }

    //TODO: tudo

}
