package domain;

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
    void getNullName_throwIllegalArgumentException() {
        Player player = new Player(null);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    player.getName();
                });
        assertEquals("player.constructor.nullName", ex.getMessage());
    }

    @Test
    void getEmptyName_throwIllegalArgumentException() {
        Player player = new Player("");
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    player.getName();
                });
        assertEquals("player.constructor.emptyName", ex.getMessage());
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
    void addNullCard_throwIllegalArgumentException() {
        Player player = new Player("Alice");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    player.addToHand(null);
                });
        assertEquals("player.addToHand.nullCard", ex.getMessage());
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

}
