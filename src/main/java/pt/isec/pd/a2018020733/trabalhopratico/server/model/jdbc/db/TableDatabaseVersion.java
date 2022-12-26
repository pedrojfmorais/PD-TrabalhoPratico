package pt.isec.pd.a2018020733.trabalhopratico.server.model.jdbc.db;

public enum TableDatabaseVersion {
    VERSION("version");

    public final String label;
    TableDatabaseVersion(String label) {
        this.label = label;
    }
}
