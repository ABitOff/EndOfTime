package org.abitoff.mc.eot.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.ChunkGenerator;

public class WorldTypeEOT extends WorldType
{
	private static final String NAME = "End of Time";
	private static final WorldType INSTANCE = new WorldTypeEOT();

	private WorldTypeEOT()
	{
		super(NAME);
	}

	public ChunkGenerator<?> createChunkGenerator(World world)
	{
		// TODO (see net.minecraft.world.dimension.createChunkGenerator())
        return world.dimension.createChunkGenerator();
	}

	public static WorldType get()
	{
		return INSTANCE;
	}
}
