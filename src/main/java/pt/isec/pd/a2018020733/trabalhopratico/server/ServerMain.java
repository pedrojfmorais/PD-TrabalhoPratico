package pt.isec.pd.a2018020733.trabalhopratico.server;

import pt.isec.pd.a2018020733.trabalhopratico.server.model.Server;

import static pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Constants.INVALID_NUMBER_OF_ARGUMENTS;


public class ServerMain {
    public static void main(String[] args) throws InterruptedException {

        if(args.length != 2)
            System.exit(INVALID_NUMBER_OF_ARGUMENTS);

        final int UDP_PORT = Integer.parseInt(args[0]);
        final String DB_PATH = args[1];

        Server s = new Server(UDP_PORT, DB_PATH);
        s.start();

    }
}