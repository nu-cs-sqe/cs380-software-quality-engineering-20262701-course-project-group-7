package domain;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class PlayerTest {
    @Test
    void getPlayerName_returnsAlice() {
        Player player = new Player("Alice");
        assertSame("Alice", player.getName());
    }

    @Test
    void getPlayerName_returnsA() {
        Player player = new Player("A");
        assertSame("A", player.getName());
    }



    @Test
    void addDefuseOnEmptyHand_sizeOne() {
        Player player = new Player("Alice");
        player.addToHand(CardType.DEFUSE);

        assertEquals(1, player.getHandSize());
    }

    @Test
    void addTwoCardsOnEmptyHand_returnTwo() {
        Player player = new Player("Alice");
        player.addToHand(CardType.DEFUSE);
        player.addToHand(CardType.ATTACK);

        assertEquals(2, player.getHandSize());
    }

    @Test
    void addDuplicateCardsOnEmptyHand_returnTwo() {
        Player player = new Player("Alice");
        player.addToHand(CardType.DEFUSE);
        player.addToHand(CardType.DEFUSE);

        assertEquals(2, player.getHandSize());
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
    }

    @Test
    void countWholeHand_returnTwo() {
        Player player = new Player("Alice");
        player.addToHand(CardType.ATTACK);
        player.addToHand(CardType.ATTACK);

        assertEquals(2, player.countInHand(CardType.ATTACK));
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
