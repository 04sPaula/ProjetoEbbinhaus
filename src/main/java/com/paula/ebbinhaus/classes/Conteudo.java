package com.paula.ebbinhaus.classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

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
    
    public static boolean addConteudo(String nome, String descricao, String status) throws SQLException {
        try {
            MySQLConnection db = new MySQLConnection();
            db.insertConteudo(nome, descricao, status);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso!");
            alert.setContentText("Conteúdo criado com sucesso!");
            alert.showAndWait();
            
            return true;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Erro ao salvar Conteúdo: " + e.getMessage());
            alert.showAndWait();
            
            return false;
        }
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
    
    public static void atualizarStatusConteudosTestesVencidos() {
        String sql = """
            UPDATE Conteudo c
            JOIN Teste t ON c.idTeste = t.id
            SET c.status = 'CONCLUIDO'
            WHERE t.data < CURDATE() 
            AND c.status != 'CONCLUIDO'
        """;
        
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int conteudosAtualizados = stmt.executeUpdate();
            if (conteudosAtualizados > 0) {
                System.out.println(conteudosAtualizados + " conteúdo(s) foram marcados como concluídos devido a testes passados.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status dos conteúdos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static ObservableList<Conteudo> carregarConteudosDisponiveis() throws SQLException {
        ObservableList<Conteudo> conteudos = FXCollections.observableArrayList();
        String sql = "SELECT id, nome, descricao, status, dataCriacao FROM Conteudo";
        
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String descricao = rs.getString("descricao");
                Status status = Status.valueOf(rs.getString("status"));
                LocalDateTime dataCriacao = rs.getTimestamp("dataCriacao").toLocalDateTime();
                
                Conteudo conteudo = new Conteudo(id, nome, descricao, status, dataCriacao);
                conteudos.add(conteudo);
            }
        }
        return conteudos;
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