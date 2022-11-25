package server.model;

import java.io.Serializable;

public class MsgTcp implements Serializable {
    private final String MSG_TYPE;
    private final Object obj;

    public MsgTcp(String MSG_TYPE, Object obj) {
        this.MSG_TYPE = MSG_TYPE;
        this.obj = obj;
    }

    public String getMSG_TYPE() {
        return MSG_TYPE;
    }

    public Object getObj() {
        return obj;
    }
}
