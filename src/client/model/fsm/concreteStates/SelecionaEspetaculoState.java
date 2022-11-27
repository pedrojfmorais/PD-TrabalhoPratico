package client.model.fsm.concreteStates;

import client.model.Client;
import client.model.fsm.ClientContext;
import client.model.fsm.ClientState;
import client.model.fsm.IClientState;

public class SelecionaEspetaculoState extends ClientAdapter {
    public SelecionaEspetaculoState(ClientContext context, Client data) {
        super(context, data);
    }

    @Override
    public String mostraLugaresDisponiveis() {
        return super.mostraLugaresDisponiveis();
    }

    @Override
    public void selecionaLugaresPretendidos(String... lugares) {
        super.selecionaLugaresPretendidos(lugares);
    }

    @Override
    public boolean validarReserva() {
        return super.validarReserva();
    }

    @Override
    public boolean cancelarReserva() {
        return super.cancelarReserva();
    }

    @Override
    public ClientState getState() {
        return ClientState.SELECIONA_ESPETACULO;
    }
}
