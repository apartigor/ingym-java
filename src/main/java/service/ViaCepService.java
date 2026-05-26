package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.EnderecoViaCep;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// consulta o endereco de um CEP na API publica ViaCEP
public class ViaCepService {

    private static final String URL_BASE = "https://viacep.com.br/ws/%s/json/";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public EnderecoViaCep buscarEndereco(String cep) {
        String cepLimpo = cep.replaceAll("[^0-9]", "");
        if (cepLimpo.length() != 8) {
            throw new IllegalArgumentException("CEP invalido: deve conter 8 digitos.");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(URL_BASE, cepLimpo)))
                .GET()
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            EnderecoViaCep endereco = mapper.readValue(response.body(), EnderecoViaCep.class);

            if (Boolean.TRUE.equals(endereco.getErro())) {
                throw new IllegalArgumentException("CEP nao encontrado: " + cep);
            }
            return endereco;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar ViaCEP: " + e.getMessage());
        }
    }
}
