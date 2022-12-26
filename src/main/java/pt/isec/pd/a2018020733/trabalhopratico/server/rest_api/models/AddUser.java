package pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.models;

import java.io.Serializable;

public class AddUser implements Serializable {

    private final String username;
    private final String nome;
    private final String password;

    public AddUser(String username, String nome, String password) {
        this.username = username;
        this.nome = nome;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getNome() {
        return nome;
    }

    public String getPassword() {
        return password;
    }
}
