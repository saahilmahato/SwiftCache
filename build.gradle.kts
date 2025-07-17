plugins {
    java
    checkstyle
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