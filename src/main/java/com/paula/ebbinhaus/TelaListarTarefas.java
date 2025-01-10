package com.paula.ebbinhaus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import com.paula.ebbinhaus.Conteudo.Status;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class TelaListarTarefas {
    private BorderPane root;
    private ObservableList<Conteudo> conteudos;

    public TelaListarTarefas(BorderPane root) {
        this.root = root;
    }

    public void exibir() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);

        // Title
        Label titulo = new Label("Lista de Tarefas");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));

        // Table setup
        TableView<Conteudo> tabela = createStyledTableView();
        
        // Columns
        TableColumn<Conteudo, Integer> colunaId = new TableColumn<>("ID");
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Conteudo, String> colunaNome = new TableColumn<>("Nome");
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaNome.setPrefWidth(200);
        
        TableColumn<Conteudo, String> colunaDescricao = new TableColumn<>("Descrição");
        colunaDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colunaDescricao.setPrefWidth(300);
        
        TableColumn<Conteudo, Status> colunaStatus = createStatusColumn();
        TableColumn<Conteudo, Void> colunaAcoes = createActionsColumn();

        tabela.getColumns().addAll(colunaId, colunaNome, colunaDescricao, colunaStatus, colunaAcoes);
        
        conteudos = carregarConteudos();
        tabela.setItems(conteudos);

        // Button container
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        Button btnVoltar = createStyledButton("Voltar", false);
        btnVoltar.setOnAction(e -> new TelaInicial(root).exibir());
        buttonContainer.getChildren().add(btnVoltar);

        container.getChildren().addAll(titulo, tabela, buttonContainer);
        root.setCenter(container);
    }

    private TableView<Conteudo> createStyledTableView() {
        TableView<Conteudo> tabela = new TableView<>();
        tabela.setEditable(true);
        tabela.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;" +
            "-fx-border-color: #ffcbdb;" +
            "-fx-border-width: 2;"
        );
        tabela.setPrefHeight(400);
        return tabela;
    }

    private TableColumn<Conteudo, Status> createStatusColumn() {
        TableColumn<Conteudo, Status> column = new TableColumn<>("Status");
        column.setCellValueFactory(new PropertyValueFactory<>("status"));
        column.setCellFactory(ComboBoxTableCell.forTableColumn(
            FXCollections.observableArrayList(Status.values())
        ));
        column.setOnEditCommit(event -> {
            Conteudo conteudo = event.getRowValue();
            Status novoStatus = event.getNewValue();
            conteudo.setStatus(novoStatus);
            atualizarStatusNoBanco(conteudo.getId(), novoStatus);
        });
        column.setPrefWidth(150);
        return column;
    }

    private TableColumn<Conteudo, Void> createActionsColumn() {
        TableColumn<Conteudo, Void> column = new TableColumn<>("Ações");
        column.setCellFactory(col -> new TableCell<Conteudo, Void>() {
            private final HBox container = new HBox(5);
            private final Button btnDetalhes = createActionButton("Detalhes", "#4CAF50");
            private final Button btnDeletar = createDeleteButton();

            {
                btnDetalhes.setOnAction(event -> {
                    Conteudo conteudo = getTableRow().getItem();
                    if (conteudo != null) {
                        new TelaDetalhesConteudo(root, conteudo).exibir();
                    }
                });
                
                btnDeletar.setOnAction(event -> {
                    Conteudo conteudo = getTableRow().getItem();
                    if (conteudo != null) {
                        confirmarDelecao(conteudo);
                    }
                });
                
                container.getChildren().addAll(btnDetalhes, btnDeletar);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
        return column;
    }

    private Button createDeleteButton() {
        Button btn = new Button("Deletar");
        btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: #ff4444;" +
            "-fx-border-color: #ff4444;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 3;" +
            "-fx-background-radius: 3;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #ff4444;" +
            "-fx-text-fill: white;" +
            "-fx-border-color: #ff4444;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 3;" +
            "-fx-background-radius: 3;" +
            "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: #ff4444;" +
            "-fx-border-color: #ff4444;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 3;" +
            "-fx-background-radius: 3;" +
            "-fx-cursor: hand;"
        ));
        return btn;
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

    private void confirmarDelecao(Conteudo conteudo) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Deletar Tarefa");
        alert.setContentText("Tem certeza que deseja deletar esta tarefa?");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            deletarConteudo(conteudo);
        }
    }

    private void mostrarMensagemSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarMensagemErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    private Button createActionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: " + color + ";" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 3;" +
            "-fx-background-radius: 3;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 3;" +
            "-fx-background-radius: 3;" +
            "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: " + color + ";" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 3;" +
            "-fx-background-radius: 3;" +
            "-fx-cursor: hand;"
        ));
        return btn;
    }

    private void deletarConteudo(Conteudo conteudo) {
        try {
            if (conteudo.deletar()) {
                // Remove o conteúdo da lista observável
                conteudos.remove(conteudo);
                mostrarMensagemSucesso("Tarefa deletada com sucesso!");
            } else {
                mostrarMensagemErro("Não foi possível deletar a tarefa");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarMensagemErro("Erro ao deletar tarefa: " + e.getMessage());
        }
    }

    private void atualizarStatusNoBanco(int id, Status novoStatus) {
        try {
            Conteudo conteudo = conteudos.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
                
            if (conteudo != null && conteudo.atualizarStatus(novoStatus)) {
                mostrarMensagemSucesso("Status atualizado com sucesso!");
            } else {
                mostrarMensagemErro("Não foi possível atualizar o status :(");
            }
        } catch (SQLException e) {
            mostrarMensagemErro("Erro ao atualizar status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ObservableList<Conteudo> carregarConteudos() {
        ObservableList<Conteudo> conteudos = FXCollections.observableArrayList();
        String sql = "SELECT id, nome, descricao, status, dataCriacao FROM Conteudo WHERE idTeste IS NULL";
        
        try (Connection conn = MySQLConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String descricao = rs.getString("descricao");
                String statusStr = rs.getString("status");
                Status status = Status.valueOf(statusStr);
                LocalDateTime dataCriacao = rs.getTimestamp("dataCriacao").toLocalDateTime();
                
                conteudos.add(new Conteudo(id, nome, descricao, status, dataCriacao));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarMensagemErro("Erro ao carregar tarefas: " + e.getMessage());
        }
        return conteudos;
    }
}