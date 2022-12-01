package server.model.jdbc.db;

public enum TableDatabaseVersion {
    VERSION("version");

    public final String label;
    TableDatabaseVersion(String label) {
        this.label = label;
    }
}
