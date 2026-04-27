package handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.EntidadeNaoEncontradaException;
import model.Aluno;
import service.AlunoService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

// recebe as requisicoes http de /api/aluno
// inclui a rota de desconto por desempenho alem do crud padrao
public class AlunoHandler implements HttpHandler {

    private final ObjectMapper mapper = new ObjectMapper();
    private final AlunoService service = new AlunoService();

    @Override
    public void handle(HttpExchange troca) throws IOException {
        String metodo = troca.getRequestMethod();
        String caminho = troca.getRequestURI().getPath();
        String[] partes = caminho.split("/");

        troca.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        troca.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        troca.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if (metodo.equals("OPTIONS")) {
            enviar(troca, 204, "");
            return;
        }

        try {
            // rota de desconto: /api/aluno/desempenho/{id}/{meses}
            if (metodo.equals("POST") && caminho.contains("/desempenho/")) {
                int id    = Integer.parseInt(partes[partes.length - 2]);
                int meses = Integer.parseInt(partes[partes.length - 1]);
                calcularDesconto(troca, id, meses);

            } else if (metodo.equals("GET") && caminho.contains("/listar")) {
                listar(troca);

            } else if (metodo.equals("GET") && caminho.contains("/buscar/")) {
                int id = Integer.parseInt(partes[partes.length - 1]);
                buscarPorId(troca, id);

            } else if (metodo.equals("POST") && caminho.contains("/cadastrar")) {
                cadastrar(troca);

            } else if (metodo.equals("PUT") && caminho.contains("/alterar/")) {
                int id = Integer.parseInt(partes[partes.length - 1]);
                alterar(troca, id);

            } else if (metodo.equals("DELETE") && caminho.contains("/deletar/")) {
                int id = Integer.parseInt(partes[partes.length - 1]);
                deletar(troca, id);

            } else {
                enviar(troca, 405, "{\"erro\": \"rota nao encontrada\"}");
            }

        } catch (EntidadeNaoEncontradaException e) {
            enviar(troca, 404, "{\"erro\": \"" + e.getMessage() + "\"}");
        } catch (IllegalArgumentException e) {
            enviar(troca, 400, "{\"erro\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            enviar(troca, 500, "{\"erro\": \"erro interno: " + e.getMessage() + "\"}");
        }
    }

    private void listar(HttpExchange troca) throws Exception {
        List<Aluno> alunos = service.listar();
        if (alunos.isEmpty()) {
            enviar(troca, 404, "{\"mensagem\": \"nenhum aluno encontrado\"}");
            return;
        }
        enviar(troca, 200, mapper.writeValueAsString(alunos));
    }

    private void buscarPorId(HttpExchange troca, int id) throws Exception {
        Aluno aluno = service.buscarPorId(id);
        enviar(troca, 200, mapper.writeValueAsString(aluno));
    }

    private void cadastrar(HttpExchange troca) throws Exception {
        byte[] corpo = troca.getRequestBody().readAllBytes();
        Aluno aluno = mapper.readValue(corpo, Aluno.class);
        Aluno criado = service.criar(aluno);
        enviar(troca, 201, mapper.writeValueAsString(criado));
    }

    private void alterar(HttpExchange troca, int id) throws Exception {
        byte[] corpo = troca.getRequestBody().readAllBytes();
        Aluno aluno = mapper.readValue(corpo, Aluno.class);
        Aluno atualizado = service.atualizar(id, aluno);
        enviar(troca, 200, mapper.writeValueAsString(atualizado));
    }

    private void deletar(HttpExchange troca, int id) throws Exception {
        service.deletar(id);
        enviar(troca, 200, "{\"mensagem\": \"aluno removido com sucesso\"}");
    }

    private void calcularDesconto(HttpExchange troca, int id, int meses) throws Exception {
        Map<String, Object> resultado = service.calcularDesconto(id, meses);
        enviar(troca, 200, mapper.writeValueAsString(resultado));
    }

    private void enviar(HttpExchange troca, int status, String corpo) throws IOException {
        troca.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = corpo.getBytes(StandardCharsets.UTF_8);
        troca.sendResponseHeaders(status, bytes.length);
        OutputStream saida = troca.getResponseBody();
        saida.write(bytes);
        saida.close();
    }
}
