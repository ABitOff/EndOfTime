package org.abitoff.mc.eot.world.dimension;

import org.abitoff.mc.eot.world.biome.provider.BiomeProviderTypeEOT;
import org.abitoff.mc.eot.world.gen.EOTOverworldGenSettings;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;

public class DimensionEOT extends OverworldDimension
{
	// super.calculateCelestialAngle(0) == this.calculateCelestialAngle(TIME_OFFSET)
	// 1.0d + Math.asin((Math.sqrt(2.0d) - 4.0d) / 6.0d) / Math.PI;
	private static final double TIME_OFFSET = 0.8581734479693928d;
	public static final long MIDNIGHT_OFFSET = 15404;

	public DimensionEOT(World worldIn, DimensionType typeIn)
	{
		super(worldIn, typeIn);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator()
	{
		ChunkGeneratorType<OverworldGenSettings, OverworldChunkGenerator> chunkGenSurface = ChunkGeneratorType.SURFACE;
		BiomeProviderType<OverworldBiomeProviderSettings, OverworldBiomeProvider> biomeProviderVanilla =
				BiomeProviderTypeEOT.get();
		EOTOverworldGenSettings settings = new EOTOverworldGenSettings(chunkGenSurface.createSettings());
		settings.setDefaultFluid(Blocks.LAVA.getDefaultState());
		settings.setVillageDistance(64);
		settings.setBiomeFeatureDistance(64);
		OverworldBiomeProviderSettings overworldbiomeprovidersettings = biomeProviderVanilla.createSettings()
				.setWorldInfo(this.world.getWorldInfo()).setGeneratorSettings(settings);
		return chunkGenSurface.create(this.world, biomeProviderVanilla.create(overworldbiomeprovidersettings),
				settings);
	}

	public float calculateCelestialAngle(long worldTime, float partialTicks)
	{
		// for future reference, super.calculateCelestialAngle(0, 0) == 1 / 12 * (8 + sqrt(2)) ~= 0.784517765045166
		double dayLength = 24000;
		double x = ((double) (worldTime % (long) dayLength) / dayLength + TIME_OFFSET) % 1.0;
		return (float) (Math.sin(Math.PI * x) / 2.0d * sgn(0.5d - x) + Math.floor(x + 0.5d));
	}

	private static double sgn(double x)
	{
		return x < 0 ? -1 : 1;
	}

	@Override
	public Vec3d getFogColor(float celestialAngle, float partialTicks)
	{
		double r = 0.25;
		double gb = 0.25;
		if (celestialAngle > 0.25 && celestialAngle < 0.75)
		{
			r = Math.max(2.5 * Math.abs((double) celestialAngle - 0.5) - 0.375, 0.125);
			gb = Math.max(2.5 * Math.abs((double) celestialAngle - 0.5) - 0.375, 0.046875);
		}
		return new Vec3d(r, gb, gb);
	}

	@Override
	public boolean doesXZShowFog(int x, int z)
	{
		return true;
	}
}
