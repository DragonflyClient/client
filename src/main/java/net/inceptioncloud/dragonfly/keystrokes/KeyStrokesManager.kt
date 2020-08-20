package net.inceptioncloud.dragonfly.keystrokes

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import khttp.get
import net.inceptioncloud.dragonfly.engine.internal.WidgetColor
import java.io.File

object KeyStrokesManager {

    var keystrokes = mutableListOf<KeyStroke>()
    val colorsFile = File("dragonfly/keystrokes.json")

    lateinit var forward: KeyStroke
    lateinit var backward: KeyStroke
    lateinit var left: KeyStroke
    lateinit var right: KeyStroke
    lateinit var jump: KeyStroke
    lateinit var sprint: KeyStroke
    lateinit var attack: KeyStroke
    lateinit var use: KeyStroke

    var colorTextActive: WidgetColor
    var colorTextInactive: WidgetColor
    var colorBgActive: WidgetColor
    var colorBgInactive: WidgetColor

    init {
        createColorsFile()

        colorTextActive = getColor("text-active")
        colorTextInactive = getColor("text-inactive")
        colorBgActive = getColor("bg-active")
        colorBgInactive = getColor("bg-inactive")
    }

    /**
     * Function which is used to register a new KeyStroke
     */
    @JvmStatic
    fun registerKeyStrokes(keyDesc: String, keyCode: Int) {
        val keyStroke = KeyStroke(keyCode, keyDesc)
        when (keyDesc) {
            "key.forward" -> forward = keyStroke
            "key.back" -> backward = keyStroke
            "key.left" -> left = keyStroke
            "key.right" -> right = keyStroke
            "key.jump" -> jump = keyStroke
            "key.sprint" -> sprint = keyStroke
            "key.attack" -> attack = keyStroke
            "key.use" -> use = keyStroke
            else -> return
        }
        keystrokes.add(keyStroke)

    }

    /**
     * This Function is used to create the 'keystrokes.json' file in the 'dragonfly' folder
     */
    private fun createColorsFile() {
        if (!colorsFile.exists()) {
            val gson = GsonBuilder().setPrettyPrinting().create()

            val obj = JsonObject()
            obj.addProperty("text-active", "1.0,1.0,1.0,1.0")
            obj.addProperty("text-inactive", "1.0,1.0,1.0,1.0")
            obj.addProperty("bg-active", "0.9,0.5,0.1,0.8")
            obj.addProperty("bg-inactive", "0.5,0.5,0.5,0.6")

            colorsFile.writeText(gson.toJson(obj))
        }
    }

    /**
     * This function returns a WidgetColor based on the config values (see 'createColorsFile' function)
     */
    private fun getColor(name: String): WidgetColor {
        val obj = Gson().fromJson(colorsFile.readText(), JsonObject::class.java)
        val elements = obj.get(name).asString.split(",")

        val red = elements[0].toDouble()
        val green = elements[1].toDouble()
        val blue = elements[2].toDouble()
        val alpha = elements[3].toDouble()

        return WidgetColor(red, green, blue, alpha)
    }

}