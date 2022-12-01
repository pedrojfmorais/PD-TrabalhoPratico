package server.model.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Reserva {
    int id;
    String username;
    Espetaculo espetaculo;
    List<Lugar> lugares; // FilaLugar
    boolean estadoPagamento;
    Date tempoPagamento;

    public Reserva(int id, String username, Espetaculo espetaculo, List<Lugar> lugares, boolean estadoPagamento, Date tempoPagamento) {
        this.id = id;
        this.username = username;
        this.espetaculo = espetaculo;
        this.lugares = lugares;
        this.estadoPagamento = estadoPagamento;
        this.tempoPagamento = tempoPagamento;
    }
}
