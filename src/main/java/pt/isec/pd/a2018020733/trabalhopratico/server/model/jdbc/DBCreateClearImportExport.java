package pt.isec.pd.a2018020733.trabalhopratico.server.model.jdbc;

import pt.isec.pd.a2018020733.trabalhopratico.server.model.data.Constants;
import pt.isec.pd.a2018020733.trabalhopratico.server.model.jdbc.db.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public final class DBCreateClearImportExport {

    //CREATE
    static void createDB(Connection dbConn) throws IOException, SQLException {

        Statement statement = dbConn.createStatement();
        List<String> sqlScript = Files.readAllLines(Paths.get(Constants.DATABASE_CREATE_SCRIPT_PATH));
        for(String script: sqlScript) {
            if(script.startsWith("--"))
                continue;

            statement.executeUpdate(script);
        }
        statement.close();
    }

    //CLEAR
    static void clearDB(Connection dbConn) throws SQLException {

        List<String> tableNames = new ArrayList<>();

        for(var tableName : EnumSet.allOf(DatabaseTableNames.class))
            tableNames.add(tableName.label);

        Statement statement = dbConn.createStatement();

        for(var tableName : tableNames) {
            statement.execute("DELETE FROM " + tableName);
        }

        statement.close();
    }

    //EXPORT
    static List<List<List<String>>> exportDB(Connection dbConn) throws SQLException {

        List<List<List<String>>> result = new ArrayList<>();

        result.add(getDatabaseVersionRecords(dbConn));
        result.add(getUtilizadorRecords(dbConn));
        result.add(getEspetaculoRecords(dbConn));
        result.add(getLugarRecords(dbConn));
        result.add(getReservaRecords(dbConn));
        result.add(getReservaLugarRecords(dbConn));

        for(var tabela : result)
            for (var row : tabela)
                System.out.println(Arrays.toString(row.toArray()));

        return result;
    }

    private static List<List<String>> getDatabaseVersionRecords(Connection dbConn) throws SQLException {

        List<List<String>> database_version = new ArrayList<>();

        Statement statement = dbConn.createStatement();

        ResultSet resultSet = statement.executeQuery(
                "SELECT * FROM " + DatabaseTableNames.DATABASE_VERSION.label
        );

        while(resultSet.next()) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(resultSet.getInt(TableDatabaseVersion.VERSION.label)));
            database_version.add(row);
        }

        statement.close();

        return database_version;
    }

    private static List<List<String>> getUtilizadorRecords(Connection dbConn) throws SQLException {

        List<List<String>> utilizador = new ArrayList<>();

        Statement statement = dbConn.createStatement();

        ResultSet resultSet = statement.executeQuery(
                "SELECT * FROM " + DatabaseTableNames.UTILIZADOR.label
        );

        while(resultSet.next()) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(resultSet.getInt(TableUtilizador.ID.label)));
            row.add(resultSet.getString(TableUtilizador.USERNAME.label));
            row.add(resultSet.getString(TableUtilizador.NOME.label));
            row.add(resultSet.getString(TableUtilizador.PASSWORD.label));
            row.add(String.valueOf(resultSet.getInt(TableUtilizador.ADMINISTRADOR.label)));
            row.add(String.valueOf(resultSet.getInt(TableUtilizador.AUTENTICADO.label)));
            utilizador.add(row);
        }

        statement.close();

        return utilizador;
    }

    private static List<List<String>> getEspetaculoRecords(Connection dbConn) throws SQLException {

        List<List<String>> espetaculo = new ArrayList<>();

        Statement statement = dbConn.createStatement();

        ResultSet resultSet = statement.executeQuery(
                "SELECT * FROM " + DatabaseTableNames.ESPETACULO.label
        );

        while(resultSet.next()) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(resultSet.getInt(TableEspetaculo.ID.label)));
            row.add(resultSet.getString(TableEspetaculo.DESCRICAO.label));
            row.add(resultSet.getString(TableEspetaculo.TIPO.label));
            row.add(resultSet.getString(TableEspetaculo.DATA_HORA.label));
            row.add(String.valueOf(resultSet.getInt(TableEspetaculo.DURACAO.label)));
            row.add(resultSet.getString(TableEspetaculo.LOCAL.label));
            row.add(resultSet.getString(TableEspetaculo.LOCALIDADE.label));
            row.add(resultSet.getString(TableEspetaculo.PAIS.label));
            row.add(resultSet.getString(TableEspetaculo.CLASSIFICACAO_ETARIA.label));
            row.add(String.valueOf(resultSet.getInt(TableEspetaculo.VISIVEL.label)));
            espetaculo.add(row);
        }

        statement.close();

        return espetaculo;
    }

    private static List<List<String>> getLugarRecords(Connection dbConn) throws SQLException {

        List<List<String>> lugar = new ArrayList<>();

        Statement statement = dbConn.createStatement();

        ResultSet resultSet = statement.executeQuery(
                "SELECT * FROM " + DatabaseTableNames.LUGAR.label
        );

        while(resultSet.next()) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(resultSet.getInt(TableLugar.ID.label)));
            row.add(resultSet.getString(TableLugar.FILA.label));
            row.add(resultSet.getString(TableLugar.ASSENTO.label));
            row.add(String.valueOf(resultSet.getDouble(TableLugar.PRECO.label)));
            row.add(String.valueOf(resultSet.getInt(TableLugar.ESPETACULO_ID.label)));
            lugar.add(row);
        }

        statement.close();

        return lugar;
    }

    private static List<List<String>> getReservaRecords(Connection dbConn) throws SQLException {

        List<List<String>> reserva = new ArrayList<>();

        Statement statement = dbConn.createStatement();

        ResultSet resultSet = statement.executeQuery(
                "SELECT * FROM " + DatabaseTableNames.RESERVA.label
        );

        while(resultSet.next()) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(resultSet.getInt(TableReserva.ID.label)));
            row.add(resultSet.getString(TableReserva.DATA_HORA.label));
            row.add(String.valueOf(resultSet.getInt(TableReserva.PAGO.label)));
            row.add(String.valueOf(resultSet.getInt(TableReserva.ID_UTILIZADOR.label)));
            row.add(String.valueOf(resultSet.getInt(TableReserva.ID_ESPETACULO.label)));
            reserva.add(row);
        }

        statement.close();

        return reserva;
    }

    private static List<List<String>> getReservaLugarRecords(Connection dbConn) throws SQLException {

        List<List<String>> reservaLugar = new ArrayList<>();

        Statement statement = dbConn.createStatement();

        ResultSet resultSet = statement.executeQuery(
                "SELECT * FROM " + DatabaseTableNames.RESERVA_LUGAR.label
        );

        while(resultSet.next()) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(resultSet.getInt(TableReservaLugar.ID_RESERVA.label)));
            row.add(String.valueOf(resultSet.getInt(TableReservaLugar.ID_LUGAR.label)));
            reservaLugar.add(row);
        }

        statement.close();

        return reservaLugar;
    }

    //IMPORT
    static boolean importDB(List<List<List<String>>> records, Connection dbConn) throws SQLException {
        return insertDatabaseVersionRecords(records.get(0), dbConn)
                && insertUtilizadorRecords(records.get(1), dbConn)
                && insertEspetaculoRecords(records.get(2), dbConn)
                && insertLugarRecords(records.get(3), dbConn)
                && insertReservaRecords(records.get(4), dbConn)
                && insertReservaLugarRecords(records.get(5), dbConn);
    }

    private static boolean insertDatabaseVersionRecords(List<List<String>> records, Connection dbConn) throws SQLException {

        Statement statement = dbConn.createStatement();
        int counter = 0;

        for (var row : records){
            counter += statement.executeUpdate(
                    "INSERT INTO " + DatabaseTableNames.DATABASE_VERSION.label
                            + " (" + TableDatabaseVersion.VERSION.label + ")"
                            + " VALUES ('" + row.get(0) + "')"
            );
        }

        return counter == records.size();
    }

    private static boolean insertUtilizadorRecords(List<List<String>> records, Connection dbConn) throws SQLException {

        Statement statement = dbConn.createStatement();
        int counter = 0;

        for (var row : records){
            counter += statement.executeUpdate(
                    "INSERT INTO " + DatabaseTableNames.UTILIZADOR.label
                            + " (" + TableUtilizador.ID.label + ", "
                            + TableUtilizador.USERNAME.label + ", "
                            + TableUtilizador.NOME.label + ", "
                            + TableUtilizador.PASSWORD.label + ", "
                            + TableUtilizador.ADMINISTRADOR.label + ", "
                            + TableUtilizador.AUTENTICADO.label + ")"
                            + " VALUES ('"
                            + row.get(0) + "', '"
                            + row.get(1) + "', '"
                            + row.get(2) + "', '"
                            + row.get(3) + "', '"
                            + row.get(4) + "', '"
                            + row.get(5) + "')"
            );
        }

        return counter == records.size();
    }

    private static boolean insertEspetaculoRecords(List<List<String>> records, Connection dbConn) throws SQLException {

        Statement statement = dbConn.createStatement();
        int counter = 0;

        for (var row : records){
            counter += statement.executeUpdate(
                    "INSERT INTO " + DatabaseTableNames.ESPETACULO.label
                            + " ("
                            + TableEspetaculo.ID.label + ", "
                            + TableEspetaculo.DESCRICAO.label + ", "
                            + TableEspetaculo.TIPO.label + ", "
                            + TableEspetaculo.DATA_HORA.label + ", "
                            + TableEspetaculo.DURACAO.label + ", "
                            + TableEspetaculo.LOCAL.label + ", "
                            + TableEspetaculo.LOCALIDADE.label + ", "
                            + TableEspetaculo.PAIS.label + ", "
                            + TableEspetaculo.CLASSIFICACAO_ETARIA.label + ", "
                            + TableEspetaculo.VISIVEL.label
                            + ") VALUES ('"
                            + row.get(0) + "', '"
                            + row.get(1) + "', '"
                            + row.get(2) + "', '"
                            + row.get(3) + "', '"
                            + row.get(4) + "', '"
                            + row.get(5) + "', '"
                            + row.get(6) + "', '"
                            + row.get(7) + "', '"
                            + row.get(8) + "', '"
                            + row.get(9) + "')"
            );
        }

        return counter == records.size();
    }

    private static boolean insertLugarRecords(List<List<String>> records, Connection dbConn) throws SQLException {

        Statement statement = dbConn.createStatement();
        int counter = 0;

        for (var row : records){
            counter += statement.executeUpdate(
                    "INSERT INTO " + DatabaseTableNames.LUGAR.label
                            + " ("
                            + TableLugar.ID.label + ", "
                            + TableLugar.FILA.label + ", "
                            + TableLugar.ASSENTO.label + ", "
                            + TableLugar.PRECO.label + ", "
                            + TableLugar.ESPETACULO_ID.label
                            +") VALUES ('"
                            + row.get(0) + "', '"
                            + row.get(1) + "', '"
                            + row.get(2) + "', '"
                            + row.get(3) + "', '"
                            + row.get(4) + "')"
            );
        }

        return counter == records.size();
    }

    private static boolean insertReservaRecords(List<List<String>> records, Connection dbConn) throws SQLException {

        Statement statement = dbConn.createStatement();
        int counter = 0;

        for (var row : records){
            counter += statement.executeUpdate(
                    "INSERT INTO " + DatabaseTableNames.RESERVA.label
                            + " ("
                            + TableReserva.ID.label + ", "
                            + TableReserva.DATA_HORA.label + ", "
                            + TableReserva.PAGO.label + ", "
                            + TableReserva.ID_UTILIZADOR.label + ", "
                            + TableReserva.ID_ESPETACULO.label
                            + ") VALUES ('"
                            + row.get(0) + "', '"
                            + row.get(1) + "', '"
                            + row.get(2) + "', '"
                            + row.get(3) + "', '"
                            + row.get(4) + "')"
            );
        }

        return counter == records.size();
    }

    private static boolean insertReservaLugarRecords(List<List<String>> records, Connection dbConn) throws SQLException {

        Statement statement = dbConn.createStatement();
        int counter = 0;

        for (var row : records){
            counter += statement.executeUpdate(
                    "INSERT INTO " + DatabaseTableNames.RESERVA_LUGAR.label
                            + " ("
                            + TableReservaLugar.ID_RESERVA.label + ", "
                            + TableReservaLugar.ID_LUGAR.label +
                            ") VALUES ('"
                            + row.get(0) + "', '"
                            + row.get(1) + "')"
            );
        }

        return counter == records.size();
    }
}
