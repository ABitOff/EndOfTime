package org.abitoff.mc.eot.event;

import java.util.function.BiFunction;
import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.world.WorldTypeEOT;
import org.abitoff.mc.eot.world.dimension.DimensionEOT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.FleeSunGoal;
import net.minecraft.entity.ai.goal.RestrictSunGoal;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
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
	public static void onServerAboutToStart(FMLServerAboutToStartEvent event)
	{
		// get this server's WorldType
		MinecraftServer server = event.getServer();
		WorldInfo info = server.getActiveAnvilConverter().getWorldInfo(server.getFolderName());
		WorldType type = info == null ? null : info.getGenerator();

		// if the server is loading a WorldTypeEOT, inject our factory
		if (type == WORLD_TYPE_EOT)
		{
			LOGGER.info("Detected server configured with {} world type. Injecting custom overworld factory.",
					WORLD_TYPE_EOT.getName());
			OVERWORLD.factory = factoryOverride;
		} else if (type == null)
		{
			LOGGER.warn("Server starting with indeterminate world type! Assuming world type is not {}. Ignoring.",
					WORLD_TYPE_EOT.getName());
		} else
		{
			LOGGER.info("Server starting without {} world type. Ignoring.", WORLD_TYPE_EOT.getName());
		}
	}

	/**
	 * Cleanup after a server has stopped, undoing our injection.
	 */
	@SubscribeEvent
	public static void onServerStopped(FMLServerStoppedEvent event)
	{
		LOGGER.info("Server stopped. Restoring default overworld factory: {}. Was: {}.", factoryDefault,
				OVERWORLD.factory);
		OVERWORLD.factory = factoryDefault;
	}

	/**
	 * World generation has finished, and a spawn point is being created. We want to place the player next to a village
	 * and give them a crafting table, so we handle that here.
	 */
	@SubscribeEvent
	public static void onCreateSpawnPosition(CreateSpawnPosition event)
	{
		IWorld world = event.getWorld();
		assert world instanceof ServerWorld;
		@SuppressWarnings("resource") // closing sw isn't our job.. seems like a java compiler bug.
		ServerWorld sw = (ServerWorld) world;

		if (sw.getWorldType() != WORLD_TYPE_EOT || sw.getDimension().getType() != OVERWORLD)
			return;

		LOGGER.info("Creating spawn position for {} world type.", sw.getWorldType().getName());

		// look for a village within 1000 chunks of (0,0,0). It's incredibly rare that this will ever fail.
		LOGGER.info("Searching for a village to spawn next to.");
		int radius = 1000;
		BlockPos villagePos = sw.findNearestStructure("Village", new BlockPos(0, 0, 0), radius, false);
		if (villagePos == null)
		{
			LOGGER.warn("No village found! Resorting to default spawn point creation algorithm.");
			return;
		}
		LOGGER.info("Village found at {}.", villagePos);

		// look for a chunk which can be set as the spawn chunk. we search in concentric squares centered around the
		// chunk which villagePos is located in. we only search the edges of the square, because the center chunks of
		// the square were searched by earlier iterations.
		LOGGER.info("Searching for appropriate spawn chunk near village.");
		ChunkPos origin = new ChunkPos(villagePos);
		for (int currRadius = 0; currRadius < radius; currRadius++)
		{
			LOGGER.info("Search radius: {}", currRadius);
			for (int x = -currRadius; x < currRadius + 1; x++)
			{
				boolean xEdge = x == -currRadius || x == currRadius;
				for (int z = -currRadius; z < currRadius + 1; z++)
				{
					ChunkPos pos = new ChunkPos(origin.x + x, origin.z + z);
					BlockPos spawn = sw.getDimension().findSpawn(pos, false);

					if (spawn != null)
					{
						LOGGER.info("Spawn chunk found: {}", spawn);
						sw.getWorldInfo().setSpawn(spawn);
						event.setCanceled(true);
						return;
					}

					if (!xEdge)
						z += currRadius;
				}
			}
		}
		LOGGER.warn("Spawn chunk not found! Resorting to default spawn point creation algorithm.");
	}

	@SubscribeEvent
	public static void onEntityJoinWorldEvent(EntityJoinWorldEvent event)
	{
		Entity entity = event.getEntity();
		if (!(entity instanceof ZombieEntity))
			return;
		ZombieEntity zombie = (ZombieEntity) entity;
		zombie.goalSelector.addGoal(4, new RestrictSunGoal(zombie));
		zombie.goalSelector.addGoal(4, new FleeSunGoal(zombie, 1.0D));
	}
}
