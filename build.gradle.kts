import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort

plugins {
    id("java")
    checkstyle
    jacoco
    id("com.github.spotbugs") version "6.0.25"
    id("info.solidsoft.pitest") version "1.19.0"
}

group = "nu.csse.sqe"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.easymock:easymock:5.4.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

tasks.compileJava {
    options.release = 11
}

tasks.test {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.12"
}

val gameTest by tasks.registering(Test::class) {
    description = "Runs only GameTest for Game-specific quality measurements."
    group = "verification"
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    useJUnitPlatform()
    filter {
        includeTestsMatching("domain.GameTest")
    }
}

val gameClasses = sourceSets.main.get().output.asFileTree.matching {
    include("domain/Game.class")
}

tasks.register<JacocoReport>("gameCoverage") {
    description = "Reports Game coverage from GameTest only."
    group = "verification"
    dependsOn(gameTest)
    executionData(gameTest.get())
    classDirectories.setFrom(gameClasses)
    sourceDirectories.setFrom(sourceSets.main.get().allSource.srcDirs)
    reports {
        xml.required = true
        html.required = true
    }
}

tasks.register<JacocoCoverageVerification>("gameCoverageVerification") {
    description = "Requires full cyclomatic complexity coverage for Game."
    group = "verification"
    dependsOn(gameTest)
    executionData(gameTest.get())
    classDirectories.setFrom(gameClasses)
    violationRules {
        rule {
            element = "CLASS"
            limit {
                counter = "COMPLEXITY"
                value = "COVEREDRATIO"
                minimum = "1.0".toBigDecimal()
            }
        }
    }
}

pitest {
    pitestVersion.set("1.19.0")
    junit5PluginVersion.set("1.2.2")
    targetClasses.set(setOf("domain.Game"))
    targetTests.set(setOf("domain.GameTest"))
    mutators.set(setOf("DEFAULTS"))
    mutationThreshold.set(100)
    threads.set(2)
    outputFormats.set(setOf("XML", "HTML"))
    timestampedReports.set(false)
}

checkstyle {
    toolVersion = "10.18.2"
    isIgnoreFailures = false
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required = false
        html.required = true
        html.stylesheet = resources.text.fromFile("config/xsl/checkstyle-noframes-severity-sorted.xsl")
    }
}

tasks.register<Checkstyle>("gameCheckstyle") {
    description = "Checks Game and GameTest using the unchanged team Checkstyle rules."
    group = "verification"
    source("src/main/java/domain/Game.java", "src/test/java/domain/GameTest.java")
    classpath = sourceSets.test.get().compileClasspath
}

tasks.check {
    dependsOn("gameCheckstyle", "gameCoverageVerification", "pitest")
}

spotbugs {
    ignoreFailures = false
    showStackTraces = true
    showProgress = true
    effort = Effort.DEFAULT
    reportLevel = Confidence.DEFAULT
    maxHeapSize = "1g"
    excludeFilter.set(file("config/spotbugs/excludeFilter.xml"))
}

tasks.spotbugsMain {
    reports.create("html") {
        required = true
        outputLocation = layout.buildDirectory.file("reports/spotbugs/spotbugs-main.html")
        setStylesheet("fancy-hist.xsl")
    }
}

tasks.spotbugsTest {
    reports.create("html") {
        required = true
        outputLocation = layout.buildDirectory.file("reports/spotbugs/spotbugs-test.html")
        setStylesheet("fancy-hist.xsl")
    }
}
