package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// cria e retorna a conexao com o banco sqlite
public class ConnectionFactory {

    // o arquivo gym.db e criado automaticamente na primeira execucao
    private static final String URL = "jdbc:sqlite:gym.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
