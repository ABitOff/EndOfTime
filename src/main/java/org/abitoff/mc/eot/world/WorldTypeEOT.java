package org.abitoff.mc.eot.world;

import org.abitoff.mc.eot.Constants;

import net.minecraft.world.WorldType;

public class WorldTypeEOT extends WorldType
{
	private static final WorldType INSTANCE = new WorldTypeEOT();

	private WorldTypeEOT()
	{
		super(Constants.END_OF_TIME_WORLD_TYPE_NAME);
	}

	public static WorldType get()
	{
		return INSTANCE;
	}
}
