package org.abitoff.mc.eot.items;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.block.MutationAcceleratorBlock;

import net.minecraft.item.BlockNamedItem;

public class MutationAcceleratorItem extends BlockNamedItem
{
	private static final MutationAcceleratorItem INSTANCE =
			(MutationAcceleratorItem) new MutationAcceleratorItem().setRegistryName(Constants.MUTATION_ACCELERATOR_RL);

	private MutationAcceleratorItem()
	{
		super(MutationAcceleratorBlock.get(), new Properties());
	}

	public static MutationAcceleratorItem get()
	{
		return INSTANCE;
	}
}
