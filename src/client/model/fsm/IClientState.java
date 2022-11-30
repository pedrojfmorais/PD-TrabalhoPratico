package client.model.fsm;

import java.io.IOException;

public interface IClientState {

    //NO_SERVER_CONNECTED
    boolean tryConnectToServer() throws IOException, ClassNotFoundException;

    //INICIO
    void login(String username, String password);
    void register(String username, String nome, String password);

    //CONSULTA_PESQUISA_ESPETACULOS
    boolean inserirEspetaculo(String filename);
    boolean eliminarEspetaculo(int id);
    boolean editarEstadoEspetaculo(int id);
    String pesquisarEspetaculo(String filtro);
    boolean editarDadosUtilizador(String ... dados);
    boolean selecionarEspetaculo(int id);
    boolean minhasReservas();
    boolean logout();

    //MINHAS_RESERVAS
    void pagarReserva(int id);
    void eliminarReservaNaoPaga(int id);
    void mostrarReservas(boolean reservaPaga);
    boolean voltarPesquisaEspetaculos();

    //SELECIONA_ESPETACULO
    String mostraLugaresDisponiveis();
    void selecionaLugaresPretendidos(String ... lugares);
    boolean validarReserva();
    boolean cancelarReserva();

    //Todos
    ClientState getState();

}
