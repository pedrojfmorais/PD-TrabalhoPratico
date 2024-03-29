package pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm;

import pt.isec.pd.a2018020733.trabalhopratico.client.model.Client;
import pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm.concreteStates.*;

public enum ClientState {
    NO_SERVER_CONNECTED,
    INICIO,
    CONSULTA_PESQUISA_ESPETACULOS,
    MINHAS_RESERVAS,
    SELECIONA_ESPETACULO;

    public IClientState createState(ClientContext context, Client data){
        return switch (this){
            case NO_SERVER_CONNECTED -> new NoServerConnectedState(context, data);
            case INICIO -> new InicioState(context, data);
            case CONSULTA_PESQUISA_ESPETACULOS -> new ConsultaPesquisaEspetaculosState(context, data);
            case MINHAS_RESERVAS -> new MinhasReservasState(context, data);
            case SELECIONA_ESPETACULO -> new SelecionaEspetaculoState(context, data);
        };
    }
}
