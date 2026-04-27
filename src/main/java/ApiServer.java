import com.sun.net.httpserver.HttpServer;
import config.ConnectionFactory;
import handler.AlunoHandler;
import handler.PlanoHandler;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.Statement;

// classe principal que sobe o servidor e registra as rotas
public class ApiServer {

    public static void main(String[] args) throws Exception {
        inicializarBanco();

        HttpServer servidor = HttpServer.create(new InetSocketAddress(5290), 0);

        // cada contexto redireciona pro handler correto
        servidor.createContext("/api/plano", new PlanoHandler());
        servidor.createContext("/api/aluno", new AlunoHandler());

        servidor.setExecutor(null);
        servidor.start();

        System.out.println("API rodando em http://localhost:5290");
    }

    // cria as tabelas no banco se ainda nao existirem
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

            System.out.println("banco inicializado (gym.db)");
        }
    }
}
