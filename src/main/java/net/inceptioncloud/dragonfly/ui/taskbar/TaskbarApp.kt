package net.inceptioncloud.dragonfly.ui.taskbar

import net.minecraft.util.ResourceLocation

class TaskbarApp(val name: String, val icon: String) {
    val resourceLocation = ResourceLocation("dragonflyres/icons/taskbar/apps/$icon.png")
}