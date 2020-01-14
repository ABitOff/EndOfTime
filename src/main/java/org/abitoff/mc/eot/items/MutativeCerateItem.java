package org.abitoff.mc.eot.items;

import org.abitoff.mc.eot.Constants;

import net.minecraft.item.Item;

public class MutativeCerateItem extends Item
{
	private static final MutativeCerateItem INSTANCE = (MutativeCerateItem) new MutativeCerateItem(new Properties())
			.setRegistryName(Constants.MUTATIVE_CERATE_RL);

	private MutativeCerateItem(Properties properties)
	{
		super(properties);
	}

	public static MutativeCerateItem get()
	{
		return INSTANCE;
	}
}
