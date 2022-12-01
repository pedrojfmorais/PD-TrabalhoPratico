package server.model.jdbc.db;

public enum TableEspetaculo {

    ID("id"),
    DESCRICAO("descricao"),
    TIPO("tipo"),
    DATA_HORA("data_hora"),
    DURACAO("duracao"),
    LOCAL("local"),
    LOCALIDADE("localidade"),
    PAIS("pais"),
    CLASSIFICACAO_ETARIA("classificacao_etaria"),
    VISIVEL("visivel");

    public final String label;
    TableEspetaculo(String label) {
        this.label = label;
    }
}
