package net.inceptioncloud.dragonfly.account

import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.font.renderer.IFontRenderer
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.structure.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundedRectangle
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField

/**
 * A simple widget that is used by the main menu to display the current Dragonfly
 * authentication status.
 */
class LoginStatusWidget(
    initializerBlock: (LoginStatusWidget.() -> Unit)? = null
) : AssembledWidget<LoginStatusWidget>(initializerBlock), IPosition, IDimension, IColor {

    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
    override var width: Double by property(200.0)
    override var height: Double by property(50.0)
    override var color: WidgetColor by property(DragonflyPalette.foreground)

    var fontRenderer: IFontRenderer? by property(null)

    override fun assemble(): Map<String, Widget<*>> = mapOf(
        "container" to RoundedRectangle(),
        "text" to TextField()
    )

    override fun updateStructure() {
        val account = Dragonfly.account

        "container"<RoundedRectangle> {
            x = this@LoginStatusWidget.x
            y = this@LoginStatusWidget.y
            width = this@LoginStatusWidget.width
            height = this@LoginStatusWidget.height
            arc = 10.0
        }

        "text"<TextField> {
            x = this@LoginStatusWidget.x
            y = this@LoginStatusWidget.y
            width = this@LoginStatusWidget.width
            height = this@LoginStatusWidget.height
            padding = 5.0
            textAlignHorizontal = Alignment.CENTER
            textAlignVertical = Alignment.CENTER
            fontRenderer = this@LoginStatusWidget.fontRenderer
            dropShadow = true
            shadowDistance = 1.5
            shadowColor = WidgetColor(0, 0, 0, 50)
        }

        if (account != null) {
            "container"<RoundedRectangle> {
                color = DragonflyPalette.foreground
            }

            "text"<TextField> {
                color = DragonflyPalette.background
                staticText = account.username
            }

            clickAction = {}
        } else {
            "container"<RoundedRectangle> {
                color = DragonflyPalette.accentNormal
            }

            "text"<TextField> {
                color = DragonflyPalette.foreground
                staticText = "Login"
            }

            clickAction = {
                AuthenticationBridge.showLoginModal()
            }
        }
    }
}