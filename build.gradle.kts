buildscript {
    repositories {
        mavenLocal()
        google()
        jcenter()
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.0.0")
    }
}

plugins {
    application
    java
    idea
    kotlin("jvm") version "1.3.72"
}

group = "net.inceptioncloud"
version = "1.1.3.0"

val outputName = "${project.name}-fat-${project.version}.jar"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13")

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.72")

    // only required for inspector extension
    implementation("no.tornado:tornadofx:1.7.20")

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
            exclude("net/inceptioncloud/dragonfly/engine/inspector/extension/**")
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

    register<proguard.gradle.ProGuardTask>("proguard") {
        val outputName = "${project.name}-fat-${project.version}.jar"

        verbose()
        dontwarn()
        dontoptimize()
        dontshrink()

        injars("build/libs/$outputName")
        outjars("C:/Users/user/AppData/Roaming/.minecraft/versions/Dragonfly-1.8.8/Dragonfly-1.8.8.jar")

        libraryjars("${System.getProperty("java.home")}/lib/rt.jar")

        obfuscationdictionary("dictionary.txt")
        classobfuscationdictionary("dictionary.txt")
        packageobfuscationdictionary("dictionary.txt")
        overloadaggressively()
        flattenpackagehierarchy()
        repackageclasses()

        // add libraries from '/libraries' and '/libraries-minecraft'
        File("libraries-minecraft").listFiles()!!.forEach { libraryjars(it.absolutePath) }
        File("libraries").listFiles()!!.forEach { libraryjars(it.absolutePath) }

        // Save the obfuscation mapping to a file, so you can de-obfuscate any stack
        // traces later on. Keep a fixed source file attribute and all line number
        // tables to get line numbers in the stack traces.
        // You can comment this out if you")re not interested in stack traces.

        printmapping("out.map")
        renamesourcefileattribute("SourceFile")
        keepattributes("SourceFile,LineNumberTable")

        keep("class com.** { *; }")
        keep("class darwin.** { *; }")
        keep("class io.** { *; }")
        keep("class javax.** { *; }")
        keep("class khttp.** { *; }")
        keep("class kotlin.** { *; }")
        keep("class kotlinx.** { *; }")
        keep("class linux.** { *; }")
        keep("class net.arikia.** { *; }")
        keep("class net.minecraftforge.** { *; }")
        keep("class optifine.** { *; }")
        keep("class org.** { *; }")
        keep("class oshi.** { *; }")

        keep("@net.inceptioncloud.dragonfly.utils.Keep public class * { *; }")
        keep("class net.inceptioncloud.dragonfly.utils.Keep")
        keep("class net.inceptioncloud.dragonfly.engine.internal.** { *; }")
        keep("class net.inceptioncloud.dragonfly.engine.structure.** { *; }")
        keep("class net.inceptioncloud.dragonfly.ui.taskbar.** { *; }")

        keep("class net.minecraft.entity.** { *; }")
        keep("class net.minecraft.village.** { *; }")
        keep("class net.minecraft.block.** { *; }")

        // Preserve all annotations.

        keepattributes("*Annotation*")

        // You can print out the seeds that are matching the keep options below.

        //printseeds("out.seeds")

        // Preserve all public applications.

        keepclasseswithmembers("""public class * {
        public static void main(java.lang.String[]);
        }""")

        // Preserve all native method names and the names of their classes.

        keepclasseswithmembernames("""class * {
            native <methods>;
        }""")

        // Preserve the special static methods that are required in all enumeration
        // classes.

        keepclassmembers(mapOf(
            "allowoptimization" to true
        ), """enum * {
        public static **[] values();
        public static ** valueOf(java.lang.String);
        }""")

        // Explicitly preserve all serialization members. The Serializable interface
        // is only a marker interface, so it wouldn")t save them.
        // You can comment this out if your application doesn")t use serialization.
        // If your code contains serializable classes that have to be backward
        // compatible, please refer to the manual.

        keepclassmembers("""class * implements java.io.Serializable {
        static final long serialVersionUID;
        static final java.io.ObjectStreamField[] serialPersistentFields;
        private void writeObject(java.io.ObjectOutputStream);
        private void readObject(java.io.ObjectInputStream);
        java.lang.Object writeReplace();
        java.lang.Object readResolve();
        }""")

        // Your application may contain more items that need to be preserved;
        // typically classes that are dynamically created using Class.forName:

        // keep("public class com.example.MyClass")
        // keep("public interface com.example.MyInterface")
        // keep("public class * implements com.example.MyInterface")
    }

    register<Jar>("fatJar") {
        baseName = "${project.name}-fat"
        manifest {
            attributes["Main-Class"] = "net.minecraft.client.main.Main"
        }
        from(configurations.runtimeClasspath.get()
            .filter { !it.absolutePath.contains("libraries-minecraft") || it.absolutePath.contains("netty-all") }
            .filter { "tornadofx" !in it.absolutePath }
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
