package server.model.data;

public enum LoginStatus {
    SUCCESSFUL_NORMAL_USER,
    SUCCESSFUL_ADMIN_USER,
    WRONG_CREDENTIALS,
    USER_ALREADY_LOGGED_IN
}
