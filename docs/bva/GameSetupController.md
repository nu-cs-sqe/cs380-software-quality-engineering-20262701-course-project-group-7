# GameSetupController BVA Analysis

## Scope

The design is in `docs/design/game-setup-phase.md` (Section 6.3).
`GameSetupController` has two purposes:

1. **Validate user input:** `hasBlankName` and `hasDuplicateName` check the
   player names. This is the only place in the setup UI where user input is
   validated. See below for assumptions.
2. **Connect the view to the model:** the constructor and the handlers call the
   right view and `Game` methods in the right order. They will be tested with mocks to ensure that they call correct methods.

Assumptions for the input checks:

- The player count is chosen with buttons that only offer 2 to 5, so every
  list of names has 2 to 5 entries. Other sizes and `null` lists or names are
  not tested.
- Names are not trimmed, and comparisons are case-sensitive.
- The order "blank names are reported before duplicates" is decided in
  `handleStart()`, so it will be tested there.

## Method under test: `static boolean hasBlankName(List<String> names)`

### Step 4: Test cases (each-choice)

| ID | Input (names) | Expected output | Implemented? |
|----|---------------|-----------------|--------------|
| BLANK-01 | `["Ana", "Ben"]` (minimum size) | `false` | :white_check_mark: |
| BLANK-02 | `["Ana", "Ben", "Cy", "Dee", "Eve"]` (maximum size) | `false` | :white_check_mark: |
| BLANK-03 | `["A", "B"]` (one-character names) | `false` | :white_check_mark: |
| BLANK-04 | `[" Ana", "Ben"]` (leading space) | `false`; a name with other characters is not blank | :white_check_mark: |
| BLANK-05 | `["", "Ben"]` (empty name first) | `true` | :white_check_mark: |
| BLANK-06 | `[" ", "Ben"]` (single space) | `true` | :white_check_mark: |
| BLANK-07 | `["\t ", "Ben"]` (mixed whitespace) | `true` | :white_check_mark: |
| BLANK-08 | `["Ana", "Ben", "Cy", "Dee", ""]` (blank name last) | `true` | :white_check_mark: |

## Method under test: `static boolean hasDuplicateName(List<String> names)`

### Step 4: Test cases (each-choice)

| ID | Input (names) | Expected output | Implemented? |
|----|---------------|-----------------|--------------|
| DUP-01 | `["Ana", "Ben"]` (minimum size, all different) | `false` | :white_check_mark: |
| DUP-02 | `["Ana", "Ben", "Cy", "Dee", "Eve"]` (maximum size, all different) | `false` | :white_check_mark: |
| DUP-03 | `["A", "a"]` (differ only by case) | `false`; names are case-sensitive | :white_check_mark: |
| DUP-04 | `["Ana", " Ana"]` (differ only by leading space) | `false`; names are not trimmed | :white_check_mark: |
| DUP-05 | `["Ana", "Ana"]` (adjacent duplicate) | `true` | :white_check_mark: |
| DUP-06 | `["Ana", "Ben", "Cy", "Dee", "Ana"]` (first and last equal) | `true` | :white_check_mark: |

## Handlers

These cases use a mocked `GameSetupView` (EasyMock to check it's calling expected methods, strict where the order
matters). The private handlers are reached by capturing the handler the
constructor registers on the view and running it. "English text" and
"Chinese text" mean the value of the key in `message.properties` and
`message_zh.properties`.

### Method under test: `GameSetupController(GameSetupView view, Random random)`

| ID | State of the system | Expected output | Implemented? |
|----|---------------------|-----------------|--------------|
| CTRL-01 | New controller with a mocked view | In order: `setOnLocaleChanged`, `setOnPlayerCountSelected`, `setOnStart`, then `updateText` with the English bundle (`setup.title` is "Exploding Kittens") | :white_check_mark: |

### Method under test: `handlePlayerCountSelected(int count)` (via `setOnPlayerCountSelected`)

| ID | State of the system | Expected output | Implemented? |
|----|---------------------|-----------------|--------------|
| COUNT-01 | Count 2 (minimum button) | In order: `clearError()`, `showNameFields(2)` | :white_check_mark: |
| COUNT-02 | Count 5 (maximum button) | In order: `clearError()`, `showNameFields(5)` | :x: |

### Method under test: `handleLocaleChanged(Locale locale)` (via `setOnLocaleChanged`)

| ID | State of the system | Expected output | Implemented? |
|----|---------------------|-----------------|--------------|
| LANG-01 | English is current; Chinese selected | `updateText` with the Chinese bundle (`setup.title` is "爆炸猫") | :x: |
| LANG-02 | Chinese is current; English selected again | `updateText` with the English bundle | :x: |

### Method under test: `handleStart()` (via `setOnStart`)

| ID | State of the system | Expected output | Implemented? |
|----|---------------------|-----------------|--------------|
| START-01 | Entered names `["", "Ben"]` | In order: `getEnteredNames()`, `clearError()`, `showError(English text of setup.name.blank)`; `showPlayers` not called | :x: |
| START-02 | Entered names `["Ana", "Ana"]` | In order: `getEnteredNames()`, `clearError()`, `showError(English text of setup.name.duplicate)`; `showPlayers` not called | :x: |
| START-03 | Entered names `["", ""]` (blank and duplicate) | `showError(English text of setup.name.blank)`; blank is reported first | :x: |
| START-04 | Entered names `["Ana", "Ana", ""]` (white-box: the duplicate appears before the blank name) | `showError(English text of setup.name.blank)`; all names are checked for blanks before duplicates | :x: |
| START-05 | Chinese selected, then entered names `["", "Ben"]` | `showError(Chinese text of setup.name.blank)` | :x: |
| START-06 | Entered names `["Ana", "Ben", "Cy"]` (valid) | In order: `getEnteredNames()`, `clearError()`, `showPlayers(["Ana", "Ben", "Cy"], "Ana")`; `showError` not called | :x: |

`handleStart()` step 5 (`Game.setup()` throws `IllegalStateException`) cannot
be reached with a standard deck and a new `Game` on every Start, but should exist to notify the user instead of failing silently.
So it has no test case. This causes coverage issues and is listed under README's Exceptions.
