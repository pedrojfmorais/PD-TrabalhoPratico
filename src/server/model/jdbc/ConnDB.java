package server.model.jdbc;

import server.model.data.*;
import server.model.data.LoginStatus;
import server.model.data.viewModels.Espetaculo;
import server.model.data.viewModels.Lugar;
import server.model.data.viewModels.Reserva;
import server.model.jdbc.db.*;

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConnDB {
    private Connection dbConn;
    private String DB_PATH;

    public ConnDB(String DB_PATH) throws SQLException {
        this.DB_PATH = DB_PATH;
        dbConn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
    }

    public void setConnDB(String DB_PATH) throws SQLException {
        this.DB_PATH = DB_PATH;
        dbConn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
    }

    public void close() throws SQLException {
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

    public int getVersionDB() throws SQLException {

        int version = 0;
        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT " + TableDatabaseVersion.VERSION.label
                + " FROM " + DatabaseTableNames.DATABASE_VERSION.label;

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next()) {
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

        if (rowsAffected == 1) {
            incrementDBVersion();
            return true;
        }
        return false;
    }

    public boolean updateUser(String oldUsername, String username, String nome, String password) throws SQLException {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "UPDATE " + DatabaseTableNames.UTILIZADOR.label + " SET "
                + TableUtilizador.USERNAME.label + "='" + username + "', "
                + TableUtilizador.NOME.label + "='" + nome + "', "
                + TableUtilizador.PASSWORD.label + "='" + password + "' " +
                "WHERE " + TableUtilizador.USERNAME.label + "='" + oldUsername + "'";
        int linhasAfetadas = statement.executeUpdate(sqlQuery);
        statement.close();

        if(linhasAfetadas > 0)
            incrementDBVersion();

        return linhasAfetadas > 0;
    }

    public LoginStatus verifyLogin(String username, String password) throws SQLException {
        LoginStatus result = LoginStatus.WRONG_CREDENTIALS;
        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT * FROM " + DatabaseTableNames.UTILIZADOR.label
                + " WHERE " + TableUtilizador.USERNAME.label + "='" + username
                + "' and " + TableUtilizador.PASSWORD.label + "='" + password + "'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next()) {
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

        while (resultSet.next())
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

        while (resultSet.next()) {
            sb.append(resultSet.getString(TableUtilizador.ID.label)).append(",")
                    .append(resultSet.getString(TableUtilizador.USERNAME.label)).append(",")
                    .append(resultSet.getString(TableUtilizador.NOME.label)).append(",")
                    .append(resultSet.getInt(TableUtilizador.ADMINISTRADOR.label) == 1 ? "admin" : "user")
                    .append(resultSet.getInt(TableUtilizador.AUTENTICADO.label) == 1 ? "logado" : "não logado");
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

        while (resultSet.next())
            res = true;

        resultSet.close();
        statement.close();

        return res;
    }

    // O espetáculo apenas pode ser eliminado caso não existam reservas pagas.
    // Se existirem reservas por pagar, são eliminadas
    public boolean eliminarEspetaculo(int id) throws SQLException {

        String sqlQuery = "SELECT * FROM " + DatabaseTableNames.RESERVA.label
                + " WHERE " + TableReserva.ID_ESPETACULO.label + "='" + id + "'"
                + " AND " + TableReserva.PAGO.label + "='1'";

        if (dbConn.createStatement().executeQuery(sqlQuery).next())
            return false;

        // Verificar se existem reservas pagas para este espetáculo
        sqlQuery = "SELECT * FROM " + DatabaseTableNames.RESERVA.label
                + " WHERE " + TableReserva.ID_ESPETACULO.label + "='" + id + "'";

        ResultSet resultSet = dbConn.createStatement().executeQuery(sqlQuery);

        while (resultSet.next()) {

            sqlQuery = "DELETE FROM " + DatabaseTableNames.RESERVA_LUGAR
                    + " WHERE " + TableReservaLugar.ID_RESERVA.label + "='"
                    + resultSet.getInt(TableReserva.ID.label) + "'";

            dbConn.createStatement().executeUpdate(sqlQuery);

            sqlQuery = "DELETE FROM " + DatabaseTableNames.RESERVA.label
                    + " WHERE " + TableReserva.ID.label + "='"
                    + resultSet.getInt(TableReserva.ID.label) + "'";

            dbConn.createStatement().executeUpdate(sqlQuery);

        }

        sqlQuery = "DELETE FROM " + DatabaseTableNames.LUGAR.label
                + " WHERE " + TableLugar.ESPETACULO_ID.label + "='" + id + "'";

        dbConn.createStatement().executeUpdate(sqlQuery);

        sqlQuery = "DELETE FROM " + DatabaseTableNames.ESPETACULO.label
                + " WHERE " + TableEspetaculo.ID.label + "='" + id + "'";

        dbConn.createStatement().executeUpdate(sqlQuery);

        incrementDBVersion();

        return true;
    }

    public boolean tornarEspetaculoVisivel(int id) throws SQLException {
        Statement statement = dbConn.createStatement();

        String sqlQueryJaVisivel = "SELECT * FROM " + DatabaseTableNames.ESPETACULO.label
                + " WHERE " + TableEspetaculo.ID.label + "='" + id + "'";

        ResultSet resultSet = dbConn.createStatement().executeQuery(sqlQueryJaVisivel);

        while(resultSet.next())
            if(resultSet.getInt(TableEspetaculo.VISIVEL.label) == 1)
                return false;

        String sqlQuery = "UPDATE " + DatabaseTableNames.ESPETACULO.label
                + " SET " + TableEspetaculo.VISIVEL.label + "='1'" +
                " WHERE " + TableEspetaculo.ID.label + "='" + id + "'";

        int rowsAffected = statement.executeUpdate(sqlQuery);

        statement.close();
        incrementDBVersion();

        return rowsAffected > 0;
    }

    // Consulta e pesquisa de espetáculos com base em diversos tipos de critérios/filtros (nome, localidade, género, data, etc.);
    public List<Espetaculo> pesquisarEspetaculo(String filtro, boolean admin) throws SQLException, ParseException {

        Espetaculo espetaculo;
        List<Espetaculo> espetaculos = new ArrayList<>();

        String sqlQuery = "SELECT * FROM " + DatabaseTableNames.ESPETACULO.label;

        if (!filtro.isBlank())
            sqlQuery += " WHERE (" + TableEspetaculo.DESCRICAO.label + " LIKE '%" + filtro + "%'"
                    + " OR " + TableEspetaculo.LOCALIDADE.label + " LIKE '%" + filtro + "%'"
                    + " OR " + TableEspetaculo.TIPO.label + " LIKE '%" + filtro + "%'"
                    + " OR " + TableEspetaculo.DATA_HORA.label + " LIKE '%" + filtro + "%')";

        if (!admin) {
            if (filtro.isBlank())
                sqlQuery += " WHERE ";
            else
                sqlQuery += " AND ";

            sqlQuery += TableEspetaculo.VISIVEL.label + "='1'";
        }

        Statement statement = dbConn.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next()) {
            String sqlQueryLugares = "SELECT * FROM " + DatabaseTableNames.LUGAR.label
                    + " WHERE " + TableLugar.ESPETACULO_ID.label + "='"
                    + resultSet.getInt(TableEspetaculo.ID.label) + "'";

            ResultSet resultSetLugares = dbConn.createStatement().executeQuery(sqlQueryLugares);

            List<Lugar> lugares = new ArrayList<>();

            while (resultSetLugares.next()) {
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
                    Constants.formatterDate.parse(resultSet.getString(TableEspetaculo.DATA_HORA.label)),
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

    public Espetaculo getEspetaculo(int id) throws SQLException, ParseException {

        Espetaculo espetaculo = null;

        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT * FROM " + DatabaseTableNames.ESPETACULO.label
                + " WHERE " + TableEspetaculo.ID.label + "='" + id + "'";
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next()) {
            String sqlQueryLugares = "SELECT * FROM " + DatabaseTableNames.LUGAR.label
                    + " WHERE " + TableLugar.ESPETACULO_ID.label + "='"
                    + resultSet.getInt(TableEspetaculo.ID.label) + "'";
            ResultSet resultSetLugares = dbConn.createStatement().executeQuery(sqlQueryLugares);

            List<Lugar> lugares = new ArrayList<>();

            while (resultSetLugares.next()) {
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
                    Constants.formatterDate.parse(resultSet.getString(TableEspetaculo.DATA_HORA.label)),
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

        while (resultSet.next())
            res = true;

        resultSet.close();
        statement.close();

        return res;
    }

    public boolean pagarReserva(int id) throws SQLException {
        Statement statement = dbConn.createStatement();

        String sqlQueryVerificaPago = "SELECT * FROM " + DatabaseTableNames.RESERVA.label
                + " WHERE " + TableReserva.ID.label + "='" + id + "'";

        ResultSet resultSet = dbConn.createStatement().executeQuery(sqlQueryVerificaPago);

        while(resultSet.next())
            if(resultSet.getInt(TableReserva.PAGO.label) == 1)
                return false;

        String sqlQuery = "UPDATE " + DatabaseTableNames.RESERVA.label
                + " SET " + TableReserva.PAGO.label + "='1'"
                + " WHERE " + TableReserva.ID.label + "='" + id + "'";
        statement.executeUpdate(sqlQuery);
        statement.close();

        incrementDBVersion();

        return true;
    }

    public boolean eliminarReserva(int id) throws SQLException {
        Statement statement = dbConn.createStatement();

        String sqlQuery = "SELECT * FROM " + DatabaseTableNames.RESERVA.label
                + " WHERE " + TableReserva.ID.label + "='" + id + "'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next()) {
            if (resultSet.getInt(TableReserva.PAGO.label) == 1) // Reserva paga
                return false;

            sqlQuery = "DELETE FROM " + DatabaseTableNames.RESERVA_LUGAR
                    + " WHERE " + TableReservaLugar.ID_RESERVA.label + "='" + id + "'";

            dbConn.createStatement().executeUpdate(sqlQuery);

            sqlQuery = "DELETE FROM " + DatabaseTableNames.RESERVA.label
                    + " WHERE " + TableReserva.ID.label + "='" + id + "'";

            dbConn.createStatement().executeUpdate(sqlQuery);
        }
        statement.close();

        incrementDBVersion();

        return true;
    }

    public List<Reserva> getReservas(boolean pago, String username) throws SQLException, ParseException {

        List<Reserva> reservas = new ArrayList<>();
        Reserva reserva;
        Statement statement = dbConn.createStatement();

        int reservaPaga = pago ? 1 : 0;

        String[] user = getUserInformation(username).split(",");

        String sqlQuery = "SELECT * FROM " + DatabaseTableNames.RESERVA.label
                + " WHERE " + TableReserva.PAGO.label + "='" + reservaPaga + "'"
                + " AND " + TableReserva.ID_UTILIZADOR + "='" + user[0] + "'";

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        while (resultSet.next()) {

            int idReserva = resultSet.getInt(TableReserva.ID.label);

            String sqlQueryEspetaculo = "SELECT * FROM " + DatabaseTableNames.ESPETACULO.label
                    + " WHERE " + TableEspetaculo.ID.label + "='" + idReserva + "'";

            ResultSet resultSetEspetaculo = dbConn.createStatement().executeQuery(sqlQueryEspetaculo);

            Espetaculo espetaculo = getEspetaculo(resultSetEspetaculo.getInt("id"));

            String sqlQueryIdLugaresReserva = "SELECT * FROM " + DatabaseTableNames.RESERVA_LUGAR.label
                    + " WHERE " + TableReservaLugar.ID_RESERVA.label + "='" + idReserva + "'";

            ResultSet resultSetIdLugaresReserva = dbConn.createStatement().executeQuery(sqlQueryIdLugaresReserva);

            List<Lugar> lugares = new ArrayList<>();
            while (resultSetIdLugaresReserva.next()){

                String sqlQueryLugares = "SELECT * FROM " + DatabaseTableNames.LUGAR.label
                        + " WHERE " + TableLugar.ID.label
                        + "='" + resultSetIdLugaresReserva.getInt(TableReservaLugar.ID_LUGAR.label) + "'";

                ResultSet resultSetLugares = dbConn.createStatement().executeQuery(sqlQueryLugares);

                while (resultSetLugares.next()) {
                    Lugar lugar = new Lugar(
                            resultSetLugares.getInt(TableLugar.ID.label),
                            resultSetLugares.getString(TableLugar.FILA.label),
                            resultSetLugares.getString(TableLugar.ASSENTO.label),
                            resultSetLugares.getDouble(TableLugar.PRECO.label)
                    );
                    lugares.add(lugar);
                }
            }

            reserva = new Reserva(
                    idReserva,
                    username,
                    espetaculo,
                    lugares,
                    resultSet.getBoolean(TableReserva.PAGO.label),
                    Constants.formatterDate.parse(resultSet.getString(TableReserva.DATA_HORA.label))
            );
            reservas.add(reserva);
        }
        return reservas;
    }
}
