package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Main extends Application {

    private static final int SCREEN_WIDTH = 400;
    private static final int SCREEN_HEIGHT = 300;

    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(new Label("Exploding Kittens"), SCREEN_WIDTH, SCREEN_HEIGHT));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
