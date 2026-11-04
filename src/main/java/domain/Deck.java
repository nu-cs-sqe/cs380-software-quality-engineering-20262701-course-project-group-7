package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Deck {

    private final List<CardType> cards;
    private final Random random;

    public Deck(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random must not be null.");
        }
        this.random = random;
        this.cards = new ArrayList<>();
    }

    public int size() {
        return cards.size();
    }

    public void add(CardType type, int numberOfCards) {
        if (type == null) {
            throw new IllegalArgumentException("Card type must not be null.");
        }
        for (int i = 0; i < numberOfCards; i++) {
            cards.add(type);
        }
    }

    public CardType draw() {
        throw new IllegalStateException("Cannot draw from an empty deck.");
    }
}
