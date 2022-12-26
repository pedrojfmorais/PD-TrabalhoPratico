package pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.controllers;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.LoginStatus;
import pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.models.AddUser;
import pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.models.UserInformation;

import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

import static pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.Application.connDB;

@RestController
@RequestMapping("admin")
public class AdminController {

    @GetMapping("utilizadores")
    public List<UserInformation> utilizadores()
    {
        List<UserInformation> espetaculos;
        try {
            //TODO: ver roles
            espetaculos = connDB.getAllUsers();
        } catch (SQLException | JSONException e) {
            throw new RuntimeException(e);
        }
        return espetaculos;
    }

    @PostMapping(value = "utilizador", consumes = {"application/json"})
    public boolean utilizador(@RequestBody AddUser user)
    {
        try {
            //TODO: ver roles
            return connDB.insertUser(user.getUsername(), user.getNome(), user.getPassword());
        } catch (SQLException e) {
            return false;
        }
    }

    @DeleteMapping( "utilizador")
    public ResponseEntity utilizador(Principal principal, @RequestBody String username)
    {
        try {
            if(connDB.getUserType(principal.getName()) != LoginStatus.SUCCESSFUL_ADMIN_USER)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(HttpStatus.UNAUTHORIZED);
            //TODO: ver roles
            return ResponseEntity.ok(connDB.deleteUser(username));
        } catch (SQLException e) {
            return ResponseEntity.ok(false);
        }
    }
}
