package org.abitoff.mc.eot.world.gen.layer;

import org.abitoff.mc.eot.EndOfTime;
import org.abitoff.mc.eot.world.biome.DeepLavaOceanBiome;
import org.abitoff.mc.eot.world.biome.ShallowLavaOceanBiome;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public class OceanTransformerLayer implements IC0Transformer
{
	public static final OceanTransformerLayer INSTANCE = new OceanTransformerLayer();
	private static final int DEEP_LAVA_OCEAN = EndOfTime.BIOME_REGISTRY.getID(DeepLavaOceanBiome.INSTANCE);
	private static final int SHALLOW_LAVA_OCEAN = EndOfTime.BIOME_REGISTRY.getID(ShallowLavaOceanBiome.INSTANCE);

	private OceanTransformerLayer()
	{
	}

	@Override
	public int apply(INoiseRandom context, int value)
	{
		if (LayerUtilEOT.isShallowOcean(value))
			return SHALLOW_LAVA_OCEAN;
		else if (LayerUtilEOT.isOcean(value))
			return DEEP_LAVA_OCEAN;
		else
			return value;
	}
}
