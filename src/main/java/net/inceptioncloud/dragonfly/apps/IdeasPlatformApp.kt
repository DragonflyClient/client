package net.inceptioncloud.dragonfly.apps

import net.inceptioncloud.dragonfly.ui.taskbar.TaskbarApp

object IdeasPlatformApp : TaskbarApp("Ideas Platform") {

    override fun open() = url("https://ideas.playdragonfly.net/?utm_source=client&utm_medium=mod_link&utm_campaign=ideas_platform")
}
