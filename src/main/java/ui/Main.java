package ui;

import java.util.Random;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.controller.GameSetupController;
import ui.view.GameSetupView;

public class Main extends Application {

    private static final int SCREEN_WIDTH = 500;
    private static final int SCREEN_HEIGHT = 400;

    @Override
    public void start(Stage stage) {
        GameSetupView view = new GameSetupView();
        new GameSetupController(view, new Random());
        stage.setScene(new Scene(view.getRoot(), SCREEN_WIDTH, SCREEN_HEIGHT));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
