package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class PlayerTest {
    @Test
    void getPlayerName_returnsAlice() {
        Player player = new Player("Alice");
        assertSame("Alice", player.getName());
    }
}
