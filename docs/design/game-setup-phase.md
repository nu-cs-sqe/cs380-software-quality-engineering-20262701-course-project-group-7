# Game Setup Phase - Design Doc

## Additional game rules that the design follows beyond acceptance criteria

1. **Original Edition deck with 56 cards:** 4 Exploding Kitten, 6 Defuse, 4 Attack, 4 Skip, 4 Favor, 4 Shuffle, 5 See the Future, 5 Nope, and 4 each of the 5 types of cat cards.
2. **Exploding and Defuse cards are removed before dealing cards to players**: to prevent players being dealt Defuse or Exploding as one of the 7 cards.
3. **Remaining Defuse cards go back into the deck** after each player receives one, defuse cards go back into the deck.

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

### 4. `Game` (class, package `domain`)

**Responsibility:** performs setup and tracks the turn order.

| Member | Description |
|--------|-------------|
| `+ static final int MIN_PLAYERS = 2`, `MAX_PLAYERS = 5` | |
| `+ static final int INITIAL_HAND_SIZE = 7` | |
| `- Deck deck` | Injected. |
| `- List<Player> players` | In turn order. |
| `- int currentPlayerIndex` | Index into `players` for turn order tracking. |
| `- boolean isSetUp` | Prevents use before setup and repeated setup. |
| `+ Game(Deck deck)` | Throws `IllegalArgumentException` if `deck` is `null`. |
| `+ void setup(int numberOfPlayers)` | Runs the setup steps. |
| `+ Player getCurrentPlayer()` | Throws `IllegalStateException("Game has not been set up.")` before setup. |
| `+ boolean isSetUp()` | |

Setup can throw exceptions if:

- `IllegalArgumentException("Number of players must be between 2 and 5.")` when `numberOfPlayers < 2` or `> 5` (AC1).
- `IllegalStateException("Game has already been set up.")` when called a second time.
- `IllegalStateException` from `Deck.draw()` if the deck runs out (cannot happen with the standard deck, but `Game` does not hide it).


## 5. `Game.setup(numberOfPlayers)` algorithm

| Step | Action |
|------|--------|
| 1 | If already set up, throw `IllegalStateException`. |
| 2 | If `numberOfPlayers` is not in [2, 5], throw `IllegalArgumentException`. |
| 3 | `deck.shuffle()` |
| 4 | `deck.removeAll(EXPLODING_KITTEN)` and `deck.removeAll(DEFUSE)`. |
| 5 | Create `numberOfPlayers`|
| 6 | For each player, `draw()` 7 cards into their hand. |
| 7 | Give each player 1 `DEFUSE`. |
| 8 | `deck.add(DEFUSE, k)` |
| 9 | `deck.add(EXPLODING_KITTEN, numberOfPlayers - 1)` |
| 10 | `deck.shuffle()` |
| 11 | `currentPlayerIndex = 0`; `isSetUp = true`. |


### Additional: UI

1. Asks for the number of players and names.
2. Calls `game.setup(n)`.
3. On `IllegalArgumentException`, shows the exception message and asks again.

# User Story
As a player of Exploding Kittens, I want to choose the number of players and enter each player's name when a new game starts, so that the game matches my group and I can tell whose turn it is.

As a player of Exploding Kittens, I want the deck to be prepared and the cards dealt automatically once the number of players is set, so that I can begin playing without manually organizing the deck and distributing cards.

Acceptance Criteria
- [ ] The game must not start unless there are 2 to 5 players.
- [ ] Each player must have a non-empty name.
- [ ] No two players may have the same name.
- [ ] All Exploding Kitten and Defuse cards must be removed from the deck before dealing.
- [ ] The deck must be shuffled before dealing.
- [ ] Each player must receive exactly 7 cards from the shuffled deck.
- [ ] Each player must receive 1 Defuse card in addition to the 7 cards.
- [ ] The remaining Defuse cards must be added back into the deck.
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
6. System shuffles the deck.
7. System deals 7 cards to each player.
8. System gives each player 1 Defuse card.
9. System returns the remaining Defuse cards to the deck.
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
- The deck contains (number of players − 1) Exploding Kitten cards and the remaining Defuse cards.
- The game is ready for the first turn.