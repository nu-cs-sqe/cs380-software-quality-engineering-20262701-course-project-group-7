package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeckTest {

    private Random mockRandom;

    @BeforeEach
    public void setUp() {
        mockRandom = EasyMock.createMock(Random.class);
    }

    // BVA Method 1, TC1
    @Test
    public void constructor_nullRandom_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Deck(null));
    }

    // BVA Method 1, TC2
    @Test
    public void constructor_validRandom_createsEmptyDeck() {
        EasyMock.replay(mockRandom);

        Deck deck = new Deck(mockRandom);

        assertEquals(0, deck.size());
    }
}
