package com.example.footballticketmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        changerScene(null, "login-view.fxml", "Connexion", 700, 480);
    }

    public static void changerScene(Node node, String fxml, String titre, int width, int height) throws Exception {
        Stage stage = (node != null) ? (Stage) node.getScene().getWindow() : primaryStage;
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/view/" + fxml));
        Scene scene = new Scene(loader.load(), width, height);
        scene.getStylesheets().add(
            HelloApplication.class.getResource("/view/style.css").toExternalForm()
        );
        stage.setTitle(titre);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
