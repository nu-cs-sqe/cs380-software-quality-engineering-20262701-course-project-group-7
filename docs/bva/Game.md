# Game BVA Analysis

## Rule Source and API

The contract is recorded in `docs/design/game-setup-phase.md` and follows
`docs/requirements/code_style_standards.md`. The constructor takes the deck and
UI-validated names; `setup()` takes no arguments.

`Deck`, `Player`, and `CardType` are available in package `domain`.

- `Game(Deck, List<String>)` creates the players in input order, which is the
  turn order. The UI validates the 2-to-5 player range and names first.
- Arguments are non-null by contract. Game does not check null or invalid user
  input, and those cases are not part of this domain BVA or GameTest.
- Player-count validation and its numeric bounds belong to the UI. Game has no
  public validation method or public constants.
- `game.setup()` runs the setup steps on the players created by the constructor.
- Already validated names are stored exactly as supplied.
- `getPlayers()` returns an unmodifiable copy in input order. Player objects
  themselves remain mutable; this is not a deep snapshot.
- `getCurrentPlayer()` returns the first player without changing the turn.
- Setup is one-shot. An insufficient deck is rejected before mutation, so the
  caller can replenish that deck and retry, or create a new Game with a fresh deck.
- An unexpected collaborator failure during dealing is propagated; the game
  is not marked set up. The deck may already be changed, so use a fresh deck
  and Game rather than assuming rollback.
- Game-owned exception messages are resource keys: `game.alreadySetUp`,
  `game.notSetUp` and `game.insufficientDeck`. User-facing translations are in
  `message_en.properties` and `message_zh.properties`; the UI resolves them.

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
Implemented cases name their test code in `GameTest`.
Former user-input cases GAME-01, GAME-05 through GAME-10, and GAME-16 were
removed when validation moved exclusively to the UI. IDs are not reused.

### `Game(Deck, List<String>)`

The constructor creates owned Player entities, so it is not a simple
assignment-only constructor. Tests cover that initialization with valid inputs,
not UI input validation.

| ID | Method(s) under test | System state / input | Expected output | Implemented? |
|----|----------------------|----------------------|-----------------|--------------|
| GAME-02 | `Game(Deck, List<String>)`, `isSetUp()` | Non-null standard deck, two names. | New game is not set up; deck size unchanged. | Yes: `constructorAcceptsDeckWithoutSettingUpOrDrawingCards` |
| GAME-11 | `Game(Deck, List<String>)`, `setup`, `getPlayers` | UI supplies validated names "A" and "a". | Original capitalization and order preserved. | Yes: `constructorPreservesCaseSensitiveNames` |
| GAME-15 | `Game(Deck, List<String>)`, `setup`, `getPlayers` | Caller mutates the original names list after construction. | Stored player names and order unchanged. | Yes: `callerCannotChangeStoredNamesAfterConstruction` |

### `setup()`

Deck sufficiency is a game-state boundary, not user-input validation.

| ID | Method(s) under test | System state / input | Expected output | Implemented? |
|----|----------------------|-----------------|-----------------|--------------|
| GAME-04 | `setup` | Standard deck; 2, 3, 4, 5 already validated players. | Eight cards per player; Defuse first, no Kittens in hands; names and turn order preserved; setup true; final counts as above. | Yes: `setupFollowsOfficialRulesForEachPlayerCount` |
| GAME-12 | `setup`, `getCurrentPlayer`, `getPlayers` | Already set up; another setup request. | `IllegalStateException("game.alreadySetUp")`; current player, players, hands and deck unchanged. | Yes: `repeatedSetupPreservesExistingState` |
| GAME-17 | `setup` | Empty custom deck, insufficient Defuses, insufficient Kittens, or one fewer drawable card than required. | `IllegalStateException("game.insufficientDeck")` before mutation; setup false. | Yes: `setupRejectsEmptyDeckBeforeMutation`, `setupRejectsInsufficientDefuses`, `setupRejectsInsufficientKittens`, `setupRejectsOneFewerDrawableCardThanRequired` |
| GAME-18 | `setup` | Custom deck with exactly 14 drawable cards, 2 Defuses and 1 Kitten; 2 players. | Setup succeeds; draw pile contains only the Kitten; each hand has eight cards. | Yes: `setupAcceptsExactlyEnoughDrawableCards` |
| GAME-19 | `setup` | Recorded Deck calls; two players and six Defuses. | Remove specials, add two extra Defuses, shuffle, draw 14 cards, add one Kitten, shuffle; no other extra specials returned. | Yes: `setupUsesOfficialCallOrderAndMayDealAdditionalDefuse` |
| GAME-20 | `setup`, `getPlayers` | Deterministic Deck deals a returned Defuse among the seven cards. | One player can have two Defuses; total hand size is still eight. | Yes: `setupUsesOfficialCallOrderAndMayDealAdditionalDefuse` |
| GAME-21 | `setup`, `isSetUp`, `getCurrentPlayer` | Deck passes preflight but throws on draw. | Same exception propagated; setup false; current player unavailable. No rollback promised. | Yes: `setupPropagatesUnexpectedDrawFailureWithoutPublishingPlayers` |
| GAME-22 | `setup`, `getPlayers` | Two players; 13 ordinary cards, 3 Defuses, 1 Kitten. Returning the extra Defuse makes exactly 14 drawable cards. | Setup succeeds, each hand has 8 cards; 3 Defuses across all hands; draw pile contains only 1 Kitten. | Yes: `setupCountsReturnedDefuseTowardDrawableBoundary` |

### `getCurrentPlayer()`

| ID | Method(s) under test | System state / input | Expected output | Implemented? |
|----|----------------------|-----------------|-----------------|--------------|
| GAME-13 | `getCurrentPlayer` | Before setup. | `IllegalStateException("game.notSetUp")`. | Yes: `queriesRejectAccessBeforeSetup` |
| GAME-14 | `getCurrentPlayer` | Successful setup; repeated queries. | Same first player; no turn advancement. | Yes: `playerQueriesDoNotAdvanceTurnOrExposeMutableList` |

### `getPlayers()`

| ID | Method(s) under test | System state / input | Expected output | Implemented? |
|----|----------------------|-----------------|-----------------|--------------|
| GAME-13B | `getPlayers` | Before setup. | `IllegalStateException("game.notSetUp")`. | Yes: `queriesRejectAccessBeforeSetup` |
| GAME-14B | `getPlayers` | Successful setup; attempted list mutation. | `UnsupportedOperationException`; order and current player unchanged. | Yes: `playerQueriesDoNotAdvanceTurnOrExposeMutableList` |

### `isSetUp()`

| ID | Method(s) under test | System state / input | Expected output | Implemented? |
|----|----------------------|-----------------|-----------------|--------------|
| GAME-03 | `isSetUp` | New game; repeated queries. | False on each call. | Yes: `isSetUpRemainsFalseBeforeSetup` |
| GAME-03B | `isSetUp` | Successful setup. | True; remains true after rejected repeated setup. | Yes: `setupFollowsOfficialRulesForEachPlayerCount`, `repeatedSetupPreservesExistingState` |
| GAME-03C | `isSetUp` | Deck preflight or draw fails. | False. | Yes: `setupRejectsEmptyDeckBeforeMutation`, `setupPropagatesUnexpectedDrawFailureWithoutPublishingPlayers` |

### Supporting Locale Checks

`setupErrorsHaveTranslationsForSupportedLocales` loads English and Chinese
bundles using `ResourceBundle.getBundle("message", locale)`. Each of the three
Game error keys must have a non-blank translation distinct from the key, and
the bundle must match the requested locale.

## Execution Record

The measurements below describe earlier increments with domain input
validation. Their test totals and coverage counters do not describe the current
UI-validation-only implementation.

- Before implementation, `./gradlew test --tests domain.GameTest --offline
  --no-daemon` failed at `compileTestJava` because the new Game methods did
  not yet exist. This was a compile-time red run, not failing assertions.
- After the constructor-based API change, `sh gradlew test --offline
  --no-daemon` finished with `BUILD SUCCESSFUL`. The results under
  `build/test-results/test` report 161 domain tests with none skipped or
  failed: `CardTypeTest` 14, `DeckTest` 95, `GameTest` 37, `PlayerTest` 15.
- Initial Game-only JaCoCo measurement: 69/69 lines, 34/34 branches and 30/30
  cyclomatic complexity units covered. `gameCoverageVerification` passed.
- Initial PIT 1.19.0 run with the default mutators: 45 of 46 mutants killed.
  The surviving mutant changed `ordinaryCards + extraDefuses` to subtraction
  in `validateDeckForSetup`. GAME-22 records the missing boundary case.
- After implementing GAME-22, all 162 domain tests passed (38 Game tests).
  JaCoCo 0.8.12 still reports 69/69 lines, 34/34 branches and 30/30 complexity
  units covered using only `GameTest`.
- Final PIT 1.19.0 run with explicitly selected `DEFAULTS` mutators: 46/46
  mutants killed, no survivors, no uncovered mutants and no timeouts. No mutants
  or Game methods were excluded. This result is specific to this mutator set.
- `gameCheckstyle` passes with the unchanged team rules; the full SpotBugs
  reports contain zero findings for `Game` and `GameTest`. The final class
  prevents the constructor-finalizer issue, and the duplicate-name test uses
  a character array to create a distinct String without `new String(String)`.
- The full `check` remains failing on teammate code: Deck's magic numbers and
  test formatting, Player's formatting and test imports, Deck's
  `CT_CONSTRUCTOR_THROW`, and Player's `ES_COMPARING_STRINGS_WITH_EQ`.
  These files and the team's exclusion filter were not changed.

### Current Standards-Aligned Verification

- All 146 domain tests passed: GameTest 22 (20 game-behavior cases and two locale
  cases), CardTypeTest 14, DeckTest 95, PlayerTest 15.
- Game-only JaCoCo: 52/52 lines, 18/18 branches and 20/20 complexity units
  covered. The 100% complexity gate passed.
- PIT `DEFAULTS`: 35/35 mutants killed, no survivors, uncovered mutants or
  timeouts. Removing UI-input validation reduced the mutated code; no mutation
  exclusions or weaker thresholds were introduced.
- Game and GameTest pass the unchanged Checkstyle configuration. SpotBugs
  reports zero findings in both; the full main report still reports issues in
  Deck and Player.
- All Game constants are private; public methods precede private helpers, and
  production and test methods stay within the 20-line target.
- No null or invalid-user-input tests remain in GameTest. All Game error
  assertions check the resource key, and both shipped locale bundles resolve it.
- The full `check --continue` still fails on teammate Checkstyle and SpotBugs
  findings, not on Game's coverage, PIT or Checkstyle gates.

### Reproducible Commands

```sh
./gradlew test gameCheckstyle gameCoverage gameCoverageVerification pitest spotbugsTest
./gradlew check --continue
```

The first command passed. The second exposes the remaining teammate findings;
it is not a passing full-build result. `check` now also runs the Game-specific
Checkstyle, 100% complexity coverage gate and 100% PIT mutation-score gate.
HTML reports are generated under `build/reports/checkstyle`,
`build/reports/jacoco/gameCoverage/html`, `build/reports/pitest` and
`build/reports/spotbugs`. This adds white-box test improvement evidence, not a
claim that earlier implementation commits followed a complete TDD cycle.

## UI Integration

- The UI must validate player count and names before construction. Game no
  longer has `validatePlayerCount`, null checks or invalid-user-input tests.
- The design document now records the constructor, no-argument setup, private
  constants, final class and resource-key error contract.
- The GUI owner must resolve Game error keys through the supplied bundles and
  provide a localized fallback for unexpected collaborator exceptions.
- GUI integration testing and teammate approval remain required before merging.
