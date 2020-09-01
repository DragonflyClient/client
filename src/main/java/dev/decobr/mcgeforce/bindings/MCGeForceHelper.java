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

import dev.decobr.mcgeforce.utils.NVGSDK;
import dev.decobr.mcgeforce.utils.NativeUtils;
import org.apache.logging.log4j.LogManager;

public class MCGeForceHelper {

    public static final MCGeForceHelper instance = new MCGeForceHelper();
    private static final String prefix = "[GeForce Experience Helper] ";
    private static long handlePtr;

    static {
        NativeUtils.loadLibrary("/GfeSDK.dll");
        NativeUtils.loadLibrary("/MCGeForce.dll");
    }

    public int createCallbackRetCode = -1;
    private int highlights;

    @SuppressWarnings("unused")
    private static void callback(int code) {
        if (!NVGSDK.succeeded(code)) {
            LogManager.getLogger().info(prefix + "Received return code: " + NVGSDK.retCodeToString(code));
            LogManager.getLogger().info(prefix + "Received error from NVIDIA: " + NVGSDK.retCodeToString(code));
        }
    }

    @SuppressWarnings("unused")
    private static void createCallback(int code, String version) {
        instance.createCallbackRetCode = code;

        if (NVGSDK.succeeded(code)) {
            LogManager.getLogger().info(prefix + "Successfully connected with GeForce Experience! (Version: " + version + ")");
        } else {
            LogManager.getLogger().info(prefix + "Failed to connect with GeForce Experience! Error: " + NVGSDK.retCodeToString(code));
        }
    }

    @SuppressWarnings("unused")
    private static void numberOfHighlightsCallback(int amount) {
        instance.highlights = amount;
    }

    private native long init();

    private native void setVideoHighlight(long handle, String id, String groupID, int start, int end);

    private native void showHighlightsEditor(long handle, String groupID);

    private native void addGroup(long handle, String id, String name);

    private native void closeGroup(long handle, String groupID, boolean deleteHighlights);

    private native void getNumOfHighlights(long handle, String groupID);

    public void initialise() {
        handlePtr = instance.init();

        if (handlePtr > 0) {
            queryAmountOfHighlights();
        }
    }

    public void saveHighlight(String id, int start, int end) {
        instance.setVideoHighlight(handlePtr, id, "mcgeforcemod", start, end);
        queryAmountOfHighlights();
    }

    public void showHighlights() {
        instance.showHighlightsEditor(handlePtr, "mcgeforcemod");
    }

    public void deleteCachedHighlights() {
        instance.closeGroup(handlePtr, "mcgeforcemod", true);
        instance.addGroup(handlePtr, "mcgeforcemod", "MCGeForce");
        instance.highlights = 0;
    }

    public void queryAmountOfHighlights() {
        getNumOfHighlights(handlePtr, "mcgeforcemod");
    }

    public int getHighlightAmount() {
        return highlights;
    }

}
