package net.inceptioncloud.dragonfly.screenshot

import java.awt.Image
import java.awt.datatransfer.*

/**
 * Utility class for copying an image to the clipboard.
 */
class TransferableImage(var i: Image) : Transferable {
    @Throws(UnsupportedFlavorException::class)
    override fun getTransferData(flavor: DataFlavor): Any {
        return if (flavor.equals(DataFlavor.imageFlavor)) {
            i
        } else {
            throw UnsupportedFlavorException(flavor)
        }
    }

    override fun getTransferDataFlavors(): Array<DataFlavor?> {
        val flavors = arrayOfNulls<DataFlavor>(1)
        flavors[0] = DataFlavor.imageFlavor
        return flavors
    }

    override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
        val flavors = transferDataFlavors
        for (dataFlavor in flavors) {
            if (flavor.equals(dataFlavor)) {
                return true
            }
        }
        return false
    }
}