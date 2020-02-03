package org.abitoff.mc.eot.event;

import java.util.Map;

import javax.annotation.Nullable;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.EndOfTime;
import org.abitoff.mc.eot.network.EOTNetworkChannel;
import org.abitoff.mc.eot.network.play.server.SMutationAcceleratorItemsChangedPacket;
import org.abitoff.mc.eot.tileentity.MutationAcceleratorTileEntity;
import org.abitoff.mc.eot.world.WorldTypeEOT;
import org.abitoff.mc.eot.world.dimension.DimensionEOT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.FleeSunGoal;
import net.minecraft.entity.ai.goal.RestrictSunGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.ServerProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.world.WorldEvent.CreateSpawnPosition;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.network.PacketDistributor;

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
	private static final DimensionType OVERWORLD = DimensionType.OVERWORLD;
	private static final String DEFAULT_WORLD_TYPE_NAME = WorldType.DEFAULT.getName();

	/**
	 * When a server is about to start, we check its settings to see if its WorldType is WorldTypeEOT. If it is, we
	 * inject our Dimension factory into DimensionType.OVERWORLD.
	 */
	@SubscribeEvent
	public static void onServerAboutToStart(FMLServerAboutToStartEvent event)
	{
		// get this server's WorldType
		MinecraftServer server = event.getServer();
		if (server.isDedicatedServer())
		{
			ServerProperties properties = ((DedicatedServer) server).getServerProperties();
			JsonElement json = new Gson().fromJson(properties.generatorSettings, JsonElement.class);
			LOGGER.info("Detected a dedicated server with the following generator-settings:\n{}", json);
			if (json.isJsonObject())
			{
				if (JSONUtils.getBoolean(json.getAsJsonObject(), Constants.MOD_ID, false))
				{
					WorldType type = properties.worldType;
					if (type == WorldType.DEFAULT)
					{
						LOGGER.info("Forcing server to tell client that this world's WorldType is {}.",
								WorldTypeEOT.class.getSimpleName());
						type.name = WorldTypeEOT.get().name;
					} else
						LOGGER.info("The server's current WorldType isn't {}. It's {}. Ignoring.",
								DEFAULT_WORLD_TYPE_NAME, type != null ? type.getName() : "null");
				} else
					LOGGER.info("Ignoring");
			}
		}
	}

	/**
	 * Cleanup after a server has stopped, undoing our injection.
	 */
	@SubscribeEvent
	public static void onServerStopped(FMLServerStoppedEvent event)
	{
		WorldType.DEFAULT.name = DEFAULT_WORLD_TYPE_NAME;
	}

	/**
	 * World generation has finished, and a spawn point is being created. We want to place the player next to a village
	 * and give them a crafting table, so we handle that here.
	 */
	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onCreateSpawnPosition(CreateSpawnPosition event)
	{
		IWorld world = event.getWorld();
		assert world instanceof ServerWorld;
		ServerWorld sw = (ServerWorld) world;

		if (!EndOfTime.isModLoaded(sw) || sw.getDimension().getType() != OVERWORLD)
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

	/**
	 * Zombies are dumb and walk into the sunlight. This makes them stop doing that.
	 */
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

	@SubscribeEvent
	public static void onChunkWatchEvent(ChunkWatchEvent.Watch event)
	{
		LOGGER.info("{} is watching chunk ({}, {}).", event.getPlayer().getName().getString(), event.getPos().x,
				event.getPos().z);
		ChunkPos pos = event.getPos();
		Chunk c = event.getWorld().getChunk(pos.x, pos.z);
		if (c != null)
		{
			Map<BlockPos, TileEntity> tileEntities = c.getTileEntityMap();
			for (TileEntity te: tileEntities.values())
			{
				if (te instanceof MutationAcceleratorTileEntity)
				{
					MutationAcceleratorTileEntity mate = (MutationAcceleratorTileEntity) te;
					EOTNetworkChannel.send(PacketDistributor.PLAYER.with(() -> event.getPlayer()),
							new SMutationAcceleratorItemsChangedPacket(mate.getPos(), mate.getSpecimen(),
									mate.getResult()));
				}
			}
		}
	}

	/**
	 * Gives the first player to log in a crafting table.
	 */
	@SubscribeEvent
	public static void onPlayerLoggedInEvent(PlayerLoggedInEvent event)
	{
		PlayerEntity p = event.getPlayer();

		// give the player a crafting table only if the world's overworld dimension doesn't contain the nbt data for
		// "eotFirstRun"
		// (CompoundNBT.putBoolean(true) simply puts a ByteNBT(1). So we do the same.)
		doIfWorldDataNotPresent(p.world, () ->
		{
			// give the player a crafting table
			ItemStack stack = new ItemStack(Blocks.CRAFTING_TABLE);
			ItemEntity entity = p.dropItem(stack, false);
			if (entity != null)
			{
				entity.setNoPickupDelay();
				entity.setOwnerId(p.getUniqueID());
			}
		}, "eotFirstRun", new ByteNBT((byte) 1));
	}

	/**
	 * When the world loads for the first time, set the time to midnight.
	 */
	@SubscribeEvent
	public static void onWorldLoadEvent(WorldEvent.Load event)
	{
		World world = event.getWorld().getWorld();

		// onWorldEventLoad is called for all dimensions. we only need to run this for overworld, so we check for that.
		if (world.dimension.getType() == OVERWORLD)
			doIfWorldDataNotPresent(world, () ->
			{
				// set the world time
				world.setDayTime(DimensionEOT.MIDNIGHT_OFFSET);
			}, "eotFirstRun", null);
	}

	/**
	 * Runs {@code r} whenever {@code dataName} doesn't exist in the NBT data for the overworld dimension. Optionally
	 * sets the value for {@code dataName} to {@code setDataIfNotPresent}. If {@code setDataIfNotPresent} is
	 * {@code null}, it does not put anything for the value of {@code dataName}. <br>
	 * <b>Note:</b> If the {@link WorldType} of {@code world} is NOT the End of Time world type, this function returns
	 * before checking NBT data.
	 * 
	 * @param world
	 *            the world whose data to check
	 * @param r
	 *            the function to run if the given data isn't present
	 * @param dataName
	 *            the nbt tag to check
	 * @param setDataIfNotPresent
	 *            the value to place for the nbt tag, if it's not already present. if this is null, no value is set for
	 *            the nbt tag
	 */
	private static void doIfWorldDataNotPresent(World world, Runnable r, String dataName,
			@Nullable INBT setDataIfNotPresent)
	{
		// get the world info and check if it's the End of Time world type
		WorldInfo info = world.getWorldInfo();
		if (!EndOfTime.isModLoaded(world))
			return;

		// get the overworld dimension nbt data
		CompoundNBT data = info.getDimensionData(OVERWORLD);

		// return if the dimension data contains the tag
		if (data.contains(dataName))
			return;

		// if we're meant to set some data, do that.
		if (setDataIfNotPresent != null)
		{
			data.put(dataName, setDataIfNotPresent);
			info.setDimensionData(OVERWORLD, data);
		}

		// call r
		r.run();
	}
}
