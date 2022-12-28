package pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.text.ParseException;

import static pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.Application.connDB;

@RestController
public class ReservasController {

    @GetMapping("/reservas/pagas")
    public ResponseEntity<?> pagas(JwtAuthenticationToken principal)
    {
        if(principal == null
            || !principal.getTokenAttributes().get("scope").equals("USER"))
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);

        try {
            return new ResponseEntity<>(connDB.getReservas(true, principal.getName()), HttpStatus.OK);
        } catch (SQLException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/reservas/aguardar-pagamento")
    public  ResponseEntity<?> aguardarPagamento(JwtAuthenticationToken principal)
    {
        if(principal == null
                || !principal.getTokenAttributes().get("scope").equals("USER"))
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        try {
            return new ResponseEntity<>(connDB.getReservas(false, principal.getName()), HttpStatus.OK);
        } catch (SQLException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
