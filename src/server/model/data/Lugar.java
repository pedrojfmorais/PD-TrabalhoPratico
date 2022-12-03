package server.model.data;

import java.io.Serializable;

public class Lugar implements Serializable {
    private int id;
    private String fila;
    private String assento;
    private double preco;
    private boolean disponivel = true;

    public Lugar(int id, String fila, String assento, double preco) {
        this.id = id;
        this.fila = fila;
        this.assento = assento;
        this.preco = preco;
    }

    public int getId() {
        return id;
    }

    public String getFila() {
        return fila;
    }

    public String getAssento() {
        return assento;
    }

    public double getPreco() {
        return preco;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Lugar lugar)
            return lugar.fila.equals(fila) && lugar.assento.equals(assento);
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fila: ").append(fila);
        sb.append(" Assento: ").append(assento);
        sb.append(" Preço: ").append(preco);
        sb.append(" Disponivel: ").append(disponivel ? "Sim" : "Não");
        return sb.toString();
    }
}
