package repository;

import config.ConnectionFactory;
import exception.EntidadeNaoEncontradaException;
import model.Plano;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// faz todas as operacoes no banco relacionadas ao plano
public class PlanoRepository {

    // retorna todos os planos
    public List<Plano> listar() throws SQLException {
        List<Plano> planos = new ArrayList<>();
        String sql = "SELECT * FROM planos";

        try (Connection con = ConnectionFactory.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                planos.add(mapearPlano(rs));
            }
        }
        return planos;
    }

    // busca um plano pelo id, retorna null se nao achar
    public Plano buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM planos WHERE id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearPlano(rs);
            }
            return null;
        }
    }

    // insere um novo plano e retorna com o id gerado
    public Plano salvar(Plano plano) throws SQLException {
        String sql = "INSERT INTO planos (nome, descricao, preco) VALUES (?, ?, ?)";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, plano.getNome());
            stmt.setString(2, plano.getDescricao());
            stmt.setDouble(3, plano.getPreco());
            stmt.executeUpdate();

            ResultSet chaves = stmt.getGeneratedKeys();
            if (chaves.next()) {
                plano.setId(chaves.getInt(1));
            }
        }
        return plano;
    }

    // atualiza os dados de um plano existente
    public Plano atualizar(int id, Plano planoAtualizado) throws SQLException {
        String sql = "UPDATE planos SET nome = ?, descricao = ?, preco = ? WHERE id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, planoAtualizado.getNome());
            stmt.setString(2, planoAtualizado.getDescricao());
            stmt.setDouble(3, planoAtualizado.getPreco());
            stmt.setInt(4, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new EntidadeNaoEncontradaException("Plano com ID " + id + " nao encontrado.");
            }
        }
        planoAtualizado.setId(id);
        return planoAtualizado;
    }

    // remove um plano pelo id
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM planos WHERE id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new EntidadeNaoEncontradaException("Plano com ID " + id + " nao encontrado.");
            }
        }
    }

    // converte uma linha do banco em objeto Plano
    private Plano mapearPlano(ResultSet rs) throws SQLException {
        return new Plano(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("descricao"),
            rs.getDouble("preco"),
            rs.getString("criado_em")
        );
    }
}
