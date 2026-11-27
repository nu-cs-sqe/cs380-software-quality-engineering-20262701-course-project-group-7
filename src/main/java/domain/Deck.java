package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Deck {

    private static final int DEFAULT_COPIES = 4;
    private static final int DEFUSE_COPIES = 6;
    private static final int SEE_THE_FUTURE_COPIES = 5;
    private static final int NOPE_COPIES = 5;
    private static final String EMPTY_DECK_MESSAGE = "Cannot draw from an empty deck.";
    private static final String NEGATIVE_NUMBER_OF_CARDS_MESSAGE =
            "Number of cards must not be negative.";
    private static final Map<CardType, Integer> STANDARD_CARD_COUNTS = createStandardCardCounts();

    private final List<CardType> cards;
    private final Random random;

    public Deck(Random random) {
        this.random = random;
        this.cards = new ArrayList<>();
    }

    public static Deck createStandardDeck(Random random) {
        Deck deck = new Deck(random);
        for (Map.Entry<CardType, Integer> entry : STANDARD_CARD_COUNTS.entrySet()) {
            deck.add(entry.getKey(), entry.getValue());
        }
        return deck;
    }

    public int size() {
        return cards.size();
    }

    public void shuffle() {
        Collections.shuffle(cards, random);
    }

    public void add(CardType type, int numberOfCards) {
        if (numberOfCards < 0) {
            throw new IllegalArgumentException(NEGATIVE_NUMBER_OF_CARDS_MESSAGE);
        }
        for (int i = 0; i < numberOfCards; i++) {
            cards.add(type);
        }
    }

    public CardType draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException(EMPTY_DECK_MESSAGE);
        }
        return cards.remove(0);
    }

    public int count(CardType type) {
        return Collections.frequency(cards, type);
    }

    public int removeAll(CardType type) {
        int sizeBefore = cards.size();
        cards.removeIf(card -> card == type);
        return sizeBefore - cards.size();
    }

    public void insertAt(CardType type, int position) {
        cards.add(position, type);
    }

    private static Map<CardType, Integer> createStandardCardCounts() {
        Map<CardType, Integer> counts = new EnumMap<>(CardType.class);
        counts.put(CardType.EXPLODING_KITTEN, DEFAULT_COPIES);
        counts.put(CardType.DEFUSE, DEFUSE_COPIES);
        counts.put(CardType.ATTACK, DEFAULT_COPIES);
        counts.put(CardType.SKIP, DEFAULT_COPIES);
        counts.put(CardType.FAVOR, DEFAULT_COPIES);
        counts.put(CardType.SHUFFLE, DEFAULT_COPIES);
        counts.put(CardType.SEE_THE_FUTURE, SEE_THE_FUTURE_COPIES);
        counts.put(CardType.NOPE, NOPE_COPIES);
        counts.put(CardType.TACOCAT, DEFAULT_COPIES);
        counts.put(CardType.CATTERMELON, DEFAULT_COPIES);
        counts.put(CardType.HAIRY_POTATO_CAT, DEFAULT_COPIES);
        counts.put(CardType.BEARD_CAT, DEFAULT_COPIES);
        counts.put(CardType.RAINBOW_RALPHING_CAT, DEFAULT_COPIES);
        return Collections.unmodifiableMap(counts);
    }
}
