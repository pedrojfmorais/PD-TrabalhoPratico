package server.communication;

import server.model.data.MsgTcp;

import java.io.Serializable;

public class Prepare implements Serializable {
    MsgTcp msgTcp;
    int versionDB;
    int porto;

    public Prepare(MsgTcp msgTcp, int versionDB, int porto) {
        this.msgTcp = msgTcp;
        this.versionDB = versionDB;
        this.porto = porto;
    }
}


