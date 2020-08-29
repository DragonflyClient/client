package net.inceptioncloud.dragonfly.hotkeys

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.processNextEventInCurrentThread
import net.inceptioncloud.dragonfly.hotkeys.types.HotkeyTypeChat
import net.inceptioncloud.dragonfly.hotkeys.types.dataclasses.HotkeyData
import net.inceptioncloud.dragonfly.hotkeys.types.dataclasses.HotkeyTypeChatData
import net.inceptioncloud.dragonfly.hotkeys.types.dataclasses.HotkeyTypeData
import org.apache.logging.log4j.LogManager
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object HotkeyController {

    /**
     * Contains all registered hotkeys
     */
    @JvmStatic
    val hotkeys = mutableListOf<Hotkey>()

    /**
     * Contains all hotkeys which current have her delay active
     */
    val blockedHotkeys = mutableListOf<Hotkey>()

    /**
     * Contains all keys which are pressed and the time when they were pressed
     */
    val pressedKeys = HashMap<Int, Long>()

    /**
     * File where all hotkeys are saved for a new game session
     */
    val configFile = File("dragonfly/hotkeys/hotkeysDB.json")

    init {
        registerHotkeys()
        LogManager.getLogger().info("Hotkey initializing successful!")
    }

    /**
     * Function to add a new hotkey (Take a look at the documentation of the 'Hotkey.kt' class to learn more about the parameters)
     */
    fun addHotkey(
        key: Int,
        modifierKey: Int?,
        time: Double,
        delay: Double,
        color: Color,
        type: String,
        typeData: HotkeyTypeData
    ): Boolean {

        if (!configFile.exists()) {
            configFile.createNewFile()
            configFile.writeText("[]")
        }

        val array = Gson().fromJson(configFile.readText(), JsonArray().javaClass)

        for (entry in array) {
            val obj = entry as JsonObject

            val data = obj.get("data") as JsonObject
            val currentKey = data.get("key").asInt
            val currentModifierKey = data.get("modifierKey")

            if (key == currentKey) {
                if (currentModifierKey == null) {
                    return false
                } else {
                    if (modifierKey == currentModifierKey.asInt) {
                        return false
                    }
                }
            }
        }

        val data = HotkeyData(
            key,
            modifierKey,
            time,
            delay,
            String.format("#%02x%02x%02x", color.red, color.green, color.blue)
        )

        val dataString = Gson().toJsonTree(data)
        val typeDataString = Gson().toJsonTree(typeData)

        val obj = JsonObject()
        obj.addProperty("type", type)
        obj.add("data", dataString)
        obj.add("typeData", typeDataString)

        array.add(obj)

        if (typeData is HotkeyTypeChatData) {
            hotkeys.add(HotkeyTypeChat(key, modifierKey, time, delay, color, typeData.fadeOut, typeData.message))
        }

        configFile.writeText(GsonBuilder().setPrettyPrinting().create().toJson(array)).also { return true }
    }

    /**
     * Function to remove a hotkey (Take a look at the documentation of the 'Hotkey.kt' class to learn more about the parameters)
     */
    fun removeHotkey(
        key: Int,
        modifierKey: Int?
    ): Boolean {

        if (configFile.exists()) {

            val array = Gson().fromJson(configFile.readText(), JsonArray().javaClass)
            val newArray = JsonArray()

            for (entry in array) {
                val obj = entry as JsonObject

                val data = obj.get("data") as JsonObject
                val typeData = obj.get("typeData") as JsonObject
                val currentKey = data.get("key").asInt
                val currentModifierKey = data.get("modifierKey")

                if (currentKey == key) {
                    if (currentModifierKey != null) {
                        if (currentModifierKey.asInt != modifierKey) {
                            newArray.add(obj)
                        }
                    }
                } else {
                    newArray.add(obj)
                }

                if (obj.get("type").asString == "HotkeyTypeChat") {
                    hotkeys.remove(
                        HotkeyTypeChat(
                            key,
                            modifierKey,
                            data.get("time").asDouble,
                            data.get("delay").asDouble,
                            Color.decode(data.get("color").asString),
                            typeData.get("fadeOut").asBoolean,
                            typeData.get("message").asString
                        )
                    )
                }

            }

            configFile.writeText(GsonBuilder().setPrettyPrinting().create().toJson(newArray)).also { return true }


        }

        return false
    }

    /**
     * Function which is used to read the 'configFile' and converts all hotkeys into an instance of the 'Hotkey.kt' class
     */
    fun registerHotkeys() {
        if (configFile.exists()) {

            val array = Gson().fromJson(configFile.readText(), JsonArray().javaClass)

            for(entry in array) {
                val obj = entry as JsonObject

                val data = obj.get("data") as JsonObject
                val typeData = obj.get("typeData") as JsonObject
                val currentModifierKey = data.get("modifierKey")
                var modifierKey: Int?

                if(currentModifierKey == null) {
                    modifierKey = null
                }else {
                    modifierKey = currentModifierKey.asInt
                }

                println("-")
                println(data.get("key").asInt)
                println(modifierKey)
                println(currentModifierKey)
                println("-")

                if (obj.get("type").asString == "HotkeyTypeChat") {
                    hotkeys.add(
                        HotkeyTypeChat(
                            data.get("key").asInt,
                            modifierKey,
                            data.get("time").asDouble,
                            data.get("delay").asDouble,
                            Color.decode(data.get("color").asString),
                            typeData.get("fadeOut").asBoolean,
                            typeData.get("message").asString
                        )
                    )
                }

            }

            for(hotkey in hotkeys) {
                if(hotkey is HotkeyTypeChat) {
                    println("${hotkey.message} ${hotkey.key} ${hotkey.modifierKey}")
                }
            }

        } else {
            configFile.createNewFile()
            configFile.writeText("[]")
        }
    }

    /**
     * Function which is used to check all keys-presses for the hotkey feature
     */
    @JvmStatic
    fun updateKeys() {

        for (hotkey in hotkeys) {

            val primary = hotkey.key
            val secondary = hotkey.modifierKey

            if (secondary == null) {
                if (primary.isKeyPressed() && !Keyboard.KEY_LSHIFT.isKeyPressed() && !Keyboard.KEY_LCONTROL.isKeyPressed() && !Keyboard.KEY_LMENU.isKeyPressed()) {

                    if (hotkey is HotkeyTypeChat && !blockedHotkeys.contains(hotkey)) {
                        hotkey.direction = 1
                    }

                } else {
                    if (hotkey is HotkeyTypeChat) {
                        if (!hotkey.transition.isAtStart) {
                            hotkey.direction = -1
                        }
                    }
                }
            } else {

                if (primary.isKeyPressed() && !pressedKeys.containsKey(primary)) {
                    pressedKeys[primary] = System.currentTimeMillis()
                } else if (secondary.isKeyPressed() && !pressedKeys.containsKey(secondary)) {
                    pressedKeys[secondary] = System.currentTimeMillis()
                } else if (!primary.isKeyPressed() && !secondary.isKeyPressed()) {
                    pressedKeys.remove(primary)
                    pressedKeys.remove(secondary)
                }

                if (primary.isKeyPressed() && secondary.isKeyPressed()) {

                    if (pressedKeys.containsKey(primary) && pressedKeys.containsKey(secondary)) {
                        if (pressedKeys[primary]!! > pressedKeys[secondary]!!) {
                            if (hotkey is HotkeyTypeChat && !blockedHotkeys.contains(hotkey)) {
                                hotkey.direction = 1
                            }

                        }
                    }
                } else {

                    if (hotkey is HotkeyTypeChat && !blockedHotkeys.contains(hotkey)) {
                        hotkey.direction = -1
                    }

                }

            }
        }
    }

    /**
     * Function which converts an JSONObject into an instance of the 'Hotkey.kt' class (is used in the 'registerHotkeys' function)
     */
    fun JSONObject.getAsHotkey(): Hotkey? {

        val key: Int = this.get("key").toString().toInt()
        val modifierKey: Int? = this.get("modifierKey").toString().toIntOrNull()
        val time: Double = this.get("time").toString().toDouble()
        val delay: Double = this.get("delay").toString().toDouble()
        val color: Color = Color.decode(this.get("color") as String)
        val type: String = this.get("type") as String
        val extra1: String = this.get("extra1") as String
        val extra2: String = this.get("extra2") as String

        if (type == "HotkeyTypeChat") {
            return HotkeyTypeChat(key, modifierKey, time, delay, color, extra1.toBoolean(), extra2)
        }

        return null
    }

    /**
     * Function which is used to check if the key (Key index as Int) is pressed
     */
    private fun Int.isKeyPressed(): Boolean = Keyboard.isKeyDown(this)

}

fun main() {

    val file = File("runtime/dragonfly/hotkeys2.json")

    val array = Gson().fromJson(file.readText(), JsonArray().javaClass)

    val data = HotkeyData(
        34,
        null,
        1.0,
        1.0,
        String.format("#%02x%02x%02x", Color.BLUE.red, Color.BLUE.green, Color.BLUE.blue)
    )
    val typeData = HotkeyTypeChatData(true, "Hello there!")

    val dataString = Gson().toJsonTree(data)
    val typeDataString = Gson().toJsonTree(typeData)

    val obj1 = JsonObject()
    obj1.addProperty("type", "HotkeyTypeChat")
    obj1.add("data", dataString)
    obj1.add("typeData", typeDataString)

    array.add(obj1)

    file.writeText(GsonBuilder().setPrettyPrinting().create().toJson(array))

    /*val array = Gson().fromJson(file.readText(), JsonArray().javaClass)

    for (entry in array) {
        val obj = entry as JsonObject

        val type = obj.get("type")
        val data = obj.get("data") as JsonObject
        val typeData = obj.get("typeData") as JsonObject

        println(
            "The Hotkey with the type $type has the key \"${data.get("key")}\" and the following message: ${typeData.get(
                "message"
            )}!"
        )
    }*/

    /*val data = HotkeyData(
        34,
        null,
        1.0,
        1.0,
        String.format("#%02x%02x%02x", Color.BLUE.red, Color.BLUE.green, Color.BLUE.blue)
    )
    val typeData = HotkeyTypeChatData(true, "Hello there!")

    val dataString = Gson().toJsonTree(data)
    val typeDataString = Gson().toJsonTree(typeData)

    val obj1 = JsonObject()
    obj1.addProperty("type", "HotkeyTypeChat")
    obj1.add("data", dataString)
    obj1.add("typeData", typeDataString)

    val obj2 = JsonObject()
    obj2.addProperty("type", "HotkeyTypeChat")
    obj2.add("data", dataString)
    obj2.add("typeData", typeDataString)

    val array = JsonArray()
    array.add(obj1)
    array.add(obj2)

    if (!file.exists()) {
        file.createNewFile()
    }

    file.writeText(GsonBuilder().setPrettyPrinting().create().toJson(array))*/

}