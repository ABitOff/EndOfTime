package org.abitoff.mc.eot.tileentity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.block.MutationAcceleratorBlock;
import org.abitoff.mc.eot.inventory.container.MutationAcceleratorContainer;
import org.abitoff.mc.eot.items.MutativeCerateItem;
import org.abitoff.mc.eot.network.EOTNetworkChannel;
import org.abitoff.mc.eot.network.play.server.SMutationAcceleratorMutationPacket;
import org.abitoff.mc.eot.recipe.MutationAcceleratorRecipe;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.PacketDistributor;

public class MutationAcceleratorTileEntity extends LockableTileEntity implements ITickableTileEntity
{
	@SuppressWarnings("unchecked")
	private static final TileEntityType<MutationAcceleratorTileEntity> TYPE_INSTANCE =
			(TileEntityType<MutationAcceleratorTileEntity>) TileEntityType.Builder
					.create(MutationAcceleratorTileEntity::new, MutationAcceleratorBlock.get()).build(null)
					.setRegistryName(Constants.MUTATION_ACCELERATOR_TILE_ENTITY_RL);

	private Set<MutationAcceleratorRecipe> mutationTrees = new HashSet<MutationAcceleratorRecipe>();
	private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);

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
		int count = stack.getCount();
		if (index == 0 && count > getInventoryStackLimit())
			count = getInventoryStackLimit();
		else if (count > 1)
			count = 1;
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
			if (populateMergeTreeIfNeeded())
			{
				final Item item = stack.getItem();
				return mutationTrees.stream().anyMatch(r ->
				{
					return r.getTree().containsKey(item);
				});
			}
			return false;
		} else if (index == 1)
		{
			return stack.getItem() == MutativeCerateItem.get();
		} else
		{
			return false;
		}
	}

	private boolean populateMergeTreeIfNeeded()
	{
		if (mutationTrees != null)
			return true;
		if (world == null || world.getRecipeManager() == null)
			return false;

		Collection<IRecipe<?>> recipes = world.getRecipeManager().getRecipes();
		mutationTrees = recipes.stream().filter(r -> r instanceof MutationAcceleratorRecipe)
				.map(r -> (MutationAcceleratorRecipe) r).collect(Collectors.toSet());
		return true;
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
				{
					MutationAcceleratorBlock.flipState(blockstate, world, pos);
					onItemMutated();
				}
				markDirty();
			}
		}
	}

	private void onItemMutated()
	{
		if (world != null && !world.isRemote)
		{
			Chunk c = world.getChunkAt(pos);
			EOTNetworkChannel.send(PacketDistributor.TRACKING_CHUNK.with(() -> c),
					new SMutationAcceleratorMutationPacket(pos));
		}
	}
}
