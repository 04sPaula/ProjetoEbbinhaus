package com.paula.ebbinhaus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TelaDetalhesConteudo {
    private BorderPane root;
    private Conteudo conteudo;

    public TelaDetalhesConteudo(BorderPane root, Conteudo conteudo) {
        this.root = root;
        this.conteudo = conteudo;
    }

    public void exibir() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);

        Label titulo = new Label("Detalhes da Tarefa");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));

        VBox infoContainer = new VBox(10);
        infoContainer.setStyle(
            "-fx-background-color: white;" +
            "-fx-padding: 20;" +
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;" +
            "-fx-border-color: #ffcbdb;" +
            "-fx-border-width: 2;"
        );

        Label lblNome = new Label("Nome: " + conteudo.getNome());
        Label lblDescricao = new Label("Descrição: " + conteudo.getDescricao());
        Label lblStatus = new Label("Status: " + conteudo.getStatus());
        Label lblDataCriacao = new Label("Data de Criação: " + 
            conteudo.getDataCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        Label lblRevisoes = new Label("Datas de Revisão Recomendadas:");
        lblRevisoes.setFont(Font.font("System", FontWeight.BOLD, 14));

        VBox revisoesContainer = new VBox(5);
        LocalDateTime dataCriacao = conteudo.getDataCriacao();
        
        String[] revisoes = {
            dataCriacao.plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            dataCriacao.plusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            dataCriacao.plusDays(16).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            dataCriacao.plusDays(35).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            dataCriacao.plusMonths(2).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        };

        for (int i = 0; i < revisoes.length; i++) {
            Label lblRevisao = new Label((i + 1) + "ª Revisão: " + revisoes[i]);
            revisoesContainer.getChildren().add(lblRevisao);
        }

        infoContainer.getChildren().addAll(
            lblNome, lblDescricao, lblStatus, lblDataCriacao,
            new Separator(), lblRevisoes, revisoesContainer
        );

        Button btnVoltar = new Button("Voltar");
        btnVoltar.setStyle(
            "-fx-background-color: #ffcbdb;" +
            "-fx-text-fill: black;" +
            "-fx-font-size: 14px;" +
            "-fx-min-width: 120;" +
            "-fx-min-height: 40;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        btnVoltar.setOnAction(e -> new TelaListarTarefas(root).exibir());

        container.getChildren().addAll(titulo, infoContainer, btnVoltar);
        root.setCenter(container);
    }
}