# Alunos:
- Bruno Rafael Hunoff de Freitas
- Igor Felipe Viana
- Nicolas Czekoski Yasumoto

# InGym API

API REST para gerenciamento de academia desenvolvida em Java puro, sem frameworks.

## Tecnologias

- Java 21
- Maven
- Jackson 2.16 (serialização/desserialização JSON)
- SQLite via JDBC (banco de dados embutido)

## Como rodar

```bash
mvn package -DskipTests
java -jar target/ingymAPI-1.0-jar-with-dependencies.jar
```

A API sobe em `http://localhost:5290`.  
O banco `gym.db` é criado automaticamente na primeira execução.

## Dependências externas

- **RabbitMQ** — necessário para mensageria. Subir com Docker:
  ```bash
  docker run -d --hostname rabbit --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
  ```

## Endpoints

### Planos

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/plano/listar` | Lista todos os planos |
| GET | `/api/plano/buscar/{id}` | Busca plano por ID |
| POST | `/api/plano/cadastrar` | Cria um novo plano |
| PUT | `/api/plano/alterar/{id}` | Atualiza um plano |
| DELETE | `/api/plano/deletar/{id}` | Remove um plano |

**Body POST/PUT:**
```json
{
  "nome": "VIP",
  "descricao": "Acesso completo",
  "preco": 99.90
}
```

---

### Alunos

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/aluno/listar` | Lista todos os alunos |
| GET | `/api/aluno/buscar/{id}` | Busca aluno por ID |
| POST | `/api/aluno/cadastrar` | Cria um aluno (consulta ViaCEP se CEP informado e publica mensagem no RabbitMQ) |
| PUT | `/api/aluno/alterar/{id}` | Atualiza um aluno |
| DELETE | `/api/aluno/deletar/{id}` | Remove um aluno |
| GET | `/api/aluno/desempenho/{id}/{meses}` | Calcula desconto no plano por meses de frequência |

**Body POST/PUT:**
```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "planoId": 1,
  "cep": "01310100"
}
```
> O campo `cep` é opcional. Quando informado, `logradouro`, `bairro`, `cidade` e `uf` são preenchidos automaticamente via ViaCEP.

**Exemplo de resposta GET aluno:**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com",
  "criadoEm": "2026-06-02 10:00:00",
  "cep": "01310-100",
  "logradouro": "Avenida Paulista",
  "bairro": "Bela Vista",
  "cidade": "São Paulo",
  "uf": "SP",
  "planoId": 1,
  "plano": {
    "id": 1,
    "nome": "VIP",
    "descricao": "Acesso completo",
    "preco": 99.90
  }
}
```

**Exemplo de resposta desempenho:**
```json
{
  "aluno": "João Silva",
  "plano": "VIP",
  "meses": 6,
  "valorOriginal": 99.90,
  "valorComDesconto": 89.91,
  "descontoAplicado": "10.0%"
}
```

**Regras de desconto por frequência:**

| Plano | 2+ meses | 3+ meses | 6+ meses | 10+ meses |
|-------|----------|----------|----------|-----------|
| VIP | — | 5% | 10% | 15% |
| VIP PLUS | 8% | — | 14% | 20% |
