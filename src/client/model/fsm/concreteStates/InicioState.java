package client.model.fsm.concreteStates;

import client.model.Client;
import client.model.fsm.ClientContext;
import client.model.fsm.ClientState;
import server.model.data.LoginStatus;
import server.model.data.MessagesTCPOperation;
import server.model.data.MsgTcp;
import server.model.data.TypeMsgTCP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InicioState extends ClientAdapter {

    public InicioState(ClientContext context, Client data) {
        super(context, data);
    }

    @Override
    public void login(String username, String password) {
        String result = null;
        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.CLIENT_SERVER_LOGIN,
                            List.of(username, password)
                    )
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(String username, String nome, String password) {
        try {
            data.getTcpConnection().sendMsg(new MsgTcp(
                    TypeMsgTCP.CLIENT,
                    MessagesTCPOperation.CLIENT_SERVER_REGISTER,
                    List.of(username, nome, password))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ClientState getState() {
        return ClientState.INICIO;
    }
}
