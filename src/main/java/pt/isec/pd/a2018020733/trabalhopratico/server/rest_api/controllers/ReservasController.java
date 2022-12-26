package pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.viewModels.Reserva;

import java.security.Principal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import static pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.Application.connDB;

@RestController
public class ReservasController {

    @GetMapping("/reservas/pagas")
    public List<Reserva> pagas(Principal principal)
    {
        try {
            return connDB.getReservas(true, principal.getName());
        } catch (SQLException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/reservas/aguardar-pagamento")
    public List<Reserva> aguardarPagamento(Principal principal)
    {
        try {
            return connDB.getReservas(false, principal.getName());
        } catch (SQLException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
