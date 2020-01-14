package org.abitoff.mc.eot;

import org.abitoff.mc.eot.items.MutatedCropItem;

import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.util.IItemProvider;

public class MutatedCropBlock extends CropsBlock
{
	private static final MutatedCropBlock INSTANCE =
			(MutatedCropBlock) new MutatedCropBlock(Properties.from(Blocks.CARROTS))
					.setRegistryName(Constants.MUTATED_CROP_RL);

	private MutatedCropBlock(Properties builder)
	{
		super(builder);
	}

	protected IItemProvider getSeedsItem()
	{
		return MutatedCropItem.get();
	}

	public static MutatedCropBlock get()
	{
		return INSTANCE;
	}
}
