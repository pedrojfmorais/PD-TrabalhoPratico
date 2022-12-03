package client.model.fsm;

import java.io.IOException;

public interface IClientState {

    //NO_SERVER_CONNECTED
    boolean tryConnectToServer(boolean changeState) throws IOException, ClassNotFoundException;

    //INICIO
    void login(String username, String password);
    void register(String username, String nome, String password);

    //CONSULTA_PESQUISA_ESPETACULOS
    boolean inserirEspetaculo(String filename);
    boolean eliminarEspetaculo(int id);
    boolean tornarEspetaculoVisivel(int id);
    void pesquisarEspetaculo(String filtro);
    void editarDadosUtilizador(String username, String nome, String password);
    void selecionarEspetaculo(int id);
    boolean minhasReservas();
    boolean logout();

    //MINHAS_RESERVAS
    void pagarReserva(int id);
    void eliminarReservaNaoPaga(int id);
    void mostrarReservas(boolean reservaPaga);
    boolean voltarPesquisaEspetaculos();

    //SELECIONA_ESPETACULO
    void mostraLugaresDisponiveis();
    void selecionaLugaresPretendidos(String ... lugares);
    void validarReserva();
    void cancelarReserva(int id);

    //Todos
    ClientState getState();

}
