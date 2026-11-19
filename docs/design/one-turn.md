# One Turn Phase - Design Doc

## User Story
As a player of Exploding Kittens,
I want the game to guide me through my turn (playing optional cards, then drawing a card to end my turn),
so that I can take actions, follow the rules correctly, and pass play to the next player without tracking the rules manually.

## Acceptance Criteria
1. The game must clearly indicate whose turn it is and show that player's hand
2. The active player may play zero or more actions cards
3. The turn ends by the active player drawing exactly one card from the top
4. If the drawn card is a regular card, it is added to the active player’s hand and the turn passes to the next player
5. If the drawn card is an Exploding Kitten
   1. If the player has a Defuse card, the Defuse is discarded, the Exploding Kitten is reinserted into the pile at a position the player chooses and the turn ends
   2. If the player has no Defuse card, the player is eliminated and their hand is discarded
6. The game must only accept cards the active player actually holds
7. If only one player remains, the game ends. Otherwise, the turn order advances to the next remaining player.

# Use Case 2: Play One Turn

## Actor
Player

## Preconditions
- A game has been set up
- It is the active player's turn
- The pile contains at least one card

## Main Flow:
1. System announces the active player and displays their hand.
2. System prompts the player to either play a card or end their turn by drawing.
3. Player chooses to draw a card (no action card played).
4. System removes the top card from the draw pile.
5. System determines the drawn card is not an Exploding Kitten.
6. System adds the card to the player’s hand.
7. System ends the turn and advances to the next player in the turn order.
8. System checks for a winner (more than one player remains, so the game continues).

## Alternative Flow:
**2.a Player plays an action card:** 

    1. Player selects a card from their hand.
    2. System validates that the card is in the player’s hand and is playable.
    3. System moves the card to the discard pile and applies its effect.
    4. If the effect ends the turn (e.g., Skip), resume at Step 7. Otherwise, resume at Step 2.
**2.b Player selects an invalid card or action:**

    1. System indicates that the selection is invalid and explains why.
    2. Resume at Step 2.
**5.a Drawn card is an Exploding Kitten and the player has a Defuse card:**

    1. System discards the player’s Defuse card.
    2. System asks the player where to reinsert the Exploding Kitten in the draw pile.
    3. Player enters a position.
    4. System inserts the Exploding Kitten at that position.
    5. Resume at Step 7.
  **5.b Drawn card is an Exploding Kitten and the player has no Defuse card:**

    1. System eliminates the player and moves their hand to the discard pile.
    2. System removes the player from the turn order.
    3. Resume at Step 8.
  **5.c Player enters an invalid insertion position (in 5.a):**

    1.System indicates that the position is invalid.
    2.Resume at 5.a, Step 2.
  **8.a Only one player remains:**

    1. System declares that player the winner.
    2. System ends the game.

## Postconditions:
- The active player’s turn has ended and the draw pile has one fewer card (or the same count if an Exploding Kitten was reinserted).
- The active player either holds one additional card, has used a Defuse and reinserted the Exploding Kitten, or has been eliminated.
- The turn order points to the next remaining player, or the game has ended with a winner.
