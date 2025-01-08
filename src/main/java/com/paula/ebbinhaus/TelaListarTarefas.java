package com.paula.ebbinhaus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.paula.ebbinhaus.Conteudo.Status;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

public class TelaListarTarefas {
    private BorderPane root;

    public TelaListarTarefas(BorderPane root) {
        this.root = root;
    }

    public void exibir() {
        TableView<Conteudo> tabela = new TableView<>();
        
        TableColumn<Conteudo, Integer> colunaId = new TableColumn<>("ID");
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Conteudo, String> colunaNome = new TableColumn<>("Nome");
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        
        TableColumn<Conteudo, String> colunaDescricao = new TableColumn<>("Descrição");
        colunaDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        
        TableColumn<Conteudo, Status> colunaStatus = new TableColumn<>("Status");
        colunaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        tabela.getColumns().addAll(colunaId, colunaNome, colunaDescricao, colunaStatus);
        tabela.setItems(carregarConteudos());
        root.setCenter(tabela);
    }

    private ObservableList<Conteudo> carregarConteudos() {
        ObservableList<Conteudo> conteudos = FXCollections.observableArrayList();
        String sql = "SELECT id, nome, descricao, status FROM Conteudo";
        try (Connection conn = MySQLConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String descricao = rs.getString("descricao");
                String statusStr = rs.getString("status");
                Status status = Status.valueOf(statusStr);
                
                conteudos.add(new Conteudo(id, nome, descricao, status));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Para debug; substitua por um logger em produção.
        }
        return conteudos;
    }
}
