package org.abitoff.mc.eot.network.play.server;

import org.abitoff.mc.eot.block.MutationAcceleratorBlock;
import org.abitoff.mc.eot.network.EOTNetworkChannel.EOTPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class SMutationAcceleratorMutationPacket implements EOTPacket
{
	private BlockPos pos;
	private int level;

	public SMutationAcceleratorMutationPacket()
	{
		this(null, 0);
	}

	public SMutationAcceleratorMutationPacket(BlockPos pos, int level)
	{
		this.pos = pos;
		this.level = level;
	}

	@Override
	public void decode(PacketBuffer buf)
	{
		this.pos = buf.readBlockPos();
		this.level = buf.readInt();
	}

	@Override
	public void encode(PacketBuffer buf)
	{
		buf.writeBlockPos(pos);
		buf.writeInt(level);
	}

	@Override
	public void handle(NetworkEvent.Context ctx)
	{
		if (ctx.getDirection().getReceptionSide() == LogicalSide.CLIENT)
		{
			ctx.enqueueWork(() ->
			{
				ClientWorld w = Minecraft.getInstance().world;
				MutationAcceleratorBlock.onItemMutated(w, pos, level);
			});
		}
		ctx.setPacketHandled(true);
	}
}
