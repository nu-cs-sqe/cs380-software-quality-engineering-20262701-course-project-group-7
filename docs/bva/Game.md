# Game BVA Analysis

## Rule Source and Proposed API

This increment follows the updated acceptance criteria and use case in design
PR #9 (commit `58feb264a3818abe38cf986ce7c8892092eaf88a`), not its stale
`Game.setup(int)` algorithm table.

`Deck`, `Player`, and `CardType` are now available in package `domain`.
The proposed API still needs agreement with the UI / GameSetup owner:

- `Game.validatePlayerCount(int)` lets the UI reject invalid counts before
  requesting names. The UI must actually call it at that point.
- `game.setup(List<String>)` validates names and initializes the game.
- Names are kept exactly as entered; duplicates are case-sensitive.
  Null, empty, and whitespace-only names are rejected before any deck mutation.
- `getPlayers()` returns an unmodifiable copy in input order. Player objects
  themselves remain mutable; this is not a deep snapshot.
- `getCurrentPlayer()` returns the first player without changing the turn.
- Setup is one-shot. Invalid input or insufficient deck contents is rejected
  before mutation, so the caller can correct the input and retry.
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
All cases below now have test code in `GameTest`.

| ID | Method(s) under test | System state / input | Expected output | Implemented? |
|----|----------------------|----------------------|-----------------|--------------|
| GAME-01 | `Game(Deck)` | Null deck. | `IllegalArgumentException`. | Yes: `constructorRejectsNullDeck` |
| GAME-02 | `Game(Deck)`, `isSetUp()` | Non-null standard deck. | New game is not set up; deck size unchanged. | Yes: `constructorAcceptsDeckWithoutSettingUpOrDrawingCards` |
| GAME-03 | `isSetUp()` | New game, queried twice. | Both calls return false. | Yes: `isSetUpRemainsFalseBeforeSetup` |
| GAME-04 | `setup`, `isSetUp`, `getPlayers`, `getCurrentPlayer` | Standard deck; 2, 3, 4, 5 unique names. | Eight cards per player; first card is Defuse, no Kittens in hands; player order and names preserved; first player current; setup true; final counts as above. | Yes: `setupFollowsOfficialRulesForEachPlayerCount` |
| GAME-05 | `validatePlayerCount` | Count 1 or 6. | `IllegalArgumentException`. | Yes: `validatePlayerCountRejectsOutsideBoundaries` |
| GAME-06 | `validatePlayerCount` | Count 2, 3, 4, 5. | No exception. | Yes: `validatePlayerCountAcceptsValidCounts` |
| GAME-07 | `setup`, `isSetUp` | Null names list. | `IllegalArgumentException`; deck unchanged; setup false. | Yes: `setupRejectsNullNamesWithoutChangingDeck` |
| GAME-08 | `setup`, `isSetUp` | Empty names list, 1 name, or 6 names. | `IllegalArgumentException`; deck unchanged; setup false. | Yes: `setupRejectsInvalidCountsWithoutChangingDeck` |
| GAME-09 | `setup`, `isSetUp` | Valid count; one name null, empty (including non-interned empty string), or whitespace-only. | `IllegalArgumentException`; deck unchanged; setup false. | Yes: `setupRejectsNullNameWithoutChangingDeck`, `setupRejectsBlankNamesWithoutChangingDeck`, `setupRejectsNonInternedEmptyName` |
| GAME-10 | `setup`, `isSetUp` | Two equal names, including distinct String objects. | `IllegalArgumentException`; deck unchanged; setup false. | Yes: `setupRejectsDuplicateNameValues` |
| GAME-11 | `setup`, `getPlayers` | Names "A" and "a". | Both accepted and preserved, per proposed case-sensitive policy. | Yes: `setupPreservesCaseSensitiveNames` |
| GAME-12 | `setup`, `getCurrentPlayer`, `getPlayers` | Already set up; another valid or invalid setup request. | `IllegalStateException`; current player, players, hands and deck unchanged. | Yes: `repeatedSetupPreservesExistingState` |
| GAME-13 | `getCurrentPlayer`, `getPlayers` | Before setup. | `IllegalStateException("Game has not been set up.")`. | Yes: `queriesRejectAccessBeforeSetup` |
| GAME-14 | `getPlayers`, `getCurrentPlayer` | After setup; repeated queries and attempted list mutation. | Returned list cannot be modified; query does not advance current player. | Yes: `playerQueriesDoNotAdvanceTurnOrExposeMutableList` |
| GAME-15 | `setup`, `getPlayers` | Caller mutates original names list after setup. | Stored player names and order unchanged. | Yes: `callerCannotChangeStoredNamesAfterSetup` |
| GAME-16 | `setup`, `isSetUp` | Invalid input followed by valid input on the same game. | First rejected without deck mutation; second succeeds. | Yes: `validSetupCanFollowRejectedInput` |
| GAME-17 | `setup` | Empty custom deck, insufficient Defuses, insufficient Kittens, or one fewer drawable card than required. | `IllegalStateException` before mutation; setup false. | Yes: `setupRejectsEmptyDeckBeforeMutation`, `setupRejectsInsufficientDefuses`, `setupRejectsInsufficientKittens`, `setupRejectsOneFewerDrawableCardThanRequired` |
| GAME-18 | `setup` | Custom deck with exactly 14 drawable cards, 2 Defuses and 1 Kitten; 2 players. | Setup succeeds; draw pile contains only the Kitten; each hand has eight cards. | Yes: `setupAcceptsExactlyEnoughDrawableCards` |
| GAME-19 | `setup` | Recorded Deck calls; two players and six Defuses. | Remove specials, add two extra Defuses, shuffle, draw 14 cards, add one Kitten, shuffle; no other extra specials returned. | Yes: `setupUsesOfficialCallOrderAndMayDealAdditionalDefuse` |
| GAME-20 | `setup`, `getPlayers` | Deterministic Deck deals a returned Defuse among the seven cards. | One player can have two Defuses; total hand size is still eight. | Yes: `setupUsesOfficialCallOrderAndMayDealAdditionalDefuse` |
| GAME-21 | `setup`, `isSetUp`, `getCurrentPlayer` | Deck passes preflight but throws on draw. | Same exception propagated; setup false; current player unavailable. No rollback promised. | Yes: `setupPropagatesUnexpectedDrawFailureWithoutPublishingPlayers` |

## Execution Record

- Before implementation, `./gradlew test --tests domain.GameTest --offline
  --no-daemon` failed at `compileTestJava` because the new Game methods did
  not yet exist. This was a compile-time red run, not failing assertions.
- After implementation, all domain sources and tests compiled with
  `javac --release 11`. JUnit Platform Console Standalone 1.10.0
  (JUnit Jupiter 5.10.0), with EasyMock 5.4.0 and its runtime dependencies,
  ran 37 Game tests successfully, with none skipped or failed.
- The same independent runner scanned all tests in package `domain`:
  161 tests passed, with none skipped or failed.
- The post-implementation Gradle run reached `processTestResources` but
  did not finish starting tests and was stopped. The independent test
  result does not establish that the Gradle build or remote CI passes.

## Remaining Team Decisions

- Confirm the proposed names-list API and UI's early count validation call.
- Confirm who owns initialization logic versus UI orchestration.
- Confirm case sensitivity and whitespace handling for player names.
- Confirm whether exception messages are mapped to localized UI messages.
- Update the design PR's old Game algorithm to match its acceptance criteria.
