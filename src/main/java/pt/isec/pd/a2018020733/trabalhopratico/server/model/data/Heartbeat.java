package pt.isec.pd.a2018020733.trabalhopratico.server.model.data;

import java.io.Serializable;
import java.util.Date;

public class Heartbeat implements Comparable<Heartbeat>, Serializable {
    private String ipServer;
    private int TCP_PORT;
    private int UDP_PORT;
    private boolean DISPONIVEL;
    private int LOCAL_DB_VERSION;
    private int NUMERO_LIGACOES_TCP;
    private Date receivedAt;

    public Heartbeat(int TCP_PORT, int UDP_PORT, boolean DISPONIVEL, int LOCAL_DB_VERSION, int NUMERO_LIGACOES_TCP) {
        this.TCP_PORT = TCP_PORT;
        this.UDP_PORT = UDP_PORT;
        this.DISPONIVEL = DISPONIVEL;
        this.LOCAL_DB_VERSION = LOCAL_DB_VERSION;
        this.NUMERO_LIGACOES_TCP = NUMERO_LIGACOES_TCP;
    }

    public int getUDP_PORT() {
        return UDP_PORT;
    }

    public void setUDP_PORT(int UDP_PORT) {
        this.UDP_PORT = UDP_PORT;
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

    public void setDISPONIVEL(boolean DISPONIVEL) {
        this.DISPONIVEL = DISPONIVEL;
    }

    public int getLOCAL_DB_VERSION() {
        return LOCAL_DB_VERSION;
    }

    public void setLOCAL_DB_VERSION(int LOCAL_DB_VERSION) {
        this.LOCAL_DB_VERSION = LOCAL_DB_VERSION;
    }

    public int getNUMERO_LIGACOES_TCP() {
        return NUMERO_LIGACOES_TCP;
    }

    public void setNUMERO_LIGACOES_TCP(int NUMERO_LIGACOES_TCP) {
        this.NUMERO_LIGACOES_TCP = NUMERO_LIGACOES_TCP;
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
