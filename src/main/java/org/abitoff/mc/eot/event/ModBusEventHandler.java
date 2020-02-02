package org.abitoff.mc.eot.event;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.EndOfTime;
import org.abitoff.mc.eot.block.MutatedCropBlock;
import org.abitoff.mc.eot.block.MutationAcceleratorBlock;
import org.abitoff.mc.eot.client.gui.screen.inventory.MutationAcceleratorScreen;
import org.abitoff.mc.eot.client.renderer.tileentity.MutationAcceleratorTileEntityRenderer;
import org.abitoff.mc.eot.inventory.container.MutationAcceleratorContainer;
import org.abitoff.mc.eot.items.MutatedCropItem;
import org.abitoff.mc.eot.items.MutationAcceleratorItem;
import org.abitoff.mc.eot.items.MutativeCerateItem;
import org.abitoff.mc.eot.network.EOTNetworkChannel;
import org.abitoff.mc.eot.network.play.server.SMutationAcceleratorMutationPacket;
import org.abitoff.mc.eot.network.play.server.SMutationAcceleratorSpecimenChangePacket;
import org.abitoff.mc.eot.recipe.EOTShapedRecipe;
import org.abitoff.mc.eot.recipe.EOTShapelessRecipe;
import org.abitoff.mc.eot.recipe.MutationAcceleratorRecipe;
import org.abitoff.mc.eot.tileentity.MutationAcceleratorTileEntity;
import org.abitoff.mc.eot.world.WorldTypeEOT;
import org.abitoff.mc.eot.world.biome.DeepLavaOceanBiome;
import org.abitoff.mc.eot.world.biome.EOTDesertBiome;
import org.abitoff.mc.eot.world.biome.LavaOceanBiome;
import org.abitoff.mc.eot.world.biome.LavaRiverBiome;
import org.abitoff.mc.eot.world.biome.provider.BiomeProviderTypeEOT;
import org.abitoff.mc.eot.world.dimension.DimensionEOT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.datafixers.util.Pair;

import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.DesertVillagePools;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = Bus.MOD)
public class ModBusEventHandler
{
	private static final Logger LOGGER = LogManager.getLogger(ModBusEventHandler.class);

	@SubscribeEvent
	public static void onCommonSetupEvent(FMLCommonSetupEvent event)
	{
		LOGGER.info("Loading {} version {}", Constants.MOD_NAME, Constants.MOD_VERSION);

		// Register network stuff
		EOTNetworkChannel.register(SMutationAcceleratorMutationPacket.class, SMutationAcceleratorMutationPacket::new);
		EOTNetworkChannel.register(SMutationAcceleratorSpecimenChangePacket.class,
				SMutationAcceleratorSpecimenChangePacket::new);

		// Register WorldType
		WorldTypeEOT.get();

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
		{
			// Register screens
			ScreenManager.registerFactory(MutationAcceleratorContainer.getContainerType(),
					MutationAcceleratorScreen::new);

			// Register tile entity renderers
			ClientRegistry.bindTileEntitySpecialRenderer(MutationAcceleratorTileEntity.class,
					new MutationAcceleratorTileEntityRenderer());
		});

		// Replace the overworld dimension factory with one that supplies a DimensionEOT when the mod is loaded and
		// enabled
		final BiFunction<World, DimensionType, ? extends Dimension> oldFactory = DimensionType.OVERWORLD.factory;
		DimensionType.OVERWORLD.factory = (world, dimType) ->
		{
			if (EndOfTime.isModLoaded(world))
				return new DimensionEOT(world, dimType);
			else
				return oldFactory.apply(world, dimType);
		};

		// Create and register a village jigsaw pattern that only generates zombie villages
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

	@SubscribeEvent
	public static void onBiomeProviderRegistryEvent(RegistryEvent.Register<BiomeProviderType<?, ?>> event)
	{
		event.getRegistry().register(BiomeProviderTypeEOT.get());
	}

	@SubscribeEvent
	public static void onBlockRegistryEvent(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().register(MutatedCropBlock.get());
		event.getRegistry().register(MutationAcceleratorBlock.get());
	}

	@SubscribeEvent
	public static void onItemRegistryEvent(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(MutativeCerateItem.get());
		event.getRegistry().register(MutatedCropItem.get());
		event.getRegistry().register(MutationAcceleratorItem.get());
	}

	@SubscribeEvent
	public static void onTileEntityRegistryEvent(RegistryEvent.Register<TileEntityType<?>> event)
	{
		event.getRegistry().register(MutationAcceleratorTileEntity.get());
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

	@SubscribeEvent
	public static void onRecipeSerializerRegistryEvent(RegistryEvent.Register<IRecipeSerializer<?>> event)
	{
		event.getRegistry().register(EOTShapelessRecipe.SERIALIZER);
		event.getRegistry().register(EOTShapedRecipe.SERIALIZER);
		event.getRegistry().register(MutationAcceleratorRecipe.SERIALIZER);
	}

	@SubscribeEvent
	public static void onContainerTypeRegistryEvent(final RegistryEvent.Register<ContainerType<?>> event)
	{
		event.getRegistry().register(MutationAcceleratorContainer.getContainerType());
	}
}
