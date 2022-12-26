package pt.isec.pd.a2018020733.trabalhopratico.server.otherThreads;

import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Constants;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.jdbc.ConnDB;

import java.sql.SQLException;

public class ThreadPagamentoReserva extends Thread{

    int idReserva;
    ConnDB connDB;

    public ThreadPagamentoReserva(int idReserva, ConnDB connDB) {
        this.idReserva = idReserva;
        this.connDB = connDB;
    }

    @Override
    public void run() {

        try {
            Thread.sleep(Constants.TIMEOUT_PAGAMENTO_RESERVA);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            connDB.eliminarReserva(idReserva);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
