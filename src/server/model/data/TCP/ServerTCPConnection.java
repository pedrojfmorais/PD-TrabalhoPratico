package server.model.data.TCP;

import java.io.Serializable;

public class ServerTCPConnection implements Serializable {

    private final String IP;
    private final int PORT;

    public ServerTCPConnection(String IP, int PORT) {
        this.IP = IP;
        this.PORT = PORT;
    }

    public String getIP() {
        return IP;
    }

    public int getPORT() {
        return PORT;
    }
}
