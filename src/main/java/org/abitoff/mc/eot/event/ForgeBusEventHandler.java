package org.abitoff.mc.eot.event;

import java.util.function.BiFunction;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.world.WorldTypeEOT;
import org.abitoff.mc.eot.world.dimension.DimensionEOT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.WorldEvent.CreateSpawnPosition;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

/**
 * Handles modifying DimensionType.OVERWORLD to inject our own custom Dimension, rather than using the default overworld
 * Dimension.
 * 
 * @author Steven Fontaine
 */
@EventBusSubscriber(modid = Constants.MOD_ID, bus = Bus.FORGE)
public class ForgeBusEventHandler
{
	private static final Logger LOGGER = LogManager.getLogger(ForgeBusEventHandler.class);
	private static final WorldType WORLD_TYPE_EOT = WorldTypeEOT.get();
	private static final DimensionType OVERWORLD = DimensionType.OVERWORLD;

	/**
	 * DimensionType.OVERWORLD's default Dimension factory
	 */
	private static final BiFunction<World, DimensionType, ? extends Dimension> factoryDefault = OVERWORLD.factory;

	/**
	 * The Dimension factory which will override DimensionType.OVERWORLD's default
	 */
	private static final BiFunction<World, DimensionType, ? extends Dimension> factoryOverride = DimensionEOT::new;

	/**
	 * When a server is about to start, we check its settings to see if its WorldType is WorldTypeEOT. If it is, we
	 * inject our Dimension factory into DimensionType.OVERWORLD.
	 */
	@SubscribeEvent
	public static void inject(FMLServerAboutToStartEvent event)
	{
		// get this server's WorldType
		WorldType t;
		MinecraftServer server = event.getServer();
		if (server instanceof IntegratedServer)
		{
			t = ((IntegratedServer) server).worldSettings.getTerrainType();
		} else if (server instanceof DedicatedServer)
		{
			t = ((DedicatedServer) server).settings.getProperties().worldType;
		} else
		{
			// this should never happen unless another mod has added a custom server type.
			LOGGER.warn("Unrecognized server type: {}. Assuming mod is not enabled!", server.getClass().getName());
			t = null;
		}

		// if the server is loading a WorldTypeEOT, inject our factory
		if (t == WORLD_TYPE_EOT)
		{
			OVERWORLD.factory = factoryOverride;
		}
	}

	/**
	 * Cleanup after a server has stopped, undoing our injection.
	 */
	@SubscribeEvent
	public static void cleanup(FMLServerStoppedEvent event)
	{
		OVERWORLD.factory = factoryDefault;
	}

	@SubscribeEvent
	public static void onCreateSpawnPosition(CreateSpawnPosition event)
	{
		IWorld world = event.getWorld();
		assert world instanceof ServerWorld;

		if (((ServerWorld) world).getWorldType() != WorldTypeEOT.get())
			return;

		int radius = 1000;

		BlockPos villagePos =
				((ServerWorld) world).findNearestStructure("Village", new BlockPos(0, 0, 0), radius, false);
		if (villagePos == null)
			return;

		ChunkPos origin = new ChunkPos(villagePos);
		for (int currRadius = 0; currRadius < radius; currRadius++)
		{
			for (int x = -currRadius; x < currRadius + 1; x++)
			{
				boolean xEdge = x == -currRadius || x == currRadius;
				for (int z = -currRadius; z < currRadius + 1; z++)
				{
					ChunkPos pos = new ChunkPos(origin.x + x, origin.z + z);
					BlockPos spawn = world.getDimension().findSpawn(pos, false);

					if (spawn != null)
					{
						world.getWorldInfo().setSpawn(spawn);
						world.setBlockState(spawn, Blocks.CRAFTING_TABLE.getDefaultState(), 3);
						event.setCanceled(true);
						return;
					}

					if (!xEdge)
						z += currRadius;
				}
			}
		}
	}
}
