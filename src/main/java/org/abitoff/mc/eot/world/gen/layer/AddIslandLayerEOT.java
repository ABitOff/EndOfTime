package org.abitoff.mc.eot.world.gen.layer;

import org.abitoff.mc.eot.EndOfTime;

import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.AddIslandLayer;
import net.minecraft.world.gen.layer.traits.IBishopTransformer;

public class AddIslandLayerEOT implements IBishopTransformer
{
	private static final int DESERT = EndOfTime.BIOME_REGISTRY.getID(Biomes.DESERT);
	public static final AddIslandLayerEOT INSTANCE = new AddIslandLayerEOT();

	@Override
	public int apply(INoiseRandom context, int a, int b, int c, int d, int e)
	{
		int value = AddIslandLayer.INSTANCE.apply(context, a, b, c, d, e);
		if (!LayerUtilEOT.isOcean(value))
		{
			return DESERT;
		} else
		{
			return value;
		}
	}
}
