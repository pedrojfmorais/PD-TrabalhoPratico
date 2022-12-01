package server.model.data;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MsgTcp implements Serializable {
    private final TypeMsgTCP MSG_TYPE;
    private final MessagesTCPOperation operation;
    private final List<Object> msg;

    public MsgTcp(TypeMsgTCP MSG_TYPE, MessagesTCPOperation operation, List<Object> msg) {
        this.MSG_TYPE = MSG_TYPE;
        this.operation = operation;
        this.msg = msg;
    }

    public TypeMsgTCP getMSG_TYPE() {
        return MSG_TYPE;
    }

    public MessagesTCPOperation getOperation() {
        return operation;
    }

    public List<Object> getMsg() {
        return msg;
    }
}
