package com.paula.ebbinhaus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import com.paula.ebbinhaus.Conteudo.Status;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class TelaListarTarefas {
    private BorderPane root;
    private ObservableList<Conteudo> conteudos;

    public TelaListarTarefas(BorderPane root) {
        this.root = root;
    }

    public void exibir() {
        TableView<Conteudo> tabela = new TableView<>();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        
        TableColumn<Conteudo, Integer> colunaId = new TableColumn<>("ID");
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Conteudo, String> colunaNome = new TableColumn<>("Nome");
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        
        TableColumn<Conteudo, String> colunaDescricao = new TableColumn<>("Descrição");
        colunaDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        
        // Configurando a coluna de status para ser editável
        TableColumn<Conteudo, Status> colunaStatus = new TableColumn<>("Status");
        colunaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colunaStatus.setCellFactory(ComboBoxTableCell.forTableColumn(
            FXCollections.observableArrayList(Status.values())
        ));
        
        // Adicionando o evento de edição
        colunaStatus.setOnEditCommit(event -> {
            Conteudo conteudo = event.getRowValue();
            Status novoStatus = event.getNewValue();
            conteudo.setStatus(novoStatus);
            atualizarStatusNoBanco(conteudo.getId(), novoStatus);
        });

        // Coluna Ações (Deletar)
        TableColumn<Conteudo, Void> colunaAcoes = new TableColumn<>("Ações");
        colunaAcoes.setCellFactory(col -> new TableCell<Conteudo, Void>() {
            private final Button btnDeletar = new Button("Deletar");

            {
                btnDeletar.setStyle(
                    "-fx-background-color: #ff4444;" +
                    "-fx-text-fill: white;" +
                    "-fx-cursor: hand;"
                );

                btnDeletar.setOnAction(event -> {
                    Conteudo conteudo = getTableRow().getItem();
                    if (conteudo != null) {
                        confirmarDelecao(conteudo);
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
        tabela.getColumns().addAll(colunaId, colunaNome, colunaDescricao, colunaStatus, colunaAcoes);
        
        // Carrega os dados e armazena na variável de instância
        conteudos = carregarConteudos();
        tabela.setItems(conteudos);
        
        Button btnVoltar = new Button("Voltar");
        btnVoltar.setOnAction(e -> new TelaInicial(root).exibir());

        layout.getChildren().addAll(tabela, btnVoltar);
        root.setCenter(layout);
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
        String sql = "SELECT id, nome, descricao, status FROM Conteudo WHERE idTeste IS NULL";
        
        try (Connection conn = MySQLConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String descricao = rs.getString("descricao");
                String statusStr = rs.getString("status");
                Status status = Status.valueOf(statusStr);
                
                conteudos.add(new Conteudo(id, nome, descricao, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarMensagemErro("Erro ao carregar tarefas: " + e.getMessage());
        }
        return conteudos;
    }
}