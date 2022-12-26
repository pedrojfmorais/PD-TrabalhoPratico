package pt.isec.pd.a2018020733.trabalhopratico.client;

import pt.isec.pd.a2018020733.trabalhopratico.client.model.fsm.ClientContext;
import pt.isec.pd.a2018020733.trabalhopratico.client.ui.text.ClientUI;

import java.io.*;

import static pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Constants.*;

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
