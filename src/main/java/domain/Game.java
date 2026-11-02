package domain;

/**
 * Coordinates game setup using a deck supplied by the caller.
 */
public class Game {

    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 5;
    public static final int INITIAL_HAND_SIZE = 7;

    private final Deck deck;
    private boolean isSetUp = false;

    public Game(Deck deck) {
        if (deck == null) {
            throw new IllegalArgumentException("Deck must not be null.");
        }
        this.deck = deck;
    }

    public boolean isSetUp() {
        return isSetUp;
    }
}
