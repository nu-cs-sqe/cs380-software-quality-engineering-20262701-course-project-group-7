package domain;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {

    private final String name;
    private final List<CardType> hand;
    
    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }


    public String getName() {
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
