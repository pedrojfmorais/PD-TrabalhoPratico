package pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.controllers;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.viewModels.Espetaculo;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import static pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.Application.connDB;

@RestController
public class EspetaculosController {

    @GetMapping("/espetaculos")
    public List<Espetaculo> espetaculos(
            JwtAuthenticationToken principal,
            @RequestParam(value="dataInicio", required=false) String dataInicio,
            @RequestParam(value="dataFim", required=false) String dataFim)
    {

        List<Espetaculo> espetaculos;
        try {
            boolean isAdmin =
                    principal != null
                    && principal.getTokenAttributes().get("scope").equals("ADMIN");

            espetaculos = connDB.pesquisarEspetaculo(dataInicio, dataFim, isAdmin);
        } catch (ParseException | SQLException e) {
            throw new RuntimeException(e);
        }
        return espetaculos;
    }
}
