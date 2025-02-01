package com.paula.ebbinhaus.telas;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.paula.ebbinhaus.classes.Conteudo;
import com.paula.ebbinhaus.classes.MySQLConnection;
import com.paula.ebbinhaus.classes.Conteudo.Status;

public class TelaEditarConteudo {
    private final Conteudo conteudo;
    private final Stage parentStage;
    private final Runnable onUpdateSuccess;

    public TelaEditarConteudo(Conteudo conteudo, Stage parentStage, Runnable onUpdateSuccess) {
        this.conteudo = conteudo;
        this.parentStage = parentStage;
        this.onUpdateSuccess = onUpdateSuccess;
    }

    public void mostrar() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Editar Tarefa");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        TextField nomeField = new TextField(conteudo.getNome());
        nomeField.setPromptText("Nome");
        nomeField.setStyle(
            "-fx-pref-width: 300;" +
            "-fx-padding: 8;" +
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;" +
            "-fx-border-color: #ffcbdb;" +
            "-fx-border-width: 1;"
        );

        TextArea descricaoArea = new TextArea(conteudo.getDescricao());
        descricaoArea.setPromptText("Descrição");
        descricaoArea.setPrefRowCount(3);
        descricaoArea.setWrapText(true);
        descricaoArea.setStyle(
            "-fx-pref-width: 300;" +
            "-fx-padding: 8;" +
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;" +
            "-fx-border-color: #ffcbdb;" +
            "-fx-border-width: 1;"
        );

        ComboBox<Status> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll(Status.values());
        statusCombo.setValue(conteudo.getStatus());
        statusCombo.setStyle(
            "-fx-pref-width: 300;" +
            "-fx-padding: 8;" +
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;" +
            "-fx-border-color: #ffcbdb;" +
            "-fx-border-width: 1;"
        );

        Button salvarBtn = new Button("Salvar");
        Button cancelarBtn = new Button("Cancelar");

        String buttonStyle = 
            "-fx-min-width: 100;" +
            "-fx-padding: 8;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;";

        salvarBtn.setStyle(buttonStyle + 
            "-fx-background-color: #ffcbdb;" +
            "-fx-text-fill: black;");

        cancelarBtn.setStyle(buttonStyle +
            "-fx-background-color: white;" +
            "-fx-border-color: #ffcbdb;" +
            "-fx-border-width: 1;" +
            "-fx-text-fill: black;");

        salvarBtn.setOnAction(e -> {
            if (salvarAlteracoes(nomeField.getText(), descricaoArea.getText(), statusCombo.getValue())) {
                dialog.close();
                onUpdateSuccess.run();
            }
        });

        cancelarBtn.setOnAction(e -> dialog.close());

        root.getChildren().addAll(
            new Label("Nome:"), 
            nomeField,
            new Label("Descrição:"), 
            descricaoArea,
            new Label("Status:"), 
            statusCombo,
            new Separator(),
            salvarBtn,
            cancelarBtn
        );

        Scene scene = new Scene(root);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private boolean salvarAlteracoes(String novoNome, String novaDescricao, Status novoStatus) {
        String sql = "UPDATE Conteudo SET nome = ?, descricao = ?, status = ? WHERE id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, novoNome);
            stmt.setString(2, novaDescricao);
            stmt.setString(3, novoStatus.toString());
            stmt.setInt(4, conteudo.getId());

            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                conteudo.setNome(novoNome);
                conteudo.setDescricao(novaDescricao);
                conteudo.setStatus(novoStatus);
                
                mostrarMensagemSucesso("Tarefa atualizada com sucesso!");
                return true;
            } else {
                mostrarMensagemErro("Não foi possível atualizar a tarefa.");
                return false;
            }
        } catch (SQLException e) {
            mostrarMensagemErro("Erro ao atualizar tarefa: " + e.getMessage());
            e.printStackTrace();
            return false;
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
}