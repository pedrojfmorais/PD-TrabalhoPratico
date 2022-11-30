package server.model.data;

public class Lugar {
    int id;
    String fila;
    String assento;
    double preco;

    public Lugar(int id, String fila, String assento, double preco) {
        this.id = id;
        this.fila = fila;
        this.assento = assento;
        this.preco = preco;
    }
}
