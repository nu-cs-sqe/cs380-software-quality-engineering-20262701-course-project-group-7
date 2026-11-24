package domain;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import org.junit.jupiter.api.Test;


public class PlayerTest {

    @Test
    void addDefuseOnEmptyHand_sizeOne() {
        Player player = new Player("Alice");
        player.addToHand(CardType.DEFUSE);

        assertEquals(1, player.getHandSize());
        assertEquals(CardType.DEFUSE, player.getHand().get(0));
    }

    @Test
    void addTwoCardsOnEmptyHand_returnTwo() {
        Player player = new Player("Alice");
        player.addToHand(CardType.DEFUSE);
        player.addToHand(CardType.ATTACK);

        assertEquals(2, player.getHandSize());
        assertEquals(List.of(CardType.DEFUSE, CardType.ATTACK), player.getHand());

    }

    @Test
    void addDuplicateCardsOnEmptyHand_returnTwo() {
        Player player = new Player("Alice");
        player.addToHand(CardType.DEFUSE);
        player.addToHand(CardType.DEFUSE);

        assertEquals(2, player.getHandSize());
        assertEquals(2, player.countInHand(CardType.DEFUSE));
    }



    @Test
    void countEmptyHand_returnZero() {
        Player player = new Player("Alice");

        assertEquals(0, player.countInHand(CardType.DEFUSE));
    }

    @Test
    void countDefuseOnAttackHand_returnZero() {
        Player player = new Player("Alice");
        player.addToHand(CardType.ATTACK);

        assertEquals(0, player.countInHand(CardType.DEFUSE));
    }

    @Test
    void countDuplicateDefuse_returnTwo() {
        Player player = new Player("Alice");
        player.addToHand(CardType.DEFUSE);
        player.addToHand(CardType.ATTACK);
        player.addToHand(CardType.DEFUSE);

        assertEquals(2, player.countInHand(CardType.DEFUSE));
        assertEquals(List.of(CardType.DEFUSE, CardType.ATTACK, CardType.DEFUSE), player.getHand());
    }

    @Test
    void countWholeHand_returnTwo() {
        Player player = new Player("Alice");
        player.addToHand(CardType.ATTACK);
        player.addToHand(CardType.ATTACK);

        assertEquals(2, player.countInHand(CardType.ATTACK));
        assertEquals(List.of(CardType.ATTACK, CardType.ATTACK), player.getHand());
    }


    @Test
    void getHandOnNewPlayer_returnZero() {
        Player player = new Player("Alice");

        assertEquals(0, player.getHand().size());
    }

    @Test
    void getHandOnTwoCardHand_returnHand() {
        Player player = new Player("Alice");

        player.addToHand(CardType.ATTACK);
        player.addToHand(CardType.DEFUSE);
        List<CardType> result = player.getHand();
        assertEquals(List.of(CardType.ATTACK, CardType.DEFUSE), result);

        result.add(CardType.DEFUSE);
        assertEquals(2, player.getHandSize());
    }


}
