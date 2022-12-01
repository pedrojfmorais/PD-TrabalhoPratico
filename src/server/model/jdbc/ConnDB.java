package server.model.jdbc;

import server.model.data.*;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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

    public boolean verifyEspetaculoExists(int id) throws SQLException {
        boolean res = false;
        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT * FROM espetaculo WHERE id='" + id + "'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next())
            res = true;

        resultSet.close();
        statement.close();

        return res;
    }

    // O espetáculo apenas pode ser eliminado caso não existam reservas pagas.
    // Se existirem reservas por pagar, são eliminadas
    public boolean eliminarEspetaculo(int id) throws SQLException {
        Statement statement = dbConn.createStatement();

        // Verificar se existem reservas pagas para este espetáculo
        String sqlQuery = "SELECT * FROM reserva WHERE id_espetaculo='" + id + "'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next()) {
            if(resultSet.getBoolean("pago"))
                return false;
            if(!resultSet.getBoolean("pago")) {
                sqlQuery = "DELETE * FROM reserva WHERE id='" + resultSet.getInt("id") + "'";
                statement.executeUpdate(sqlQuery);
            }
        }

        sqlQuery = "DELETE * FROM espetaculo WHERE id='" + id + "'";

        statement.executeUpdate(sqlQuery);
        statement.close();

        return true;
    }

    // TODO: (Dúvida) Retorno ??
    public boolean editarEspetaculo(int id) throws SQLException {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "UPDATE espetaculo SET visivel='" + 0 + "' WHERE id=" + id;
        statement.executeUpdate(sqlQuery);
        statement.close();

        incrementDBVersion();

        return true;
    }

    // TODO (Dúvida): Guardar data e hora separadas na BD ?
    // Consulta e pesquisa de espetáculos com base em diversos tipos de critérios/filtros (nome, localidade, género, data, etc.);
    public List<Espetaculo> pesquisarEspetaculo(String filtro) throws SQLException {

        Espetaculo espetaculo = null;
        List<Espetaculo> espetaculos = new ArrayList<>();

        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT * FROM espetaculo WHERE descricao='" + filtro + "'" + "OR localidade=''" + filtro + "'" + "OR tipo='" + filtro + "'" + "OR data_hora='" + filtro + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next())
        {
            String sqlQueryLugares = "SELECT lugar.id, lugar.fila, lugar.assento, lugar.preco FROM lugar INNER JOIN espetaculo ON lugar.espetaculo_id=espetaculo.id WHERE espetaculo.id='" + resultSet.getInt("id") + "'";
            ResultSet resultSetLugares = statement.executeQuery(sqlQueryLugares);

            List<Lugar> lugares = new ArrayList<>();

            while(resultSetLugares.next()) {
                Lugar lugar = new Lugar(
                        resultSetLugares.getInt("id"),
                        resultSetLugares.getString("fila"),
                        resultSetLugares.getString("assento"),
                        resultSetLugares.getDouble("preco")
                );
                lugares.add(lugar);
            }

            espetaculo = new Espetaculo(
                    resultSet.getInt("id"),
                    resultSet.getInt("visivel") == 1,
                    resultSet.getString("descricao"),
                    resultSet.getString("tipo"),
                    resultSet.getString("data_hora"),
                    resultSet.getString("data_hora"),
                    resultSet.getInt("duracao"),
                    resultSet.getString("local"),
                    resultSet.getString("localidade"),
                    resultSet.getString("pais"),
                    resultSet.getString("classificacao_etaria"),
                    lugares
            );
            espetaculos.add(espetaculo);
        }

        resultSet.close();
        statement.close();

        return espetaculos;
    }

    public Espetaculo getEspetaculo(int id) throws SQLException {

        Espetaculo espetaculo = null;

        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT * FROM espetaculo WHERE id='" + id + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next())
        {
            String sqlQueryLugares = "SELECT lugar.id, lugar.fila, lugar.assento, lugar.preco FROM lugar INNER JOIN espetaculo ON lugar.espetaculo_id=espetaculo.id WHERE espetaculo.id='" + resultSet.getInt("id") + "'";
            ResultSet resultSetLugares = statement.executeQuery(sqlQueryLugares);

            List<Lugar> lugares = new ArrayList<>();

            while(resultSetLugares.next()) {
                Lugar lugar = new Lugar(
                        resultSetLugares.getInt("id"),
                        resultSetLugares.getString("fila"),
                        resultSetLugares.getString("assento"),
                        resultSetLugares.getDouble("preco")
                );
                lugares.add(lugar);
            }

            espetaculo = new Espetaculo(
                    resultSet.getInt("id"),
                    resultSet.getInt("visivel") == 1,
                    resultSet.getString("descricao"),
                    resultSet.getString("tipo"),
                    resultSet.getString("data_hora"),
                    resultSet.getString("data_hora"),
                    resultSet.getInt("duracao"),
                    resultSet.getString("local"),
                    resultSet.getString("localidade"),
                    resultSet.getString("pais"),
                    resultSet.getString("classificacao_etaria"),
                    lugares
            );
        }

        resultSet.close();
        statement.close();

        return espetaculo;
    }

    public boolean verifyReservaExists(int id) throws SQLException {
        boolean res = false;
        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT * FROM reserva WHERE id='" + id + "'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next())
            res = true;

        resultSet.close();
        statement.close();

        return res;
    }

    public boolean pagarReserva(int id) throws SQLException {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "UPDATE reserva SET pago='" + 1 + "' WHERE id=" + id;
        statement.executeUpdate(sqlQuery);
        statement.close();

        incrementDBVersion();

        return true;
    }

    public boolean eliminarReserva(int id) throws SQLException {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT pago FROM reserva WHERE id=" + id;
        statement.executeQuery(sqlQuery);

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next())
        {
            if(resultSet.getInt("pago") == 1) // Reserva paga
                return false;
        }
        sqlQuery = "DELETE * FROM reserva WHERE id='" + id + "'";
        statement.executeUpdate(sqlQuery);

        statement.close();
        incrementDBVersion();

        return true;
    }

    public List<Reserva> getReservas(boolean pago) throws SQLException {

        List<Reserva> reservas = new ArrayList<>();
        Reserva reserva = null;
        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT * FROM reserva WHERE pago=" + pago;
        statement.executeQuery(sqlQuery);

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next()) {
            String sqlQueryUsername = "SELECT utilizador.username FROM utilizador INNER JOIN reserva ON reserva.id_utilizador=utilizador.id WHERE reserva.id='" + resultSet.getInt("id") + "'";
            ResultSet resultSetUsername = statement.executeQuery(sqlQueryUsername);

            String sqlQueryEspetaculo = "SELECT * FROM espetaculo INNER JOIN reserva ON reserva.id_espetaculo=espetaculo.id WHERE reserva.id='" + resultSet.getInt("id") + "'";
            ResultSet resultSetEspetaculo = statement.executeQuery(sqlQueryEspetaculo);

            Espetaculo espetaculo = getEspetaculo(resultSetEspetaculo.getInt("id"));

            String sqlQueryLugares = "SELECT lugar.id, lugar.fila, lugar.assento, lugar.preco FROM lugar INNER JOIN reserva_lugar ON lugar.reserva_id=reserva_lugar.reserva_id WHERE lugar.reserva_id='" + resultSet.getInt("id") + "'";
            ResultSet resultSetLugares = statement.executeQuery(sqlQueryLugares);

            List<Lugar> lugares = new ArrayList<>();

            while(resultSetLugares.next()) {
                Lugar lugar = new Lugar(
                        resultSetLugares.getInt("id"),
                        resultSetLugares.getString("fila"),
                        resultSetLugares.getString("assento"),
                        resultSetLugares.getDouble("preco")
                );
                lugares.add(lugar);
            }

            reserva = new Reserva(
                    resultSet.getInt("id"),
                    resultSetUsername.getString("username"),
                    espetaculo,
                    lugares,
                    resultSet.getBoolean("pago"),
                    new Date()
            );
            reservas.add(reserva);
        }
        return reservas;
    }

/*
    public void listUsers(String whereName) throws SQLException
    {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT id, name, birthdate FROM users";
        if (whereName != null)
            sqlQuery += " WHERE name like '%" + whereName + "%'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next())
        {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            Date birthdate = resultSet.getDate("birthdate");
            System.out.println("[" + id + "] " + name + " (" + birthdate + ")");
        }

        resultSet.close();
        statement.close();
    }

    public void insertUser(String name, String birthdate) throws SQLException
    {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "INSERT INTO users VALUES (NULL,'" + name + "','" + birthdate + "')";
        statement.executeUpdate(sqlQuery);
        statement.close();
    }

    public void updateUser(int id, String name, String birthdate) throws SQLException
    {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "UPDATE users SET name='" + name + "', " +
                            "BIRTHDATE='" + birthdate + "' WHERE id=" + id;
        statement.executeUpdate(sqlQuery);
        statement.close();
    }

    public void deleteUser(int id) throws SQLException
    {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "DELETE FROM users WHERE id=" + id;
        statement.executeUpdate(sqlQuery);
        statement.close();
    }

    public static void main(String[] args)
    {
        try
        {
            ConnDB connDB = new ConnDB();
            Scanner scanner = new Scanner(System.in);
            boolean exit = false;

            while (!exit)
            {
                System.out.print("Command: ");
                String command = scanner.nextLine();
                String[] comParts = command.split(",");

                if (command.startsWith("select"))
                    connDB.listUsers(null);
                else if (command.startsWith("find"))
                    connDB.listUsers(comParts[1]);
                else if (command.startsWith("insert"))
                    connDB.insertUser(comParts[1], comParts[2]);
                else if (command.startsWith("update"))
                    connDB.updateUser(Integer.parseInt(comParts[1]), comParts[2], comParts[3]);
                else if (command.startsWith("delete"))
                    connDB.deleteUser(Integer.parseInt(comParts[1]));
                else
                    exit = true;
            }

            connDB.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    */
}
