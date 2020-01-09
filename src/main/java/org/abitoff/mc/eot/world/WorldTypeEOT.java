package org.abitoff.mc.eot.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;

public class WorldTypeEOT extends WorldType
{
	private static final String NAME = "End of Time";
	private static final WorldType INSTANCE = new WorldTypeEOT();
	private static final List<Biome> BIOMES = Arrays.asList(Biomes.DESERT);
	private static final List<Biome> MODIFIED_SPAWN_BIOMES = new ArrayList<Biome>();

	private WorldTypeEOT()
	{
		super(NAME);
	}

	public ChunkGenerator<?> createChunkGenerator(World world)
	{
		MODIFIED_SPAWN_BIOMES.clear();
		for (BiomeType type: BiomeType.values())
		{
			for (BiomeEntry entry: BiomeManager.getBiomes(type))
			{
				BiomeManager.removeBiome(type, entry);
			}

			for (Biome b: BIOMES)
			{
				BiomeManager.addBiome(type, new BiomeEntry(b, 1));
			}
		}

		for (Biome b: BIOMES)
		{
			if (!BiomeProvider.BIOMES_TO_SPAWN_IN.contains(b))
			{
				BiomeManager.addSpawnBiome(b);
				MODIFIED_SPAWN_BIOMES.add(b);
			}
		}

		return super.createChunkGenerator(world);
	}

	public static WorldType get()
	{
		return INSTANCE;
	}
}
