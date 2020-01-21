package org.abitoff.mc.eot.event;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.block.MutatedCropBlock;
import org.abitoff.mc.eot.items.MutatedCropItem;
import org.abitoff.mc.eot.items.MutativeCerateItem;
import org.abitoff.mc.eot.recipe.EOTShapedRecipe;
import org.abitoff.mc.eot.recipe.EOTShapelessRecipe;
import org.abitoff.mc.eot.world.biome.DeepLavaOceanBiome;
import org.abitoff.mc.eot.world.biome.EOTDesertBiome;
import org.abitoff.mc.eot.world.biome.LavaOceanBiome;
import org.abitoff.mc.eot.world.biome.LavaRiverBiome;
import org.abitoff.mc.eot.world.biome.provider.BiomeProviderTypeEOT;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = Bus.MOD)
public class ModBusEventHandler
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

	@SubscribeEvent
	public static void onRecipeSerializerRegistryEvent(RegistryEvent.Register<IRecipeSerializer<?>> event)
	{
		event.getRegistry().register(EOTShapelessRecipe.SERIALIZER);
		event.getRegistry().register(EOTShapedRecipe.SERIALIZER);
	}
}
