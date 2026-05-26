import com.sun.net.httpserver.HttpServer;
import config.ConnectionFactory;
import handler.AlunoHandler;
import handler.PlanoHandler;
import messaging.AlunoConsumer;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.Statement;

public class ApiServer {

    public static void main(String[] args) throws Exception {
        inicializarBanco();
        iniciarConsumer();

        HttpServer servidor = HttpServer.create(new InetSocketAddress(5290), 0);

        servidor.createContext("/api/plano", new PlanoHandler());
        servidor.createContext("/api/aluno", new AlunoHandler());

        servidor.setExecutor(null);
        servidor.start();

        System.out.println("API rodando em http://localhost:5290");
    }

    private static void inicializarBanco() throws Exception {
        try (Connection con = ConnectionFactory.getConnection();
             Statement stmt = con.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS planos (
                    id        INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome      TEXT    NOT NULL,
                    descricao TEXT,
                    preco     REAL    NOT NULL,
                    criado_em TEXT    DEFAULT (datetime('now', 'localtime'))
                )
                """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS alunos (
                    id        INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome      TEXT    NOT NULL,
                    email     TEXT    NOT NULL,
                    plano_id  INTEGER,
                    criado_em TEXT    DEFAULT (datetime('now', 'localtime')),
                    FOREIGN KEY (plano_id) REFERENCES planos(id)
                )
                """);

            for (String coluna : new String[]{
                "ADD COLUMN cep        TEXT",
                "ADD COLUMN logradouro TEXT",
                "ADD COLUMN bairro     TEXT",
                "ADD COLUMN cidade     TEXT",
                "ADD COLUMN uf         TEXT"
            }) {
                try {
                    stmt.execute("ALTER TABLE alunos " + coluna);
                } catch (Exception ignored) {

                }
            }

            System.out.println("banco inicializado (gym.db)");
        }
    }

    // sobe o consumer do RabbitMQ em uma thread separada (nao bloqueia a API)
    private static void iniciarConsumer() {
        Thread consumer = new Thread(new AlunoConsumer());
        consumer.setDaemon(true);
        consumer.start();
    }
}
