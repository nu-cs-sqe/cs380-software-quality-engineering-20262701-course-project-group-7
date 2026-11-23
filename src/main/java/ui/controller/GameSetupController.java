package ui.controller;

import java.util.List;

public class GameSetupController {

    public static boolean hasBlankName(List<String> names) {
        for (String name : names) {
            if (name.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
