package net.inceptioncloud.dragonfly.engine.inspector

import tornadofx.*

class MyView1: View() {
    override val root = vbox {
        paddingAll = 50.0
        button("Go to MyView2") {
            action {
                replaceWith(MyView2::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.LEFT))
            }
        }
    }
}

class MyView2: View() {
    override val root = vbox {
        paddingAll = 50.0
        button("Go to MyView1") {
            action {
                replaceWith(MyView1::class, ViewTransition.Slide(0.3.seconds, ViewTransition.Direction.RIGHT))
            }
        }
    }
}