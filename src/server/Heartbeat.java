package server;

import java.io.Serializable;
import java.util.Date;

public class Heartbeat implements Comparable<Heartbeat>, Serializable {
    private String ipServer;
    private int TCP_PORT;
    private final boolean DISPONIVEL;
    private final int LOCAL_DB_VERSION;
    private final int NUMERO_LIGACOES_TCP;
    private Date receivedAt;

    public Heartbeat(int TCP_PORT, boolean DISPONIVEL, int LOCAL_DB_VERSION, int NUMERO_LIGACOES_TCP) {
        this.TCP_PORT = TCP_PORT;
        this.DISPONIVEL = DISPONIVEL;
        this.LOCAL_DB_VERSION = LOCAL_DB_VERSION;
        this.NUMERO_LIGACOES_TCP = NUMERO_LIGACOES_TCP;
    }

    public int getTCP_PORT() {
        return TCP_PORT;
    }

    public void setTCP_PORT(int TCP_PORT) {
        this.TCP_PORT = TCP_PORT;
    }

    public boolean isDISPONIVEL() {
        return DISPONIVEL;
    }

    public int getLOCAL_DB_VERSION() {
        return LOCAL_DB_VERSION;
    }

    public int getNUMERO_LIGACOES_TCP() {
        return NUMERO_LIGACOES_TCP;
    }

    public String getIpServer() {
        return ipServer;
    }

    public void setIpServer(String ipServer) {
        this.ipServer = ipServer;
    }

    public Date getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Date receivedAt) {
        this.receivedAt = receivedAt;
    }

    @Override
    public int compareTo(Heartbeat o) {
        return Integer.compare(NUMERO_LIGACOES_TCP, o.NUMERO_LIGACOES_TCP);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Heartbeat h))
            return false;

        return ipServer.equals(h.ipServer) && TCP_PORT == h.TCP_PORT;
    }
}
