# Game Setup Phase - Design Doc

## Additional game rules that the design follows beyond acceptance criteria

Source: official *Exploding Kittens* Original Edition rules sheet (Setup, steps 1–6).

1. **Original Edition deck with 56 cards:** 4 Exploding Kitten, 6 Defuse, 4 Attack, 4 Skip, 4 Favor, 4 Shuffle, 5 See the Future, 5 Nope, and 4 each of the 5 types of cat cards.
2. **Exploding and Defuse cards are removed before dealing cards to players**: so that no player is dealt an Exploding Kitten, and every player receives 1 guaranteed Defuse before the 7 cards are drawn.
3. **Only 2 of the remaining Defuse cards go back into the deck** after each player receives one (1 in a 5 player game, since only 1 is left); the rest are removed from the game. They go back before the 7 cards are dealt, so a player may be dealt a second Defuse.
4. **Extra Exploding Kittens are removed from the game:** only (number of players − 1) go back into the deck.
5. **Player 1 goes first:** the official rules let the group pick any player to go first. Here the turn order follows the order in which names are entered, so the group chooses who goes first by entering that player's name first.
6. **Faster Game Variant** (removing about one-third of the deck in 2 and 3 player games) is out of scope.

## Classes
### 1. `CardType` (enum, package `domain`)

**Responsibility:** identifies the kind of a card. The setup phase only needs the card type, so no card class is being implemented yet.

Values: `EXPLODING_KITTEN`, `DEFUSE`, `ATTACK`, `SKIP`, `FAVOR`, `SHUFFLE`, `SEE_THE_FUTURE`, `NOPE`, `TACOCAT`, `CATTERMELON`, `HAIRY_POTATO_CAT`, `BEARD_CAT`, `RAINBOW_RALPHING_CAT`.

### 2. `Deck` (class, package `domain`)

**Responsibility:** holds the draw pile and its order. It only provides operations; the order in which they are called during setup is decided by `Game`.

| Member | Description |
|--------|-------------|
| `- List<CardType> cards` | Index 0 is the top of the deck. |
| `- Random random` | Injected for shuffling, so tests can pass an EasyMock mock of `Random` and stub `nextInt`. |
| `+ Deck(Random random)` | Creates an empty deck. Simple constructor (only initializes fields), so it has no BVA or tests. Lets tests build small decks (0, 1, few cards) with `add`. |
| `+ static Deck createStandardDeck(Random random)` | Builds the 56 card Original Edition deck with the counts in rule 1, in a fixed (unshuffled) order. |
| `+ void shuffle()` | Shuffles the cards with `Collections.shuffle(cards, random)`. Size and card counts do not change. |
| `+ CardType draw()` | Removes and returns the top card. Throws `IllegalStateException("Cannot draw from an empty deck.")` if deck empty. |
| `+ int removeAll(CardType type)` | Removes every card of `type`, returns how many were removed (0 if none). Used to remove Exploding and Defuse from deck. |
| `+ void add(CardType type, int numberOfCards)` | Adds `numberOfCards` cards of `type` to the bottom of the deck. `numberOfCards = 0` does nothing. Throws `IllegalArgumentException` if `numberOfCards < 0`. Does not enforce official card limits; callers stay within them. Used to put Exploding and Defuse back into the deck. |
| `+ int size()` | Number of cards in the deck. |
| `+ int count(CardType type)` | Number of cards of `type` in the deck. Lets tests check the Defuse and Exploding Kitten counts after setup without drawing every card. |

Following the team code style standards, `Deck` does not check for `null` arguments: callers never pass `null`.

The standard counts are kept in one constant `EnumMap` (`CardType` → count) inside `Deck`, built from named constants, not as literal numbers spread through `createStandardDeck`. Because an `EnumMap` iterates in enum order, the unshuffled standard deck always has the same order.

### 3. `Player` (class, package `domain`)

**Responsibility:** a participant and the cards in their hand. Nothing game-play related is implemented yet.

| Member | Description |
|--------|-------------|
| `- String name` | |
| `- List<CardType> hand` | Player's current hand.|
| `+ Player(String name)` | Initialize with an empty hand. |
| `+ String getName()` | Getter for player name. |
| `+ void addToHand(CardType card)` | |
| `+ int getHandSize()` | |
| `+ int countInHand(CardType type)` | |
| `+ List<CardType> getHand()` | |

### 4. `Game` (final class, package `domain`)

**Responsibility:** creates players in input order, performs setup, and exposes
the initial player order. Turn advancement and card effects are out of scope
for this increment.

| Member | Description |
|--------|-------------|
| `- static final int INITIAL_HAND_SIZE = 7` | Number of cards drawn per player, in addition to the guaranteed Defuse. |
| `- static final int MAX_EXTRA_DEFUSES = 2` | Maximum number of extra Defuses returned to the deck (rule 3). |
| `- static final String INSUFFICIENT_DECK_MESSAGE` | Internal diagnostic `Deck does not contain enough cards for setup.` |
| `- static final String ALREADY_SET_UP_MESSAGE` | Internal diagnostic `Game has already been set up.` |
| `- static final String NOT_SET_UP_MESSAGE` | Internal diagnostic `Game has not been set up.` |
| `- final Deck deck` | Non-null collaborator injected by the caller. |
| `- final List<Player> players` | Owned Player entities created from validated names, in input order. |
| `- boolean isSetUp = false` | Becomes true only after all setup steps succeed. |
| `+ Game(Deck deck, List<String> playerNames)` | Creates players with empty hands in input order. Does not inspect or mutate the deck. Inputs are non-null and names are already validated by the UI. |
| `+ void setup()` | Runs Section 5 on the players created by the constructor. |
| `+ Player getCurrentPlayer()` | Returns the first player after setup, without advancing the turn. Throws `IllegalStateException("Game has not been set up.")` before successful setup. |
| `+ List<Player> getPlayers()` | Returns an unmodifiable copy of the list in input order after setup. The Player objects remain mutable. Throws `IllegalStateException("Game has not been set up.")` before successful setup. |
| `+ boolean isSetUp()` | Returns false before successful setup and true afterward; does not change state. |

The constructor preserves each validated name exactly as supplied. Later changes
to the caller's names list do not change the stored players or their order.
Game has no public constants, no player-count validator, and no
`currentPlayerIndex` in this increment.

Input validation belongs to the UI: the caller supplies 2 to 5 players with
non-empty, non-blank, unique names and non-null arguments. Game does not check
null or invalid user input. This follows
`docs/requirements/code_style_standards.md`; deck sufficiency is a separate
game-state check.

Setup failures:

- A repeated call after successful setup throws
  `IllegalStateException("Game has already been set up.")` without changing existing state.
- An insufficient deck throws
  `IllegalStateException("Deck does not contain enough cards for setup.")`
  before any card is moved. The deck and hands remain unchanged, so the caller
  can replenish the deck and retry.
- Unexpected Deck exceptions are propagated. `isSetUp` remains false, but
  cards may already have moved; there is no rollback guarantee. Use a fresh
  Game and deck after such a failure rather than retrying a partially dealt game.

Game-owned exception constants store meaningful English internal diagnostics,
not translation keys. Internal exception messages do not require i18n and
must not be used as resource-bundle lookup keys. This follows the author's
clarification of the team guidance and supersedes the earlier key-based design.
Any prompts or recovery messages shown to players belong to the UI and may
still require localization. The UI should not expose internal exception text
directly as player-facing copy.

## 5. `Game.setup()` algorithm

`n` is the number of players created by the constructor (`players.size()`).

| Step | Action |
|------|--------|
| 1 | If already set up, throw `IllegalStateException("Game has already been set up.")`. |
| 2 | Check that the deck has at least `n` Defuses, `n - 1` Kittens, and `INITIAL_HAND_SIZE * n` drawable cards after returning extra Defuses. Otherwise throw `IllegalStateException("Deck does not contain enough cards for setup.")` before mutation. |
| 3 | `deck.removeAll(EXPLODING_KITTEN)`; `removedDefuses = deck.removeAll(DEFUSE)`. |
| 4 | Give each player 1 `DEFUSE` (`addToHand`). |
| 5 | `deck.add(DEFUSE, min(MAX_EXTRA_DEFUSES, removedDefuses - n))`. The other Defuses are removed from the game. |
| 6 | `deck.shuffle()`. |
| 7 | Draw `INITIAL_HAND_SIZE` cards for each player and add them to that player's hand, processing players in input order. |
| 8 | `deck.add(EXPLODING_KITTEN, n - 1)`. The other Kittens are removed from the game. |
| 9 | `deck.shuffle()`. |
| 10 | Set `isSetUp = true`. `getCurrentPlayer()` now returns `players.get(0)`. |

The preflight uses `deck.count(DEFUSE)`, `deck.count(EXPLODING_KITTEN)`, and
`deck.size()`. Drawable cards are all non-special cards plus
`min(MAX_EXTRA_DEFUSES, defuses - n)` returned Defuses. Returned Defuses count
toward the seven-card-per-player boundary.

Step 5 uses the value returned by `removeAll`, so custom decks may return fewer
than two extra Defuses. A standard deck returns two for 2 to 4 players and one
for 5 players. The seven drawn cards may contain additional Defuses.

For standard decks with 2, 3, 4, or 5 players, final draw-pile sizes are
35, 29, 23, and 16 respectively; Defuse totals across hands and deck are
4, 5, 6, and 6. Game behavior and boundaries are recorded in `docs/bva/Game.md`
on the implementation branch.

### Additional: UI

1. Ask for the number of players. Reject numbers outside 2 to 5 immediately,
   before asking for any names. These bounds belong to the UI, not Game.
2. Ask for each player's name. Reject empty or whitespace-only names and
   duplicates. The current policy preserves spelling and compares names
   case-sensitively, so "A" and "a" are distinct.
3. Create `new Game(deck, validatedNames)` with non-null inputs in name-entry
   order, then call `game.setup()`.
4. After successful setup, query `getPlayers()` and `getCurrentPlayer()`.
   The returned list protects the order, not the mutable Player objects.
5. Treat Game exception messages as internal diagnostics, not locale keys.
   Keep any player-facing recovery text separate. Use a fresh game after
   unexpected collaborator failures.

# User Story
As a player of Exploding Kittens, I want to choose the number of players and enter each player's name when a new game starts, so that the game matches my group and I can tell whose turn it is.

As a player of Exploding Kittens, I want the deck to be prepared and the cards dealt automatically according to the official rules once the players are set, so that I can begin playing without manually organizing the deck and distributing cards.

Acceptance Criteria
- [ ] The game must not start unless there are 2 to 5 players.
- [ ] An invalid number of players must be rejected immediately, before any player names are entered.
- [ ] Each player must have a non-empty, non-blank name.
- [ ] No two players may have the same name.
- [ ] All Exploding Kitten and Defuse cards must be removed from the deck before dealing.
- [ ] Each player must receive 1 guaranteed Defuse before the 7 cards are drawn; those 7 cards may contain additional Defuses.
- [ ] 2 of the remaining Defuse cards (1 in a 5 player game) must be added back into the deck before the 7 cards are dealt; the rest are removed from the game.
- [ ] The deck must be shuffled before dealing the 7 cards.
- [ ] Each player must receive exactly 7 cards from the shuffled deck, for a hand of 8 cards in total.
- [ ] The number of Exploding Kitten cards added back into the deck must be one less than the number of players; the rest are removed from the game.
- [ ] The deck must be shuffled after adding the Exploding Kitten cards back in.
- [ ] Once setup is complete, the turn order must be set in the order the names were entered, starting with player 1.

# Use Case 1: Start New Game

## Actor
Player

## Preconditions
The game application is launched.

## Main Flow
1. Player selects "Start Game".
2. System asks for the total number of players.
3. Player enters the number of players.
4. Player enters a name for each player.
5. System removes all Exploding Kitten and Defuse cards from the deck.
6. System gives each player 1 Defuse card.
7. System returns 2 of the remaining Defuse cards to the deck (1 in a 5 player game) and removes the rest from the game.
8. System shuffles the deck.
9. System deals 7 cards to each player.
10. System returns (number of players − 1) Exploding Kitten cards to the deck and removes the rest from the game.
11. System shuffles the deck again.
12. System sets the initial turn order in the order the names were entered, starting with player 1.

## Alternative Flow
3a. Player enters a number less than 2 or greater than 5:
  1. System immediately displays an error message, before asking for any names.
  2. Return to step 2.
4a. Player enters an empty or whitespace-only name:
  1. System displays an error message.
  2. System asks for that player's name again.
4b. Player enters a name that is already taken:
  1. System displays an error message.
  2. System asks for that player's name again.

## Postconditions
- Each player has 8 cards (1 Defuse + 7 cards from the shuffled deck).
- The deck contains exactly (number of players − 1) Exploding Kitten cards.
- The deck plus all players' hands contain (number of players + 2) Defuse cards in total (6 in a 5 player game).
- The game is ready for the first turn, and player 1 is the current player.
