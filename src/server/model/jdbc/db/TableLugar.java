package server.model.jdbc.db;

public enum TableLugar {

    ID("id"),
    FILA("fila"),
    ASSENTO("assento"),
    PRECO("preco"),
    ESPETACULO_ID("espetaculo_id");

    public final String label;
    TableLugar(String label) {
        this.label = label;
    }
}
