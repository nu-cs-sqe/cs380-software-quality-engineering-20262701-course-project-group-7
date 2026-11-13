# Game BVA Analysis

## Rule Source and API

This increment follows the updated design in `game-setup-phase-design`
(commit `eb0acac`): the player names are known when the `Game` is created and
`setup()` takes no arguments. That document's class table still writes
`Game(Deck)`, so the constructor signature used here is the one its UI section
describes ("Creates the `Game` with the deck and the validated names, then
calls `game.setup()`"), and it still needs one confirmation.

`Deck`, `Player`, and `CardType` are available in package `domain`.

- `Game(Deck, List<String>)` creates the players in input order, which is the
  turn order. The UI validates first; the constructor repeats the count and
  name checks so a game always starts with valid players.
- `Game.validatePlayerCount(int)` lets the UI reject invalid counts before it
  asks for names. The UI must actually call it at that point.
- `game.setup()` runs the setup steps on the players created by the constructor.
- Names are kept exactly as entered; duplicates are case-sensitive.
  Null, empty, and whitespace-only names are rejected before any deck mutation.
- `getPlayers()` returns an unmodifiable copy in input order. Player objects
  themselves remain mutable; this is not a deep snapshot.
- `getCurrentPlayer()` returns the first player without changing the turn.
- Setup is one-shot. An insufficient deck is rejected before mutation, so the
  caller can replace the deck and retry.
- An unexpected collaborator failure during dealing is propagated; the game
  is not marked set up. The deck may already be changed, so use a fresh deck
  and Game rather than assuming rollback.

## Setup Rules

Remove all Exploding Kittens and Defuses, give each player one Defuse, return
up to two remaining Defuses (only one remains in a standard five-player game),
shuffle, then deal seven cards to each player. Those seven cards may contain
additional Defuses. Return exactly player count minus one Exploding Kittens,
discard the extra special cards, shuffle again, and select the first player.

For standard decks, the final draw-pile sizes for 2, 3, 4, 5 players are
35, 29, 23, 16 respectively. The total Defuse counts across hands and deck
are 4, 5, 6, 6 respectively.

## Test Cases

Every ID below is unique. "Implemented" refers to test code, not a passing run.
All cases below have test code in `GameTest`.

| ID | Method(s) under test | System state / input | Expected output | Implemented? |
|----|----------------------|----------------------|-----------------|--------------|
| GAME-01 | `Game(Deck, List<String>)` | Null deck, valid names. | `IllegalArgumentException`. | Yes: `constructorRejectsNullDeck` |
| GAME-02 | `Game(Deck, List<String>)`, `isSetUp()` | Non-null standard deck, two names. | New game is not set up; deck size unchanged. | Yes: `constructorAcceptsDeckWithoutSettingUpOrDrawingCards` |
| GAME-03 | `isSetUp()` | New game, queried twice. | Both calls return false. | Yes: `isSetUpRemainsFalseBeforeSetup` |
| GAME-04 | `setup`, `isSetUp`, `getPlayers`, `getCurrentPlayer` | Standard deck; 2, 3, 4, 5 unique names passed at construction. | Eight cards per player; first card is Defuse, no Kittens in hands; player order and names preserved; first player current; setup true; final counts as above. | Yes: `setupFollowsOfficialRulesForEachPlayerCount` |
| GAME-05 | `validatePlayerCount` | Count 1 or 6. | `IllegalArgumentException`. | Yes: `validatePlayerCountRejectsOutsideBoundaries` |
| GAME-06 | `validatePlayerCount` | Count 2, 3, 4, 5. | No exception. | Yes: `validatePlayerCountAcceptsValidCounts` |
| GAME-07 | `Game(Deck, List<String>)` | Null names list. | `IllegalArgumentException`; deck unchanged; no game created. | Yes: `constructorRejectsNullNames` |
| GAME-08 | `Game(Deck, List<String>)` | Empty names list, 1 name, or 6 names. | `IllegalArgumentException`; deck unchanged; no game created. | Yes: `constructorRejectsInvalidCounts` |
| GAME-09 | `Game(Deck, List<String>)` | Valid count; one name null, empty (including non-interned empty string), or whitespace-only. | `IllegalArgumentException`; deck unchanged; no game created. | Yes: `constructorRejectsNullName`, `constructorRejectsBlankNames`, `constructorRejectsNonInternedEmptyName` |
| GAME-10 | `Game(Deck, List<String>)` | Two equal names, including distinct String objects. | `IllegalArgumentException`; deck unchanged; no game created. | Yes: `constructorRejectsDuplicateNameValues` |
| GAME-11 | `Game(Deck, List<String>)`, `setup`, `getPlayers` | Names "A" and "a". | Both accepted and preserved, per case-sensitive policy. | Yes: `constructorPreservesCaseSensitiveNames` |
| GAME-12 | `setup`, `getCurrentPlayer`, `getPlayers` | Already set up; another setup request. | `IllegalStateException`; current player, players, hands and deck unchanged. | Yes: `repeatedSetupPreservesExistingState` |
| GAME-13 | `getCurrentPlayer`, `getPlayers` | Before setup. | `IllegalStateException("Game has not been set up.")`. | Yes: `queriesRejectAccessBeforeSetup` |
| GAME-14 | `getPlayers`, `getCurrentPlayer` | After setup; repeated queries and attempted list mutation. | Returned list cannot be modified; query does not advance current player. | Yes: `playerQueriesDoNotAdvanceTurnOrExposeMutableList` |
| GAME-15 | `Game(Deck, List<String>)`, `setup`, `getPlayers` | Caller mutates the original names list after construction. | Stored player names and order unchanged. | Yes: `callerCannotChangeStoredNamesAfterConstruction` |
| GAME-16 | `Game(Deck, List<String>)`, `setup` | Rejected construction followed by a valid game on the same deck. | First rejected; second sets up successfully. | Yes: `invalidConstructionDoesNotPreventLaterValidGame` |
| GAME-17 | `setup` | Empty custom deck, insufficient Defuses, insufficient Kittens, or one fewer drawable card than required. | `IllegalStateException` before mutation; setup false. | Yes: `setupRejectsEmptyDeckBeforeMutation`, `setupRejectsInsufficientDefuses`, `setupRejectsInsufficientKittens`, `setupRejectsOneFewerDrawableCardThanRequired` |
| GAME-18 | `setup` | Custom deck with exactly 14 drawable cards, 2 Defuses and 1 Kitten; 2 players. | Setup succeeds; draw pile contains only the Kitten; each hand has eight cards. | Yes: `setupAcceptsExactlyEnoughDrawableCards` |
| GAME-19 | `setup` | Recorded Deck calls; two players and six Defuses. | Remove specials, add two extra Defuses, shuffle, draw 14 cards, add one Kitten, shuffle; no other extra specials returned. | Yes: `setupUsesOfficialCallOrderAndMayDealAdditionalDefuse` |
| GAME-20 | `setup`, `getPlayers` | Deterministic Deck deals a returned Defuse among the seven cards. | One player can have two Defuses; total hand size is still eight. | Yes: `setupUsesOfficialCallOrderAndMayDealAdditionalDefuse` |
| GAME-21 | `setup`, `isSetUp`, `getCurrentPlayer` | Deck passes preflight but throws on draw. | Same exception propagated; setup false; current player unavailable. No rollback promised. | Yes: `setupPropagatesUnexpectedDrawFailureWithoutPublishingPlayers` |

## Execution Record

- Before implementation, `./gradlew test --tests domain.GameTest --offline
  --no-daemon` failed at `compileTestJava` because the new Game methods did
  not yet exist. This was a compile-time red run, not failing assertions.
- After the constructor-based API change, `sh gradlew test --offline
  --no-daemon` finished with `BUILD SUCCESSFUL`. The results under
  `build/test-results/test` report 161 domain tests with none skipped or
  failed: `CardTypeTest` 14, `DeckTest` 95, `GameTest` 37, `PlayerTest` 15.

## Remaining Team Decisions

- Confirm the `Game(Deck, List<String>)` constructor signature and the
  no-argument `setup()`, since the design document's class table still shows
  `Game(Deck)` while its UI section passes the names to the constructor.
- Confirm who owns initialization logic versus UI orchestration.
- Confirm case sensitivity and whitespace handling for player names.
- Confirm whether exception messages are mapped to localized UI messages.
