package server.model.data;

import java.io.Serializable;
import java.util.List;
import java.util.Date;

public class Espetaculo implements Serializable {
    private final int id;
    private boolean visibilidade = false;
    private final String designacao;
    private final String tipo;
    private final Date data;
    /*String data;
    String hora;*/
    private final int duracao;
    private final String local;
    private final String localidade;
    private final String pais;
    private final String classificacao;
    private final List<Lugar> lugares;

    public Espetaculo(int id, boolean visibilidade, String designacao, String tipo, Date data/*, String hora*/, int duracao, String local, String localidade, String pais, String classificacao, List<Lugar> lugares) {
        this.id = id;
        this.visibilidade = visibilidade;
        this.designacao = designacao;
        this.tipo = tipo;
        this.data = data;
        // this.hora = hora;
        this.duracao = duracao;
        this.local = local;
        this.localidade = localidade;
        this.pais = pais;
        this.classificacao = classificacao;
        this.lugares = lugares;
    }

    public int getId() {
        return id;
    }

    public boolean isVisibilidade() {
        return visibilidade;
    }

    public String getDesignacao() {
        return designacao;
    }

    public String getTipo() {
        return tipo;
    }

    public Date getData() {
        return data;
    }

    public int getDuracao() {
        return duracao;
    }

    public String getLocal() {
        return local;
    }

    public String getLocalidade() {
        return localidade;
    }

    public String getPais() {
        return pais;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public List<Lugar> getLugares() {
        return lugares;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append(System.lineSeparator());
        sb.append("Designação: ").append(designacao).append(System.lineSeparator());
        sb.append("Data: ").append(data.toString()).append(System.lineSeparator());
        sb.append("Duracao: ").append(duracao).append(System.lineSeparator());
        sb.append("Local: ").append(local).append(System.lineSeparator());
        sb.append("Localidade: ").append(localidade).append(System.lineSeparator());
        sb.append("Pais: ").append(pais).append(System.lineSeparator());
        sb.append("Classificação: ").append(classificacao).append(System.lineSeparator());
        sb.append("Lugares:").append(System.lineSeparator());
        for(var lugar : lugares)
            sb.append(" ->").append(lugar).append(System.lineSeparator());
        return sb.toString();
    }
}
