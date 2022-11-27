package client.model.fsm.concreteStates;

import client.model.Client;
import client.model.fsm.ClientContext;
import client.model.fsm.ClientState;
import client.model.fsm.IClientState;

public class MinhasReservasState extends ClientAdapter{
    public MinhasReservasState(ClientContext context, Client data) {
        super(context, data);
    }

    @Override
    public boolean pagarReserva(int id) {
        return super.pagarReserva(id);
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
