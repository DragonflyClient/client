package net.minecraft.client.renderer;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import optifine.Config;
import optifine.Reflector;
import org.lwjgl.opengl.GL11;
import shadersmod.client.SVertexBuilder;

import java.nio.ByteBuffer;
import java.util.List;

public class WorldVertexBufferUploader {
    private static final String __OBFID = "CL_00002567";

    public void func_181679_a(WorldRenderer worldRenderer) {
        if (worldRenderer.getVertexCount() > 0) {
            VertexFormat vertexformat = worldRenderer.getVertexFormat();
            int i = vertexformat.getNextOffset();
            ByteBuffer bytebuffer = worldRenderer.getByteBuffer();
            List list = vertexformat.getElements();
            boolean flag = Reflector.ForgeVertexFormatElementEnumUseage_preDraw.exists();
            boolean flag1 = Reflector.ForgeVertexFormatElementEnumUseage_postDraw.exists();

            for (int j = 0; j < list.size(); ++j) {
                VertexFormatElement vertexformatelement = (VertexFormatElement)list.get(j);
                VertexFormatElement.EnumUsage vertexformatelement$enumusage = vertexformatelement.getUsage();

                if (flag)
                {
                    Reflector.callVoid(vertexformatelement$enumusage, Reflector.ForgeVertexFormatElementEnumUseage_preDraw, vertexformat, j, i, bytebuffer);
                }
                else
                {
                    int l = vertexformatelement.getType().getGlConstant();
                    int k = vertexformatelement.getIndex();
                    bytebuffer.position(vertexformat.func_181720_d(j));

                    switch (WorldVertexBufferUploader.WorldVertexBufferUploader$1.vertexFormatElements[vertexformatelement$enumusage.ordinal()]) {
                        case 1:
                            GL11.glVertexPointer(vertexformatelement.getElementCount(), l, i, bytebuffer);
                            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                            break;

                        case 2:
                            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + k);
                            GL11.glTexCoordPointer(vertexformatelement.getElementCount(), l, i, bytebuffer);
                            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                            break;

                        case 3:
                            GL11.glColorPointer(vertexformatelement.getElementCount(), l, i, bytebuffer);
                            GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                            break;

                        case 4:
                            GL11.glNormalPointer(l, i, bytebuffer);
                            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                    }
                }
            }

            if (worldRenderer.isMultiTexture()) {
                worldRenderer.drawMultiTexture();
            } else if (Config.isShaders()) {
                SVertexBuilder.drawArrays(worldRenderer.getDrawMode(), 0, worldRenderer.getVertexCount(), worldRenderer);
            } else {
                GL11.glDrawArrays(worldRenderer.getDrawMode(), 0, worldRenderer.getVertexCount());
            }

            int i1 = 0;

            for (int k1 = list.size(); i1 < k1; ++i1)
            {
                VertexFormatElement vertexformatelement1 = (VertexFormatElement)list.get(i1);
                VertexFormatElement.EnumUsage vertexformatelement$enumusage1 = vertexformatelement1.getUsage();

                if (flag1)
                {
                    Reflector.callVoid(vertexformatelement$enumusage1, Reflector.ForgeVertexFormatElementEnumUseage_postDraw, vertexformat, Integer.valueOf(i1), Integer.valueOf(i), bytebuffer);
                }
                else
                {
                    int j1 = vertexformatelement1.getIndex();

                    switch (WorldVertexBufferUploader.WorldVertexBufferUploader$1.vertexFormatElements[vertexformatelement$enumusage1.ordinal()]) {
                        case 1:
                            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
                            break;

                        case 2:
                            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + j1);
                            GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                            break;

                        case 3:
                            GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
                            GlStateManager.resetColor();
                            break;

                        case 4:
                            GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
                    }
                }
            }
        }

        worldRenderer.reset();
    }

    static final class WorldVertexBufferUploader$1
    {
        static final int[] vertexFormatElements = new int[VertexFormatElement.EnumUsage.values().length];
        private static final String __OBFID = "CL_00002566";

        static
        {
            try
            {
                vertexFormatElements[VertexFormatElement.EnumUsage.POSITION.ordinal()] = 1;
            }
            catch (NoSuchFieldError var4)
            {
            }

            try
            {
                vertexFormatElements[VertexFormatElement.EnumUsage.UV.ordinal()] = 2;
            }
            catch (NoSuchFieldError var3)
            {
            }

            try
            {
                vertexFormatElements[VertexFormatElement.EnumUsage.COLOR.ordinal()] = 3;
            }
            catch (NoSuchFieldError var2)
            {
            }

            try
            {
                vertexFormatElements[VertexFormatElement.EnumUsage.NORMAL.ordinal()] = 4;
            }
            catch (NoSuchFieldError var1)
            {
            }
        }
    }
}
