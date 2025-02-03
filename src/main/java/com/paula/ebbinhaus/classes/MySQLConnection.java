package com.paula.ebbinhaus.classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MySQLConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/ebbinhaus";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Cria as tabelas necessárias, se ainda não existirem
    public static void initializeDatabase() {
        String createDisciplinaTable = "CREATE DATABASE IF NOT EXISTS EBBINHAUS"
        		+ "USE EBBINHAUS"
        		+ "CREATE TABLE IF NOT EXISTS Disciplina ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "nome VARCHAR(100) NOT NULL,"
                + "descricao TEXT,"
                + "status ENUM('A_FAZER', 'EM_PROGRESSO', 'EM_PAUSA', 'CONCLUIDO') NOT NULL"
                + ")";

        String createTesteTable = "CREATE TABLE IF NOT EXISTS Teste ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "data DATE NOT NULL"
                + ")";

                String createConteudoTable = "CREATE TABLE IF NOT EXISTS Conteudo ("
        	    + "id INT AUTO_INCREMENT PRIMARY KEY,"
        	    + "idTeste INT,"
        	    + "idDisciplina INT,"
        	    + "nome VARCHAR(100) NOT NULL,"
        	    + "descricao TEXT,"
        	    + "status ENUM('A_FAZER', 'EM_PROGRESSO', 'EM_PAUSA', 'CONCLUIDO') NOT NULL,"
        	    + "dataCriacao DATETIME DEFAULT CURRENT_TIMESTAMP,"
        	    + "FOREIGN KEY (idTeste) REFERENCES Teste(id),"
        	    + "FOREIGN KEY (idDisciplina) REFERENCES Disciplina(id)"
        	    + ")";

                String createRevisaoTable = "CREATE TABLE IF NOT EXISTS Revisao ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "conteudoId INT NOT NULL,"
                + "dataRevisao DATE NOT NULL,"
                + "status ENUM('A_FAZER', 'CONCLUIDO') NOT NULL,"
                + "FOREIGN KEY (conteudoId) REFERENCES Conteudo(id)"
                + ")";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createDisciplinaTable);
            stmt.execute(createTesteTable);
            stmt.execute(createConteudoTable);
            stmt.execute(createRevisaoTable);
            System.out.println("Tabelas criadas com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao criar as tabelas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void insertConteudo(String nome, String descricao, String status) {
        String sql = "INSERT INTO Conteudo (nome, descricao, status) VALUES (?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, descricao);
            pstmt.setString(3, status);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listConteudos() {
        String sql = "SELECT * FROM Conteudo";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Nome: " + rs.getString("nome"));
                System.out.println("Descrição: " + rs.getString("descricao"));
                System.out.println("Status: " + rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void gerarRevisoesSeNecessario(int conteudoId) {
        try (Connection conn = getConnection()) {
            String verificaRevisoesExistentes = "SELECT COUNT(*) FROM Revisao WHERE conteudoId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(verificaRevisoesExistentes)) {
                stmt.setInt(1, conteudoId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return;
                    }
                }
            }

            String buscaDataCriacao = "SELECT dataCriacao FROM Conteudo WHERE id = ?";
            LocalDateTime dataCriacao = null;
            try (PreparedStatement stmt = conn.prepareStatement(buscaDataCriacao)) {
                stmt.setInt(1, conteudoId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        dataCriacao = rs.getTimestamp("dataCriacao").toLocalDateTime();
                    }
                }
            }

            if (dataCriacao == null) {
                System.err.println("Conteúdo não encontrado.");
                return;
            }

            String insertRevisao = "INSERT INTO Revisao (conteudoId, dataRevisao, status) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertRevisao)) {
                LocalDate[] datasRevisao = {
                    dataCriacao.plusDays(1).toLocalDate(),
                    dataCriacao.plusDays(7).toLocalDate(),
                    dataCriacao.plusDays(16).toLocalDate(),
                    dataCriacao.plusDays(35).toLocalDate(),
                    dataCriacao.plusMonths(2).toLocalDate()
                };

                for (LocalDate dataRevisao : datasRevisao) {
                    stmt.setInt(1, conteudoId);
                    stmt.setDate(2, java.sql.Date.valueOf(dataRevisao));
                    stmt.setString(3, "A_FAZER");
                    stmt.executeUpdate();
                }

                System.out.println("Revisões geradas para o conteúdo ID: " + conteudoId);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gerar revisões: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        initializeDatabase();
        MySQLConnection db = new MySQLConnection();
    }
}