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
