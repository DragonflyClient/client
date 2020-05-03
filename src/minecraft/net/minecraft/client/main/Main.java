package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import joptsimple.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.io.File;
import java.net.*;
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

        if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(s1) && isNullOrEmpty(s2)) {
            Authenticator.setDefault(new Authenticator()
            {
                protected PasswordAuthentication getPasswordAuthentication ()
                {
                    return new PasswordAuthentication(s1, s2.toCharArray());
                }
            });
        }

        int i = optionset.valueOf(specWidth);
        int j = optionset.valueOf(specHeight);
        boolean flag = optionset.has("fullscreen");
        boolean flag1 = optionset.has("checkGlErrors");
        boolean flag2 = optionset.has("demo");
        String s3 = optionset.valueOf(specVersion);
        Gson gson = ( new GsonBuilder() ).registerTypeAdapter(PropertyMap.class, new Serializer()).create();
        PropertyMap propertymap = gson.fromJson(optionset.valueOf(specUserProperties), PropertyMap.class);
        PropertyMap propertymap1 = gson.fromJson(optionset.valueOf(specProfileProperties), PropertyMap.class);
        File file1 = optionset.valueOf(specGameDir);
        File file2 = optionset.has(specAssetsDir) ? optionset.valueOf(specAssetsDir) : new File(file1, "assets/");
        File file3 = optionset.has(specResourcePackDir) ? optionset.valueOf(specResourcePackDir) : new File(file1, "resourcepacks/");
        String s4 = optionset.has(specUUID) ? specUUID.value(optionset) : specUsername.value(optionset);
        String s5 = optionset.has(specAssetIndex) ? specAssetIndex.value(optionset) : null;
        String s6 = optionset.valueOf(specServer);
        Integer integer = optionset.valueOf(specPort);
        Session session = new Session(specUsername.value(optionset), s4, specAccessToken.value(optionset), specUserType.value(optionset));
        GameConfiguration gameconfiguration = new GameConfiguration(new GameConfiguration.UserInformation(session, propertymap, propertymap1, proxy), new GameConfiguration.DisplayInformation(i, j, flag, flag1), new GameConfiguration.FolderInformation(file1, file3, file2, s5), new GameConfiguration.GameInformation(flag2, s3), new GameConfiguration.ServerInformation(s6, integer));
        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread")
        {
            public void run ()
            {
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
