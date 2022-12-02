package server.model.data;

public class Lugar {
    int id;
    String fila;
    String assento;
    double preco;
    boolean disponivel = true;

    public Lugar(int id, String fila, String assento, double preco) {
        this.id = id;
        this.fila = fila;
        this.assento = assento;
        this.preco = preco;
    }

    @Override
    public boolean equals(Object obj) {
        Lugar lugar = (Lugar) obj;
        return lugar.fila.equals(fila) && lugar.assento.equals(assento);
        // return super.equals(obj);
    }

    @Override
    public String toString() {
        return "Lugar{" +
                "id=" + id +
                ", fila='" + fila + '\'' +
                ", assento='" + assento + '\'' +
                ", preco=" + preco +
                ", disponivel=" + disponivel +
                '}' + System.lineSeparator();
    }
}
