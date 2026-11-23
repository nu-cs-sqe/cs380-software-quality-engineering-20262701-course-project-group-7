package ui.view;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class GameSetupView {

    private static final String TITLE_KEY = "setup.title";
    private static final String LANGUAGE_KEY = "setup.language";
    private static final String CHOOSE_COUNT_KEY = "setup.chooseCount";
    private static final String PLAYER_NAME_KEY = "setup.playerName";
    private static final String START_KEY = "setup.start";
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 5;
    private static final double SPACING = 10;
    private static final double PADDING = 20;

    private final Label titleLabel = new Label();
    private final Label languageLabel = new Label();
    private final ComboBox<Locale> languageBox = new ComboBox<>();
    private final Label chooseCountLabel = new Label();
    private final List<Button> countButtons = new ArrayList<>();
    private final VBox nameRows = new VBox(SPACING);
    private final List<Label> nameLabels = new ArrayList<>();
    private final List<TextField> nameFields = new ArrayList<>();
    private final Button startButton = new Button();
    private final VBox root;
    private String playerNamePattern = "";

    public GameSetupView() {
        setShown(startButton, false);
        root = new VBox(SPACING, createLanguageRow(), titleLabel, chooseCountLabel,
                createCountRow(), nameRows, startButton);
        root.setPadding(new Insets(PADDING));
    }

    public Parent getRoot() {
        return root;
    }

    public void setOnLocaleChanged(Consumer<Locale> handler) {
        languageBox.setOnAction(event -> handler.accept(languageBox.getValue()));
    }

    public void setOnPlayerCountSelected(IntConsumer handler) {
        for (int i = 0; i < countButtons.size(); i++) {
            int count = MIN_PLAYERS + i;
            countButtons.get(i).setOnAction(event -> handler.accept(count));
        }
    }

    public void setOnStart(Runnable handler) {
        startButton.setOnAction(event -> handler.run());
    }

    public void showNameFields(int count) {
        nameRows.getChildren().clear();
        nameLabels.clear();
        nameFields.clear();
        for (int i = 0; i < count; i++) {
            nameRows.getChildren().add(createNameRow(i));
        }
        setShown(startButton, true);
    }

    public List<String> getEnteredNames() {
        List<String> names = new ArrayList<>();
        for (TextField field : nameFields) {
            names.add(field.getText());
        }
        return names;
    }

    public void updateText(ResourceBundle bundle) {
        titleLabel.setText(bundle.getString(TITLE_KEY));
        languageLabel.setText(bundle.getString(LANGUAGE_KEY));
        chooseCountLabel.setText(bundle.getString(CHOOSE_COUNT_KEY));
        startButton.setText(bundle.getString(START_KEY));
        playerNamePattern = bundle.getString(PLAYER_NAME_KEY);
        for (int i = 0; i < nameLabels.size(); i++) {
            nameLabels.get(i).setText(formatPlayerName(i));
        }
    }

    private HBox createLanguageRow() {
        languageBox.getItems().addAll(Locale.ENGLISH, Locale.CHINESE);
        languageBox.setValue(Locale.ENGLISH);
        languageBox.setConverter(new LocaleNameConverter());
        return new HBox(SPACING, languageLabel, languageBox);
    }

    private HBox createCountRow() {
        HBox countRow = new HBox(SPACING);
        for (int count = MIN_PLAYERS; count <= MAX_PLAYERS; count++) {
            Button button = new Button(String.valueOf(count));
            countButtons.add(button);
            countRow.getChildren().add(button);
        }
        return countRow;
    }

    private HBox createNameRow(int index) {
        Label label = new Label(formatPlayerName(index));
        TextField field = new TextField();
        nameLabels.add(label);
        nameFields.add(field);
        return new HBox(SPACING, label, field);
    }

    private String formatPlayerName(int index) {
        return MessageFormat.format(playerNamePattern, index + 1);
    }

    private static void setShown(Node node, boolean shown) {
        node.setVisible(shown);
        node.setManaged(shown);
    }

    private static class LocaleNameConverter extends StringConverter<Locale> {

        @Override
        public String toString(Locale locale) {
            return locale.getDisplayLanguage(locale);
        }

        @Override
        public Locale fromString(String text) {
            return Locale.forLanguageTag(text);
        }
    }
}
