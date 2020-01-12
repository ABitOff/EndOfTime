package org.abitoff.mc.eot.world.dimension;

import org.abitoff.mc.eot.world.biome.provider.BiomeProviderTypeEOT;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
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
		OverworldGenSettings settings = chunkGenSurface.createSettings();
		settings.setDefaultFluid(Blocks.LAVA.getDefaultState());
		OverworldBiomeProviderSettings overworldbiomeprovidersettings = biomeProviderVanilla.createSettings()
				.setWorldInfo(this.world.getWorldInfo()).setGeneratorSettings(settings);
		return chunkGenSurface.create(this.world, biomeProviderVanilla.create(overworldbiomeprovidersettings),
				settings);
	}

	@Override
	public BlockPos findSpawn(int posX, int posZ, boolean checkValid)
	{
		return super.findSpawn(posX, posZ, false);
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTicks)
	{
		return 0f;
	}

	@Override
	public boolean isSurfaceWorld()
	{
		return true;
	}

	@Override
	public Vec3d getFogColor(float celestialAngle, float partialTicks)
	{
		return new Vec3d(.25, .25, .25);
	}

	@Override
	public boolean canRespawnHere()
	{
		return true;
	}

	@Override
	public boolean doesXZShowFog(int x, int z)
	{
		return true;
	}
}