package server.model.jdbc;

import server.model.data.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnDB
{
    private Connection dbConn;

    public ConnDB(String DB_PATH) throws SQLException
    {
        dbConn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
    }

    public void setConnDB(String DB_PATH) throws SQLException
    {
        dbConn = DriverManager.getConnection("jdbc:sqlite:"+DB_PATH);
    }

    public void close() throws SQLException
    {
        if (dbConn != null)
            dbConn.close();
    }

    public void createDB() throws IOException, SQLException {

        Statement statement = dbConn.createStatement();
        List<String> sqlScript = Files.readAllLines(Paths.get(Constants.DATABASE_CREATE_SCRIPT_PATH));
        for(String script: sqlScript) {
            if(script.startsWith("--"))
                continue;

            statement.executeUpdate(script);
        }
        statement.close();
    }

    public void cleanDB(){
        //TODO: truncate todas tabelas
    }

    //TODO: ver folha
    public void exportDB() throws SQLException {
        Statement stmt = dbConn.createStatement();

        String filename = "outfile.txt";
        String tablename = "utilizador";
        stmt.executeUpdate("SELECT * INTO OUTFILE '" + filename + "' FROM " + tablename);

        stmt.close();
    }

    public int getVersionDB() throws SQLException{

        int version = 0;
        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT version FROM database_version";

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next())
        {
            version = resultSet.getInt("version");
        }

        resultSet.close();
        statement.close();

        return version;
    }

    public void incrementDBVersion() throws SQLException
    {
        int versaoAtual = getVersionDB();

        Statement statement = dbConn.createStatement();

        String sqlQuery = "UPDATE database_version SET version='" + (versaoAtual + 1) + "'";
        statement.executeUpdate(sqlQuery);
        statement.close();
    }

    public boolean insertUser(String username, String nome, String password) throws SQLException {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "INSERT INTO utilizador (username, nome, password) " +
                "VALUES ('" + username + "','" + nome + "','" + password + "')";

        int rowsAffected = statement.executeUpdate(sqlQuery);
        statement.close();

        if(rowsAffected == 1) {
            incrementDBVersion();
            return true;
        }
        return false;
    }

    public void updateUser(String oldUsername, String username, String nome, String password) throws SQLException
    {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "UPDATE utilizador SET username='" + username + "', nome='" + nome + "', " +
                "password='" + password + "' WHERE username=" + oldUsername;
        statement.executeUpdate(sqlQuery);
        statement.close();

        incrementDBVersion();
    }

    public LoginStatus verifyLogin(String username, String password) throws SQLException {
        LoginStatus result = LoginStatus.WRONG_CREDENTIALS;
        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT * FROM utilizador " +
                "WHERE username='" + username + "' and password='" + password + "'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next())
        {
            if (resultSet.getInt("administrador") == 1)
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

        String sqlQuery = "SELECT * FROM utilizador WHERE username='" + username + "' or nome='" + nome + "'";

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

        String sqlQuery = "SELECT * FROM utilizador WHERE username='" + username + "'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next())
        {
            sb.append(resultSet.getString("username")).append(",")
                    .append(resultSet.getString("nome")).append(",")
                    .append(resultSet.getInt("administrador")  == 1 ? "admin" : "user");
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

    // TODO: (Dúvida) Retorno ??
    public boolean eliminarEspetaculo(int id) throws SQLException {
        boolean res = false;
        Statement statement = dbConn.createStatement();

        String sqlQuery = "DELETE * FROM espetaculo WHERE id='" + id + "'";

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
    public List<Espetaculo> pesquisarEspetaculo(String filtro) throws SQLException {

        Espetaculo espetaculo = null;
        List<Espetaculo> espetaculos = new ArrayList<>();

        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT * FROM espetaculo WHERE descricao='" + filtro + "'";
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
