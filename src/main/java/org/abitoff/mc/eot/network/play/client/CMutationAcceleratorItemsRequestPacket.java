package org.abitoff.mc.eot.network.play.client;

import org.abitoff.mc.eot.network.EOTNetworkChannel;
import org.abitoff.mc.eot.network.EOTNetworkChannel.EOTPacket;
import org.abitoff.mc.eot.network.play.server.SMutationAcceleratorItemsChangedPacket;
import org.abitoff.mc.eot.tileentity.MutationAcceleratorTileEntity;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;

public class CMutationAcceleratorItemsRequestPacket implements EOTPacket
{
	private BlockPos pos;

	public CMutationAcceleratorItemsRequestPacket()
	{
		this(null);
	}

	public CMutationAcceleratorItemsRequestPacket(BlockPos pos)
	{
		this.pos = pos;
	}

	@Override
	public void decode(PacketBuffer buf)
	{
		this.pos = buf.readBlockPos();
	}

	@Override
	public void encode(PacketBuffer buf)
	{
		buf.writeBlockPos(pos);
	}

	@Override
	public void handle(Context ctx)
	{
		if (ctx.getDirection().getReceptionSide() == LogicalSide.SERVER)
		{
			final ServerPlayerEntity player = ctx.getSender();
			ctx.enqueueWork(() ->
			{
				ServerWorld world = player.getServerWorld();
				TileEntity te = world.getTileEntity(pos);
				if (te != null && te instanceof MutationAcceleratorTileEntity)
				{
					MutationAcceleratorTileEntity mate = (MutationAcceleratorTileEntity) te;
					EOTNetworkChannel.send(PacketDistributor.PLAYER.with(() -> player),
							new SMutationAcceleratorItemsChangedPacket(pos, mate.getSpecimen(), mate.getResult()));
				}
			});
		}
		ctx.setPacketHandled(true);
	}
}
