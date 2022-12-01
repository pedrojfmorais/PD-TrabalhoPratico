package client.model.fsm.concreteStates;

import client.model.Client;
import client.model.fsm.ClientContext;
import client.model.fsm.ClientState;
import client.model.fsm.IClientState;

import java.io.IOException;

abstract class ClientAdapter implements IClientState{

    ClientContext context;
    Client data;

    public ClientAdapter(ClientContext context, Client data) {
        this.context = context;
        this.data = data;
    }

    void changeState(ClientState state){context.changeState(state.createState(context, data));}

    @Override
    public boolean tryConnectToServer() throws IOException, ClassNotFoundException { return false; }

    @Override
    public void login(String username, String password) { }

    @Override
    public void register(String username, String nome, String password) { }

    @Override
    public boolean inserirEspetaculo(String filename) { return false; }

    @Override
    public boolean eliminarEspetaculo(int id) { return false; }

    @Override
    public boolean editarEstadoEspetaculo(int id) { return false; }

    @Override
    public void pesquisarEspetaculo(String filtro) {
        return;
    }

    @Override
    public void editarDadosUtilizador(String... dados) {
    }

    @Override
    public void selecionarEspetaculo(int id) {
    }

    @Override
    public boolean minhasReservas() {
        return false;
    }

    @Override
    public boolean logout() {
        return false;
    }

    @Override
    public void pagarReserva(int id) { }

    @Override
    public void eliminarReservaNaoPaga(int id) { }

    @Override
    public void mostrarReservas(boolean reservaPaga) { }

    @Override
    public boolean voltarPesquisaEspetaculos() {
        return false;
    }

    @Override
    public void mostraLugaresDisponiveis() {
        return;
    }

    @Override
    public void selecionaLugaresPretendidos(String... lugares) { }

    @Override
    public void validarReserva() {
    }

    @Override
    public void cancelarReserva(int id) {
    }
}
