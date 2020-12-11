plugins {
    kotlin("jvm") version "1.3.72"
}

repositories {
    mavenCentral()
    maven("https://jcenter.bintray.com/")
    /*maven("https://repo.spring.io/libs-release/")*/
}

dependencies {
    implementation("com.jcraft:jsch:0.1.55")
    implementation("khttp:khttp:1.0.0")
}