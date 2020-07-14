plugins {
    application
    java
    idea
    kotlin("jvm") version "1.3.72"
}

group = "net.inceptioncloud"
version = "1.0.0.6"

val outputName = "${project.name}-fat-${project.version}.jar"

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
        "-XX:+UnlockCommercialFeatures",
        "-Dlog4j.configurationFile=assets\\log_configs\\client-1.7.xml"
    )
}

sourceSets {
    main {
        java {
            srcDirs("src/main/resources")
        }
        resources {
            srcDirs("resources/")
        }
    }
}

// custom tasks
tasks {
    register<net.inceptioncloud.build.update.VersionTask>("version")
    register<net.inceptioncloud.build.update.PublishTask>("publish") {
        dependsOn("fatJar")
    }

    register<Jar>("fatJar") {
        baseName = "${project.name}-fat"
        manifest {
            attributes["Main-Class"] = "net.minecraft.client.main.Main"
        }
        from(configurations.runtimeClasspath.get()
            .filter { !it.absolutePath.contains("libraries-minecraft") || it.absolutePath.contains("netty-all") }
            .map { if (it.isDirectory) it else zipTree(it) }
        )
        with(jar.get() as CopySpec)
    }

    register("copy") {
        dependsOn("copyJar", "copyJson")
    }

    register<Copy>("copyJar") {
        dependsOn("fatJar")

        from("build/libs/")
        include(outputName)

        destinationDir = file("C:\\Users\\user\\AppData\\Roaming\\.minecraft\\versions\\Dragonfly-1.8.8")
        rename(outputName, "Dragonfly-1.8.8.jar")
    }

    register<Copy>("copyJson") {
        from("resources/")
        include("Dragonfly-1.8.8.json")
        into("C:\\Users\\user\\AppData\\Roaming\\.minecraft\\versions\\Dragonfly-1.8.8\\")
    }
}

// tasks configurations
tasks {
    build.configure {
        dependsOn("fatJar")
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