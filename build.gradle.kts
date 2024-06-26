plugins {
    application
    id("java")
}

group = "com.skyteeee.tungeon"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.json:json:20231013")
    implementation("org.telegram:telegrambots:6.9.7.1")
    implementation("org.telegram:telegrambotsextensions:6.9.7.1")
    implementation("com.vdurmont:emoji-java:5.1.1")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass = "com.skyteeee.tungeon.Main"
}
