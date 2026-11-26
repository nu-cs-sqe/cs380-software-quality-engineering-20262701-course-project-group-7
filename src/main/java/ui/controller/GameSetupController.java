package ui.controller;

import domain.Deck;
import domain.Game;
import domain.Player;
import java.util.ArrayList;
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
    private static final String SETUP_FAILED_KEY = "setup.error.failed";

    private final GameSetupView view;
    private final Random random;
    private ResourceBundle bundle;

    public GameSetupController(GameSetupView view, Random random) {
        this.view = view;
        this.random = random;
        view.setOnLocaleChanged(this::handleLocaleChanged);
        view.setOnPlayerCountSelected(this::handlePlayerCountSelected);
        view.setOnStart(this::handleStart);
        bundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);
        view.updateText(bundle);
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
        bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        view.updateText(bundle);
    }

    private void handleStart() {
        List<String> names = view.getEnteredNames();
        view.clearError();
        if (hasBlankName(names)) {
            view.showError(bundle.getString(BLANK_NAME_KEY));
        } else if (hasDuplicateName(names)) {
            view.showError(bundle.getString(DUPLICATE_NAME_KEY));
        } else {
            startGame(names);
        }
    }

    private void startGame(List<String> names) {
        try {
            Game game = new Game(Deck.createStandardDeck(random), names);
            game.setup();
            view.showPlayers(playerNames(game), game.getCurrentPlayer().getName());
        } catch (IllegalStateException e) {
            view.showError(bundle.getString(SETUP_FAILED_KEY));
        }
    }

    private static List<String> playerNames(Game game) {
        List<String> names = new ArrayList<>();
        for (Player player : game.getPlayers()) {
            names.add(player.getName());
        }
        return names;
    }
}
