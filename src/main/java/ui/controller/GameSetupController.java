package ui.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import ui.view.GameSetupView;

public class GameSetupController {

    private static final String BUNDLE_NAME = "message";
    private static final String BLANK_NAME_KEY = "setup.name.blank";
    private static final String DUPLICATE_NAME_KEY = "setup.name.duplicate";

    private final GameSetupView view;

    public GameSetupController(GameSetupView view, Random random) {
        this.view = view;
        view.setOnLocaleChanged(this::handleLocaleChanged);
        view.setOnPlayerCountSelected(this::handlePlayerCountSelected);
        view.setOnStart(this::handleStart);
        view.updateText(ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH));
    }

    public static boolean hasBlankName(List<String> names) {
        for (String name : names) {
            if (name.isBlank()) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasDuplicateName(List<String> names) {
        Set<String> seenPlayerNames = new HashSet<>();
        for (String name : names) {
            if (!seenPlayerNames.add(name)) {
                return true;
            }
        }
        return false;
    }

    private void handlePlayerCountSelected(int count) {
        view.clearError();
        view.showNameFields(count);
    }

    private void handleLocaleChanged(Locale locale) {
        view.updateText(ResourceBundle.getBundle(BUNDLE_NAME, locale));
    }

    private void handleStart() {
        List<String> names = view.getEnteredNames();
        view.clearError();
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);
        if (hasBlankName(names)) {
            view.showError(bundle.getString(BLANK_NAME_KEY));
        } else if (hasDuplicateName(names)) {
            view.showError(bundle.getString(DUPLICATE_NAME_KEY));
        }
    }
}
