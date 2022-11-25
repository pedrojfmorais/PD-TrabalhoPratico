package server.model;

public class Constants {
    public final static int INVALID_NUMBER_OF_ARGUMENTS = 1;
    public final static int PORT_MULTICAST = 4004;
    public final static String IP_MULTICAST = "239.39.39.39";
    public final static int TIMEOUT_HEARTBEAT_MILLISECONDS = 1 * 1000; //TODO: 10
    public final static int TIMEOUT_REMOVE_OLD_SERVERS_MILLISECONDS = 5 * 1000; //TODO: 35
    public final static int TIMEOUT_WAIT_TCP_CONFIRMATION = 5 * 1000;

    public final static String MSG_TCP_CLIENT_TRY_CONNECTION = "HELLO";
    public final static String MSG_TCP_CLIENT_CONFIRM_CONNECTION = "ACK";

}
