package org.abitoff.mc.eot.network;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.abitoff.mc.eot.Constants;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor.PacketTarget;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class EOTNetworkChannel
{
	private static final SimpleChannel CHANNEL =
			NetworkRegistry.newSimpleChannel(new ResourceLocation(Constants.NETWORK_CHANNEL_RL),
					Constants.MOD_VERSION::toString, Constants.MOD_VERSION::equals, Constants.MOD_VERSION::equals);
	private static volatile AtomicInteger index = new AtomicInteger(0);

	public static synchronized <T extends EOTPacket> void register(Class<T> type, Supplier<T> nullaryConstructor)
	{
		int index = EOTNetworkChannel.index.getAndIncrement();
		CHANNEL.registerMessage(index, type, EOTPacket::encode, (buf) ->
		{
			T p = nullaryConstructor.get();
			p.decode(buf);
			return p;
		}, (p, ctxSup) ->
		{
			NetworkEvent.Context ctx = ctxSup.get();
			p.handle(ctx);
		});
	}

	public static void send(PacketTarget target, EOTPacket packet)
	{
		CHANNEL.send(target, packet);
	}

	public static void sendTo(EOTPacket packet, NetworkManager manager, NetworkDirection direction)
	{
		CHANNEL.sendTo(packet, manager, direction);
	}

	public static void sendToServer(EOTPacket packet)
	{
		CHANNEL.sendToServer(packet);
	}

	public static void reply(EOTPacket received, NetworkEvent.Context ctx)
	{
		CHANNEL.reply(received, ctx);
	}

	public static interface EOTPacket
	{
		public void decode(PacketBuffer buf);

		public void encode(PacketBuffer buf);

		public void handle(NetworkEvent.Context handler);
	}
}
