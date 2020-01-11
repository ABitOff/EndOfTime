package org.abitoff.mc.eot.world;

import net.minecraft.world.WorldType;

public class WorldTypeEOT extends WorldType
{
	private static final String NAME = "end-of-time";
	private static final WorldType INSTANCE = new WorldTypeEOT();

	private WorldTypeEOT()
	{
		super(NAME);
	}

	public static WorldType get()
	{
		return INSTANCE;
	}
}
