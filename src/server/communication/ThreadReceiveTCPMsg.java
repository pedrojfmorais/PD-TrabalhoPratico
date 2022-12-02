package server.communication;

import server.model.data.LoginStatus;
import server.model.data.MessagesTCPOperation;
import server.model.data.MsgTcp;
import server.model.data.TypeMsgTCP;
import server.model.data.*;
import server.model.jdbc.ConnDB;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ThreadReceiveTCPMsg extends Thread {

    private final ConnDB connDB;
    private final Socket cliSocket;
    private final Heartbeat serverData;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    private boolean threadContinue = true;

    public ThreadReceiveTCPMsg(Socket cliSocket, ConnDB connDB, Heartbeat serverData) throws IOException {
        this.cliSocket = cliSocket;
        this.connDB = connDB;
        this.serverData = serverData;

        oos = new ObjectOutputStream(cliSocket.getOutputStream());
        ois = new ObjectInputStream(cliSocket.getInputStream());
    }

    @Override
    public void run() {

        do {
            MsgTcp msgRec;
            try {
                msgRec = (MsgTcp) ois.readObject();
            } catch (SocketException e) {
                try {
                    cliSocket.close();
                } catch (IOException ignored) {
                }
                close();
                break;
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            try {

                tratarMensagem(msgRec);
                serverData.setLOCAL_DB_VERSION(connDB.getVersionDB());

            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        } while (threadContinue);

        try {
            cliSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void tratarMensagemCliente(MsgTcp msg) throws SQLException, IOException {
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
            case CLIENT_SERVER_EDITAR_ESPETACULO -> {
                boolean result = false;
                int id = (int) msg.getMsg().get(0);
                if (connDB.verifyEspetaculoExists(id))
                    result = connDB.editarEspetaculo(id);

                sendMsg(
                        new MsgTcp(
                                TypeMsgTCP.REPLY_SERVER,
                                MessagesTCPOperation.CLIENT_SERVER_EDITAR_ESPETACULO,
                                List.of(result)
                        )
                );
            }
            case CLIENT_SERVER_SELECIONAR_ESPETACULO -> {
                // TODO ???
            }
            case CLIENT_SERVER_PESQUISA_ESPETACULO -> {
                if (msg.getMsg().get(0) instanceof String filtro) {

                    List<Espetaculo> espetaculos = new ArrayList<>(connDB.pesquisarEspetaculo(filtro));

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

                List<Reserva> reservas = new ArrayList<>(connDB.getReservas(reservaPaga));

                sendMsg(
                        new MsgTcp(
                                TypeMsgTCP.REPLY_SERVER,
                                MessagesTCPOperation.CLIENT_SERVER_MOSTRAR_RESERVAS,
                                List.of(reservas)
                        )
                );
            }
        }
    }

    public void sendMsg(MsgTcp msgSend) throws IOException {
        oos.reset();
        oos.writeUnshared(msgSend);
    }
}
