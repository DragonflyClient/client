package net.minecraft.client.gui

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import net.inceptioncloud.dragonfly.Dragonfly
import net.inceptioncloud.dragonfly.Dragonfly.fontManager
import net.inceptioncloud.dragonfly.Dragonfly.splashScreen
import net.inceptioncloud.dragonfly.design.color.GreyToneColor
import net.inceptioncloud.dragonfly.design.color.RGB
import net.inceptioncloud.dragonfly.engine.internal.*
import net.inceptioncloud.dragonfly.engine.widgets.assembled.TextField
import net.inceptioncloud.dragonfly.mods.hotkeys.HotkeysMod
import net.inceptioncloud.dragonfly.mods.keystrokes.*
import net.inceptioncloud.dragonfly.mods.togglesneak.ToggleSneakMod
import net.inceptioncloud.dragonfly.options.sections.OptionsSectionScoreboard.scoreboardBackground
import net.inceptioncloud.dragonfly.options.sections.OptionsSectionScoreboard.scoreboardScores
import net.inceptioncloud.dragonfly.options.sections.OptionsSectionScoreboard.scoreboardTitle
import net.inceptioncloud.dragonfly.transition.number.DoubleTransition
import net.inceptioncloud.dragonfly.transition.supplier.ForwardBackward
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RenderItem
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.resources.I18n
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.boss.BossStatus
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.inventory.IInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.potion.Potion
import net.minecraft.scoreboard.Score
import net.minecraft.scoreboard.ScoreObjective
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.util.*
import optifine.Config
import optifine.CustomColors
import java.util.*
import java.util.stream.Collectors

class GuiIngame(private val mc: Minecraft) : Gui() {
    private val rand = Random()
    private val itemRenderer: RenderItem

    /**
     * returns a pointer to the persistant Chat GUI, containing all previous chat messages and such
     */
    /**
     * ChatGUI instance that retains all previous chat data
     */
    val chatGUI: GuiNewChat
    private val streamIndicator: GuiStreamIndicator
    private val overlayDebug: GuiOverlayDebug

    /**
     * Controller for all set hotkeys
     */
    var controller = HotkeysMod.controller

    /**
     * The spectator GUI for this in-game GUI instance
     */
    @JvmField
    val spectatorGui: GuiSpectator

    val tabList: GuiPlayerTabOverlay

    /**
     * Previous frame vignette brightness (slowly changes by 1% each frame)
     */
    var prevVignetteBrightness = 1.0f
    var updateCounter = 0
        private set

    /**
     * The string specifying which record music is playing
     */
    private var recordPlaying = ""

    /**
     * How many ticks the record playing message will be displayed
     */
    private var recordPlayingUpFor = 0
    private var recordAnimateColor = false

    /**
     * Remaining ticks the item highlight should be visible
     */
    private var remainingHighlightTicks = 0

    var hotbarX = 0
    var hotbarY = 0
    var hotbarW = 0
    var hotbarH = 0

    /**
     * The ItemStack that is currently being highlighted
     */
    private var highlightingItemStack: ItemStack? = null
    private var title_timer = 0
    private var title_title = ""
    private var title_subtitle = ""
    private var title_fadeIn = 0
    private var title_stay = 0
    private var title_fadeOut = 0
    private var playerHealth = 0
    private var lastPlayerHealth = 0

    /**
     * The last recorded system time
     */
    private var lastSystemTime = 0L

    /**
     * Used with updateCounter to make the heart bar flash
     */
    private var healthUpdateCounter = 0L
    private var actionBarDisplayed = false
    private val actionBar =
        DoubleTransition.builder().start(0.0).end(1.0).amountOfSteps(20)
            .autoTransformator(ForwardBackward { recordPlayingUpFor > 0 })
            .reachStart { actionBarDisplayed = false }.build()

    val stage = WidgetStage("Ingame Overlay")

    fun func_175177_a() {
        title_fadeIn = 10
        title_stay = 70
        title_fadeOut = 20
    }

    fun renderGameOverlay(partialTicks: Float) {
        val scaledresolution = ScaledResolution(mc)
        val scaledWidth = scaledresolution.scaledWidth
        val scaledHeight = scaledresolution.scaledHeight
        mc.entityRenderer.setupOverlayRendering(false)
        GlStateManager.enableBlend()
        if (Config.isVignetteEnabled()) {
            renderVignette(mc.thePlayer.getBrightness(partialTicks), scaledresolution)
        } else {
            GlStateManager.enableDepth()
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        }
        val itemstack = mc.thePlayer.inventory.armorItemInSlot(3)
        if (mc.gameSettings.thirdPersonView == 0 && itemstack != null && itemstack.item === Item.getItemFromBlock(
                Blocks.pumpkin
            )
        ) {
            renderPumpkinOverlay(scaledresolution)
        }
        if (!mc.thePlayer.isPotionActive(Potion.confusion)) {
            val f =
                mc.thePlayer.prevTimeInPortal + (mc.thePlayer.timeInPortal - mc.thePlayer.prevTimeInPortal) * partialTicks
            if (f > 0.0f) {
                func_180474_b(f, scaledresolution)
            }
        }
        if (mc.playerController.isSpectator) {
            spectatorGui.renderTooltip(scaledresolution, partialTicks)
        } else {
            renderTooltip(scaledresolution, partialTicks)
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        mc.textureManager.bindTexture(icons)
        GlStateManager.enableBlend()
        if (showCrosshair() && mc.gameSettings.thirdPersonView < 1) {
            GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0)
            GlStateManager.enableAlpha()
            this.drawTexturedModalRect(scaledWidth / 2 - 7, scaledHeight / 2 - 7, 0, 0, 16, 16)
        }
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        mc.mcProfiler.startSection("bossHealth")
        renderBossHealth()
        mc.mcProfiler.endSection()
        if (mc.playerController.shouldDrawHUD()) {
            renderPlayerStats(scaledresolution)
        }
        GlStateManager.disableBlend()
        if (mc.thePlayer.sleepTimer > 0) {
            mc.mcProfiler.startSection("sleep")
            GlStateManager.disableDepth()
            GlStateManager.disableAlpha()
            val l = mc.thePlayer.sleepTimer
            var f2 = l.toFloat() / 100.0f
            if (f2 > 1.0f) {
                f2 = 1.0f - (l - 100) as Float / 10.0f
            }
            val k = (220.0f * f2).toInt() shl 24 or 1052704
            drawRect(0, 0, scaledWidth, scaledHeight, k)
            GlStateManager.enableAlpha()
            GlStateManager.enableDepth()
            mc.mcProfiler.endSection()
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        val i2 = scaledWidth / 2 - 91
        if (mc.thePlayer.isRidingHorse) {
            renderHorseJumpBar(scaledresolution, i2)
        } else if (mc.playerController.gameIsSurvivalOrAdventure()) {
            renderExpBar(scaledresolution, i2)
        }
        if (mc.gameSettings.heldItemTooltips && !mc.playerController.isSpectator) {
            drawSelectedItemName(scaledresolution)
        } else if (mc.thePlayer.isSpectator) {
            spectatorGui.func_175263_a(scaledresolution)
        }
        if (mc.isDemo) {
            renderDemo(scaledresolution)
        }
        if (mc.gameSettings.showDebugInfo) {
            overlayDebug.renderDebugInfo(scaledresolution)
        }
        if (canDisplayActionBar && actionBarDisplayed) {
            mc.mcProfiler.startSection("overlayMessage")
            val f3 = recordPlayingUpFor.toFloat() - partialTicks
            var k1 = (f3 * 255.0f / 20.0f).toInt()
            if (k1 > 255) {
                k1 = 255
            }
            GlStateManager.pushMatrix()
            GlStateManager.translate((scaledWidth / 2).toFloat(), (scaledHeight - 68).toFloat(), 0.0f)
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
            var i1 = 16777215
            if (recordAnimateColor) i1 = MathHelper.hsvToRTB(f3 / 50.0f, 0.7f, 0.6f) and 16777215
            val posY = (6 - actionBar.get() * 14).toInt()
            var color = if (recordAnimateColor) i1 + (k1 shl 24 and -16777216) else 0xFFFFFF
            color = RGB.of(color).alpha((55 + 200.0 * actionBar.get()).toInt()).rgb()
            val fontRenderer = fontManager.regular
            fontRenderer.drawCenteredString(recordPlaying, 0, posY, color, true)
            GlStateManager.disableBlend()
            GlStateManager.popMatrix()
            mc.mcProfiler.endSection()
        }
        if (title_timer > 0) {
            mc.mcProfiler.startSection("titleAndSubtitle")
            val f4 = title_timer.toFloat() - partialTicks
            var l1 = 255
            if (title_timer > title_fadeOut + title_stay) {
                val f1 = (title_fadeIn + title_stay + title_fadeOut).toFloat() - f4
                l1 = (f1 * 255.0f / title_fadeIn.toFloat()).toInt()
            }
            if (title_timer <= title_fadeOut) {
                l1 = (f4 * 255.0f / title_fadeOut.toFloat()).toInt()
            }
            l1 = MathHelper.clamp_int(l1, 0, 255)
            if (l1 > 8) {
                val titleRenderer = fontManager.title
                val subtitleRenderer = fontManager.subtitle
                GlStateManager.pushMatrix()
                GlStateManager.translate((scaledWidth / 2).toFloat(), (scaledHeight / 2).toFloat(), 0.0f)
                GlStateManager.enableBlend()
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
                GlStateManager.pushMatrix()
                GlStateManager.scale(1.5f, 1.5f, 1.5f)
                val j2 = l1 shl 24 and -16777216
                titleRenderer.drawString(
                    title_title,
                    (-titleRenderer.getStringWidth(title_title) / 2).toFloat(),
                    -10.0f,
                    16777215 or j2,
                    true
                )
                GlStateManager.popMatrix()
                GlStateManager.pushMatrix()
                GlStateManager.scale(1.5f, 1.5f, 1.5f)
                subtitleRenderer.drawString(
                    title_subtitle,
                    (-subtitleRenderer.getStringWidth(title_subtitle) / 2).toFloat(),
                    10.0f,
                    16777215 or j2,
                    true
                )
                GlStateManager.popMatrix()
                GlStateManager.disableBlend()
                GlStateManager.popMatrix()
            }
            mc.mcProfiler.endSection()
        }
        val scoreboard = mc.theWorld.scoreboard
        var scoreobjective: ScoreObjective? = null
        val scoreplayerteam = scoreboard.getPlayersTeam(mc.thePlayer.name)
        if (scoreplayerteam != null) {
            val j1 = scoreplayerteam.chatFormat.colorIndex
            if (j1 >= 0) {
                scoreobjective = scoreboard.getObjectiveInDisplaySlot(3 + j1)
            }
        }
        var scoreobjective1 =
            scoreobjective ?: scoreboard.getObjectiveInDisplaySlot(1)
        if (scoreobjective1 != null) {
            renderScoreboard(scoreobjective1, scaledresolution)
        }
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.disableAlpha()
        GlStateManager.pushMatrix()
        GlStateManager.translate(0.0f, (scaledHeight - 48).toFloat(), 0.0f)
        mc.mcProfiler.startSection("chat")
        chatGUI.drawChat(updateCounter)
        mc.mcProfiler.endSection()
        GlStateManager.popMatrix()
        scoreobjective1 = scoreboard.getObjectiveInDisplaySlot(0)
        tabList.updatePlayerList(mc.gameSettings.keyBindPlayerList.isKeyDown && (!mc.isIntegratedServerRunning || mc.thePlayer.sendQueue.playerInfoMap.size > 1 || scoreobjective1 != null))
        if (!mc.isIntegratedServerRunning || mc.thePlayer.sendQueue.playerInfoMap.size > 1 || scoreobjective1 != null) tabList.renderPlayerlist(
            scaledWidth,
            scoreboard,
            scoreobjective1
        )

        // ICMM Hotkey Draw
        for (key in controller.hotkeys) {
            key.drawProgress()
        }

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.disableLighting()
        GlStateManager.enableAlpha()

        // ICMM Render Stage
        stage.render()
    }

    protected fun renderTooltip(sr: ScaledResolution, partialTicks: Float) {

        if (mc.renderViewEntity is EntityPlayer) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            mc.textureManager.bindTexture(widgetsTexPath)
            val entityplayer = mc.renderViewEntity as EntityPlayer
            val i = sr.scaledWidth / 2
            val f = zLevel
            zLevel = -90.0f

            val reInitKeystrokesOverlay = hotbarX != (i - 91)

            hotbarX = i - 91
            hotbarY = sr.scaledHeight - 22
            hotbarW = 182
            hotbarH = 22

            this.drawTexturedModalRect(hotbarX, hotbarY, 0, 0, hotbarW, hotbarH)
            this.drawTexturedModalRect(
                i - 91 - 1 + entityplayer.inventory.currentItem * 20,
                sr.scaledHeight - 22 - 1,
                0,
                22,
                24,
                22
            )
            zLevel = f
            GlStateManager.enableRescaleNormal()
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
            RenderHelper.enableGUIStandardItemLighting()
            for (slot in 0..8) {
                val x = sr.scaledWidth / 2 - 90 + slot * 20 + 2
                val y = sr.scaledHeight - 16 - 3
                renderHotbarItem(slot, x, y, partialTicks, entityplayer)
            }
            RenderHelper.disableStandardItemLighting()
            GlStateManager.disableRescaleNormal()
            GlStateManager.disableBlend()

            if (reInitKeystrokesOverlay) {
                initInGameOverlay()
            }

        }
    }

    fun renderHorseJumpBar(p_175186_1_: ScaledResolution, p_175186_2_: Int) {
        mc.mcProfiler.startSection("jumpBar")
        mc.textureManager.bindTexture(icons)
        val f = mc.thePlayer.horseJumpPower
        val short1: Short = 182
        val i = (f * (short1 + 1).toFloat()).toInt()
        val j = p_175186_1_.scaledHeight - 32 + 3
        this.drawTexturedModalRect(p_175186_2_, j, 0, 84, short1.toInt(), 5)
        if (i > 0) {
            this.drawTexturedModalRect(p_175186_2_, j, 0, 89, i, 5)
        }
        mc.mcProfiler.endSection()
    }

    fun renderExpBar(p_175176_1_: ScaledResolution, p_175176_2_: Int) {
        mc.mcProfiler.startSection("expBar")
        mc.textureManager.bindTexture(icons)
        val i = mc.thePlayer.xpBarCap()
        val fontRenderer = fontManager.medium
        if (i > 0) {
            val short1: Short = 182
            val k = (mc.thePlayer.experience * (short1 + 1).toFloat()).toInt()
            val j = p_175176_1_.scaledHeight - 32 + 3
            this.drawTexturedModalRect(p_175176_2_, j, 0, 64, short1.toInt(), 5)
            if (k > 0) {
                this.drawTexturedModalRect(p_175176_2_, j, 0, 69, k, 5)
            }
        }
        mc.mcProfiler.endSection()
        if (mc.thePlayer.experienceLevel > 0) {
            mc.mcProfiler.startSection("expLevel")
            var j1 = 8453920
            if (Config.isCustomColors()) {
                j1 = CustomColors.getExpBarTextColor(j1)
            }
            val s = "" + mc.thePlayer.experienceLevel
            val i1 = (p_175176_1_.scaledWidth - fontRenderer.getStringWidth(s)) / 2
            val l = p_175176_1_.scaledHeight - 31 - 4
            fontRenderer.drawString(s, i1 + 1, l, 0)
            fontRenderer.drawString(s, i1 - 1, l, 0)
            fontRenderer.drawString(s, i1, l + 1, 0)
            fontRenderer.drawString(s, i1, l - 1, 0)
            fontRenderer.drawString(s, i1, l, j1)
            mc.mcProfiler.endSection()
        }
    }

    fun drawSelectedItemName(resolution: ScaledResolution) {
        mc.mcProfiler.startSection("selectedItemName")
        val fontRenderer = fontManager.regular
        if (remainingHighlightTicks > 0 && highlightingItemStack != null) {
            var s = highlightingItemStack!!.displayName
            if (highlightingItemStack!!.hasDisplayName()) {
                s = EnumChatFormatting.ITALIC.toString() + s
            }
            val i = (resolution.scaledWidth - fontRenderer.getStringWidth(s)) / 2
            var j = resolution.scaledHeight - 59
            if (!mc.playerController.shouldDrawHUD()) {
                j += 14
            }
            var k = (remainingHighlightTicks.toFloat() * 256.0f / 10.0f).toInt()
            if (k > 255) {
                k = 255
            }
            if (k > 0) {
                GlStateManager.pushMatrix()
                GlStateManager.enableBlend()
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
                fontRenderer.drawStringWithShadow(s, i.toFloat(), j.toFloat(), 16777215 + (k shl 24))
                GlStateManager.disableBlend()
                GlStateManager.popMatrix()
            }
        }
        mc.mcProfiler.endSection()
    }

    fun renderDemo(p_175185_1_: ScaledResolution) {
        mc.mcProfiler.startSection("demo")
        val fontRenderer = fontManager.regular
        var s: String? = ""
        s = if (mc.theWorld.totalWorldTime >= 120500L) {
            I18n.format("demo.demoExpired")
        } else {
            I18n.format(
                "demo.remainingTime",
                StringUtils.ticksToElapsedTime((120500L - mc.theWorld.totalWorldTime).toInt())
            )
        }
        val i = fontRenderer.getStringWidth(s)
        fontRenderer.drawStringWithShadow(s, (p_175185_1_.scaledWidth - i - 10).toFloat(), 5.0f, 16777215)
        mc.mcProfiler.endSection()
    }

    protected fun showCrosshair(): Boolean {
        return if (mc.gameSettings.showDebugInfo && !mc.thePlayer.hasReducedDebug() && !mc.gameSettings.reducedDebugInfo) {
            false
        } else if (mc.playerController.isSpectator) {
            if (mc.pointedEntity != null) {
                true
            } else {
                if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    val blockpos = mc.objectMouseOver.blockPos
                    return mc.theWorld.getTileEntity(blockpos) is IInventory
                }
                false
            }
        } else {
            true
        }
    }

    fun renderStreamIndicator(resolution: ScaledResolution) {
        streamIndicator.render(resolution.scaledWidth - 10, 10)
    }

    private fun renderScoreboard(objective: ScoreObjective, resolution: ScaledResolution) {
        val fontRegular = fontManager.regular
        val fontMedium = fontManager.regular
        val scoreboard = objective.scoreboard
        val sortedScores = scoreboard.getSortedScores(objective)
        val displayableScores: ArrayList<Score> = sortedScores.stream()
            .filter { score: Score ->
                score.playerName != null && !score.playerName.startsWith("#")
            }.collect(
                Collectors.toCollection { Lists.newArrayList<Score>() }
            )
        val trimmedScores =
            if (displayableScores.size > 15) Lists.newArrayList(
                Iterables.skip(
                    displayableScores,
                    sortedScores.size - 15
                )
            ) else displayableScores
        var i = (fontMedium.getStringWidth(objective.displayName) * 1.2).toInt()
        for (score in trimmedScores) {
            val scoreplayerteam = scoreboard.getPlayersTeam((score as Score).playerName)
            val s = ScorePlayerTeam.formatPlayerName(
                scoreplayerteam,
                score.playerName
            ) + ": " + EnumChatFormatting.RED + score.scorePoints
            i = Math.max(i, fontRegular.getStringWidth(s))
        }
        val j1 = trimmedScores.size * fontRegular.height
        val k1 = resolution.scaledHeight / 2 + j1 / 3
        val b0: Byte = 3
        val left = resolution.scaledWidth - i - 3
        var k = 0
        val lightColor = RGB.of(GreyToneColor.GREY).alpha(0.5f).rgb()
        val darkColor = RGB.of(GreyToneColor.DARK_GREY).alpha(0.5f).rgb()
        val shouldRenderScores = shouldRenderScores(trimmedScores)
        for (score in trimmedScores) {
            ++k
            val scoreplayerteam1 = scoreboard.getPlayersTeam(score.playerName)
            val s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score.playerName)
            val s2 = EnumChatFormatting.RED.toString() + score.scorePoints
            val top = k1 - k * fontRegular.height
            val right = resolution.scaledWidth - b0 + 2
            val background = scoreboardBackground.getKey().get()
            val title = scoreboardTitle.getKey().get()
            if (background) drawRect(left - 2, top - 1, right + 2, top + fontRegular.height - 1, lightColor)
            if (shouldRenderScores) fontRegular.drawString(
                s2,
                right - fontRegular.getStringWidth(s2).toFloat(),
                top.toFloat(),
                0xFFFFFF,
                true
            )
            fontRegular.drawString(s1, left.toFloat(), top.toFloat(), 0xFFFFFF, true)
            if (k == trimmedScores.size && title) {
                if (background) {
                    drawRect(left - 2, top - fontMedium.height - 1, right + 2, top - 1, darkColor)
                }
                val s3 = objective.displayName
                val x = left + i / 2f - fontMedium.getStringWidth(s3) / 2f
                fontMedium.drawString(s3, x, top - fontMedium.height + 1.toFloat(), 0xFFFFFF, true)
            }
        }
    }

    private fun shouldRenderScores(scores: ArrayList<Score>): Boolean {
        val mode = scoreboardScores.key.get()
        return if (mode == 0 || scores.size <= 1) false else if (mode == 1) true else {
            var last = scores[0].scorePoints
            var increase: Boolean? = null
            for (i in 1 until scores.size) {
                val value = scores[i].scorePoints
                if (increase == null) {
                    increase = if (value == last + 1) true else if (value == last - 1) false else return true
                    last = value
                } else {
                    last = if (increase && value != last + 1 || !increase && value != last - 1) {
                        return true
                    } else value
                }
            }
            false
        }
    }

    private fun renderPlayerStats(resolution: ScaledResolution) {
        if (mc.renderViewEntity is EntityPlayer) {
            val entityplayer = mc.renderViewEntity as EntityPlayer
            val i = MathHelper.ceiling_float_int(entityplayer.health)
            val flag =
                healthUpdateCounter > updateCounter.toLong() && (healthUpdateCounter - updateCounter.toLong()) / 3L % 2L == 1L
            if (i < playerHealth && entityplayer.hurtResistantTime > 0) {
                lastSystemTime = Minecraft.getSystemTime()
                healthUpdateCounter = updateCounter + 20.toLong()
            } else if (i > playerHealth && entityplayer.hurtResistantTime > 0) {
                lastSystemTime = Minecraft.getSystemTime()
                healthUpdateCounter = updateCounter + 10.toLong()
            }
            if (Minecraft.getSystemTime() - lastSystemTime > 1000L) {
                playerHealth = i
                lastPlayerHealth = i
                lastSystemTime = Minecraft.getSystemTime()
            }
            playerHealth = i
            val j = lastPlayerHealth
            rand.setSeed(updateCounter * 312871.toLong())
            val flag1 = false
            val foodstats = entityplayer.foodStats
            val k = foodstats.foodLevel
            val l = foodstats.prevFoodLevel
            val iattributeinstance =
                entityplayer.getEntityAttribute(SharedMonsterAttributes.maxHealth)
            val i1 = resolution.scaledWidth / 2 - 91
            val j1 = resolution.scaledWidth / 2 + 91
            val k1 = resolution.scaledHeight - 39
            val f = iattributeinstance.attributeValue.toFloat()
            val f1 = entityplayer.absorptionAmount
            val l1 = MathHelper.ceiling_float_int((f + f1) / 2.0f / 10.0f)
            val i2 = Math.max(10 - (l1 - 2), 3)
            val j2 = k1 - (l1 - 1) * i2 - 10
            var f2 = f1
            val k2 = entityplayer.totalArmorValue
            var l2 = -1
            if (entityplayer.isPotionActive(Potion.regeneration)) {
                l2 = updateCounter % MathHelper.ceiling_float_int(f + 5.0f)
            }
            mc.mcProfiler.startSection("armor")
            for (i3 in 0..9) {
                if (k2 > 0) {
                    val j3 = i1 + i3 * 8
                    if (i3 * 2 + 1 < k2) {
                        this.drawTexturedModalRect(j3, j2, 34, 9, 9, 9)
                    }
                    if (i3 * 2 + 1 == k2) {
                        this.drawTexturedModalRect(j3, j2, 25, 9, 9, 9)
                    }
                    if (i3 * 2 + 1 > k2) {
                        this.drawTexturedModalRect(j3, j2, 16, 9, 9, 9)
                    }
                }
            }
            mc.mcProfiler.endStartSection("health")
            for (j5 in MathHelper.ceiling_float_int((f + f1) / 2.0f) - 1 downTo 0) {
                var k5 = 16
                if (entityplayer.isPotionActive(Potion.poison)) {
                    k5 += 36
                } else if (entityplayer.isPotionActive(Potion.wither)) {
                    k5 += 72
                }
                var b0: Byte = 0
                if (flag) {
                    b0 = 1
                }
                val k3 = MathHelper.ceiling_float_int((j5 + 1).toFloat() / 10.0f) - 1
                val l3 = i1 + j5 % 10 * 8
                var i4 = k1 - k3 * i2
                if (i <= 4) {
                    i4 += rand.nextInt(2)
                }
                if (j5 == l2) {
                    i4 -= 2
                }
                var b1: Byte = 0
                if (entityplayer.worldObj.worldInfo.isHardcoreModeEnabled) {
                    b1 = 5
                }
                this.drawTexturedModalRect(l3, i4, 16 + b0 * 9, 9 * b1, 9, 9)
                if (flag) {
                    if (j5 * 2 + 1 < j) {
                        this.drawTexturedModalRect(l3, i4, k5 + 54, 9 * b1, 9, 9)
                    }
                    if (j5 * 2 + 1 == j) {
                        this.drawTexturedModalRect(l3, i4, k5 + 63, 9 * b1, 9, 9)
                    }
                }
                if (f2 <= 0.0f) {
                    if (j5 * 2 + 1 < i) {
                        this.drawTexturedModalRect(l3, i4, k5 + 36, 9 * b1, 9, 9)
                    }
                    if (j5 * 2 + 1 == i) {
                        this.drawTexturedModalRect(l3, i4, k5 + 45, 9 * b1, 9, 9)
                    }
                } else {
                    if (f2 == f1 && f1 % 2.0f == 1.0f) {
                        this.drawTexturedModalRect(l3, i4, k5 + 153, 9 * b1, 9, 9)
                    } else {
                        this.drawTexturedModalRect(l3, i4, k5 + 144, 9 * b1, 9, 9)
                    }
                    f2 -= 2.0f
                }
            }
            val entity = entityplayer.ridingEntity
            if (entity == null) {
                mc.mcProfiler.endStartSection("food")
                for (l5 in 0..9) {
                    var i8 = k1
                    var j6 = 16
                    var b4: Byte = 0
                    if (entityplayer.isPotionActive(Potion.hunger)) {
                        j6 += 36
                        b4 = 13
                    }
                    if (entityplayer.foodStats
                            .saturationLevel <= 0.0f && updateCounter % (k * 3 + 1) == 0
                    ) {
                        i8 = k1 + (rand.nextInt(3) - 1)
                    }
                    if (flag1) {
                        b4 = 1
                    }
                    val k7 = j1 - l5 * 8 - 9
                    this.drawTexturedModalRect(k7, i8, 16 + b4 * 9, 27, 9, 9)
                    if (flag1) {
                        if (l5 * 2 + 1 < l) {
                            this.drawTexturedModalRect(k7, i8, j6 + 54, 27, 9, 9)
                        }
                        if (l5 * 2 + 1 == l) {
                            this.drawTexturedModalRect(k7, i8, j6 + 63, 27, 9, 9)
                        }
                    }
                    if (l5 * 2 + 1 < k) {
                        this.drawTexturedModalRect(k7, i8, j6 + 36, 27, 9, 9)
                    }
                    if (l5 * 2 + 1 == k) {
                        this.drawTexturedModalRect(k7, i8, j6 + 45, 27, 9, 9)
                    }
                }
            } else if (entity is EntityLivingBase) {
                mc.mcProfiler.endStartSection("mountHealth")
                val entitylivingbase = entity
                val l7 = Math.ceil(entitylivingbase.health.toDouble()).toInt()
                val f3 = entitylivingbase.maxHealth
                var l6 = (f3 + 0.5f).toInt() / 2
                if (l6 > 30) {
                    l6 = 30
                }
                var j7 = k1
                var j4 = 0
                while (l6 > 0) {
                    val k4 = Math.min(l6, 10)
                    l6 -= k4
                    for (l4 in 0 until k4) {
                        val b2: Byte = 52
                        var b3: Byte = 0
                        if (flag1) {
                            b3 = 1
                        }
                        val i5 = j1 - l4 * 8 - 9
                        this.drawTexturedModalRect(i5, j7, b2 + b3 * 9, 9, 9, 9)
                        if (l4 * 2 + 1 + j4 < l7) {
                            this.drawTexturedModalRect(i5, j7, b2 + 36, 9, 9, 9)
                        }
                        if (l4 * 2 + 1 + j4 == l7) {
                            this.drawTexturedModalRect(i5, j7, b2 + 45, 9, 9, 9)
                        }
                    }
                    j7 -= 10
                    j4 += 20
                }
            }
            mc.mcProfiler.endStartSection("air")
            if (entityplayer.isInsideOfMaterial(Material.water)) {
                val i6 = mc.thePlayer.air
                val j8 = MathHelper.ceiling_double_int((i6 - 2).toDouble() * 10.0 / 300.0)
                val k6 = MathHelper.ceiling_double_int(i6.toDouble() * 10.0 / 300.0) - j8
                for (i7 in 0 until j8 + k6) {
                    if (i7 < j8) {
                        this.drawTexturedModalRect(j1 - i7 * 8 - 9, j2, 16, 18, 9, 9)
                    } else {
                        this.drawTexturedModalRect(j1 - i7 * 8 - 9, j2, 25, 18, 9, 9)
                    }
                }
            }
            mc.mcProfiler.endSection()
        }
    }

    /**
     * Renders dragon's (boss) health on the HUD
     */
    private fun renderBossHealth() {
        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
            --BossStatus.statusBarTime
            val fontrenderer = fontManager.regular
            val scaledresolution = ScaledResolution(mc)
            val i = scaledresolution.scaledWidth
            val short1: Short = 182
            val j = i / 2 - short1 / 2
            val k = (BossStatus.healthScale * (short1 + 1).toFloat()).toInt()
            val b0: Byte = 12
            this.drawTexturedModalRect(j, b0.toInt(), 0, 74, short1.toInt(), 5)
            this.drawTexturedModalRect(j, b0.toInt(), 0, 74, short1.toInt(), 5)
            if (k > 0) {
                this.drawTexturedModalRect(j, b0.toInt(), 0, 79, k, 5)
            }
            val s = BossStatus.bossName
            var l = 16777215
            if (Config.isCustomColors()) {
                l = CustomColors.getBossTextColor(l)
            }
            fontrenderer.drawStringWithShadow(
                s,
                (i / 2 - fontrenderer.getStringWidth(s) / 2).toFloat(),
                (b0 - 10).toFloat(),
                l
            )
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            mc.textureManager.bindTexture(icons)
        }
    }

    private fun renderPumpkinOverlay(p_180476_1_: ScaledResolution) {
        GlStateManager.disableDepth()
        GlStateManager.depthMask(false)
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.disableAlpha()
        mc.textureManager.bindTexture(pumpkinBlurTexPath)
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
        worldrenderer.pos(0.0, p_180476_1_.scaledHeight.toDouble(), -90.0).tex(0.0, 1.0).endVertex()
        worldrenderer.pos(p_180476_1_.scaledWidth.toDouble(), p_180476_1_.scaledHeight.toDouble(), -90.0)
            .tex(1.0, 1.0).endVertex()
        worldrenderer.pos(p_180476_1_.scaledWidth.toDouble(), 0.0, -90.0).tex(1.0, 0.0).endVertex()
        worldrenderer.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex()
        tessellator.draw()
        GlStateManager.depthMask(true)
        GlStateManager.enableDepth()
        GlStateManager.enableAlpha()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    }

    /**
     * Renders a Vignette arount the entire screen that changes with light level.
     */
    private fun renderVignette(p_180480_1_: Float, p_180480_2_: ScaledResolution) {
        var p_180480_1_ = p_180480_1_
        if (!Config.isVignetteEnabled()) {
            GlStateManager.enableDepth()
        } else {
            p_180480_1_ = 1.0f - p_180480_1_
            p_180480_1_ = MathHelper.clamp_float(p_180480_1_, 0.0f, 1.0f)
            val worldborder = mc.theWorld.worldBorder
            var f = worldborder.getClosestDistance(mc.thePlayer).toFloat()
            val d0 = Math.min(
                worldborder.resizeSpeed * worldborder.warningTime.toDouble() * 1000.0,
                Math.abs(worldborder.targetSize - worldborder.diameter)
            )
            val d1 = Math.max(worldborder.warningDistance.toDouble(), d0)
            f = if (f.toDouble() < d1) {
                1.0f - (f.toDouble() / d1).toFloat()
            } else {
                0.0f
            }
            prevVignetteBrightness =
                (prevVignetteBrightness + (p_180480_1_ - prevVignetteBrightness) * 0.01).toFloat()
            GlStateManager.disableDepth()
            GlStateManager.depthMask(false)
            GlStateManager.tryBlendFuncSeparate(0, 769, 1, 0)
            if (f > 0.0f) {
                GlStateManager.color(0.0f, f, f, 1.0f)
            } else {
                GlStateManager.color(
                    prevVignetteBrightness,
                    prevVignetteBrightness,
                    prevVignetteBrightness,
                    1.0f
                )
            }
            mc.textureManager.bindTexture(vignetteTexPath)
            val tessellator = Tessellator.getInstance()
            val worldrenderer = tessellator.worldRenderer
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
            worldrenderer.pos(0.0, p_180480_2_.scaledHeight.toDouble(), -90.0).tex(0.0, 1.0).endVertex()
            worldrenderer.pos(p_180480_2_.scaledWidth.toDouble(), p_180480_2_.scaledHeight.toDouble(), -90.0)
                .tex(1.0, 1.0).endVertex()
            worldrenderer.pos(p_180480_2_.scaledWidth.toDouble(), 0.0, -90.0).tex(1.0, 0.0).endVertex()
            worldrenderer.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex()
            tessellator.draw()
            GlStateManager.depthMask(true)
            GlStateManager.enableDepth()
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        }
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
    }

    private fun func_180474_b(param: Float, resolution: ScaledResolution) {
        var param = param
        if (param < 1.0f) {
            param = param * param
            param = param * param
            param = param * 0.8f + 0.2f
        }
        GlStateManager.disableAlpha()
        GlStateManager.disableDepth()
        GlStateManager.depthMask(false)
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.color(1.0f, 1.0f, 1.0f, param)
        mc.textureManager.bindTexture(TextureMap.locationBlocksTexture)
        val textureatlassprite =
            mc.blockRendererDispatcher.blockModelShapes.getTexture(Blocks.portal.defaultState)
        val f = textureatlassprite.minU
        val f1 = textureatlassprite.minV
        val f2 = textureatlassprite.maxU
        val f3 = textureatlassprite.maxV
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
        worldrenderer.pos(0.0, resolution.scaledHeight.toDouble(), -90.0).tex(f.toDouble(), f3.toDouble())
            .endVertex()
        worldrenderer.pos(resolution.scaledWidth.toDouble(), resolution.scaledHeight.toDouble(), -90.0)
            .tex(f2.toDouble(), f3.toDouble()).endVertex()
        worldrenderer.pos(resolution.scaledWidth.toDouble(), 0.0, -90.0).tex(f2.toDouble(), f1.toDouble())
            .endVertex()
        worldrenderer.pos(0.0, 0.0, -90.0).tex(f.toDouble(), f1.toDouble()).endVertex()
        tessellator.draw()
        GlStateManager.depthMask(true)
        GlStateManager.enableDepth()
        GlStateManager.enableAlpha()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    }

    private fun renderHotbarItem(
        index: Int,
        xPos: Int,
        yPos: Int,
        partialTicks: Float,
        p_175184_5_: EntityPlayer
    ) {
        val itemstack = p_175184_5_.inventory.mainInventory[index]
        if (itemstack != null) {
            val f = itemstack.animationsToGo.toFloat() - partialTicks
            if (f > 0.0f) {
                GlStateManager.pushMatrix()
                val f1 = 1.0f + f / 5.0f
                GlStateManager.translate((xPos + 8).toFloat(), (yPos + 12).toFloat(), 0.0f)
                GlStateManager.scale(1.0f / f1, (f1 + 1.0f) / 2.0f, 1.0f)
                GlStateManager.translate((-(xPos + 8)).toFloat(), (-(yPos + 12)).toFloat(), 0.0f)
            }
            itemRenderer.renderItemAndEffectIntoGUI(itemstack, xPos, yPos)
            if (f > 0.0f) {
                GlStateManager.popMatrix()
            }
            itemRenderer.renderItemOverlays(itemstack, xPos, yPos)
        }
    }

    /**
     * The update tick for the ingame UI
     */
    fun updateTick() {
        if (recordPlayingUpFor > 0) {
            --recordPlayingUpFor
        }
        if (title_timer > 0) {
            --title_timer
            if (title_timer <= 0) {
                title_title = ""
                title_subtitle = ""
            }
        }
        ++updateCounter
        streamIndicator.func_152439_a()
        if (mc.thePlayer != null) {
            val itemstack = mc.thePlayer.inventory.getCurrentItem()
            if (itemstack == null) {
                remainingHighlightTicks = 0
            } else if (highlightingItemStack != null && itemstack.item === highlightingItemStack!!.item && ItemStack.areItemStackTagsEqual(
                    itemstack,
                    highlightingItemStack
                ) && (itemstack.isItemStackDamageable || itemstack.metadata == highlightingItemStack!!.metadata)
            ) {
                if (remainingHighlightTicks > 0) {
                    --remainingHighlightTicks
                }
            } else {
                remainingHighlightTicks = 40
            }
            highlightingItemStack = itemstack
        }
    }

    fun setRecordPlayingMessage(p_73833_1_: String?) {
        this.setRecordPlaying(I18n.format("record.nowPlaying", p_73833_1_), true)
    }

    fun setRecordPlaying(text: String, animateColor: Boolean) {
        recordPlaying = text
        recordPlayingUpFor = 60
        recordAnimateColor = animateColor
        actionBarDisplayed = true
    }

    fun displayTitle(
        title: String?,
        subtitle: String?,
        fadeIn: Int,
        stay: Int,
        fadeOut: Int
    ) {
        if (title == null && subtitle == null && fadeIn < 0 && stay < 0 && fadeOut < 0) {
            title_title = ""
            title_subtitle = ""
            title_timer = 0
        } else if (title != null) {
            title_title = title
            title_timer = title_fadeIn + title_stay + title_fadeOut
        } else if (subtitle != null) {
            title_subtitle = subtitle
        } else {
            if (fadeIn >= 0) {
                title_fadeIn = fadeIn
            }
            if (stay >= 0) {
                title_stay = stay
            }
            if (fadeOut >= 0) {
                title_fadeOut = fadeOut
            }
            if (title_timer > 0) {
                title_timer = title_fadeIn + title_stay + title_fadeOut
            }
        }
    }

    fun setRecordPlaying(component: IChatComponent, animateColor: Boolean) {
        this.setRecordPlaying(component.unformattedText, animateColor)
    }

    fun func_181029_i() {
        tabList.func_181030_a()
    }

    /**
     * An operator function that allows adding widgets to the stage. After providing the widget,
     * an id for it must be specified with the infix function [WidgetIdBuilder.id].
     */
    operator fun <W : Widget<W>> W.unaryPlus(): WidgetIdBuilder<W> {
        return WidgetIdBuilder(stage, widget = this)
    }

    /**
     * Tries to get a widget and additionally cast it to the specified type. This will return
     * null if the widget was not found or cannot be cast.
     */
    @Suppress("UNCHECKED_CAST")
    fun <W : Widget<W>> getWidget(identifier: String): W? = stage[identifier] as? W

    fun initInGameOverlay() {
        stage.clear()
        
        for(keyStroke in KeystrokesManager.keystrokes) {
            stage.add(Pair("keystroke-${keyStroke.keyDesc}", keyStroke.textField))
        }

        ToggleSneakMod.updateOverlayText()
        stage.add(Pair("togglesneak-text", TextField().apply {
            x = ToggleSneakMod.posX
            y = ToggleSneakMod.posY
            width = ToggleSneakMod.width
            staticText = ToggleSneakMod.overlayText
            color = ToggleSneakMod.overlayTextColor
            backgroundColor = ToggleSneakMod.overlayBackgroundColor
            fontRenderer = fontManager.defaultFont.fontRenderer(size = ToggleSneakMod.overlaySize, useScale = false)
            textAlignHorizontal = Alignment.CENTER
        }))

    }

    companion object {
        private val vignetteTexPath =
            ResourceLocation("textures/misc/vignette.png")
        private val widgetsTexPath =
            ResourceLocation("textures/gui/widgets.png")
        private val pumpkinBlurTexPath =
            ResourceLocation("textures/misc/pumpkinblur.png")
        private const val __OBFID = "CL_00000661"
        var canDisplayActionBar = true
    }

    init {
        itemRenderer = mc.renderItem
        overlayDebug = GuiOverlayDebug(mc)
        spectatorGui = GuiSpectator(mc)
        chatGUI = GuiNewChat(mc)
        streamIndicator = GuiStreamIndicator(mc)
        tabList = GuiPlayerTabOverlay(mc, this)
        func_175177_a()
        splashScreen.update()
    }
}