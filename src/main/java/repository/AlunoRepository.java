package repository;

import config.ConnectionFactory;
import exception.EntidadeNaoEncontradaException;
import model.Aluno;
import model.Plano;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoRepository {

    public List<Aluno> listar() throws SQLException {
        List<Aluno> alunos = new ArrayList<>();

        String sql = """
            SELECT a.id, a.nome, a.email, a.criado_em, a.plano_id,
                   a.cep, a.logradouro, a.bairro, a.cidade, a.uf,
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

    public Aluno buscarPorId(int id) throws SQLException {
        String sql = """
            SELECT a.id, a.nome, a.email, a.criado_em, a.plano_id,
                   a.cep, a.logradouro, a.bairro, a.cidade, a.uf,
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

    public Aluno salvar(Aluno aluno) throws SQLException {
        String sql = """
            INSERT INTO alunos (nome, email, plano_id, cep, logradouro, bairro, cidade, uf)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getEmail());
            stmt.setInt(3, aluno.getPlanoId());
            stmt.setString(4, aluno.getCep());
            stmt.setString(5, aluno.getLogradouro());
            stmt.setString(6, aluno.getBairro());
            stmt.setString(7, aluno.getCidade());
            stmt.setString(8, aluno.getUf());
            stmt.executeUpdate();

            ResultSet chaves = stmt.getGeneratedKeys();
            if (chaves.next()) {
                return buscarPorId(chaves.getInt(1));
            }
        }
        return aluno;
    }

    public Aluno atualizar(int id, Aluno aluno) throws SQLException {
        String sql = """
            UPDATE alunos SET nome = ?, email = ?, plano_id = ?,
                              cep = ?, logradouro = ?, bairro = ?, cidade = ?, uf = ?
            WHERE id = ?
            """;

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getEmail());
            stmt.setInt(3, aluno.getPlanoId());
            stmt.setString(4, aluno.getCep());
            stmt.setString(5, aluno.getLogradouro());
            stmt.setString(6, aluno.getBairro());
            stmt.setString(7, aluno.getCidade());
            stmt.setString(8, aluno.getUf());
            stmt.setInt(9, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new EntidadeNaoEncontradaException("Aluno com ID " + id + " nao encontrado.");
            }
        }
        aluno.setId(id);
        return aluno;
    }

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

    private Aluno mapearAluno(ResultSet rs) throws SQLException {
        int planoId = rs.getInt("plano_id");
        boolean semPlano = rs.wasNull();
        Plano plano = null;
        if (!semPlano) {
            plano = new Plano(
                planoId,
                rs.getString("plano_nome"),
                rs.getString("descricao"),
                rs.getDouble("preco"),
                rs.getString("plano_criado_em")
            );
        }

        return new Aluno(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("email"),
            rs.getString("criado_em"),
            semPlano ? null : planoId,
            plano,
            rs.getString("cep"),
            rs.getString("logradouro"),
            rs.getString("bairro"),
            rs.getString("cidade"),
            rs.getString("uf")
        );
    }
}
