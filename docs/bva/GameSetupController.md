# GameSetupController BVA Analysis

## BVA Scope

`GameSetupController` is the only class in the setup UI that validates user
input, all other UI classes are views and exempt from BVA.

- `GameSetupController(GameSetupView, Random)` only stores its arguments and
  registers view handlers with no logic, no BVA cases.
- `handleStart()` and `handleLocaleChanged(Locale)` are private handlers wired
  to the view, no BVA cases.
- The player count is chosen with buttons that only offer 2 to 5, so an
  invalid count cannot reach the controller. Every list passed to
  `validateNames` has 2 to 5 names. Lists outside that size, and `null` lists
  or names, are not tested (no null checks).

## Method under test: `void validateNames(List<String> names)`

### Steps 1-3

- **Input:** a list of 2 to 5 player names (Collection of Strings).
  - Size [INTERVAL] => [2,5], fixed by buttons (must be valid).
  - Each name [STRING] => empty `""`, whitespace only, one character, several characters, leading space (not trimmed).
  - Pairs of names => different, equal, equal ignoring case only.
  - Position of an invalid name => first, last.
- **Output:** [CASE]
  - no exception (valid)
  - `IllegalArgumentException` with message is the resource key `setup.name.blank` or `setup.name.duplicate`.
    Blank names are checked before duplicates.

### Step 4: Test cases (each-choice)

| ID | Input (names) | Expected output | Implemented? |
|----|---------------|-----------------|--------------|
| SETUP-01 | `["Ana", "Ben"]` | No exception | :white_check_mark: |
| SETUP-02 | `["Ana", "Ben", "Cy", "Dee", "Eve"]` | No exception | :white_check_mark: |
| SETUP-03 | `["A", "B"]` | No exception | :x: |
| SETUP-04 | `["A", "a"]` | No exception; names are case-sensitive | :x: |
| SETUP-05 | `[" Ana", "Ben"]` | No exception; names are not trimmed | :x: |
| SETUP-06 | `["", "Ben"]` | `IllegalArgumentException("setup.name.blank")` | :x: |
| SETUP-07 | `[" ", "Ben"]` | `IllegalArgumentException("setup.name.blank")` | :x: |
| SETUP-08 | `["\t ", "Ben"]` | `IllegalArgumentException("setup.name.blank")` | :x: |
| SETUP-09 | `["Ana", "Ben", "Cy", "Dee", ""]` | `IllegalArgumentException("setup.name.blank")` | :x: |
| SETUP-10 | `["Ana", "Ana"]` | `IllegalArgumentException("setup.name.duplicate")` | :x: |
| SETUP-11 | `["Ana", "Ben", "Cy", "Dee", "Ana"]` | `IllegalArgumentException("setup.name.duplicate")` | :x: |
| SETUP-12 | `["", ""]` | `IllegalArgumentException("setup.name.blank")`; blank is checked first | :x: |
