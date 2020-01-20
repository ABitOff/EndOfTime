package org.abitoff.mc.eot.items;

import org.abitoff.mc.eot.MutatedCropBlock;

import net.minecraft.block.Block;
import net.minecraft.item.BlockNamedItem;

public class MutatedCropItem extends BlockNamedItem
{
	private static final MutatedCropItem INSTANCE =
			(MutatedCropItem) new MutatedCropItem(MutatedCropBlock.get(), new Properties())
					.setRegistryName(MutatedCropBlock.get().getRegistryName());

	private MutatedCropItem(Block blockIn, Properties builder)
	{
		super(blockIn, builder);
	}

	public static MutatedCropItem get()
	{
		return INSTANCE;
	}
}
