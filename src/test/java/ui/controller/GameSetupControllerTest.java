package ui.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.Test;

class GameSetupControllerTest {

    // SETUP-01
    @Test
    void validateNamesAcceptsMinimumNumberOfNames() {
        assertDoesNotThrow(() -> GameSetupController.validateNames(List.of("Ana", "Ben")));
    }

    // SETUP-02
    @Test
    void validateNamesAcceptsMaximumNumberOfNames() {
        List<String> names = List.of("Ana", "Ben", "Cy", "Dee", "Eve");
        assertDoesNotThrow(() -> GameSetupController.validateNames(names));
    }

    // SETUP-03
    @Test
    void validateNamesAcceptsOneCharacterNames() {
        assertDoesNotThrow(() -> GameSetupController.validateNames(List.of("A", "B")));
    }
}
