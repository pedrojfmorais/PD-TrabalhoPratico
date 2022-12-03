package server.model.data.syncDB;

import server.model.data.TCP.MsgTcp;

import java.io.Serializable;

public class Prepare implements Serializable {

    private static int id;
    private final int idPrepare;
    private final MsgTcp msgTcp;
    private final int versionDB;
    private final int porto;

    static {
        id = 0;
    }

    public Prepare(MsgTcp msgTcp, int versionDB, int porto) {
        this.msgTcp = msgTcp;
        this.versionDB = versionDB;
        this.porto = porto;

        idPrepare = ++id;
    }

    public int getIdPrepare() {
        return idPrepare;
    }

    public MsgTcp getMsgTcp() {
        return msgTcp;
    }

    public int getVersionDB() {
        return versionDB;
    }

    public int getPorto() {
        return porto;
    }
}


