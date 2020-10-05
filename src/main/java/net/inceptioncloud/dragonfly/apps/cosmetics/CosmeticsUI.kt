package net.inceptioncloud.dragonfly.apps.cosmetics

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.inceptioncloud.dragonfly.controls.*
import net.inceptioncloud.dragonfly.controls.sidebar.SidebarEntry
import net.inceptioncloud.dragonfly.controls.ui.ControlsUI
import net.inceptioncloud.dragonfly.cosmetics.logic.CosmeticData
import net.inceptioncloud.dragonfly.cosmetics.logic.CosmeticsManager
import net.inceptioncloud.dragonfly.cosmetics.types.capes.CapeManager
import net.inceptioncloud.dragonfly.design.color.DragonflyPalette
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation
import net.inceptioncloud.dragonfly.engine.animation.alter.MorphAnimation.Companion.morph
import net.inceptioncloud.dragonfly.engine.internal.ImageResource
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseQuad
import net.inceptioncloud.dragonfly.engine.sequence.easing.EaseSine
import net.inceptioncloud.dragonfly.engine.switch
import net.inceptioncloud.dragonfly.engine.tooltip.Tooltip
import net.inceptioncloud.dragonfly.engine.tooltip.TooltipAlignment
import net.inceptioncloud.dragonfly.engine.widgets.assembled.RoundButton
import net.inceptioncloud.dragonfly.options.*
import net.inceptioncloud.dragonfly.overlay.toast.Toast
import net.inceptioncloud.dragonfly.utils.Either
import net.inceptioncloud.dragonfly.utils.MojangRequest
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import java.awt.image.BufferedImage

class CosmeticsUI(previousScreen: GuiScreen) : ControlsUI(previousScreen) {

    override val sidebarWidth: Double = 400.0

    override val controlsWidth: Double get() = width - 400.0 - 120.0 - 43.0 - previewWidth
    override val controlsX: Double get() = sidebarWidth + 60.0

    val previewWidth = 400.0
    val previewX: Double get() = controlsX + controlsWidth + 60.0 + 43.0

    override val scrollbarX: Double? get() = width - previewWidth

    override val placeholderImage: ResourceLocation? = ResourceLocation("dragonflyres/vectors/equipment.png")
    override val placeholderText: String? = "Select a cosmetic item from the left to customize it."

    private val cosmetics by lazy { CosmeticsManager.dragonflyAccountCosmetics }

    override fun initGui() {
        super.initGui()

        +PlayerPreview {
            x = previewX
            y = 0.0
            width = previewWidth
            height = this@CosmeticsUI.height.toDouble()
        } id "preview"

        +RoundButton {
            width = 250.0
            height = 40.0
            x = previewX + previewWidth / 2.0 - width / 2.0
            y = this@CosmeticsUI.height - height - 30.0
            textSize = 40
            text = "Synchronize Cosmetics"
            arc = 13.0
            hoverAction = {
                detachAnimation<MorphAnimation>()
                morph(
                    20, EaseQuad.IN_OUT,
                    ::color to if (it) DragonflyPalette.accentNormal else DragonflyPalette.background
                )?.start()
            }
            onClick {
                GlobalScope.launch {
                    CosmeticsManager.refreshCosmeticsSync()

                    if (CosmeticsManager.dragonflyAccountCosmetics?.isNotEmpty() == true) {
                        CosmeticsUI(previousScreen)
                    } else {
                        NoCosmeticsUI(previousScreen)
                    }.switch()
                }
            }
        } id "sync-cosmetics"
    }

    override fun produceSidebar(): Collection<SidebarEntry> {
        val cosmetics = cosmetics ?: return listOf()
        val accounts = cosmetics
            .groupBy { it.minecraft }
            .toSortedMap(
                Comparator { _, o2 ->
                    when (o2) {
                        mc.session?.profile?.id.toString() -> 1
                        null -> -2
                        else -> -1
                    }
                }
            )

        return accounts.flatMap { entry ->
            var playerName: String? = null
            var playerSkull: BufferedImage? = null
            val isAccountLoggedIn = entry.key == mc.session?.profile?.id.toString()
            val uuid = entry.key

            if (uuid != null) {
                MojangRequest()
                    .withUUID(uuid)
                    .getName { playerName = it }
                    .getSkull { playerSkull = it }
            } else {
                MojangRequest()
                    .withUUID("606e2ff0-ed77-4842-9d6c-e1d3321c7838")
                    .getSkull { playerSkull = it }
            }

            val playerSkullTexture = playerSkull?.let { DynamicTexture(it) }
            val icon = playerSkullTexture?.let { ImageResource(it) }
            val titleEntry = if (playerName != null) {
                SidebarEntry("${DragonflyPalette.accentBright.chatCode}$playerName", icon)
            } else {
                SidebarEntry("${DragonflyPalette.accentDark.chatCode}Not bound", icon).apply {
                    tooltip = Tooltip("You can link your Dragonfly Cosmetics to your Minecraft accounts in the dashboard. (Click)", TooltipAlignment.BELOW)
                    openUrl = "https://dashboard.playdragonfly.net/cosmetics?utm_source=client&utm_medium=tooltip&utm_campaign=cosmetics"
                }
            }.apply {
                iconMargin = 10.0
                isSelectable = false
            }

            listOf(titleEntry) + entry.value.mapNotNull { data ->
                val cosmeticName = CosmeticsManager.getDatabaseModelById(data.cosmeticId)?.get("name")?.asString
                val prefix = if (!isAccountLoggedIn) DragonflyPalette.foreground.darker(0.8).chatCode else ""
                val playerData = mc.thePlayer?.cosmetics?.firstOrNull { it.cosmeticQualifier == data.cosmeticQualifier }

                cosmeticName?.let { text ->
                    SidebarEntry(prefix + text, null, playerData ?: data).apply { isSelectable = isAccountLoggedIn }
                }
            }
        }
    }

    override fun produceControls(entry: SidebarEntry): Collection<ControlElement<*>>? {
        val data = entry.metadata as? CosmeticData ?: return null
        val cosmetic = CosmeticsManager.cosmetics.firstOrNull { it.cosmeticId == data.cosmeticId }
        val controls = cosmetic?.generateControls(data)
        val model = CosmeticsManager.getDatabaseModelById(data.cosmeticId)
        val isCape = model != null && model.has("type") && model.get("type").asString == "CAPE"

        val preset = mutableListOf(
            TitleControl("General"),
            BooleanControl(
                Either(b = PseudoOptionKey.new<Boolean>()
                    .set { value ->
                        data.enabled = value
                        if (isCape) mc.thePlayer?.let { CapeManager.downloadCape(it) }
                    }
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

    override fun onClose() {
        GlobalScope.launch {
            var success = true

            CosmeticsManager.clearCache(mc.session?.profile?.id)
            sidebarManager.entries
                .mapNotNull { it.metadata as? CosmeticData }
                .forEach {
                    val a = CosmeticsManager.toggleCosmetic(it.cosmeticQualifier, it.enabled)
                    val b = CosmeticsManager.configureCosmetic(it.cosmeticQualifier, it.config)

                    if (!a || !b) success = false
                }

            if (!success) {
                Toast.queue("§cCould not upload all cosmetic configurations! Please try again later.", 500)
            }
        }
    }

    operator fun <T> Collection<T>.times(amount: Int) = this.flatMap { item -> (0..amount).map { item } }
}