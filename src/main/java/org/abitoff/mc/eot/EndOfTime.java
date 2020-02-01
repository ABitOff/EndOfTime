package org.abitoff.mc.eot;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.world.WorldTypeEOT;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class EndOfTime
{
	public static boolean isModLoaded(World w)
	{
		if (w == null || w.getWorldType() == null)
			return false;
		return WorldTypeEOT.get().getName().equals(w.getWorldType().getName());
	}
}
