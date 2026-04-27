# InGym API

API REST para gerenciamento de academia desenvolvida em Java.

## Tecnologias
- Java 21
- Maven
- Jackson (JSON)
- SQLite via JDBC

## Como rodar

```bash
mvn package -DskipTests
java -jar target/ingymAPI-1.0-jar-with-dependencies.jar
```

A API sobe em `http://localhost:5290`.

## Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/plano/listar` | Lista todos os planos |
| GET | `/api/plano/buscar/{id}` | Busca plano por ID |
| POST | `/api/plano/cadastrar` | Cadastra novo plano |
| PUT | `/api/plano/alterar/{id}` | Atualiza plano |
| DELETE | `/api/plano/deletar/{id}` | Remove plano |
| GET | `/api/aluno/listar` | Lista todos os alunos |
| GET | `/api/aluno/buscar/{id}` | Busca aluno por ID |
| POST | `/api/aluno/cadastrar` | Cadastra novo aluno |
| PUT | `/api/aluno/alterar/{id}` | Atualiza aluno |
| DELETE | `/api/aluno/deletar/{id}` | Remove aluno |
| POST | `/api/aluno/desempenho/{id}/{meses}` | Calcula desconto por desempenho |