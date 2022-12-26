package pt.isec.pd.a2018020733.trabalhopratico.server.model.data.syncDB;

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
