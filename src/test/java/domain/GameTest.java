package domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {

    @Test
    void constructorRejectsNullDeck() {
        assertThrows(IllegalArgumentException.class, () -> new Game(null));
    }

    @Test
    void constructorAcceptsDeckWithoutSettingUpOrDrawingCards() {
        Deck deck = Deck.createStandardDeck(new Random(0));
        int initialDeckSize = deck.size();

        Game game = assertDoesNotThrow(() -> new Game(deck));

        assertFalse(game.isSetUp());
        assertEquals(initialDeckSize, deck.size());
    }

    @Test
    void isSetUpRemainsFalseBeforeSetup() {
        Deck deck = Deck.createStandardDeck(new Random(0));
        Game game = new Game(deck);

        assertFalse(game.isSetUp());
        assertFalse(game.isSetUp());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5})
    void setupFollowsOfficialRulesForEachPlayerCount(int count) {
        Deck deck = Deck.createStandardDeck(new Random(0));
        Game game = new Game(deck);
        List<String> names = namesFor(count);

        game.setup(names);

        assertTrue(game.isSetUp());
        assertEquals(count, game.getPlayers().size());
        assertSame(game.getPlayers().get(0), game.getCurrentPlayer());
        int extraDefuses = Math.min(2, 6 - count);
        int totalDefuses = deck.count(CardType.DEFUSE);
        for (int i = 0; i < count; i++) {
            Player player = game.getPlayers().get(i);
            assertEquals(names.get(i), player.getName());
            assertEquals(8, player.getHandSize());
            assertEquals(CardType.DEFUSE, player.getHand().get(0));
            assertEquals(0, player.countInHand(CardType.EXPLODING_KITTEN));
            totalDefuses += player.countInHand(CardType.DEFUSE);
        }
        assertEquals(count + extraDefuses, totalDefuses);
        assertEquals(count - 1, deck.count(CardType.EXPLODING_KITTEN));
        assertEquals(46 + extraDefuses - 7 * count + count - 1, deck.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 6})
    void validatePlayerCountRejectsOutsideBoundaries(int count) {
        assertThrows(IllegalArgumentException.class, () -> Game.validatePlayerCount(count));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5})
    void validatePlayerCountAcceptsValidCounts(int count) {
        assertDoesNotThrow(() -> Game.validatePlayerCount(count));
    }

    @Test
    void setupRejectsNullNamesWithoutChangingDeck() {
        assertInvalidNames(null);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 6})
    void setupRejectsInvalidCountsWithoutChangingDeck(int count) {
        assertInvalidNames(namesFor(count));
    }

    @Test
    void setupRejectsNullNameWithoutChangingDeck() {
        assertInvalidNames(Arrays.asList("Alice", null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t\n"})
    void setupRejectsBlankNamesWithoutChangingDeck(String name) {
        assertInvalidNames(Arrays.asList("Alice", name));
    }

    @Test
    void setupRejectsNonInternedEmptyName() {
        assertInvalidNames(Arrays.asList("Alice", new String(new char[0])));
    }

    @Test
    void setupRejectsDuplicateNameValues() {
        assertInvalidNames(Arrays.asList("Alice", new String("Alice")));
    }

    @Test
    void setupPreservesCaseSensitiveNames() {
        Game game = new Game(Deck.createStandardDeck(new Random(0)));

        game.setup(Arrays.asList("A", "a"));

        assertEquals("A", game.getPlayers().get(0).getName());
        assertEquals("a", game.getPlayers().get(1).getName());
    }

    @Test
    void repeatedSetupPreservesExistingState() {
        Deck deck = Deck.createStandardDeck(new Random(0));
        Game game = new Game(deck);
        game.setup(namesFor(2));
        Player current = game.getCurrentPlayer();
        List<Player> players = game.getPlayers();
        List<CardType> firstHand = current.getHand();
        Map<CardType, Integer> counts = cardCounts(deck);
        int size = deck.size();

        assertThrows(IllegalStateException.class, () -> game.setup(namesFor(3)));
        assertThrows(IllegalStateException.class, () -> game.setup(null));

        assertTrue(game.isSetUp());
        assertSame(current, game.getCurrentPlayer());
        assertEquals(players, game.getPlayers());
        assertEquals(firstHand, current.getHand());
        assertEquals(counts, cardCounts(deck));
        assertEquals(size, deck.size());
    }

    @Test
    void queriesRejectAccessBeforeSetup() {
        Game game = new Game(Deck.createStandardDeck(new Random(0)));

        assertEquals("Game has not been set up.",
                assertThrows(IllegalStateException.class, game::getCurrentPlayer).getMessage());
        assertEquals("Game has not been set up.",
                assertThrows(IllegalStateException.class, game::getPlayers).getMessage());
    }

    @Test
    void playerQueriesDoNotAdvanceTurnOrExposeMutableList() {
        Game game = new Game(Deck.createStandardDeck(new Random(0)));
        game.setup(namesFor(2));
        Player first = game.getCurrentPlayer();

        assertThrows(UnsupportedOperationException.class, () -> game.getPlayers().clear());

        assertSame(first, game.getCurrentPlayer());
        assertSame(first, game.getCurrentPlayer());
        assertEquals(2, game.getPlayers().size());
    }

    @Test
    void callerCannotChangeStoredNamesAfterSetup() {
        Game game = new Game(Deck.createStandardDeck(new Random(0)));
        List<String> names = namesFor(2);
        game.setup(names);

        names.set(0, "Changed");
        names.clear();

        assertEquals("Player 1", game.getCurrentPlayer().getName());
        assertEquals(2, game.getPlayers().size());
    }

    @Test
    void validSetupCanFollowRejectedInput() {
        Game game = new Game(Deck.createStandardDeck(new Random(0)));
        assertThrows(IllegalArgumentException.class, () -> game.setup(namesFor(1)));

        game.setup(namesFor(2));

        assertTrue(game.isSetUp());
    }

    @Test
    void setupRejectsEmptyDeckBeforeMutation() {
        assertInsufficientDeck(new Deck(new Random(0)));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void setupRejectsInsufficientDefuses(int defuses) {
        assertInsufficientDeck(customDeck(14, defuses, 1));
    }

    @Test
    void setupRejectsInsufficientKittens() {
        assertInsufficientDeck(customDeck(14, 2, 0));
    }

    @Test
    void setupRejectsOneFewerDrawableCardThanRequired() {
        assertInsufficientDeck(customDeck(13, 2, 1));
    }

    @Test
    void setupAcceptsExactlyEnoughDrawableCards() {
        Deck deck = customDeck(14, 2, 1);
        Game game = new Game(deck);

        game.setup(namesFor(2));

        assertTrue(game.isSetUp());
        assertEquals(1, deck.size());
        assertEquals(CardType.EXPLODING_KITTEN, deck.draw());
        for (Player player : game.getPlayers()) {
            assertEquals(8, player.getHandSize());
            assertEquals(1, player.countInHand(CardType.DEFUSE));
        }
    }

    @Test
    void setupUsesOfficialCallOrderAndMayDealAdditionalDefuse() {
        Deck deck = mockDeckBeforeDealing();
        EasyMock.expect(deck.draw()).andReturn(CardType.DEFUSE);
        EasyMock.expect(deck.draw()).andReturn(CardType.ATTACK).times(13);
        deck.add(CardType.EXPLODING_KITTEN, 1);
        deck.shuffle();
        EasyMock.replay(deck);
        Game game = new Game(deck);

        game.setup(namesFor(2));

        assertEquals(2, game.getPlayers().get(0).countInHand(CardType.DEFUSE));
        assertEquals(8, game.getPlayers().get(0).getHandSize());
        assertEquals(8, game.getPlayers().get(1).getHandSize());
        EasyMock.verify(deck);
    }

    @Test
    void setupPropagatesUnexpectedDrawFailureWithoutPublishingPlayers() {
        Deck deck = mockDeckBeforeDealing();
        IllegalStateException failure = new IllegalStateException("Simulated draw failure");
        EasyMock.expect(deck.draw()).andThrow(failure);
        EasyMock.replay(deck);
        Game game = new Game(deck);

        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> game.setup(namesFor(2))));

        assertFalse(game.isSetUp());
        assertThrows(IllegalStateException.class, game::getCurrentPlayer);
        assertThrows(IllegalStateException.class, game::getPlayers);
        EasyMock.verify(deck);
    }

    private Deck mockDeckBeforeDealing() {
        Deck deck = EasyMock.createStrictMock(Deck.class);
        EasyMock.expect(deck.count(CardType.DEFUSE)).andReturn(6);
        EasyMock.expect(deck.count(CardType.EXPLODING_KITTEN)).andReturn(4);
        EasyMock.expect(deck.size()).andReturn(56);
        EasyMock.expect(deck.removeAll(CardType.EXPLODING_KITTEN)).andReturn(4);
        EasyMock.expect(deck.removeAll(CardType.DEFUSE)).andReturn(6);
        deck.add(CardType.DEFUSE, 2);
        deck.shuffle();
        return deck;
    }

    private void assertInvalidNames(List<String> names) {
        Deck deck = Deck.createStandardDeck(new Random(0));
        Game game = new Game(deck);
        Map<CardType, Integer> counts = cardCounts(deck);
        int size = deck.size();

        assertThrows(IllegalArgumentException.class, () -> game.setup(names));

        assertFalse(game.isSetUp());
        assertEquals(size, deck.size());
        assertEquals(counts, cardCounts(deck));
    }

    private void assertInsufficientDeck(Deck deck) {
        Game game = new Game(deck);
        Map<CardType, Integer> counts = cardCounts(deck);
        int size = deck.size();

        assertThrows(IllegalStateException.class, () -> game.setup(namesFor(2)));

        assertFalse(game.isSetUp());
        assertEquals(size, deck.size());
        assertEquals(counts, cardCounts(deck));
    }

    private Map<CardType, Integer> cardCounts(Deck deck) {
        Map<CardType, Integer> counts = new EnumMap<>(CardType.class);
        for (CardType type : CardType.values()) {
            counts.put(type, deck.count(type));
        }
        return counts;
    }

    private Deck customDeck(int ordinary, int defuses, int kittens) {
        Deck deck = new Deck(new Random(0));
        deck.add(CardType.ATTACK, ordinary);
        deck.add(CardType.DEFUSE, defuses);
        deck.add(CardType.EXPLODING_KITTEN, kittens);
        return deck;
    }

    private List<String> namesFor(int count) {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            names.add("Player " + (i + 1));
        }
        return names;
    }
}
