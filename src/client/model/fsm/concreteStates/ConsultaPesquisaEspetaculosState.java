package client.model.fsm.concreteStates;

import client.model.Client;
import client.model.fsm.ClientContext;
import client.model.fsm.ClientState;
import client.model.fsm.IClientState;
import server.model.data.Espetaculo;
import server.model.data.Lugar;
import server.model.data.MsgTcp;
import server.model.data.TypeMsgTCP;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ConsultaPesquisaEspetaculosState extends ClientAdapter {
    public ConsultaPesquisaEspetaculosState(ClientContext context, Client data) {
        super(context, data);
    }

    //ADMIN
    @Override
    public boolean inserirEspetaculo(String filename) {
        if(!super.inserirEspetaculo(filename))
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
