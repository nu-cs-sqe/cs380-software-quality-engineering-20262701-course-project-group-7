package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates game setup using a deck and player names supplied by the caller.
 */
public final class Game {

    private static final int INITIAL_HAND_SIZE = 7;
    private static final int MAX_EXTRA_DEFUSES = 2;
    private static final String INSUFFICIENT_DECK_MESSAGE = "game.insufficientDeck";
    private static final String ALREADY_SET_UP_MESSAGE = "game.alreadySetUp";
    private static final String NOT_SET_UP_MESSAGE = "game.notSetUp";

    private final Deck deck;
    private final List<Player> players;
    private boolean isSetUp = false;

    /**
     * The caller supplies a non-null deck and non-null, UI-validated names.
     * Creating players here keeps later changes to the caller's list from
     * changing the turn order.
     */
    public Game(Deck deck, List<String> playerNames) {
        this.deck = deck;
        this.players = createPlayers(playerNames);
    }

    /**
     * Runs the setup steps on the players created by the constructor.
     * An insufficient deck is rejected before any card is moved; unexpected
     * dealing failures are propagated without rolling back the deck.
     */
    public void setup() {
        if (isSetUp) {
            throw new IllegalStateException(ALREADY_SET_UP_MESSAGE);
        }
        validateDeckForSetup();
        prepareDeckAndGiveDefuses();
        dealInitialCards();
        finishSetup();
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

    public boolean isSetUp() {
        return isSetUp;
    }

    private List<Player> createPlayers(List<String> playerNames) {
        List<Player> created = new ArrayList<>();
        for (String name : playerNames) {
            created.add(new Player(name));
        }
        return created;
    }

    private void validateDeckForSetup() {
        int numberOfPlayers = players.size();
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

    private void prepareDeckAndGiveDefuses() {
        deck.removeAll(CardType.EXPLODING_KITTEN);
        int removedDefuses = deck.removeAll(CardType.DEFUSE);
        for (Player player : players) {
            player.addToHand(CardType.DEFUSE);
        }
        int extraDefuses = Math.min(MAX_EXTRA_DEFUSES, removedDefuses - players.size());
        deck.add(CardType.DEFUSE, extraDefuses);
        deck.shuffle();
    }

    private void dealInitialCards() {
        for (Player player : players) {
            for (int i = 0; i < INITIAL_HAND_SIZE; i++) {
                player.addToHand(deck.draw());
            }
        }
    }

    private void finishSetup() {
        deck.add(CardType.EXPLODING_KITTEN, players.size() - 1);
        deck.shuffle();
        isSetUp = true;
    }

    private void requireSetUp() {
        if (!isSetUp) {
            throw new IllegalStateException(NOT_SET_UP_MESSAGE);
        }
    }

}
