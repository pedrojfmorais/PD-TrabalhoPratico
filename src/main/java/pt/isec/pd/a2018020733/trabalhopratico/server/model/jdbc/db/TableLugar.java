package pt.isec.pd.a2018020733.trabalhopratico.server.model.jdbc.db;

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
