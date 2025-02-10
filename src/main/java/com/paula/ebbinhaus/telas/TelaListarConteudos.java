package com.paula.ebbinhaus.telas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import com.paula.ebbinhaus.classes.Conteudo;
import com.paula.ebbinhaus.classes.MySQLConnection;
import com.paula.ebbinhaus.classes.Status;

import javafx.stage.Stage;

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


public class TelaListarConteudos {
    private BorderPane root;
    private ObservableList<Conteudo> conteudos;
    private ObservableList<Conteudo> todosConteudos;
    private ObservableList<Conteudo> conteudosFiltrados;
    private TableView<Conteudo> tabela;
    private ToggleGroup grupoFiltros;
    
    public TelaListarConteudos(BorderPane root) {
        this.root = root;
    }

    public void exibir() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);

        Label titulo = new Label("Lista de Conteúdos");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));

        HBox filterContainer = new HBox(10);
        filterContainer.setAlignment(Pos.CENTER);
        
        grupoFiltros = new ToggleGroup();
        
        ToggleButton btnAtivos = createFilterToggleButton("Ativos", true);
        ToggleButton btnInativos = createFilterToggleButton("Inativos", false);
        ToggleButton btnTodos = createFilterToggleButton("Todos", false);
        
        btnAtivos.setToggleGroup(grupoFiltros);
        btnInativos.setToggleGroup(grupoFiltros);
        btnTodos.setToggleGroup(grupoFiltros);
        
        btnAtivos.setSelected(true);
        
        filterContainer.getChildren().addAll(btnAtivos, btnInativos, btnTodos);
        
        tabela = createStyledTableView();

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
        
        todosConteudos = carregarConteudos();
        conteudosFiltrados = FXCollections.observableArrayList();
        
        grupoFiltros.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                oldVal.setSelected(true);
                return;
            }
            atualizarFiltro();
        });
        
        atualizarFiltro();
        
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        Button btnVoltar = createStyledButton("Voltar", false);
        btnVoltar.setOnAction(e -> new TelaInicial(root).exibir());
        buttonContainer.getChildren().add(btnVoltar);

        container.getChildren().addAll(titulo, filterContainer, tabela, buttonContainer);
        root.setCenter(container);
    }

    private ToggleButton createFilterToggleButton(String text, boolean primary) {
        ToggleButton button = new ToggleButton(text);
        button.setMinWidth(100);
        button.setMinHeight(35);
        
        String defaultStyle = primary ?
            "-fx-background-color: white;" +
            "-fx-text-fill: #ffcbdb;" +
            "-fx-border-color: #ffcbdb;" :
            "-fx-background-color: white;" +
            "-fx-text-fill: #666666;" +
            "-fx-border-color: #666666;";
            
        String selectedStyle =
            "-fx-background-color: #ffcbdb;" +
            "-fx-text-fill: white;" +
            "-fx-border-color: #ffcbdb;";
        
        button.setStyle(defaultStyle +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 15;" +
            "-fx-background-radius: 15;"
        );
        
        button.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            button.setStyle((isSelected ? selectedStyle : defaultStyle) +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 15;" +
                "-fx-background-radius: 15;"
            );
        });
        
        return button;
    }

    private void atualizarFiltro() {
        ToggleButton selectedButton = (ToggleButton) grupoFiltros.getSelectedToggle();
        conteudosFiltrados.clear();
        
        if (selectedButton == null || selectedButton.getText().equals("Todos")) {
            conteudosFiltrados.addAll(todosConteudos);
        } else if (selectedButton.getText().equals("Ativos")) {
            conteudosFiltrados.addAll(todosConteudos.filtered(
                conteudo -> Status.isAtivo(conteudo.getStatus())
            ));
        } else if (selectedButton.getText().equals("Inativos")) {
            conteudosFiltrados.addAll(todosConteudos.filtered(
                conteudo -> Status.isInativo(conteudo.getStatus())
            ));
        }
        
        tabela.setItems(conteudosFiltrados);
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
            private final Button btnEditar = createActionButton("Editar", "#FFA500");
            private final Button btnDeletar = createDeleteButton();

            {
                btnDetalhes.setOnAction(event -> {
                    Conteudo conteudo = getTableRow().getItem();
                    if (conteudo != null) {
                        new TelaDetalhesConteudo(root, conteudo).exibir();
                    }
                });
                
                btnEditar.setOnAction(event -> {
                    Conteudo conteudo = getTableRow().getItem();
                    if (conteudo != null) {
                        new TelaEditarConteudo(conteudo, 
                                             (Stage) root.getScene().getWindow(),
                                             () -> conteudos.set(getIndex(), conteudo))
                            .mostrar();
                    }
                });
                
                btnDeletar.setOnAction(event -> {
                    Conteudo conteudo = getTableRow().getItem();
                    if (conteudo != null) {
                        confirmarDelecao(conteudo);
                    }
                });
                
                container.getChildren().addAll(btnDetalhes, btnEditar, btnDeletar);
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
        alert.setHeaderText("Deletar Conteúdo");
        alert.setContentText("Tem certeza que deseja deletar este Conteúdo?");

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
                conteudos.remove(conteudo);
                mostrarMensagemSucesso("Conteúdo deletado com sucesso!");
            } else {
                mostrarMensagemErro("Não foi possível deletar o Conteúdo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarMensagemErro("Erro ao deletar Conteúdo: " + e.getMessage());
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
            mostrarMensagemErro("Erro ao carregar Conteúdos: " + e.getMessage());
        }
        return conteudos;
    }
}