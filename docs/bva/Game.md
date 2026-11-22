# Game BVA Analysis

## Contract

The specification is `docs/design/game-setup-phase.md` on the merged main
branch. The UI validates non-null inputs, 2 to 5 players, and non-blank,
case-sensitive unique names before calling `new Game(deck, playerNames)`.
Game preserves names and their order and exposes `setup()` with no arguments.
Invalid user inputs and null checks are not domain test cases under the team
standards. Earlier validation cases are retired, not silently claimed as tested.

`getPlayers()` protects the list order, not the mutable Player objects.
Both player queries throw `IllegalStateException("game.notSetUp")` before
successful setup. Repeated setup throws `"game.alreadySetUp"`. Insufficient
deck contents throw `"game.insufficientDeck"` before mutation and permit retry.
Unexpected Deck errors propagate without rollback; use a fresh game afterward.

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

IDs below replace the earlier proposed-API cases. Markers remain pending until
the corresponding test passes; test code and the marker update share a commit.
The constructor's ownership of player names is exercised through public queries
after setup. There is no standalone invalid-input constructor test.

| ID | Method(s) under test | System state / input | Expected output | Implemented? |
|----|----------------------|----------------------|-----------------|--------------|
| GAME-01 | `isSetUp()` | Newly constructed game; strict mock deck has no expected calls. | False twice; constructor and query do not inspect or mutate deck. | Yes: `isSetUpRemainsFalseWithoutInspectingDeck` |
| GAME-02 | `setup`, `isSetUp`, player queries | Standard deck; 2, 3, 4, 5 validated names. | Eight cards each, guaranteed first Defuse, no hand Kittens, input order, first player current, correct final counts. | Yes: `setupFollowsOfficialRulesForEachPlayerCount` |
| GAME-03 | `setup`, `getPlayers` | Valid names `" A "` and `"a"`. | Exact spelling and whitespace preserved. | Yes: `setupPreservesValidatedNamesExactly` |
| GAME-04 | `setup`, player queries | Caller changes and clears names after construction, before setup. | Stored player names and order unchanged. | Yes: `callerCannotChangeStoredNamesBeforeSetup` |
| GAME-05 | `setup`, player queries | Already set up; call again. | Already-set-up key; deck, both hands, players and current player unchanged. | Yes: `repeatedSetupPreservesExistingState` |
| GAME-06 | `getCurrentPlayer`, `getPlayers` | Not yet set up. | Not-set-up key from each query. | Yes: `queriesRejectAccessBeforeSetup` |
| GAME-07 | `getPlayers`, `getCurrentPlayer` | Set up; repeat queries and attempt list mutation. | Unmodifiable list; no turn advancement. | Yes: `playerQueriesDoNotAdvanceTurnOrExposeMutableList` |
| GAME-08 | `setup`, `isSetUp` | Empty deck, or 0/1 Defuses for 2 players. | Insufficient-deck key; no mutation. | Yes: `setupRejectsEmptyDeckBeforeMutation`, `setupRejectsInsufficientDefuses` |
| GAME-09 | `setup`, `isSetUp` | 2 Defuses, 14 ordinary cards, no Kitten. | Insufficient-deck key; no mutation. | Yes: `setupRejectsInsufficientKittens` |
| GAME-10 | `setup`, `isSetUp` | 2 Defuses, 1 Kitten, 13 ordinary cards. | Insufficient-deck key; no mutation. | Yes: `setupRejectsOneFewerDrawableCardThanRequired` |
| GAME-11 | `setup`, `getPlayers` | 2 players; 2 Defuses, 1 Kitten, 14/15 ordinary cards. | Setup succeeds at and above boundary; eight cards each and one guaranteed Defuse. | Yes: `setupAcceptsDrawableCardBoundary` |
| GAME-12 | `setup`, `getPlayers` | 2 players; 3/4 Defuses and 13/12 ordinary cards. | Returned 1/2 Defuses count toward the 14 drawable cards; exact total preserved. | Yes: `returnedDefusesCountTowardDrawableBoundary` |
| GAME-13 | `setup` | 4 Defuses, 1 Kitten, 11 ordinary cards. | One fewer drawable card even after return; no mutation. | Yes: `setupRejectsInsufficientDrawableCardsIncludingReturnedDefuses` |
| GAME-14 | `setup`, `isSetUp` | Preflight rejects 13 ordinary cards; add one and retry. | Initial deck unchanged; retry succeeds. | Yes: `setupCanRetryAfterDeckIsReplenished` |
| GAME-15 | `setup`, `getPlayers` | Strict mock tracks removal, return, two shuffles and dealing; first draw Defuse. | Official call order, 14 draws, one Kitten returned, possible second hand Defuse. | Yes: `setupUsesOfficialCallOrderAndMayDealAdditionalDefuse` |
| GAME-16 | `setup`, `isSetUp`, player queries | Preflight passes; draw throws. | Same exception propagated; false and not-set-up keys; no rollback promised. | Pending: `setupPropagatesUnexpectedDrawFailure` |
| GAME-17 | `setup` | Strict mock returns 6 in count and 3 from removeAll. | Return count uses actual removal result (one extra), not preflight count. | Pending: `extraDefusesUseActualRemovalCount` |

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

The earlier execution record is historical evidence, not verification of the
current constructor API or the new team quality tools. Current results will be
recorded after running the Gradle reports.
