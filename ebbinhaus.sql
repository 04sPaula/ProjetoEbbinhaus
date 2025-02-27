CREATE DATABASE IF NOT EXISTS EBBINHAUS;
USE EBBINHAUS;

CREATE TABLE IF NOT EXISTS Disciplina (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    status ENUM('A_FAZER', 'EM_PROGRESSO', 'EM_PAUSA', 'CONCLUIDO') NOT NULL
);

CREATE TABLE IF NOT EXISTS Teste (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS Conteudo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    idTeste INT,
    idDisciplina INT,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    status ENUM('A_FAZER', 'EM_PROGRESSO', 'EM_PAUSA', 'CONCLUIDO') NOT NULL,
    dataCriacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idTeste) REFERENCES Teste(id),
    FOREIGN KEY (idDisciplina) REFERENCES Disciplina(id)
);

CREATE TABLE IF NOT EXISTS Revisao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    conteudoId INT NOT NULL,
    dataRevisao DATE NOT NULL,
    status ENUM('A_FAZER', 'CONCLUIDO') NOT NULL,
    FOREIGN KEY (conteudoId) REFERENCES Conteudo(id)
);

