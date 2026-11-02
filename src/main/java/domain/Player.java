package domain;


import java.util.ArrayList;
import java.util.Collections;

public class Player {
    private static final String NULL_NAME_KEY = "player.constructor.nullName";
    private static final String EMPTY_NAME_KEY = "player.constructor.emptyName";
    private static final String NULL_CARD_KEY = "player.addToHand.nullCard";
    private static final String NULL_TYPE_KEY = "player.countInHand.nullType";
    private final String name;
    private final ArrayList<Object> hand;
    
    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }


    public String getName() {
        if (name == null){
            throw new IllegalArgumentException(NULL_NAME_KEY);
        }
        if (name == ""){
            throw new IllegalArgumentException(EMPTY_NAME_KEY);
        }
        return name;
    }

    public void addToHand(CardType card){
        if(card == null){
            throw new IllegalArgumentException(NULL_CARD_KEY);
        }

        hand.add(card);
    }

    public int getHandSize(){
        return hand.size();
    }

    public int countInHand(CardType type){
        if (type == null){
            throw new IllegalArgumentException(NULL_TYPE_KEY);
        }
        int count = Collections.frequency(hand, type);
        return count;
    }
}
