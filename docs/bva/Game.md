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

## Method Under Test: `setup()`

Player-count boundaries are 2 and 5; 3 and 4 are interior cases. Deck
availability is tested below, at and above the required drawable-card boundary.
Invalid UI inputs are outside the domain contract.

| ID | Method(s) under test | System state / input | Expected output | Implemented? |
|----|----------------------|----------------------|-----------------|--------------|
| GAME-02 | `setup`, `isSetUp`, player queries | Standard deck; 2, 3, 4, 5 validated names. | Eight cards each, guaranteed first Defuse, no hand Kittens, input order, first player current, correct final counts. | Yes: `setupFollowsOfficialRulesForEachPlayerCount` |
| GAME-03 | `setup`, `getPlayers` | Valid names `" A "` and `"a"`. | Exact spelling and whitespace preserved. | Yes: `setupPreservesValidatedNamesExactly` |
| GAME-04 | `setup`, player queries | Caller changes and clears names after construction, before setup. | Stored player names and order unchanged. | Yes: `callerCannotChangeStoredNamesBeforeSetup` |
| GAME-05 | `setup`, player queries | Already set up; call again. | Already-set-up English diagnostic; deck, both hands, players and current player unchanged. | Yes: `repeatedSetupPreservesExistingState` |
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

## Method Under Test: `getCurrentPlayer()`

| ID | System state | Expected output | Implemented? |
|----|--------------|-----------------|--------------|
| GAME-06 | Newly constructed game, before setup. | `IllegalStateException("Game has not been set up.")`. | Yes: `queriesRejectAccessBeforeSetup` |
| GAME-18 | Successful setup with 2, 3, 4 or 5 players. | Same Player instance as the first entry of `getPlayers()`. | Yes: `setupFollowsOfficialRulesForEachPlayerCount` (GAME-02) |
| GAME-19 | Set up; repeat queries and attempt player-list mutation. | Current Player identity unchanged; queries do not advance the turn. | Yes: `playerQueriesDoNotAdvanceTurnOrExposeMutableList` |
| GAME-20 | Dealing threw an unexpected Deck exception. | Not-set-up diagnostic; partial setup is not exposed. | Yes: `setupPropagatesUnexpectedDrawFailure` (GAME-16) |

## Method Under Test: `getPlayers()`

| ID | System state | Expected output | Implemented? |
|----|--------------|-----------------|--------------|
| GAME-07 | Set up; caller attempts to clear returned list. | `UnsupportedOperationException`; stored player order and count unchanged. | Yes: `playerQueriesDoNotAdvanceTurnOrExposeMutableList` |
| GAME-21 | Newly constructed game, before setup. | `IllegalStateException("Game has not been set up.")`. | Yes: `queriesRejectAccessBeforeSetup` (GAME-06) |
| GAME-22 | Successful setup with 2, 3, 4 or 5 players. | All players in input order with eight-card hands, guaranteed first Defuse and no Kittens. | Yes: `setupFollowsOfficialRulesForEachPlayerCount` (GAME-02) |
| GAME-23 | Names contain validated whitespace/case distinctions. | Names preserved exactly. | Yes: `setupPreservesValidatedNamesExactly` (GAME-03) |
| GAME-24 | Caller changed and cleared names after construction. | Original player names and order preserved. | Yes: `callerCannotChangeStoredNamesBeforeSetup` (GAME-04) |
| GAME-25 | Dealing threw an unexpected Deck exception. | Not-set-up diagnostic; partial setup is not exposed. | Yes: `setupPropagatesUnexpectedDrawFailure` (GAME-16) |

## Method Under Test: `isSetUp()`

| ID | System state | Expected output | Implemented? |
|----|--------------|-----------------|--------------|
| GAME-01 | Newly constructed game; strict mock expects no Deck calls. | False on repeated queries; constructor and query do not access Deck. | Yes: `isSetUpRemainsFalseWithoutInspectingDeck` |
| GAME-26 | Setup completed for 2, 3, 4 or 5 players. | True. | Yes: `setupFollowsOfficialRulesForEachPlayerCount` (GAME-02) |
| GAME-27 | Insufficient deck rejected before mutation. | False. | Yes: rejection tests in GAME-08 through GAME-10 and GAME-13 |
| GAME-28 | Insufficient deck replenished; retry completed. | True. | Yes: `setupCanRetryAfterDeckIsReplenished` (GAME-14) |
| GAME-29 | Repeated setup rejected after successful setup. | Remains true. | Yes: `repeatedSetupPreservesExistingState` (GAME-05) |
| GAME-30 | Unexpected Deck draw failure during setup. | False. | Yes: `setupPropagatesUnexpectedDrawFailure` (GAME-16) |

The constructor initializes players without validating UI inputs. Its name
preservation and collection-ownership properties are covered through the
queries above. Additional IDs make existing cross-method assertions explicit;
they do not represent newly introduced tests. TDD history limitations are
recorded in README's Exceptions section, not as execution journals here.
