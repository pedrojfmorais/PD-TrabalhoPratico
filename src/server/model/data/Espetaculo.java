package server.model.data;

import java.util.List;

public class Espetaculo {

    String designacao;
    String tipo;
    String data;
    String hora;
    int duracao;
    String local;
    String localidade;
    String pais;
    String classificacao;
    List<Lugar> lugares;

    public Espetaculo(String designacao, String tipo, String data, String hora, int duracao, String local, String localidade, String pais, String classificacao, List<Lugar> lugares) {
        this.designacao = designacao;
        this.tipo = tipo;
        this.data = data;
        this.hora = hora;
        this.duracao = duracao;
        this.local = local;
        this.localidade = localidade;
        this.pais = pais;
        this.classificacao = classificacao;
        this.lugares = lugares;
    }
}
