package service;

import exception.EntidadeNaoEncontradaException;
import model.Plano;
import repository.PlanoRepository;

import java.sql.SQLException;
import java.util.List;

// camada de regras de negocio do plano
// valida os dados antes de chamar o repositorio
public class PlanoService {

    private final PlanoRepository repositorio = new PlanoRepository();

    public List<Plano> listar() throws SQLException {
        return repositorio.listar();
    }

    // busca pelo id e lanca excecao se nao encontrar
    public Plano buscarPorId(int id) throws SQLException {
        Plano plano = repositorio.buscarPorId(id);
        if (plano == null) {
            throw new EntidadeNaoEncontradaException("Plano com ID " + id + " nao encontrado.");
        }
        return plano;
    }

    // valida os campos obrigatorios antes de salvar
    public Plano criar(Plano plano) throws SQLException {
        if (plano.getNome() == null || plano.getNome().isBlank()) {
            throw new IllegalArgumentException("o nome do plano e obrigatorio.");
        }
        if (plano.getPreco() == null || plano.getPreco() <= 0) {
            throw new IllegalArgumentException("o preco deve ser maior que zero.");
        }
        return repositorio.salvar(plano);
    }

    public Plano atualizar(int id, Plano plano) throws SQLException {
        Plano existente = buscarPorId(id);
        if (plano.getNome() != null && !plano.getNome().isBlank()) existente.setNome(plano.getNome());
        if (plano.getDescricao() != null) existente.setDescricao(plano.getDescricao());
        if (plano.getPreco() != null && plano.getPreco() > 0) existente.setPreco(plano.getPreco());
        return repositorio.atualizar(id, existente);
    }

    public void deletar(int id) throws SQLException {
        buscarPorId(id);
        repositorio.deletar(id);
    }
}
