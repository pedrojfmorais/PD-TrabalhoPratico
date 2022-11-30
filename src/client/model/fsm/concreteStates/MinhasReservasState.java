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
                    new MsgTcp(TypeMsgTCP.CLIENT, "pagarReserva", List.of(id))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean eliminarReservaNaoPaga(int id) {
        return super.eliminarReservaNaoPaga(id);
    }

    @Override
    public String mostrarReservas(boolean reservaPaga) {
        return super.mostrarReservas(reservaPaga);
    }

    @Override
    public boolean voltarPesquisaEspetaculos() {
        return super.voltarPesquisaEspetaculos();
    }

    @Override
    public ClientState getState() {
        return ClientState.MINHAS_RESERVAS;
    }
}
