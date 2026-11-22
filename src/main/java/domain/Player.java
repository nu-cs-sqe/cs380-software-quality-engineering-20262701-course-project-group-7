package domain;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {
    private static final String EMPTY_NAME_KEY = "player.constructor.emptyName";

    private final String name;
    private final ArrayList<CardType> hand;
    
    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }


    public String getName() {

        if (name.equals("")) {
            throw new IllegalArgumentException(EMPTY_NAME_KEY);
        }
        return name;
    }

    public void addToHand(CardType card) {

        hand.add(card);
    }

    public int getHandSize() {
        return hand.size();
    }

    public int countInHand(CardType type) {

        int count = Collections.frequency(hand, type);
        return count;
    }

    public List<CardType> getHand() {
        return new ArrayList<>(hand);
    }
}
