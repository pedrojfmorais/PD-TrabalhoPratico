package server.model.data.syncDB;

import java.io.Serializable;

public class Commit implements Serializable {
    private final int idPrepare;

    public Commit(int idPrepare) {
        this.idPrepare = idPrepare;
    }

    public int getIdPrepare() {
        return idPrepare;
    }
}
