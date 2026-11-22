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

## Exceptions
### Code Coverage
N/A
### Mutation Testing
Game: all 35 mutants killed; the latest full-project run killed all 63 mutants.
An earlier report contained one `MEMORY_ERROR` in `Deck.add`, not a normal
assertion-based kill or an equivalent-mutant exemption. It was not reproduced
in the latest run; its cause has not been investigated.
### Others
Game API migration: BVA was committed before the local compile-time red run;
the previously unpublished migration commit was split with author permission.
Each restored test method has its own passing commit with the corresponding
BVA marker update. Parameterized invocations remain one test method.
The implementation accompanies the first restored test, since existing setup
behavior was migrated, not reimplemented test by test. Splitting history does
not establish a new per-test red-green workflow. Earlier failing-test commits
remain in the preserved history. No instructor waiver is claimed.

Full build remains blocked by Checkstyle findings in Deck, Player and their
tests, plus SpotBugs findings in Deck's constructor and Player's string
comparison. These are unresolved issues, not approved exceptions.
Game and GameTest have no findings. See [Game BVA](docs/bva/Game.md)
for the execution record.

Game exceptions contain meaningful English internal diagnostics, not locale
keys. Internal messages do not require i18n, per the author's clarification
of the team guidance. No exception-message bundles or lookup are required.
UI integration is still unfinished; localization of player-facing prompts
and recovery text is separate UI work, not a Game exception-message blocker.
