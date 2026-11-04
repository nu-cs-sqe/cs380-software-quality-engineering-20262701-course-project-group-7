package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Deck {

    private static final Map<CardType, Integer> STANDARD_CARD_COUNTS = createStandardCardCounts();

    private final List<CardType> cards;
    private final Random random;

    public Deck(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random must not be null.");
        }
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

    private static Map<CardType, Integer> createStandardCardCounts() {
        Map<CardType, Integer> counts = new EnumMap<>(CardType.class);
        counts.put(CardType.EXPLODING_KITTEN, 4);
        counts.put(CardType.DEFUSE, 6);
        counts.put(CardType.ATTACK, 4);
        counts.put(CardType.SKIP, 4);
        counts.put(CardType.FAVOR, 4);
        counts.put(CardType.SHUFFLE, 4);
        counts.put(CardType.SEE_THE_FUTURE, 5);
        counts.put(CardType.NOPE, 5);
        counts.put(CardType.TACOCAT, 4);
        counts.put(CardType.CATTERMELON, 4);
        counts.put(CardType.HAIRY_POTATO_CAT, 4);
        counts.put(CardType.BEARD_CAT, 4);
        counts.put(CardType.RAINBOW_RALPHING_CAT, 4);
        return Collections.unmodifiableMap(counts);
    }

    public int size() {
        return cards.size();
    }

    public void add(CardType type, int numberOfCards) {
        requireCardType(type);
        if (numberOfCards < 0) {
            throw new IllegalArgumentException("Number of cards must not be negative.");
        }
        for (int i = 0; i < numberOfCards; i++) {
            cards.add(type);
        }
    }

    public CardType draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Cannot draw from an empty deck.");
        }
        return cards.remove(0);
    }

    public int count(CardType type) {
        requireCardType(type);
        return Collections.frequency(cards, type);
    }

    public int removeAll(CardType type) {
        requireCardType(type);
        int sizeBefore = cards.size();
        cards.removeIf(card -> card == type);
        return sizeBefore - cards.size();
    }

    private static void requireCardType(CardType type) {
        if (type == null) {
            throw new IllegalArgumentException("Card type must not be null.");
        }
    }
}
