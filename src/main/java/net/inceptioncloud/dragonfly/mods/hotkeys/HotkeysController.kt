package net.inceptioncloud.dragonfly.mods.hotkeys

import com.google.common.eventbus.Subscribe
import net.inceptioncloud.dragonfly.event.control.KeyInputEvent
import net.inceptioncloud.dragonfly.mc
import net.inceptioncloud.dragonfly.mods.hotkeys.types.data.HotkeyData
import net.inceptioncloud.dragonfly.mods.hotkeys.types.data.HotkeyRepository
import net.inceptioncloud.dragonfly.utils.ListParameterizedType
import org.apache.logging.log4j.LogManager
import org.lwjgl.input.Keyboard
import java.io.File
import kotlin.reflect.full.primaryConstructor

object HotkeysController {

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

    /**
     * Handles the key input by subscribing to the [KeyInputEvent].
     */
    @Subscribe
    fun onKeyInput(event: KeyInputEvent) {
        if (!HotkeysMod.enabled) return

        for (hotkey in hotkeys) {
            if (event.press) {
                if (event.key != hotkey.data.key) continue // activate if primary key is pressed

                if (hotkey.areModifiersSatisfied()) {
                    hotkey.progressForward() // start if modifiers are also satisfied
                }
            } else {
                if (event.key == hotkey.data.key || !hotkey.areModifiersSatisfied()) {
                    hotkey.progressBackward() // cancel if modifier key is primary key is released
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
            try {
                val typeToken = ListParameterizedType(HotkeyData::class.java)
                repository = HotkeyRepository(gson.fromJson(content, typeToken))
                hotkeys = repository.mapNotNull { data ->
                    try {
                        val hotkeyClass = data.type.hotkeyClass
                        val configClass = data.type.configClass
                        val config = gson.fromJson(data.config, configClass.java)
                        hotkeyClass.primaryConstructor!!.call(data, config) as Hotkey
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        null
                    }
                }.toMutableList()
                return
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

        repository = HotkeyRepository(listOf())
        hotkeys = mutableListOf()
    }
}

/**
 * Function which is used to check if the key (Key index as Int) is pressed
 */
fun Int.isKeyPressed(): Boolean = Keyboard.isKeyDown(this)