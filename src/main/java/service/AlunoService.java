package service;

import exception.EntidadeNaoEncontradaException;
import messaging.AlunoProducer;
import model.Aluno;
import model.EnderecoViaCep;
import model.Plano;
import repository.AlunoRepository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlunoService {

    private final AlunoRepository repositorio = new AlunoRepository();
    private final PlanoService planoService = new PlanoService();
    private final ViaCepService viaCepService = new ViaCepService();
    private final AlunoProducer producer = new AlunoProducer();

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

    public Aluno criar(Aluno aluno) throws SQLException {
        if (aluno.getNome() == null || aluno.getNome().isBlank()) {
            throw new IllegalArgumentException("o nome do aluno e obrigatorio.");
        }
        if (aluno.getEmail() == null || aluno.getEmail().isBlank()) {
            throw new IllegalArgumentException("o email e obrigatorio.");
        }
        if (!aluno.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("formato de email invalido.");
        }
        if (aluno.getPlanoId() == null) {
            throw new IllegalArgumentException("o plano e obrigatorio.");
        }
        planoService.buscarPorId(aluno.getPlanoId());

        // se o aluno informou um CEP, consulta o ViaCEP e preenche o endereco automaticamente
        if (aluno.getCep() != null && !aluno.getCep().isBlank()) {
            EnderecoViaCep endereco = viaCepService.buscarEndereco(aluno.getCep());
            aluno.setCep(endereco.getCep());
            aluno.setLogradouro(endereco.getLogradouro());
            aluno.setBairro(endereco.getBairro());
            aluno.setCidade(endereco.getLocalidade());
            aluno.setUf(endereco.getUf());
        }

        Aluno criado = repositorio.salvar(aluno);

        // publica mensagem assincrona na fila do RabbitMQ apos o cadastro
        String mensagem = String.format("Bem-vindo, %s! Seu plano foi ativado com sucesso.", criado.getNome());
        producer.publicar(mensagem);

        return criado;
    }

    public Aluno atualizar(int id, Aluno aluno) throws SQLException {
        buscarPorId(id);
        planoService.buscarPorId(aluno.getPlanoId());

        if (aluno.getCep() != null && !aluno.getCep().isBlank()) {
            EnderecoViaCep endereco = viaCepService.buscarEndereco(aluno.getCep());
            aluno.setCep(endereco.getCep());
            aluno.setLogradouro(endereco.getLogradouro());
            aluno.setBairro(endereco.getBairro());
            aluno.setCidade(endereco.getLocalidade());
            aluno.setUf(endereco.getUf());
        }

        return repositorio.atualizar(id, aluno);
    }

    public void deletar(int id) throws SQLException {
        buscarPorId(id);
        repositorio.deletar(id);
    }

    // calcula o desconto no plano do aluno com base nos meses de frequencia
    public Map<String, Object> calcularDesconto(int alunoId, int meses) throws SQLException {
        if (meses <= 0) {
            throw new IllegalArgumentException("a quantidade de meses deve ser maior que 0.");
        }

        Aluno aluno = buscarPorId(alunoId);
        Plano plano = aluno.getPlano();

        if (plano == null) {
            throw new IllegalArgumentException("o aluno nao possui um plano ativo.");
        }

        double desconto = calcularPercentual(plano.getNome(), meses);
        double valorOriginal = plano.getPreco();
        double valorComDesconto = valorOriginal * (1 - desconto);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("aluno", aluno.getNome());
        resultado.put("plano", plano.getNome());
        resultado.put("meses", meses);
        resultado.put("valorOriginal", valorOriginal);
        resultado.put("valorComDesconto", desconto > 0 ? valorComDesconto : valorOriginal);
        resultado.put("descontoAplicado", desconto > 0 ? (desconto * 100) + "%" : "nenhum desconto aplicado");

        return resultado;
    }

    // VIP:      3+ meses 5%   6+ meses 10%  10+ meses 15%
    // VIP PLUS: 2+ meses 8%   6+ meses 14%  10+ meses 20%
    private double calcularPercentual(String nomeDoPlano, int meses) {
        if (nomeDoPlano == null) return 0;
        String nome = nomeDoPlano.trim().toUpperCase();
        if (nome.equals("VIP")) {
            if (meses >= 10) return 0.15;
            if (meses >= 6)  return 0.10;
            if (meses >= 3)  return 0.05;
        } else if (nome.equals("VIP PLUS")) {
            if (meses >= 10) return 0.20;
            if (meses >= 6)  return 0.14;
            if (meses >= 2)  return 0.08;
        }
        return 0;
    }
}
