package client.model.fsm.concreteStates;

import client.model.Client;
import client.model.fsm.ClientContext;
import client.model.fsm.ClientState;
import server.model.data.TCP.MessagesTCPOperation;
import server.model.data.TCP.MsgTcp;
import server.model.data.TCP.TypeMsgTCP;

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
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.CLIENT_SERVER_MOSTRA_LUGARES,
                            null)
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void selecionaLugaresPretendidos(String... lugares) {
        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.CLIENT_SERVER_SELECIONA_LUGARES,
                            List.of(lugares))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void validarReserva(int id) {
        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.CLIENT_SERVER_VALIDA_RESERVA,
                            List.of(id))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cancelarReserva(int id) {
        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.CLIENT_SERVER_CANCELAR_RESERVA,
                            List.of(id))
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
