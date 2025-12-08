plugins {
    java
    checkstyle
    id("com.diffplug.spotless") version "8.0.0"
    id("com.github.spotbugs") version "6.4.2"
}

group = "org.saahil"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

checkstyle {
    toolVersion = "10.26.1"
    config = resources.text.fromUri("https://raw.githubusercontent.com/checkstyle/checkstyle/master/src/main/resources/google_checks.xml")
    isIgnoreFailures = false
}

spotless {
    java {
        googleJavaFormat()
    }
}

spotbugs {
    effort = com.github.spotbugs.snom.Effort.MAX
    reportLevel = com.github.spotbugs.snom.Confidence.LOW
    ignoreFailures = false
    showProgress = true
}