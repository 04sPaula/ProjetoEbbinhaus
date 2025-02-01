package com.paula.ebbinhaus.classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Conteudo {
    private int id;
    private int idTeste;
    private int idDisciplina;
    private String nome;
    private String descricao;
    private Status status;
	private LocalDateTime dataCriacao;

    public enum Status {
        A_FAZER, EM_PROGRESSO, EM_PAUSA, CONCLUIDO
    }

    public Conteudo(int id, String nome, String descricao, Status status, LocalDateTime dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.status = status;
        this.dataCriacao = dataCriacao;
    }

    public Conteudo(int id, String nome, Status status) {
        this.id = id;
        this.nome = nome;
        this.status = status;
        this.descricao = null;
    }

    public boolean deletar() throws SQLException {
        String sql = "DELETE FROM Conteudo WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, this.id);
            int linhasAfetadas = stmt.executeUpdate();
            
            return linhasAfetadas > 0;
        }
    }

    public boolean atualizarStatus(Status novoStatus) throws SQLException {
        String sql = "UPDATE Conteudo SET status = ? WHERE id = ?";
        
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, novoStatus.toString());
            stmt.setInt(2, this.id);
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                this.status = novoStatus;
                return true;
            }
            return false;
        }
    }

    // Getters e setters
    public int getId() {
        return id;
    }

    public int getIdTeste() {
        return idTeste;
    }

    public void setIdTeste(int idTeste) {
        this.idTeste = idTeste;
    }

    public int getIdDisciplina() {
        return idDisciplina;
    }

    public void setIdDisciplina(int idDisciplina) {
        this.idDisciplina = idDisciplina;
    }

    public String getNome() {
        return nome;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}