package net.inceptioncloud.dragonfly.hotkeys

import net.inceptioncloud.dragonfly.hotkeys.types.HotkeyTypeChat
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
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
    val configFile = File("dragonfly/hotkeys.json")

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
        extra1: String,
        extra2: String
    ): Boolean {

        if (!configFile.exists()) {
            configFile.writeText("[]")
        }

        try {

            val reader = FileReader(configFile)
            val obj = JSONParser().parse(reader)
            val hotkeys = obj as JSONArray

            for (entry in hotkeys) {
                val hotkey = entry as JSONObject

                if (modifierKey == null) {
                    if (hotkey.get("$key") != null) {
                        return false
                    }
                } else {
                    if (hotkey.get("${modifierKey}_$key") != null) {
                        return false
                    }
                }

            }

            val details = JSONObject()
            details.put("key", key.toString())
            details.put("modifierKey", modifierKey.toString())
            details.put("time", time.toString())
            details.put("delay", delay.toString())
            details.put("color", String.format("#%02x%02x%02x", color.red, color.green, color.blue))
            details.put("type", type)
            details.put("extra1", extra1)
            details.put("extra2", extra2)

            val hotkey = JSONObject()
            if (modifierKey == null) {
                hotkey.put(key, details)
            } else {
                hotkey.put("${modifierKey}_$key", details)
            }

            hotkeys.add(hotkey)

            if (type == "HotkeyTypeChat") {
                this.hotkeys.add(HotkeyTypeChat(key, modifierKey, time, delay, color, extra1.toBoolean(), extra2))
            }


            try {

                val writer = FileWriter(configFile)
                writer.write(hotkeys.toJSONString())
                writer.flush()
                writer.close()
                return true

            } catch (e: Exception) {
                e.printStackTrace()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    /**
     * Function to remove a hotkey (Take a look at the documentation of the 'Hotkey.kt' class to learn more about the parameters)
     */
    fun removeHotkey(
        key: Int,
        modifierKey: Int?
    ) {

        if (configFile.exists()) {

            try {

                val reader = FileReader(configFile)
                val obj = JSONParser().parse(reader)
                val hotkeys = obj as JSONArray

                for (entry in hotkeys) {
                    val hotkey = entry as JSONObject

                    if (modifierKey == null) {
                        if (hotkey.get("$key") != null) {
                            hotkeys.remove(hotkey)
                            (hotkey.get("$key") as JSONObject).getAsHotkey()?.let { this.hotkeys.remove(it) }
                            break
                        }
                    } else {
                        if (hotkey.get("${modifierKey}_$key") != null) {
                            hotkeys.remove(hotkey)
                            (hotkey.get("${modifierKey}_$key") as JSONObject).getAsHotkey()
                                ?.let { this.hotkeys.remove(it) }
                            break
                        }
                    }

                }

                try {

                    val writer = FileWriter(configFile)
                    writer.write(hotkeys.toJSONString())
                    writer.flush()
                    writer.close()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    /**
     * Function which is used to read the 'configFile' and converts all hotkeys into an instance of the 'Hotkey.kt' class
     */
    fun registerHotkeys() {
        if (configFile.exists()) {
            val reader = FileReader(configFile)
            val obj = JSONParser().parse(reader)
            val hotkeys = obj as JSONArray

            for (title in hotkeys) {
                for (key in (title as JSONObject).keys) {
                    val hotkey = title.get(key) as JSONObject
                    hotkey.getAsHotkey()?.let { this.hotkeys.add(it) }
                }

            }

        } else {
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