package org.abitoff.mc.eot.world.gen.layer;

import org.abitoff.mc.eot.EndOfTime;

import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.OverworldGenSettings;
//import net.minecraft.world.gen.layer.LayerUtil;
import net.minecraft.world.gen.layer.traits.IC0Transformer;
//import net.minecraftforge.registries.ForgeRegistries;
//import net.minecraftforge.registries.GameData;

public class BiomeLayerEOT implements IC0Transformer
{
	private static final int DESERT = EndOfTime.BIOME_REGISTRY.getID(Biomes.DESERT);
	private final OverworldGenSettings settings;

	public BiomeLayerEOT(WorldType p_i48641_1_, OverworldGenSettings p_i48641_2_)
	{
		this.settings = p_i48641_2_;
	}

	public int apply(INoiseRandom context, int value)
	{
		if (this.settings != null && this.settings.getBiomeId() >= 0)
		{
			return this.settings.getBiomeId();
		} else
		{
			value = value & -3841;
			if (!LayerUtilEOT.isOcean(value))
			{
				return DESERT;
			} else
			{
				return value;
			}
		}
	}
}
