package server.communication;

import server.model.data.LoginStatus;
import server.model.data.TCP.MessagesTCPOperation;
import server.model.data.TCP.MsgTcp;
import server.model.data.TCP.TypeMsgTCP;
import server.model.data.*;
import server.model.data.viewModels.Espetaculo;
import server.model.data.viewModels.Reserva;
import server.model.jdbc.ConnDB;
import server.otherThreads.ThreadPagamentoReserva;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ThreadReceiveTCPMsg extends Thread {

    private final ConnDB connDB;
    private final Socket cliSocket;
    private final Heartbeat serverData;
    private final List<Heartbeat> listaServidores;
    private SendListaServidoresClientesTCP atualizaClientes;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    private boolean threadContinue = true;

    public ThreadReceiveTCPMsg(Socket cliSocket, ConnDB connDB,
                               Heartbeat serverData, SendListaServidoresClientesTCP atualizaClientes,
                               List<Heartbeat> listaServidores) throws IOException {
        this.cliSocket = cliSocket;
        this.connDB = connDB;
        this.serverData = serverData;
        this.atualizaClientes = atualizaClientes;
        this.listaServidores = listaServidores;

        oos = new ObjectOutputStream(cliSocket.getOutputStream());
        ois = new ObjectInputStream(cliSocket.getInputStream());
    }

    @Override
    public void run() {

        do {
            MsgTcp msgRec;
            try {
                msgRec = (MsgTcp) ois.readObject();
            } catch (SocketException | EOFException e) {
                close();
                break;
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            try {
                synchronized (connDB) {
                    tratarMensagem(msgRec);
                    if (connDB.getVersionDB() != serverData.getLOCAL_DB_VERSION()) {
                        serverData.setLOCAL_DB_VERSION(connDB.getVersionDB());
                        ThreadSendHeartbeat.enviaHeartBeat(serverData);
                    }
                }

            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        } while (threadContinue);

        try {
            cliSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        synchronized (serverData) {
            serverData.setNUMERO_LIGACOES_TCP(serverData.getNUMERO_LIGACOES_TCP() - 1);
            try {
                ThreadSendHeartbeat.enviaHeartBeat(serverData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        atualizaClientes.enviarLista();
    }

    public void close() {
        threadContinue = false;
    }

    public void tratarMensagem(MsgTcp msg) throws SQLException, IOException {
        switch (msg.getMSG_TYPE()) {
            case CLIENT -> tratarMensagemCliente(msg);
            case CREATE_DB_COPY -> sendMsg(new MsgTcp(
                    TypeMsgTCP.CREATE_DB_COPY,
                    MessagesTCPOperation.CREATE_DB_COPY_RESPOSTA,
                    new ArrayList<>(connDB.exportDB())
            ));
            default -> new MsgTcp(TypeMsgTCP.REPLY_SERVER, null, null);
        }
    }

    public void tratarMensagemCliente(MsgTcp msg) throws SQLException {
        MessagesTCPOperation operation = msg.getOperation();
        if (operation == MessagesTCPOperation.CLIENT_SERVER_HELLO) {
            sendMsg(new MsgTcp(
                    TypeMsgTCP.REPLY_SERVER,
                    MessagesTCPOperation.CLIENT_SERVER_HELLO,
                    List.of("SERVER_OK")));
            close();
            return;
        }

        switch (operation) {
            case CLIENT_SERVER_LOGIN -> {
                LoginStatus ls = LoginStatus.WRONG_CREDENTIALS;
                if (msg.getMsg().get(0) instanceof String username
                        && msg.getMsg().get(1) instanceof String password) {
                    ls = connDB.verifyLogin(username, password);
                }
                sendMsg(
                        new MsgTcp(
                                TypeMsgTCP.REPLY_SERVER,
                                MessagesTCPOperation.CLIENT_SERVER_LOGIN,
                                List.of(ls, msg.getMsg().get(0))
                        )
                );
            }
            case CLIENT_SERVER_LOGOUT -> {
                if (msg.getMsg().get(0) instanceof String username)
                    connDB.logout(username);

            }
            case CLIENT_SERVER_REGISTER -> {

                boolean insertUser = false;

                if (msg.getMsg().get(0) instanceof String username
                        && msg.getMsg().get(1) instanceof String nome
                        && msg.getMsg().get(2) instanceof String password) {

                    if (!connDB.verifyUserExists(username, nome))
                        insertUser = connDB.insertUser(username, nome, password);

                    sendMsg(
                            new MsgTcp(
                                    TypeMsgTCP.REPLY_SERVER,
                                    MessagesTCPOperation.CLIENT_SERVER_REGISTER,
                                    List.of(insertUser)
                            )
                    );
                }

            }
            case CLIENT_SERVER_EDITAR_UTILIZADOR -> {
                boolean insertUser = false;

                if (msg.getMsg().get(0) instanceof String oldUsername
                        && msg.getMsg().get(1) instanceof String username
                        && msg.getMsg().get(2) instanceof String nome
                        && msg.getMsg().get(3) instanceof String password) {

                    if (!connDB.verifyUserExists(username, nome)) {
                        insertUser = connDB.updateUser(oldUsername, username, nome, password);
                    }
                }
                sendMsg(
                        new MsgTcp(
                                TypeMsgTCP.REPLY_SERVER,
                                MessagesTCPOperation.CLIENT_SERVER_EDITAR_UTILIZADOR,
                                List.of(insertUser, msg.getMsg().get(1))
                        )
                );
            }
            case CLIENT_SERVER_INSERIR_ESPETACULO -> {
                boolean result;
                Espetaculo espetaculo = (Espetaculo) msg.getMsg().get(0);
                result = connDB.inserirEspetaculo(espetaculo);

                sendMsg(
                        new MsgTcp(
                                TypeMsgTCP.REPLY_SERVER,
                                MessagesTCPOperation.CLIENT_SERVER_INSERIR_ESPETACULO,
                                List.of(result)
                        )
                );
            }
            case CLIENT_SERVER_ELIMINAR_ESPETACULO -> {
                boolean result = false;
                int id = (int) msg.getMsg().get(0);
                if (connDB.verifyEspetaculoExists(id))
                    result = connDB.eliminarEspetaculo(id);

                sendMsg(
                        new MsgTcp(
                                TypeMsgTCP.REPLY_SERVER,
                                MessagesTCPOperation.CLIENT_SERVER_ELIMINAR_ESPETACULO,
                                List.of(result)
                        )
                );
            }
            case CLIENT_SERVER_TORNAR_ESPETACULO_VISIVEL -> {
                boolean result = false;
                int id = (int) msg.getMsg().get(0);
                if (connDB.verifyEspetaculoExists(id))
                    result = connDB.tornarEspetaculoVisivel(id);

                sendMsg(
                        new MsgTcp(
                                TypeMsgTCP.REPLY_SERVER,
                                MessagesTCPOperation.CLIENT_SERVER_TORNAR_ESPETACULO_VISIVEL,
                                List.of(result)
                        )
                );
            }
            case CLIENT_SERVER_SELECIONAR_ESPETACULO -> {
                if (msg.getMsg().get(0) instanceof Integer idEspetaculo) {
                    Espetaculo espetaculo;
                    try {
                        espetaculo = connDB.getEspetaculo(idEspetaculo);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    long diffInMillies = espetaculo.getData().getTime() - new Date().getTime();
                    long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                    if (diff < 24)
                        espetaculo = null;

                    if (espetaculo != null)
                        sendMsg(
                                new MsgTcp(
                                        TypeMsgTCP.REPLY_SERVER,
                                        MessagesTCPOperation.CLIENT_SERVER_SELECIONAR_ESPETACULO,
                                        List.of(espetaculo)
                                )
                        );
                    else
                        sendMsg(
                                new MsgTcp(
                                        TypeMsgTCP.REPLY_SERVER,
                                        MessagesTCPOperation.CLIENT_SERVER_SELECIONAR_ESPETACULO,
                                        null
                                )
                        );
                }
            }
            case CLIENT_SERVER_PESQUISA_ESPETACULO -> {
                if (msg.getMsg().get(0) instanceof String filtro
                        && msg.getMsg().get(1) instanceof LoginStatus ls) {

                    List<Espetaculo> espetaculos;
                    try {
                        espetaculos = connDB.pesquisarEspetaculo(filtro,
                                ls == LoginStatus.SUCCESSFUL_ADMIN_USER);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    sendMsg(
                            new MsgTcp(
                                    TypeMsgTCP.REPLY_SERVER,
                                    MessagesTCPOperation.CLIENT_SERVER_PESQUISA_ESPETACULO,
                                    List.of(espetaculos)
                            )
                    );
                }
            }
            case CLIENT_SERVER_PAGAR_RESERVA -> {
                boolean result = false;
                int id = (int) msg.getMsg().get(0);
                if (connDB.verifyReservaExists(id))
                    result = connDB.pagarReserva(id);

                sendMsg(
                        new MsgTcp(
                                TypeMsgTCP.REPLY_SERVER,
                                MessagesTCPOperation.CLIENT_SERVER_PAGAR_RESERVA,
                                List.of(result)
                        )
                );
            }
            case CLIENT_SERVER_ELIMINAR_RESERVA -> {
                boolean result = false;
                int id = (int) msg.getMsg().get(0);
                if (connDB.verifyReservaExists(id))
                    result = connDB.eliminarReserva(id);

                sendMsg(
                        new MsgTcp(
                                TypeMsgTCP.REPLY_SERVER,
                                MessagesTCPOperation.CLIENT_SERVER_ELIMINAR_RESERVA,
                                List.of(result)
                        )
                );
            }
            case CLIENT_SERVER_MOSTRAR_RESERVAS -> {
                boolean reservaPaga = (boolean) msg.getMsg().get(0);

                List<Reserva> reservas;
                try {
                    reservas = new ArrayList<>(
                            connDB.getReservas(
                                    reservaPaga,
                                    (String) msg.getMsg().get(1)
                            )
                    );
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                sendMsg(
                        new MsgTcp(
                                TypeMsgTCP.REPLY_SERVER,
                                MessagesTCPOperation.CLIENT_SERVER_MOSTRAR_RESERVAS,
                                List.of(reservas)
                        )
                );
            }
            case CLIENT_SERVER_SELECIONA_LUGARES -> {

                if (msg.getMsg().get(0) instanceof String username
                        && msg.getMsg().get(1) instanceof Integer idEspetaculo
                        && msg.getMsg().get(2) instanceof List<?> lugares) {

                    String utilizador = connDB.getUserInformation(username);
                    List<Boolean> resInserirLugar = new ArrayList<>();
                    int idReserva = 0;

                    if (utilizador != null) {
                        int idUtilizador = Integer.parseInt(utilizador.split(",")[0]);
                        idReserva = connDB.criarReserva(idUtilizador, idEspetaculo);

                        if (idReserva != 0) {
                            for (var lugar : lugares)
                                if (lugar instanceof String str) {
                                    String[] dadosLugar = str.split(":");

                                    resInserirLugar.add(
                                            connDB.selecionarLugar(idReserva, idEspetaculo, dadosLugar[0], dadosLugar[1])
                                    );
                                }
                        }
                    }
                    if (utilizador == null || idReserva == 0 || resInserirLugar.isEmpty()
                            || resInserirLugar.contains(false) && !resInserirLugar.contains(true))
                        sendMsg(
                                new MsgTcp(
                                        TypeMsgTCP.REPLY_SERVER,
                                        MessagesTCPOperation.CLIENT_SERVER_SELECIONA_LUGARES,
                                        List.of(false)
                                )
                        );

                    else {
                        Thread t = new ThreadPagamentoReserva(idReserva, connDB);
                        t.start();
                        sendMsg(
                                new MsgTcp(
                                        TypeMsgTCP.REPLY_SERVER,
                                        MessagesTCPOperation.CLIENT_SERVER_SELECIONA_LUGARES,
                                        List.of(true)
                                )
                        );
                    }
                }
            }
        }
    }

    public void sendMsg(MsgTcp msgSend) {
        try {
            oos.writeUnshared(msgSend);

        } catch (SocketException | EOFException ignored) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
