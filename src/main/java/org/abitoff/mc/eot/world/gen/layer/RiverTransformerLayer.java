package org.abitoff.mc.eot.world.gen.layer;

import org.abitoff.mc.eot.EndOfTime;
import org.abitoff.mc.eot.world.biome.LavaRiverBiome;

import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public class RiverTransformerLayer implements IC0Transformer
{
	public static final RiverTransformerLayer INSTANCE = new RiverTransformerLayer();
	private static final int RIVER = EndOfTime.BIOME_REGISTRY.getID(Biomes.RIVER);
	private static final int FROZEN_RIVER = EndOfTime.BIOME_REGISTRY.getID(Biomes.FROZEN_RIVER);
	private static final int LAVA_RIVER = EndOfTime.BIOME_REGISTRY.getID(LavaRiverBiome.INSTANCE);

	private RiverTransformerLayer()
	{
	}

	@Override
	public int apply(INoiseRandom context, int value)
	{
		if (value == RIVER || value == FROZEN_RIVER)
			return LAVA_RIVER;
		else
			return value;
	}
}
