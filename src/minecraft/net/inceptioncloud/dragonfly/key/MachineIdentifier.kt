package net.inceptioncloud.dragonfly.key

import oshi.SystemInfo

/**
 * Generation of unique identifiers for the current machine.
 */
object MachineIdentifier {

    /**
     * Generates a unique machine identifier that consists of six hyphen-delimited 32-bit hexadecimal
     * values that represent parts of the computer hardware.
     */
    fun generateIdentifier(): String {
        return with(SystemInfo()) {
            val identifier = hardware.processor.processorIdentifier
            val baseboard = hardware.computerSystem.baseboard

            compressData(
                operatingSystem.manufacturer, operatingSystem.family,
                identifier.vendor, identifier.processorID,
                baseboard.manufacturer, baseboard.serialNumber
            )
        }
    }

    /**
     * Compresses the input [data] by converting it to 32-bit hexadecimal values and joining them
     * to a string delimited with hyphens.
     */
    private fun compressData(vararg data: String) = data.joinToString("-") {
        String.format("%08x", it.hashCode())
    }
}