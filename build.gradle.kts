plugins {
    kotlin("jvm") version "1.7.10"
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("commons-io:commons-io:2.1")
    implementation("org.json:json:20090211")
    implementation("com.google.code.gson:gson:2.0")
    implementation("org.apache.httpcomponents:httpcore:4.1.2")
    implementation("org.apache.httpcomponents:httpclient:4.1.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    testImplementation("junit:junit:4.12")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation("org.assertj:assertj-core:3.9.1")
}