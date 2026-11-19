package ui.controller;

import java.util.List;

public class GameSetupController {

    private static final String BLANK_NAME_KEY = "setup.name.blank";

    public static void validateNames(List<String> names) {
        for (String name : names) {
            if (name.isBlank()) {
                throw new IllegalArgumentException(BLANK_NAME_KEY);
            }
        }
    }
}
