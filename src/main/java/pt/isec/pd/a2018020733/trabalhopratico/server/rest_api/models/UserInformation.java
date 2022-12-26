package pt.isec.pd.a2018020733.trabalhopratico.server.rest_api.models;

import java.io.Serializable;

public class UserInformation implements Serializable {

    private final String username;
    private final String nome;
    private final int administrador;
    private final int autenticado;

    public UserInformation(String username, String nome, int administrador, int autenticado) {
        this.username = username;
        this.nome = nome;
        this.administrador = administrador;
        this.autenticado = autenticado;
    }

    public String getUsername() {
        return username;
    }

    public String getNome() {
        return nome;
    }

    public int getAdministrador() {
        return administrador;
    }

    public int getAutenticado() {
        return autenticado;
    }
}
