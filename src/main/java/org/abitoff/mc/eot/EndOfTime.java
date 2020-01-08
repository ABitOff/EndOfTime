package org.abitoff.mc.eot;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.abitoff.mc.eot.world.WorldTypeEOT;
import org.abitoff.mc.eot.world.biome.BiomeProviderTypeEOT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

@Mod("eot")
public class EndOfTime
{
	private static final Logger LOGGER = LogManager.getLogger(EndOfTime.class);
	private static final Map<String, Object> BUILD_DATA = getBuildData();
	private static final WorldType WORLD_TYPE_EOT = WorldTypeEOT.get();
	// this is technically a ForgeRegistry<BiomeProviderType<
	// ? extends net.minecraft.world.biome.provider.IBiomeProviderSettings,
	// ? extends net.minecraft.world.biome.provider.BiomeProvider
	// >>
	private static final ForgeRegistry<BiomeProviderType<?, ?>> BIOME_PROVIDER_REGISTRY =
			RegistryManager.ACTIVE.getRegistry(new ResourceLocation("minecraft:biome_source_type"));

	public EndOfTime()
	{
		// Register the setup method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
		BIOME_PROVIDER_REGISTRY.register(BiomeProviderTypeEOT.get());
	}

	private void setup(final FMLCommonSetupEvent event)
	{
		LOGGER.info("Loading " + BUILD_DATA.getOrDefault("mod-name", "End of Time") + " version "
				+ BUILD_DATA.getOrDefault("mod-version", "?"));
	}

	// @SubscribeEvent
	// public void registerBlocks(RegistryEvent.Register<BiomeProviderType<?, ?>> event)
	// {
	// }

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
}
