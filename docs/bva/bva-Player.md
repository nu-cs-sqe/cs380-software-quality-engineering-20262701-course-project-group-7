# BVA Analysis for Player Class

## Method 1: ```public Player(String name)```

### Step 1-3 Results

| Step | Input | Output |
|------|-------|--------|
| Step 1 | Player name | New player with that name and an empty hand, or exception |
| Step 2 | Reference type `String`  | `Player` instance / `IllegalArgumentException` |
| Step 3 | `null` (null pointer), `""` (empty string), `"A"` (one char), `"Alice"` (normal string) | exception / `Player`|

### Step 4:
##### All-combination or each-choice: each-choice

| Test Case # | System under test | Expected output | Implemented? |
|-------------|------------------|-----------------|--------------|
| TC1 | `new Player("Alice")` | `getName().equals("Alice")`, `getHandSize()==0` | y |
| TC2 | `new Player("A")` | `getName().equals("A")`, `getHandSize()==0` | y |
| TC3 | `new Player("")` | throws `IllegalArgumentException` with message `"player.constructor.emptyName"` | y |
| TC4 | `new Player(null)` | throws `IllegalArgumentException` with message `"player.constructor.nullName"` | y |

---

## Method 2: ```public String getName()```

### Step 1-3 Results

| Step | Input | Output |
|------|-------|--------|
| Step 1 | none (instance query) | Name passed to the constructor |
| Step 2 | n/a | `String` |
| Step 3 | name `"Alice"`, name `"A"` (shortest valid name) | same `String` returned |

### Step 4:
##### All-combination or each-choice: each-choice

| Test Case # | System under test | Expected output | Implemented? |
|-------------|------------------|-----------------|--------------|
| TC1 | `new Player("Alice").getName()` | `"Alice"` | y |
| TC2 | `new Player("A").getName()` | `"A"` | y |

---

## Method 3: ```public void addToHand(CardType card)```

### Step 1-3 Results

| Step | Input | Output |
|------|-------|--------|
| Step 1 | Card type to add (or `null`) | Hand grows by one, or exception |
| Step 2 | Enum reference `CardType`; hand `List<CardType>` | changed hand / `IllegalArgumentException` |
| Step 3 | valid `CardType` added to an empty hand, to a hand of size 1, a duplicate of a card already in hand, `null` | size 0→1 / size 1→2, added at the end / duplicate kept, size +1 / exception |

### Step 4:
##### All-combination or each-choice: each-choice

| Test Case # | System under test | Expected output | Implemented? |
|-------------|------------------|-----------------|--------------|
| TC1 | `addToHand(DEFUSE)` on empty hand | `getHandSize()==1`, `getHand().get(0)==DEFUSE` | y |
| TC2 | `addToHand(ATTACK)` on hand `[DEFUSE]` | `getHandSize()==2`, `getHand()` equals `[DEFUSE, ATTACK]` (appended at the end) | y |
| TC3 | `addToHand(DEFUSE)` on hand `[DEFUSE]` | `getHandSize()==2`, `countInHand(DEFUSE)==2` (duplicates allowed) | y |
| TC4 | `addToHand(null)` | throws `IllegalArgumentException` with message `"player.addToHand.nullCard"` | y |

---

## Method 4: ```public int getHandSize()```

### Step 1-3 Results

| Step | Input | Output |
|------|-------|--------|
| Step 1 | none (instance query) | Number of cards in hand |
| Step 2 | n/a | `int` (Catalog: Counts) |
| Step 3 | empty hand, hand of size 1, hand of size >1 | `0`, `1`, `2` |

### Step 4:
##### All-combination or each-choice: each-choice

| Test Case # | System under test | Expected output | Implemented? |
|-------------|------------------|-----------------|--------------|
| TC1 | new player | `0` | y |
| TC2 | after one `addToHand` | `1` | y |
| TC3 | after two `addToHand` | `2` | y |

---

## Method 5: ```public int countInHand(CardType type)```

### Step 1-3 Results

| Step | Input | Output |
|------|-------|--------|
| Step 1 | Card type to count | Number of cards of that type in hand |
| Step 2 | Enum reference `CardType` (Catalog: Cases, Pointers); hand `List<CardType>` (Catalog: Collections: size, duplicates, first/last element) | `int` (Catalog: Counts) |
| Step 3 | empty hand; type absent; type appears once as the first element; type appears once as the last element; type appears more than once; whole hand is that type; `null` | `0` / `0` / `1` / `1` / `>1` / `getHandSize()` / exception |

### Step 4:
##### All-combination or each-choice: each-choice

| Test Case # | System under test | Expected output | Implemented? |
|-------------|------------------|-----------------|--------------|
| TC1 | empty hand, `countInHand(DEFUSE)` | `0` | y |
| TC2 | hand `[DEFUSE]`, `countInHand(ATTACK)` | `0` | y |
| TC3 | hand `[DEFUSE, ATTACK]`, `countInHand(DEFUSE)` (first element) | `1` | y |
| TC4 | hand `[DEFUSE, ATTACK]`, `countInHand(ATTACK)` (last element) | `1` | y |
| TC5 | hand `[DEFUSE, ATTACK, DEFUSE]`, `countInHand(DEFUSE)` (duplicates, not adjacent) | `2` | y |
| TC6 | hand `[ATTACK, ATTACK]`, `countInHand(ATTACK)` (whole hand) | `2` | y |
| TC7 | hand `[DEFUSE]`, `countInHand(null)` | throws `IllegalArgumentException` with message `"player.countInHand.nullType"` | y |

---

## Method 6: ```public List<CardType> getHand()```

### Step 1-3 Results

| Step | Input | Output |
|------|-------|--------|
| Step 1 | none (instance query) | The cards in the hand, in the order they were added |
| Step 2 | n/a | `List<CardType>` (a copy, not the internal list) |
| Step 3 | empty hand, hand of size 1, hand of size >1 (order), changing the returned list | empty list / list of size 1 / insertion order kept / player's hand unchanged |

### Step 4:
##### All-combination or each-choice: each-choice

| Test Case # | System under test | Expected output | Implemented? |
|-------------|------------------|-----------------|--------------|
| TC1 | `getHand()` on new player | empty list (`size()==0`) | y |
| TC2 | `getHand()` after `addToHand(DEFUSE)` | list `[DEFUSE]` | y |
| TC3 | `getHand()` after `addToHand(DEFUSE)`, `addToHand(ATTACK)` | list `[DEFUSE, ATTACK]` (insertion order) | y |
| TC4 | call `add(ATTACK)` on the list returned by `getHand()`, then call `getHandSize()` | player's hand size is unchanged (the list is a copy) | y |

---
