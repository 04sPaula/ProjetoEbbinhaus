package com.paula.ebbinhaus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

public class TelaNovaTarefa {
    private BorderPane root;
    private ComboBox<String> comboStatus;

    public TelaNovaTarefa(BorderPane root) {
        this.root = root;
    }
    
    public void exibir() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);

        // Title
        Label titulo = new Label("Nova Tarefa");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        
        // Form container
        VBox form = new VBox(15);
        form.setMaxWidth(500);
        form.setAlignment(Pos.CENTER);

        // Form fields
        TextField txtNome = createStyledTextField("Nome da Tarefa");
        TextArea txtDescricao = createStyledTextArea("Descrição");
        
        comboStatus = new ComboBox<>();
        comboStatus.getItems().addAll("A_FAZER", "EM_PROGRESSO", "EM_PAUSA", "CONCLUIDO");
        comboStatus.setPromptText("Selecione o Status");
        styleComboBox(comboStatus);

        // Buttons
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        
        Button btnVoltar = createStyledButton("Voltar", false);
        Button btnSalvar = createStyledButton("Salvar", true);
        
        btnVoltar.setOnAction(e -> new TelaInicial(root).exibir());
        btnSalvar.setOnAction(e -> salvarTarefa(txtNome.getText(), txtDescricao.getText(), comboStatus.getValue()));
        
        buttonContainer.getChildren().addAll(btnVoltar, btnSalvar);

        form.getChildren().addAll(
            createFieldLabel("Nome:"),
            txtNome,
            createFieldLabel("Descrição:"),
            txtDescricao,
            createFieldLabel("Status:"),
            comboStatus,
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

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setMaxWidth(500);
        field.setPrefHeight(40);
        field.setStyle(
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;" +
            "-fx-border-color: #ffcbdb;" +
            "-fx-border-width: 2;" +
            "-fx-padding: 5;"
        );
        return field;
    }

    private TextArea createStyledTextArea(String prompt) {
        TextArea area = new TextArea();
        area.setPromptText(prompt);
        area.setMaxWidth(500);
        area.setPrefRowCount(3);
        area.setWrapText(true);
        area.setStyle(
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;" +
            "-fx-border-color: #ffcbdb;" +
            "-fx-border-width: 2;" +
            "-fx-padding: 5;"
        );
        return area;
    }

    private void styleComboBox(ComboBox<?> combo) {
        combo.setMaxWidth(500);
        combo.setPrefHeight(40);
        combo.setStyle(
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;" +
            "-fx-border-color: #ffcbdb;" +
            "-fx-border-width: 2;" +
            "-fx-padding: 5;"
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

    private void salvarTarefa(String nome, String descricao, String status) {
        if (nome == null || nome.isEmpty() || status == null) {
            showAlert("Erro", "Preencha todos os campos obrigatórios!");
            return;
        }

        try {
            MySQLConnection db = new MySQLConnection();
            db.insertConteudo(nome, descricao, status);
            showAlert("Sucesso", "Tarefa criada com sucesso!");
            new TelaInicial(root).exibir();
        } catch (Exception e) {
            showAlert("Erro", "Erro ao salvar tarefa: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}