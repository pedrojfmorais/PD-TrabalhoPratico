package pt.isec.pd.a2018020733.trabalhopratico.server.model.jdbc.db;

public enum TableUtilizador {

    ID("id"),
    USERNAME("username"),
    NOME("nome"),
    PASSWORD("password"),
    ADMINISTRADOR("administrador"),
    AUTENTICADO("autenticado");

    public final String label;
    TableUtilizador(String label) {
        this.label = label;
    }
}
