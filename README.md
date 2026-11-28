![Gradle Build](https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-7/actions/workflows/main.yml/badge.svg)

# Exploding Kittens

## Contributors
- Casey Zhang
- Fiona Gan
- Graciela Zhang
- Yiliu Li

## Dependencies
- JDK 11
- JUnit 5.10
- EasyMock 5.4
- Gradle 8.10
- Checkstyle 10.18.2 (Google style base, customized in config/checkstyle/checkstyle.xml)
- SpotBugs (Gradle plugin 6.0.25)
- JaCoCo (Gradle built-in plugin)
- PITest 1.15.0 (Gradle plugin 1.15.0, JUnit 5 plugin 1.2.1)
- JavaFX 17.0.10 (Gradle plugin 0.1.0, module javafx.controls)

## Exceptions
### Code Coverage
View classes (`ui/view/**`), the main class (`ui/Main`), and enums (`domain/CardType`) are excluded from JaCoCo coverage.
- **`GameSetupController.startGame`**: the `catch (IllegalStateException e)` block is not covered since `Game.setup()` only throws when the deck is insufficient or the game is already set up. The controller always uses a new standard deck and a new `Game`, so no test can reach `Game` throwing this. It stays so that an unexpected setup failure shows the user a message instead of failing silently.

### Mutation Testing
PITest mutates only `domain.*` and `ui.controller.*`; view classes and the main class are excluded.
- **`GameSetupController.startGame`**: the mutant that removes the `showError` call in the `catch` block has no coverage, for the same reason as the code coverage exception for the same block of code.

### Others
- **TDD commit history** before the team's TDD commit rule changed on 11/19, failing tests were committed separately from the code that makes them pass. These 11 commits fail their tests; each is followed by a commit that makes them pass: `77058c3`, `d4f1e6e`, `93a6013`, `e1f40b4`, `51a0025`, `afc36db`, `e4b01c9`, `fdc22d8`, `ef75105`, `65d2121`, `eceb645`. Met with Dr.Yiji on 11/19, all mistakes forgiven before 4 PM.
