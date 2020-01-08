package org.abitoff.mc.eot.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraftforge.registries.ForgeRegistries;

public class WorldTypeEOT extends WorldType
{
	private static final String NAME = "End of Time";
	private static final WorldType INSTANCE = new WorldTypeEOT();

	private WorldTypeEOT()
	{
		super(NAME);
	}

	public ChunkGenerator<?> createChunkGenerator(World world)
	{
		ForgeRegistries.BIOME_PROVIDER_TYPES.getClass();
		ChunkGeneratorType<OverworldGenSettings, OverworldChunkGenerator> chunkgeneratortype4 =
				ChunkGeneratorType.SURFACE;
		BiomeProviderType<OverworldBiomeProviderSettings, OverworldBiomeProvider> biomeprovidertype1 =
				BiomeProviderType.VANILLA_LAYERED;
		OverworldGenSettings overworldgensettings = chunkgeneratortype4.createSettings();
		OverworldBiomeProviderSettings overworldbiomeprovidersettings = biomeprovidertype1.createSettings()
				.setWorldInfo(world.getWorldInfo()).setGeneratorSettings(overworldgensettings);
		return chunkgeneratortype4.create(world, biomeprovidertype1.create(overworldbiomeprovidersettings),
				overworldgensettings);
	}

	public static WorldType get()
	{
		return INSTANCE;
	}
}
