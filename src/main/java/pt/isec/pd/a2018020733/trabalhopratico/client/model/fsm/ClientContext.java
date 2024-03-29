package pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm;

import pt.isec.pd.a2018020733.trabalhopratico.client.model.Client;

import java.io.IOException;
import java.util.List;

public class ClientContext {

    private final Client data;
    private IClientState state;

    public ClientContext(String IP_SERVER, int PORT_UDP){
        this.data = new Client(IP_SERVER, PORT_UDP, this);
        this.state = ClientState.NO_SERVER_CONNECTED.createState(this, data);
    }

    public void changeState(IClientState state){
        this.state = state;
    }

    public Client getData() {
        return data;
    }

    public ClientState getState(){
        return state.getState() == null ? null : state.getState();
    }

    public boolean tryConnectToServer() throws IOException, ClassNotFoundException {
        return state.tryConnectToServer(true);
    }

    public void login(String username, String password){
        state.login(username, password);
    }

    public void register(String username, String nome, String password){
        state.register(username, nome, password);
    }

    public void pesquisaEspetaculos(String filtro){
        state.pesquisarEspetaculo(filtro);
    }
    public void editarDadosUtilizador(String username, String nome, String password){
        state.editarDadosUtilizador(username, nome, password);
    }
    public boolean logout(){return state.logout();}

    public void inserirEspetaculo(String filename){
        state.inserirEspetaculo(filename);
    }
    public void removerEspetaculo(long id){
        state.eliminarEspetaculo(Math.toIntExact(id));
    }

    public void tornarEspetaculoVisivel(long id){state.tornarEspetaculoVisivel(Math.toIntExact(id));}
    public void minhasReservas(){state.minhasReservas();}
    public void consultarReservas(boolean pagas){state.mostrarReservas(pagas);}
    public void voltarConsultaPesquisaEspetaculos(){state.voltarPesquisaEspetaculos();}
    public void pagarReserva(long id){state.pagarReserva(Math.toIntExact(id));}
    public void removerReserva(long id){state.eliminarReservaNaoPaga(Math.toIntExact(id));}
    public void selecionarEspetaculo(long id){state.selecionarEspetaculo(Math.toIntExact(id));}

    public void mostrarLugaresDisponiveis(){state.mostraLugaresDisponiveis();}

    public void selecionarLugaresReserva(List<String> lugares){state.selecionaLugaresPretendidos(lugares);}
    public void cancelarReserva(){state.cancelarReserva();}
}
