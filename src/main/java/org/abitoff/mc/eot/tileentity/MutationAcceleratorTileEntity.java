package org.abitoff.mc.eot.tileentity;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.block.MutationAcceleratorBlock;
import org.abitoff.mc.eot.inventory.container.MutationAcceleratorContainer;
import org.abitoff.mc.eot.items.MutativeCerateItem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class MutationAcceleratorTileEntity extends LockableTileEntity implements ITickableTileEntity
{
	@SuppressWarnings("unchecked")
	private static final TileEntityType<MutationAcceleratorTileEntity> TYPE_INSTANCE =
			(TileEntityType<MutationAcceleratorTileEntity>) TileEntityType.Builder
					.create(MutationAcceleratorTileEntity::new, MutationAcceleratorBlock.get()).build(null)
					.setRegistryName(Constants.MUTATION_ACCELERATOR_TILE_ENTITY_RL);

	// private Set<MutationAcceleratorRecipe> mutationTrees;
	private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
	private boolean lit = false;

	public MutationAcceleratorTileEntity()
	{
		super(TYPE_INSTANCE);
		// mutationTrees =
		// this.world.getRecipeManager().getRecipes().stream().filter(r -> r instanceof MutationAcceleratorRecipe)
		// .map(r -> (MutationAcceleratorRecipe) r).collect(Collectors.toSet());
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
	protected Container createMenu(int id, PlayerInventory inv)
	{
		if (this.canOpen(inv.player))
			return new MutationAcceleratorContainer(id, inv, this);
		else
			return null;
	}

	public static TileEntityType<MutationAcceleratorTileEntity> get()
	{
		return TYPE_INSTANCE;
	}

	@Override
	public void tick()
	{
		if (world == null)
			return;
		if (!world.isRemote)
		{
			if (world.getGameTime() % 20 == 0)
			{
				BlockState blockstate = this.getBlockState();
				Block block = blockstate.getBlock();
				if (block == MutationAcceleratorBlock.get())
					MutationAcceleratorBlock.updateLevel(blockstate, world, pos);
			}
			if (world.getGameTime() % 50 == 0)
			{
				BlockState blockstate = this.getBlockState();
				Block block = blockstate.getBlock();
				if (block == MutationAcceleratorBlock.get())
					MutationAcceleratorBlock.flipState(blockstate, world, pos);
				markDirty();
			}
		} else
		{
			boolean newLit = MutationAcceleratorBlock.isLit(getBlockState());
			if (!newLit && lit)
			{
				onItemMutated();
			}
			lit = newLit;
		}
	}

	public void onItemMutated()
	{
		if (world != null && world.isRemote)
		{
			BlockState blockstate = this.getBlockState();
			Block block = blockstate.getBlock();
			if (block == MutationAcceleratorBlock.get())
			{
				MutationAcceleratorBlock.onItemMutated(blockstate, world, pos);
			}
		}
	}
}
