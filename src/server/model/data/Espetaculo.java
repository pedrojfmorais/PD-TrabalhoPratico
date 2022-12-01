package server.model.data;

import java.util.List;
import java.util.Date;

public class Espetaculo {
    int id;
    boolean visibilidade = false;
    String designacao;
    String tipo;
    Date data;
    /*String data;
    String hora;*/
    int duracao;
    String local;
    String localidade;
    String pais;
    String classificacao;
    List<Lugar> lugares;

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
}
