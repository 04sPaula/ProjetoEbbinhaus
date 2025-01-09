package com.paula.ebbinhaus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;

public class TelaListarTestes {
    private BorderPane root;
    private ObservableList<Teste> testes;

    public TelaListarTestes(BorderPane root) {
        this.root = root;
    }

    public void exibir() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TableView<Teste> tabela = new TableView<>();
        
        // Coluna ID
        TableColumn<Teste, Integer> colunaId = new TableColumn<>("ID");
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        // Coluna Conteúdos
        TableColumn<Teste, String> colunaConteudos = new TableColumn<>("Conteúdos");
        colunaConteudos.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getConteudosAsString())
        );
        
        // Coluna Data com DatePicker
        TableColumn<Teste, LocalDate> colunaData = new TableColumn<>("Data");
        colunaData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colunaData.setCellFactory(column -> new TableCell<Teste, LocalDate>() {
            private final DatePicker datePicker = new DatePicker();

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

        // Coluna Ações (Deletar)
        TableColumn<Teste, Void> colunaAcoes = new TableColumn<>("Ações");
        colunaAcoes.setCellFactory(col -> new TableCell<Teste, Void>() {
            private final Button btnDeletar = new Button("Deletar");

            {
                btnDeletar.setStyle(
                    "-fx-background-color: #ff4444;" +
                    "-fx-text-fill: white;" +
                    "-fx-cursor: hand;"
                );

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
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDeletar);
                }
            }
        });

        tabela.setEditable(true);
        tabela.getColumns().addAll(colunaId, colunaConteudos, colunaData, colunaAcoes);
        
        // Carrega os dados e armazena na variável de instância
        testes = carregarTestes();
        tabela.setItems(testes);

        // Botão Voltar
        Button btnVoltar = new Button("Voltar");
        btnVoltar.setOnAction(e -> new TelaInicial(root).exibir());

        layout.getChildren().addAll(tabela, btnVoltar);
        root.setCenter(layout);
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
                // Remove o teste da lista observável
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