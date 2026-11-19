package ui.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameSetupController {

    private static final String BLANK_NAME_KEY = "setup.name.blank";
    private static final String DUPLICATE_NAME_KEY = "setup.name.duplicate";

    public static void validateNames(List<String> names) {
        for (String name : names) {
            if (name.isBlank()) {
                throw new IllegalArgumentException(BLANK_NAME_KEY);
            }
        }
        Set<String> seenNames = new HashSet<>();
        for (String name : names) {
            if (!seenNames.add(name)) {
                throw new IllegalArgumentException(DUPLICATE_NAME_KEY);
            }
        }
    }
}
