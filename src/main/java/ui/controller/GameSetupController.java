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

    public GameSetupController(GameSetupView view, Random random) {
        view.setOnLocaleChanged(locale -> {});
        view.setOnPlayerCountSelected(count -> {});
        view.setOnStart(() -> {});
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
}
