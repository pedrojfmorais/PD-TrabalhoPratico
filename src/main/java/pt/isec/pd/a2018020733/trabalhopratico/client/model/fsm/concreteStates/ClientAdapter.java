package pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm.concreteStates;

import pt.isec.pd.a2018020733.trabalhopratico.client.model.Client;
import pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm.ClientContext;
import pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm.ClientState;
import pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm.IClientState;

import java.io.IOException;
import java.util.List;

abstract class ClientAdapter implements IClientState{

    ClientContext context;
    Client data;

    public ClientAdapter(ClientContext context, Client data) {
        this.context = context;
        this.data = data;
    }

    void changeState(ClientState state){context.changeState(state.createState(context, data));}

    @Override
    public boolean tryConnectToServer(boolean changeState) throws IOException, ClassNotFoundException { return false; }

    @Override
    public void login(String username, String password) { }

    @Override
    public void register(String username, String nome, String password) { }

    @Override
    public boolean inserirEspetaculo(String filename) { return false; }

    @Override
    public boolean eliminarEspetaculo(int id) { return false; }

    @Override
    public boolean tornarEspetaculoVisivel(int id) { return false; }

    @Override
    public void pesquisarEspetaculo(String filtro) {
    }

    @Override
    public void editarDadosUtilizador(String username, String nome, String password) {
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
    }

    @Override
    public void selecionaLugaresPretendidos(List<String> lugares) { }

    @Override
    public void cancelarReserva() {
    }
}
