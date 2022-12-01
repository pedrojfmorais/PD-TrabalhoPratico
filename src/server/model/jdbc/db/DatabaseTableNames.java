package server.model.jdbc.db;

public enum DatabaseTableNames {

    DATABASE_VERSION("database_version"),
    UTILIZADOR("utilizador"),
    ESPETACULO("espetaculo"),
    LUGAR("lugar"),
    RESERVA("reserva"),
    RESERVA_LUGAR("reserva_lugar");

    public final String label;
    DatabaseTableNames(String label) {
        this.label = label;
    }
}
