package handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.EntidadeNaoEncontradaException;
import model.Plano;
import service.PlanoService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

// recebe as requisicoes http de /api/plano e chama o service adequado
public class PlanoHandler implements HttpHandler {

    private final ObjectMapper mapper = new ObjectMapper();
    private final PlanoService service = new PlanoService();

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
            if (metodo.equals("GET") && caminho.contains("/listar")) {
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
        List<Plano> planos = service.listar();
        if (planos.isEmpty()) {
            enviar(troca, 404, "{\"mensagem\": \"nenhum plano encontrado\"}");
            return;
        }
        enviar(troca, 200, mapper.writeValueAsString(planos));
    }

    private void buscarPorId(HttpExchange troca, int id) throws Exception {
        Plano plano = service.buscarPorId(id);
        enviar(troca, 200, mapper.writeValueAsString(plano));
    }

    // le o corpo da requisicao e converte de json pra objeto Plano
    private void cadastrar(HttpExchange troca) throws Exception {
        byte[] corpo = troca.getRequestBody().readAllBytes();
        Plano plano = mapper.readValue(corpo, Plano.class);
        Plano criado = service.criar(plano);
        enviar(troca, 201, mapper.writeValueAsString(criado));
    }

    private void alterar(HttpExchange troca, int id) throws Exception {
        byte[] corpo = troca.getRequestBody().readAllBytes();
        Plano plano = mapper.readValue(corpo, Plano.class);
        Plano atualizado = service.atualizar(id, plano);
        enviar(troca, 200, mapper.writeValueAsString(atualizado));
    }

    private void deletar(HttpExchange troca, int id) throws Exception {
        service.deletar(id);
        enviar(troca, 200, "{\"mensagem\": \"plano removido com sucesso\"}");
    }

    // monta e envia a resposta http com o status e corpo informados
    private void enviar(HttpExchange troca, int status, String corpo) throws IOException {
        troca.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = corpo.getBytes(StandardCharsets.UTF_8);
        troca.sendResponseHeaders(status, bytes.length);
        OutputStream saida = troca.getResponseBody();
        saida.write(bytes);
        saida.close();
    }
}
