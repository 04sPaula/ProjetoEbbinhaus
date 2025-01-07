package com.paula.ebbinhaus;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaInicial {
    private BorderPane root;

    public TelaInicial(BorderPane root) {
        this.root = root;
    }

    public void exibir() {
        VBox menu = new VBox(10);
        Button btnNovaTarefa = new Button("Criar Nova Tarefa");
        Button btnListarTarefas = new Button("Listar Tarefas");
        Button btnRevisarConteudo = new Button("Revisar Conteúdo");

        btnNovaTarefa.setOnAction(e -> new TelaNovaTarefa(root).exibir());
        btnListarTarefas.setOnAction(e -> new TelaListarTarefas(root).exibir());

        menu.getChildren().addAll(btnNovaTarefa, btnListarTarefas, btnRevisarConteudo);
        root.setCenter(menu);
    }
}
