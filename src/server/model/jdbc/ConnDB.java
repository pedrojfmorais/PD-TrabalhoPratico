package server.model.jdbc;

import server.model.data.Constants;
import server.model.data.LoginStatus;
import server.model.jdbc.db.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class ConnDB
{
    private Connection dbConn;
    private String DB_PATH;

    public ConnDB(String DB_PATH) throws SQLException
    {
        this.DB_PATH = DB_PATH;
        dbConn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
    }

    public void setConnDB(String DB_PATH) throws SQLException
    {
        this.DB_PATH = DB_PATH;
        dbConn = DriverManager.getConnection("jdbc:sqlite:"+DB_PATH);
    }

    public void close() throws SQLException
    {
        if (dbConn != null)
            dbConn.close();
    }

    public void createDB() throws IOException, SQLException {
        DBCreateClearImportExport.createDB(dbConn);
    }

    public void clearDB() throws SQLException {
        DBCreateClearImportExport.clearDB(dbConn);
    }

    public List<List<List<String>>> exportDB() throws SQLException {
        return DBCreateClearImportExport.exportDB(dbConn);
    }

    public boolean importDB(List<List<List<String>>> records) throws SQLException {
        return DBCreateClearImportExport.importDB(records, dbConn);
    }

    public int getVersionDB() throws SQLException{

        int version = 0;
        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT " + TableDatabaseVersion.VERSION.label
                + " FROM " + DatabaseTableNames.DATABASE_VERSION.label;

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next())
        {
            version = resultSet.getInt(TableDatabaseVersion.VERSION.label);
        }

        resultSet.close();
        statement.close();

        return version;
    }

    public void incrementDBVersion() throws SQLException {
        int versaoAtual = getVersionDB();

        Statement statement = dbConn.createStatement();

        String sqlQuery = "UPDATE " + DatabaseTableNames.DATABASE_VERSION.label
                + " SET " + TableDatabaseVersion.VERSION.label + "='" + (versaoAtual + 1) + "'";
        statement.executeUpdate(sqlQuery);
        statement.close();
    }

    public boolean insertUser(String username, String nome, String password) throws SQLException {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "INSERT INTO " + DatabaseTableNames.UTILIZADOR.label + " ("
            + TableUtilizador.USERNAME.label + ", "
                + TableUtilizador.NOME.label + ", "
                + TableUtilizador.PASSWORD.label + ") "
                + "VALUES ('" + username + "','" + nome + "','" + password + "')";

        int rowsAffected = statement.executeUpdate(sqlQuery);
        statement.close();

        if(rowsAffected == 1) {
            incrementDBVersion();
            return true;
        }
        return false;
    }

    public void updateUser(String oldUsername, String username, String nome, String password) throws SQLException {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "UPDATE " + DatabaseTableNames.UTILIZADOR.label + " SET "
                + TableUtilizador.USERNAME.label + "='" + username + "', "
                + TableUtilizador.NOME.label + "='" + nome + "', "
                + TableUtilizador.PASSWORD.label + "='" + password + "' " +
                "WHERE " + TableUtilizador.USERNAME.label + "=" + oldUsername;
        statement.executeUpdate(sqlQuery);
        statement.close();

        incrementDBVersion();
    }

    public LoginStatus verifyLogin(String username, String password) throws SQLException {
        LoginStatus result = LoginStatus.WRONG_CREDENTIALS;
        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT * FROM " + DatabaseTableNames.UTILIZADOR.label
                + " WHERE " + TableUtilizador.USERNAME.label + "='" + username
                + "' and " + TableUtilizador.PASSWORD.label + "='" + password + "'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next())
        {
            //TODO: meter autenticado na bd a 1
            if (resultSet.getInt(TableUtilizador.ADMINISTRADOR.label) == 1)
                result = LoginStatus.SUCCESSFUL_ADMIN_USER;
            else
                result = LoginStatus.SUCCESSFUL_NORMAL_USER;
        }

        resultSet.close();
        statement.close();

        return result;
    }

    public boolean verifyUserExists(String username, String nome) throws SQLException {
        boolean res = false;
        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT * FROM " + DatabaseTableNames.UTILIZADOR.label
                + " WHERE " + TableUtilizador.USERNAME.label + "='" + username
                + "' or " + TableUtilizador.NOME.label + "='" + nome + "'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next())
            res = true;

        resultSet.close();
        statement.close();

        return res;
    }

    public String getUserInformation(String username) throws SQLException {
        StringBuilder sb = new StringBuilder();

        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT * FROM " + DatabaseTableNames.UTILIZADOR.label
                + " WHERE " + TableUtilizador.USERNAME.label + "='" + username + "'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next())
        {
            sb.append(resultSet.getString(TableUtilizador.USERNAME.label)).append(",")
                    .append(resultSet.getString(TableUtilizador.NOME.label)).append(",")
                    .append(resultSet.getInt(TableUtilizador.ADMINISTRADOR.label)  == 1 ? "admin" : "user");
        }

        resultSet.close();
        statement.close();

        return sb.toString().isEmpty() ? null : sb.toString();
    }
}
