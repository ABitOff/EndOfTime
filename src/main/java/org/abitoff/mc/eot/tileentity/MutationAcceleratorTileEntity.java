package org.abitoff.mc.eot.tileentity;

import java.util.Set;
import java.util.stream.Collectors;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.block.MutationAcceleratorBlock;
import org.abitoff.mc.eot.items.MutativeCerateItem;
import org.abitoff.mc.eot.recipe.MutationAcceleratorRecipe;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class MutationAcceleratorTileEntity extends LockableTileEntity
{
	@SuppressWarnings("unchecked")
	private static final TileEntityType<MutationAcceleratorTileEntity> TYPE_INSTANCE =
			(TileEntityType<MutationAcceleratorTileEntity>) TileEntityType.Builder
					.create(MutationAcceleratorTileEntity::new, MutationAcceleratorBlock.get()).build(null)
					.setRegistryName(Constants.MUTATION_ACCELERATOR_TILE_ENTITY_RL);
	// private static final WeightedItemCollection mutableCrops = new WeightedItemCollection(p(Items.WHEAT_SEEDS, 1),
	// p(Items.BEETROOT_SEEDS, 0.25), p(Items.CARROT, 0.1), p(Items.POTATO, 0.1), p(Items.POISONOUS_POTATO, 0.25),
	// p(Items.MELON_SEEDS, 0.25), p(Items.PUMPKIN, 0.4));
	// private static final WeightedItemCollection mutableSaplings = new WeightedItemCollection(p(Items.OAK_SAPLING, 1),
	// p(Items.ACACIA_SAPLING, 0.5), p(Items.BIRCH_SAPLING, 0.5), p(Items.DARK_OAK_SAPLING, 0.5),
	// p(Items.JUNGLE_SAPLING, 0.5), p(Items.SPRUCE_SAPLING, 0.5), p(Items.DEAD_BUSH, 2.5));
	// private static final WeightedItemCollection mutableUseful = new WeightedItemCollection(p(Items.BAMBOO, 0.25),
	// p(Items.CACTUS, 0.75), p(Items.COCOA_BEANS, 0.1), p(Items.SUGAR_CANE, 0.25), p(Items.KELP, 0.25));
	// private static final WeightedItemCollection mutableFungal = new WeightedItemCollection(p(Items.RED_MUSHROOM,
	// 1.5),
	// p(Items.BROWN_MUSHROOM, 0.5), p(Items.NETHER_WART, 0.125), p(Items.CHORUS_FLOWER, 0.05));
	// private static final WeightedItemCollection mutableFlowers = new WeightedItemCollection(p(Items.DANDELION, 1),
	// p(Items.POPPY, 1), p(Items.BLUE_ORCHID, 1), p(Items.ALLIUM, 1), p(Items.AZURE_BLUET, 1),
	// p(Items.RED_TULIP, 1), p(Items.ORANGE_TULIP, 1), p(Items.WHITE_TULIP, 1), p(Items.PINK_TULIP, 1),
	// p(Items.OXEYE_DAISY, 1), p(Items.CORNFLOWER, 1), p(Items.LILY_OF_THE_VALLEY, 1), p(Items.WITHER_ROSE, 1),
	// p(Items.SUNFLOWER, 1), p(Items.LILAC, 1), p(Items.ROSE_BUSH, 1), p(Items.PEONY, 1));
	// private static final WeightedItemCollection mutableCoral = new WeightedItemCollection(p(Items.BRAIN_CORAL, 1));
	// private static final WeightedItemCollection mutableDecorative =
	// new WeightedItemCollection(p(Items.LILY_PAD, 1), p(Items.VINE, 1), p(Items.FERN, 1), p(Items.GRASS, 1),
	// p(Items.SEAGRASS, 1), p(Items.SWEET_BERRIES, 1), p(Items.SEA_PICKLE, 1));
	// private static final ImmutableList<WeightedItemCollection[]> MUTABLE_GROUPS = ImmutableList.of(
	// new WeightedItemCollection[] {mutableCrops}, new WeightedItemCollection[] {mutableSaplings},
	// new WeightedItemCollection[] {mutableUseful, mutableFungal},
	// new WeightedItemCollection[] {mutableFungal, mutableFlowers, mutableCoral, mutableDecorative});

	private final Set<MutationAcceleratorRecipe> mutationTrees;
	protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);

	public MutationAcceleratorTileEntity()
	{
		super(TYPE_INSTANCE);
		mutationTrees =
				this.world.getRecipeManager().getRecipes().stream().filter(r -> r instanceof MutationAcceleratorRecipe)
						.map(r -> (MutationAcceleratorRecipe) r).collect(Collectors.toSet());
	}

	public void read(CompoundNBT compound)
	{
		super.read(compound);
		this.items = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(compound, items);
	}

	public CompoundNBT write(CompoundNBT compound)
	{
		super.write(compound);
		ItemStackHelper.saveAllItems(compound, items);
		return compound;
	}

	@Override
	public int getSizeInventory()
	{
		return 3;
	}

	@Override
	public boolean isEmpty()
	{
		for (ItemStack itemstack: items)
			if (!itemstack.isEmpty())
				return false;

		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index)
	{
		return items.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		return ItemStackHelper.getAndSplit(items, index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		return ItemStackHelper.getAndRemove(items, index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		this.items.set(index, stack);
		if (stack.getCount() > getInventoryStackLimit())
			stack.setCount(getInventoryStackLimit());
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player)
	{
		if (this.world.getTileEntity(pos) != this)
			return false;
		else
			return player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
	}

	public boolean isItemValidForSlot(int index, ItemStack stack)
	{

		if (index == 0)
		{
			return true;
		} else if (index == 1)
		{
			return stack.getItem() == MutativeCerateItem.get();
		} else
		{
			return false;
		}
	}

	@Override
	public void clear()
	{
		this.items.clear();
	}

	@Override
	protected ITextComponent getDefaultName()
	{
		return new TranslationTextComponent(Constants.MUTATION_ACCELERATOR_NAME_TTC);
	}

	@Override
	protected Container createMenu(int id, PlayerInventory player)
	{
		return null;
	}

	public static TileEntityType<MutationAcceleratorTileEntity> get()
	{
		return TYPE_INSTANCE;
	}
}
