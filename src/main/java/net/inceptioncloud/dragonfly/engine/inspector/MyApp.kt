package net.inceptioncloud.dragonfly.engine.inspector

import tornadofx.*

class MyApp : App(MyView1::class)

fun main(args: Array<String>) {
    launch<MyApp>(args)
}
