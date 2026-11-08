# Project Standards

These are the standards every contribution to this repository commits to.
Every pull request is reviewed against this document by at least one other group member
before it can be merged.

The goal is the **A-tier** of the project grading rubric.

Rules apply to all code except where the rubric explicitly excludes a case
(View classes, the main class, and enums are excluded from TDD and coverage
requirements).

---

## 1. Design doc as spec

The design documents in `docs/design/` are the team's binding contract for what
the domain classes look like.

- Every class the team implements has a section in a design doc.
- Each class section lists its **public** data members and its  **public** methods as Java signatures
    - eg. `` - `Deck(List<CardType> cards, Random random)` — constructor ``.
- Any changes to the class design (renaming a method, adding a public method, etc.) must be immediately updated in the design doc and every other places of reference to ensure that the team is working with the most recent version.

## 2. BVA discipline
BVA files live in `docs/bva/`, one file per class (e.g. `docs/bva/Deck.md`), with a section per public method.

### What must be recorded

- Step 4 results for every public method in scope (see below). Steps 1–3 are optional, include them when they help reviewers follow the analysis.
- Each test case records, in any easy-to-read format:
  1. A unique ID (e.g. `TC1`)
  2. The method under test
  3. The state of the system under test (inputs and object state)
  4. The expected output (return value, exception, or resulting state)
  5. Whether it has been implemented
- The "implemented" marker flips to yes in the same commit that adds the passing test for that case.
- It should be clear which BVA case each test case is related to, whether through noting or matching names.

### Scope: what gets BVA and tests

- **Input validation of user inputs is done in the GUI.** Domain classes assume the input they receive has already been validated by the UI, so the domain does not BVA or test invalid user input.
- **No null checks.** Following *Clean Code* (Ch. 7: don't pass null, don't return null), domain code does not check for `null` arguments.
- **Simple constructors get no BVA and no tests.** A simple constructor only assigns parameters to instance variables (e.g. `this.name = name;`) and does nothing else.
- **Constructors that do more than initialization** choose one of:
  1. **Check it in the constructor:** the constructor then gets BVA and tests
     for those checks.
  2. **Don't check it in the constructor:** the constructor stays simple, and the property of the value (e.g. the name string) is covered by the BVA and tests of the class's other methods that depend on it.

## 3. TDD ordering in commits

The C-tier rubric requires "evidence of a TDD/BDD workflow ... in the GitHub
commit history." The team enforces this as a per-method commit pattern:

For every newly-introduced public method in a PR, the PR's commit history must
show, **in topological order**:

1. A commit that adds the method's BVA (in `docs/bva/<ClassName>.md`).
2. A commit that adds the failing test in `src/test/java/`.
3. A commit that adds the implementation in `src/main/java/`.

It is fine for one commit doing two of these steps at once does not violate
ordering. When white-box BVA introduces new cases, they must be added to the BVA file and committed immediately.

## 4. Clean Code

The A-tier rubric demands "code fully satisfying Clean Code standards." These
rules apply to both production and test code. Checkstyle and SpotBugs must pass
on every PR.

### Naming Conventions

- Constants (`static final` fields and enum values) use `ALL_CAPS` with underscores, e.g. `MAX_PLAYERS`, `CardType.EXPLODING_KITTEN`.
- Everything else uses camel case: `UpperCamelCase` for classes, interfaces, and enums (`Deck`, `CardType`), `lowerCamelCase` for methods, variables, and parameters (`drawCard`, `handSize`).
- Names should be descriptive. No abbreviations or single-letter names except domain-standard ones (`id`, `i` for a loop counter in a tight scope).
- Classes are nouns (`Deck`), methods are verbs (`drawCard`), booleans read as yes/no questions (`isAlive`, `hasDefuse`).
- No Hungarian notation, `m_` prefixes, or `I` prefixes on interfaces.

### Functions

- Small functions: target ≤ 20 lines. Each function does one thing at one level of abstraction. Extract helper steps into well-named private methods if necessary.
- Function parameters: at most 3 parameters. No flag parameters (a `boolean` that switches between two behaviors).
- Errors: Report errors with exceptions and meaningful error messages, not error codes.
- No unused methods, fields, or classes. Every method exists because a test drove it.

### Classes and objects

- Each class has a single responsibility.
- Collaborators and sources of randomness (e.g. `Random`) are injected through
  the constructor, not created with `new` inside domain classes.
- View classes contain no game logic, since they
  are excluded from TDD and coverage.
- Fields are `private`. Don't expose mutable internals: return unmodifiable views
  or copies of collections.

### Magic numbers

- Any numeric literal other than `0`, `1`, or `-1` is a `private static final` constant with a meaningful name, e.g. `private static final int MAX_PLAYERS = 4;`.

### Comments

- Comment only when the *why* is not obvious from the code. Don't restate what the code does.
- No commented-out code. If it's not in use, delete it.
- No journal, attribution, or task/PR references in comments ("added for #42"). That history belongs in commit messages and PRs.

### Formatting

- Indentation is 4 spaces (IntelliJ's default).
- Line length ≤ 100 characters.
- No wildcard imports or unused imports.
- Member order: constants, fields, constructors, public methods, private helpers. Callers appear above the methods they call (stepdown rule).
- Declare variables close to where they are used; separate distinct concepts with a blank line.

### Error handling

- Use unchecked exceptions with specific types (`IllegalStateException` for an action that is invalid in the current game state).
- Never return `null`, return an empty collection or throw instead.
- Exception messages are `private static final String` constants with descriptive names, e.g. `private static final String EMPTY_DECK_MESSAGE = "Cannot draw from an empty deck.";`.

## 5. i18n requirement (A-tier)

The rubric: "code supports adding new locales without changing existing code."

- All user-facing strings (game prompts, card names, error text shown
  to the player) are looked up from `src/main/resources/message_<locale>.properties`.
  Hard-coding any user-facing string in Java is a violation.
- Bundles are loaded via `ResourceBundle.getBundle("message", locale)`,
  so adding a new locale is purely a `.properties` file addition, with no Java edits.
- At least 2 locales ship by the time the team submits.
- Domain code (`Card`, `Deck`, `Player`, `Game`) does not store
  human-readable text. That is a UI concern, looked up by a key such as `CardType`.

## 6. Testing conventions

- JUnit 5 (`@Test`, `@ParameterizedTest`, `@EnumSource`, `@ValueSource`).
- One test class per source class, mirroring the package and named
  `<Class>Test` (e.g. `domain.Card` ↔ `domain.CardTest`).
- Exception cases use `assertThrows(SomeException.class, () -> { ... })`
  and also assert on the exception's message.
- Test method names use one of two formats:
  - a descriptive English clause, e.g. `addNonDefuseToPlayerWhenShouldBeDefuse`
  - `methodName_stateUnderTest_expectedBehavior`, as taught in class
- Tests do not share mutable state (no `static` test fixtures).

---

## 7. Reviewing a pull request

- Every PR is approved by at least one teammate before merging.
- `./gradlew build` (tests, Checkstyle, SpotBugs) passes in CI.
- The reviewer checks the PR against every section of this document. A 
  violation is fixed before merging.
- If the author disagrees with a review comment, discuss it in the PR thread
  rather than merging over it.