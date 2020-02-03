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

public class SMutationAcceleratorItemsChangedPacket implements EOTPacket
{
	private BlockPos pos;
	private Item specimen;
	private Item result;

	public SMutationAcceleratorItemsChangedPacket()
	{
		this(null, null, null);
	}

	public SMutationAcceleratorItemsChangedPacket(BlockPos pos, Item specimen, Item result)
	{
		this.pos = pos;
		this.specimen = specimen;
		this.result = result;
	}

	@Override
	public void decode(PacketBuffer buf)
	{
		this.pos = buf.readBlockPos();
		this.specimen = Item.getItemById(buf.readInt());
		this.result = Item.getItemById(buf.readInt());
	}

	@Override
	public void encode(PacketBuffer buf)
	{
		buf.writeBlockPos(pos);
		buf.writeInt(Item.getIdFromItem(specimen));
		buf.writeInt(Item.getIdFromItem(result));
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
					((MutationAcceleratorTileEntity) te).onSpecimenChangedPacketReceived(specimen, result);
			});
		}
		ctx.setPacketHandled(true);
	}
}
