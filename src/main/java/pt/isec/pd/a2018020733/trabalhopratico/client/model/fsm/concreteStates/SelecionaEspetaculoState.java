package pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm.concreteStates;

import pt.isec.pd.a2018020733.trabalhopratico.client.model.Client;
import pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm.ClientContext;
import pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm.ClientState;
import pt.isec.pd.a2018020733.trabalhopratico.client.ui.text.ClientUI;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.MessagesTCPOperation;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.MsgTcp;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.TypeMsgTCP;

import java.io.IOException;
import java.util.List;

public class SelecionaEspetaculoState extends ClientAdapter {
    public SelecionaEspetaculoState(ClientContext context, Client data) {
        super(context, data);
    }

    @Override
    public void mostraLugaresDisponiveis() {
        for (var lugar : data.getEspetaculoSelecionado().getLugares())
            if (lugar.isDisponivel())
                ClientUI.showMessage(lugar.toString(), false);
    }

    @Override
    public void selecionaLugaresPretendidos(List<String> lugares) {
        try {
            data.getTcpConnection().sendMsg(
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.CLIENT_SERVER_SELECIONA_LUGARES,
                            List.of(data.getUser().getUsername(), data.getEspetaculoSelecionado().getId(), lugares))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cancelarReserva() {
        data.setEspetaculoSelecionado(null);
        changeState(ClientState.CONSULTA_PESQUISA_ESPETACULOS);
    }

    @Override
    public ClientState getState() {
        return ClientState.SELECIONA_ESPETACULO;
    }
}
