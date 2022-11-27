package client.model.fsm.concreteStates;

import client.model.Client;
import client.model.fsm.ClientContext;
import client.model.fsm.ClientState;
import client.model.fsm.IClientState;

public class ConsultaPesquisaEspetaculosState extends ClientAdapter {
    public ConsultaPesquisaEspetaculosState(ClientContext context, Client data) {
        super(context, data);
    }

    //ADMIN
    @Override
    public boolean inserirEspetaculo(String filename) {
        return super.inserirEspetaculo(filename);
    }

    @Override
    public boolean eliminarEspetaculo(int id) {
        return super.eliminarEspetaculo(id);
    }

    @Override
    public boolean editarEstadoEspetaculo(int id) {
        return super.editarEstadoEspetaculo(id);
    }

    //USER
    @Override
    public boolean selecionarEspetaculo(int id) {
        return super.selecionarEspetaculo(id);
    }

    @Override
    public boolean minhasReservas() {
        return super.minhasReservas();
    }

    //TODOS
    @Override
    public String pesquisarEspetaculo(String filtro) {
        return super.pesquisarEspetaculo(filtro);
    }

    @Override
    public boolean editarDadosUtilizador(String... dados) {
        return super.editarDadosUtilizador(dados);
    }

    @Override
    public boolean logout() {
        return super.logout();
    }

    @Override
    public ClientState getState() {
        return ClientState.CONSULTA_PESQUISA_ESPETACULOS;
    }
}
