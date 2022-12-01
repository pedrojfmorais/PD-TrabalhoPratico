package client.model.fsm.concreteStates;

import client.model.Client;
import client.model.fsm.ClientContext;
import client.model.fsm.ClientState;
import server.model.data.MsgTcp;
import server.model.data.TypeMsgTCP;

import java.io.IOException;
import java.util.List;

public class SelecionaEspetaculoState extends ClientAdapter {
    public SelecionaEspetaculoState(ClientContext context, Client data) {
        super(context, data);
    }

    @Override
    public void mostraLugaresDisponiveis() {
        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(TypeMsgTCP.CLIENT, "mostra lugares", null)
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void selecionaLugaresPretendidos(String... lugares) {
        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(TypeMsgTCP.CLIENT, "seleciona lugares", List.of(lugares))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void validarReserva(int id) {
        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(TypeMsgTCP.CLIENT, "validar reserva", List.of(id))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cancelarReserva(int id) {
        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(TypeMsgTCP.CLIENT, "cancelar reserva", List.of(id))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ClientState getState() {
        return ClientState.SELECIONA_ESPETACULO;
    }
}
