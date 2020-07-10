plugins {
    application
    java
    idea
    kotlin("jvm") version "1.3.72"
}

group = "net.inceptioncloud"
version = "1.0.3.0-alpha"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13")

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")

    implementation(fileTree("libraries"))
    implementation(fileTree("libraries-minecraft"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClassName = "net.minecraft.client.main.Main"
    applicationDefaultJvmArgs = listOf(
        "-Djava.library.path=../natives/",
        "-XX:+DisableExplicitGC",
        "-XX:+UseConcMarkSweepGC",
        "-XX:+UseParNewGC",
        "-XX:+UseNUMA",
        "-XX:+CMSParallelRemarkEnabled",
        "-XX:MaxGCPauseMillis=30",
        "-XX:GCPauseIntervalMillis=150",
        "-XX:+UseAdaptiveGCBoundary",
        "-XX:-UseGCOverheadLimit",
        "-XX:+UseBiasedLocking",
        "-XX:SurvivorRatio=8",
        "-XX:TargetSurvivorRatio=90",
        "-XX:MaxTenuringThreshold=15",
        "-Dfml.ignorePatchDiscrepancies=true",
        "-Dfml.ignoreInvalidMinecraftCertificates\u00AD=true",
        "-XX:+UseFastAccessorMethods",
        "-XX:+UseCompressedOops",
        "-XX:+OptimizeStringConcat",
        "-XX:+AggressiveOpts",
        "-XX:ReservedCodeCacheSize=2048M",
        "-XX:+UseCodeCacheFlushing",
        "-XX:SoftRefLRUPolicyMSPerMB=20000",
        "-XX:ParallelGCThreads=10",
        "-XX:+UnlockCommercialFeatures"
    )
}

val fatJar = task("fatJar", type = Jar::class) {
    baseName = "${project.name}-fat"
    manifest {
        attributes["Main-Class"] = "net.minecraft.client.main.Main"
    }
    from(configurations.runtimeClasspath.get()
        .filter { !it.absolutePath.contains("libraries-minecraft") }
        .map { if (it.isDirectory) it else zipTree(it) }
    )
    with(tasks.jar.get() as CopySpec)
}

sourceSets {
    main {
        java {
            srcDirs("src/main/resources")
        }
    }
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
    
    run.configure {
        main = "Start"
        workingDir("runtime/")
    }

    test {
        useJUnit()
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.ExperimentalStdlibApi",
            "-Xopt-in=kotlin.contracts.ExperimentalContracts",
            "-Xopt-in=kotlinx.coroutines.ObsoleteCoroutinesApi",
            "-Xuse-experimental=kotlin.Experimental"
        )
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    compileTestJava {
        options.encoding = "UTF-8"
    }
}