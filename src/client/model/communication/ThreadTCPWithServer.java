package client.model.communication;

import client.model.fsm.ClientContext;
import client.model.fsm.ClientState;
import client.model.fsm.concreteStates.NoServerConnectedState;
import client.ui.text.ClientUI;
import server.model.data.*;
import server.model.data.TCP.MessagesTCPOperation;
import server.model.data.TCP.MsgTcp;
import server.model.data.TCP.ServerTCPConnection;
import server.model.data.TCP.TypeMsgTCP;
import server.model.data.viewModels.Espetaculo;
import server.model.data.viewModels.Reserva;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.stream.Collectors;

public class ThreadTCPWithServer extends Thread {

    final ClientContext fsm;
    Socket serverSocket;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    List<ServerTCPConnection> listaServidores;

    public ThreadTCPWithServer(ClientContext fsm, Socket serverSocket,
                               List<ServerTCPConnection> listaServidores) throws IOException {

        this.fsm = fsm;
        this.serverSocket = serverSocket;
        this.listaServidores = listaServidores;

        this.oos = new ObjectOutputStream(serverSocket.getOutputStream());
        this.ois = new ObjectInputStream(serverSocket.getInputStream());
    }

    @Override
    public void run() {
        do {
            MsgTcp msgRec;
            try {
                msgRec = (MsgTcp) ois.readObject();
                tratarMensagem(msgRec);
            } catch (SocketException e) {
                try {
                    synchronized (fsm) {
                        if (!new NoServerConnectedState(fsm, fsm.getData()).tryConnectToServer(false)) {
                            ClientUI.showMessage("Não existem servidores disponiveis!", false);
                            System.exit(1);
                        }
                    }
                    close();
                    break;
                } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }

            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        } while (true);
    }

    public void tratarMensagem(MsgTcp msg) throws IOException, ClassNotFoundException {

        if (msg.getOperation() == MessagesTCPOperation.CLIENT_SERVER_HELLO && msg.getMsg().get(0).equals("SERVER_OK"))
            return;

        if (msg.getMSG_TYPE() == TypeMsgTCP.SERVER_ASYNC) {
            switch (msg.getOperation()) {
                case SERVER_ASYNC_RESET_CONNECTION -> {
                    listaServidores = msg.getMsg().stream()
                            .map(object -> (ServerTCPConnection) object)
                            .collect(Collectors.toList());
                    close();
                }
                case SERVER_ASYNC_UPDATE_SERVER_LIST ->
                        listaServidores = (List<ServerTCPConnection>) msg.getMsg().get(0);
            }
        }

        switch (msg.getOperation()) {
            case CLIENT_SERVER_LOGIN -> {
                if (msg.getMsg().size() == 2 && msg.getMsg().get(0) instanceof LoginStatus ls) {
                    if (ls == LoginStatus.WRONG_CREDENTIALS) {
                        synchronized (fsm) {
                            fsm.getData().getUser().setStatus(LoginStatus.WRONG_CREDENTIALS);
                            fsm.getData().getUser().setUsername(null);
                        }
                        ClientUI.showMessage("Credenciais Incorretas", false);
                    } else {
                        if (msg.getMsg().get(1) instanceof String username) {
                            synchronized (fsm) {
                                fsm.getData().getUser().setStatus(ls);
                                fsm.getData().getUser().setUsername(username);
                                fsm.changeState(ClientState.CONSULTA_PESQUISA_ESPETACULOS.createState(fsm, fsm.getData()));
                            }
                            ClientUI.showMessage("Bem vindo " + username, true);
                        }
                    }
                } else
                    ClientUI.showMessage("Erro na comunicação com o servidor!", false);
            }
            case CLIENT_SERVER_REGISTER -> {
                if (msg.getMsg().get(0) instanceof Boolean b && b)
                    ClientUI.showMessage("Utilizador inserido com sucesso", false);
                else
                    ClientUI.showMessage("Erro a inserir o utilizador", false);
            }
            case CLIENT_SERVER_EDITAR_UTILIZADOR -> {
                if (msg.getMsg().get(0) instanceof Boolean b && b) {
                    ClientUI.showMessage("Utilizador atualizado com sucesso", false);
                    fsm.getData().getUser().setUsername((String) msg.getMsg().get(1));
                } else
                    ClientUI.showMessage("Erro a atualizar os dados do utilizador", false);
            }
            case CLIENT_SERVER_PESQUISA_ESPETACULO -> {
                List<Espetaculo> espetaculos = (List<Espetaculo>) msg.getMsg().get(0);
                for (var espetaculo : espetaculos)
                    ClientUI.showMessage(espetaculo + System.lineSeparator(), true);
            }
            case CLIENT_SERVER_ELIMINAR_ESPETACULO -> {
                if (msg.getMsg().get(0) instanceof Boolean b && b)
                    ClientUI.showMessage("Espetaculo eliminado com sucesso", false);
                else
                    ClientUI.showMessage("Erro a eliminar o espetaculo", false);
            }
            case CLIENT_SERVER_TORNAR_ESPETACULO_VISIVEL -> {
                if (msg.getMsg().get(0) instanceof Boolean b && b)
                    ClientUI.showMessage("Espetaculo está visivel", false);
                else
                    ClientUI.showMessage("Espetaculo não existe ou já está visivel", false);
            }
            case CLIENT_SERVER_MOSTRAR_RESERVAS ->{
                List<Reserva> reservas = (List<Reserva>) msg.getMsg().get(0);
                for (var reserva : reservas)
                    ClientUI.showMessage(reserva + System.lineSeparator(), true);
            }
            case CLIENT_SERVER_PAGAR_RESERVA -> {
                if (msg.getMsg().get(0) instanceof Boolean b && b)
                    ClientUI.showMessage("Reserva foi paga", false);
                else
                    ClientUI.showMessage("Reserva não existe ou já está paga", false);
            }
            case CLIENT_SERVER_ELIMINAR_RESERVA -> {
                if (msg.getMsg().get(0) instanceof Boolean b && b)
                    ClientUI.showMessage("Reserva foi removida", false);
                else
                    ClientUI.showMessage("Reserva não existe ou já foi paga", false);
            }
        }
    }

    public void sendMsg(MsgTcp msgSend) throws IOException, ClassNotFoundException {
        oos.writeUnshared(msgSend);
    }

    public void close() throws IOException {
        serverSocket.close();
    }
}
