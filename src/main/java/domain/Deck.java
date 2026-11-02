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
}
