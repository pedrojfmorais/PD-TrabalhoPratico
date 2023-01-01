package pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.models;

import java.io.Serializable;

public record AddUser(String username, String nome, String password) implements Serializable {

}
