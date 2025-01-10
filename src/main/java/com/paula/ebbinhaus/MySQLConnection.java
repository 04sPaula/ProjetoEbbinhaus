package com.paula.ebbinhaus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/ebbinhaus"; // Substitua "ebbinhaus" pelo nome do seu banco
    private static final String USER = "root"; // Substitua pelo seu usuário do MySQL
    private static final String PASSWORD = ""; // Substitua pela sua senha do MySQL

    // Obtém a conexão com o banco de dados
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Cria as tabelas necessárias, se ainda não existirem
    public static void initializeDatabase() {
        // Primeiro, criamos as tabelas que não têm foreign keys
        String createDisciplinaTable = "CREATE TABLE IF NOT EXISTS Disciplina ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "nome VARCHAR(100) NOT NULL,"
                + "descricao TEXT,"
                + "status ENUM('A_FAZER', 'EM_PROGRESSO', 'EM_PAUSA', 'CONCLUIDO') NOT NULL"
                + ")";

        String createTesteTable = "CREATE TABLE IF NOT EXISTS Teste ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "data DATE NOT NULL"
                + ")";

        // Depois, criamos a tabela Conteudo que depende das anteriores
        String createConteudoTable = "CREATE TABLE IF NOT EXISTS Conteudo ("
        	    + "id INT AUTO_INCREMENT PRIMARY KEY,"
        	    + "idTeste INT,"
        	    + "idDisciplina INT,"
        	    + "nome VARCHAR(100) NOT NULL,"
        	    + "descricao TEXT,"
        	    + "status ENUM('A_FAZER', 'EM_PROGRESSO', 'EM_PAUSA', 'CONCLUIDO') NOT NULL,"
        	    + "dataCriacao DATETIME DEFAULT CURRENT_TIMESTAMP," // Nova coluna
        	    + "FOREIGN KEY (idTeste) REFERENCES Teste(id),"
        	    + "FOREIGN KEY (idDisciplina) REFERENCES Disciplina(id)"
        	    + ")";

        // Por último, criamos a tabela Revisao que depende da tabela Conteudo
        String createRevisaoTable = "CREATE TABLE IF NOT EXISTS Revisao ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "conteudoId INT NOT NULL,"
                + "dataRevisao DATE NOT NULL,"
                + "status ENUM('A_FAZER', 'EM_PROGRESSO', 'EM_PAUSA', 'CONCLUIDO') NOT NULL,"
                + "FOREIGN KEY (conteudoId) REFERENCES Conteudo(id)"
                + ")";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Executando na ordem correta
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

    // Exemplo de método para inserir uma disciplina
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

    // Exemplo de método para buscar conteudo
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

    public static void main(String[] args) {
        initializeDatabase(); // Inicializa o banco e cria as tabelas, se necessário
        MySQLConnection db = new MySQLConnection();
    }
}