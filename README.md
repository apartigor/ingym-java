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

## Arquitetura

```
Handler (HTTP) → Service (regras de negócio) → Repository (JDBC) → SQLite
```

- **Handler**: recebe a requisição HTTP, faz parse do JSON e devolve a resposta
- **Service**: valida os dados e aplica as regras de negócio
- **Repository**: executa as queries SQL via JDBC com PreparedStatement
- **Model**: entidades `Plano` e `Aluno` com encapsulamento completo

## Regras de negócio

### Validações ao cadastrar/alterar Aluno
- `nome`: obrigatório
- `email`: obrigatório, deve ter formato válido (ex: `user@dominio.com`)
- `planoId`: obrigatório, o plano deve existir no banco

### Cálculo de desconto por desempenho
O endpoint `GET /api/aluno/desempenho/{id}/{meses}` calcula o desconto no plano do aluno com base nos meses de frequência. As regras são aplicadas pelo **nome** do plano:

| Nome do plano | Meses | Desconto |
|---------------|-------|----------|
| VIP           | 3+    | 5%       |
| VIP           | 6+    | 10%      |
| VIP           | 10+   | 15%      |
| VIP PLUS      | 2+    | 8%       |
| VIP PLUS      | 6+    | 14%      |
| VIP PLUS      | 10+   | 20%      |
| Demais planos | —     | sem desconto |

---

## Endpoints

### Plano

#### `GET /api/plano/listar`
Lista todos os planos cadastrados.

**Resposta 200:**
```json
[
  {
    "id": 1,
    "nome": "Basico",
    "descricao": "Acesso simples à academia",
    "preco": 99.90,
    "criadoEm": "2024-06-01 10:00:00"
  }
]
```

---

#### `GET /api/plano/buscar/{id}`
Busca um plano pelo ID.

**Resposta 200:**
```json
{
  "id": 2,
  "nome": "VIP",
  "descricao": "Acesso completo com personal",
  "preco": 199.90,
  "criadoEm": "2024-06-01 10:00:00"
}
```

**Resposta 404:**
```json
{ "erro": "Plano com ID 99 nao encontrado." }
```

---

#### `POST /api/plano/cadastrar`
Cadastra um novo plano.

**Body:**
```json
{
  "nome": "VIP PLUS",
  "descricao": "Acesso ilimitado com nutricionista",
  "preco": 349.90
}
```

**Resposta 201:**
```json
{
  "id": 3,
  "nome": "VIP PLUS",
  "descricao": "Acesso ilimitado com nutricionista",
  "preco": 349.90,
  "criadoEm": null
}
```

---

#### `PUT /api/plano/alterar/{id}`
Atualiza os dados de um plano existente.

**Body:**
```json
{
  "nome": "VIP PLUS",
  "descricao": "Acesso ilimitado com nutricionista e fisioterapeuta",
  "preco": 399.90
}
```

**Resposta 200:**
```json
{
  "id": 3,
  "nome": "VIP PLUS",
  "descricao": "Acesso ilimitado com nutricionista e fisioterapeuta",
  "preco": 399.90,
  "criadoEm": null
}
```

---

#### `DELETE /api/plano/deletar/{id}`
Remove um plano pelo ID.

**Resposta 200:**
```json
{ "mensagem": "plano removido com sucesso" }
```

---

### Aluno

#### `GET /api/aluno/listar`
Lista todos os alunos com os dados do plano vinculado.

**Resposta 200:**
```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@email.com",
    "criadoEm": "2024-06-10 14:30:00",
    "planoId": 2,
    "plano": {
      "id": 2,
      "nome": "VIP",
      "descricao": "Acesso completo com personal",
      "preco": 199.90,
      "criadoEm": "2024-06-01 10:00:00"
    }
  }
]
```

---

#### `GET /api/aluno/buscar/{id}`
Busca um aluno pelo ID.

**Resposta 200:**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com",
  "criadoEm": "2024-06-10 14:30:00",
  "planoId": 2,
  "plano": {
    "id": 2,
    "nome": "VIP",
    "descricao": "Acesso completo com personal",
    "preco": 199.90,
    "criadoEm": "2024-06-01 10:00:00"
  }
}
```

---

#### `POST /api/aluno/cadastrar`
Cadastra um novo aluno. O `planoId` deve corresponder a um plano existente.

**Body:**
```json
{
  "nome": "Maria Souza",
  "email": "maria@email.com",
  "planoId": 3
}
```

**Resposta 201:**
```json
{
  "id": 2,
  "nome": "Maria Souza",
  "email": "maria@email.com",
  "criadoEm": null,
  "planoId": 3,
  "plano": null
}
```

**Resposta 400 (email inválido):**
```json
{ "erro": "formato de email invalido." }
```

**Resposta 404 (plano não existe):**
```json
{ "erro": "Plano com ID 3 nao encontrado." }
```

---

#### `PUT /api/aluno/alterar/{id}`
Atualiza os dados de um aluno existente.

**Body:**
```json
{
  "nome": "Maria Souza",
  "email": "maria.souza@email.com",
  "planoId": 2
}
```

**Resposta 200:**
```json
{
  "id": 2,
  "nome": "Maria Souza",
  "email": "maria.souza@email.com",
  "criadoEm": null,
  "planoId": 2,
  "plano": null
}
```

---

#### `DELETE /api/aluno/deletar/{id}`
Remove um aluno pelo ID.

**Resposta 200:**
```json
{ "mensagem": "aluno removido com sucesso" }
```

---

#### `GET /api/aluno/desempenho/{id}/{meses}`
Calcula o desconto no plano do aluno com base nos meses de frequência informados.

**Exemplo:** `GET /api/aluno/desempenho/1/7`

**Resposta 200:**
```json
{
  "aluno": "João Silva",
  "plano": "VIP",
  "meses": 7,
  "valorOriginal": 199.90,
  "valorComDesconto": 179.91,
  "descontoAplicado": "10.0%"
}
```

**Resposta 400 (meses inválido):**
```json
{ "erro": "a quantidade de meses deve ser maior que 0." }
```

---

## Códigos de status HTTP

| Código | Significado |
|--------|-------------|
| 200    | Sucesso |
| 201    | Recurso criado |
| 400    | Dados inválidos |
| 404    | Recurso não encontrado |
| 500    | Erro interno do servidor |
