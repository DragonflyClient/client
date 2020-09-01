package net.inceptioncloud.dragonfly.mods.hotkeys

import net.inceptioncloud.dragonfly.mods.hotkeys.types.data.HotkeyData
import net.inceptioncloud.dragonfly.mods.hotkeys.types.data.HotkeyRepository
import org.apache.logging.log4j.LogManager
import org.lwjgl.input.Keyboard
import java.io.File
import kotlin.reflect.full.primaryConstructor

class HotkeysController {

    /**
     * Contains all registered hotkeys
     */
    lateinit var hotkeys: MutableList<Hotkey>

    /**
     * Contains all keys which are pressed and the time when they were pressed
     */
    val pressedKeys = HashMap<Int, Long>()

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

    fun updateKeys() {
        if (HotkeysMod.enabled) {
            val modifierKeys = listOf(Keyboard.KEY_LCONTROL, Keyboard.KEY_LSHIFT, Keyboard.KEY_LMENU)

            modifierKeys.forEach { updateKeyState(it) }
            hotkeys.forEach {
                if (it.isSatisfied()) {
                    it.progressForward()
                } else {
                    it.progressBackward()
                }
            }
        }

    }

    fun updateKeyState(key: Int) {
        if (key.isKeyPressed() && !pressedKeys.containsKey(key)) {
            pressedKeys[key] = System.currentTimeMillis()
        } else if (!key.isKeyPressed() && pressedKeys.containsKey(key)) {
            pressedKeys.remove(key)
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
}

/**
 * Function which is used to check if the key (Key index as Int) is pressed
 */
fun Int.isKeyPressed(): Boolean = Keyboard.isKeyDown(this)