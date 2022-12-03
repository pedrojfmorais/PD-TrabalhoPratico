package client.ui.text;

import client.model.fsm.ClientContext;
import client.utils.PAInput;

import java.io.IOException;

public class ClientUI {

    ClientContext fsm;

    public ClientUI(ClientContext fsm) {
        this.fsm = fsm;
    }

    private boolean finish = false;

    public static void showMessage(String msg, boolean atualizar) {
        System.out.println("\n!!!" + msg + "!!!\n");
        if (atualizar)
            System.out.println("Clique enter para atualizar a consola!\n");
    }

    public void start() throws IOException, ClassNotFoundException {
        while (!finish) {
            switch (fsm.getState()) {
                case NO_SERVER_CONNECTED -> {
                    if (!fsm.tryConnectToServer()) {
                        System.out.println("Não foi possivel encontrar nenhum servidor ativo!");
                        finish = true;
                    }
                }
                case INICIO -> inicioUI();
                case CONSULTA_PESQUISA_ESPETACULOS -> consultaPesquisaEspetaculosUI();
                case MINHAS_RESERVAS -> minhasReservasUI();
                case SELECIONA_ESPETACULO -> selecionaEspetaculoUI();
            }
        }
    }

    private void inicioUI() {
        switch (PAInput.chooseOption("Bem vindo! \n", "Login", "Registar", "Sair")) {
            case 1 -> fsm.login(
                    PAInput.readString("Insira o username: ", true),
                    PAInput.readString("Insira a password: ", false)
            );
            case 2 -> fsm.register(
                    PAInput.readString("Insira o username: ", true),
                    PAInput.readString("Insira o nome: ", true),
                    PAInput.readString("Insira a password: ", false)
            );
            case 3 -> {
                finish = true;
            }
        }
    }

    private void consultaPesquisaEspetaculosUI() {
        switch (fsm.getData().getUser().getStatus()) {

            case SUCCESSFUL_NORMAL_USER -> {
                switch (PAInput.chooseOption("Pesquisa e Consulta de Espetaculo",
                        "Pesquisar", "Editar dados conta utilizador",
                        "Ver Minhas Reservas", "Logout")) {
                    case 1 -> fsm.pesquisaEspetaculos(
                            PAInput.readString("Insira o filtro a procurar: ", false, true)
                    );
                    case 2 -> fsm.editarDadosUtilizador(
                            PAInput.readString("Insira o novo username: ", false),
                            PAInput.readString("Insira o novo nome: ", false),
                            PAInput.readString("Insira a nova password: ", false)
                    );
                    case 3 -> fsm.minhasReservas();
                    case 4 -> fsm.logout();
                }
            }
            case SUCCESSFUL_ADMIN_USER -> {
                switch (PAInput.chooseOption("Pesquisa e Consulta de Espetaculo",
                        "Inserir Espetaculo", "Remover Espetaculo", "Tornar Espetaculo Visivel",
                        "Pesquisar", "Editar dados conta utilizador", "Logout")) {
                    case 1 -> {
                        //TODO: Mais logo
                    }
                    case 2 -> fsm.removerEspetaculo(
                            PAInput.readLong("Insira o id do espetaculo a remover: ")
                    );
                    case 3 -> fsm.tornarEspetaculoVisivel(
                            PAInput.readLong("Insira o id do espetaculo a tornar visivel: ")
                    );
                    case 4 -> fsm.pesquisaEspetaculos(
                            PAInput.readString("Insira o filtro a procurar: ", false, true)
                    );
                    case 5 -> fsm.editarDadosUtilizador(
                            PAInput.readString("Insira o novo username: ", false),
                            PAInput.readString("Insira o novo nome: ", false),
                            PAInput.readString("Insira a nova password: ", false)
                    );
                    case 6 -> fsm.logout();
                }
            }
        }
    }

    private void minhasReservasUI() {
        switch (PAInput.chooseOption("Minhas Reservas",
                "Ver Reservas Pagas", "Ver Reservas Por Pagar", "Pagar Reserva",
                "Remover Reserva Não Paga", "Voltar")) {
            case 1 -> fsm.consultarReservas(true);
            case 2 -> fsm.consultarReservas(false);
            case 3 -> fsm.pagarReserva(
                    PAInput.readLong("Insira o ID da Reserva a pagar: ")
            );
            case 4 -> fsm.removerReserva(
                    PAInput.readLong("Insira o ID da Reserva a remover: ")
            );
            case 5 -> fsm.voltarConsultaPesquisaEspetaculos();

        }
    }

    private void selecionaEspetaculoUI() {

    }

}
