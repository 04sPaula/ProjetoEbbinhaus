package com.paula.ebbinhaus;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
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
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);

        // Title
        Label titulo = new Label("Novo Teste");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        
        // Form container
        VBox form = new VBox(15);
        form.setMaxWidth(600);
        form.setAlignment(Pos.CENTER);

        // DatePicker
        DatePicker datePicker = createStyledDatePicker();
        datePicker.setValue(LocalDate.now());

        // ListView
        listaConteudos = new ListView<>();
        listaConteudos.setPrefHeight(300);
        listaConteudos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        styleListView(listaConteudos);
        carregarConteudos();

        // Buttons
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        
        Button btnVoltar = createStyledButton("Voltar", false);
        Button btnSalvar = createStyledButton("Salvar", true);
        
        btnVoltar.setOnAction(e -> new TelaInicial(root).exibir());
        btnSalvar.setOnAction(e -> salvarTeste(datePicker.getValue(), 
            listaConteudos.getSelectionModel().getSelectedItems()));
        
        buttonContainer.getChildren().addAll(btnVoltar, btnSalvar);

        form.getChildren().addAll(
            createFieldLabel("Data do Teste:"),
            datePicker,
            createFieldLabel("Selecione os Conteúdos:"),
            listaConteudos,
            buttonContainer
        );

        container.getChildren().addAll(titulo, form);
        root.setCenter(container);
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 14));
        return label;
    }

    private DatePicker createStyledDatePicker() {
        DatePicker picker = new DatePicker();
        picker.setPrefHeight(40);
        picker.setMaxWidth(600);
        picker.setStyle(
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;" +
            "-fx-border-color: #ffcbdb;" +
            "-fx-border-width: 2;"
        );
        return picker;
    }

    private void styleListView(ListView<?> listView) {
        listView.setStyle(
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;" +
            "-fx-border-color: #ffcbdb;" +
            "-fx-border-width: 2;"
        );
    }

    private Button createStyledButton(String text, boolean isPrimary) {
        Button button = new Button(text);
        button.setMinWidth(120);
        button.setMinHeight(40);
        
        String baseStyle = isPrimary ? 
            "-fx-background-color: #ffcbdb;" :
            "-fx-background-color: white;" +
            "-fx-border-color: #ffcbdb;" +
            "-fx-border-width: 2;";
            
        button.setStyle(
            baseStyle +
            "-fx-text-fill: black;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;" +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: #ff709b;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-cursor: hand;"
            )
        );
        
        button.setOnMouseExited(e -> button.setStyle(baseStyle +
            "-fx-text-fill: black;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;" +
            "-fx-cursor: hand;"
        ));
        
        return button;
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