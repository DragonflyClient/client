package net.inceptioncloud.dragonfly.apps.modmanager.controls

import net.inceptioncloud.dragonfly.engine.internal.AssembledWidget
import net.inceptioncloud.dragonfly.engine.structure.IDimension
import net.inceptioncloud.dragonfly.engine.structure.IPosition

abstract class ControlElement<W : AssembledWidget<W>> : AssembledWidget<W>(), IPosition, IDimension