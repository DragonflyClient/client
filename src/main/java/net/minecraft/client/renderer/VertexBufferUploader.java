package net.minecraft.client.renderer;

import net.minecraft.client.renderer.vertex.VertexBuffer;

public class VertexBufferUploader extends WorldVertexBufferUploader
{
    private VertexBuffer vertexBuffer = null;

    public void func_181679_a(WorldRenderer worldRenderer) {
        worldRenderer.reset();
        this.vertexBuffer.func_181722_a(worldRenderer.getByteBuffer());
    }

    public void setVertexBuffer(VertexBuffer vertexBufferIn)
    {
        this.vertexBuffer = vertexBufferIn;
    }
}
