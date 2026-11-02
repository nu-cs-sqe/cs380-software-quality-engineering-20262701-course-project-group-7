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

}
