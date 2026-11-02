package domain;


import java.util.ArrayList;

public class Player {
    private final String name;
    private final ArrayList<Object> hand;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }


    public String getName() {
        return name;
    }
}
