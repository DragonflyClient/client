package net.inceptioncloud.dragonfly.apps.settings

import net.inceptioncloud.dragonfly.options.OptionsBase
import java.io.File

/**
 * This class manages the reading and writing of the options to the specific file.
 */
object DragonflyOptions : OptionsBase(File("dragonfly/options.json"))