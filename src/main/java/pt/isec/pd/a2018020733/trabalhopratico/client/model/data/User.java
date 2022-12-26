package pt.isec.pd.a2018020733.trabalhopratico.client.model.data;

import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.LoginStatus;

public class User {

    private String username;
    private LoginStatus status;

    public User(String username, LoginStatus status) {
        this.username = username;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LoginStatus getStatus() {
        return status;
    }

    public void setStatus(LoginStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Olá " + username + " (");
        switch (status){
            case SUCCESSFUL_NORMAL_USER -> sb.append("user");
            case SUCCESSFUL_ADMIN_USER -> sb.append("admin");
            case WRONG_CREDENTIALS -> sb.append("no user");
        }
        sb.append(")");
        return sb.toString();
    }
}
