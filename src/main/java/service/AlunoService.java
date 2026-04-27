package service;

import exception.EntidadeNaoEncontradaException;
import model.Aluno;
import model.Plano;
import repository.AlunoRepository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// camada de regras de negocio do aluno
// tambem contem o calculo de desconto por desempenho
public class AlunoService {

    private final AlunoRepository repositorio = new AlunoRepository();
    private final PlanoService planoService = new PlanoService();

    public List<Aluno> listar() throws SQLException {
        return repositorio.listar();
    }

    public Aluno buscarPorId(int id) throws SQLException {
        Aluno aluno = repositorio.buscarPorId(id);
        if (aluno == null) {
            throw new EntidadeNaoEncontradaException("Aluno com ID " + id + " nao encontrado.");
        }
        return aluno;
    }

    // valida os campos e checa se o plano existe antes de salvar
    public Aluno criar(Aluno aluno) throws SQLException {
        if (aluno.getNome() == null || aluno.getNome().isBlank()) {
            throw new IllegalArgumentException("o nome do aluno e obrigatorio.");
        }
        if (aluno.getEmail() == null || aluno.getEmail().isBlank()) {
            throw new IllegalArgumentException("o email e obrigatorio.");
        }
        if (!aluno.getEmail().contains("@")) {
            throw new IllegalArgumentException("formato de email invalido.");
        }
        if (aluno.getPlanoId() == null) {
            throw new IllegalArgumentException("o plano e obrigatorio.");
        }
        planoService.buscarPorId(aluno.getPlanoId());
        return repositorio.salvar(aluno);
    }

    public Aluno atualizar(int id, Aluno aluno) throws SQLException {
        buscarPorId(id);
        planoService.buscarPorId(aluno.getPlanoId());
        return repositorio.atualizar(id, aluno);
    }

    public void deletar(int id) throws SQLException {
        buscarPorId(id);
        repositorio.deletar(id);
    }

    // calcula o desconto no plano do aluno com base nos meses de frequencia
    public Map<String, Object> calcularDesconto(int alunoId, int meses) throws SQLException {
        if (meses < 0) {
            throw new IllegalArgumentException("a quantidade de meses deve ser maior que 0.");
        }

        Aluno aluno = buscarPorId(alunoId);
        Plano plano = aluno.getPlano();

        double desconto = calcularPercentual(plano.getId(), meses);
        double valorOriginal = plano.getPreco();
        double valorComDesconto = valorOriginal * (1 - desconto);

        // monta o resultado usando Map (estrutura chave valor)
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("aluno", aluno.getNome());
        resultado.put("plano", plano.getNome());
        resultado.put("meses", meses);
        resultado.put("valorOriginal", valorOriginal);
        resultado.put("valorComDesconto", desconto > 0 ? valorComDesconto : valorOriginal);
        resultado.put("descontoAplicado", desconto > 0 ? (desconto * 100) + "%" : "nenhum desconto aplicado");

        return resultado;
    }

    // plano 2 (vip):      3+ meses 5%   6+ meses 10%  10+ meses 15%
    // plano 3 (vip plus): 2+ meses 8%   6+ meses 14%  10+ meses 20%
    private double calcularPercentual(int planoId, int meses) {
        if (planoId == 2) {
            if (meses >= 10) return 0.15;
            if (meses >= 6)  return 0.10;
            if (meses >= 3)  return 0.05;
        } else if (planoId == 3) {
            if (meses >= 10) return 0.20;
            if (meses >= 6)  return 0.14;
            if (meses >= 2)  return 0.08;
        }
        return 0;
    }
}
