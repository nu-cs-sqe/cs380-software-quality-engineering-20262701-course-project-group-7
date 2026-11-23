package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    private static final int SCREEN_WIDTH = 500;
    private static final int SCREEN_HEIGHT = 400;

    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(new StackPane(), SCREEN_WIDTH, SCREEN_HEIGHT));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
