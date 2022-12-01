package client.model.fsm.concreteStates;

import client.model.Client;
import client.model.fsm.ClientContext;
import client.model.fsm.ClientState;
import server.model.data.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

        if(!isAdminUser())
            return false;

        try(FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr)){

            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(";");
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Espetaculo(designacao, tipo, data, hora, duracao, local, localidade, pais, classificacao, lugares)

        /*try {
            data.getTcpConnection().sendMsg(new MsgTcp(
                    TypeMsgTCP.CLIENT,
                    "inserir",
                    List.of(username, nome, password))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }*/
        return true;
    }

    @Override
    public boolean eliminarEspetaculo(int id) {
        if(!isAdminUser())
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
    public boolean editarEstadoEspetaculo(int id) {
        if(!isAdminUser())
            return false;

        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.CLIENT_SERVER_EDITAR_ESPETACULO,
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
                            List.of(filtro))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void editarDadosUtilizador(String... dados) {
        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.CLIENT_SERVER_EDITAR_UTILIZADOR,
                            List.of(dados))
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean logout() {
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
