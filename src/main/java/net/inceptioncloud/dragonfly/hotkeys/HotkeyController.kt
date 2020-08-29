package net.inceptioncloud.dragonfly.hotkeys

import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.hotkeys.types.ChatHotkey
import net.inceptioncloud.dragonfly.hotkeys.types.config.ChatHotkeyConfig
import net.inceptioncloud.dragonfly.hotkeys.types.data.*
import net.inceptioncloud.dragonfly.mods.HotkeysMod
import org.apache.logging.log4j.LogManager
import org.lwjgl.input.Keyboard
import java.io.File
import kotlin.reflect.full.primaryConstructor

object HotkeyController {

    /**
     * Contains all registered hotkeys
     */
    @JvmStatic
    lateinit var hotkeys: MutableList<Hotkey>

    /**
     * Contains all keys which are pressed and the time when they were pressed
     */
    private val pressedKeys = HashMap<Int, Long>()

    /**
     * File where all hotkeys are saved for a new game session
     */
    private val repositoryFile = File("dragonfly/mods/hotkeys/hotkeys-repository.json")

    /**
     * A representation of the [repositoryFile] content as a collection of [HotkeyData]s.
     */
    private lateinit var repository: HotkeyRepository

    /**
     * The GSON instance of the [HotkeysMod] that is responsible for (de-)serialization.
     */
    val gson = HotkeysMod.optionsBase.gson

    init {
        readRepository()
        LogManager.getLogger().info("Parsed ${hotkeys.size} hotkey(s) from the repository")
    }
    /**
     * Adds the [hotkey] to the [hotkeys] collection and to the [repository].
     */
    fun addHotkey(hotkey: Hotkey) {
        repository.add(hotkey.data)
        hotkeys.add(hotkey)
    }

    /**
     * Removes the [hotkey] from the [hotkeys] collection and from the [repository].
     */
    fun removeHotkey(hotkey: Hotkey) {
        hotkeys.remove(hotkey)
        repository.remove(hotkey.data)
    }

    @JvmStatic
    fun updateKeys() {
        for (hotkey in hotkeys) {

            val primary = hotkey.data.key
            val secondary = hotkey.data.modifierKey

            if (secondary == null) {
                if (primary.isKeyPressed() && !Keyboard.KEY_LSHIFT.isKeyPressed() && !Keyboard.KEY_LCONTROL.isKeyPressed() && !Keyboard.KEY_LMENU.isKeyPressed()) {
                    hotkey.progressForward()
                } else {
                    if (!hotkey.transition.isAtStart) {
                        hotkey.progressBackward()
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
                            hotkey.progressForward()
                        }
                    }
                } else {
                    hotkey.progressBackward()
                }
            }
        }
    }

    /**
     * Commits the [repository] by writing it to the [repositoryFile].
     */
    fun commit() {
        repositoryFile.writeText(gson.toJson(repository))
    }

    /**
     * Reads the [repositoryFile] saving it as the [repository] and parsing all hotkeys. The
     * parsed hotkeys are saved in the [hotkeys] collection.
     */
    private fun readRepository() {
        val content = repositoryFile.takeIf { it.exists() }?.readText()

        if (content != null) {
            repository = gson.fromJson(content, HotkeyRepository::class.java)
            hotkeys = repository.map {
                val hotkeyClass = it.type.hotkeyClass
                val configClass = it.type.configClass
                val config = gson.fromJson(it.config, configClass.java)
                hotkeyClass.primaryConstructor!!.call(it, config) as Hotkey
            }.toMutableList()
        } else {
            repository = HotkeyRepository()
            hotkeys = mutableListOf()
        }
    }

    /**
     * Function which is used to check if the key (Key index as Int) is pressed
     */
    private fun Int.isKeyPressed(): Boolean = Keyboard.isKeyDown(this)

}