package client.model;

import client.model.communication.ThreadTCPWithServer;
import client.model.data.User;
import client.model.fsm.ClientContext;
import client.ui.text.ClientUI;
import server.model.data.Constants;
import server.model.data.LoginStatus;
import server.model.data.TCP.MessagesTCPOperation;
import server.model.data.TCP.MsgTcp;
import server.model.data.TCP.ServerTCPConnection;
import server.model.data.TCP.TypeMsgTCP;
import server.model.data.viewModels.Espetaculo;
import server.model.data.viewModels.Lugar;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static client.model.communication.StartConnectionToServer.getActiveServers;
import static client.model.communication.StartConnectionToServer.testTCPServer;

public class Client {

    ClientContext fsm;
    private final User user;
    ThreadTCPWithServer tcpConnection;
    List<ServerTCPConnection> listaServidores;
    final String IP_SERVER;
    final int PORT_UDP;

    public Client(String IP_SERVER, int PORT_UDP, ClientContext fsm) {
        user = new User(null, LoginStatus.WRONG_CREDENTIALS);
        this.IP_SERVER = IP_SERVER;
        this.PORT_UDP = PORT_UDP;
        this.fsm = fsm;

        try {
            listaServidores = getActiveServers(IP_SERVER, PORT_UDP);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public User getUser() {
        return user;
    }

    public ThreadTCPWithServer getTcpConnection() {
        return tcpConnection;
    }

    public boolean tryConnectToServer() throws IOException {
        ServerTCPConnection serverTCPConnected = null;
        for (var server : listaServidores) {
            System.out.println(server.getIP() + " " + server.getPORT());
            try {
                if (testTCPServer(server)) {
                    serverTCPConnected = server;
                    break;
                }
            } catch (SocketException | EOFException ignored) {
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        if (serverTCPConnected == null)
            return false;

        tcpConnection = new ThreadTCPWithServer(
                fsm,
                new Socket(serverTCPConnected.getIP(), serverTCPConnected.getPORT()),
                listaServidores
        );
        tcpConnection.setDaemon(true);
        tcpConnection.start();

        ClientUI.showMessage(
                "Conectado ao servidor " + serverTCPConnected.getIP() + ":" + serverTCPConnected.getPORT(),
                true);

        return true;
    }

    private String prepareToSave(String token) {
        do {
            // Removes spaces at the beginning or ending
            token = token.trim();

            // Removes quotes
            token = token.replaceAll("\"", "");

        } while (token.startsWith("\"") || token.endsWith("\"") || token.startsWith(" ") || token.endsWith(" "));
        return token;
    }

    public Espetaculo readFileEspetaculo(String filename) {

        int id = 0;
        boolean visibilidade = false;
        String designacao = null;
        String tipo = null;
        Date date;
        int duracao = 0;
        String local = null;
        String localidade = null;
        String pais = null;
        String classificacao = null;
        List<Lugar> lugares = new ArrayList<>();

        String dias = null;
        String horas = null;

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
                            ClientUI.showMessage("Existe um Lugar repetido! (Fila " + linePurpose.toUpperCase()
                                    + " Assento " + seat[0] + ")", false);
                            return null;
                        }
                        lugares.add(lugar);
                    }
                }
            }

        } catch (FileNotFoundException ignored) {
            ClientUI.showMessage("Ficheiro não encontrado", false);
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            if (dias == null || horas == null) {
                ClientUI.showMessage("Data ou hora do espetaculo não definida!", false);
                return null;
            }
            date = Constants.formatterDate.parse(dias + " " + horas);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        if (designacao == null) {
            ClientUI.showMessage("Designação do espetaculo não definida!", false);
            return null;
        }
        if (tipo == null) {
            ClientUI.showMessage("Tipo de espetaculo não definida!", false);
            return null;
        }
        if (duracao == 0) {
            ClientUI.showMessage("Duração do espetaculo não definida!", false);
            return null;
        }
        if (local == null) {
            ClientUI.showMessage("Local do espetaculo não definida!", false);
            return null;
        }
        if (localidade == null) {
            ClientUI.showMessage("Localidade do espetaculo não definida!", false);
            return null;
        }
        if (pais == null) {
            ClientUI.showMessage("País do espetaculo não definida!", false);
            return null;
        }
        if (classificacao == null) {
            ClientUI.showMessage("Classificação Etária do espetaculo não definida!", false);
            return null;
        }

        return new Espetaculo(id, visibilidade, designacao, tipo, date, duracao, local,
                localidade, pais, classificacao, lugares);
    }
}
