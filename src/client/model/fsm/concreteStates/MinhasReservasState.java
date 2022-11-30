package client.model.fsm.concreteStates;

import client.model.Client;
import client.model.fsm.ClientContext;
import client.model.fsm.ClientState;
import server.model.data.MsgTcp;
import server.model.data.TypeMsgTCP;

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
                    new MsgTcp(TypeMsgTCP.CLIENT, "pagar reserva", List.of(id))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void eliminarReservaNaoPaga(int id) {
        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(TypeMsgTCP.CLIENT, "eliminar reserva", List.of(id))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void mostrarReservas(boolean reservaPaga) {
        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(TypeMsgTCP.CLIENT, "mostrar reservas", null)
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean voltarPesquisaEspetaculos() {
        return super.voltarPesquisaEspetaculos(); // Muda de estado
    }

    @Override
    public ClientState getState() {
        return ClientState.MINHAS_RESERVAS;
    }
}
