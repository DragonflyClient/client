package net.inceptioncloud.dragonfly.engine.inspector

import javafx.application.Platform
import javafx.scene.input.*
import javafx.scene.text.Font
import javafx.stage.Stage
import net.inceptioncloud.dragonfly.Dragonfly
import org.apache.logging.log4j.LogManager
import net.minecraft.client.Minecraft
import tornadofx.*
import java.io.File
import kotlin.concurrent.thread

/**
 * ## Inspector
 *
 * This class is the core of the graphics engine inspector. It represents the interface between
 * the JavaFx (TornadoFx) application and the Minecraft client. No "internal" classes of the
 * inspector are referenced outside of this package.
 */
object Inspector {

    /**
     * The main view of the [InspectorApp].
     *
     * This property is set when the [InspectorView] is initialized and is referenced in
     * [stageUpdated] to re-populate the tree view that lists the stage- (and widget-) hierarchy.
     */
    lateinit var inspectorView: InspectorView

    /**
     * The [Stage] of the [InspectorApp].
     *
     * A stage is the core of a JavaFx (and TornadoFx) application. This property is set when
     * the [InspectorApp] is [started][InspectorApp.start] and is referenced in [launch] to
     * re-open the inspector after it has been closed.
     */
    lateinit var stage: Stage

    /**
     * A boolean property that represents whether the [InspectorApp] has already been launched.
     *
     * If this is the case, the [launch] function will try to re-open the [stage] instead of
     * launching the whole [InspectorApp] which is illegal in JavaFx. Additionally, [stageUpdated]
     * will only re-populate the hierarchy in the [inspectorView] if the app has been launched
     * since [inspectorView] would otherwise not be initialized.
     */
    var isLaunched = false
        private set

    /**
     * Launches the inspector.
     *
     * If the function is called for the first time it will launch the [InspectorApp] after changing
     * some JavaFx and TornadoFx preferences and set [isLaunched] to true. If this is not the case
     * (and thus [isLaunched] is falsy) it tries to re-open the [stage].
     *
     * If the [stage] property is (for some reason) not initialized or if the inspector is already
     * visible, this function will do nothing more than logging a warning.
     *
     * This function can be called safely whenever the user wants to launch the inspector, which is
     * usually only when pressing Ctrl+Shift+I (which the [InspectorSubscriber] listens for).
     */
    fun launch() {
        if (!Dragonfly.isDeveloperMode) {
            LogManager.getLogger().info("The inspector is only available in development mode!")
            return
        }

        if (isLaunched) {
            Platform.runLater {
                if (::stage.isInitialized && !stage.isShowing) {
                    LogManager.getLogger().info("Re-opening inspector stage")
                    stage.show()
                } else LogManager.getLogger().warn("Cannot re-open inspector stage!")
            }
            return
        }

        thread {
            isLaunched = true

            Platform.setImplicitExit(false);
            FX.layoutDebuggerShortcut = KeyCodeCombination.valueOf("Ctrl+Shift+I") as KeyCodeCombination?

            System.setProperty("prism.lcdtext", "false")
            System.setProperty("prism.text", "t2k")

            Font.loadFont(File("dragonfly\\assets\\fonts\\SF Pro Display Medium.ttf").inputStream(), 15.0)
            Font.loadFont(File("dragonfly\\assets\\fonts\\SF Pro Display.ttf").inputStream(), 15.0)

            launch<InspectorApp>()
        }
    }

    /**
     * Re-populates the stage hierarchy in the [inspectorView] upon stage switch.
     *
     * This function is called by the client whenever a stage is switched. At the time of
     * writing this is only the case when the gui screen is switched and thus this function
     * is only called from [Minecraft.displayGuiScreen].
     *
     * Before re-populating the hierarchy, this function checks if the inspector is
     * [launched][isLaunched] and switches to the JavaFx-Thread using [Platform.runLater].
     */
    @JvmStatic
    fun stageUpdated() {
        if (isLaunched) {
            Platform.runLater {
                inspectorView.repopulate()
            }
        }
    }
}