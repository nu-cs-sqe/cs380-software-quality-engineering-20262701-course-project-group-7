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
- Gradle 8.10
- Checkstyle 10.18.2 (Google style base, customized in config/checkstyle/checkstyle.xml)
- SpotBugs (Gradle plugin 6.0.25)
- JaCoCo (Gradle built-in plugin)
- PITest 1.15.0 (Gradle plugin 1.15.0, JUnit 5 plugin 1.2.1)
- Easymock 5.4

## Exceptions
### Code Coverage
View classes (`ui/view/**`), the main class (`ui/Main`), and enums (`domain/CardType`) are excluded from JaCoCo coverage.
### Mutation Testing
PITest mutates only `domain.*` and `ui.controller.*`; view classes and the main class are excluded.
### Others
N/A
