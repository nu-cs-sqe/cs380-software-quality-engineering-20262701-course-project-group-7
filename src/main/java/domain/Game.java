package domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Coordinates game setup using a deck supplied by the caller.
 */
public class Game {

    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 5;
    public static final int INITIAL_HAND_SIZE = 7;

    private static final int MAX_EXTRA_DEFUSES = 2;
    private static final String NULL_DECK_MESSAGE = "Deck must not be null.";
    private static final String PLAYER_COUNT_MESSAGE = "Number of players must be between 2 and 5.";
    private static final String NULL_NAMES_MESSAGE = "Player names must not be null.";
    private static final String BLANK_NAME_MESSAGE = "Player names must not be blank.";
    private static final String DUPLICATE_NAME_MESSAGE = "Player names must be unique.";
    private static final String INSUFFICIENT_DECK_MESSAGE = "Deck does not contain enough cards for setup.";
    private static final String ALREADY_SET_UP_MESSAGE = "Game has already been set up.";
    private static final String NOT_SET_UP_MESSAGE = "Game has not been set up.";

    private final Deck deck;
    private List<Player> players = new ArrayList<>();
    private boolean isSetUp = false;

    public Game(Deck deck) {
        if (deck == null) {
            throw new IllegalArgumentException(NULL_DECK_MESSAGE);
        }
        this.deck = deck;
    }

    /**
     * The UI can call this before asking for names; setup also revalidates the count.
     */
    public static void validatePlayerCount(int numberOfPlayers) {
        if (numberOfPlayers < MIN_PLAYERS || numberOfPlayers > MAX_PLAYERS) {
            throw new IllegalArgumentException(PLAYER_COUNT_MESSAGE);
        }
    }

    /**
     * Uses input order as turn order and preserves case-sensitive names.
     * Invalid inputs and insufficient decks are rejected before deck mutation.
     * Unexpected dealing failures are propagated without rolling back the deck.
     */
    public void setup(List<String> playerNames) {
        if (isSetUp) {
            throw new IllegalStateException(ALREADY_SET_UP_MESSAGE);
        }
        List<String> names = validatedNames(playerNames);
        validateDeckForSetup(names.size());
        List<Player> newPlayers = createPlayers(names);
        prepareDeckAndGiveDefuses(newPlayers);
        dealInitialCards(newPlayers);
        finishSetup(newPlayers);
    }

    private List<String> validatedNames(List<String> playerNames) {
        if (playerNames == null) {
            throw new IllegalArgumentException(NULL_NAMES_MESSAGE);
        }
        List<String> names = new ArrayList<>(playerNames);
        validatePlayerCount(names.size());
        Set<String> uniqueNames = new HashSet<>();
        for (String name : names) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException(BLANK_NAME_MESSAGE);
            }
            if (!uniqueNames.add(name)) {
                throw new IllegalArgumentException(DUPLICATE_NAME_MESSAGE);
            }
        }
        return names;
    }

    private void validateDeckForSetup(int numberOfPlayers) {
        int defuses = deck.count(CardType.DEFUSE);
        int kittens = deck.count(CardType.EXPLODING_KITTEN);
        int ordinaryCards = deck.size() - defuses - kittens;
        int extraDefuses = Math.min(MAX_EXTRA_DEFUSES, defuses - numberOfPlayers);
        int drawableCards = ordinaryCards + extraDefuses;
        if (defuses < numberOfPlayers || kittens < numberOfPlayers - 1
                || drawableCards < INITIAL_HAND_SIZE * numberOfPlayers) {
            throw new IllegalStateException(INSUFFICIENT_DECK_MESSAGE);
        }
    }

    private List<Player> createPlayers(List<String> names) {
        List<Player> newPlayers = new ArrayList<>();
        for (String name : names) {
            newPlayers.add(new Player(name));
        }
        return newPlayers;
    }

    private void prepareDeckAndGiveDefuses(List<Player> newPlayers) {
        deck.removeAll(CardType.EXPLODING_KITTEN);
        int removedDefuses = deck.removeAll(CardType.DEFUSE);
        for (Player player : newPlayers) {
            player.addToHand(CardType.DEFUSE);
        }
        int extraDefuses = Math.min(MAX_EXTRA_DEFUSES, removedDefuses - newPlayers.size());
        deck.add(CardType.DEFUSE, extraDefuses);
        deck.shuffle();
    }

    private void dealInitialCards(List<Player> newPlayers) {
        for (Player player : newPlayers) {
            for (int i = 0; i < INITIAL_HAND_SIZE; i++) {
                player.addToHand(deck.draw());
            }
        }
    }

    private void finishSetup(List<Player> newPlayers) {
        deck.add(CardType.EXPLODING_KITTEN, newPlayers.size() - 1);
        deck.shuffle();
        players = newPlayers;
        isSetUp = true;
    }

    public Player getCurrentPlayer() {
        requireSetUp();
        return players.get(0);
    }

    /**
     * Protects the player order, but does not copy the mutable Player objects.
     */
    public List<Player> getPlayers() {
        requireSetUp();
        return List.copyOf(players);
    }

    private void requireSetUp() {
        if (!isSetUp) {
            throw new IllegalStateException(NOT_SET_UP_MESSAGE);
        }
    }

    public boolean isSetUp() {
        return isSetUp;
    }
}
