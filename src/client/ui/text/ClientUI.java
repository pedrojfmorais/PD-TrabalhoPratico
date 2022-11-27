package client.ui.text;

import client.model.fsm.ClientContext;
import client.utils.PAInput;

import java.io.IOException;

public class ClientUI {

    ClientContext fsm;

    public ClientUI(ClientContext fsm){this.fsm = fsm;}

    private boolean finish = false;

    public void start() throws IOException, ClassNotFoundException {
        while (!finish){
            switch (fsm.getState()){
                case NO_SERVER_CONNECTED -> {
                    if(!fsm.tryConnectToServer()) {
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
        switch (PAInput.chooseOption("Bem vindo! \n", "Login", "Registar", "Sair")){
            case 1 -> {
                if(!fsm.login(
                        PAInput.readString("Insira o username: ", true),
                        PAInput.readString("Insira a password: ", false)
                ))
                    System.out.println("Credenciais incorretas!");
            }
            case 2 -> {
                if(!fsm.register(
                        PAInput.readString("Insira o username: ", true),
                        PAInput.readString("Insira o nome: ", true),
                        PAInput.readString("Insira a password: ", false)
                ))
                    System.out.println("Erro a registar o utilizador!");
            }
            case 3 -> finish = true;
        }
    }

    private void consultaPesquisaEspetaculosUI() {
        System.out.println("consultaPesquisaEspetaculoUI");
        System.out.println(fsm.getData().getUser());
    }

    private void minhasReservasUI() {

    }

    private void selecionaEspetaculoUI() {

    }

}
