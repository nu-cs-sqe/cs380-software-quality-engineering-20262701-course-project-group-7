# Game Setup Phase - Design Doc

## Additional game rules that the design follows beyond acceptance criteria

Source: official *Exploding Kittens* Original Edition rules sheet (Setup, steps 1–6).

1. **Original Edition deck with 56 cards:** 4 Exploding Kitten, 6 Defuse, 4 Attack, 4 Skip, 4 Favor, 4 Shuffle, 5 See the Future, 5 Nope, and 4 each of the 5 types of cat cards.
2. **Exploding and Defuse cards are removed before dealing cards to players**: so that no player is dealt an Exploding Kitten, and every player is guaranteed exactly 1 Defuse.
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
| `- Random random` | Injected for shuffling, so tests can pass a seeded `Random`. |
| `+ Deck(Random random)` | Creates an empty deck. Throws `IllegalArgumentException` if `random` is `null`. Lets tests build small decks (0, 1, few cards) with `add`. |
| `+ static Deck createStandardDeck(Random random)` | Builds the 56 card Original Edition deck with the counts in rule 1, in a fixed (unshuffled) order. Throws `IllegalArgumentException` if `random` is `null`. |
| `+ void shuffle()` | Shuffles the cards with `Collections.shuffle(cards, random)`. Size and card counts do not change. |
| `+ CardType draw()` | Removes and returns the top card. Throws `IllegalStateException("Cannot draw from an empty deck.")` if deck empty. |
| `+ int removeAll(CardType type)` | Removes every card of `type`, returns how many were removed (0 if none). Throws `IllegalArgumentException` if `type` is `null`. Used to remove Exploding and Defuse from deck. |
| `+ void add(CardType type, int count)` | Adds `count` cards of `type` to the bottom of the deck. `count = 0` does nothing. Throws `IllegalArgumentException` if `type` is `null` or `count < 0`. Used to put Exploding and Defuse back into the deck. |
| `+ int size()` | Number of cards in the deck. |
| `+ int count(CardType type)` | Number of cards of `type` in the deck. Throws `IllegalArgumentException` if `type` is `null`. Lets tests check the Defuse and Exploding Kitten counts after setup without drawing every card. |

The standard counts are kept in one constant map (`CardType` → count) inside `Deck`, not as literal numbers spread through `createStandardDeck`.

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

As a player of Exploding Kittens, I want the deck to be prepared and the cards dealt automatically according to the official rules once the players are set, so that I can begin playing without manually organizing the deck and distributing cards.

Acceptance Criteria
- [ ] The game must not start unless there are 2 to 5 players.
- [ ] An invalid number of players must be rejected immediately, before any player names are entered.
- [ ] Each player must have a non-empty name.
- [ ] No two players may have the same name.
- [ ] All Exploding Kitten and Defuse cards must be removed from the deck before dealing.
- [ ] Each player must receive exactly 1 Defuse card.
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
4a. Player enters an empty name:
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