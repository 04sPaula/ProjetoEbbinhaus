package com.paula.ebbinhaus.classes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Teste {
    private int id;
    private LocalDate data;
    private List<Conteudo> conteudos;

    public Teste(int id, LocalDate data, List<Conteudo> conteudos) {
        this.id = id;
        this.data = data;
        this.conteudos = conteudos;
    }

    public Teste(LocalDate data, List<Conteudo> conteudos) {
        this.data = data;
        this.conteudos = conteudos;
    }

    public boolean deletar() throws SQLException {
        try (Connection conn = MySQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                String sqlDeleteConteudos = "DELETE FROM Conteudo WHERE idTeste = ?";
                try (PreparedStatement stmtConteudos = conn.prepareStatement(sqlDeleteConteudos)) {
                    stmtConteudos.setInt(1, this.id);
                    stmtConteudos.executeUpdate();
                }
                
                String sqlDeleteTeste = "DELETE FROM Teste WHERE id = ?";
                try (PreparedStatement stmtTeste = conn.prepareStatement(sqlDeleteTeste)) {
                    stmtTeste.setInt(1, this.id);
                    int linhasAfetadas = stmtTeste.executeUpdate();
                    
                    if (linhasAfetadas > 0) {
                        conn.commit();
                        return true;
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean atualizarData(LocalDate novaData) throws SQLException {
        String sql = "UPDATE Teste SET data = ? WHERE id = ?";
        
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(novaData));
            stmt.setInt(2, this.id);
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                this.data = novaData;
                return true;
            }
            return false;
        }
    }

    public boolean salvar() throws SQLException {
        if (data == null || conteudos == null || conteudos.isEmpty()) {
            throw new IllegalStateException("Data e conteúdos são obrigatórios");
        }

        try (Connection conn = MySQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                String sqlTeste = "INSERT INTO Teste (data) VALUES (?)";
                try (PreparedStatement stmt = conn.prepareStatement(sqlTeste, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setDate(1, java.sql.Date.valueOf(data));
                    stmt.executeUpdate();
                    
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            this.id = generatedKeys.getInt(1);
                        } else {
                            throw new SQLException("Falha ao obter ID do teste.");
                        }
                    }
                }

                String sqlUpdateConteudo = "UPDATE Conteudo SET idTeste = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateConteudo)) {
                    for (Conteudo conteudo : conteudos) {
                        stmt.setInt(1, this.id);
                        stmt.setInt(2, conteudo.getId());
                        stmt.executeUpdate();
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public List<Conteudo> getConteudos() {
        return conteudos;
    }

    public void setConteudos(List<Conteudo> conteudos) {
        this.conteudos = conteudos;
    }
    
    public String getConteudosAsString() {
        if (conteudos == null || conteudos.isEmpty()) {
            return "";
        }
        return String.join(", ", conteudos.stream()
                                         .map(Conteudo::getNome)
                                         .filter(nome -> nome != null)
                                         .toArray(String[]::new));
    }
}