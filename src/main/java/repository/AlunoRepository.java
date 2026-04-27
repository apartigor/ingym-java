package repository;

import config.ConnectionFactory;
import exception.EntidadeNaoEncontradaException;
import model.Aluno;
import model.Plano;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// faz todas as operacoes no banco relacionadas ao aluno
public class AlunoRepository {

    // retorna todos os alunos com os dados do plano de cada um
    public List<Aluno> listar() throws SQLException {
        List<Aluno> alunos = new ArrayList<>();

        // join pra trazer os dados do plano junto com o aluno
        String sql = """
            SELECT a.id, a.nome, a.email, a.criado_em, a.plano_id,
                   p.nome AS plano_nome, p.descricao, p.preco, p.criado_em AS plano_criado_em
            FROM alunos a
            LEFT JOIN planos p ON a.plano_id = p.id
            """;

        try (Connection con = ConnectionFactory.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                alunos.add(mapearAluno(rs));
            }
        }
        return alunos;
    }

    // busca um aluno pelo id com os dados do plano
    public Aluno buscarPorId(int id) throws SQLException {
        String sql = """
            SELECT a.id, a.nome, a.email, a.criado_em, a.plano_id,
                   p.nome AS plano_nome, p.descricao, p.preco, p.criado_em AS plano_criado_em
            FROM alunos a
            LEFT JOIN planos p ON a.plano_id = p.id
            WHERE a.id = ?
            """;

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearAluno(rs);
            }
            return null;
        }
    }

    // insere um novo aluno no banco
    public Aluno salvar(Aluno aluno) throws SQLException {
        String sql = "INSERT INTO alunos (nome, email, plano_id) VALUES (?, ?, ?)";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getEmail());
            stmt.setInt(3, aluno.getPlanoId());
            stmt.executeUpdate();

            ResultSet chaves = stmt.getGeneratedKeys();
            if (chaves.next()) {
                aluno.setId(chaves.getInt(1));
            }
        }
        return aluno;
    }

    // atualiza os dados de um aluno existente
    public Aluno atualizar(int id, Aluno alunoAtualizado) throws SQLException {
        String sql = "UPDATE alunos SET nome = ?, email = ?, plano_id = ? WHERE id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, alunoAtualizado.getNome());
            stmt.setString(2, alunoAtualizado.getEmail());
            stmt.setInt(3, alunoAtualizado.getPlanoId());
            stmt.setInt(4, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new EntidadeNaoEncontradaException("Aluno com ID " + id + " nao encontrado.");
            }
        }
        alunoAtualizado.setId(id);
        return alunoAtualizado;
    }

    // remove um aluno pelo id
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM alunos WHERE id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new EntidadeNaoEncontradaException("Aluno com ID " + id + " nao encontrado.");
            }
        }
    }

    // monta um objeto Aluno com o Plano aninhado a partir do resultado do join
    private Aluno mapearAluno(ResultSet rs) throws SQLException {
        Plano plano = new Plano(
            rs.getInt("plano_id"),
            rs.getString("plano_nome"),
            rs.getString("descricao"),
            rs.getDouble("preco"),
            rs.getString("plano_criado_em")
        );

        return new Aluno(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("email"),
            rs.getString("criado_em"),
            rs.getInt("plano_id"),
            plano
        );
    }
}
