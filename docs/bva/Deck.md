# BVA Analysis for Deck Class

Notes:
- "Deck [A, B]" means A is the top card and B is below it; `add` puts cards at the bottom.
- `Deck` has no capacity limit; the standard deck (56 cards) is the largest deck in the game.
- `mockRandom = EasyMock.createMock(Random.class)`. Only `shuffle` tests stub it, with `expect(mockRandom.nextInt(...)).andStubReturn(...)`.
- `shuffle` uses Fisher–Yates: for `i` from `size − 1` down to `1`, `j = random.nextInt(i + 1)`, then swap cards `i` and `j`.

## Method 1: `public int size()`

### Step 1–3 Results

| Step | Input | Output |
|------|-------|--------|
| Step 1 | Deck state | Number of cards |
| Step 2 | Collection (sizes, contents) | Count |
| Step 3 | deck sizes: 0, 1, 3, 56 (duplicates) | 0, 1, 3, 56 |

### Step 4:
each-choice

| Test Case # | System under test | Expected output | Implemented? |
|-------------|-------------------|-----------------|--------------|
| TC1 | empty deck | 0 | yes |
| TC2 | Deck [ATTACK] | 1 | yes |
| TC3 | Deck [ATTACK, SKIP, FAVOR] | 3 | yes |
| TC4 | standard deck | 56 | yes (Method 6 TC1) |

## Method 2: `public void add(CardType type, int numberOfCards)`

### Step 1–3 Results

| Step | Input | Output |
|------|-------|--------|
| Step 1 | Card type, number of cards, deck state | Whole deck: existing cards unchanged, then all `numberOfCards` new cards at the bottom / exception |
| Step 2 | Cases, Count, Collection (sizes, contents, appending) | Collection / IllegalArgumentException |
| Step 3 | type: 13 CardType values; numberOfCards: −1, 0, 1, 3, each type's official count (4, 5, 6); deck sizes: 0, 1, 2 (duplicates), 52 → 56 (maximum possible size) | size + numberOfCards / IllegalArgumentException |

### Step 4:
each-choice

| Test Case # | System under test | Expected output | Implemented? |
|-------------|-------------------|-----------------|--------------|
| TC1 | Deck [SKIP], `add(ATTACK, -1)` | throws IllegalArgumentException; size = 1; draw = SKIP | yes |
| TC2 | empty deck, `add(ATTACK, 0)` | size = 0 | yes |
| TC3 | empty deck, `add(ATTACK, 1)` | size = 1; draw = ATTACK | yes |
| TC4 | Deck [SKIP], `add(DEFUSE, 3)` | size = 4; draws = SKIP, DEFUSE, DEFUSE, DEFUSE | yes |
| TC5 | empty deck, `add(EXPLODING_KITTEN, 4)` | size = 4; count(EXPLODING_KITTEN) = 4 | yes |
| TC6 | Deck [ATTACK], `add(DEFUSE, 6)` | size = 7; count(DEFUSE) = 6; first draw = ATTACK | yes |
| TC7 | Deck [SKIP, FAVOR], `add(ATTACK, 4)` | size = 6; count(ATTACK) = 4; first draw = SKIP | yes |
| TC8 | empty deck, `add(SKIP, 4)` | size = 4; count(SKIP) = 4 | yes |
| TC9 | Deck [NOPE], `add(FAVOR, 4)` | size = 5; count(FAVOR) = 4; first draw = NOPE | yes |
| TC10 | Deck [DEFUSE, DEFUSE], `add(SHUFFLE, 4)` | size = 6; count(SHUFFLE) = 4; first draw = DEFUSE | yes |
| TC11 | empty deck, `add(SEE_THE_FUTURE, 5)` | size = 5; count(SEE_THE_FUTURE) = 5 | yes |
| TC12 | Deck [TACOCAT], `add(NOPE, 5)` | size = 6; count(NOPE) = 5; first draw = TACOCAT | yes |
| TC13 | Deck [ATTACK, SKIP, FAVOR], `add(TACOCAT, 4)` | size = 7; count(TACOCAT) = 4; first draw = ATTACK | yes |
| TC14 | empty deck, `add(CATTERMELON, 4)` | size = 4; count(CATTERMELON) = 4 | yes |
| TC15 | Deck [BEARD_CAT], `add(HAIRY_POTATO_CAT, 4)` | size = 5; count(HAIRY_POTATO_CAT) = 4; first draw = BEARD_CAT | yes |
| TC16 | Deck [NOPE, NOPE], `add(BEARD_CAT, 4)` | size = 6; count(BEARD_CAT) = 4; first draw = NOPE | yes |
| TC17 | Deck [SHUFFLE], `add(RAINBOW_RALPHING_CAT, 4)` | size = 5; count(RAINBOW_RALPHING_CAT) = 4; first draw = SHUFFLE | yes |
| TC18 | Deck [DEFUSE, DEFUSE], `add(DEFUSE, 1)` | size = 3; count(DEFUSE) = 3 | yes |
| TC19 | standard deck after `removeAll(EXPLODING_KITTEN)` (52 cards), `add(EXPLODING_KITTEN, 4)` | size = 56; count(EXPLODING_KITTEN) = 4; last 4 draws = EXPLODING_KITTEN | yes |

## Method 3: `public CardType draw()`

### Step 1–3 Results

| Step | Input | Output |
|------|-------|--------|
| Step 1 | Deck state | Top card removed and returned / exception |
| Step 2 | Collection (sizes, contents, using an element) | Cases (CardType) / IllegalStateException |
| Step 3 | deck sizes: 0, 1, 3, 56; duplicates; first and last element | each of the 13 CardType values; size − 1 / IllegalStateException |

### Step 4:
each-choice

| Test Case # | System under test | Expected output | Implemented? |
|-------------|-------------------|-----------------|--------------|
| TC1 | empty deck | throws IllegalStateException | yes |
| TC2 | Deck [NOPE] | NOPE; size = 0 | yes |
| TC3 | Deck [SKIP, ATTACK, FAVOR] | SKIP; size = 2 | yes |
| TC4 | Deck [SKIP, ATTACK, FAVOR], draw 4 times | SKIP, ATTACK, FAVOR, then throws IllegalStateException | yes |
| TC5 | standard deck, draw returns `t` | size = 55; count(t) = standard count − 1 | yes |
| TC6 | Deck [SKIP, SKIP, ATTACK] | SKIP; size = 2; count(SKIP) = 1 | yes |
| TC7 | one of each type, in enum order; draw 13 times | each CardType once, in enum order | yes |

## Method 4: `public int count(CardType type)`

### Step 1–3 Results

| Step | Input | Output |
|------|-------|--------|
| Step 1 | Card type, deck state | Number of cards of that type |
| Step 2 | Cases, Collection (sizes, contents, searching) | Count |
| Step 3 | type: 13 CardType values; deck: empty, 1 card, no match, match first, match last, several matches, all match | 0, 1, 2, size |

### Step 4:
each-choice

| Test Case # | System under test | Expected output | Implemented? |
|-------------|-------------------|-----------------|--------------|
| TC1 | empty deck, `count(DEFUSE)` | 0 | yes |
| TC2 | Deck [DEFUSE], `count(DEFUSE)` | 1 | yes |
| TC3 | Deck [ATTACK, SKIP], `count(DEFUSE)` | 0 | yes |
| TC4 | Deck [DEFUSE, ATTACK, SKIP], `count(DEFUSE)` | 1 | yes |
| TC5 | Deck [ATTACK, SKIP, DEFUSE], `count(DEFUSE)` | 1 | yes |
| TC6 | Deck [DEFUSE, ATTACK, DEFUSE], `count(DEFUSE)` | 2 | yes |
| TC7 | Deck [DEFUSE, DEFUSE, DEFUSE], `count(DEFUSE)` | 3 | yes |
| TC8 | Deck [EXPLODING_KITTEN, DEFUSE], `count(EXPLODING_KITTEN)` | 1 | yes |
| TC9 | Deck [ATTACK, DEFUSE, DEFUSE], `count(DEFUSE)` | 2 | yes |
| TC10 | Deck [ATTACK], `count(ATTACK)` | 1 | yes |
| TC11 | Deck [SKIP, NOPE, SKIP, SKIP], `count(SKIP)` | 3 | yes |
| TC12 | Deck [ATTACK, SKIP], `count(FAVOR)` | 0 | yes |
| TC13 | Deck [NOPE, SHUFFLE], `count(SHUFFLE)` | 1 | yes |
| TC14 | Deck [SEE_THE_FUTURE, SEE_THE_FUTURE], `count(SEE_THE_FUTURE)` | 2 | yes |
| TC15 | empty deck, `count(NOPE)` | 0 | yes |
| TC16 | Deck [TACOCAT, CATTERMELON, TACOCAT], `count(TACOCAT)` | 2 | yes |
| TC17 | Deck [TACOCAT, CATTERMELON], `count(CATTERMELON)` | 1 | yes |
| TC18 | Deck [BEARD_CAT, HAIRY_POTATO_CAT, BEARD_CAT], `count(HAIRY_POTATO_CAT)` | 1 | yes |
| TC19 | standard deck, `count(BEARD_CAT)` | 4 | yes |
| TC20 | one of each type, `count(RAINBOW_RALPHING_CAT)` | 1 | yes |

## Method 5: `public int removeAll(CardType type)`

### Step 1–3 Results

| Step | Input | Output |
|------|-------|--------|
| Step 1 | Card type, deck state | Number removed; other cards keep their order |
| Step 2 | Cases, Collection (sizes, contents, searching, continued searching, deleting, subset) | Count, Collection |
| Step 3 | type: 13 CardType values; deck: empty, 1 card, no match, match first, match last, several matches, adjacent matches, all but one match, all match | 0, 1, 2, 3, size |

### Step 4:
each-choice

| Test Case # | System under test | Expected output | Implemented? |
|-------------|-------------------|-----------------|--------------|
| TC1 | empty deck, `removeAll(DEFUSE)` | 0; size = 0 | yes |
| TC2 | Deck [DEFUSE], `removeAll(DEFUSE)` | 1; size = 0 | yes |
| TC3 | Deck [ATTACK, SKIP], `removeAll(DEFUSE)` | 0; draws = ATTACK, SKIP | yes |
| TC4 | Deck [DEFUSE, ATTACK, SKIP], `removeAll(DEFUSE)` | 1; draws = ATTACK, SKIP | yes |
| TC5 | Deck [ATTACK, SKIP, DEFUSE], `removeAll(DEFUSE)` | 1; draws = ATTACK, SKIP | yes |
| TC6 | Deck [DEFUSE, ATTACK, DEFUSE, SKIP, DEFUSE], `removeAll(DEFUSE)` | 3; draws = ATTACK, SKIP | yes |
| TC7 | Deck [ATTACK, DEFUSE, DEFUSE, SKIP], `removeAll(DEFUSE)` | 2; draws = ATTACK, SKIP | yes |
| TC8 | Deck [DEFUSE, DEFUSE, ATTACK], `removeAll(DEFUSE)` | 2; draws = ATTACK | yes |
| TC9 | Deck [DEFUSE, DEFUSE], `removeAll(DEFUSE)` | 2; size = 0 | yes |
| TC10 | Deck [EXPLODING_KITTEN, DEFUSE], `removeAll(EXPLODING_KITTEN)` | 1; draws = DEFUSE | yes |
| TC11 | Deck [DEFUSE, ATTACK, DEFUSE], `removeAll(DEFUSE)` | 2; draws = ATTACK | yes |
| TC12 | Deck [ATTACK], `removeAll(ATTACK)` | 1; size = 0 | yes |
| TC13 | Deck [SKIP, NOPE, SKIP, SKIP], `removeAll(SKIP)` | 3; draws = NOPE | yes |
| TC14 | Deck [ATTACK, SKIP], `removeAll(FAVOR)` | 0; draws = ATTACK, SKIP | yes |
| TC15 | Deck [NOPE, SHUFFLE], `removeAll(SHUFFLE)` | 1; draws = NOPE | yes |
| TC16 | Deck [SEE_THE_FUTURE, SEE_THE_FUTURE], `removeAll(SEE_THE_FUTURE)` | 2; size = 0 | yes |
| TC17 | empty deck, `removeAll(NOPE)` | 0; size = 0 | yes |
| TC18 | Deck [TACOCAT, CATTERMELON, TACOCAT], `removeAll(TACOCAT)` | 2; draws = CATTERMELON | yes |
| TC19 | Deck [TACOCAT, CATTERMELON, BEARD_CAT], `removeAll(CATTERMELON)` | 1; draws = TACOCAT, BEARD_CAT | yes |
| TC20 | Deck [HAIRY_POTATO_CAT, BEARD_CAT, HAIRY_POTATO_CAT, HAIRY_POTATO_CAT], `removeAll(HAIRY_POTATO_CAT)` | 3; draws = BEARD_CAT | yes |
| TC21 | standard deck, `removeAll(BEARD_CAT)` | 4; size = 52; count(BEARD_CAT) = 0 | yes |
| TC22 | one of each type, `removeAll(RAINBOW_RALPHING_CAT)` | 1; size = 12; count(RAINBOW_RALPHING_CAT) = 0 | yes |

## Method 6: `public static Deck createStandardDeck(Random random)`

### Step 1–3 Results

| Step | Input | Output |
|------|-------|--------|
| Step 1 | Random generator | 56-card Original Edition deck |
| Step 2 | Pointer | Collection (duplicates), Count per CardType |
| Step 3 | pointer to a true object (`mockRandom`) | size 56; official count of each type |

### Step 4:
each-choice

| Test Case # | System under test | Expected output | Implemented? |
|-------------|-------------------|-----------------|--------------|
| TC1 | `createStandardDeck(mockRandom)` | size = 56; `mockRandom` not called | yes |
| TC2 | standard deck, `count(EXPLODING_KITTEN)` | 4 | yes |
| TC3 | standard deck, `count(DEFUSE)` | 6 | yes |
| TC4 | standard deck, `count(ATTACK)` | 4 | yes |
| TC5 | standard deck, `count(SKIP)` | 4 | yes |
| TC6 | standard deck, `count(FAVOR)` | 4 | yes |
| TC7 | standard deck, `count(SHUFFLE)` | 4 | yes |
| TC8 | standard deck, `count(SEE_THE_FUTURE)` | 5 | yes |
| TC9 | standard deck, `count(NOPE)` | 5 | yes |
| TC10 | standard deck, `count(TACOCAT)` | 4 | yes |
| TC11 | standard deck, `count(CATTERMELON)` | 4 | yes |
| TC12 | standard deck, `count(HAIRY_POTATO_CAT)` | 4 | yes |
| TC13 | standard deck, `count(BEARD_CAT)` | 4 | yes |
| TC14 | standard deck, `count(RAINBOW_RALPHING_CAT)` | 4 | yes |

## Method 7: `public void shuffle()`

### Step 1–3 Results

| Step | Input | Output |
|------|-------|--------|
| Step 1 | Deck state, value returned by `nextInt` | Same cards, new order |
| Step 2 | Collection (sizes, contents), Interval [0, i] | Collection |
| Step 3 | deck sizes: 0, 1, 3, 56 (duplicates); `nextInt` returns 0 or i | same cards; order given by the swaps |

### Step 4:
each-choice

| Test Case # | System under test | Expected output | Implemented? |
|-------------|-------------------|-----------------|--------------|
| TC1 | empty deck | size = 0 | yes |
| TC2 | Deck [NOPE] | draw = NOPE | yes |
| TC3 | Deck [ATTACK, SKIP, FAVOR], stub `nextInt(3)` → 2, `nextInt(2)` → 1 | draws = ATTACK, SKIP, FAVOR | yes |
| TC4 | Deck [ATTACK, SKIP, FAVOR], stub `nextInt(anyInt())` → 0 | draws = SKIP, FAVOR, ATTACK | yes |
| TC5 | standard deck, stub `nextInt(anyInt())` → 0 | size = 56; count of every type unchanged | yes |
