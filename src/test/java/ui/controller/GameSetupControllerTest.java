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
}
