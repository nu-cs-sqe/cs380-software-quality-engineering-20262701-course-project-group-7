package domain;


import java.util.ArrayList;

public class Player {
    private static final String NULL_NAME_KEY = "player.constructor.nullName";
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
        return name;
    }
}
