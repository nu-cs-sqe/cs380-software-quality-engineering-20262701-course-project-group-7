package ui.view;

import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class GameSetupView {

    private static final String TITLE_KEY = "setup.title";
    private static final double SPACING = 10;
    private static final double PADDING = 20;

    private final Label titleLabel = new Label();
    private final VBox root;

    public GameSetupView() {
        root = new VBox(SPACING, titleLabel);
        root.setPadding(new Insets(PADDING));
    }

    public Parent getRoot() {
        return root;
    }

    public void updateText(ResourceBundle bundle) {
        titleLabel.setText(bundle.getString(TITLE_KEY));
    }
}
