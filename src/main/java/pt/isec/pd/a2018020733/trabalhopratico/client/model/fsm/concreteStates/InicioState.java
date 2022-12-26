package pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm.concreteStates;

import pt.isec.pd.a2018020733.trabalhopratico.client.model.Client;
import pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm.ClientContext;
import pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm.ClientState;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.MessagesTCPOperation;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.MsgTcp;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.TypeMsgTCP;

import java.io.IOException;
import java.util.List;

public class InicioState extends ClientAdapter {

    public InicioState(ClientContext context, Client data) {
        super(context, data);
    }

    @Override
    public void login(String username, String password) {
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
