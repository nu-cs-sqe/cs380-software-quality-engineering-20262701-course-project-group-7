package ui.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import org.junit.jupiter.api.Test;

class GameSetupControllerTest {

    // BLANK-01
    @Test
    void hasBlankNameReturnsFalseForMinimumNumberOfNames() {
        assertFalse(GameSetupController.hasBlankName(List.of("Ana", "Ben")));
    }

    // BLANK-02
    @Test
    void hasBlankNameReturnsFalseForMaximumNumberOfNames() {
        List<String> names = List.of("Ana", "Ben", "Cy", "Dee", "Eve");

        assertFalse(GameSetupController.hasBlankName(names));
    }

    // BLANK-03
    @Test
    void hasBlankNameReturnsFalseForOneCharacterNames() {
        assertFalse(GameSetupController.hasBlankName(List.of("A", "B")));
    }
}
