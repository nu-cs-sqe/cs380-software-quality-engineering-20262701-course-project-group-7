package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeckTest {

    private Random mockRandom;

    @BeforeEach
    public void setUp() {
        mockRandom = EasyMock.createMock(Random.class);
    }

    // BVA Method 1, TC1
    @Test
    public void constructor_nullRandom_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Deck(null));
    }

    // BVA Method 1, TC2
    @Test
    public void constructor_validRandom_createsEmptyDeck() {
        Deck deck = createDeckWith();

        assertEquals(0, deck.size());
    }

    // BVA Method 2, TC2
    @Test
    public void size_oneCard_returnsOne() {
        Deck deck = createDeckWith(CardType.ATTACK);

        assertEquals(1, deck.size());
    }

    // BVA Method 2, TC3
    @Test
    public void size_threeCards_returnsThree() {
        Deck deck = createDeckWith(CardType.ATTACK, CardType.SKIP, CardType.FAVOR);

        assertEquals(3, deck.size());
    }

    // BVA Method 3, TC1
    @Test
    public void add_nullType_throwsIllegalArgumentException() {
        Deck deck = createDeckWith();

        assertThrows(IllegalArgumentException.class, () -> deck.add(null, 1));
        assertEquals(0, deck.size());
    }

    // BVA Method 3, TC3
    @Test
    public void add_zeroCards_leavesDeckEmpty() {
        Deck deck = createDeckWith();

        deck.add(CardType.ATTACK, 0);

        assertEquals(0, deck.size());
    }

    // BVA Method 4, TC1
    @Test
    public void draw_emptyDeck_throwsIllegalStateException() {
        Deck deck = createDeckWith();

        assertThrows(IllegalStateException.class, deck::draw);
    }

    // BVA Method 4, TC2
    @Test
    public void draw_oneCard_returnsItAndEmptiesDeck() {
        Deck deck = createDeckWith(CardType.NOPE);

        assertEquals(CardType.NOPE, deck.draw());
        assertEquals(0, deck.size());
    }

    // BVA Method 4, TC3
    @Test
    public void draw_threeCards_returnsTopCard() {
        Deck deck = createDeckWith(CardType.SKIP, CardType.ATTACK, CardType.FAVOR);

        assertEquals(CardType.SKIP, deck.draw());
        assertEquals(2, deck.size());
    }

    // BVA Method 4, TC4
    @Test
    public void draw_untilEmpty_returnsCardsInOrderThenThrows() {
        Deck deck = createDeckWith(CardType.SKIP, CardType.ATTACK, CardType.FAVOR);

        assertEquals(CardType.SKIP, deck.draw());
        assertEquals(CardType.ATTACK, deck.draw());
        assertEquals(CardType.FAVOR, deck.draw());
        assertThrows(IllegalStateException.class, deck::draw);
    }

    // BVA Method 4, TC7
    @Test
    public void draw_oneOfEachType_returnsEveryTypeInOrder() {
        Deck deck = createDeckWith(CardType.values());

        for (CardType expected : CardType.values()) {
            assertEquals(expected, deck.draw());
        }
    }

    // BVA Method 3, TC4
    @Test
    public void add_oneCardToEmptyDeck_drawReturnsThatCard() {
        Deck deck = createDeckWith();

        deck.add(CardType.ATTACK, 1);

        assertEquals(1, deck.size());
        assertEquals(CardType.ATTACK, deck.draw());
    }

    // BVA Method 3, TC5
    @Test
    public void add_threeCardsToNonEmptyDeck_addsThemToTheBottom() {
        Deck deck = createDeckWith(CardType.SKIP);

        deck.add(CardType.DEFUSE, 3);

        assertEquals(4, deck.size());
        assertEquals(CardType.SKIP, deck.draw());
        assertEquals(CardType.DEFUSE, deck.draw());
        assertEquals(CardType.DEFUSE, deck.draw());
        assertEquals(CardType.DEFUSE, deck.draw());
    }

    // BVA Method 3, TC2
    @Test
    public void add_negativeNumberOfCards_throwsAndLeavesDeckUnchanged() {
        Deck deck = createDeckWith(CardType.SKIP);

        assertThrows(IllegalArgumentException.class, () -> deck.add(CardType.ATTACK, -1));
        assertEquals(1, deck.size());
        assertEquals(CardType.SKIP, deck.draw());
    }

    // BVA Method 5, TC1
    @Test
    public void count_nullType_throwsIllegalArgumentException() {
        Deck deck = createDeckWith(CardType.ATTACK);

        assertThrows(IllegalArgumentException.class, () -> deck.count(null));
    }

    // BVA Method 5, TC2
    @Test
    public void count_emptyDeck_returnsZero() {
        Deck deck = createDeckWith();

        assertEquals(0, deck.count(CardType.DEFUSE));
    }

    // BVA Method 5, TC3
    @Test
    public void count_oneMatchingCard_returnsOne() {
        Deck deck = createDeckWith(CardType.DEFUSE);

        assertEquals(1, deck.count(CardType.DEFUSE));
    }

    // BVA Method 5, TC4
    @Test
    public void count_noMatchingCard_returnsZero() {
        Deck deck = createDeckWith(CardType.ATTACK, CardType.SKIP);

        assertEquals(0, deck.count(CardType.DEFUSE));
    }

    // BVA Method 5, TC5
    @Test
    public void count_matchOnTop_returnsOne() {
        Deck deck = createDeckWith(CardType.DEFUSE, CardType.ATTACK, CardType.SKIP);

        assertEquals(1, deck.count(CardType.DEFUSE));
    }

    // BVA Method 5, TC6
    @Test
    public void count_matchAtBottom_returnsOne() {
        Deck deck = createDeckWith(CardType.ATTACK, CardType.SKIP, CardType.DEFUSE);

        assertEquals(1, deck.count(CardType.DEFUSE));
    }

    // BVA Method 5, TC7
    @Test
    public void count_twoMatchingCards_returnsTwo() {
        Deck deck = createDeckWith(CardType.DEFUSE, CardType.ATTACK, CardType.DEFUSE);

        assertEquals(2, deck.count(CardType.DEFUSE));
    }

    // BVA Method 5, TC8
    @Test
    public void count_everyCardMatches_returnsDeckSize() {
        Deck deck = createDeckWith(CardType.DEFUSE, CardType.DEFUSE, CardType.DEFUSE);

        assertEquals(3, deck.count(CardType.DEFUSE));
    }

    // BVA Method 5, TC9
    @Test
    public void count_explodingKittenOnTop_returnsOne() {
        Deck deck = createDeckWith(CardType.EXPLODING_KITTEN, CardType.DEFUSE);

        assertEquals(1, deck.count(CardType.EXPLODING_KITTEN));
    }

    // BVA Method 5, TC10
    @Test
    public void count_twoDefusesAtBottom_returnsTwo() {
        Deck deck = createDeckWith(CardType.ATTACK, CardType.DEFUSE, CardType.DEFUSE);

        assertEquals(2, deck.count(CardType.DEFUSE));
    }

    // BVA Method 5, TC11
    @Test
    public void count_onlyCardIsAttack_returnsOne() {
        Deck deck = createDeckWith(CardType.ATTACK);

        assertEquals(1, deck.count(CardType.ATTACK));
    }

    // BVA Method 5, TC12
    @Test
    public void count_threeSkips_returnsThree() {
        Deck deck = createDeckWith(CardType.SKIP, CardType.NOPE, CardType.SKIP, CardType.SKIP);

        assertEquals(3, deck.count(CardType.SKIP));
    }

    // BVA Method 5, TC13
    @Test
    public void count_noFavor_returnsZero() {
        Deck deck = createDeckWith(CardType.ATTACK, CardType.SKIP);

        assertEquals(0, deck.count(CardType.FAVOR));
    }

    // BVA Method 5, TC14
    @Test
    public void count_shuffleAtBottom_returnsOne() {
        Deck deck = createDeckWith(CardType.NOPE, CardType.SHUFFLE);

        assertEquals(1, deck.count(CardType.SHUFFLE));
    }

    // BVA Method 5, TC15
    @Test
    public void count_onlySeeTheFutures_returnsTwo() {
        Deck deck = createDeckWith(CardType.SEE_THE_FUTURE, CardType.SEE_THE_FUTURE);

        assertEquals(2, deck.count(CardType.SEE_THE_FUTURE));
    }

    // BVA Method 5, TC16
    @Test
    public void count_noNopeInEmptyDeck_returnsZero() {
        Deck deck = createDeckWith();

        assertEquals(0, deck.count(CardType.NOPE));
    }

    // BVA Method 5, TC17
    @Test
    public void count_twoTacocats_returnsTwo() {
        Deck deck = createDeckWith(CardType.TACOCAT, CardType.CATTERMELON, CardType.TACOCAT);

        assertEquals(2, deck.count(CardType.TACOCAT));
    }

    // BVA Method 5, TC18
    @Test
    public void count_cattermelonAtBottom_returnsOne() {
        Deck deck = createDeckWith(CardType.TACOCAT, CardType.CATTERMELON);

        assertEquals(1, deck.count(CardType.CATTERMELON));
    }

    // BVA Method 5, TC19
    @Test
    public void count_hairyPotatoCatInMiddle_returnsOne() {
        Deck deck = createDeckWith(CardType.BEARD_CAT, CardType.HAIRY_POTATO_CAT, CardType.BEARD_CAT);

        assertEquals(1, deck.count(CardType.HAIRY_POTATO_CAT));
    }

    // BVA Method 5, TC21
    @Test
    public void count_oneOfEachType_returnsOneRainbowRalphingCat() {
        Deck deck = createDeckWith(CardType.values());

        assertEquals(1, deck.count(CardType.RAINBOW_RALPHING_CAT));
    }

    // BVA Method 3, TC6
    @Test
    public void add_officialCountOfExplodingKittenToEmptyDeck_addsAllCards() {
        Deck deck = createDeckWith();

        deck.add(CardType.EXPLODING_KITTEN, 4);

        assertEquals(4, deck.size());
        assertEquals(4, deck.count(CardType.EXPLODING_KITTEN));
    }

    // BVA Method 3, TC7
    @Test
    public void add_officialCountOfDefuseToNonEmptyDeck_addsAllCards() {
        Deck deck = createDeckWith(CardType.ATTACK);

        deck.add(CardType.DEFUSE, 6);

        assertEquals(7, deck.size());
        assertEquals(6, deck.count(CardType.DEFUSE));
        assertEquals(CardType.ATTACK, deck.draw());
    }

    // BVA Method 3, TC8
    @Test
    public void add_officialCountOfAttackToNonEmptyDeck_addsAllCards() {
        Deck deck = createDeckWith(CardType.SKIP, CardType.FAVOR);

        deck.add(CardType.ATTACK, 4);

        assertEquals(6, deck.size());
        assertEquals(4, deck.count(CardType.ATTACK));
        assertEquals(CardType.SKIP, deck.draw());
    }

    // BVA Method 3, TC9
    @Test
    public void add_officialCountOfSkipToEmptyDeck_addsAllCards() {
        Deck deck = createDeckWith();

        deck.add(CardType.SKIP, 4);

        assertEquals(4, deck.size());
        assertEquals(4, deck.count(CardType.SKIP));
    }

    // BVA Method 3, TC10
    @Test
    public void add_officialCountOfFavorToNonEmptyDeck_addsAllCards() {
        Deck deck = createDeckWith(CardType.NOPE);

        deck.add(CardType.FAVOR, 4);

        assertEquals(5, deck.size());
        assertEquals(4, deck.count(CardType.FAVOR));
        assertEquals(CardType.NOPE, deck.draw());
    }

    // BVA Method 3, TC11
    @Test
    public void add_officialCountOfShuffleToNonEmptyDeck_addsAllCards() {
        Deck deck = createDeckWith(CardType.DEFUSE, CardType.DEFUSE);

        deck.add(CardType.SHUFFLE, 4);

        assertEquals(6, deck.size());
        assertEquals(4, deck.count(CardType.SHUFFLE));
        assertEquals(CardType.DEFUSE, deck.draw());
    }

    // BVA Method 3, TC12
    @Test
    public void add_officialCountOfSeeTheFutureToEmptyDeck_addsAllCards() {
        Deck deck = createDeckWith();

        deck.add(CardType.SEE_THE_FUTURE, 5);

        assertEquals(5, deck.size());
        assertEquals(5, deck.count(CardType.SEE_THE_FUTURE));
    }

    // BVA Method 3, TC13
    @Test
    public void add_officialCountOfNopeToNonEmptyDeck_addsAllCards() {
        Deck deck = createDeckWith(CardType.TACOCAT);

        deck.add(CardType.NOPE, 5);

        assertEquals(6, deck.size());
        assertEquals(5, deck.count(CardType.NOPE));
        assertEquals(CardType.TACOCAT, deck.draw());
    }

    // BVA Method 3, TC14
    @Test
    public void add_officialCountOfTacocatToNonEmptyDeck_addsAllCards() {
        Deck deck = createDeckWith(CardType.ATTACK, CardType.SKIP, CardType.FAVOR);

        deck.add(CardType.TACOCAT, 4);

        assertEquals(7, deck.size());
        assertEquals(4, deck.count(CardType.TACOCAT));
        assertEquals(CardType.ATTACK, deck.draw());
    }

    // BVA Method 3, TC15
    @Test
    public void add_officialCountOfCattermelonToEmptyDeck_addsAllCards() {
        Deck deck = createDeckWith();

        deck.add(CardType.CATTERMELON, 4);

        assertEquals(4, deck.size());
        assertEquals(4, deck.count(CardType.CATTERMELON));
    }

    // BVA Method 3, TC16
    @Test
    public void add_officialCountOfHairyPotatoCatToNonEmptyDeck_addsAllCards() {
        Deck deck = createDeckWith(CardType.BEARD_CAT);

        deck.add(CardType.HAIRY_POTATO_CAT, 4);

        assertEquals(5, deck.size());
        assertEquals(4, deck.count(CardType.HAIRY_POTATO_CAT));
        assertEquals(CardType.BEARD_CAT, deck.draw());
    }

    // BVA Method 3, TC17
    @Test
    public void add_officialCountOfBeardCatToNonEmptyDeck_addsAllCards() {
        Deck deck = createDeckWith(CardType.NOPE, CardType.NOPE);

        deck.add(CardType.BEARD_CAT, 4);

        assertEquals(6, deck.size());
        assertEquals(4, deck.count(CardType.BEARD_CAT));
        assertEquals(CardType.NOPE, deck.draw());
    }

    // BVA Method 3, TC18
    @Test
    public void add_officialCountOfRainbowRalphingCatToNonEmptyDeck_addsAllCards() {
        Deck deck = createDeckWith(CardType.SHUFFLE);

        deck.add(CardType.RAINBOW_RALPHING_CAT, 4);

        assertEquals(5, deck.size());
        assertEquals(4, deck.count(CardType.RAINBOW_RALPHING_CAT));
        assertEquals(CardType.SHUFFLE, deck.draw());
    }

    // BVA Method 3, TC19
    @Test
    public void add_cardTypeAlreadyInDeck_countsAllCopies() {
        Deck deck = createDeckWith(CardType.DEFUSE, CardType.DEFUSE);

        deck.add(CardType.DEFUSE, 1);

        assertEquals(3, deck.size());
        assertEquals(3, deck.count(CardType.DEFUSE));
    }

    // BVA Method 4, TC6
    @Test
    public void draw_duplicateTopCards_removesOnlyOneCopy() {
        Deck deck = createDeckWith(CardType.SKIP, CardType.SKIP, CardType.ATTACK);

        assertEquals(CardType.SKIP, deck.draw());
        assertEquals(2, deck.size());
        assertEquals(1, deck.count(CardType.SKIP));
    }

    // BVA Method 6, TC1
    @Test
    public void removeAll_nullType_throwsAndLeavesDeckUnchanged() {
        Deck deck = createDeckWith(CardType.ATTACK);

        assertThrows(IllegalArgumentException.class, () -> deck.removeAll(null));
        assertEquals(1, deck.size());
    }

    // BVA Method 6, TC2
    @Test
    public void removeAll_emptyDeck_returnsZero() {
        Deck deck = createDeckWith();

        assertEquals(0, deck.removeAll(CardType.DEFUSE));
        assertEquals(0, deck.size());
    }

    // BVA Method 6, TC3
    @Test
    public void removeAll_onlyCardMatches_emptiesDeck() {
        Deck deck = createDeckWith(CardType.DEFUSE);

        assertEquals(1, deck.removeAll(CardType.DEFUSE));
        assertEquals(0, deck.size());
    }

    // BVA Method 6, TC4
    @Test
    public void removeAll_noMatch_leavesDeckUnchanged() {
        Deck deck = createDeckWith(CardType.ATTACK, CardType.SKIP);

        assertEquals(0, deck.removeAll(CardType.DEFUSE));
        assertEquals(2, deck.size());
        assertEquals(CardType.ATTACK, deck.draw());
        assertEquals(CardType.SKIP, deck.draw());
    }

    // BVA Method 6, TC5
    @Test
    public void removeAll_matchOnTop_removesIt() {
        Deck deck = createDeckWith(CardType.DEFUSE, CardType.ATTACK, CardType.SKIP);

        assertEquals(1, deck.removeAll(CardType.DEFUSE));
        assertEquals(2, deck.size());
        assertEquals(CardType.ATTACK, deck.draw());
        assertEquals(CardType.SKIP, deck.draw());
    }

    // BVA Method 6, TC6
    @Test
    public void removeAll_matchAtBottom_removesIt() {
        Deck deck = createDeckWith(CardType.ATTACK, CardType.SKIP, CardType.DEFUSE);

        assertEquals(1, deck.removeAll(CardType.DEFUSE));
        assertEquals(2, deck.size());
        assertEquals(CardType.ATTACK, deck.draw());
        assertEquals(CardType.SKIP, deck.draw());
    }

    // BVA Method 6, TC7
    @Test
    public void removeAll_scatteredMatches_removesAllAndKeepsOrder() {
        Deck deck = createDeckWith(CardType.DEFUSE, CardType.ATTACK, CardType.DEFUSE, CardType.SKIP, CardType.DEFUSE);

        assertEquals(3, deck.removeAll(CardType.DEFUSE));
        assertEquals(2, deck.size());
        assertEquals(CardType.ATTACK, deck.draw());
        assertEquals(CardType.SKIP, deck.draw());
    }

    // BVA Method 6, TC8
    @Test
    public void removeAll_adjacentMatches_removesBoth() {
        Deck deck = createDeckWith(CardType.ATTACK, CardType.DEFUSE, CardType.DEFUSE, CardType.SKIP);

        assertEquals(2, deck.removeAll(CardType.DEFUSE));
        assertEquals(2, deck.size());
        assertEquals(CardType.ATTACK, deck.draw());
        assertEquals(CardType.SKIP, deck.draw());
    }

    // BVA Method 6, TC9
    @Test
    public void removeAll_allButOneMatch_leavesOneCard() {
        Deck deck = createDeckWith(CardType.DEFUSE, CardType.DEFUSE, CardType.ATTACK);

        assertEquals(2, deck.removeAll(CardType.DEFUSE));
        assertEquals(1, deck.size());
        assertEquals(CardType.ATTACK, deck.draw());
    }

    // BVA Method 6, TC10
    @Test
    public void removeAll_everyCardMatches_emptiesDeck() {
        Deck deck = createDeckWith(CardType.DEFUSE, CardType.DEFUSE);

        assertEquals(2, deck.removeAll(CardType.DEFUSE));
        assertEquals(0, deck.size());
    }

    // BVA Method 6, TC11
    @Test
    public void removeAll_explodingKitten_removesEveryCopy() {
        Deck deck = createDeckWith(CardType.EXPLODING_KITTEN, CardType.DEFUSE);

        assertEquals(1, deck.removeAll(CardType.EXPLODING_KITTEN));
        assertEquals(1, deck.size());
        assertEquals(CardType.DEFUSE, deck.draw());
    }

    // BVA Method 6, TC12
    @Test
    public void removeAll_defuse_removesEveryCopy() {
        Deck deck = createDeckWith(CardType.DEFUSE, CardType.ATTACK, CardType.DEFUSE);

        assertEquals(2, deck.removeAll(CardType.DEFUSE));
        assertEquals(1, deck.size());
        assertEquals(CardType.ATTACK, deck.draw());
    }

    // BVA Method 6, TC13
    @Test
    public void removeAll_attack_removesEveryCopy() {
        Deck deck = createDeckWith(CardType.ATTACK);

        assertEquals(1, deck.removeAll(CardType.ATTACK));
        assertEquals(0, deck.size());
    }

    // BVA Method 6, TC14
    @Test
    public void removeAll_skip_removesEveryCopy() {
        Deck deck = createDeckWith(CardType.SKIP, CardType.NOPE, CardType.SKIP, CardType.SKIP);

        assertEquals(3, deck.removeAll(CardType.SKIP));
        assertEquals(1, deck.size());
        assertEquals(CardType.NOPE, deck.draw());
    }

    // BVA Method 6, TC15
    @Test
    public void removeAll_favor_removesEveryCopy() {
        Deck deck = createDeckWith(CardType.ATTACK, CardType.SKIP);

        assertEquals(0, deck.removeAll(CardType.FAVOR));
        assertEquals(2, deck.size());
        assertEquals(CardType.ATTACK, deck.draw());
        assertEquals(CardType.SKIP, deck.draw());
    }

    // BVA Method 6, TC16
    @Test
    public void removeAll_shuffle_removesEveryCopy() {
        Deck deck = createDeckWith(CardType.NOPE, CardType.SHUFFLE);

        assertEquals(1, deck.removeAll(CardType.SHUFFLE));
        assertEquals(1, deck.size());
        assertEquals(CardType.NOPE, deck.draw());
    }

    // BVA Method 6, TC17
    @Test
    public void removeAll_seeTheFuture_removesEveryCopy() {
        Deck deck = createDeckWith(CardType.SEE_THE_FUTURE, CardType.SEE_THE_FUTURE);

        assertEquals(2, deck.removeAll(CardType.SEE_THE_FUTURE));
        assertEquals(0, deck.size());
    }

    // BVA Method 6, TC18
    @Test
    public void removeAll_nope_removesEveryCopy() {
        Deck deck = createDeckWith();

        assertEquals(0, deck.removeAll(CardType.NOPE));
        assertEquals(0, deck.size());
    }

    // BVA Method 6, TC19
    @Test
    public void removeAll_tacocat_removesEveryCopy() {
        Deck deck = createDeckWith(CardType.TACOCAT, CardType.CATTERMELON, CardType.TACOCAT);

        assertEquals(2, deck.removeAll(CardType.TACOCAT));
        assertEquals(1, deck.size());
        assertEquals(CardType.CATTERMELON, deck.draw());
    }

    // BVA Method 6, TC20
    @Test
    public void removeAll_cattermelon_removesEveryCopy() {
        Deck deck = createDeckWith(CardType.TACOCAT, CardType.CATTERMELON, CardType.BEARD_CAT);

        assertEquals(1, deck.removeAll(CardType.CATTERMELON));
        assertEquals(2, deck.size());
        assertEquals(CardType.TACOCAT, deck.draw());
        assertEquals(CardType.BEARD_CAT, deck.draw());
    }

    // BVA Method 6, TC21
    @Test
    public void removeAll_hairyPotatoCat_removesEveryCopy() {
        Deck deck = createDeckWith(CardType.HAIRY_POTATO_CAT, CardType.BEARD_CAT, CardType.HAIRY_POTATO_CAT, CardType.HAIRY_POTATO_CAT);

        assertEquals(3, deck.removeAll(CardType.HAIRY_POTATO_CAT));
        assertEquals(1, deck.size());
        assertEquals(CardType.BEARD_CAT, deck.draw());
    }

    // BVA Method 6, TC23
    @Test
    public void removeAll_oneOfEachType_removesOnlyRainbowRalphingCat() {
        Deck deck = createDeckWith(CardType.values());

        assertEquals(1, deck.removeAll(CardType.RAINBOW_RALPHING_CAT));
        assertEquals(12, deck.size());
        assertEquals(0, deck.count(CardType.RAINBOW_RALPHING_CAT));
    }

    // BVA Method 7, TC1
    @Test
    public void createStandardDeck_nullRandom_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Deck.createStandardDeck(null));
    }

    // BVA Method 7, TC2
    @Test
    public void createStandardDeck_validRandom_creates56CardsWithoutShuffling() {
        Deck deck = createStandardDeck();

        assertEquals(56, deck.size());
    }

    // BVA Method 7, TC3
    @Test
    public void createStandardDeck_validRandom_contains4ExplodingKittenCards() {
        Deck deck = createStandardDeck();

        assertEquals(4, deck.count(CardType.EXPLODING_KITTEN));
    }

    // BVA Method 7, TC4
    @Test
    public void createStandardDeck_validRandom_contains6DefuseCards() {
        Deck deck = createStandardDeck();

        assertEquals(6, deck.count(CardType.DEFUSE));
    }

    // BVA Method 7, TC5
    @Test
    public void createStandardDeck_validRandom_contains4AttackCards() {
        Deck deck = createStandardDeck();

        assertEquals(4, deck.count(CardType.ATTACK));
    }

    // BVA Method 7, TC6
    @Test
    public void createStandardDeck_validRandom_contains4SkipCards() {
        Deck deck = createStandardDeck();

        assertEquals(4, deck.count(CardType.SKIP));
    }

    // BVA Method 7, TC7
    @Test
    public void createStandardDeck_validRandom_contains4FavorCards() {
        Deck deck = createStandardDeck();

        assertEquals(4, deck.count(CardType.FAVOR));
    }

    // BVA Method 7, TC8
    @Test
    public void createStandardDeck_validRandom_contains4ShuffleCards() {
        Deck deck = createStandardDeck();

        assertEquals(4, deck.count(CardType.SHUFFLE));
    }

    // BVA Method 7, TC9
    @Test
    public void createStandardDeck_validRandom_contains5SeeTheFutureCards() {
        Deck deck = createStandardDeck();

        assertEquals(5, deck.count(CardType.SEE_THE_FUTURE));
    }

    // BVA Method 7, TC10
    @Test
    public void createStandardDeck_validRandom_contains5NopeCards() {
        Deck deck = createStandardDeck();

        assertEquals(5, deck.count(CardType.NOPE));
    }

    // BVA Method 7, TC11
    @Test
    public void createStandardDeck_validRandom_contains4TacocatCards() {
        Deck deck = createStandardDeck();

        assertEquals(4, deck.count(CardType.TACOCAT));
    }

    // BVA Method 7, TC12
    @Test
    public void createStandardDeck_validRandom_contains4CattermelonCards() {
        Deck deck = createStandardDeck();

        assertEquals(4, deck.count(CardType.CATTERMELON));
    }

    // BVA Method 7, TC13
    @Test
    public void createStandardDeck_validRandom_contains4HairyPotatoCatCards() {
        Deck deck = createStandardDeck();

        assertEquals(4, deck.count(CardType.HAIRY_POTATO_CAT));
    }

    // BVA Method 7, TC14
    @Test
    public void createStandardDeck_validRandom_contains4BeardCatCards() {
        Deck deck = createStandardDeck();

        assertEquals(4, deck.count(CardType.BEARD_CAT));
    }

    // BVA Method 7, TC15
    @Test
    public void createStandardDeck_validRandom_contains4RainbowRalphingCatCards() {
        Deck deck = createStandardDeck();

        assertEquals(4, deck.count(CardType.RAINBOW_RALPHING_CAT));
    }

    // BVA Method 3, TC20
    @Test
    public void add_fillsStandardDeckBackTo56_addsToTheBottom() {
        Deck deck = createStandardDeck();
        int cardsWithoutKittens = 52;
        int kittens = 4;

        deck.removeAll(CardType.EXPLODING_KITTEN);
        deck.add(CardType.EXPLODING_KITTEN, kittens);

        assertEquals(56, deck.size());
        assertEquals(kittens, deck.count(CardType.EXPLODING_KITTEN));
        for (int i = 0; i < cardsWithoutKittens; i++) {
            deck.draw();
        }
        for (int i = 0; i < kittens; i++) {
            assertEquals(CardType.EXPLODING_KITTEN, deck.draw());
        }
    }

    // BVA Method 4, TC5
    @Test
    public void draw_standardDeck_removesOneCopyOfTheDrawnType() {
        Deck deck = createStandardDeck();
        Deck untouchedDeck = Deck.createStandardDeck(mockRandom);

        CardType drawn = deck.draw();

        assertEquals(55, deck.size());
        assertEquals(untouchedDeck.count(drawn) - 1, deck.count(drawn));
    }

    // BVA Method 5, TC20
    @Test
    public void count_standardDeck_returnsFourBeardCats() {
        Deck deck = createStandardDeck();

        assertEquals(4, deck.count(CardType.BEARD_CAT));
    }

    // BVA Method 6, TC22
    @Test
    public void removeAll_standardDeck_removesAllFourBeardCats() {
        Deck deck = createStandardDeck();

        assertEquals(4, deck.removeAll(CardType.BEARD_CAT));
        assertEquals(52, deck.size());
        assertEquals(0, deck.count(CardType.BEARD_CAT));
    }

    private Deck createDeckWith(CardType... cardsFromTop) {
        EasyMock.replay(mockRandom);
        Deck deck = new Deck(mockRandom);
        for (CardType card : cardsFromTop) {
            deck.add(card, 1);
        }
        return deck;
    }

    private Deck createStandardDeck() {
        EasyMock.replay(mockRandom);
        return Deck.createStandardDeck(mockRandom);
    }
}
