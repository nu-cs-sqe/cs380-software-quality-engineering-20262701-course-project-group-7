# Game Setup Phase - Design Doc

## Additional game rules that the design follows beyond acceptance criteria

1. **Original Edition deck with 56 cards:** 4 Exploding Kitten, 6 Defuse, 4 Attack, 4 Skip, 4 Favor, 4 Shuffle, 5 See the Future, 5 Nope, and 4 each of the 5 types of cat cards.
2. **Exploding and Defuse cards are removed before dealing cards to players**: no player is dealt an Exploding Kitten, and each player is guaranteed one Defuse.
3. **Return up to two remaining Defuses before dealing the seven cards** (one in a standard five-player game). The rest are removed from the game. A player may therefore receive an additional Defuse among the seven cards.
4. **Return exactly one fewer Exploding Kitten than players** after dealing; remove the extra Kittens from the game.
5. **Player 1 goes first:** turn order follows the order in which the names are entered.

These setup rules follow the team's updated `game-setup-phase-design` branch.
Input validation follows `docs/requirements/code_style_standards.md`: the UI
validates all user input before creating domain objects.

## Classes
### 1. `CardType` (enum)

**Responsibility:** identifies the kind of a card. The setup phase only needs the card type, so no card class is being implemented yet.

Values: `EXPLODING_KITTEN`, `DEFUSE`, `ATTACK`, `SKIP`, `FAVOR`, `SHUFFLE`, `SEE_THE_FUTURE`, `NOPE`, `TACOCAT`, `CATTERMELON`, `HAIRY_POTATO_CAT`, `BEARD_CAT`, `RAINBOW_RALPHING_CAT`.

### 2. `Deck` (class)

**Responsibility:** holds the draw pile and its order.

| Member | Description |
|--------|-------------|
| `- List<CardType> cards` | Index 0 is the top of the deck. |
| `- Random random` | Injected for shuffling. |
| `+ static Deck createStandardDeck(Random random)` | Builds the base game deck. |
| `+ void shuffle()` | Shuffle the cards. |
| `+ CardType draw()` | Removes and returns the top card. Throws `IllegalStateException("Cannot draw from an empty deck.")` if deck empty. |
| `+ int removeAll(CardType type)` | Removes every card of `type`, returns how many were removed. Used to remove Exploding and Defuse from deck.|
| `+ void add(CardType type, int count)` | Adds `count` cards of `type`. Throws `IllegalArgumentException` if `count < 0`.  Used to add Exploding and Defuse from deck.|
| `+ int size()` | Number of cards in the deck. |
| `+ int count(CardType type)` | Number of cards of this type, used by Game's deck-state preflight. |

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

**Responsibility:** performs setup and tracks the turn order.

| Member | Description |
|--------|-------------|
| `- static final int INITIAL_HAND_SIZE = 7` | Ordinary draw count per player. |
| `- static final int MAX_EXTRA_DEFUSES = 2` | Maximum extra Defuses returned. |
| `- final Deck deck` | Injected, non-null collaborator. |
| `- final List<Player> players` | Owned player entities, created from validated names in turn order. |
| `- boolean isSetUp` | Prevents use before setup and repeated setup. |
| `+ Game(Deck deck, List<String> playerNames)` | Creates players with empty hands in input order. Does not inspect or change the deck. Arguments are non-null and names are already validated by the UI; no domain input validation. |
| `+ void setup()` | Runs Section 5 on the players created by the constructor. |
| `+ Player getCurrentPlayer()` | Returns player 1; throws `IllegalStateException("game.notSetUp")` before setup. No turn advancement is implemented in this increment. |
| `+ List<Player> getPlayers()` | Returns an unmodifiable copy in turn order; Player objects remain mutable. Throws `IllegalStateException("game.notSetUp")` before setup. |
| `+ boolean isSetUp()` | False before successful setup, true afterward. |

Setup can throw exceptions if:

- `IllegalStateException("game.alreadySetUp")` when called a second time.
- `IllegalStateException("game.insufficientDeck")` before mutation if the deck
  lacks the required Defuses, Kittens or drawable cards.
- Unexpected exceptions from Deck are propagated without publishing a successful
  setup; no rollback is promised after mutation starts.

There is no public player-count validator or public numeric constant in Game.
The UI owns the 2-to-5 player range, name validation and all null handling.
Game is final and does not support subclassing.

Exception message constants hold resource keys rather than user-facing text.
The UI resolves Game-owned errors with
`ResourceBundle.getBundle("message", locale).getString(exception.getMessage())`.
English and Chinese bundles are provided; the base bundle falls back to English.
Unexpected collaborator errors need the UI's generic localized error handler,
not a direct display of `exception.getMessage()`.

## 5. `Game.setup()` algorithm

`n` is the number of players created by the constructor.

| Step | Action |
|------|--------|
| 1 | If already set up, throw `IllegalStateException`. |
| 2 | Preflight: at least `n` Defuses, `n - 1` Kittens, and `7 * n` drawable cards after returning up to two extra Defuses. |
| 3 | Remove all Kittens, then all Defuses; retain the removed Defuse count. |
| 4 | Give each player one Defuse. |
| 5 | Add `min(MAX_EXTRA_DEFUSES, removedDefuses - n)` Defuses back to the deck. |
| 6 | Shuffle the deck. |
| 7 | Draw `INITIAL_HAND_SIZE` cards for each player. |
| 8 | Add `n - 1` Kittens back to the deck. |
| 9 | Shuffle the deck again. |
| 10 | Set `isSetUp = true`; the first player in the stored order is current. |


### Additional: UI

1. Ask for the number of players; reject values outside 2 to 5 immediately,
   before asking for names.
2. Ask for each name; reject empty or whitespace-only names and duplicates.
   The current name policy preserves spelling and compares names case-sensitively.
3. Create `new Game(deck, validatedNames)` and call `game.setup()`.
4. Translate known Game error keys through the locale bundle, never display
   a raw resource key or collaborator diagnostic to the user.

# User Story
As a player of Exploding Kittens, I want to choose the number of players and enter each player's name when a new game starts, so that the game matches my group and I can tell whose turn it is.

As a player of Exploding Kittens, I want the deck to be prepared and the cards dealt automatically once the number of players is set, so that I can begin playing without manually organizing the deck and distributing cards.

Acceptance Criteria
- [ ] The game must not start unless there are 2 to 5 players.
- [ ] Invalid player counts must be rejected by the UI before asking for names.
- [ ] Each player must have a non-empty, non-blank name.
- [ ] No two players may have the same name.
- [ ] All Exploding Kitten and Defuse cards must be removed from the deck before dealing.
- [ ] The deck must be shuffled before dealing.
- [ ] Each player must receive exactly 7 cards from the shuffled deck.
- [ ] Each player must receive 1 Defuse card before the 7 cards are drawn.
- [ ] Up to 2 remaining Defuses (1 in a standard five-player game) must be returned before the 7 cards are dealt; other Defuses are removed.
- [ ] The number of Exploding Kitten cards added back into the deck must be one less than the number of players.
- [ ] The deck must be shuffled after adding the Exploding Kitten and remaining Defuse cards back in.
- [ ] Once setup is complete, the turn order must be set, starting with player 1.

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
7. System returns up to 2 remaining Defuses to the deck (1 in a standard five-player game).
8. System shuffles the deck.
9. System deals 7 cards to each player.
10. System returns (number of players − 1) Exploding Kitten cards to the deck.
11. System shuffles the deck again.
12. System sets the initial turn order, starting with player 1.

## Alternative Flow
3a. Player enters a number less than 2 or greater than 5:
  1. System displays an error message.
  2. Return to step 2.
4a. Player enters an empty name:
  1. System displays an error message.
  2. System asks for that player's name again.
4b. Player enters a name that is already taken:
  1. System displays an error message.
  2. System asks for that player's name again.

## Postconditions
- Each player has 8 cards (7 cards from the shuffled deck + 1 Defuse).
- The deck contains (number of players - 1) Exploding Kitten cards.
- The hands and deck together contain (number of players + 2) Defuses, or 6 for five players.
- The game is ready for the first turn.
