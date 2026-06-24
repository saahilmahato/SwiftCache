plugins {
    java
}

group = "org.saahil"
version = "0.1.0"

repositories {
    mavenCentral()
}

val h2Version = "2.3.232"

dependencies {
    implementation("com.h2database:h2:$h2Version")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}