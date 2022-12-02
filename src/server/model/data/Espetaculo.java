package server.model.data;

import java.util.List;
import java.util.Date;

public class Espetaculo {
    private int id;
    private boolean visibilidade = false;
    private String designacao;
    private String tipo;
    private Date data;
    /*String data;
    String hora;*/
    private int duracao;
    private String local;
    private String localidade;
    private String pais;
    private String classificacao;
    private List<Lugar> lugares;

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
