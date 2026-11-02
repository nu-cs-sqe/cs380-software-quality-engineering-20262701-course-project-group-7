package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CardTypeTest {

    private static final int ORIGINAL_EDITION_CARD_TYPE_COUNT = 13;

    @Test
    public void values_hasOneConstantPerOriginalEditionCardType() {
        assertEquals(ORIGINAL_EDITION_CARD_TYPE_COUNT, CardType.values().length);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "EXPLODING_KITTEN",
            "DEFUSE",
            "ATTACK",
            "SKIP",
            "FAVOR",
            "SHUFFLE",
            "SEE_THE_FUTURE",
            "NOPE",
            "TACOCAT",
            "CATTERMELON",
            "HAIRY_POTATO_CAT",
            "BEARD_CAT",
            "RAINBOW_RALPHING_CAT"
    })
    public void valueOf_returnsConstantWithMatchingName(String name) {
        assertEquals(name, CardType.valueOf(name).name());
    }
}
