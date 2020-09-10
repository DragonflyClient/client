package dev.decobr.mcgeforce.utils;
/*
 * Class dev.decobr.mcgeforce.utils.NVGSDK is published under the The MIT License:
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

public class NVGSDK {
    public static boolean succeeded(int code) {
        return code >= 0;
    }

    public static String retCodeToString(int code) {
        switch (code) {
            case 0:
                return "NVGSDK_SUCCESS";
            case 1001:
                return "NVGSDK_SUCCESS_VERSION_OLD_SDK";
            case 1002:
                return "NVGSDK_SUCCESS_VERSION_OLD_GFE";
            case 1003:
                return "NVGSDK_SUCCESS_PENDING";
            case 1004:
                return "NVGSDK_SUCCESS_USER_NOT_INTERESTED";
            case 1005:
                return "NVGSDK_SUCCESS_PERMISSION_GRANTED";
            case 1006:
                return "NVGSDK_SUCCESS_LINKED";
            case -1001:
                return "NVGSDK_ERR_GENERIC";
            case -1002:
                return "NVGSDK_ERR_GFE_VERSION";
            case -1003:
                return "NVGSDK_ERR_SDK_VERSION";
            case -1004:
                return "NVGSDK_ERR_NOT_IMPLEMENTED";
            case -1005:
                return "NVGSDK_ERR_INVALID_PARAMETER";
            case -1006:
                return "NVGSDK_ERR_NOT_SET";
            case -1007:
                return "NVGSDK_ERR_SHADOWPLAY_IR_DISABLED";
            case -1008:
                return "NVGSDK_ERR_SDK_IN_USE";
            case -1009:
                return "NVGSDK_ERR_GROUP_NOT_FOUND";
            case -1010:
                return "NVGSDK_ERR_FILE_NOT_FOUND";
            case -1011:
                return "NVGSDK_ERR_HIGHLIGHTS_SETUP_FAILED";
            case -1012:
                return "NVGSDK_ERR_HIGHLIGHTS_NOT_CONFIGURED";
            case -1013:
                return "NVGSDK_ERR_HIGHLIGHTS_SAVE_FAILED";
            case -1014:
                return "NVGSDK_ERR_UNEXPECTED_EXCEPTION";
            case -1015:
                return "NVGSDK_ERR_NO_HIGHLIGHTS";
            case -1016:
                return "NVGSDK_ERR_NO_CONNECTION";
            case -1017:
                return "NVGSDK_ERR_PERMISSION_NOT_GRANTED";
            case -1018:
                return "NVGSDK_ERR_PERMISSION_DENIED";
            case -1019:
                return "NVGSDK_ERR_INVALID_HANDLE";
            case -1020:
                return "NVGSDK_ERR_UNHANDLED_EXCEPTION";
            case -1021:
                return "NVGSDK_ERR_OUT_OF_MEMORY";
            case -1022:
                return "NVGSDK_ERR_LOAD_LIBRARY";
            case -1023:
                return "NVGSDK_ERR_LIB_CALL_FAILED";
            case -1024:
                return "NVGSDK_ERR_IPC_FAILED";
            case -1025:
                return "NVGSDK_ERR_CONNECTION";
            case -1026:
                return "NVGSDK_ERR_MODULE_NOT_LOADED";
            case -1027:
                return "NVGSDK_ERR_LIB_CALL_TIMEOUT";
            case -1028:
                return "NVGSDK_ERR_APPLICATION_LOOKUP_FAILED";
            case -1029:
                return "NVGSDK_ERR_APPLICATION_NOT_KNOWN";
            case -1030:
                return "NVGSDK_ERR_FEATURE_DISABLED";
            case -1031:
                return "NVGSDK_ERR_APP_NO_OPTIMIZATION";
            case -1032:
                return "NVGSDK_ERR_APP_SETTINGS_READ";
            case -1033:
                return "NVGSDK_ERR_APP_SETTINGS_WRITE";
            default:
                return "Unknown Return Code";
        }
    }
}
