package com.paula.ebbinhaus.telas;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.paula.ebbinhaus.classes.Conteudo;
import com.paula.ebbinhaus.classes.Revisao;
import com.paula.ebbinhaus.classes.Revisao.Status;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TelaDetalhesConteudo {
    private BorderPane root;
    private Conteudo conteudo;
    private List<Revisao> revisoes;

    public TelaDetalhesConteudo(BorderPane root, Conteudo conteudo) {
        this.root = root;
        this.conteudo = conteudo;
        
        try {
            this.revisoes = Revisao.buscarRevisoesPorConteudo(conteudo.getId());
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar revisões", e.getMessage());
            this.revisoes = List.of();
        }
    }

    public void exibir() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);

        Label titulo = new Label("Detalhes do Conteúdo");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));

        VBox infoContainer = new VBox(10);
        infoContainer.setStyle("-fx-background-color: white;" + 
                               "-fx-padding: 20;" + 
                               "-fx-background-radius: 5;" +
                               "-fx-border-radius: 5;" + 
                               "-fx-border-color: #ffcbdb;" + 
                               "-fx-border-width: 2;");

        Label lblNome = new Label("Nome: " + conteudo.getNome());
        Label lblDescricao = new Label("Descrição: " + conteudo.getDescricao());
        Label lblStatus = new Label("Status: " + conteudo.getStatus());
        Label lblDataCriacao = new Label("Data de Criação: " + 
            conteudo.getDataCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        Label lblRevisoes = new Label("Datas de Revisão:");
        lblRevisoes.setFont(Font.font("System", FontWeight.BOLD, 14));

        ListView<HBox> listaRevisoes = new ListView<>();
        listaRevisoes.setStyle("-fx-background-color: white;" + 
                               "-fx-border-color: #ffcbdb;");

        for (Revisao revisao : revisoes) {
            CheckBox checkRevisao = new CheckBox(
                LocalDate.parse(revisao.getDataRevisao().toString()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );
            checkRevisao.setSelected(revisao.getStatus() == Status.CONCLUIDO);
            
            checkRevisao.setOnAction(e -> {
                try {
                    revisao.atualizarStatus(checkRevisao.isSelected() ? Status.CONCLUIDO : Status.A_FAZER);
                } catch (SQLException ex) {
                    mostrarErro("Erro ao atualizar status", ex.getMessage());
                    checkRevisao.setSelected(!checkRevisao.isSelected());
                }
            });

            listaRevisoes.getItems().add(new HBox(10, checkRevisao));
        }

        if (revisoes.isEmpty()) {
            Label lblSemRevisoes = new Label("Nenhuma revisão programada.");
            listaRevisoes.getItems().add(new HBox(10, lblSemRevisoes));
        }

        infoContainer.getChildren().addAll(
            lblNome, lblDescricao, lblStatus, lblDataCriacao, 
            new Separator(), lblRevisoes, listaRevisoes
        );

        Button btnVoltar = new Button("Voltar");
        btnVoltar.setStyle("-fx-background-color: #ffcbdb;" + 
                           "-fx-text-fill: black;" + 
                           "-fx-font-size: 14px;" +
                           "-fx-min-width: 120;" + 
                           "-fx-min-height: 40;" + 
                           "-fx-background-radius: 5;" + 
                           "-fx-cursor: hand;");
        btnVoltar.setOnAction(e -> new TelaListarConteudos(root).exibir());

        container.getChildren().addAll(titulo, infoContainer, btnVoltar);
        root.setCenter(container);
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}