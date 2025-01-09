package com.paula.ebbinhaus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class TelaListarTestes {
    private BorderPane root;
    private ObservableList<Teste> testes;
    private TableView<Teste> tabela;

    public TelaListarTestes(BorderPane root) {
        this.root = root;
    }

    public void exibir() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);

        Label titulo = new Label("Lista de Testes");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));

        tabela = createStyledTableView();
        
        TableColumn<Teste, Integer> colunaId = new TableColumn<>("ID");
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Teste, String> colunaConteudos = new TableColumn<>("Conteúdos");
        colunaConteudos.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getConteudosAsString()));
        colunaConteudos.setPrefWidth(400);
        
        TableColumn<Teste, LocalDate> colunaData = createDateColumn();
        TableColumn<Teste, Void> colunaAcoes = createActionsColumn();

        tabela.getColumns().addAll(colunaId, colunaConteudos, colunaData, colunaAcoes);
        
        testes = carregarTestes();
        tabela.setItems(testes);

        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        Button btnVoltar = createStyledButton("Voltar", false);
        btnVoltar.setOnAction(e -> new TelaInicial(root).exibir());
        buttonContainer.getChildren().add(btnVoltar);

        container.getChildren().addAll(titulo, tabela, buttonContainer);
        root.setCenter(container);
    }

    private TableView<Teste> createStyledTableView() {
        TableView<Teste> table = new TableView<>();
        table.setEditable(true);
        table.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;" +
            "-fx-border-color: #ffcbdb;" +
            "-fx-border-width: 2;"
        );
        table.setPrefHeight(400);
        return table;
    }

    private TableColumn<Teste, LocalDate> createDateColumn() {
        TableColumn<Teste, LocalDate> column = new TableColumn<>("Data");
        column.setCellValueFactory(new PropertyValueFactory<>("data"));
        column.setCellFactory(col -> new TableCell<Teste, LocalDate>() {
            private final DatePicker datePicker = createStyledDatePicker();

            {
                datePicker.setOnAction(event -> {
                    Teste teste = getTableRow().getItem();
                    if (teste != null) {
                        LocalDate novaData = datePicker.getValue();
                        atualizarDataNoBanco(teste.getId(), novaData);
                        teste.setData(novaData);
                    }
                });
            }

            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    datePicker.setValue(item);
                    setGraphic(datePicker);
                }
            }
        });
        column.setPrefWidth(150);
        return column;
    }

    private DatePicker createStyledDatePicker() {
        DatePicker picker = new DatePicker();
        picker.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #ffcbdb;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 3;" +
            "-fx-background-radius: 3;"
        );
        return picker;
    }

    private TableColumn<Teste, Void> createActionsColumn() {
        TableColumn<Teste, Void> column = new TableColumn<>("Ações");
        column.setCellFactory(col -> new TableCell<Teste, Void>() {
            private final Button btnDeletar = createDeleteButton();

            {
                btnDeletar.setOnAction(event -> {
                    Teste teste = getTableRow().getItem();
                    if (teste != null) {
                        confirmarDelecao(teste);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDeletar);
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

    private void confirmarDelecao(Teste teste) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Deletar Teste");
        alert.setContentText("Tem certeza que deseja deletar este teste e todos os seus conteúdos associados?");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            deletarTeste(teste);
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

    private void deletarTeste(Teste teste) {
        try {
            if (teste.deletar()) {
                testes.remove(teste);
                mostrarMensagemSucesso("Teste deletado com sucesso!");
            } else {
                mostrarMensagemErro("Não foi possível deletar o teste");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarMensagemErro("Erro ao deletar teste: " + e.getMessage());
        }
    }

    private void atualizarDataNoBanco(int id, LocalDate novaData) {
        try {
            Teste teste = testes.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
                
            if (teste != null && teste.atualizarData(novaData)) {
                mostrarMensagemSucesso("Data do teste atualizada com sucesso!");
            } else {
                mostrarMensagemErro("Não foi possível atualizar a data do teste");
            }
        } catch (SQLException e) {
            mostrarMensagemErro("Erro ao atualizar data do teste: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ObservableList<Teste> carregarTestes() {
        ObservableList<Teste> testes = FXCollections.observableArrayList();
        String sql = """
            SELECT t.id, t.data, c.id AS conteudoId, c.nome AS conteudoNome
            FROM Teste t
            LEFT JOIN Conteudo c ON c.idTeste = t.id
        """;

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            Map<Integer, Teste> testeMap = new HashMap<>();

            while (rs.next()) {
                int id = rs.getInt("id");
                LocalDate data = rs.getDate("data").toLocalDate();

                Teste teste = testeMap.get(id);
                if (teste == null) {
                    teste = new Teste(id, data, new ArrayList<>());
                    testeMap.put(id, teste);
                    testes.add(teste);
                }

                int conteudoId = rs.getInt("conteudoId");
                if (conteudoId != 0) {
                    String conteudoNome = rs.getString("conteudoNome");
                    teste.getConteudos().add(new Conteudo(conteudoId, conteudoNome, null, null));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarMensagemErro("Erro ao carregar testes: " + e.getMessage());
        }
        return testes;
    }
}