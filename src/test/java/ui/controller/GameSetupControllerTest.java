package ui.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.IntConsumer;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;
import ui.view.GameSetupView;

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

    // DUP-04
    @Test
    void hasDuplicateNameReturnsFalseForNamesDifferingOnlyByLeadingSpace() {
        assertFalse(GameSetupController.hasDuplicateName(List.of("Ana", " Ana")));
    }

    // DUP-05
    @Test
    void hasDuplicateNameReturnsTrueForAdjacentDuplicateNames() {
        assertTrue(GameSetupController.hasDuplicateName(List.of("Ana", "Ana")));
    }

    // DUP-06
    @Test
    void hasDuplicateNameReturnsTrueForEqualFirstAndLastNames() {
        List<String> names = List.of("Ana", "Ben", "Cy", "Dee", "Ana");

        assertTrue(GameSetupController.hasDuplicateName(names));
    }

    // CTRL-01
    @Test
    void constructorRegistersHandlersThenShowsEnglishText() {
        GameSetupView view = EasyMock.createStrictMock(GameSetupView.class);
        view.setOnLocaleChanged(EasyMock.anyObject());
        view.setOnPlayerCountSelected(EasyMock.anyObject());
        view.setOnStart(EasyMock.anyObject());
        Capture<ResourceBundle> bundle = EasyMock.newCapture();
        view.updateText(EasyMock.capture(bundle));
        EasyMock.replay(view);

        new GameSetupController(view, new Random(0));

        EasyMock.verify(view);
        assertEquals("Exploding Kittens", bundle.getValue().getString("setup.title"));
    }

    // COUNT-01
    @Test
    void playerCountHandlerShowsTwoNameFieldsForMinimumCount() {
        GameSetupView view = EasyMock.createStrictMock(GameSetupView.class);
        view.setOnLocaleChanged(EasyMock.anyObject());
        Capture<IntConsumer> countHandler = EasyMock.newCapture();
        view.setOnPlayerCountSelected(EasyMock.capture(countHandler));
        view.setOnStart(EasyMock.anyObject());
        view.updateText(EasyMock.anyObject());
        view.clearError();
        view.showNameFields(2);
        EasyMock.replay(view);

        new GameSetupController(view, new Random(0));
        countHandler.getValue().accept(2);

        EasyMock.verify(view);
    }
}
