package server.model.data.viewModels;

import server.model.data.Constants;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Reserva implements Serializable {
    private final int id;
    private final String username;
    private final Espetaculo espetaculo;
    private final List<Lugar> lugares;
    private final boolean estadoPagamento;
    private final Date dataPagamento;

    public Reserva(int id, String username, Espetaculo espetaculo, List<Lugar> lugares, boolean estadoPagamento, Date dataPagamento) {
        this.id = id;
        this.username = username;
        this.espetaculo = espetaculo;
        this.lugares = lugares;
        this.estadoPagamento = estadoPagamento;
        this.dataPagamento = dataPagamento;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Espetaculo getEspetaculo() {
        return espetaculo;
    }

    public List<Lugar> getLugares() {
        return lugares;
    }

    public boolean isEstadoPagamento() {
        return estadoPagamento;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append(System.lineSeparator());
        sb.append("Utilizador: ").append(username).append(System.lineSeparator());
        sb.append("Espetaculo ID: ").append(espetaculo.getId()).append(System.lineSeparator());
        sb.append("Espetaculo Designação: ").append(espetaculo.getDesignacao()).append(System.lineSeparator());
        sb.append("Lugares:").append(System.lineSeparator());
        for(var lugar : lugares)
            sb.append(" ->").append(lugar).append(System.lineSeparator());
        sb.append("Estado Pagamento: ").append(estadoPagamento ? "Pago" : "Não Pago").append(System.lineSeparator());
        if(estadoPagamento)
            sb.append("Data Pagamento: ").append(Constants.formatterDate.format(dataPagamento))
                    .append(System.lineSeparator());
        return sb.toString();
    }
}
