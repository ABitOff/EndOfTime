package org.abitoff.mc.eot.tileentity;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.block.MutationAcceleratorBlock;
import org.abitoff.mc.eot.inventory.container.MutationAcceleratorContainer;
import org.abitoff.mc.eot.items.MutativeCerateItem;
import org.abitoff.mc.eot.network.EOTNetworkChannel;
import org.abitoff.mc.eot.network.play.server.SMutationAcceleratorMutationPacket;
import org.abitoff.mc.eot.network.play.server.SMutationAcceleratorSpecimenChangePacket;
import org.abitoff.mc.eot.recipe.MutationAcceleratorRecipe;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;

public class MutationAcceleratorTileEntity extends LockableTileEntity implements ITickableTileEntity, ISidedInventory
{
	@SuppressWarnings("unchecked")
	private static final TileEntityType<MutationAcceleratorTileEntity> TYPE_INSTANCE =
			(TileEntityType<MutationAcceleratorTileEntity>) TileEntityType.Builder
					.create(MutationAcceleratorTileEntity::new, MutationAcceleratorBlock.get()).build(null)
					.setRegistryName(Constants.MUTATION_ACCELERATOR_TILE_ENTITY_RL);

	private static final Map<World, ImmutableSet<MutationAcceleratorRecipe>> MUTATION_TREES =
			new ConcurrentHashMap<World, ImmutableSet<MutationAcceleratorRecipe>>();

	// hoppers attached to one of the side faces or the top face have access to the two input slots
	private static final int[] TOP_AND_SIDE_SLOTS = new int[] {0, 1};
	// hoppers attached to the bottom face have access to the output slot
	private static final int[] DOWN_SLOTS = new int[] {2};

	private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
	private Item specimen = null;

	public MutationAcceleratorTileEntity()
	{
		super(TYPE_INSTANCE);
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
		return isItemValidForSlotStatic(index, stack, world);
	}

	public static boolean isItemValidForSlotStatic(int index, ItemStack stack, World world)
	{
		if (index == 0)
		{
			// if there's a recipe which takes this item as an ingredient, then the item is valid for the slot.

			Set<MutationAcceleratorRecipe> trees;
			// get or populate the tree for this world
			if ((trees = populateMutationTreeIfNeeded(world)) != null)
			{
				final Item item = stack.getItem();
				// iterate through the recipes for this world. if any take item as an ingredient, return true
				return trees.stream().anyMatch(r ->
				{
					return r.getTree().containsKey(item);
				});
			}
			// there are no trees for this world. return false?
			return false;
		} else if (index == 1)
		{
			// only mutative cerate is allowed in slot 1
			return stack.getItem() == MutativeCerateItem.get();
		} else
		{
			// nothing is allowed to be placed in the output slot, unless placed there directly by the tile entity.
			return false;
		}
	}

	private static ImmutableSet<MutationAcceleratorRecipe> populateMutationTreeIfNeeded(World world)
	{
		if (world == null)
			return null;

		ImmutableSet<MutationAcceleratorRecipe> tree;
		// if we already have a recipe for this world, return it.
		if ((tree = MUTATION_TREES.get(world)) != null)
			return tree;

		if (world.getRecipeManager() == null)
			return null;

		// get all the recipes registered for this world
		Collection<IRecipe<?>> recipes = world.getRecipeManager().getRecipes();
		// filter only the MutationAcceleratorRecipes, cast them to MutationAcceleratorRecipe, and collect them in a set
		tree = ImmutableSet.copyOf(recipes.stream().filter(r -> r instanceof MutationAcceleratorRecipe)
				.map(r -> (MutationAcceleratorRecipe) r).collect(Collectors.toSet()));
		// store that tree, then return it.
		MUTATION_TREES.put(world, tree);
		return tree;
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
			// calculate the sunlight level. this is the same code as in DaylightDetector.
			int level = 0;
			if (world.dimension.hasSkyLight())
			{
				level = world.getLightFor(LightType.SKY, pos) - world.getSkylightSubtracted();
				float sunAngle = world.getCelestialAngleRadians(1);
				if (level > 0)
				{
					float f1 = sunAngle < Math.PI ? 0 : (float) Math.PI * 2;
					sunAngle = sunAngle + (f1 - sunAngle) * 0.2f;
					level = Math.round((float) level * MathHelper.cos(sunAngle));
				}

				level = MathHelper.clamp(level, 0, 15);
			}

			// only active if there's sunlight, there's a specimen and some cerate, and the output slot is empty
			boolean active = level > 0 && !items.get(0).isEmpty() && !items.get(1).isEmpty() && items.get(2).isEmpty();

			// set the block state to match the active property if necessary.
			BlockState state = getBlockState();
			if (MutationAcceleratorBlock.isActive(state) != active)
				MutationAcceleratorBlock.setActive(world, pos, state, active);

			// averages to one mutation per minute at light level 15. time per mutation increases by 30 seconds per
			// level after that
			if (active && world.getRandom().nextInt(600 * (17 - level)) == 0)
			{
				ItemStack specimenStack = items.get(0);
				ItemStack cerateStack = items.get(1);
				Set<MutationAcceleratorRecipe> trees;
				// default result is dead bush
				ItemStack result = Items.DEAD_BUSH.getDefaultInstance();
				// 50% chance for dead bush. if there is no mutation tree for this world, dead bush.
				if (world.rand.nextDouble() < 0.5 && (trees = populateMutationTreeIfNeeded(world)) != null)
				{
					result = null;
					Item specimen = specimenStack.getItem();
					// iterate through the mutation trees until we find one that matches our specimen.
					for (MutationAcceleratorRecipe mar: trees)
						if ((result = mar.getCraftingResult(specimen, world.rand)) != null)
							break;
				}
				// if a result wasn't found, an item somehow ended up in the specimen slot which shouldn't have. there's
				// no defined result, so give them nothing...
				if (result == null)
					result = ItemStack.EMPTY;
				// decrease item stacks by one
				specimenStack = decrementItemStack(specimenStack);
				cerateStack = decrementItemStack(cerateStack);
				// overwrite the old item stacks with the new ones
				setInventorySlotContents(0, specimenStack);
				setInventorySlotContents(1, cerateStack);
				setInventorySlotContents(2, result);
				// send the "pop!" and sparkle effect to the client
				onItemMutated(level);
				// our inventory chaged, so mark dirty
				markDirty();
			}

			Item tempSpecimen;
			if ((tempSpecimen = items.get(0).getItem()) != specimen)
			{
				specimen = tempSpecimen;
				onSpecimenChanged();
			}
		}
	}

	private static ItemStack decrementItemStack(ItemStack stack)
	{
		stack.setCount(stack.getCount() - 1);
		return stack.isEmpty() ? ItemStack.EMPTY : stack;
	}

	private void onItemMutated(int level)
	{
		assert !world.isRemote;
		Chunk c = world.getChunkAt(pos);
		EOTNetworkChannel.send(PacketDistributor.TRACKING_CHUNK.with(() -> c),
				new SMutationAcceleratorMutationPacket(pos, level));
	}

	private void onSpecimenChanged()
	{
		assert !world.isRemote;
		Chunk c = world.getChunkAt(pos);
		EOTNetworkChannel.send(PacketDistributor.TRACKING_CHUNK.with(() -> c),
				new SMutationAcceleratorSpecimenChangePacket(pos, specimen));
	}

	public void onSpecimenChangedPacketReceived(Item item)
	{
		this.specimen = item;
	}

	public Item getSpecimen()
	{
		return this.specimen;
	}

	@Override
	public int[] getSlotsForFace(Direction face)
	{
		return face == Direction.DOWN ? DOWN_SLOTS : TOP_AND_SIDE_SLOTS;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, Direction face)
	{
		return isItemValidForSlot(index, stack);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction face)
	{
		// hoppers can only extract from the output slot
		return index == 2;
	}

	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
	{
		// prevent forge from handling hopper extraction....
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return LazyOptional.empty();
		return super.getCapability(cap, side);
	}
}
