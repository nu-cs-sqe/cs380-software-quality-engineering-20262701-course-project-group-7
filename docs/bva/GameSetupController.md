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
| BLANK-07 | `["\t ", "Ben"]` (mixed whitespace) | `true` | :x: |
| BLANK-08 | `["Ana", "Ben", "Cy", "Dee", ""]` (blank name last) | `true` | :x: |

## Method under test: `static boolean hasDuplicateName(List<String> names)`

### Step 4: Test cases (each-choice)

| ID | Input (names) | Expected output | Implemented? |
|----|---------------|-----------------|--------------|
| DUP-01 | `["Ana", "Ben"]` (minimum size, all different) | `false` | :x: |
| DUP-02 | `["Ana", "Ben", "Cy", "Dee", "Eve"]` (maximum size, all different) | `false` | :x: |
| DUP-03 | `["A", "a"]` (differ only by case) | `false`; names are case-sensitive | :x: |
| DUP-04 | `["Ana", " Ana"]` (differ only by leading space) | `false`; names are not trimmed | :x: |
| DUP-05 | `["Ana", "Ana"]` (adjacent duplicate) | `true` | :x: |
| DUP-06 | `["Ana", "Ben", "Cy", "Dee", "Ana"]` (first and last equal) | `true` | :x: |
