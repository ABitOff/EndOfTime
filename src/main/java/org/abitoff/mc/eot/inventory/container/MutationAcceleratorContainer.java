package org.abitoff.mc.eot.inventory.container;

import org.abitoff.mc.eot.Constants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class MutationAcceleratorContainer extends Container
{
	@SuppressWarnings("unchecked")
	private static final ContainerType<MutationAcceleratorContainer> CONTAINER_TYPE =
			(ContainerType<MutationAcceleratorContainer>) new ContainerType<MutationAcceleratorContainer>(
					MutationAcceleratorContainer::new).setRegistryName(Constants.MUTATION_ACCELERATOR_CONTAINER);
	private static final int CONTAINER_START = 0;
	private static final int CONTAINER_SIZE = 3;
	private static final int INV_START = CONTAINER_START + CONTAINER_SIZE;
	private static final int INV_SIZE = 27;
	private static final int HOTBAR_START = INV_START + INV_SIZE;
	private static final int HOTBAR_SIZE = 9;
	private static final int TOTAL_SIZE = HOTBAR_START + HOTBAR_SIZE;

	private final IInventory mutationAcceleratorInv;

	private MutationAcceleratorContainer(int id, PlayerInventory playerInv)
	{
		this(id, playerInv, new Inventory(CONTAINER_SIZE));
	}

	public MutationAcceleratorContainer(int id, PlayerInventory playerInv, IInventory iInv)
	{
		super(CONTAINER_TYPE, id);
		this.mutationAcceleratorInv = iInv;
		this.addSlot(new MASlot(iInv, 0, 56, 17));
		this.addSlot(new MASlot(iInv, 1, 56, 53));
		this.addSlot(new MASlot(iInv, 2, 116, 35));

		int invRows = 3;
		int invCols = 9;
		int slotWidth = 18;
		int slotHeight = 18;
		int hOffset = 8;
		int yOffset = 84;
		for (int y = 0; y < invRows; ++y)
		{
			for (int x = 0; x < invCols; ++x)
			{
				this.addSlot(
						new Slot(playerInv, x + (y + 1) * invCols, hOffset + x * slotWidth, yOffset + y * slotHeight));
			}
		}

		yOffset = 142;
		for (int x = 0; x < invCols; ++x)
		{
			this.addSlot(new Slot(playerInv, x, hOffset + x * slotWidth, yOffset));
		}
	}

	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
	{
		Slot slot = this.inventorySlots.get(index);
		if (slot == null || !slot.getHasStack())
			return ItemStack.EMPTY;

		ItemStack stack = slot.getStack();
		ItemStack old = stack.copy();
		if (index < CONTAINER_SIZE)
		{
			if (!mergeIntoInvAndHotbar(stack))
				return ItemStack.EMPTY;
		} else
		{
			boolean merged = false;
			for (int i = 0; i < CONTAINER_SIZE; i++)
			{
				if (mutationAcceleratorInv.isItemValidForSlot(i, stack))
				{
					int count = stack.getCount();
					if (!mergeItemStack(stack, i, i + 1, false))
						return ItemStack.EMPTY;
					merged = stack.getCount() != count;
					break;
				}
			}
			if (!merged)
			{
				if (index >= CONTAINER_SIZE && index < HOTBAR_START)
				{
					if (!mergeFromInvToHotbar(stack))
						return ItemStack.EMPTY;
				} else if (index >= HOTBAR_START && index < TOTAL_SIZE)
				{
					if (!mergeFromHotbarToInv(stack))
						return ItemStack.EMPTY;
				}
			}
		}

		if (stack.isEmpty())
			slot.putStack(ItemStack.EMPTY);
		else
			slot.onSlotChanged();

		if (stack.getCount() == old.getCount())
			return ItemStack.EMPTY;

		slot.onTake(playerIn, stack);

		return old;
	}

	/**
	 * @return true if the stack has items remaining, false otherwise.
	 */
	private boolean mergeIntoInvAndHotbar(ItemStack stack)
	{
		return mergeItemStack(stack, INV_START, TOTAL_SIZE, true);
	}

	/**
	 * @return true if the stack has items remaining, false otherwise.
	 */
	private boolean mergeFromHotbarToInv(ItemStack stack)
	{
		return mergeItemStack(stack, INV_START, HOTBAR_START, false);
	}

	/**
	 * @return true if the stack has items remaining, false otherwise.
	 */
	private boolean mergeFromInvToHotbar(ItemStack stack)
	{
		return mergeItemStack(stack, HOTBAR_START, TOTAL_SIZE, false);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn)
	{
		return mutationAcceleratorInv.isUsableByPlayer(playerIn);
	}

	public static ContainerType<MutationAcceleratorContainer> getContainerType()
	{
		return CONTAINER_TYPE;
	}

	private static final class MASlot extends Slot
	{
		public MASlot(IInventory inv, int index, int x, int y)
		{
			super(inv, index, x, y);
		}

		public boolean isItemValid(ItemStack stack)
		{
			return inventory.isItemValidForSlot(getSlotIndex(), stack);
		}
	}
}
