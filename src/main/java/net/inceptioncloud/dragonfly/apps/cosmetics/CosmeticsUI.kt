package net.inceptioncloud.dragonfly.apps.cosmetics

import net.inceptioncloud.dragonfly.controls.*
import net.inceptioncloud.dragonfly.controls.sidebar.SidebarEntry
import net.inceptioncloud.dragonfly.controls.ui.ControlsUI
import net.inceptioncloud.dragonfly.cosmetics.logic.CosmeticData
import net.inceptioncloud.dragonfly.cosmetics.logic.CosmeticsManager
import net.inceptioncloud.dragonfly.cosmetics.types.wings.CosmeticWingsConfig
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.internal.ImageResource
import net.inceptioncloud.dragonfly.options.*
import net.inceptioncloud.dragonfly.utils.Either
import net.inceptioncloud.dragonfly.utils.MojangRequest
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import java.awt.image.BufferedImage

class CosmeticsUI(previousScreen: GuiScreen) : ControlsUI(previousScreen) {

    override val sidebarWidth: Double = 400.0

    override val controlsWidth: Double
        get() = width - 400.0 - 120.0 - 43.0 - previewWidth
    override val controlsX: Double
        get() = sidebarWidth + 60.0

    val previewWidth = 500.0
    val previewX: Double
            get() = controlsX + controlsWidth + 60.0 + 43.0

    override val scrollbarX: Double?
        get() = width - previewWidth

    override val placeholderImage: ResourceLocation? = ResourceLocation("dragonflyres/vectors/equipment.png")
    override val placeholderText: String? = "Select a cosmetic item from the left to customize it."

    private val cosmetics by lazy { CosmeticsManager.dragonflyAccountCosmetics }

    override fun initGui() {
        super.initGui()

        +PlayerPreview {
            x = previewX
            y = 0.0
            width = 500.0
            height = this@CosmeticsUI.height.toDouble()
        } id "preview"
    }

    override fun produceSidebar(): Collection<SidebarEntry> {
        val cosmetics = cosmetics ?: return listOf()
        val accounts = cosmetics.groupBy { it.minecraft }
            .toSortedMap(
                Comparator { o1, _ -> if (mc.session?.profile?.id.toString() == o1) -1 else 1 }
            )

        return accounts.flatMap { entry ->
            var playerName: String? = null
            var playerSkull: BufferedImage? = null
            val isAccountLoggedIn = entry.key == mc.session?.profile?.id.toString()

            MojangRequest()
                .withUUID(entry.key)
                .getName { playerName = it }
                .getSkull { playerSkull = it }

            val playerSkullTexture = playerSkull?.let { DynamicTexture(it) }
            val icon = playerSkullTexture?.let { ImageResource(it) }

            listOf(
                SidebarEntry("${DragonflyPalette.accentBright.chatCode}$playerName", icon).apply {
                    iconMargin = 10.0
                    isSelectable = false
                }
            ) + entry.value.mapNotNull { data ->
                val cosmeticName = CosmeticsManager.getDatabaseModelById(data.cosmeticId)?.get("name")?.asString
                val prefix = if (!isAccountLoggedIn) DragonflyPalette.foreground.darker(0.8).chatCode else ""
                val playerData = mc.thePlayer?.cosmetics?.firstOrNull { it.cosmeticQualifier == data.cosmeticQualifier }

                cosmeticName?.let { text ->
                    SidebarEntry(prefix + text, null, playerData).apply { isSelectable = isAccountLoggedIn }
                }
            }
        }
    }

    override fun produceControls(entry: SidebarEntry): Collection<ControlElement<*>>? {
        val data = entry.metadata as? CosmeticData ?: return null
        val cosmetic = CosmeticsManager.cosmetics.firstOrNull { it.cosmeticId == data.cosmeticId }
        val controls = cosmetic?.generateControls(cosmetic.parseConfig(data))

        val preset = mutableListOf(
            TitleControl("General"),
            BooleanControl(
                Either(b = PseudoOptionKey.new<Boolean>()
                    .set { data.enabled = it }
                    .get { data.enabled }
                    .defaultValue { true }
                    .build()
                ), "Enable cosmetic"
            )
        )

        if (controls != null)
            preset.add(TitleControl("Configuration", "Configure the appearance of your cosmetic item"))

        controls?.mapNotNull { it as? OptionControlElement<*> }?.forEach { it.isResettable = true }

        return if (controls == null) preset else preset + controls
    }

    operator fun <T> Collection<T>.times(amount: Int): Collection<T> {
        return this.flatMap { item -> (0..amount).map { item } }
    }
}