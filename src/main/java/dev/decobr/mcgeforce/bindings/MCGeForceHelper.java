package dev.decobr.mcgeforce.bindings;
/*
 * Class dev.decobr.mcgeforce.bindings.MCGeForceHelper is published under the The MIT License:
 *
 * Copyright (c) 2012 Adam Heinrich <adam@adamh.cz>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import dev.decobr.mcgeforce.utils.EnumHighlightType;
import dev.decobr.mcgeforce.utils.NVGSDK;
import net.inceptioncloud.dragonfly.mods.nvidiahighlights.NvidiaHighlightsMod;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MCGeForceHelper {

    public static final MCGeForceHelper instance = new MCGeForceHelper();
    private static final String prefix = "[GeForce Helper] ";
    public boolean isSystemValid = isSystemValidStatic;
    public static boolean isSystemValidStatic;
    private static long handlePtr;

    static {
        isSystemValidStatic = validateSystem();

        if (isSystemValidStatic) {
            System.loadLibrary("GfeSDK");
            System.loadLibrary("MCGeForce");

            initialise();
        }
    }

    public int createCallbackRetCode = -1;
    private int highlights;

    private static Boolean validateSystem() {
        LogManager.getLogger().info(prefix + "Checking if system is qualified...");

        String os = System.getProperty("os.name");
        Boolean isShadowPlayRunning = isProcessRunning("nvsphelper64.exe");

        if (os.toLowerCase().contains("windows") && isShadowPlayRunning) {
            LogManager.getLogger().info(prefix + "System is qualified! (OS: '" + os + "' - nvsphelper64.exe: 'true')");
            return true;
        } else {
            LogManager.getLogger().info(prefix + "System is not qualified! (OS: '" + os + "' - nvsphelper64.exe: '" + isShadowPlayRunning +  "')");
            return false;
        }
    }

    private static Boolean isProcessRunning(String processName) {
        try {
            if (!System.getProperty("os.name").toLowerCase().contains("linux")) {
                String line = "";
                String pidInfo = "";
                Process process = null;

                process = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");

                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

                while ((line = input.readLine()) != null) {
                    pidInfo += line;
                }

                input.close();

                return pidInfo.contains(processName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static void callback(int code) {
        if (isSystemValidStatic) {
            if (!NVGSDK.succeeded(code)) {
                LogManager.getLogger().info(prefix + "Received return code: " + NVGSDK.retCodeToString(code));
                LogManager.getLogger().info(prefix + "Received error from NVIDIA: " + NVGSDK.retCodeToString(code));
            }
        }
    }

    private static void createCallback(int code, String version) {
        if (isSystemValidStatic) {
            instance.createCallbackRetCode = code;

            if (NVGSDK.succeeded(code)) {
                LogManager.getLogger().info(prefix + "Successfully connected with GeForce Experience! (Version: " + version + ")");
            } else {
                LogManager.getLogger().info(prefix + "Failed to connect with GeForce Experience! Error: " + NVGSDK.retCodeToString(code));
            }
        }
    }

    private static void numberOfHighlightsCallback(int amount) {
        if (isSystemValidStatic) {
            instance.highlights = amount;
        }
    }

    private static void initialise() {
        if (isSystemValidStatic) {
            handlePtr = instance.init();

            if (handlePtr > 0) {
                queryAmountOfHighlights();
            }
        }
    }

    private static void queryAmountOfHighlights() {
        if (isSystemValidStatic) {
            getNumOfHighlights(handlePtr, "mcdragonfly");
        }
    }

    private static native void getNumOfHighlights(long handle, String groupID);

    public void deleteCachedHighlights() {
        if (isSystemValid) {
            instance.closeGroup(handlePtr, "mcdragonfly", true);
            instance.addGroup(handlePtr, "mcdragonfly", "Dragonfly");
            instance.highlights = 0;
        }
    }

    public int getHighlightAmount() {
        if (isSystemValid) {
            return highlights;
        } else {
            return -1;
        }
    }

    public void saveHighlight(EnumHighlightType highlightType) {
        if (isSystemValid) {

            if(!NvidiaHighlightsMod.INSTANCE.getEnabled()) {
                return;
            }else if(highlightType == EnumHighlightType.KILL && !NvidiaHighlightsMod.INSTANCE.getSaveKills()) {
                return;
            }else if(highlightType == EnumHighlightType.DEATH && !NvidiaHighlightsMod.INSTANCE.getSaveDeaths()) {
                return;
            }else if(highlightType == EnumHighlightType.WIN && !NvidiaHighlightsMod.INSTANCE.getSaveWins()) {
                return;
            }

            LogManager.getLogger().info("Saving Highlight...");
            instance.setVideoHighlight(handlePtr, highlightType.getId(), "mcdragonfly", -(NvidiaHighlightsMod.INSTANCE.getLength() * 1000), 1000);
            queryAmountOfHighlights();
        }
    }

    public void showHighlights() {
        if (isSystemValid) {
            instance.showHighlightsEditor(handlePtr, "mcdragonfly");
        }
    }

    private native long init();

    private native void addGroup(long handle, String id, String name);

    private native void showHighlightsEditor(long handle, String groupID);

    private native void closeGroup(long handle, String groupID, boolean deleteHighlights);

    private native void setVideoHighlight(long handle, String id, String groupID, int start, int end);

}
