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

## Exceptions
### Code Coverage
View classes (`ui/view/**`), the main class (`ui/Main`), and enums (`domain/CardType`) are excluded from JaCoCo coverage.
### Mutation Testing
PITest mutates only `domain.*` and `ui.controller.*`; view classes and the main class are excluded.
After merging reviewed main `26165d2` and refactoring Game, all 37 Game mutants
and all 56 project mutants are normally KILLED. No surviving, uncovered,
timed-out or error outcomes occur in this run. Earlier Deck `MEMORY_ERROR`
outcomes are historical, not current failures or equivalent-mutant exemptions.
### Others
Game TDD history limitation: BVA preceded the API migration's compile-time
red run. The unpublished migration was subsequently split with author
permission into 18 passing test-method commits, with implementation in the
first commit. That split does not establish separate per-test red-green
evidence. Earlier failing-test commits remain preserved; no Game instructor
waiver is claimed. The review fixes are behavior-preserving refactoring,
not a new TDD implementation.

Latest local full build passes all 142 tests, including 24 Game invocations,
and both main/test Checkstyle and SpotBugs tasks. Game, Deck and Player have
zero missed JaCoCo complexity. Team quality settings are unchanged.
The former Player blockers were resolved by the reviewed main implementation,
not by changes to teammate code in Game's review fixes. CI and teammate approval
are still required before merging.

Game exceptions contain meaningful English internal diagnostics, not locale
keys. Internal messages do not require i18n, per the author's clarification
of the team guidance. No exception-message bundles or lookup are required.
UI integration and localization of player-facing prompts and recovery text
are separate UI work, not a Game exception-message blocker.

Deck contributor's record from main (not a waiver claimed for Game):
- **TDD commit history** before the team's TDD commit rule changed on 11/19, failing tests were committed separately from the code that makes them pass. These 11 commits fail their tests; each is followed by a commit that makes them pass: `77058c3`, `d4f1e6e`, `93a6013`, `e1f40b4`, `51a0025`, `afc36db`, `e4b01c9`, `fdc22d8`, `ef75105`, `65d2121`, `eceb645`. Met with Dr.Yiji on 11/19, all mistakes forgiven before 4 PM.
