--
-- File generated with SQLiteStudio v3.4.0 on qua nov 30 15:06:29 2022
--
-- Text encoding used: System
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Table: database_version
CREATE TABLE IF NOT EXISTS database_version (version INTEGER PRIMARY KEY UNIQUE);
INSERT INTO database_version (version) VALUES (1);

-- Table: espetaculo
CREATE TABLE IF NOT EXISTS espetaculo (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, descricao TEXT NOT NULL, tipo TEXT NOT NULL, data_hora TEXT NOT NULL, duracao INTEGER NOT NULL, local TEXT NOT NULL, localidade TEXT NOT NULL, pais TEXT NOT NULL, classificacao_etaria TEXT NOT NULL, visivel INTEGER NOT NULL DEFAULT (0));

-- Table: lugar
CREATE TABLE IF NOT EXISTS lugar (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, fila TEXT NOT NULL, assento TEXT NOT NULL, preco REAL NOT NULL, espetaculo_id INTEGER REFERENCES espetaculo (id) NOT NULL);

-- Table: reserva
CREATE TABLE IF NOT EXISTS reserva (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, data_hora TEXT NOT NULL, pago INTEGER NOT NULL DEFAULT (0), id_utilizador INTEGER REFERENCES utilizador (id) NOT NULL, id_espetaculo INTEGER REFERENCES espetaculo (id) NOT NULL);

-- Table: reserva_lugar
CREATE TABLE IF NOT EXISTS reserva_lugar (id_reserva INTEGER REFERENCES reserva (id) NOT NULL, id_lugar INTEGER REFERENCES lugar (id) NOT NULL, PRIMARY KEY (id_reserva, id_lugar));

-- Table: utilizador
CREATE TABLE IF NOT EXISTS utilizador (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, username TEXT UNIQUE NOT NULL, nome TEXT UNIQUE NOT NULL, password TEXT NOT NULL, administrador INTEGER NOT NULL DEFAULT (0), autenticado INTEGER NOT NULL DEFAULT (0));
INSERT INTO utilizador (id, username, nome, password, administrador, autenticado) VALUES (1, 'admin', 'admin', 'admin', 1, 0);

COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
