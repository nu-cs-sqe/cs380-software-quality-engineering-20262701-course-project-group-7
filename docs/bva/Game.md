# Game BVA Analysis

## Contract

The specification is `docs/design/game-setup-phase.md`, refined by the author's
clarification that internal exception messages do not require i18n.
The UI validates non-null inputs, 2 to 5 players, and non-blank,
case-sensitive unique names before calling `new Game(deck, playerNames)`.
Game preserves names and their order and exposes `setup()` with no arguments.
Invalid user inputs and null checks are not domain test cases under the team
standards. Earlier validation cases are retired, not silently claimed as tested.

`getPlayers()` protects the list order, not the mutable Player objects.
Both player queries throw `IllegalStateException("Game has not been set up.")`
before successful setup. Repeated setup throws `"Game has already been set up."`.
Insufficient deck contents throw `"Deck does not contain enough cards for setup."`
before mutation and permit retry. These are internal English diagnostics, not
resource keys or user-facing localized messages.
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

IDs below replace the earlier proposed-API cases. Markers flip only after the
corresponding test passes; test code and the marker update share a commit.
The constructor's ownership of player names is exercised through public queries
after setup. There is no standalone invalid-input constructor test.

| ID | Method(s) under test | System state / input | Expected output | Implemented? |
|----|----------------------|----------------------|-----------------|--------------|
| GAME-01 | `isSetUp()` | Newly constructed game; strict mock deck has no expected calls. | False twice; constructor and query do not inspect or mutate deck. | Yes: `isSetUpRemainsFalseWithoutInspectingDeck` |
| GAME-02 | `setup`, `isSetUp`, player queries | Standard deck; 2, 3, 4, 5 validated names. | Eight cards each, guaranteed first Defuse, no hand Kittens, input order, first player current, correct final counts. | Yes: `setupFollowsOfficialRulesForEachPlayerCount` |
| GAME-03 | `setup`, `getPlayers` | Valid names `" A "` and `"a"`. | Exact spelling and whitespace preserved. | Yes: `setupPreservesValidatedNamesExactly` |
| GAME-04 | `setup`, player queries | Caller changes and clears names after construction, before setup. | Stored player names and order unchanged. | Yes: `callerCannotChangeStoredNamesBeforeSetup` |
| GAME-05 | `setup`, player queries | Already set up; call again. | Already-set-up English diagnostic; deck, both hands, players and current player unchanged. | Yes: `repeatedSetupPreservesExistingState` |
| GAME-06 | `getCurrentPlayer`, `getPlayers` | Not yet set up. | Not-set-up English diagnostic from each query. | Yes: `queriesRejectAccessBeforeSetup` |
| GAME-07 | `getPlayers`, `getCurrentPlayer` | Set up; repeat queries and attempt list mutation. | Unmodifiable list; no turn advancement. | Yes: `playerQueriesDoNotAdvanceTurnOrExposeMutableList` |
| GAME-08 | `setup`, `isSetUp` | Empty deck, or 0/1 Defuses for 2 players. | Insufficient-deck English diagnostic; no mutation. | Yes: `setupRejectsEmptyDeckBeforeMutation`, `setupRejectsInsufficientDefuses` |
| GAME-09 | `setup`, `isSetUp` | 2 Defuses, 14 ordinary cards, no Kitten. | Insufficient-deck English diagnostic; no mutation. | Yes: `setupRejectsInsufficientKittens` |
| GAME-10 | `setup`, `isSetUp` | 2 Defuses, 1 Kitten, 13 ordinary cards. | Insufficient-deck English diagnostic; no mutation. | Yes: `setupRejectsOneFewerDrawableCardThanRequired` |
| GAME-11 | `setup`, `getPlayers` | 2 players; 2 Defuses, 1 Kitten, 14/15 ordinary cards. | Setup succeeds at and above boundary; eight cards each and one guaranteed Defuse. | Yes: `setupAcceptsDrawableCardBoundary` |
| GAME-12 | `setup`, `getPlayers` | 2 players; 3/4 Defuses and 13/12 ordinary cards. | Returned 1/2 Defuses count toward the 14 drawable cards; exact total preserved. | Yes: `returnedDefusesCountTowardDrawableBoundary` |
| GAME-13 | `setup` | 4 Defuses, 1 Kitten, 11 ordinary cards. | One fewer drawable card even after return; English diagnostic and no mutation. | Yes: `setupRejectsInsufficientDrawableCardsIncludingReturnedDefuses` |
| GAME-14 | `setup`, `isSetUp` | Preflight rejects 13 ordinary cards; add one and retry. | English diagnostic and unchanged initial deck; retry succeeds. | Yes: `setupCanRetryAfterDeckIsReplenished` |
| GAME-15 | `setup`, `getPlayers` | Strict mock tracks removal, return, two shuffles and dealing; first draw Defuse. | Official call order, 14 draws, one Kitten returned, possible second hand Defuse. | Yes: `setupUsesOfficialCallOrderAndMayDealAdditionalDefuse` |
| GAME-16 | `setup`, `isSetUp`, player queries | Preflight passes; draw throws. | Same exception propagated; false and not-set-up English diagnostics; no rollback promised. | Yes: `setupPropagatesUnexpectedDrawFailure` |
| GAME-17 | `setup` | Strict mock returns 6 in count and 3 from removeAll. | Return count uses actual removal result (one extra), not preflight count. | Yes: `extraDefusesUseActualRemovalCount` |

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
current constructor API or the new team quality tools.

### Current API Verification and Commit Split

- Before the original unpublished migration, the new API tests failed locally
  at `compileTestJava`: the old implementation did not accept
  `(Deck, List<String>)` or provide argument-free `setup()`. This was an
  API-level red run, not a separate failing-assertion run for each test.
- With author permission, unpublished commit `8937274` was replaced by 18
  individual test-method commits, from `2572498` through `3dc952d`.
  The original remains on local branch `backup/game-before-test-split`.
  Earlier history was not rewritten, and nothing was force-pushed.
- Each test was restored individually, the cumulative GameTest suite was
  run successfully using `./gradlew test --tests domain.GameTest --offline
  --no-daemon --console=plain`, then its passing code and BVA marker were
  committed. GAME-08 groups two methods, so its marker changed only after
  both methods passed. Parameterized inputs remain one method per commit.
- A diff audit confirmed exactly one test method introduced per split commit,
  and the restored final test file matched the original byte for byte.
- Existing setup behavior and the API migration implementation accompany
  the first restored test. Splitting commits does not retroactively establish
  a new per-test red-green workflow. No instructor waiver is claimed.
- A subsequent refactoring-only commit extracts player and deck assertions.
  Game and GameTest method bodies meet the 20-line target.
- The final `./gradlew build --continue --offline --no-daemon --console=plain`
  ran all 148 tests successfully: 24 Game invocations, with no test failures
  or errors. The full build nevertheless failed quality checks below.
- JaCoCo Game: zero missed complexity (20 covered), branches (18 covered),
  and lines (51 covered). PIT Game: all 35 mutants KILLED, no error outcomes.
- No Game or GameTest Checkstyle or SpotBugs findings. Team tool versions,
  exclusions and failure settings were not weakened.

### Internal Exception Message Correction

- The author clarified that internal domain exception messages do not need
  i18n. The Game resource-key design is superseded by meaningful English
  diagnostics. Player-facing text remains the UI's separate responsibility.
- BVA contract changes were committed first in `6c51ea5`.
  Updated assertions then produced 10 failing invocations out of 24 in the
  local GameTest run: the implementation still returned the old keys.
- Only after observing those assertion failures were the three Game message
  constants changed. The author explicitly approved one atomic correction
  commit for the implementation, existing assertions and documentation.
  The previous 18 individual test commits remain unchanged.
- Full Gradle verification after correction: all 148 tests pass (24 Game);
  Game has 100% complexity, branch and line coverage and all 35 PIT mutants
  KILLED. Game and GameTest have no Checkstyle or SpotBugs findings.
- This PIT run reports 63/63 project mutants KILLED, with no MEMORY_ERROR.
  The earlier Deck.add error was not reproduced and its cause was not
  investigated; it is historical evidence, not a current failed outcome.
- Full build still fails Checkstyle main/test and SpotBugs main for the
  Deck/Player findings below. SpotBugs test now passes.

### PR Publication Verification

- The published draft had five commits outside the local branch. A merge
  preserves that remote history while retaining the verified Game version.
  Its obsolete exception-only locale bundles are not restored.
- Latest main `dc674387` is merged, including the narrower JaCoCo exclusions
  and `ui.controller.*` PIT scope. No checks are disabled.
- `./gradlew build --continue --offline --no-daemon --console=plain` was
  rerun on this merged version: all 148 tests pass, including 24 Game.
  Game retains 100% complexity/branch/line coverage and 35/35 KILLED mutants.
- Project PIT now has 62 normal kills and one Deck.add MEMORY_ERROR.
  That error has recurred after the intervening successful run; its root
  cause remains uninvestigated, and the displayed detected percentage is
  not treated as all mutants killed normally.
- Full build still fails teammate Checkstyle and SpotBugs main checks.
  Game/GameTest have no findings. These failures still prevent merge readiness.
- GitHub Actions run `45069380492` on `a536563` also failed
  `checkstyleMain`, `checkstyleTest` and `spotbugsMain`, matching the local
  build blockers. Passing Game checks are not a passing project CI result.
- Synchronization audit: local and remote Game heads both equal `a536563`;
  `dc674387` is its direct parent. `git ls-files -u` is empty, and
  `git merge-tree --write-tree HEAD dc674387` succeeds without conflicts.
  GitHub nevertheless reported CONFLICTING; that remote status is distinct
  from local unresolved conflicts and requires a refreshed mergeability check.

### Unresolved Project Blockers

- Checkstyle: Deck has literal card counts flagged as magic numbers; Player
  has spacing violations. DeckTest has long lines and variable-use distance
  violations; PlayerTest has import order, wildcard import and spacing issues.
- SpotBugs: Deck's throwing, non-final constructor triggers
  `CT_CONSTRUCTOR_THROW`; Player.getName compares strings using `==`,
  triggering `ES_COMPARING_STRINGS_WITH_EQ`.
- Deck.add mutation produces MEMORY_ERROR in the publication run; the cause
  has not been investigated and no mutation exemption is claimed.
- UI.Main remains empty. UI validation and any localized player-facing
  prompts or recovery text remain UI work; Game's internal exception messages
  do not need locale bundles or translation lookup.
- These are unresolved work, not approved exceptions. The author requested
  PR publication; successful CI and review are still required before merging.
