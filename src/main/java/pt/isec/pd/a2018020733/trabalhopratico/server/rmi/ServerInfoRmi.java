package pt.isec.pd.a2018020733.trabalhopratico.server.rmi;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Objects;

public class ServerInfoRmi implements Serializable {

    private final String ipServer;
    private final int TCP_PORT;
    private final int UDP_PORT;
    private final int numeroLigacoesTcp;
    private final Date receivedAt;

    public ServerInfoRmi(String ipServer, int TCP_PORT, int UDP_PORT, int numeroLigacoesTcp, Date receivedAt) {
        this.ipServer = ipServer;
        this.TCP_PORT = TCP_PORT;
        this.UDP_PORT = UDP_PORT;
        this.numeroLigacoesTcp = numeroLigacoesTcp;
        this.receivedAt = receivedAt;
    }

    public String getIpServer() {
        return ipServer;
    }

    public int getTCP_PORT() {
        return TCP_PORT;
    }

    public int getUDP_PORT() {
        return UDP_PORT;
    }

    public int getNumeroLigacoesTcp() {
        return numeroLigacoesTcp;
    }

    public Date getReceivedAt() {
        return receivedAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Servidor: ").append(ipServer).append(":").append(UDP_PORT).append(System.lineSeparator());
        sb.append("Porto TCP: ").append(TCP_PORT).append(System.lineSeparator());
        sb.append("Carga: ").append(numeroLigacoesTcp).append(System.lineSeparator());
        sb.append("Último heartbeat: ").append(receivedAt).append(System.lineSeparator());

        return sb.toString();
    }
}
