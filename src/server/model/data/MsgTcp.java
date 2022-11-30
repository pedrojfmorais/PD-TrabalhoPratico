package server.model.data;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MsgTcp implements Serializable {
    private final TypeMsgTCP MSG_TYPE;
    private final String operation;
    private final ArrayList<Object> msg;

    public MsgTcp(TypeMsgTCP MSG_TYPE,  String operation, ArrayList<Object> msg) {
        this.MSG_TYPE = MSG_TYPE;
        this.operation = operation;
        this.msg = msg;
    }

    public TypeMsgTCP getMSG_TYPE() {
        return MSG_TYPE;
    }

    public String getOperation() {
        return operation;
    }

    public ArrayList<Object> getMsg() {
        return msg;
    }
}
