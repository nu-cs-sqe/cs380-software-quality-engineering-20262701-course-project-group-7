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
        Deck deck = createDeckWith();

        assertEquals(0, deck.size());
    }

    // BVA Method 2, TC2
    @Test
    public void size_oneCard_returnsOne() {
        Deck deck = createDeckWith(CardType.ATTACK);

        assertEquals(1, deck.size());
    }

    // BVA Method 2, TC3
    @Test
    public void size_threeCards_returnsThree() {
        Deck deck = createDeckWith(CardType.ATTACK, CardType.SKIP, CardType.FAVOR);

        assertEquals(3, deck.size());
    }

    // BVA Method 3, TC1
    @Test
    public void add_nullType_throwsIllegalArgumentException() {
        Deck deck = createDeckWith();

        assertThrows(IllegalArgumentException.class, () -> deck.add(null, 1));
        assertEquals(0, deck.size());
    }

    // BVA Method 3, TC3
    @Test
    public void add_zeroCards_leavesDeckEmpty() {
        Deck deck = createDeckWith();

        deck.add(CardType.ATTACK, 0);

        assertEquals(0, deck.size());
    }

    // BVA Method 4, TC1
    @Test
    public void draw_emptyDeck_throwsIllegalStateException() {
        Deck deck = createDeckWith();

        assertThrows(IllegalStateException.class, deck::draw);
    }

    // BVA Method 4, TC2
    @Test
    public void draw_oneCard_returnsItAndEmptiesDeck() {
        Deck deck = createDeckWith(CardType.NOPE);

        assertEquals(CardType.NOPE, deck.draw());
        assertEquals(0, deck.size());
    }

    // BVA Method 4, TC3
    @Test
    public void draw_threeCards_returnsTopCard() {
        Deck deck = createDeckWith(CardType.SKIP, CardType.ATTACK, CardType.FAVOR);

        assertEquals(CardType.SKIP, deck.draw());
        assertEquals(2, deck.size());
    }

    // BVA Method 4, TC4
    @Test
    public void draw_untilEmpty_returnsCardsInOrderThenThrows() {
        Deck deck = createDeckWith(CardType.SKIP, CardType.ATTACK, CardType.FAVOR);

        assertEquals(CardType.SKIP, deck.draw());
        assertEquals(CardType.ATTACK, deck.draw());
        assertEquals(CardType.FAVOR, deck.draw());
        assertThrows(IllegalStateException.class, deck::draw);
    }

    // BVA Method 4, TC7
    @Test
    public void draw_oneOfEachType_returnsEveryTypeInOrder() {
        Deck deck = createDeckWith(CardType.values());

        for (CardType expected : CardType.values()) {
            assertEquals(expected, deck.draw());
        }
    }

    // BVA Method 3, TC4
    @Test
    public void add_oneCardToEmptyDeck_drawReturnsThatCard() {
        Deck deck = createDeckWith();

        deck.add(CardType.ATTACK, 1);

        assertEquals(1, deck.size());
        assertEquals(CardType.ATTACK, deck.draw());
    }

    private Deck createDeckWith(CardType... cardsFromTop) {
        EasyMock.replay(mockRandom);
        Deck deck = new Deck(mockRandom);
        for (CardType card : cardsFromTop) {
            deck.add(card, 1);
        }
        return deck;
    }
}
