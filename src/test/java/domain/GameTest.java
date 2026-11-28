package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GameTest {

    @Test
    void isSetUpRemainsFalseWithoutInspectingDeck() {
        Deck deck = EasyMock.createStrictMock(Deck.class);
        EasyMock.replay(deck);
        Game game = new Game(deck, namesFor(2));

        assertFalse(game.isSetUp());
        assertFalse(game.isSetUp());
        EasyMock.verify(deck);
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5})
    void setupFollowsOfficialRulesForEachPlayerCount(int count) {
        Deck deck = Deck.createStandardDeck(new Random(0));
        List<String> names = namesFor(count);
        Game game = new Game(deck, names);

        game.setup();

        assertInitialPlayers(game, names);
        assertInitialDeck(deck, game.getPlayers());
    }

    @Test
    void setupPreservesValidatedNamesExactly() {
        Game game = new Game(Deck.createStandardDeck(new Random(0)), List.of(" A ", "a"));

        game.setup();

        assertEquals(" A ", game.getPlayers().get(0).getName());
        assertEquals("a", game.getPlayers().get(1).getName());
    }

    @Test
    void repeatedSetupPreservesExistingState() {
        Deck deck = Deck.createStandardDeck(new Random(0));
        Game game = new Game(deck, namesFor(2));
        game.setup();
        final Player current = game.getCurrentPlayer();
        final List<Player> players = game.getPlayers();
        final List<CardType> firstHand = current.getHand();
        final List<CardType> secondHand = players.get(1).getHand();
        final Map<CardType, Integer> counts = cardCounts(deck);
        final int size = deck.size();

        assertEquals("Game has already been set up.",
                assertThrows(IllegalStateException.class, game::setup).getMessage());

        assertTrue(game.isSetUp());
        assertSame(current, game.getCurrentPlayer());
        assertEquals(players, game.getPlayers());
        assertEquals(firstHand, current.getHand());
        assertEquals(secondHand, players.get(1).getHand());
        assertEquals(counts, cardCounts(deck));
        assertEquals(size, deck.size());
    }

    @Test
    void queriesRejectAccessBeforeSetup() {
        Game game = new Game(Deck.createStandardDeck(new Random(0)), namesFor(2));

        assertEquals("Game has not been set up.",
                assertThrows(IllegalStateException.class, game::getCurrentPlayer).getMessage());
        assertEquals("Game has not been set up.",
                assertThrows(IllegalStateException.class, game::getPlayers).getMessage());
    }

    @Test
    void playerQueriesDoNotAdvanceTurnOrExposeMutableList() {
        Game game = new Game(Deck.createStandardDeck(new Random(0)), namesFor(2));
        game.setup();
        Player first = game.getCurrentPlayer();

        assertThrows(UnsupportedOperationException.class, () -> game.getPlayers().clear());

        assertSame(first, game.getCurrentPlayer());
        assertSame(first, game.getCurrentPlayer());
        assertEquals(2, game.getPlayers().size());
    }

    @Test
    void callerCannotChangeStoredNamesBeforeSetup() {
        List<String> names = namesFor(2);
        Game game = new Game(Deck.createStandardDeck(new Random(0)), names);

        names.set(0, "Changed");
        names.clear();
        game.setup();

        assertEquals("Player 1", game.getCurrentPlayer().getName());
        assertEquals("Player 2", game.getPlayers().get(1).getName());
        assertEquals(2, game.getPlayers().size());
    }

    @Test
    void setupCanRetryAfterDeckIsReplenished() {
        Deck deck = customDeck(13, 2, 1);
        Game game = new Game(deck, namesFor(2));
        assertEquals("Deck does not contain enough cards for setup.",
                assertThrows(IllegalStateException.class, game::setup).getMessage());

        deck.add(CardType.ATTACK, 1);
        game.setup();

        assertTrue(game.isSetUp());
        assertEquals(8, game.getCurrentPlayer().getHandSize());
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

    @ParameterizedTest
    @ValueSource(ints = {14, 15})
    void setupAcceptsDrawableCardBoundary(int ordinaryCards) {
        Deck deck = customDeck(ordinaryCards, 2, 1);
        Game game = new Game(deck, namesFor(2));

        game.setup();

        assertTrue(game.isSetUp());
        assertEquals(ordinaryCards - 14 + 1, deck.size());
        assertEquals(1, deck.count(CardType.EXPLODING_KITTEN));
        for (Player player : game.getPlayers()) {
            assertEquals(8, player.getHandSize());
            assertEquals(1, player.countInHand(CardType.DEFUSE));
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4})
    void returnedDefusesCountTowardDrawableBoundary(int defuses) {
        Deck deck = customDeck(16 - defuses, defuses, 1);
        Game game = new Game(deck, namesFor(2));

        game.setup();

        assertTrue(game.isSetUp());
        assertEquals(1, deck.size());
        int totalDefuses = deck.count(CardType.DEFUSE);
        for (Player player : game.getPlayers()) {
            assertEquals(8, player.getHandSize());
            totalDefuses += player.countInHand(CardType.DEFUSE);
        }
        assertEquals(defuses, totalDefuses);
    }

    @Test
    void setupRejectsInsufficientDrawableCardsIncludingReturnedDefuses() {
        assertInsufficientDeck(customDeck(11, 4, 1));
    }

    @Test
    void setupUsesOfficialCallOrderAndMayDealAdditionalDefuse() {
        Deck deck = mockDeckBeforeDealing();
        EasyMock.expect(deck.draw()).andReturn(CardType.DEFUSE);
        EasyMock.expect(deck.draw()).andReturn(CardType.ATTACK).times(13);
        deck.add(CardType.EXPLODING_KITTEN, 1);
        deck.shuffle();
        EasyMock.replay(deck);
        Game game = new Game(deck, namesFor(2));

        game.setup();

        assertEquals(2, game.getPlayers().get(0).countInHand(CardType.DEFUSE));
        assertEquals(8, game.getPlayers().get(0).getHandSize());
        assertEquals(8, game.getPlayers().get(1).getHandSize());
        EasyMock.verify(deck);
    }

    @Test
    void setupPropagatesUnexpectedDrawFailure() {
        Deck deck = mockDeckBeforeDealing();
        IllegalStateException failure = new IllegalStateException("Simulated draw failure");
        EasyMock.expect(deck.draw()).andThrow(failure);
        EasyMock.replay(deck);
        Game game = new Game(deck, namesFor(2));

        assertSame(failure, assertThrows(IllegalStateException.class, game::setup));

        assertFalse(game.isSetUp());
        assertEquals("Game has not been set up.",
                assertThrows(IllegalStateException.class, game::getCurrentPlayer).getMessage());
        assertEquals("Game has not been set up.",
                assertThrows(IllegalStateException.class, game::getPlayers).getMessage());
        EasyMock.verify(deck);
    }

    @Test
    void extraDefusesUseActualRemovalCount() {
        Deck deck = mockDeckWithRemovedDefuses(3);
        EasyMock.expect(deck.draw()).andReturn(CardType.ATTACK).times(14);
        deck.add(CardType.EXPLODING_KITTEN, 1);
        deck.shuffle();
        EasyMock.replay(deck);
        Game game = new Game(deck, namesFor(2));

        game.setup();

        assertTrue(game.isSetUp());
        EasyMock.verify(deck);
    }

    private void assertInitialPlayers(Game game, List<String> names) {
        assertTrue(game.isSetUp());
        assertEquals(names.size(), game.getPlayers().size());
        assertSame(game.getPlayers().get(0), game.getCurrentPlayer());
        for (int i = 0; i < names.size(); i++) {
            Player player = game.getPlayers().get(i);
            assertEquals(names.get(i), player.getName());
            assertEquals(8, player.getHandSize());
            assertEquals(CardType.DEFUSE, player.getHand().get(0));
            assertEquals(0, player.countInHand(CardType.EXPLODING_KITTEN));
        }
    }

    private void assertInitialDeck(Deck deck, List<Player> players) {
        int count = players.size();
        int extraDefuses = Math.min(2, 6 - count);
        int totalDefuses = deck.count(CardType.DEFUSE);
        for (Player player : players) {
            totalDefuses += player.countInHand(CardType.DEFUSE);
        }
        assertEquals(count + extraDefuses, totalDefuses);
        assertEquals(count - 1, deck.count(CardType.EXPLODING_KITTEN));
        assertEquals(46 + extraDefuses - 7 * count + count - 1, deck.size());
    }

    private Deck mockDeckBeforeDealing() {
        return mockDeckWithRemovedDefuses(6);
    }

    private Deck mockDeckWithRemovedDefuses(int defuses) {
        Deck deck = EasyMock.createStrictMock(Deck.class);
        EasyMock.expect(deck.count(CardType.DEFUSE)).andReturn(6);
        EasyMock.expect(deck.count(CardType.EXPLODING_KITTEN)).andReturn(4);
        EasyMock.expect(deck.size()).andReturn(56);
        EasyMock.expect(deck.removeAll(CardType.EXPLODING_KITTEN)).andReturn(4);
        EasyMock.expect(deck.removeAll(CardType.DEFUSE)).andReturn(defuses);
        deck.add(CardType.DEFUSE, Math.min(2, defuses - 2));
        deck.shuffle();
        return deck;
    }

    private void assertInsufficientDeck(Deck deck) {
        Game game = new Game(deck, namesFor(2));
        final Map<CardType, Integer> counts = cardCounts(deck);
        final int size = deck.size();

        assertEquals("Deck does not contain enough cards for setup.",
                assertThrows(IllegalStateException.class, game::setup).getMessage());

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
