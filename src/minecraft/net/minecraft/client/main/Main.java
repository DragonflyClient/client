package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;

public class Main
{
    public static void main (String[] args)
    {
        System.setProperty("java.net.preferIPv4Stack", "true");
        OptionParser optionparser = new OptionParser();
        optionparser.allowsUnrecognizedOptions();
        optionparser.accepts("demo");
        optionparser.accepts("fullscreen");
        optionparser.accepts("checkGlErrors");
        optionparser.accepts("drgn-debug");
        OptionSpec<String> specServer = optionparser.accepts("server").withRequiredArg();
        OptionSpec<Integer> specPort = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
        OptionSpec<File> specGameDir = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
        OptionSpec<File> specAssetsDir = optionparser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        OptionSpec<File> specResourcePackDir = optionparser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        OptionSpec<String> specProxyHost = optionparser.accepts("proxyHost").withRequiredArg();
        OptionSpec<Integer> specProxyPort = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
        OptionSpec<String> specProxyUser = optionparser.accepts("proxyUser").withRequiredArg();
        OptionSpec<String> specProxyPass = optionparser.accepts("proxyPass").withRequiredArg();
        OptionSpec<String> specUsername = optionparser.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L);
        OptionSpec<String> specUUID = optionparser.accepts("uuid").withRequiredArg();
        OptionSpec<String> specAccessToken = optionparser.accepts("accessToken").withRequiredArg().required();
        OptionSpec<String> specVersion = optionparser.accepts("version").withRequiredArg().required();
        OptionSpec<Integer> specWidth = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
        OptionSpec<Integer> specHeight = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
        OptionSpec<String> specUserProperties = optionparser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> specProfileProperties = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> specAssetIndex = optionparser.accepts("assetIndex").withRequiredArg();
        OptionSpec<String> specUserType = optionparser.accepts("userType").withRequiredArg().defaultsTo("legacy");
        OptionSpec<String> nonOptions = optionparser.nonOptions();
        OptionSet optionset = optionparser.parse(args);
        List<String> list = optionset.valuesOf(nonOptions);

        if (!list.isEmpty()) {
            System.out.println("Completely ignored arguments: " + list);
        }

        String s = optionset.valueOf(specProxyHost);
        Proxy proxy = Proxy.NO_PROXY;

        if (s != null) {
            try {
                proxy = new Proxy(Type.SOCKS, new InetSocketAddress(s, optionset.valueOf(specProxyPort)));
            } catch (Exception ignored) {
            }
        }

        final String s1 = optionset.valueOf(specProxyUser);
        final String s2 = optionset.valueOf(specProxyPass);
        final boolean isDrgnDebug = optionset.has("drgn-debug");

        if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(s1) && isNullOrEmpty(s2)) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(s1, s2.toCharArray());
                }
            });
        }

        int width = optionset.valueOf(specWidth);
        int height = optionset.valueOf(specHeight);
        boolean fullscreen = optionset.has("fullscreen");
        boolean checkGlErrors = optionset.has("checkGlErrors");
        boolean demo = optionset.has("demo");
        String version = optionset.valueOf(specVersion);
        Gson gson = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new Serializer()).create();
        PropertyMap userProperties = gson.fromJson(optionset.valueOf(specUserProperties), PropertyMap.class);
        PropertyMap profileProperties = gson.fromJson(optionset.valueOf(specProfileProperties), PropertyMap.class);
        File gameDir = optionset.valueOf(specGameDir);
        File assetsDir = optionset.has(specAssetsDir) ? optionset.valueOf(specAssetsDir) : new File(gameDir, "assets/");
        File resourcePacksDir = optionset.has(specResourcePackDir) ? optionset.valueOf(specResourcePackDir) : new File(gameDir, "resourcepacks/");
        String uuid = optionset.has(specUUID) ? specUUID.value(optionset) : specUsername.value(optionset);
        String assetsIndex = optionset.has(specAssetIndex) ? specAssetIndex.value(optionset) : null;
        String server = optionset.valueOf(specServer);
        Integer port = optionset.valueOf(specPort);
        Session session = new Session(specUsername.value(optionset), uuid, specAccessToken.value(optionset), specUserType.value(optionset));
        GameConfiguration gameconfiguration = new GameConfiguration(
                new GameConfiguration.UserInformation(session, userProperties, profileProperties, proxy),
                new GameConfiguration.DisplayInformation(width, height, fullscreen, checkGlErrors),
                new GameConfiguration.FolderInformation(gameDir, resourcePacksDir, assetsDir, assetsIndex),
                new GameConfiguration.GameInformation(demo, version, isDrgnDebug),
                new GameConfiguration.ServerInformation(server, port)
        );
        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
            public void run() {
                Minecraft.stopIntegratedServer();
            }
        });
        Thread.currentThread().setName("Client thread");
        new Minecraft(gameconfiguration).run();
    }

    private static boolean isNullOrEmpty (String str)
    {
        return str != null && !str.isEmpty();
    }
}
