package client.model.fsm.concreteStates;

import client.model.Client;
import client.model.fsm.ClientContext;
import client.model.fsm.ClientState;
import client.model.fsm.IClientState;
import server.model.data.LoginStatus;

import java.io.IOException;

abstract class ClientAdapter implements IClientState{

    ClientContext context;
    Client data;

    public ClientAdapter(ClientContext context, Client data) {
        this.context = context;
        this.data = data;
    }

    private boolean isAdminUser() {
        return data.getUser().getStatus() == LoginStatus.SUCCESSFUL_ADMIN_USER;
    }

    void changeState(ClientState state){context.changeState(state.createState(context, data));}

    @Override
    public boolean tryConnectToServer() throws IOException, ClassNotFoundException {
        return false;
    }

    @Override
    public void login(String username, String password) {
    }

    @Override
    public void register(String username, String nome, String password) {
    }

    @Override
    public boolean inserirEspetaculo(String filename) {
        if(isAdminUser())
            return false;
        return true;
    }

    @Override
    public boolean eliminarEspetaculo(int id) {
        if(isAdminUser())
            return false;
        return false;
    }

    @Override
    public boolean editarEstadoEspetaculo(int id) {
        if(isAdminUser())
            return false;
        return false;
    }

    @Override
    public String pesquisarEspetaculo(String filtro) {
        return null;
    }

    @Override
    public boolean editarDadosUtilizador(String... dados) {
        return false;
    }

    @Override
    public boolean selecionarEspetaculo(int id) {
        return false;
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
    public void pagarReserva(int id) { return;}

    @Override
    public void eliminarReservaNaoPaga(int id) {
    }

    @Override
    public void mostrarReservas(boolean reservaPaga) { return;}

    @Override
    public boolean voltarPesquisaEspetaculos() {
        return false;
    }

    @Override
    public String mostraLugaresDisponiveis() {
        return null;
    }

    @Override
    public void selecionaLugaresPretendidos(String... lugares) {

    }

    @Override
    public boolean validarReserva() {
        return false;
    }

    @Override
    public boolean cancelarReserva() {
        return false;
    }
}
