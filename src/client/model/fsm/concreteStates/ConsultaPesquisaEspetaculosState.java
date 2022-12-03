package client.model.fsm.concreteStates;

import client.model.Client;
import client.model.fsm.ClientContext;
import client.model.fsm.ClientState;
import server.model.data.*;
import server.model.data.TCP.MessagesTCPOperation;
import server.model.data.TCP.MsgTcp;
import server.model.data.TCP.TypeMsgTCP;
import server.model.data.viewModels.Espetaculo;
import server.model.data.viewModels.Lugar;

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
    /*@Override
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

        return true;
    }*/

    private String prepareToSave(String token) {
        do {
            // Removes spaces at the beginning or ending
            token = token.trim();

            // Removes quotes
            token = token.replaceAll("\"", "");

        } while (token.startsWith("\"") || token.endsWith("\"") || token.startsWith(" ") || token.endsWith(" "));
        return token;
    }

    /*
     * Lugares de erro no exemplo:
     *  Tipo - Tem um : a separar, em vez de ;
     *  Repete a fila E e F
     */
    @Override
    public boolean inserirEspetaculo(String filename) {

        int id = 0;
        boolean visibilidade = false;
        String designacao = "";
        String tipo = "";
        Date date = new Date();
        int duracao = 0;
        String local = "";
        String localidade = "";
        String pais = "";
        String classificacao = "";
        List<Lugar> lugares = new ArrayList<>();

        String dias = "", horas = "";

        filename = "src/aMarcoTest/espetaculo.csv";

        try (FileReader fr = new FileReader(filename);
             BufferedReader br = new BufferedReader(fr)) {

            String line;
            String linePurpose;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(";");
                linePurpose = prepareToSave(tokens[0]);

                if (linePurpose.equals("Designação")) {
                    designacao = prepareToSave(tokens[1]);
                } else if (linePurpose.equals("Tipo")) {
                    tipo = prepareToSave(tokens[1]);
                } else if (linePurpose.equals("Data")) {
                    dias = prepareToSave(tokens[1]) + "/" +
                            prepareToSave(tokens[2]) + "/" +
                            prepareToSave(tokens[3]);
                } else if (linePurpose.equals("Hora")) {
                    horas = prepareToSave(tokens[1]) + ":" + prepareToSave(tokens[2]);
                } else if (linePurpose.equals("Duração")) {
                    duracao = Integer.parseInt(prepareToSave(tokens[1]));
                } else if (linePurpose.equals("Local")) {
                    local = prepareToSave(tokens[1]);
                } else if (linePurpose.equals("Localidade")) {
                    localidade = prepareToSave(tokens[1]);
                } else if (linePurpose.equals("País")) {
                    pais = prepareToSave(tokens[1]);
                } else if (linePurpose.equals("Classificação etária")) {
                    classificacao = prepareToSave(tokens[1]);
                } else if (linePurpose.equals("Fila")) {
                } else if (linePurpose.length() == 1) {
                    // Remove first
                    String[] modifiedTokens = Arrays.copyOfRange(tokens, 1, tokens.length);

                    for (String tok : modifiedTokens) {
                        // Separate seat from price
                        String[] seat = prepareToSave(tok).split(":");
                        // Create new Lugar
                        Lugar lugar = new Lugar(0, linePurpose.toUpperCase(), seat[0], Double.parseDouble(seat[1]));

                        // If seat has been registered, stop
                        if (lugares.contains(lugar)) {
                            //System.out.println("Errou " + linePurpose.toUpperCase() + " " + seat[0] + " " + seat[1]);
                            return false;
                        }
                        lugares.add(lugar);
                    }
                } else {
                    // If invalid field, stop
                    if (linePurpose.length() != 0)
                        return false;
                    //System.out.println("MORREU " + line + " " + linePurpose);
                }


            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            date = new SimpleDateFormat(Constants.DATE_FORMAT).parse(dias + " " + horas);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.CLIENT_SERVER_INSERIR_ESPETACULO,
                            List.of(new Espetaculo(id, visibilidade, designacao, tipo, date, duracao, local, localidade, pais, classificacao, lugares))
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
