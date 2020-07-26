package net.minecraft.client.renderer.chunk

import com.google.common.collect.Sets
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.minecraft.block.*
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexBuffer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import net.minecraft.world.World
import optifine.*
import shadersmod.client.SVertexBuilder
import java.util.*
import java.util.concurrent.locks.ReentrantLock

open class RenderChunk(
    private var world: World?,
    private val renderGlobal: RenderGlobal,
    blockPosIn: BlockPos,
    private val index: Int
) {
    @JvmField
    val lockCompileTask: ReentrantLock = ReentrantLock()

    @JvmField
    val lockCompiledChunk = ReentrantLock()

    private val mutableSet: MutableSet<Any> = Sets.newHashSet<Any>()
    private val modelviewMatrix = GLAllocation.createDirectFloatBuffer(16)
    private val vertexBuffers =
        arrayOfNulls<VertexBuffer>(EnumWorldBlockLayer.values().size)

    @JvmField
    var compiledChunk = CompiledChunk.DUMMY

    @JvmField
    var boundingBox: AxisAlignedBB? = null
    var position: BlockPos? = null
        private set
    private var compileTask: ChunkCompileTaskGenerator? = null
    private var frameIndex = -1
    var isNeedsUpdate = true
        set(needsUpdateIn) {
            field = needsUpdateIn
            if (isNeedsUpdate) {
                if (isWorldPlayerUpdate) {
                    isPlayerUpdate = true
                }
            } else {
                isPlayerUpdate = false
            }
        }
    private val field_181702_p: EnumMap<*, *>? = null
    private val positionOffsets16 = arrayOfNulls<BlockPos>(EnumFacing.VALUES.size)
    private val blockLayersSingle = arrayOfNulls<EnumWorldBlockLayer>(1)
    private val isMipmaps = Config.isMipmaps()
    private val fixBlockLayer = !Reflector.BetterFoliageClient.exists()
    var isPlayerUpdate = false
        private set

    fun setFrameIndex(frameIndexIn: Int): Boolean {
        return if (frameIndex == frameIndexIn) {
            false
        } else {
            frameIndex = frameIndexIn
            true
        }
    }

    fun getVertexBufferByLayer(layer: Int): VertexBuffer? {
        return vertexBuffers[layer]
    }

    fun resortTransparency(x: Float, y: Float, z: Float, generator: ChunkCompileTaskGenerator) {
        val compiledchunk = generator.compiledChunk
        if (compiledchunk.state != null && !compiledchunk.isLayerEmpty(EnumWorldBlockLayer.TRANSLUCENT)) {
            val worldrenderer = generator.regionRenderCacheBuilder.getWorldRendererByLayer(EnumWorldBlockLayer.TRANSLUCENT)
            preRenderBlocks(worldrenderer, position)
            worldrenderer.setVertexState(compiledchunk.state)
            postRenderBlocks(EnumWorldBlockLayer.TRANSLUCENT, x, y, z, worldrenderer, compiledchunk)
        }
    }

    fun rebuildChunk(x: Float, y: Float, z: Float, generator: ChunkCompileTaskGenerator) {
        val compiledchunk = CompiledChunk()
        val blockpos = position
        val blockpos1 = blockpos!!.add(15, 15, 15)
        generator.lock.lock()
        val regionrendercache: RegionRenderCache
        try {
            if (generator.status != ChunkCompileTaskGenerator.Status.COMPILING) {
                return
            }
            if (world == null) {
                return
            }
            regionrendercache = createRegionRenderCache(world, blockpos.add(-1, -1, -1), blockpos1.add(1, 1, 1), 1)
            if (Reflector.MinecraftForgeClient_onRebuildChunk.exists()) {
                Reflector.call(Reflector.MinecraftForgeClient_onRebuildChunk, world, position, regionrendercache)
            }
            generator.compiledChunk = compiledchunk
        } finally {
            generator.lock.unlock()
        }
        val var10 = VisGraph()
        val set = mutableSetOf<TileEntity>()

        if (!regionrendercache.extendedLevelsInChunkCache()) {
            ++renderChunksUpdated
            val aboolean = BooleanArray(ENUM_WORLD_BLOCK_LAYERS.size)
            val blockrendererdispatcher = Minecraft.getMinecraft().blockRendererDispatcher
            val iterator: Iterator<*> = BlockPosM.getAllInBoxMutable(blockpos, blockpos1).iterator()
            val flag2 = Reflector.ForgeBlock_canRenderInLayer.exists()
            val flag3 = Reflector.ForgeHooksClient_setRenderLayer.exists()

            while (iterator.hasNext()) {
                val blockposm = iterator.next() as BlockPosM
                val iblockstate = regionrendercache.getBlockState(blockposm)
                val block = iblockstate.block

                if (block.isOpaqueCube) {
                    var10.func_178606_a(blockposm)
                }

                if (ReflectorForge.blockHasTileEntity(iblockstate)) {
                    val tileEntity = regionrendercache.getTileEntity(BlockPos(blockposm))
                    val tileEntitySpecialRenderer: TileEntitySpecialRenderer<*>? =
                        TileEntityRendererDispatcher.instance.getSpecialRenderer<TileEntity>(tileEntity)
                    if (tileEntity != null && tileEntitySpecialRenderer != null) {
                        compiledchunk.addTileEntity(tileEntity)
                        if (tileEntitySpecialRenderer.func_181055_a()) {
                            set.add(tileEntity)
                        }
                    }
                }

                val enumWorldBlockLayers: Array<EnumWorldBlockLayer?>

                if (flag2) {
                    enumWorldBlockLayers = ENUM_WORLD_BLOCK_LAYERS
                } else {
                    enumWorldBlockLayers = blockLayersSingle
                    enumWorldBlockLayers[0] = block.blockLayer
                }

                for (i in enumWorldBlockLayers.indices) {
                    var enumWorldBlockLayer = enumWorldBlockLayers[i]
                    if (flag2) {
                        val flag4 =
                            Reflector.callBoolean(block, Reflector.ForgeBlock_canRenderInLayer, enumWorldBlockLayer)
                        if (!flag4) {
                            continue
                        }
                    }
                    if (flag3) {
                        Reflector.callVoid(Reflector.ForgeHooksClient_setRenderLayer, enumWorldBlockLayer)
                    }
                    if (fixBlockLayer) {
                        enumWorldBlockLayer = fixBlockLayer(block, enumWorldBlockLayer)
                    }

                    val j = enumWorldBlockLayer!!.ordinal
                    if (block.renderType != -1) {
                        val worldRenderer = generator.regionRenderCacheBuilder.getWorldRendererByLayerId(j)
                        worldRenderer.setBlockLayer(enumWorldBlockLayer)

                        if (!compiledchunk.isLayerStarted(enumWorldBlockLayer)) {
                            compiledchunk.setLayerStarted(enumWorldBlockLayer)
                            preRenderBlocks(worldRenderer, blockpos)

                        }

                        aboolean[j] = aboolean[j] or blockrendererdispatcher.renderBlock(iblockstate, blockposm, regionrendercache, worldRenderer)
                    }
                }
            }

            for (enumworldblocklayer1 in ENUM_WORLD_BLOCK_LAYERS) {
                if (aboolean[enumworldblocklayer1!!.ordinal]) {
                    compiledchunk.setLayerUsed(enumworldblocklayer1)
                }
                if (compiledchunk.isLayerStarted(enumworldblocklayer1)) {
                    if (Config.isShaders()) {
                        SVertexBuilder.calcNormalChunkLayer(generator.regionRenderCacheBuilder.getWorldRendererByLayer(enumworldblocklayer1))
                    }
                    postRenderBlocks(
                        enumworldblocklayer1,
                        x,
                        y,
                        z,
                        generator.regionRenderCacheBuilder.getWorldRendererByLayer(enumworldblocklayer1),
                        compiledchunk
                    )
                }
            }
        }
        compiledchunk.setVisibility(var10.computeVisibility())
        lockCompileTask.lock()
        try {
            val hashset1: HashSet<*> = Sets.newHashSet(set)
            val hashset2: HashSet<*> = Sets.newHashSet<Any>(mutableSet)
            hashset1.removeAll(mutableSet)
            hashset2.removeAll(set)
            mutableSet.clear()
            mutableSet.addAll(set)
            renderGlobal.func_181023_a(hashset2, hashset1)
        } finally {
            lockCompileTask.unlock()
        }
    }

    protected fun finishCompileTask() {
        lockCompileTask.lock()
        try {
            if (compileTask != null && compileTask!!.status != ChunkCompileTaskGenerator.Status.DONE) {
                compileTask!!.finish()
                compileTask = null
            }
        } finally {
            lockCompileTask.unlock()
        }
    }

    fun makeCompileTaskChunk(): ChunkCompileTaskGenerator? {
        lockCompileTask.lock()
        val chunkcompiletaskgenerator: ChunkCompileTaskGenerator?
        try {
            finishCompileTask()
            compileTask = ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.REBUILD_CHUNK)
            chunkcompiletaskgenerator = compileTask
        } finally {
            lockCompileTask.unlock()
        }
        return chunkcompiletaskgenerator
    }

    fun makeCompileTaskTransparency(): ChunkCompileTaskGenerator? {
        lockCompileTask.lock()
        val chunkcompiletaskgenerator1: ChunkCompileTaskGenerator?
        try {
            if (compileTask != null && compileTask!!.status == ChunkCompileTaskGenerator.Status.PENDING) {
                return null
            }
            if (compileTask != null && compileTask!!.status != ChunkCompileTaskGenerator.Status.DONE) {
                compileTask!!.finish()
                compileTask = null
            }
            compileTask = ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY)
            compileTask!!.compiledChunk = compiledChunk
            val chunkcompiletaskgenerator = compileTask
            chunkcompiletaskgenerator1 = chunkcompiletaskgenerator
        } finally {
            lockCompileTask.unlock()
        }
        return chunkcompiletaskgenerator1
    }

    private fun preRenderBlocks(worldRendererIn: WorldRenderer, pos: BlockPos?) {
        worldRendererIn.begin(7, DefaultVertexFormats.BLOCK)
        worldRendererIn.setTranslation(-pos!!.x.toDouble(), -pos.y.toDouble(), -pos.z.toDouble())
    }

    private fun postRenderBlocks(
        layer: EnumWorldBlockLayer?,
        x: Float,
        y: Float,
        z: Float,
        worldRendererIn: WorldRenderer,
        compiledChunkIn: CompiledChunk
    ) {
        if (layer == EnumWorldBlockLayer.TRANSLUCENT && !compiledChunkIn.isLayerEmpty(layer)) {
            worldRendererIn.func_181674_a(x, y, z)
            compiledChunkIn.state = worldRendererIn.func_181672_a()
        }
        worldRendererIn.finishDrawing()
    }

    private fun initModelviewMatrix() {
        GlStateManager.pushMatrix()
        GlStateManager.loadIdentity()
        val f = 1.000001f
        GlStateManager.translate(-8.0f, -8.0f, -8.0f)
        GlStateManager.scale(f, f, f)
        GlStateManager.translate(8.0f, 8.0f, 8.0f)
        GlStateManager.getFloat(2982, modelviewMatrix)
        GlStateManager.popMatrix()
    }

    fun multModelviewMatrix() {
        GlStateManager.multMatrix(modelviewMatrix)
    }

    fun getCompiledChunk(): CompiledChunk {
        return compiledChunk
    }

    fun setCompiledChunk(compiledChunkIn: CompiledChunk) {
        lockCompiledChunk.lock()
        try {
            compiledChunk = compiledChunkIn
        } finally {
            lockCompiledChunk.unlock()
        }
    }

    fun stopCompileTask() {
        finishCompileTask()
        compiledChunk = CompiledChunk.DUMMY
    }

    open fun deleteGlResources() {
        stopCompileTask()
        world = null
        for (i in EnumWorldBlockLayer.values().indices) {
            if (vertexBuffers[i] != null) {
                vertexBuffers[i]!!.deleteGlBuffers()
            }
        }
    }

    fun setPosition(pos: BlockPos) {
        stopCompileTask()
        position = pos
        boundingBox = AxisAlignedBB(pos, pos.add(16, 16, 16))
        initModelviewMatrix()
        for (i in positionOffsets16.indices) {
            positionOffsets16[i] = null
        }
    }

    fun func_181701_a(p_181701_1_: EnumFacing): BlockPos? {
        return getPositionOffset16(p_181701_1_)
    }

    fun getPositionOffset16(p_getPositionOffset16_1_: EnumFacing): BlockPos? {
        val i = p_getPositionOffset16_1_.index
        var blockpos = positionOffsets16[i]
        if (blockpos == null) {
            blockpos = position!!.offset(p_getPositionOffset16_1_, 16)
            positionOffsets16[i] = blockpos
        }
        return blockpos
    }

    private val isWorldPlayerUpdate: Boolean
        private get() = if (world is WorldClient) {
            val worldclient = world as WorldClient?
            worldclient!!.isPlayerUpdate
        } else {
            false
        }

    protected fun createRegionRenderCache(
        p_createRegionRenderCache_1_: World?,
        p_createRegionRenderCache_2_: BlockPos?,
        p_createRegionRenderCache_3_: BlockPos?,
        p_createRegionRenderCache_4_: Int
    ): RegionRenderCache {
        return RegionRenderCache(
            p_createRegionRenderCache_1_,
            p_createRegionRenderCache_2_,
            p_createRegionRenderCache_3_,
            p_createRegionRenderCache_4_
        )
    }

    private fun fixBlockLayer(p_fixBlockLayer_1_: Block, p_fixBlockLayer_2_: EnumWorldBlockLayer?): EnumWorldBlockLayer? {
        if (isMipmaps) {
            if (p_fixBlockLayer_2_ == EnumWorldBlockLayer.CUTOUT) {
                if (p_fixBlockLayer_1_ is BlockRedstoneWire) {
                    return p_fixBlockLayer_2_
                }
                return if (p_fixBlockLayer_1_ is BlockCactus) {
                    p_fixBlockLayer_2_
                } else EnumWorldBlockLayer.CUTOUT_MIPPED
            }
        } else if (p_fixBlockLayer_2_ == EnumWorldBlockLayer.CUTOUT_MIPPED) {
            return EnumWorldBlockLayer.CUTOUT
        }
        return p_fixBlockLayer_2_
    }

    companion object {
        private const val __OBFID = "CL_00002452"

        @JvmField
        var renderChunksUpdated = 0
        private val ENUM_WORLD_BLOCK_LAYERS: Array<EnumWorldBlockLayer?> = EnumWorldBlockLayer.values().map { it.takeIf { true } }.toTypedArray()
    }

    init {
        if (blockPosIn != position) {
            setPosition(blockPosIn)
        }
        if (OpenGlHelper.useVbo()) {
            for (i in EnumWorldBlockLayer.values().indices) {
                vertexBuffers[i] = VertexBuffer(DefaultVertexFormats.BLOCK)
            }
        }
    }
}