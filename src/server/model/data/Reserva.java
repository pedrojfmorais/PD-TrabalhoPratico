package server.model.data;

import java.util.ArrayList;
import java.util.Date;

public class Reserva {
    int id;
    String username;
    Espetaculo espetaculo;
    ArrayList<Lugar> lugares; // FilaLugar
    boolean estadoPagamento;
    String tempoPagamento;

    public Reserva(int id, String username, Espetaculo espetaculo, ArrayList<Lugar> lugares, boolean estadoPagamento, String tempoPagamento) {
        this.id = id;
        this.username = username;
        this.espetaculo = espetaculo;
        this.lugares = lugares;
        this.estadoPagamento = estadoPagamento;
        this.tempoPagamento = tempoPagamento;
    }
}
