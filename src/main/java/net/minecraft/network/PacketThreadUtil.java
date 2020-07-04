package net.minecraft.network;

import net.minecraft.util.IThreadListener;

public class PacketThreadUtil
{
    public static <T extends INetHandler> void checkThreadAndEnqueue(final Packet<T> packet, final T handler, IThreadListener threadListener) throws ThreadQuickExitException
    {
        if (!threadListener.isCallingFromMinecraftThread())
        {
            threadListener.addScheduledTask(() -> packet.processPacket(handler));
            throw ThreadQuickExitException.field_179886_a;
        }
    }
}
