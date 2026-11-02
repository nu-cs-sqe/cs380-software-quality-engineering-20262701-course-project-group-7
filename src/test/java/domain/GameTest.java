package domain;

import java.util.Random;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameTest {

    @Test
    void constructorRejectsNullDeck() {
        assertThrows(IllegalArgumentException.class, () -> new Game(null));
    }

    @Test
    void constructorAcceptsDeckWithoutSettingUpOrDrawingCards() {
        Deck deck = Deck.createStandardDeck(new Random(0));
        int initialDeckSize = deck.size();

        Game game = assertDoesNotThrow(() -> new Game(deck));

        assertFalse(game.isSetUp());
        assertEquals(initialDeckSize, deck.size());
    }

    @Test
    void isSetUpRemainsFalseBeforeSetup() {
        Deck deck = Deck.createStandardDeck(new Random(0));
        Game game = new Game(deck);

        assertFalse(game.isSetUp());
        assertFalse(game.isSetUp());
    }
}
