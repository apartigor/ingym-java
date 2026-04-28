-- Script de criacao do banco de dados InGym
-- SQLite

CREATE TABLE IF NOT EXISTS planos (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    nome      TEXT    NOT NULL,
    descricao TEXT,
    preco     REAL    NOT NULL,
    criado_em TEXT    DEFAULT (datetime('now', 'localtime'))
);

CREATE TABLE IF NOT EXISTS alunos (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    nome      TEXT    NOT NULL,
    email     TEXT    NOT NULL,
    plano_id  INTEGER,
    criado_em TEXT    DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (plano_id) REFERENCES planos(id)
);
