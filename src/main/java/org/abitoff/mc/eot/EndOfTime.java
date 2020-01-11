package org.abitoff.mc.eot;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.world.biome.DeepLavaOceanBiome;
import org.abitoff.mc.eot.world.biome.LavaRiverBiome;
import org.abitoff.mc.eot.world.biome.LavaOceanBiome;
import org.abitoff.mc.eot.world.biome.provider.BiomeProviderTypeEOT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

@Mod(Constants.MOD_ID)
public class EndOfTime
{
	private static final Logger LOGGER = LogManager.getLogger(EndOfTime.class);
	public static final ForgeRegistry<Biome> BIOME_REGISTRY = RegistryManager.ACTIVE
			.getRegistry(RegistryManager.ACTIVE.getName(RegistryManager.ACTIVE.getRegistry(Biome.class)));

	public EndOfTime()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event)
	{
		LOGGER.info("Loading {} version {}", Constants.MOD_NAME, Constants.MOD_VERSION);
		while (true)
		{
			LOGGER.info(Constants.MOD_NAME);
		}
	}

	@EventBusSubscriber(modid = Constants.MOD_ID, bus = Bus.MOD)
	public static class ModDimensionRegistrar
	{
		@SubscribeEvent
		public static void onBiomeProviderRegistryEvent(RegistryEvent.Register<BiomeProviderType<?, ?>> event)
		{
			event.getRegistry().register(BiomeProviderTypeEOT.get());
		}

		@SubscribeEvent
		public static void onBiomeRegistryEvent(RegistryEvent.Register<Biome> event)
		{
			event.getRegistry().register(DeepLavaOceanBiome.INSTANCE);
			event.getRegistry().register(LavaOceanBiome.INSTANCE);
			event.getRegistry().register(LavaRiverBiome.INSTANCE);
		}
	}
}
