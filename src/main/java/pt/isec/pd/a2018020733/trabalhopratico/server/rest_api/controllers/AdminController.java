package pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.controllers;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.models.AddUser;
import pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.models.UserInformation;

import java.sql.SQLException;
import java.util.List;

import static pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.Application.connDB;

@RestController
@RequestMapping("admin")
public class AdminController {

    @GetMapping("utilizadores")
    public ResponseEntity<?> utilizadores(JwtAuthenticationToken principal) {
        if (principal == null
                || !principal.getTokenAttributes().get("scope").equals("ADMIN"))
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);

        List<UserInformation> espetaculos;
        try {
            espetaculos = connDB.getAllUsers();
        } catch (SQLException | JSONException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(espetaculos, HttpStatus.OK);
    }

    @PostMapping(value = "utilizador", consumes = {"application/json"})
    public ResponseEntity<?> utilizador(
            JwtAuthenticationToken principal,
            @RequestBody AddUser user
    ) {
        if (principal == null
                || !principal.getTokenAttributes().get("scope").equals("ADMIN"))
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);

        String res;

        try {
            if(connDB.insertUser(user.getUsername(), user.getNome(), user.getPassword()))
                res = "Utilizador inserido com sucesso";
            else
                res = "Não é possivel inserir o utilizador";
        } catch (SQLException e) {
            res = "Erro a inserir o utilizador";
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("utilizador")
    public ResponseEntity<?> utilizador(JwtAuthenticationToken principal, @RequestBody String username) {
        if (principal == null
                || !principal.getTokenAttributes().get("scope").equals("ADMIN"))
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);

        String res;

        try {
            if(connDB.deleteUser(username))
                res = "Utilizador removido com sucesso";
            else
                res = "Não é possivel remover o utilizador";
        } catch (SQLException e) {
            res = "Erro a remover o utilizador";
        }

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
