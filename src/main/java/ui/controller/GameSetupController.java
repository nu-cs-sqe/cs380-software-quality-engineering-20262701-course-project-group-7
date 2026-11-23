package ui.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameSetupController {

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
