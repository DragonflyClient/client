package net.minecraft.client.main

import com.google.gson.GsonBuilder
import com.mojang.authlib.properties.PropertyMap
import joptsimple.OptionParser
import joptsimple.OptionSpec
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.event.client.ApplicationStartEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.main.GameConfiguration.*
import net.minecraft.util.Session
import java.io.File
import java.net.*

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val event = ApplicationStartEvent(args.contains("--drgn-developer"))
        Dragonfly.eventBus.post(event)

        if (event.isCancelled) {
            return
        }

        System.setProperty("java.net.preferIPv4Stack", "true")
        val optionParser = OptionParser()
        optionParser.allowsUnrecognizedOptions()
        optionParser.accepts("demo")
        optionParser.accepts("fullscreen")
        optionParser.accepts("checkGlErrors")
        optionParser.accepts("drgn-developer")

        val specServer: OptionSpec<String> = optionParser.accepts("server").withRequiredArg()
        val specPort: OptionSpec<Int> =
            optionParser.accepts("port").withRequiredArg().ofType(Int::class.java).defaultsTo(25565)
        val specGameDir: OptionSpec<File> =
            optionParser.accepts("gameDir").withRequiredArg().ofType(File::class.java).defaultsTo(File(System.getProperty("user.dir")))
        val specAssetsDir: OptionSpec<File> =
            optionParser.accepts("assetsDir").withRequiredArg().ofType(File::class.java)
        val specResourcePackDir: OptionSpec<File> =
            optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File::class.java)
        val specProxyHost: OptionSpec<String> = optionParser.accepts("proxyHost").withRequiredArg()
        val specProxyPort: OptionSpec<Int> =
            optionParser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", *arrayOfNulls(0))
                .ofType(Int::class.java)
        val specProxyUser: OptionSpec<String> = optionParser.accepts("proxyUser").withRequiredArg()
        val specProxyPass: OptionSpec<String> = optionParser.accepts("proxyPass").withRequiredArg()
        val specUsername: OptionSpec<String> =
            optionParser.accepts("username").withRequiredArg().defaultsTo("DragonflyUser")
        val specUUID: OptionSpec<String> = optionParser.accepts("uuid").withRequiredArg()
        val specAccessToken: OptionSpec<String> = optionParser.accepts("accessToken").withRequiredArg().required()
        val specVersion: OptionSpec<String> = optionParser.accepts("version").withRequiredArg().required()
        val specWidth: OptionSpec<Int> =
            optionParser.accepts("width").withRequiredArg().ofType(Int::class.java).defaultsTo(854)
        val specHeight: OptionSpec<Int> =
            optionParser.accepts("height").withRequiredArg().ofType(Int::class.java).defaultsTo(480)
        val specUserProperties: OptionSpec<String> =
            optionParser.accepts("userProperties").withRequiredArg().defaultsTo("{}")
        val specProfileProperties: OptionSpec<String> =
            optionParser.accepts("profileProperties").withRequiredArg().defaultsTo("{}")
        val specAssetIndex: OptionSpec<String> = optionParser.accepts("assetIndex").withRequiredArg()
        val specUserType: OptionSpec<String> = optionParser.accepts("userType").withRequiredArg().defaultsTo("legacy")
        val nonOptions: OptionSpec<String> = optionParser.nonOptions()
        val optionset = optionParser.parse(*args)
        val list = optionset.valuesOf(nonOptions)
        if (!list.isEmpty()) {
            println("Completely ignored arguments: $list")
        }
        val s = optionset.valueOf(specProxyHost)
        var proxy = Proxy.NO_PROXY
        if (s != null) {
            try {
                proxy = Proxy(
                    Proxy.Type.SOCKS,
                    InetSocketAddress(s, optionset.valueOf(specProxyPort))
                )
            } catch (ignored: Exception) {
            }
        }
        val s1 = optionset.valueOf(specProxyUser)
        val s2 = optionset.valueOf(specProxyPass)
        val isDeveloperMode = optionset.has("drgn-developer")
        if (proxy != Proxy.NO_PROXY && isNullOrEmpty(s1) && isNullOrEmpty(s2)) {
            Authenticator.setDefault(object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(s1, s2.toCharArray())
                }
            })
        }
        val width = optionset.valueOf(specWidth)
        val height = optionset.valueOf(specHeight)
        val fullscreen = optionset.has("fullscreen")
        val checkGlErrors = optionset.has("checkGlErrors")
        val demo = optionset.has("demo")
        val version = optionset.valueOf(specVersion)
        val gson = GsonBuilder().registerTypeAdapter(
            PropertyMap::class.java,
            PropertyMap.Serializer()
        ).create()
        val userProperties = gson.fromJson(optionset.valueOf(specUserProperties), PropertyMap::class.java)
        val profileProperties = gson.fromJson(optionset.valueOf(specProfileProperties), PropertyMap::class.java)
        val gameDir = optionset.valueOf(specGameDir)
        val assetsDir = if (optionset.has(specAssetsDir)) optionset.valueOf(specAssetsDir) else File(gameDir, "assets/")
        val resourcePacksDir = if (optionset.has(specResourcePackDir)) optionset.valueOf(specResourcePackDir) else File(
            gameDir,
            "resourcepacks/"
        )
        val uuid = if (optionset.has(specUUID)) specUUID.value(optionset) else specUsername.value(optionset)
        val assetsIndex = if (optionset.has(specAssetIndex)) specAssetIndex.value(optionset) else null
        val server = optionset.valueOf(specServer)
        val port = optionset.valueOf(specPort)
        val session = Session(
            specUsername.value(optionset),
            uuid,
            specAccessToken.value(optionset),
            specUserType.value(optionset)
        )
        val gameConfiguration = GameConfiguration(
            UserInformation(session, userProperties, profileProperties, proxy),
            DisplayInformation(width, height, fullscreen, checkGlErrors),
            FolderInformation(gameDir, resourcePacksDir, assetsDir, assetsIndex),
            GameInformation(demo, version, isDeveloperMode),
            ServerInformation(server, port)
        )
        Runtime.getRuntime().addShutdownHook(object : Thread("Client Shutdown Thread") {
            override fun run() {
                Minecraft.stopIntegratedServer()
            }
        })
        Thread.currentThread().name = "Client thread"
        Minecraft(gameConfiguration).run()
    }

    private fun isNullOrEmpty(str: String?): Boolean {
        return str != null && str.isNotEmpty()
    }
}