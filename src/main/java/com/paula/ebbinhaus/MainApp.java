package com.paula.ebbinhaus;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Ebbinhaus Task Manager");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Exemplo: Exibe a tela inicial
        TelaInicial telaInicial = new TelaInicial(root);
        telaInicial.exibir();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
