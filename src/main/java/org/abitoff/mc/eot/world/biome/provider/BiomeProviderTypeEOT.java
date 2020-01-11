package org.abitoff.mc.eot.world.biome.provider;

import org.abitoff.mc.eot.Constants;

import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;

public class BiomeProviderTypeEOT extends BiomeProviderType<OverworldBiomeProviderSettings, OverworldBiomeProvider>
{
	private static final BiomeProviderTypeEOT INSTANCE =
			(BiomeProviderTypeEOT) new BiomeProviderTypeEOT().setRegistryName(Constants.BIOME_PROVIDER_RL);

	private BiomeProviderTypeEOT()
	{
		super(BiomeProviderEOT::new, OverworldBiomeProviderSettings::new);
	}

	public static BiomeProviderTypeEOT get()
	{
		return INSTANCE;
	}
}
