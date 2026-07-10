package com.foodorder.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        // Start with the Login Screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1000, 700);
        // Standard window with OS title bar for fullscreen/close
        stage.setScene(scene);
        stage.setTitle("Premium Food Order");
        stage.show();
    }

    public static void setRoot(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxml));
        Parent root = loader.load();
        Scene scene = primaryStage.getScene();
        scene.setRoot(root);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
