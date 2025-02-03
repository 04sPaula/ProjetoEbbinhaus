package com.paula.ebbinhaus.classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Revisao {
    private int id;
    private int conteudoId;
    private Date dataRevisao;
    private Status status;

    public enum Status{
        A_FAZER, CONCLUIDO
    }

    public Revisao(int id, int conteudoId, Date dataRevisao, Status status) {
        this.id = id;
        this.conteudoId = conteudoId;
        this.dataRevisao = dataRevisao;
        this.status = status;
    }
    
    public static List<Revisao> buscarRevisoesPorConteudo(int conteudoId) throws SQLException {
        MySQLConnection.gerarRevisoesSeNecessario(conteudoId);
        
        List<Revisao> revisoes = new ArrayList<>();
        String sql = "SELECT id, conteudoId, dataRevisao, status FROM Revisao WHERE conteudoId = ?";
        
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, conteudoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    Date dataRevisao = rs.getDate("dataRevisao");
                    Status status = Status.valueOf(rs.getString("status"));
                    
                    Revisao revisao = new Revisao(id, conteudoId, dataRevisao, status);
                    revisoes.add(revisao);
                }
            }
        }       
        return revisoes;
    }

    public void atualizarStatus(Status novoStatus) throws SQLException {
        String sql = "UPDATE Revisao SET status = ? WHERE id = ?";
        
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, novoStatus.toString());
            stmt.setInt(2, this.id);
            stmt.executeUpdate();
            
            this.status = novoStatus;
        }
    }

    // Getters e setters

    public int getId() {
        return id;
    }

    public int getconteudoId() {
        return conteudoId;
    }

    public void setconteudoId(int conteudoId) {
        this.conteudoId = conteudoId;
    }

    public Date getDataRevisao() {
        return dataRevisao;
    }

    public void setDataRevisao(Date dataRevisao) {
        this.dataRevisao = dataRevisao;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
