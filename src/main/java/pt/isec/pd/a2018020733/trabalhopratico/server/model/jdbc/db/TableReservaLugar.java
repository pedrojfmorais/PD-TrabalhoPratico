package pt.isec.pd.a2018020733.trabalhopratico.server.model.jdbc.db;

public enum TableReservaLugar {

    ID_RESERVA("id_reserva"),
    ID_LUGAR("id_lugar");

    public final String label;
    TableReservaLugar(String label) {
        this.label = label;
    }
}
