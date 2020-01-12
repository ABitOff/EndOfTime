package org.abitoff.mc.eot.world.gen.layer;

import java.util.function.LongFunction;

import com.google.common.collect.ImmutableList;

import net.minecraft.world.WorldType;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.layer.AddBambooForestLayer;
import net.minecraft.world.gen.layer.AddIslandLayer;
import net.minecraft.world.gen.layer.AddSnowLayer;
import net.minecraft.world.gen.layer.BiomeLayer;
import net.minecraft.world.gen.layer.DeepOceanLayer;
import net.minecraft.world.gen.layer.EdgeBiomeLayer;
import net.minecraft.world.gen.layer.EdgeLayer;
import net.minecraft.world.gen.layer.HillsLayer;
import net.minecraft.world.gen.layer.IslandLayer;
import net.minecraft.world.gen.layer.LayerUtil;
import net.minecraft.world.gen.layer.MixOceansLayer;
import net.minecraft.world.gen.layer.MixRiverLayer;
import net.minecraft.world.gen.layer.OceanLayer;
import net.minecraft.world.gen.layer.RemoveTooMuchOceanLayer;
import net.minecraft.world.gen.layer.RiverLayer;
import net.minecraft.world.gen.layer.ShoreLayer;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.StartRiverLayer;
import net.minecraft.world.gen.layer.VoroniZoomLayer;
import net.minecraft.world.gen.layer.ZoomLayer;

public class LayerUtilEOT extends LayerUtil
{
	public static <T extends IArea, C extends IExtendedNoiseRandom<T>> ImmutableList<IAreaFactory<T>>
			buildEOTProcedure(WorldType type, OverworldGenSettings settings, LongFunction<C> ctxFactory)
	{
		IAreaFactory<T> factory1 = IslandLayer.INSTANCE.apply(ctxFactory.apply(1));
		factory1 = ZoomLayer.FUZZY.apply(ctxFactory.apply(2000), factory1);
		factory1 = AddIslandLayer.INSTANCE.apply(ctxFactory.apply(1), factory1);
		factory1 = ZoomLayer.NORMAL.apply(ctxFactory.apply(2001), factory1);
		factory1 = AddIslandLayer.INSTANCE.apply(ctxFactory.apply(2), factory1);
		factory1 = AddIslandLayer.INSTANCE.apply(ctxFactory.apply(50), factory1);
		factory1 = AddIslandLayer.INSTANCE.apply(ctxFactory.apply(70), factory1);
		factory1 = RemoveTooMuchOceanLayer.INSTANCE.apply(ctxFactory.apply(2), factory1);
		IAreaFactory<T> factory2 = OceanLayer.INSTANCE.apply(ctxFactory.apply(2));
		factory2 = LayerUtil.repeat(2001, ZoomLayer.NORMAL, factory2, 6, ctxFactory);
		factory1 = AddSnowLayer.INSTANCE.apply(ctxFactory.apply(2), factory1);
		factory1 = AddIslandLayer.INSTANCE.apply(ctxFactory.apply(3), factory1);
		factory1 = EdgeLayer.CoolWarm.INSTANCE.apply(ctxFactory.apply(2), factory1);
		factory1 = EdgeLayer.HeatIce.INSTANCE.apply(ctxFactory.apply(2), factory1);
		factory1 = EdgeLayer.Special.INSTANCE.apply(ctxFactory.apply(3), factory1);
		factory1 = ZoomLayer.NORMAL.apply(ctxFactory.apply(2002), factory1);
		factory1 = ZoomLayer.NORMAL.apply(ctxFactory.apply(2003), factory1);
		factory1 = AddIslandLayer.INSTANCE.apply(ctxFactory.apply(4), factory1);
		factory1 = DeepOceanLayer.INSTANCE.apply(ctxFactory.apply(4), factory1);
		factory1 = LayerUtil.repeat(1000L, ZoomLayer.NORMAL, factory1, 0, ctxFactory);
		int biomeSize = 4;
		int riverSize = biomeSize;
		if (settings != null)
		{
			biomeSize = settings.getBiomeSize();
			riverSize = settings.getRiverSize();
		}

		IAreaFactory<T> factory3 = LayerUtil.repeat(1000, ZoomLayer.NORMAL, factory1, 0, ctxFactory);
		factory3 = StartRiverLayer.INSTANCE.apply(ctxFactory.apply(100), factory3);
		IAreaFactory<T> factory4 = getBiomeLayer(type, factory1, settings, ctxFactory);
		IAreaFactory<T> factory5 = LayerUtil.repeat(1000, ZoomLayer.NORMAL, factory3, 2, ctxFactory);
		factory4 = HillsLayer.INSTANCE.apply(ctxFactory.apply(1000), factory4, factory5);
		factory3 = LayerUtil.repeat(1000, ZoomLayer.NORMAL, factory3, 2, ctxFactory);
		factory3 = LayerUtil.repeat(1000, ZoomLayer.NORMAL, factory3, riverSize, ctxFactory);
		factory3 = RiverLayer.INSTANCE.apply(ctxFactory.apply(1), factory3);
		factory3 = SmoothLayer.INSTANCE.apply(ctxFactory.apply(1000), factory3);

		for (int k = 0; k < biomeSize; k++)
		{
			factory4 = ZoomLayer.NORMAL.apply(ctxFactory.apply(1000 + k), factory4);
			if (k == 0)
			{
				factory4 = AddIslandLayer.INSTANCE.apply(ctxFactory.apply(3), factory4);
			}

			if (k == 1 || biomeSize == 1)
			{
				factory4 = ShoreLayer.INSTANCE.apply(ctxFactory.apply(1000), factory4);
			}
		}

		factory4 = SmoothLayer.INSTANCE.apply(ctxFactory.apply(1000), factory4);
		factory4 = MixRiverLayer.INSTANCE.apply(ctxFactory.apply(100), factory4, factory3);
		factory4 = MixOceansLayer.INSTANCE.apply(ctxFactory.apply(100), factory4, factory2);
		factory4 = EOTBiomeMapLayer.INSTANCE.apply(ctxFactory.apply(200), factory4);
		IAreaFactory<T> factory6 = VoroniZoomLayer.INSTANCE.apply(ctxFactory.apply(10), factory4);
		return ImmutableList.of(factory4, factory6, factory4);
	}

	public static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> getBiomeLayer(WorldType type,
			IAreaFactory<T> parentLayer, OverworldGenSettings chunkSettings, LongFunction<C> contextFactory)
	{
		parentLayer = (new BiomeLayer(type, chunkSettings)).apply(contextFactory.apply(200), parentLayer);
		parentLayer = AddBambooForestLayer.INSTANCE.apply(contextFactory.apply(1001), parentLayer);
		parentLayer = LayerUtil.repeat(1000, ZoomLayer.NORMAL, parentLayer, 2, contextFactory);
		parentLayer = EdgeBiomeLayer.INSTANCE.apply(contextFactory.apply(1000), parentLayer);
		return parentLayer;
	}

	public static boolean isOcean(int biomeIn)
	{
		return LayerUtil.isOcean(biomeIn);
	}

	public static boolean isShallowOcean(int biomeIn)
	{
		return LayerUtil.isShallowOcean(biomeIn);
	}
}
