package client;

import client.model.fsm.ClientContext;
import client.ui.text.ClientUI;

import java.io.*;

import static server.model.data.Constants.*;

public class ClientMain {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        if(args.length != 2)
            System.exit(INVALID_NUMBER_OF_ARGUMENTS);

        final String IP_SERVER = args[0];
        final int PORT_UDP = Integer.parseInt(args[1]);

        ClientContext fsm = new ClientContext(IP_SERVER, PORT_UDP);
        ClientUI ui = new ClientUI(fsm);
        ui.start();

    }
}
