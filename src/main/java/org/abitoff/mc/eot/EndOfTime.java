package org.abitoff.mc.eot;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.client.gui.screen.MutationAcceleratorScreen;
import org.abitoff.mc.eot.inventory.container.MutationAcceleratorContainer;
import org.abitoff.mc.eot.recipe.MutationAcceleratorRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.DesertVillagePools;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

@Mod(Constants.MOD_ID)
public class EndOfTime
{
	private static final Logger LOGGER = LogManager.getLogger(EndOfTime.class);
	public static final ForgeRegistry<Biome> BIOME_REGISTRY =
			(ForgeRegistry<Biome>) RegistryManager.ACTIVE.getRegistry(Biome.class);

	public EndOfTime()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event)
	{
		LOGGER.info("Loading {} version {}", Constants.MOD_NAME, Constants.MOD_VERSION);

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
		{
			ScreenManager.registerFactory(MutationAcceleratorContainer.getContainerType(),
					MutationAcceleratorScreen::new);
		});

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

	private void enqueueIMC(final InterModEnqueueEvent event)
	{
		try (Reader r = new FileReader(
				new File("Z:\\EndOfTime\\src\\main\\resources\\data\\eot\\recipes\\mutation_accelerator_tree.json")))
		{
			((SpecialRecipeSerializer<?>) MutationAcceleratorRecipe.SERIALIZER).read(new ResourceLocation("null"),
					new Gson().fromJson(r, JsonObject.class));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
