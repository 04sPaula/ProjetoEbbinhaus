package com.paula.ebbinhaus;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TelaNovaTarefa {
    private BorderPane root;
    private Stage stage;
    private ComboBox<String> comboStatus;

    public TelaNovaTarefa(BorderPane root) {
        this.root = root;
    }
    

    public void exibir() {
        BorderPane novaTarefaPane = new BorderPane();
        GridPane form = new GridPane();
        form.setPadding(new Insets(10));
        form.setHgap(10);
        form.setVgap(10);

        Scene scene = new Scene(new Group(), 800, 600);

        // Título da tela
        Text title = new Text("Nova Tarefa");

        // Campos para nome e descrição
        TextField txtNome = new TextField();
        TextField txtDescricao = new TextField();

        // ComboBox para selecionar o status
        comboStatus = new ComboBox<>();
        comboStatus.getItems().addAll("A_FAZER", "EM_PROGRESSO", "EM_PAUSA", "CONCLUIDO");

        VBox layout = new VBox(10);
        layout.getChildren().add(comboStatus);

        // Botão para salvar a tarefa
        Button btnSalvar = new Button("Salvar");
        btnSalvar.setOnAction(e -> salvarTarefa(txtNome.getText(), txtDescricao.getText(), comboStatus.getValue()));

        // Botão para voltar à tela inicial
        Button btnVoltar = new Button("Voltar");
        btnVoltar.setOnAction(e -> new TelaInicial(root).exibir());

        // Adiciona os elementos ao formulário
        form.add(title, 0, 0, 2, 1);
        form.add(new Text("Nome:"), 0, 1);
        form.add(txtNome, 1, 1);
        form.add(new Text("Descrição:"), 0, 2);
        form.add(txtDescricao, 1, 2);
        form.add(new Text("Status:"), 0, 3);
        form.add(comboStatus, 1, 3);
        form.add(btnSalvar, 1, 4);
        form.add(btnVoltar, 0, 4);

        // Define o formulário como centro do layout
        root.setCenter(form);

    }

    private void salvarTarefa(String nome, String descricao, String status) {
        if (nome == null || nome.isEmpty() || status == null) {
            System.out.println("Preencha todos os campos obrigatórios!");
            return;
        }

        MySQLConnection db = new MySQLConnection();
        db.insertDisciplina(nome, descricao, status); // Insere a tarefa no banco
        System.out.println("INSERT INTO tabela (nome, descricao, status) VALUES ('" + nome + "', '" + descricao + "', '" + status + "');");
        System.out.println("Status selecionado: " + comboStatus.getValue());
    }
}
