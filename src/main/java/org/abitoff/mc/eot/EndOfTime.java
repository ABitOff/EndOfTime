package org.abitoff.mc.eot;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

import org.abitoff.mc.eot.world.biome.DeepLavaOceanBiome;
import org.abitoff.mc.eot.world.biome.LavaRiverBiome;
import org.abitoff.mc.eot.world.biome.ShallowLavaOceanBiome;
import org.abitoff.mc.eot.world.biome.provider.BiomeProviderTypeEOT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.minecraft.util.ResourceLocation;
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

@Mod("eot")
public class EndOfTime
{
	private static final Logger LOGGER = LogManager.getLogger(EndOfTime.class);
	private static final Map<String, Object> BUILD_DATA = getBuildData();
	public static final ResourceLocation EOT_BIOME_PROVIDER_RL = new ResourceLocation("eot:eot-biome-provider");
	public static final ForgeRegistry<Biome> BIOME_REGISTRY = RegistryManager.ACTIVE
			.getRegistry(RegistryManager.ACTIVE.getName(RegistryManager.ACTIVE.getRegistry(Biome.class)));

	public EndOfTime()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event)
	{
		LOGGER.info("Loading {} version {}", BUILD_DATA.get("mod-name"), BUILD_DATA.get("mod-version"));
	}

	private static Map<String, Object> getBuildData()
	{
		Type mapType = new TypeToken<Map<String, Object>>()
		{
		}.getType();
		try (Reader r = new InputStreamReader(EndOfTime.class.getResourceAsStream("/build-data.json")))
		{
			return new Gson().fromJson(r, mapType);
		} catch (Exception e)
		{
			LOGGER.error("Failed to get End of Time build data!", e);
			throw new RuntimeException(e);
		}
	}

	@EventBusSubscriber(modid = "eot", bus = Bus.MOD)
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
			event.getRegistry().register(ShallowLavaOceanBiome.INSTANCE);
			event.getRegistry().register(LavaRiverBiome.INSTANCE);
		}
	}
}
