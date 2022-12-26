package pt.isec.pd.a2018020733.trabalhopratico.server.model.data.syncDB;

import java.io.Serializable;

public class Abort implements Serializable {
    private final int idPrepare;

    public Abort(int idPrepare) {
        this.idPrepare = idPrepare;
    }

    public int getIdPrepare() {
        return idPrepare;
    }
}
