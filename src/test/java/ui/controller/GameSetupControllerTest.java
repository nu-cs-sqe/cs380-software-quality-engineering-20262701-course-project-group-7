package ui.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    // BLANK-04
    @Test
    void hasBlankNameReturnsFalseForNameWithLeadingSpace() {
        assertFalse(GameSetupController.hasBlankName(List.of(" Ana", "Ben")));
    }

    // BLANK-05
    @Test
    void hasBlankNameReturnsTrueForEmptyFirstName() {
        assertTrue(GameSetupController.hasBlankName(List.of("", "Ben")));
    }

    // BLANK-06
    @Test
    void hasBlankNameReturnsTrueForSingleSpaceName() {
        assertTrue(GameSetupController.hasBlankName(List.of(" ", "Ben")));
    }

    // BLANK-07
    @Test
    void hasBlankNameReturnsTrueForMixedWhitespaceName() {
        assertTrue(GameSetupController.hasBlankName(List.of("\t ", "Ben")));
    }

    // BLANK-08
    @Test
    void hasBlankNameReturnsTrueForBlankLastName() {
        List<String> names = List.of("Ana", "Ben", "Cy", "Dee", "");

        assertTrue(GameSetupController.hasBlankName(names));
    }

    // DUP-01
    @Test
    void hasDuplicateNameReturnsFalseForMinimumNumberOfNames() {
        assertFalse(GameSetupController.hasDuplicateName(List.of("Ana", "Ben")));
    }

    // DUP-02
    @Test
    void hasDuplicateNameReturnsFalseForMaximumNumberOfNames() {
        List<String> names = List.of("Ana", "Ben", "Cy", "Dee", "Eve");

        assertFalse(GameSetupController.hasDuplicateName(names));
    }

    // DUP-03
    @Test
    void hasDuplicateNameReturnsFalseForNamesDifferingOnlyByCase() {
        assertFalse(GameSetupController.hasDuplicateName(List.of("A", "a")));
    }
}
