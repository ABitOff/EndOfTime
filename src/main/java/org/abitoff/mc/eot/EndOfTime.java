package org.abitoff.mc.eot;

import java.util.List;
import java.util.stream.Collectors;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.items.MutatedCropItem;
import org.abitoff.mc.eot.items.MutativeCerateItem;
import org.abitoff.mc.eot.world.biome.DeepLavaOceanBiome;
import org.abitoff.mc.eot.world.biome.EOTDesertBiome;
import org.abitoff.mc.eot.world.biome.LavaRiverBiome;
import org.abitoff.mc.eot.world.biome.LavaOceanBiome;
import org.abitoff.mc.eot.world.biome.provider.BiomeProviderTypeEOT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.datafixers.util.Pair;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.DesertVillagePools;
import net.minecraftforge.common.BiomeManager;
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

		DesertVillagePools.init();
		JigsawPattern pattern = JigsawManager.field_214891_a.get(new ResourceLocation("village/desert/town_centers"));
		List<Pair<JigsawPiece, Integer>> pairList = pattern.field_214952_d.stream().filter(pair ->
		{
			JigsawPiece piece = pair.getFirst();
			if (piece instanceof SingleJigsawPiece)
				return ((SingleJigsawPiece) piece).location.toString().contains("zombie");
			return false;
		}).collect(Collectors.toList());
		JigsawManager.field_214891_a
				.register(new JigsawPattern(new ResourceLocation(Constants.MOD_ID, "village/eot/town_centers"),
						new ResourceLocation("empty"), pairList, JigsawPattern.PlacementBehaviour.RIGID));
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
		public static void onBlockRegistryEvent(RegistryEvent.Register<Block> event)
		{
			event.getRegistry().register(MutatedCropBlock.get());
		}

		@SubscribeEvent
		public static void onItemRegistryEvent(RegistryEvent.Register<Item> event)
		{
			event.getRegistry().register(MutativeCerateItem.get());
			event.getRegistry().register(MutatedCropItem.get());
		}

		@SubscribeEvent
		public static void onBiomeRegistryEvent(RegistryEvent.Register<Biome> event)
		{
			event.getRegistry().register(DeepLavaOceanBiome.INSTANCE);
			event.getRegistry().register(LavaOceanBiome.INSTANCE);
			event.getRegistry().register(LavaRiverBiome.INSTANCE);
			event.getRegistry().register(EOTDesertBiome.INSTANCE);
			BiomeManager.addSpawnBiome(EOTDesertBiome.INSTANCE);
		}
	}
}
