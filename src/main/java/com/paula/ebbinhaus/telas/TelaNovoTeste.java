package com.paula.ebbinhaus.telas;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import java.sql.SQLException;
import java.time.LocalDate;

import com.paula.ebbinhaus.classes.Conteudo;
import com.paula.ebbinhaus.classes.Teste;

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

    private void carregarConteudos() {
        try {
            listaConteudos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            
            listaConteudos.setItems(Conteudo.carregarConteudosDisponiveis());
            
            listaConteudos.setCellFactory(lv -> new ListCell<Conteudo>() {
                @Override
                protected void updateItem(Conteudo item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNome() + " (" + item.getStatus() + ")");
                    }
                }
            });
            
            listaConteudos.setDisable(false);
            
            listaConteudos.setStyle(
                "-fx-background-radius: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: #ffcbdb;" +
                "-fx-border-width: 2;" +
                "-fx-selection-bar: #ffcbdb;" + 
                "-fx-selection-bar-non-focused: #ffdde7;"
            );
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao carregar conteúdos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void salvarTeste(LocalDate data, ObservableList<Conteudo> conteudosSelecionados) {
        if (data == null || conteudosSelecionados.isEmpty()) {
            showAlert("Erro", "Por favor, selecione a data e pelo menos um conteúdo.");
            return;
        }

        try {
            Teste novoTeste = new Teste(data, conteudosSelecionados.stream().toList());
            if (novoTeste.salvar()) {
                showAlert("Sucesso", "Teste criado com sucesso!");
                new TelaInicial(root).exibir();
            }
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao salvar teste: " + e.getMessage());
            e.printStackTrace();
        }
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}