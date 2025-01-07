package com.paula.ebbinhaus;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

public class TelaListarTarefas {
    private BorderPane root;

    public TelaListarTarefas(BorderPane root) {
        this.root = root;
    }

    public void exibir() {
        TableView<Disciplina> tabela = new TableView<>();
        TableColumn<Disciplina, String> colunaNome = new TableColumn<>("Nome");
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Disciplina, String> colunaStatus = new TableColumn<>("Status");
        colunaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        tabela.getColumns().addAll(colunaNome, colunaStatus);
        tabela.setItems(carregarDisciplinas());

        root.setCenter(tabela);
    }

    private ObservableList<Disciplina> carregarDisciplinas() {
        ObservableList<Disciplina> disciplinas = FXCollections.observableArrayList();
        // Conecte ao banco para buscar as disciplinas e adicionar à lista
        return disciplinas;
    }
}
