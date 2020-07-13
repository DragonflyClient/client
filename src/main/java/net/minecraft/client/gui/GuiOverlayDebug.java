package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.inceptioncloud.dragonfly.Dragonfly;
import net.inceptioncloud.dragonfly.versioning.DragonflyVersion;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import optifine.Reflector;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.Map.Entry;

public class GuiOverlayDebug extends Gui
{
    private final Minecraft mc;
    private final FontRenderer fontRenderer;
    private static final String __OBFID = "CL_00001956";

    public GuiOverlayDebug(Minecraft mc)
    {
        this.mc = mc;
        this.fontRenderer = mc.fontRendererObj;
    }

    public void renderDebugInfo(ScaledResolution scaledResolutionIn)
    {
        this.mc.mcProfiler.startSection("debug");
        GlStateManager.pushMatrix();
        this.renderDebugInfoLeft();
        this.renderDebugInfoRight(scaledResolutionIn);
        GlStateManager.popMatrix();
        this.mc.mcProfiler.endSection();
    }

    private boolean isReducedDebug()
    {
        return this.mc.thePlayer.hasReducedDebug() || this.mc.gameSettings.reducedDebugInfo;
    }

    protected void renderDebugInfoLeft()
    {
        List<String> list = this.call();

        for (int i = 0; i < list.size(); ++i)
        {
            String s = list.get(i);

            if (!Strings.isNullOrEmpty(s))
            {
                int j = this.fontRenderer.FONT_HEIGHT;
                int k = this.fontRenderer.getStringWidth(s);
                int l = 2 + j * i;
                drawRect(1, l - 1, 2 + k + 1, l + j - 1, -1873784752);
                this.fontRenderer.drawString(s, 2, l, 14737632);
            }
        }
    }

    protected void renderDebugInfoRight(ScaledResolution resolution)
    {
        List<String> list = this.getDebugInfoRight();

        for (int i = 0; i < list.size(); ++i)
        {
            String s = list.get(i);

            if (!Strings.isNullOrEmpty(s))
            {
                int j = this.fontRenderer.FONT_HEIGHT;
                int k = this.fontRenderer.getStringWidth(s);
                int l = resolution.getScaledWidth() - 2 - k;
                int i1 = 2 + j * i;
                drawRect(l - 1, i1 - 1, l + k + 1, i1 + j - 1, -1873784752);
                this.fontRenderer.drawString(s, l, i1, 14737632);
            }
        }
    }

    protected List<String> call()
    {
        BlockPos blockpos = new BlockPos(this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ);

        if (this.isReducedDebug())
        {
            return Lists.newArrayList("Inception Cloud Dragonfly " + DragonflyVersion.getString() + " for MC-1.8.8",
                    this.mc.debug,
                    Dragonfly.getLastTPS() + " mod tps (ideally 200)",
                    this.mc.renderGlobal.getDebugInfoRenders(),
                    this.mc.renderGlobal.getDebugInfoEntities(),
                    "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.theWorld.getDebugLoadedEntities(),
                    this.mc.theWorld.getProviderName(),
                    "",
                    String.format("Chunk-relative: %d %d %d", blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15)
            );
        }
        else
        {
            Entity entity = this.mc.getRenderViewEntity();
            EnumFacing enumfacing = entity.getHorizontalFacing();
            String s = "Invalid";

            switch (GuiOverlayDebug.GuiOverlayDebug$1.field_178907_a[enumfacing.ordinal()])
            {
                case 1:
                    s = "Towards negative Z";
                    break;

                case 2:
                    s = "Towards positive Z";
                    break;

                case 3:
                    s = "Towards negative X";
                    break;

                case 4:
                    s = "Towards positive X";
            }

            ArrayList<String> arraylist = Lists.newArrayList("Inception Cloud Dragonfly " + DragonflyVersion.getString() + " for MC-1.8.8",
                    this.mc.debug,
                    Dragonfly.getLastTPS() + " mod tps (ideally 200)",
                    this.mc.renderGlobal.getDebugInfoRenders(),
                    this.mc.renderGlobal.getDebugInfoEntities(),
                    "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.theWorld.getDebugLoadedEntities(),
                    this.mc.theWorld.getProviderName(),
                    "",
                    String.format("XYZ: %.3f / %.5f / %.3f",
                            this.mc.getRenderViewEntity().posX,
                            this.mc.getRenderViewEntity().getEntityBoundingBox().minY,
                            this.mc.getRenderViewEntity().posZ
                    ),
                    String.format("Block: %d %d %d", blockpos.getX(), blockpos.getY(), blockpos.getZ()),
                    String.format("Chunk: %d %d %d in %d %d %d",
                            blockpos.getX() & 15,
                            blockpos.getY() & 15,
                            blockpos.getZ() & 15,
                            blockpos.getX() >> 4,
                            blockpos.getY() >> 4,
                            blockpos.getZ() >> 4
                    ),
                    String.format("Facing: %s (%s) (%.1f / %.1f)",
                            enumfacing,
                            s,
                            MathHelper.wrapAngleTo180_float(entity.rotationYaw),
                            MathHelper.wrapAngleTo180_float(entity.rotationPitch)
                    )
            );

            if (this.mc.theWorld != null && this.mc.theWorld.isBlockLoaded(blockpos))
            {
                Chunk chunk = this.mc.theWorld.getChunkFromBlockCoords(blockpos);
                arraylist.add("Biome: " + chunk.getBiome(blockpos, this.mc.theWorld.getWorldChunkManager()).biomeName);
                arraylist.add("Light: " + chunk.getLightSubtracted(blockpos, 0) + " (" + chunk.getLightFor(EnumSkyBlock.SKY, blockpos) + " sky, " + chunk.getLightFor(EnumSkyBlock.BLOCK, blockpos) + " block)");
                DifficultyInstance difficultyinstance = this.mc.theWorld.getDifficultyForLocation(blockpos);

                if (this.mc.isIntegratedServerRunning() && this.mc.getIntegratedServer() != null)
                {
                    EntityPlayerMP entityplayermp = this.mc.getIntegratedServer().getConfigurationManager().getPlayerByUUID(this.mc.thePlayer.getUniqueID());

                    if (entityplayermp != null)
                    {
                        difficultyinstance = entityplayermp.worldObj.getDifficultyForLocation(new BlockPos(entityplayermp));
                    }
                }

                arraylist.add(String.format("Local Difficulty: %.2f (Day %d)", difficultyinstance.getAdditionalDifficulty(), this.mc.theWorld.getWorldTime() / 24000L));
            }

            if (this.mc.entityRenderer != null && this.mc.entityRenderer.isShaderActive())
            {
                arraylist.add("Shader: " + this.mc.entityRenderer.getShaderGroup().getShaderGroupName());
            }

            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.objectMouseOver.getBlockPos() != null)
            {
                BlockPos blockpos1 = this.mc.objectMouseOver.getBlockPos();
                arraylist.add(String.format("Looking at: %d %d %d", blockpos1.getX(), blockpos1.getY(), blockpos1.getZ()));
            }

            return arraylist;
        }
    }

    protected List<String> getDebugInfoRight()
    {
        long i = Runtime.getRuntime().maxMemory();
        long j = Runtime.getRuntime().totalMemory();
        long k = Runtime.getRuntime().freeMemory();
        long l = j - k;
        ArrayList<String> arraylist = Lists.newArrayList(String.format("Java: %s %dbit", System.getProperty("java.version"), this.mc.isJava64bit() ? 64 : 32), String.format("Mem: % 2d%% %03d/%03dMB", l * 100L / i, bytesToMb(l), bytesToMb(i)), String.format("Allocated: % 2d%% %03dMB", j * 100L / i, bytesToMb(j)), "", String.format("CPU: %s", OpenGlHelper.func_183029_j()), "", String.format("Display: %dx%d (%s)", Display.getWidth(), Display.getHeight(), GL11.glGetString(GL11.GL_VENDOR)), GL11.glGetString(GL11.GL_RENDERER), GL11.glGetString(GL11.GL_VERSION));

        if (Reflector.FMLCommonHandler_getBrandings.exists())
        {
            Object object = Reflector.call(Reflector.FMLCommonHandler_instance);
            arraylist.add("");
            arraylist.addAll(( Collection<String> ) Objects.requireNonNull(Reflector.call(object, Reflector.FMLCommonHandler_getBrandings, Boolean.FALSE)));
        }

        if (!this.isReducedDebug()) {
            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.objectMouseOver.getBlockPos() != null) {
                BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
                IBlockState iblockstate = this.mc.theWorld.getBlockState(blockpos);

                if (this.mc.theWorld.getWorldType() != WorldType.DEBUG_WORLD) {
                    iblockstate = iblockstate.getBlock().getActualState(iblockstate, this.mc.theWorld, blockpos);
                }

                arraylist.add("");
                arraylist.add(String.valueOf(Block.blockRegistry.getNameForObject(iblockstate.getBlock())));
                Entry<IProperty, Comparable> entry;
                String s;

                for (Iterator<Entry<IProperty, Comparable>> iterator = iblockstate.getProperties().entrySet().iterator() ; iterator.hasNext() ; arraylist.add(( entry.getKey() ).getName() + ": " + s)) {
                    entry = iterator.next();
                    s = entry.getValue().toString();

                    if (entry.getValue() == Boolean.TRUE) {
                        s = EnumChatFormatting.GREEN + s;
                    } else if (entry.getValue() == Boolean.FALSE) {
                        s = EnumChatFormatting.RED + s;
                    }
                }
            }

        }

        return arraylist;
    }

    private void func_181554_e()
    {
        GlStateManager.disableDepth();
        FrameTimer frametimer = this.mc.getFrameTimer();
        int i = frametimer.func_181749_a();
        int j = frametimer.func_181750_b();
        long[] along = frametimer.func_181746_c();
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int k = i;
        int l = 0;
        drawRect(0, scaledresolution.getScaledHeight() - 60, 240, scaledresolution.getScaledHeight(), -1873784752);

        while (k != j)
        {
            int i1 = frametimer.func_181748_a(along[k], 30);
            int j1 = this.func_181552_c(MathHelper.clamp_int(i1, 0, 60));
            drawVerticalLine(l, scaledresolution.getScaledHeight(), scaledresolution.getScaledHeight() - i1, j1);
            ++l;
            k = frametimer.func_181751_b(k + 1);
        }

        drawRect(1, scaledresolution.getScaledHeight() - 30 + 1, 14, scaledresolution.getScaledHeight() - 30 + 10, -1873784752);
        this.fontRenderer.drawString("60", 2, scaledresolution.getScaledHeight() - 30 + 2, 14737632);
        drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 30, -1);
        drawRect(1, scaledresolution.getScaledHeight() - 60 + 1, 14, scaledresolution.getScaledHeight() - 60 + 10, -1873784752);
        this.fontRenderer.drawString("30", 2, scaledresolution.getScaledHeight() - 60 + 2, 14737632);
        drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 60, -1);
        drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 1, -1);
        drawVerticalLine(0, scaledresolution.getScaledHeight() - 60, scaledresolution.getScaledHeight(), -1);
        drawVerticalLine(239, scaledresolution.getScaledHeight() - 60, scaledresolution.getScaledHeight(), -1);

        if (this.mc.gameSettings.limitFramerate <= 120)
        {
            drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 60 + this.mc.gameSettings.limitFramerate / 2, -16711681);
        }

        GlStateManager.enableDepth();
    }

    private int func_181552_c (int param)
    {
        return param < 30 ? this.func_181553_a(-16711936, -256, (float)param / (float) 30) : this.func_181553_a(-256, -65536, (float)( param - 30 ) / (float)( 60 - 30 ));
    }

    private int func_181553_a(int p_181553_1_, int p_181553_2_, float p_181553_3_)
    {
        int i = p_181553_1_ >> 24 & 255;
        int j = p_181553_1_ >> 16 & 255;
        int k = p_181553_1_ >> 8 & 255;
        int l = p_181553_1_ & 255;
        int i1 = p_181553_2_ >> 24 & 255;
        int j1 = p_181553_2_ >> 16 & 255;
        int k1 = p_181553_2_ >> 8 & 255;
        int l1 = p_181553_2_ & 255;
        int i2 = MathHelper.clamp_int((int)((float)i + (float)(i1 - i) * p_181553_3_), 0, 255);
        int j2 = MathHelper.clamp_int((int)((float)j + (float)(j1 - j) * p_181553_3_), 0, 255);
        int k2 = MathHelper.clamp_int((int)((float)k + (float)(k1 - k) * p_181553_3_), 0, 255);
        int l2 = MathHelper.clamp_int((int)((float)l + (float)(l1 - l) * p_181553_3_), 0, 255);
        return i2 << 24 | j2 << 16 | k2 << 8 | l2;
    }

    private static long bytesToMb(long bytes)
    {
        return bytes / 1024L / 1024L;
    }

    static final class GuiOverlayDebug$1
    {
        static final int[] field_178907_a = new int[EnumFacing.values().length];
        private static final String __OBFID = "CL_00001955";

        static
        {
            try
            {
                field_178907_a[EnumFacing.NORTH.ordinal()] = 1;
            }
            catch (NoSuchFieldError ignored)
            {
            }

            try
            {
                field_178907_a[EnumFacing.SOUTH.ordinal()] = 2;
            }
            catch (NoSuchFieldError ignored)
            {
            }

            try
            {
                field_178907_a[EnumFacing.WEST.ordinal()] = 3;
            }
            catch (NoSuchFieldError ignored)
            {
            }

            try
            {
                field_178907_a[EnumFacing.EAST.ordinal()] = 4;
            }
            catch (NoSuchFieldError ignored)
            {
            }
        }
    }
}