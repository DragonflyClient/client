package net.inceptioncloud.dragonfly.overlay.modal

import net.inceptioncloud.dragonfly.engine.internal.AssembledWidget
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition

abstract class ModalWidget(
    val name: String, desiredWidth: Double, desiredHeight: Double
): AssembledWidget<ModalWidget>(), IDimension, IPosition {

    override var width: Double by property(desiredWidth)
    override var height: Double by property(desiredHeight)
    override var x: Double by property(0.0)
    override var y: Double by property(0.0)
}