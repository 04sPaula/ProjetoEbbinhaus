package com.paula.ebbinhaus;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;
import com.paula.ebbinhaus.Conteudo.Status;

public class TelaNovoTeste {
    private BorderPane root;
    private ListView<Conteudo> listaConteudos;

    public TelaNovoTeste(BorderPane root) {
        this.root = root;
    }

    public void exibir() {
        GridPane form = new GridPane();
        form.setPadding(new Insets(10));
        form.setHgap(10);
        form.setVgap(10);

        // Título
        Text title = new Text("Novo Teste");

        // DatePicker para selecionar a data
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());

        // ListView para mostrar e selecionar conteúdos
        listaConteudos = new ListView<>();
        listaConteudos.setPrefHeight(200);
        listaConteudos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        carregarConteudos();

        // Botões
        Button btnSalvar = new Button("Salvar");
        btnSalvar.setOnAction(e -> salvarTeste(datePicker.getValue(), 
            listaConteudos.getSelectionModel().getSelectedItems()));

        Button btnVoltar = new Button("Voltar");
        btnVoltar.setOnAction(e -> new TelaInicial(root).exibir());

        // Montando o formulário
        form.add(title, 0, 0, 2, 1);
        form.add(new Text("Data:"), 0, 1);
        form.add(datePicker, 1, 1);
        form.add(new Text("Conteúdos:"), 0, 2);
        form.add(listaConteudos, 1, 2);
        form.add(btnVoltar, 0, 3);
        form.add(btnSalvar, 1, 3);

        root.setCenter(form);
    }

    private void carregarConteudos() {
        ObservableList<Conteudo> conteudos = FXCollections.observableArrayList();
        String sql = "SELECT id, nome, descricao, status FROM Conteudo";
        
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String descricao = rs.getString("descricao");
                Status status = Status.valueOf(rs.getString("status"));
                
                Conteudo conteudo = new Conteudo(id, nome, descricao, status);
                conteudos.add(conteudo);
            }
            
            listaConteudos.setItems(conteudos);
            
            // Sobrescrevendo o toString para mostrar informação mais relevante na lista
            listaConteudos.setCellFactory(lv -> new ListCell<Conteudo>() {
                @Override
                protected void updateItem(Conteudo item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getId() + " - " + item.getNome() + " (" + item.getStatus() + ")");
                    }
                }
            });
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void salvarTeste(LocalDate data, ObservableList<Conteudo> conteudosSelecionados) {
        if (data == null || conteudosSelecionados.isEmpty()) {
            showAlert("Erro", "Por favor, selecione a data e pelo menos um conteúdo.");
            return;
        }

        try (Connection conn = MySQLConnection.getConnection()) {
            // Primeiro, insere o teste
            String sqlTeste = "INSERT INTO Teste (data) VALUES (?)";
            int testeId;
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlTeste, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setDate(1, java.sql.Date.valueOf(data));
                stmt.executeUpdate();
                
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        testeId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Falha ao obter ID do teste.");
                    }
                }
            }

            // Depois, atualiza os conteúdos selecionados com o ID do teste
            String sqlUpdateConteudo = "UPDATE Conteudo SET idTeste = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateConteudo)) {
                for (Conteudo conteudo : conteudosSelecionados) {
                    stmt.setInt(1, testeId);
                    stmt.setInt(2, conteudo.getId());
                    stmt.executeUpdate();
                }
            }

            showAlert("Sucesso", "Teste criado com sucesso!");
            new TelaInicial(root).exibir();

        } catch (SQLException e) {
            showAlert("Erro", "Erro ao salvar teste: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}