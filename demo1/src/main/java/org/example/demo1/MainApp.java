package org.example.demo1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        CalculadoraView vista = new CalculadoraView();
        Scene scene = new Scene(vista.getRoot(), 900, 650);

        primaryStage.setTitle("Calculadora — Área Bajo la Curva");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(750);
        primaryStage.setMinHeight(550);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
