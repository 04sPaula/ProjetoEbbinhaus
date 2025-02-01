package com.paula.ebbinhaus.telas;

import com.paula.ebbinhaus.classes.AtualizadorStatusConteudo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public class TelaInicial {
    private BorderPane root;

    public TelaInicial(BorderPane root) {
        this.root = root;
        AtualizadorStatusConteudo.atualizarStatusConteudosTestesVencidos();
    }

    public void exibir() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);

        Label titulo = new Label("Assistente de Estudos");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        
        Label explicacao = new Label(
            "Baseado no método Ebbinghaus de repetição espaçada, " +
            "este aplicativo ajuda você a organizar seus estudos de forma eficiente. " +
            "Crie tarefas de estudo e agende revisões nos intervalos ideais para " +
            "maximizar sua retenção de conhecimento."
        );
        explicacao.setWrapText(true);
        explicacao.setTextAlignment(TextAlignment.CENTER);
        explicacao.setMaxWidth(500);
        explicacao.setFont(Font.font("System", 14));

        Label secaoTarefas = criarTituloSecao("Gerenciar Tarefas");
        Label secaoTestes = criarTituloSecao("Gerenciar Testes");

        VBox menuTarefas = new VBox(10);
        menuTarefas.setAlignment(Pos.CENTER);
        Button btnNovaTarefa = criarBotao("Criar Nova Tarefa");
        Button btnListarTarefas = criarBotao("Visualizar Tarefas");
        
        btnNovaTarefa.setOnAction(e -> new TelaNovaTarefa(root).exibir());
        btnListarTarefas.setOnAction(e -> new TelaListarTarefas(root).exibir());
        menuTarefas.getChildren().addAll(btnNovaTarefa, btnListarTarefas);

        VBox menuTestes = new VBox(10);
        menuTestes.setAlignment(Pos.CENTER);
        Button btnAgendarTeste = criarBotao("Agendar Teste");
        Button btnListarTestes = criarBotao("Visualizar Testes");
        
        btnAgendarTeste.setOnAction(e -> new TelaNovoTeste(root).exibir());
        btnListarTestes.setOnAction(e -> new TelaListarTestes(root).exibir());
        menuTestes.getChildren().addAll(btnAgendarTeste, btnListarTestes);

        container.getChildren().addAll(
            titulo,
            explicacao,
            secaoTarefas,
            menuTarefas,
            secaoTestes,
            menuTestes
        );

        root.setCenter(container);
    }

    private Label criarTituloSecao(String texto) {
        Label titulo = new Label(texto);
        titulo.setFont(Font.font("System", FontWeight.BOLD, 18));
        titulo.setPadding(new Insets(20, 0, 10, 0));
        return titulo;
    }

    private Button criarBotao(String texto) {
        Button botao = new Button(texto);
        botao.setMinWidth(200);
        botao.setMinHeight(40);
        botao.setStyle(
            "-fx-background-color: #ffcbdb;" +
            "-fx-text-fill: black;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        
        botao.setOnMouseEntered(e -> 
            botao.setStyle(
                "-fx-background-color: #ff709b;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
            )
        );
        
        botao.setOnMouseExited(e -> 
            botao.setStyle(
                "-fx-background-color: #ffcbdb;" +
                "-fx-text-fill: black;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
            )
        );

        return botao;
    }
}
