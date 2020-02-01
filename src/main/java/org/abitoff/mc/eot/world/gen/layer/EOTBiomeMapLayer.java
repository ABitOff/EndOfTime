package org.abitoff.mc.eot.world.gen.layer;

import org.abitoff.mc.eot.world.biome.DeepLavaOceanBiome;
import org.abitoff.mc.eot.world.biome.EOTDesertBiome;
import org.abitoff.mc.eot.world.biome.LavaOceanBiome;
import org.abitoff.mc.eot.world.biome.LavaRiverBiome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

public class EOTBiomeMapLayer implements IC0Transformer
{
	public static final EOTBiomeMapLayer INSTANCE = new EOTBiomeMapLayer();
	public static final ForgeRegistry<Biome> BIOME_REGISTRY =
			(ForgeRegistry<Biome>) RegistryManager.ACTIVE.getRegistry(Biome.class);
	private static final int RIVER = BIOME_REGISTRY.getID(Biomes.RIVER);
	private static final int FROZEN_RIVER = BIOME_REGISTRY.getID(Biomes.FROZEN_RIVER);
	private static final int LAVA_RIVER = BIOME_REGISTRY.getID(LavaRiverBiome.INSTANCE);
	private static final int DEEP_LAVA_OCEAN = BIOME_REGISTRY.getID(DeepLavaOceanBiome.INSTANCE);
	private static final int SHALLOW_LAVA_OCEAN = BIOME_REGISTRY.getID(LavaOceanBiome.INSTANCE);
	private static final int EOT_DESERT = BIOME_REGISTRY.getID(EOTDesertBiome.INSTANCE);

	private EOTBiomeMapLayer()
	{
	}

	@Override
	public int apply(INoiseRandom context, int value)
	{
		if (value == RIVER || value == FROZEN_RIVER)
		{
			return LAVA_RIVER;
		}
		if (LayerUtilEOT.isOcean(value))
		{
			if (LayerUtilEOT.isShallowOcean(value))
			{
				return SHALLOW_LAVA_OCEAN;
			} else
			{
				return DEEP_LAVA_OCEAN;
			}
		} else
		{
			return EOT_DESERT;
		}
	}
}
