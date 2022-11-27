package server.model.data;

import java.io.Serializable;

public class MsgTcp implements Serializable {
    private final TypeMsgTCP MSG_TYPE;
    private final String msg;

    public MsgTcp(TypeMsgTCP MSG_TYPE, String msg) {
        this.MSG_TYPE = MSG_TYPE;
        this.msg = msg;
    }

    public TypeMsgTCP getMSG_TYPE() {
        return MSG_TYPE;
    }

    public String getMsg() {
        return msg;
    }
}
