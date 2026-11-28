# BVA Analysis for Game Class

Notes:
- The specification is `docs/design/game-setup-phase.md`. The UI validates the
  player count (2 to 5) and non-blank, case-sensitive unique names before
  calling `new Game(deck, playerNames)`, so the domain does not BVA or test
  invalid user input or nulls (team standards).
- `Game` keeps the given names and their order; `setup()` takes no arguments.
- Internal exception messages are plain English diagnostics, not locale keys.
- Drawable cards for dealing = ordinary cards + returned Defuses, where the
  returned Defuses = `min(2, defuses - players)`. Each player draws 7, so a
  2-player game needs 14 drawable cards.
- Preflight rejects a deck when `defuses < players`, `kittens < players - 1`,
  or `drawable < 7 * players`. A rejected `setup()` leaves the deck, the hands
  and `isSetUp()` unchanged, so the caller can replenish the deck and retry.

## Method 1: ```public void setup()```

### Step 1-3 Results

| Step | Input | Output |
|------|-------|--------|
| Step 1 | Deck contents; number of players and their names | Deck and hands after the official setup / `IllegalStateException` |
| Step 2 | Cases (not set up, already set up); Counts (players, cards per type); Boundaries (drawable cards below, at, above `7 * players`); Collection (deck contents); Exception | Mutated deck and hands / `IllegalStateException` |
| Step 3 | players: 2, 5 (boundaries) and 3, 4 (interior); defuses: 0, 1, 2, 3, 4, 6; kittens: 0, 1, 4; ordinary: 11, 12, 13, 14, 15 | 8-card hands and the official final deck, or `IllegalStateException` |

### Step 4:
##### All-combination or each-choice: each-choice

| Test Case # | System under test | Expected output | Implemented? |
|-------------|-------------------|-----------------|--------------|
| TC1 | standard deck, 2 players | each hand 8 cards starting with a Defuse, no Kittens in hands, input order kept, first player current; final deck 35 cards with 1 Kitten; 4 Defuses across hands and deck | y (setupFollowsOfficialRulesForEachPlayerCount) |
| TC2 | standard deck, 3 players | same shape as TC1; final deck 29 cards; 5 Defuses across hands and deck | y (setupFollowsOfficialRulesForEachPlayerCount) |
| TC3 | standard deck, 4 players | same shape as TC1; final deck 23 cards; 6 Defuses across hands and deck | y (setupFollowsOfficialRulesForEachPlayerCount) |
| TC4 | standard deck, 5 players | same shape as TC1; final deck 16 cards; 6 Defuses across hands and deck | y (setupFollowsOfficialRulesForEachPlayerCount) |
| TC5 | names `" A "` and `"a"` | names stored exactly, including whitespace and case | y (setupPreservesValidatedNamesExactly) |
| TC6 | caller changes and then clears the names list after construction | stored names and order unchanged | y (callerCannotChangeStoredNamesBeforeSetup) |
| TC7 | already set up, `setup()` called again | `IllegalStateException("Game has already been set up.")`; players, hands, deck and current player unchanged; still set up | y (repeatedSetupPreservesExistingState) |
| TC8 | empty deck (0 of every type) | insufficient-deck diagnostic; deck unchanged; not set up | y (setupRejectsEmptyDeckBeforeMutation) |
| TC9 | 2 players, 14 ordinary, 0 Defuses, 1 Kitten | insufficient-deck diagnostic; deck unchanged | y (setupRejectsInsufficientDefuses) |
| TC10 | 2 players, 14 ordinary, 1 Defuse, 1 Kitten | insufficient-deck diagnostic; deck unchanged | y (setupRejectsInsufficientDefuses) |
| TC11 | 2 players, 14 ordinary, 2 Defuses, 0 Kittens | insufficient-deck diagnostic; deck unchanged | y (setupRejectsInsufficientKittens) |
| TC12 | 2 players, 13 ordinary, 2 Defuses, 1 Kitten (13 drawable, one below the boundary) | insufficient-deck diagnostic; deck unchanged | y (setupRejectsOneFewerDrawableCardThanRequired) |
| TC13 | 2 players, 14 ordinary, 2 Defuses, 1 Kitten (exactly 14 drawable) | setup succeeds; each hand 8 cards with 1 Defuse; final deck 1 card with 1 Kitten | y (setupAcceptsDrawableCardBoundary) |
| TC14 | 2 players, 15 ordinary, 2 Defuses, 1 Kitten (15 drawable, just above) | setup succeeds; final deck 2 cards | y (setupAcceptsDrawableCardBoundary) |
| TC15 | 2 players, 13 ordinary, 3 Defuses, 1 Kitten (1 returned Defuse makes 14 drawable) | returned Defuse counts; setup succeeds; final deck 1 card; 3 Defuses across hands and deck | y (returnedDefusesCountTowardDrawableBoundary) |
| TC16 | 2 players, 12 ordinary, 4 Defuses, 1 Kitten (2 returned Defuses make 14 drawable) | both returned Defuses count; setup succeeds; final deck 1 card; 4 Defuses across hands and deck | y (returnedDefusesCountTowardDrawableBoundary) |
| TC17 | 2 players, 11 ordinary, 4 Defuses, 1 Kitten (13 drawable even with returns) | insufficient-deck diagnostic; deck unchanged | y (setupRejectsInsufficientDrawableCardsIncludingReturnedDefuses) |
| TC18 | 13 ordinary rejected, then 1 ordinary added and `setup()` retried | first call throws and does not mutate the deck; the retry succeeds and is set up | y (setupCanRetryAfterDeckIsReplenished) |
| TC19 | strict mock of a standard deck; 14 draws, the first one is a Defuse | official call order: remove Kittens, remove Defuses, give each player a Defuse, return extra Defuses, shuffle, 14 draws, return `players - 1` Kittens, shuffle; the dealt Defuse stays in hand (8 cards) | y (setupUsesOfficialCallOrderAndMayDealAdditionalDefuse) |
| TC20 | `Deck.draw()` throws during dealing | the same exception propagates; not set up; both queries reject; no rollback promised | y (setupPropagatesUnexpectedDrawFailure) |
| TC21 | `count(DEFUSE)` reports 6 but `removeAll(DEFUSE)` returns 3 | the returned count uses the actual removal result (1 extra), not the preflight count | y (extraDefusesUseActualRemovalCount) |

## Method 2: ```public Player getCurrentPlayer()```

### Step 1-3 Results

| Step | Input | Output |
|------|-------|--------|
| Step 1 | none (instance query) | The current player / `IllegalStateException` |
| Step 2 | Cases (set up, not set up) | `Player` / `IllegalStateException` |
| Step 3 | not set up; set up for 2, 3, 4, 5 players | the first player / `IllegalStateException` |

### Step 4:
##### All-combination or each-choice: each-choice

| Test Case # | System under test | Expected output | Implemented? |
|-------------|-------------------|-----------------|--------------|
| TC1 | game created, not set up yet | `IllegalStateException("Game has not been set up.")` | y (queriesRejectAccessBeforeSetup) |
| TC2 | set up with 2, 3, 4 or 5 players | the same instance as `getPlayers().get(0)` | y (setupFollowsOfficialRulesForEachPlayerCount) |
| TC3 | set up; query repeated several times | same instance each time; the turn does not advance | y (playerQueriesDoNotAdvanceTurnOrExposeMutableList) |
| TC4 | dealing failed during setup | `IllegalStateException("Game has not been set up.")` | y (setupPropagatesUnexpectedDrawFailure) |

## Method 3: ```public List<Player> getPlayers()```

### Step 1-3 Results

| Step | Input | Output |
|------|-------|--------|
| Step 1 | none (instance query) | The players in turn order / `IllegalStateException` |
| Step 2 | Cases (set up, not set up); Collection (order, size, mutability of the returned list) | `List<Player>` / `IllegalStateException` / `UnsupportedOperationException` |
| Step 3 | not set up; set up for 2, 3, 4, 5 players; clearing the returned list | players in input order / `IllegalStateException` / `UnsupportedOperationException` |

### Step 4:
##### All-combination or each-choice: each-choice

| Test Case # | System under test | Expected output | Implemented? |
|-------------|-------------------|-----------------|--------------|
| TC1 | game created, not set up yet | `IllegalStateException("Game has not been set up.")` | y (queriesRejectAccessBeforeSetup) |
| TC2 | set up with 2, 3, 4 or 5 players | all players in input order, 8 cards each starting with a Defuse, no Kittens | y (setupFollowsOfficialRulesForEachPlayerCount) |
| TC3 | set up; caller calls `clear()` on the returned list | `UnsupportedOperationException`; stored players and order unchanged | y (playerQueriesDoNotAdvanceTurnOrExposeMutableList) |
| TC4 | names `" A "` and `"a"` | the entered names are returned exactly, in order | y (setupPreservesValidatedNamesExactly) |
| TC5 | caller changed and cleared the names list after construction | the stored names and order are unchanged | y (callerCannotChangeStoredNamesBeforeSetup) |
| TC6 | dealing failed during setup | `IllegalStateException("Game has not been set up.")` | y (setupPropagatesUnexpectedDrawFailure) |

## Method 4: ```public boolean isSetUp()```

### Step 1-3 Results

| Step | Input | Output |
|------|-------|--------|
| Step 1 | none (instance query) | Whether setup has completed |
| Step 2 | n/a | `boolean` |
| Step 3 | new game; after a successful setup; after a rejected setup; after a failed setup | `false`, `true` |

### Step 4:
##### All-combination or each-choice: each-choice

| Test Case # | System under test | Expected output | Implemented? |
|-------------|-------------------|-----------------|--------------|
| TC1 | new game, strict mock deck | `false` on two calls; construction and the query do not call the deck | y (isSetUpRemainsFalseWithoutInspectingDeck) |
| TC2 | set up with 2, 3, 4 or 5 players | `true` | y (setupFollowsOfficialRulesForEachPlayerCount) |
| TC3 | preflight rejected the deck (empty, or insufficient Defuses, Kittens or drawable cards) | `false` | y (setupRejectsEmptyDeckBeforeMutation, setupRejectsInsufficientDefuses, setupRejectsInsufficientKittens, setupRejectsOneFewerDrawableCardThanRequired, setupRejectsInsufficientDrawableCardsIncludingReturnedDefuses) |
| TC4 | rejected deck replenished, `setup()` retried | `true` | y (setupCanRetryAfterDeckIsReplenished) |
| TC5 | `setup()` called again after a successful setup | stays `true` | y (repeatedSetupPreservesExistingState) |
| TC6 | `Deck.draw()` threw during setup | `false` | y (setupPropagatesUnexpectedDrawFailure) |
