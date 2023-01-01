package pt.isec.pd.a2018020733.trabalhopratico.server.model.data;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
    public final static String DATE_FORMAT = "dd/MM/yyyy HH:mm";
    public static SimpleDateFormat formatterDate = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.ENGLISH);
    public final static int INVALID_NUMBER_OF_ARGUMENTS = 1;
    public final static int PORT_MULTICAST = 4004;
    public final static String IP_MULTICAST = "239.39.39.39";
    public final static int TIMEOUT_HEARTBEAT_MILLISECONDS = 10 * 1000;
    public final static int TIMEOUT_DATABASE_CONSISTENCY = 1000;
    public final static int TIMEOUT_REMOVE_OLD_SERVERS_MILLISECONDS = 35 * 1000;
    public final static int TIMEOUT_STARTUP_PHASE = 30 * 1000;
    public final static int TIMEOUT_WAIT_TCP_CONFIRMATION = 5 * 1000;
    public final static String DATABASE_CREATE_SCRIPT_PATH = "src/main/java/pt/isec/pd/a2018020733/trabalhopratico/server/model/jdbc/createDbScript.sql";
    public final static String NETWORK_INTERFACE_NAME = "wlan2";
    public final static int TIMEOUT_PAGAMENTO_RESERVA = 10 * 1000;
}
