package client.model.fsm.concreteStates;

import client.model.Client;
import client.model.fsm.ClientContext;
import client.model.fsm.ClientState;
import client.model.fsm.IClientState;

import java.io.IOException;

abstract class ClientAdapter implements IClientState{

    ClientContext context;
    Client data;

    public ClientAdapter(ClientContext context, Client data){
        this.context = context;
        this.data = data;
    }

    void changeState(ClientState state){context.changeState(state.createState(context, data));}

    @Override
    public boolean tryConnectToServer() throws IOException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean login(String username, String password) {
        return false;
    }

    @Override
    public boolean register(String username, String nome, String password) {
        return false;
    }

    @Override
    public boolean inserirEspetaculo(String filename) {
        return false;
    }

    @Override
    public boolean eliminarEspetaculo(int id) {
        return false;
    }

    @Override
    public boolean editarEstadoEspetaculo(int id) {
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
    public boolean pagarReserva(int id) {
        return false;
    }

    @Override
    public boolean eliminarReservaNaoPaga(int id) {
        return false;
    }

    @Override
    public String mostrarReservas(boolean reservaPaga) {
        return null;
    }

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
