package server.model.jdbc;

import server.model.data.*;
import server.model.data.LoginStatus;
import server.model.jdbc.db.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
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

        String sqlQuery = "SELECT * FROM " + DatabaseTableNames.ESPETACULO.label
                + " WHERE " + TableEspetaculo.ID.label + "='" + id + "'";

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
        String sqlQuery = "SELECT * FROM " + DatabaseTableNames.RESERVA.label
                + " WHERE " + TableReserva.ID_ESPETACULO.label + "='" + id + "'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next()) {
            if(resultSet.getBoolean(TableReserva.PAGO.label))
                return false;
            if(!resultSet.getBoolean(TableReserva.PAGO.label)) {
                sqlQuery = "DELETE * FROM " + DatabaseTableNames.RESERVA.label
                        + " WHERE " + TableReserva.ID.label + "='"
                        + resultSet.getInt(TableReserva.ID.label) + "'";
                statement.executeUpdate(sqlQuery);
            }
        }

        sqlQuery = "DELETE * FROM "+ DatabaseTableNames.ESPETACULO.label
                +" WHERE " + TableEspetaculo.ID.label + "='" + id + "'";

        statement.executeUpdate(sqlQuery);
        statement.close();

        return true;
    }

    // TODO: (Dúvida) Retorno ??
    public boolean editarEspetaculo(int id) throws SQLException {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "UPDATE " +  DatabaseTableNames.ESPETACULO.label
                + " SET " + TableEspetaculo.VISIVEL.label + "='" + 0 + "' " +
                "WHERE " + TableEspetaculo.ID.label + "=" + id;
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

        String sqlQuery = "SELECT * FROM " + DatabaseTableNames.ESPETACULO.label
                + " WHERE " + TableEspetaculo.DESCRICAO.label + "='" + filtro + "'"
                + " OR " +  TableEspetaculo.LOCALIDADE.label + "='" + filtro + "'"
                + " OR " +  TableEspetaculo.TIPO.label + "='" + filtro + "'"
                + " OR " +  TableEspetaculo.DATA_HORA.label + "='" + filtro + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next())
        {
            String sqlQueryLugares = "SELECT "
                    + DatabaseTableNames.LUGAR.label + "." + TableLugar.ID.label + ", "
                    + DatabaseTableNames.LUGAR.label + "." + TableLugar.FILA.label + ", "
                    + DatabaseTableNames.LUGAR.label + "." + TableLugar.ASSENTO.label + ", "
                    + DatabaseTableNames.LUGAR.label + "." + TableLugar.PRECO.label
                    + " FROM " + DatabaseTableNames.LUGAR.label
                    + " INNER JOIN " + DatabaseTableNames.ESPETACULO.label
                    + " ON " + DatabaseTableNames.LUGAR.label + "." + TableLugar.ESPETACULO_ID.label
                    + "=" + DatabaseTableNames.ESPETACULO.label + "." + TableEspetaculo.ID.label
                    + " WHERE "+ DatabaseTableNames.ESPETACULO.label + "." + TableEspetaculo.ID.label
                    + "='" + resultSet.getInt(TableEspetaculo.ID.label) + "'";
            ResultSet resultSetLugares = statement.executeQuery(sqlQueryLugares);

            List<Lugar> lugares = new ArrayList<>();

            while(resultSetLugares.next()) {
                Lugar lugar = new Lugar(
                        resultSetLugares.getInt(TableLugar.ID.label),
                        resultSetLugares.getString(TableLugar.FILA.label),
                        resultSetLugares.getString(TableLugar.ASSENTO.label),
                        resultSetLugares.getDouble(TableLugar.PRECO.label)
                );
                lugares.add(lugar);
            }

            espetaculo = new Espetaculo(
                    resultSet.getInt(TableEspetaculo.ID.label),
                    resultSet.getInt(TableEspetaculo.VISIVEL.label) == 1,
                    resultSet.getString(TableEspetaculo.DESCRICAO.label),
                    resultSet.getString(TableEspetaculo.TIPO.label),
                    new Date(resultSet.getString(TableEspetaculo.DATA_HORA.label)),
                    resultSet.getInt(TableEspetaculo.DURACAO.label),
                    resultSet.getString(TableEspetaculo.LOCAL.label),
                    resultSet.getString(TableEspetaculo.LOCALIDADE.label),
                    resultSet.getString(TableEspetaculo.PAIS.label),
                    resultSet.getString(TableEspetaculo.CLASSIFICACAO_ETARIA.label),
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

        String sqlQuery = "SELECT * FROM " + DatabaseTableNames.ESPETACULO.label
                + " WHERE " + TableEspetaculo.ID.label + "='" + id + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next())
        {
            String sqlQueryLugares = "SELECT "
                    + DatabaseTableNames.LUGAR.label + "." + TableLugar.ID.label + ", "
                    + DatabaseTableNames.LUGAR.label + "." + TableLugar.FILA.label + ", "
                    + DatabaseTableNames.LUGAR.label + "." + TableLugar.ASSENTO.label + ", "
                    + DatabaseTableNames.LUGAR.label + "." + TableLugar.PRECO.label
                    +" FROM "+ DatabaseTableNames.LUGAR.label
                    +" INNER JOIN "+ DatabaseTableNames.ESPETACULO.label
                    + " ON " + DatabaseTableNames.LUGAR.label + "." + TableLugar.ESPETACULO_ID.label
                    + "=" + DatabaseTableNames.ESPETACULO.label + "." + TableEspetaculo.ID.label
                    + " WHERE "+ DatabaseTableNames.ESPETACULO.label + "." + TableEspetaculo.ID.label
                    + "='" + resultSet.getInt(TableEspetaculo.ID.label) + "'";
            ResultSet resultSetLugares = statement.executeQuery(sqlQueryLugares);

            List<Lugar> lugares = new ArrayList<>();

            while(resultSetLugares.next()) {
                Lugar lugar = new Lugar(
                        resultSetLugares.getInt(TableLugar.ID.label),
                        resultSetLugares.getString(TableLugar.FILA.label),
                        resultSetLugares.getString(TableLugar.ASSENTO.label),
                        resultSetLugares.getDouble(TableLugar.PRECO.label)
                );
                lugares.add(lugar);
            }

            espetaculo = new Espetaculo(
                    resultSet.getInt(TableEspetaculo.ID.label),
                    resultSet.getInt(TableEspetaculo.VISIVEL.label) == 1,
                    resultSet.getString(TableEspetaculo.DESCRICAO.label),
                    resultSet.getString(TableEspetaculo.TIPO.label),
                    new Date(resultSet.getString(TableEspetaculo.DATA_HORA.label)),
                    resultSet.getInt(TableEspetaculo.DURACAO.label),
                    resultSet.getString(TableEspetaculo.LOCAL.label),
                    resultSet.getString(TableEspetaculo.LOCALIDADE.label),
                    resultSet.getString(TableEspetaculo.PAIS.label),
                    resultSet.getString(TableEspetaculo.CLASSIFICACAO_ETARIA.label),
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

        String sqlQuery = "SELECT * FROM " + DatabaseTableNames.RESERVA.label
                + " WHERE " + TableReserva.ID.label + "='" + id + "'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next())
            res = true;

        resultSet.close();
        statement.close();

        return res;
    }

    public boolean pagarReserva(int id) throws SQLException {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "UPDATE "+ DatabaseTableNames.RESERVA.label
                + " SET " + TableReserva.PAGO.label + "='" + 1
                + "' WHERE " + TableReserva.ID.label + "=" + id;
        statement.executeUpdate(sqlQuery);
        statement.close();

        incrementDBVersion();

        return true;
    }

    public boolean eliminarReserva(int id) throws SQLException {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT " + TableReserva.PAGO.label
                + " FROM " + DatabaseTableNames.RESERVA.label
                + " WHERE " + TableReserva.ID.label  + "=" + id;
        statement.executeQuery(sqlQuery);

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next())
        {
            if(resultSet.getInt(TableReserva.PAGO.label) == 1) // Reserva paga
                return false;
        }
        sqlQuery = "DELETE * FROM " + DatabaseTableNames.RESERVA.label
                + " WHERE " + TableReserva.ID.label  + "='" + id + "'";
        statement.executeUpdate(sqlQuery);

        statement.close();
        incrementDBVersion();

        return true;
    }

    public List<Reserva> getReservas(boolean pago) throws SQLException {

        List<Reserva> reservas = new ArrayList<>();
        Reserva reserva = null;
        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT * FROM " + DatabaseTableNames.RESERVA.label
                + " WHERE " + TableReserva.PAGO.label + "=" + pago;
        statement.executeQuery(sqlQuery);

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while(resultSet.next()) {
            String sqlQueryUsername = "SELECT "
                    + DatabaseTableNames.UTILIZADOR.label + "." + TableUtilizador.USERNAME.label
                    + " FROM " + DatabaseTableNames.UTILIZADOR.label
                    + " INNER JOIN " + DatabaseTableNames.RESERVA.label
                    + " ON " + DatabaseTableNames.RESERVA.label + "." + TableReserva.ID_UTILIZADOR.label
                    + "=" + DatabaseTableNames.UTILIZADOR.label + "." + TableUtilizador.ID.label
                    +" WHERE " + DatabaseTableNames.RESERVA.label + "." + TableReserva.ID.label
                    +"='" + resultSet.getInt(TableReserva.ID.label) + "'";
            ResultSet resultSetUsername = statement.executeQuery(sqlQueryUsername);

            String sqlQueryEspetaculo = "SELECT * FROM " + DatabaseTableNames.ESPETACULO.label
                    + " INNER JOIN "+ DatabaseTableNames.RESERVA.label
                    + " ON "+ DatabaseTableNames.RESERVA.label + "." + TableReserva.ID_ESPETACULO.label
                    + "=" + DatabaseTableNames.ESPETACULO.label + "." + TableEspetaculo.ID.label
                    + " WHERE "+ DatabaseTableNames.RESERVA.label + "." + TableReserva.ID.label
                    + "='" + resultSet.getInt(TableReserva.ID.label) + "'";

            ResultSet resultSetEspetaculo = statement.executeQuery(sqlQueryEspetaculo);

            Espetaculo espetaculo = getEspetaculo(resultSetEspetaculo.getInt("id"));

            String sqlQueryLugares = "SELECT "
                    + DatabaseTableNames.LUGAR.label + "." + TableLugar.ID.label + ", "
                    + DatabaseTableNames.LUGAR.label + "." + TableLugar.FILA.label + ", "
                    + DatabaseTableNames.LUGAR.label + "." + TableLugar.ASSENTO.label + ", "
                    + DatabaseTableNames.LUGAR.label + "." + TableLugar.PRECO.label
                    +" FROM " + DatabaseTableNames.LUGAR.label
                    + " INNER JOIN " + DatabaseTableNames.RESERVA_LUGAR.label
                    + " ON " + DatabaseTableNames.LUGAR.label + "." + TableLugar.ID.label
                    + "=" + DatabaseTableNames.RESERVA_LUGAR.label + "." + TableReservaLugar.ID_LUGAR
                    + "WHERE " + DatabaseTableNames.RESERVA_LUGAR.label + "." + TableReservaLugar.ID_RESERVA
                    + "='" + resultSet.getInt(TableReserva.ID.label) + "'";

            ResultSet resultSetLugares = statement.executeQuery(sqlQueryLugares);

            List<Lugar> lugares = new ArrayList<>();

            while(resultSetLugares.next()) {
                Lugar lugar = new Lugar(
                        resultSetLugares.getInt(TableLugar.ID.label),
                        resultSetLugares.getString(TableLugar.FILA.label),
                        resultSetLugares.getString(TableLugar.ASSENTO.label),
                        resultSetLugares.getDouble(TableLugar.PRECO.label)
                );
                lugares.add(lugar);
            }

            reserva = new Reserva(
                    resultSet.getInt(TableReserva.ID.label),
                    resultSetUsername.getString(TableUtilizador.USERNAME.label),
                    espetaculo,
                    lugares,
                    resultSet.getBoolean(TableReserva.PAGO.label),
                    new Date()
            );
            reservas.add(reserva);
        }
        return reservas;
    }
}
