package server.model.jdbc.db;

public enum TableReserva {

    ID("id"),
    DATA_HORA("data_hora"),
    PAGO("pago"),
    ID_UTILIZADOR("id_utilizador"),
    ID_ESPETACULO("id_espetaculo");

    public final String label;
    TableReserva(String label) {
        this.label = label;
    }
}
