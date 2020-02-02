package org.abitoff.mc.eot.network.play.server;

import org.abitoff.mc.eot.network.EOTNetworkChannel.EOTPacket;
import org.abitoff.mc.eot.tileentity.MutationAcceleratorTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SMutationAcceleratorSpecimenChangePacket implements EOTPacket
{
	private BlockPos pos;
	private Item specimen;

	public SMutationAcceleratorSpecimenChangePacket()
	{
		this(null, null);
	}

	public SMutationAcceleratorSpecimenChangePacket(BlockPos pos, Item specimen)
	{
		this.pos = pos;
		this.specimen = specimen;
	}

	@Override
	public void decode(PacketBuffer buf)
	{
		this.pos = buf.readBlockPos();
		this.specimen = Item.getItemById(buf.readInt());
	}

	@Override
	public void encode(PacketBuffer buf)
	{
		buf.writeBlockPos(pos);
		buf.writeInt(Item.getIdFromItem(specimen));
	}

	@Override
	public void handle(Context ctx)
	{
		if (ctx.getDirection().getReceptionSide() == LogicalSide.CLIENT)
		{
			ctx.enqueueWork(() ->
			{
				ClientWorld w = Minecraft.getInstance().world;
				TileEntity te = w.getTileEntity(pos);
				if (te != null && te instanceof MutationAcceleratorTileEntity)
					((MutationAcceleratorTileEntity) te).onSpecimenChangedPacketReceived(specimen);
			});
		}
		ctx.setPacketHandled(true);
	}
}
