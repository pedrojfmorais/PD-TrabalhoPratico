package client.model.fsm.concreteStates;

import client.model.Client;
import client.model.fsm.ClientContext;
import client.model.fsm.ClientState;
import server.model.data.TCP.MessagesTCPOperation;
import server.model.data.TCP.MsgTcp;
import server.model.data.TCP.TypeMsgTCP;

import java.io.IOException;
import java.util.List;

public class MinhasReservasState extends ClientAdapter{
    public MinhasReservasState(ClientContext context, Client data) {
        super(context, data);
    }

    @Override
    public void pagarReserva(int id) {
        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.CLIENT_SERVER_PAGAR_RESERVA,
                            List.of(id))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void eliminarReservaNaoPaga(int id) {
        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.CLIENT_SERVER_ELIMINAR_RESERVA,
                            List.of(id))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void mostrarReservas(boolean reservaPaga) {
        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.CLIENT_SERVER_MOSTRAR_RESERVAS,
                            List.of(reservaPaga))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean voltarPesquisaEspetaculos() {
        changeState(ClientState.CONSULTA_PESQUISA_ESPETACULOS);
        return true;
    }

    @Override
    public ClientState getState() {
        return ClientState.MINHAS_RESERVAS;
    }
}
