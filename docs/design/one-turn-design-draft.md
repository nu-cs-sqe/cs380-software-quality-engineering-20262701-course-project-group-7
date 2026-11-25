# One Turn Phase - Design

This section follows the user story and use case for the One Turn phase. It
lists the classes and methods this phase adds, and the cards they are designed
for.

## 1. Proposed deck for this game

The game uses the Original Edition deck with **Nope removed**. The 5 Nope
cards are replaced by 5 invented cards, so the deck keeps 56 cards.
The setup phase is therefore unchanged.
In this specific phase, only Exploding Kittens and Defuse is implemented.
This deck will be applied when the invented card types are added in the
Cards phase and `game-setup-phase.md` rule 1 is updated at the same time.

| Card | Count |
|------|-------|
| Exploding Kitten | 4 |
| Defuse | 6 |
| See the Future | 5 |
| Attack | 4 |
| Skip | 4 |
| Favor | 4 |
| Shuffle | 4 |
| Cat cards (Tacocat, Cattermelon, Hairy Potato Cat, Beard Cat, Rainbow-Ralphing Cat) | 4 each (20) |
| Custom win condition card (invented) | 2 |
| Three other invented cards (TBD) | 1 each (3) |
| **Total** | **56** |

## 2. Capabilities and the cards that need them

Every new method exists because a rule or a card needs it.
**This is written to account for all original cards except Nope, no invented cards are included since they are not designed.**

| Capability | Method | Class | Needed by | Phase |
|------------|--------|-------|-----------|-------|
| Whose turn it is | `getCurrentPlayer()` | `Game` | every turn | One Turn |
| Pass the turn, or take another turn | `endTurn()`, `getTurnsRemaining()` | `Game` | every turn; Attack, Skip | One Turn |
| Draw to end a turn | `drawCard()` | `Game` | every turn | One Turn |
| Remove a player | `eliminate(Player)` | `Game` | Exploding Kitten | One Turn |
| Reinsert a card at a position | `insertAt(CardType, int)` | `Deck` | Defuse | One Turn |
| Use a Defuse | `reinsertExplodingKitten(int)` | `Game` | Defuse | One Turn |
| Discard cards | `add`, `addAll` | `DiscardPile` | Defuse, elimination, every played card | One Turn |
| Remove a card from a hand | `removeFromHand(CardType)` | `Player` | Defuse, every played card, Favor | One Turn |
| Empty a hand | `discardHand()` | `Player` | elimination | One Turn |
| End of game | `isGameOver()`, `getWinner()` | `Game` | elimination | One Turn |
| Look at the top cards | `peekTop(int)` | `Deck` | See the Future | One Turn (used in Cards) |
| Take a random card | `removeCardAt(int)` | `Player` | cat pairs | One Turn (used in Cards) |
| Shuffle the draw pile | `shuffle()` (exists) | `Deck` | Shuffle (used in Cards) | Setup |

## 3. Classes

### 3.1 `DrawResult` (enum, package `domain`)

**Responsibility:** tells the caller what happened when a card was drawn. This gets back to the UI to determine what to show accordingly.

| Value | Meaning | Next step for the UI |
|-------|---------|----------------------|
| `CARD_ADDED` | A card that is not an Exploding Kitten went to the hand; the turn ended. | Show the next player. |
| `DEFUSE_NEEDED` | An Exploding Kitten was drawn and the player has a Defuse. | Ask for a position, then call `reinsertExplodingKitten`. |
| `PLAYER_ELIMINATED` | An Exploding Kitten was drawn and the player has no Defuse. | Show the elimination, then check `isGameOver()`. |

### 3.2 `Deck` (additions)

| Member | Description |
|--------|-------------|
| `+ void insertAt(CardType type, int position)` | Inserts the card so that it becomes position `position` from the top: 0 is the top, `size()` is the bottom. The position is validated by the UI. |
| `+ List<CardType> peekTop(int count)` | Returns the top `count` cards, top first, without removing them; fewer if the deck has fewer cards. |

### 3.3 `Player` (additions)

| Member | Description |
|--------|-------------|
| `+ void removeFromHand(CardType card)` | Removes one copy of `card`. The caller only removes cards the player holds. |
| `+ CardType removeCardAt(int index)` | Removes and returns the card at `index` in the hand. |
| `+ List<CardType> discardHand()` | Empties the hand and returns the removed cards. |

### 3.4 `DiscardPile` (class, package `domain`)

**Responsibility:** holds played and discarded cards.

| Member | Description |
|--------|-------------|
| `- List<CardType> cards` | Discarded cards, most recent last. |
| `+ void add(CardType card)` | Adds one card. |
| `+ void addAll(List<CardType> cards)` | Adds several cards (an eliminated player's hand). |
| `+ int size()` | Number of discarded cards. |
| `+ List<CardType> getCards()` | Unmodifiable copy, most recent last. |

### 3.5 `Game` (additions and changes)

| Member | Description |
|--------|-------------|
| `- final DiscardPile discardPile` | Created by `Game`. |
| `- int currentPlayerIndex` | Index of the current player among the remaining players; 0 after setup. |
| `- int turnsRemaining` | Turns the current player still has to take; 1 except after an Attack. |
| `+ Player getCurrentPlayer()` *(changed)* | Returns the remaining player at `currentPlayerIndex`. |
| `+ List<Player> getRemainingPlayers()` | Players not eliminated, in turn order. |
| `+ int getTurnsRemaining()` | Turns the current player still has to take. |
| `+ void endTurn()` | If the current player has more than one turn remaining, decrements it; otherwise passes the turn to the next remaining player with 1 turn. |
| `+ DrawResult drawCard()` | See Section 4. |
| `+ void reinsertExplodingKitten(int position)` | Called after `drawCard()` returns `DEFUSE_NEEDED`. Removes a Defuse from the current player's hand and discards it, puts the Exploding Kitten at `position` with `Deck.insertAt`, and ends the turn. |
| `+ void eliminate(Player player)` | Moves the player's hand to the discard pile and removes the player from the remaining players. The next player is the one who would have played after them, with 1 turn. |
| `+ int getDrawPileSize()` | Number of cards in the draw pile, used by the UI to validate positions (0 to this size). |
| `+ boolean isGameOver()` | True when one player remains. |
| `+ Player getWinner()` | The remaining player; only meaningful once `isGameOver()` is true. |
| `+ List<CardType> getDiscardedCards()` | Copy of the discard pile, for display and tests. |

`Game` trusts its caller to use these methods in the right order. The only caller is the UI's `TurnController`, which
only asks for a position after `DEFUSE_NEEDED`, only enables Draw otherwise, and only calls `getWinner()` once `isGameOver()` is true. 
The controller will be implemented with tests check that order.

## 4. `drawCard()` behavior

| Step | Action |
|------|--------|
| 1 | `card = deck.draw()`. |
| 2 | If `card` is not an Exploding Kitten: add it to the current player's hand, call `endTurn()`, return `CARD_ADDED`. |
| 3 | If the current player has a Defuse (`countInHand(DEFUSE) > 0`): return `DEFUSE_NEEDED`. The turn continues until `reinsertExplodingKitten` is called. |
| 4 | Otherwise: `eliminate(currentPlayer)`; the Exploding Kitten is removed from the game. Return `PLAYER_ELIMINATED`. |

## 5. UI (MVC with JavaFX)

The turn screen follows the same MVC structure as the setup UI.

### 5.1 `TurnView` (class, package `ui.view`)

| Member | Description |
|--------|-------------|
| `+ Parent getRoot()` | Root node. |
| `+ void setOnDraw(Runnable handler)` | Called when Draw is pressed. |
| `+ void setOnReinsert(Runnable handler)` | Called when the reinsert position is confirmed. |
| `+ void showTurn(String playerName, List<CardType> hand)` | Shows whose turn it is and only that player's hand. Card names come from the bundle (Section 7). |
| `+ void showReinsertPrompt(int maxPosition)` | Asks for a position from 0 to `maxPosition`. |
| `+ String getEnteredPosition()` | The position text as typed. |
| `+ void showEliminated(String playerName)` | Shows that a player was eliminated. |
| `+ void showWinner(String playerName)` | Shows the winner and ends play. |
| `+ void showError(String message)`, `+ void clearError()` | Already translated messages. |
| `+ void updateText(ResourceBundle bundle)` | Sets all text from the bundle. |

### 5.2 `TurnController` (class, package `ui.controller`)

| Member | Description |
|--------|-------------|
| `+ TurnController(TurnView view, Game game, ResourceBundle bundle)` | Registers the handlers and shows the first turn. |
| `+ static boolean isValidPosition(String text, int drawPileSize)` | True if `text` is a whole number from 0 to `drawPileSize`. |
| `- void handleDraw()` | Calls `game.drawCard()` and responds to the `DrawResult`: show the next turn, show the reinsert prompt, or show the elimination and then the winner if `isGameOver()`. |
| `- void handleReinsert()` | Validates the entered position; shows an error and asks again if invalid, otherwise calls `game.reinsertExplodingKitten(position)` and shows the next turn. |

All controller methods will have BVA and will be tested with a mocked view.

## 6. Addition: `ScreenRouter`

A small router switches the window between the setup screen and the turn
screen so controllers do not create other screens.

### 6.1 `ScreenRouter` (interface, package `ui.navigation`)

| Member | Description |
|--------|-------------|
| `+ void showSetup()` | Shows the setup screen. |
| `+ void showTurn(Game game, ResourceBundle bundle)` | Shows the turn screen for a set-up game, in the language chosen during setup. |

NOTE: controllers depend on this interface.

### 6.2 `JavaFxScreenRouter` (class, package `ui.navigation`)

**Responsibility:** JavaFX wiring only. Holds the `Stage`, creates each
screen's view and controller, and replaces the scene's root.

| Member | Description |
|--------|-------------|
| `+ JavaFxScreenRouter(Stage stage)` | Stores the window. |
| `+ void showSetup()` | Creates `GameSetupView` and `GameSetupController` and shows the view. |
| `+ void showTurn(Game game, ResourceBundle bundle)` | Creates `TurnView` and `TurnController` and shows the view. |

It contains no logic, so it is excluded from coverage and mutation testing.

### 6.3 Changes to the setup screen

- `Main.start` creates `JavaFxScreenRouter` and calls `showSetup()`.
- `GameSetupController` receives the `ScreenRouter` in its constructor.
- After a successful setup, the setup screen shows the turn order (as now)
  and a Begin button. Its handler calls
  `router.showTurn(game, bundle)`, so the turn screen starts with the chosen
  language.
- `GameSetupView` gains `setOnBegin(Runnable handler)` to match the new begin button after showing player list.

## 7. Card names i18n

Card types are shown to players, and their names will be internationalized.
