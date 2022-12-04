package server.model.data;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
    public final static String DATE_FORMAT = "dd/MM/yyyy HH:mm";
    public static SimpleDateFormat formatterDate = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.ENGLISH);
    public final static int INVALID_NUMBER_OF_ARGUMENTS = 1;
    public final static int PORT_MULTICAST = 4004;
    public final static String IP_MULTICAST = "239.39.39.27";
    public final static int TIMEOUT_HEARTBEAT_MILLISECONDS = 10 * 1000; //TODO: 10
    public final static int TIMEOUT_DATABASE_CONSISTENCY = 1000;
    public final static int TIMEOUT_REMOVE_OLD_SERVERS_MILLISECONDS = 5 * 1000; //TODO: 35
    public final static int TIMEOUT_STARTUP_PHASE = 5 * 1000; //TODO: 30
    public final static int TIMEOUT_WAIT_TCP_CONFIRMATION = 5 * 1000;
    public final static String DATABASE_CREATE_SCRIPT_PATH = "src/server/model/jdbc/createDbScript.sql";
    public final static String NETWORK_INTERFACE_NAME = "eth7";
    public final static int TIMEOUT_PAGAMENTO_RESERVA = 10 * 1000;
}
