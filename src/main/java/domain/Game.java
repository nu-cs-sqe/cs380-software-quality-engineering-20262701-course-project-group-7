package domain;

import java.util.ArrayList;
import java.util.List;

public final class Game {

    private static final int INITIAL_HAND_SIZE = 7;
    private static final int MAX_EXTRA_DEFUSES = 2;
    private static final String INSUFFICIENT_DECK_MESSAGE =
            "Deck does not contain enough cards for setup.";
    private static final String ALREADY_SET_UP_MESSAGE = "Game has already been set up.";
    private static final String NOT_SET_UP_MESSAGE = "Game has not been set up.";

    private final Deck deck;
    private final List<Player> players;
    private boolean isSetUp = false;

    public Game(Deck deck, List<String> playerNames) {
        this.deck = deck;
        this.players = createPlayers(playerNames);
    }

    public void setup() {
        if (isSetUp) {
            throw new IllegalStateException(ALREADY_SET_UP_MESSAGE);
        }
        validateDeckForSetup();
        int removedDefuses = removeSpecialCards();
        giveGuaranteedDefuses();
        returnExtraDefuses(removedDefuses);
        deck.shuffle();
        dealInitialCards();
        returnExplodingKittens();
        deck.shuffle();
        isSetUp = true;
    }

    public Player getCurrentPlayer() {
        requireSetUp();
        return players.get(0);
    }

    public List<Player> getPlayers() {
        requireSetUp();
        return List.copyOf(players);
    }

    public boolean isSetUp() {
        return isSetUp;
    }

    private List<Player> createPlayers(List<String> names) {
        List<Player> newPlayers = new ArrayList<>();
        for (String name : names) {
            newPlayers.add(new Player(name));
        }
        return newPlayers;
    }

    private void validateDeckForSetup() {
        int defuses = deck.count(CardType.DEFUSE);
        int kittens = deck.count(CardType.EXPLODING_KITTEN);
        int ordinaryCards = deck.size() - defuses - kittens;
        int extraDefuses = Math.min(MAX_EXTRA_DEFUSES, defuses - players.size());
        int drawableCards = ordinaryCards + extraDefuses;
        if (defuses < players.size() || kittens < players.size() - 1
                || drawableCards < INITIAL_HAND_SIZE * players.size()) {
            throw new IllegalStateException(INSUFFICIENT_DECK_MESSAGE);
        }
    }

    private int removeSpecialCards() {
        deck.removeAll(CardType.EXPLODING_KITTEN);
        return deck.removeAll(CardType.DEFUSE);
    }

    private void giveGuaranteedDefuses() {
        for (Player player : players) {
            player.addToHand(CardType.DEFUSE);
        }
    }

    private void returnExtraDefuses(int removedDefuses) {
        int extraDefuses = Math.min(MAX_EXTRA_DEFUSES, removedDefuses - players.size());
        deck.add(CardType.DEFUSE, extraDefuses);
    }

    private void dealInitialCards() {
        for (Player player : players) {
            for (int i = 0; i < INITIAL_HAND_SIZE; i++) {
                player.addToHand(deck.draw());
            }
        }
    }

    private void returnExplodingKittens() {
        deck.add(CardType.EXPLODING_KITTEN, players.size() - 1);
    }

    private void requireSetUp() {
        if (!isSetUp) {
            throw new IllegalStateException(NOT_SET_UP_MESSAGE);
        }
    }
}
