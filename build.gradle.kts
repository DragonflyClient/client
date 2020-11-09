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
    kotlin("jvm") version "1.4.0"
    id("org.openjfx.javafxplugin") version "0.0.9"
}

group = "net.inceptioncloud"
version = "1.1.4.5"

val outputName = "${project.name}-${project.version}-full.jar"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13")

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.0")
    implementation("com.squareup.okhttp3:okhttp:4.3.1")
    implementation("com.esotericsoftware:kryonet:2.22.0-RC1")
    implementation("org.javassist:javassist:3.15.0-GA")

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
            exclude("resources/Dragonfly-1.8.8.json")
        }
    }
}

// custom tasks
tasks {
    register<net.inceptioncloud.build.update.VersionTask>("version")
    register<net.inceptioncloud.build.update.PublishTask>("publish") {
        dependsOn("fullJar", "obfuscateJar")
    }

    register<proguard.gradle.ProGuardTask>("obfuscateJar") {
        verbose()
        dontwarn()
        dontoptimize()
        dontshrink()

        injars("build/libs/$outputName")
        outjars("build/libs/${project.name}-${project.version}-obfuscated.jar")

        libraryjars("${System.getProperty("java.home")}/lib/rt.jar")

        obfuscationdictionary("obfuscation/dictionary.txt")
        classobfuscationdictionary("obfuscation/dictionary.txt")
        packageobfuscationdictionary("obfuscation/dictionary.txt")

        overloadaggressively()
        flattenpackagehierarchy()
        repackageclasses()

        File("libraries-minecraft").listFiles()!!.forEach { libraryjars(it.absolutePath) }
        File("libraries").listFiles()!!.forEach { libraryjars(it.absolutePath) }

        printmapping("obfuscation/${project.name}-${project.version}.map")
        renamesourcefileattribute("~")
        keepattributes("SourceFile,LineNumberTable")

        dontnote("kotlin.internal.PlatformImplementationsKt")
        dontnote("kotlin.reflect.jvm.internal.**")

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

        keep("@net.inceptioncloud.dragonfly.utils.Keep class * { *; }")
        keep("class net.inceptioncloud.dragonfly.utils.Keep")
        keep("class net.inceptioncloud.dragonfly.ui.taskbar.** { *; }")
        keep("class net.inceptioncloud.dragonfly.engine.internal.** { *; }")
        keep("class net.inceptioncloud.dragonfly.engine.structure.** { *; }")
        keep("class * extends net.inceptioncloud.dragonfly.engine.internal.Widget { *; }")
        keep("class * extends net.inceptioncloud.dragonfly.engine.internal.AssembledWidget { *; }")
        keep("class * extends net.inceptioncloud.dragonfly.mods.core.DragonflyMod { *; }")
        keep("class net.inceptioncloud.dragonfly.mods.core.DragonflyMod { *; }")
        keep("class net.dragonfly.kernel.packets.Packet { *; }")
        keep("class * implements net.dragonfly.kernel.packets.Packet { *; }")

        keep("class net.minecraft.entity.** { *; }")
        keep("class net.minecraft.village.** { *; }")
        keep("class net.minecraft.block.** { *; }")
        keep("class net.minecraft.realms.** { *; }")
        keep("class net.minecraft.client.ClientBrandRetriever { *; }")
        keep("class net.minecraft.client.gui.Gui { *; }")
        keep("class net.minecraft.client.gui.GuiScreen { *; }")
        keep("class net.minecraft.client.gui.GuiYesNoCallback { *; }")
        keep("class net.minecraft.server.MinecraftServer { *; }")

        keepattributes("*Annotation*")

        keepclasseswithmembers("""public class * {
        public static void main(java.lang.String[]);
        }""")

        keepclasseswithmembernames("""class * {
            native <methods>;
        }""")

        keepclassmembers(mapOf(
            "allowoptimization" to true
        ), """enum * {
        public static **[] values();
        public static ** valueOf(java.lang.String);
        }""")

        keepclassmembers("""class * implements java.io.Serializable {
        static final long serialVersionUID;
        static final java.io.ObjectStreamField[] serialPersistentFields;
        private void writeObject(java.io.ObjectOutputStream);
        private void readObject(java.io.ObjectInputStream);
        java.lang.Object writeReplace();
        java.lang.Object readResolve();
        }""")
    }

    register<Jar>("fullJar") {
        archiveClassifier.set("full")

        manifest {
            attributes["Main-Class"] = "net.minecraft.client.main.Main"
        }

        from(configurations.runtimeClasspath.get()
            .filter { !it.absolutePath.contains("libraries-minecraft") || it.absolutePath.contains("netty-all") }
            .filter { "tornadofx" !in it.absolutePath } // exclude tornadofx
            .filter { "reflections" !in it.absolutePath } // exclude org.reflections but not kotlin-reflect
            .map { if (it.isDirectory) it else zipTree(it) }
        ) {
            exclude { it.name == "module-info.class" || it.name == "icon_inspector.png" }
        }

        with(jar.get() as CopySpec)
    }

    register("copy") {
        dependsOn("copyJar", "copyJson")
    }

    register<Copy>("copyJar") {
        dependsOn("fullJar")

        from("build/libs/")
        include(outputName)

        destinationDir = file("${System.getenv("APPDATA")}\\.minecraft\\versions\\Dragonfly-1.8.8")
        rename(outputName, "Dragonfly-1.8.8.jar")
    }

    register<Copy>("copyObfuscated") {
        dependsOn("fullJar", "obfuscateJar")

        from("build/libs")
        include("${project.name}-${project.version}-obfuscated.jar")

        destinationDir = file("${System.getenv("APPDATA")}\\.minecraft\\versions\\Dragonfly-1.8.8")
        rename("${project.name}-${project.version}-obfuscated.jar", "Dragonfly-1.8.8.jar")
    }

    register<Copy>("copyJson") {
        from("resources/")
        include("Dragonfly-1.8.8.json")
        into("${System.getenv("APPDATA")}\\.minecraft\\versions\\Dragonfly-1.8.8\\")
    }
}

// tasks configurations
tasks {
    build.configure {
        dependsOn("fullJar")
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

javafx {
    version = "11.0.2"
    modules = arrayListOf("javafx.controls")
}