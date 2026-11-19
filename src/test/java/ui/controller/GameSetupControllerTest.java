package ui.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class GameSetupControllerTest {

    private static final String BLANK_NAME_KEY = "setup.name.blank";

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

    // SETUP-04
    @Test
    void validateNamesTreatsNamesDifferingOnlyByCaseAsDifferent() {
        assertDoesNotThrow(() -> GameSetupController.validateNames(List.of("A", "a")));
    }

    // SETUP-05
    @Test
    void validateNamesAcceptsNameWithLeadingSpace() {
        assertDoesNotThrow(() -> GameSetupController.validateNames(List.of(" Ana", "Ben")));
    }

    // SETUP-06
    @Test
    void validateNamesRejectsEmptyFirstName() {
        List<String> names = List.of("", "Ben");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> GameSetupController.validateNames(names));
        assertEquals(BLANK_NAME_KEY, exception.getMessage());
    }

    // SETUP-07
    @Test
    void validateNamesRejectsSingleSpaceName() {
        List<String> names = List.of(" ", "Ben");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> GameSetupController.validateNames(names));
        assertEquals(BLANK_NAME_KEY, exception.getMessage());
    }

    // SETUP-08
    @Test
    void validateNamesRejectsTabWhitespaceName() {
        List<String> names = List.of("\t ", "Ben");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> GameSetupController.validateNames(names));
        assertEquals(BLANK_NAME_KEY, exception.getMessage());
    }
}
