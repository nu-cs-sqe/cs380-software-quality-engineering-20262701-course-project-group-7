# Game BVA Analysis

## Scope

The first increment covers `Game(Deck)` and `isSetUp()` only.
`Deck` is supplied by another team member. Tests assume that it is in
package `domain` and provides `Deck.createStandardDeck(Random)`.
The tests are written, but execution is pending the real `Deck` implementation.

## Game(Deck deck)

| ID | Method under test | State / input | Expected output | Test implemented? |
|----|-------------------|---------------|-----------------|-------------------|
| GAME-01 | `Game(Deck)` | `deck` is `null` | Throws `IllegalArgumentException`; no game is created. | Yes: `constructorRejectsNullDeck` |
| GAME-02 | `Game(Deck)`, `isSetUp()` | A non-null standard deck; no setup has run. | Construction succeeds; `isSetUp()` returns `false`; the deck size is unchanged. | Yes: `constructorAcceptsDeckWithoutSettingUpOrDrawingCards` |

## isSetUp()

| ID | Method under test | State / input | Expected output | Test implemented? |
|----|-------------------|---------------|-----------------|-------------------|
| GAME-03 | `isSetUp()` | A newly constructed game; queried twice before setup. | Returns `false` on both calls. | Yes: `isSetUpRemainsFalseBeforeSetup` |
| GAME-04 | `isSetUp()` | A successfully initialized game. | Returns `true`. | No: depends on the setup increment. |

## Planned Setup Increment

These cases follow the existing design, but the setup signature and player
name handling must be agreed with the team before implementation.

| ID | Method under test | State / input | Expected output | Test implemented? |
|----|-------------------|---------------|-----------------|-------------------|
| GAME-05 | `setup` | A new game; 1 player. | Throws `IllegalArgumentException`; game remains uninitialized; deck is unchanged. | No |
| GAME-06 | `setup` | A new game; 2 players (lower valid boundary). | Each player gets 7 ordinary cards and 1 Defuse; 1 Exploding Kitten is returned; first player is current; game is initialized. | No |
| GAME-07 | `setup` | A new game; 3 players (next to lower boundary). | Each player gets 7 ordinary cards and 1 Defuse; 2 Exploding Kittens are returned; game is initialized. | No |
| GAME-08 | `setup` | A new game; 4 players (next to upper boundary). | Each player gets 7 ordinary cards and 1 Defuse; 3 Exploding Kittens are returned; game is initialized. | No |
| GAME-09 | `setup` | A new game; 5 players (upper valid boundary). | Each player gets 7 ordinary cards and 1 Defuse; 4 Exploding Kittens are returned; game is initialized. | No |
| GAME-10 | `setup` | A new game; 6 players. | Throws `IllegalArgumentException`; game remains uninitialized; deck is unchanged. | No |
| GAME-11 | `setup` | Setup has already succeeded; another setup request. | Throws `IllegalStateException("Game has already been set up.")`; existing state is unchanged. | No |
| GAME-12 | `setup` | The injected deck runs out while dealing. | Propagates the `IllegalStateException` from `Deck.draw()`; game is not marked initialized. Recovery behavior needs team agreement. | No |
| GAME-13 | `getCurrentPlayer()` | Setup has not run. | Throws `IllegalStateException("Game has not been set up.")`. | No |
| GAME-14 | `getCurrentPlayer()` | Setup has succeeded. | Returns the first player in setup order. | No |

## Open Interface Questions

- Confirm the package and public API of `Deck`, `Player`, and `CardType`.
- The design accepts a player count, but the user story also requires names.
  Agree how names reach `Game` and who validates empty or duplicate names.
- Confirm that returned Defuse count is the number removed minus player count.
- Follow the acceptance criteria: remove special cards, shuffle, deal, return
  special cards, then shuffle again. The algorithm table currently puts the
  first shuffle before removing special cards.
- Agree how tests and the UI inspect all players and the remaining deck.
- Agree whether a failed setup on a short custom deck can be retried.
