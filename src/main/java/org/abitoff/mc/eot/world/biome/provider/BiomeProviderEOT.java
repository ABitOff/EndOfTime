package org.abitoff.mc.eot.world.biome.provider;

import org.abitoff.mc.eot.world.gen.layer.LayerUtilEOT;

import com.google.common.collect.ImmutableList;

import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.Layer;
import net.minecraft.world.storage.WorldInfo;

public class BiomeProviderEOT extends OverworldBiomeProvider
{
	public BiomeProviderEOT(OverworldBiomeProviderSettings settingsProvider)
	{
		super(settingsProvider);
		WorldInfo info = settingsProvider.getWorldInfo();
		OverworldGenSettings ogSettings = settingsProvider.getGeneratorSettings();
		ImmutableList<IAreaFactory<LazyArea>> areaFactories = LayerUtilEOT.buildEOTProcedure(info.getGenerator(),
				ogSettings, seedMod -> new LazyAreaLayerContext(25, info.getSeed(), seedMod));
		this.genBiomes = new Layer(areaFactories.get(0));
		this.biomeFactoryLayer = new Layer(areaFactories.get(1));
	}
}
