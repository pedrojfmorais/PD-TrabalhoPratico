package pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm.concreteStates;

import pt.isec.pd.a2018020733.trabalhopratico.client.model.Client;
import pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm.ClientContext;
import pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm.ClientState;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.*;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.MessagesTCPOperation;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.MsgTcp;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.TCP.TypeMsgTCP;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.viewModels.Espetaculo;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.viewModels.Lugar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ConsultaPesquisaEspetaculosState extends ClientAdapter {
    public ConsultaPesquisaEspetaculosState(ClientContext context, Client data) {
        super(context, data);
    }

    private boolean isAdminUser() {
        return data.getUser().getStatus() == LoginStatus.SUCCESSFUL_ADMIN_USER;
    }

    //ADMIN
    @Override
    public boolean inserirEspetaculo(String filename) {
        Espetaculo espetaculo = data.readFileEspetaculo(filename);
        if (espetaculo == null)
            return false;

        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.CLIENT_SERVER_INSERIR_ESPETACULO,
                            List.of(espetaculo)
                    )
            );

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public boolean eliminarEspetaculo(int id) {
        if (!isAdminUser())
            return false;

        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.CLIENT_SERVER_ELIMINAR_ESPETACULO,
                            List.of(id))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public boolean tornarEspetaculoVisivel(int id) {
        if (!isAdminUser())
            return false;

        try {
            data.getTcpConnection().sendMsg(
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.CLIENT_SERVER_TORNAR_ESPETACULO_VISIVEL,
                            List.of(id))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    //USER
    @Override
    public void selecionarEspetaculo(int id) {
        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.CLIENT_SERVER_SELECIONAR_ESPETACULO,
                            List.of(id))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean minhasReservas() {
        changeState(ClientState.MINHAS_RESERVAS);
        return true;
    }

    //TODOS
    @Override
    public void pesquisarEspetaculo(String filtro) {
        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.CLIENT_SERVER_PESQUISA_ESPETACULO,
                            List.of(filtro, data.getUser().getStatus()))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void editarDadosUtilizador(String username, String nome, String password) {
        try {
            data.getTcpConnection().sendMsg(
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.CLIENT_SERVER_EDITAR_UTILIZADOR,
                            List.of(data.getUser().getUsername(), username, nome, password))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean logout() {
        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.CLIENT_SERVER_LOGOUT,
                            List.of(data.getUser().getUsername())
                    )
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        data.getUser().setStatus(LoginStatus.WRONG_CREDENTIALS);
        data.getUser().setUsername(null);

        changeState(ClientState.INICIO);
        return true;
    }

    @Override
    public ClientState getState() {
        return ClientState.CONSULTA_PESQUISA_ESPETACULOS;
    }
}
