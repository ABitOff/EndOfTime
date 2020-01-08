package org.abitoff.mc.eot.world.biome;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.IBiomeProviderSettings;

public class BiomeProviderTypeEOT<C extends IBiomeProviderSettings, T extends BiomeProvider>
		extends BiomeProviderType<C, T>
{
	private static final BiomeProviderTypeEOT INSTANCE = new BiomeProviderTypeEOT(null, null);

	private BiomeProviderTypeEOT(Function<C, T> factory, Supplier<C> settingsFactory)
	{
		super(factory, settingsFactory);
		this.setRegistryName("eot", "eot_biome_provider_type");
	}

	public static BiomeProviderTypeEOT get()
	{
		return INSTANCE;
	}
}
