package domain;


import java.util.ArrayList;

public class Player {
    private static final String NULL_NAME_KEY = "player.constructor.nullName";
    private static final String EMPTY_NAME_KEY = "player.constructor.emptyName";
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
        hand.add(card);
    }

    public int getHandSize(){
        return hand.size();
    }
}
