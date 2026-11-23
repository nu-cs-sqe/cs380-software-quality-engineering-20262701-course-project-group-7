package ui.view;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class GameSetupView {

    private static final String TITLE_KEY = "setup.title";
    private static final String LANGUAGE_KEY = "setup.language";
    private static final double SPACING = 10;
    private static final double PADDING = 20;

    private final Label titleLabel = new Label();
    private final Label languageLabel = new Label();
    private final ComboBox<Locale> languageBox = new ComboBox<>();
    private final VBox root;

    public GameSetupView() {
        root = new VBox(SPACING, createLanguageRow(), titleLabel);
        root.setPadding(new Insets(PADDING));
    }

    public Parent getRoot() {
        return root;
    }

    public void setOnLocaleChanged(Consumer<Locale> handler) {
        languageBox.setOnAction(event -> handler.accept(languageBox.getValue()));
    }

    public void updateText(ResourceBundle bundle) {
        titleLabel.setText(bundle.getString(TITLE_KEY));
        languageLabel.setText(bundle.getString(LANGUAGE_KEY));
    }

    private HBox createLanguageRow() {
        languageBox.getItems().addAll(Locale.ENGLISH, Locale.CHINESE);
        languageBox.setValue(Locale.ENGLISH);
        languageBox.setConverter(new LocaleNameConverter());
        return new HBox(SPACING, languageLabel, languageBox);
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
