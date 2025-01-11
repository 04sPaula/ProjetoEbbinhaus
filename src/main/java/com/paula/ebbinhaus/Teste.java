package com.paula.ebbinhaus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
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