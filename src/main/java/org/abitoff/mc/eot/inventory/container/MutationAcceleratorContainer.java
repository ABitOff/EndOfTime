package org.abitoff.mc.eot.inventory.container;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.items.MutativeCerateItem;
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

	private final IInventory mutationAcceleratorInv;

	private MutationAcceleratorContainer(int id, PlayerInventory playerInv)
	{
		this(id, playerInv, new Inventory(3));
	}

	public MutationAcceleratorContainer(int id, PlayerInventory playerInv, IInventory mutationAcceleratorInv)
	{
		super(CONTAINER_TYPE, id);
		this.mutationAcceleratorInv = mutationAcceleratorInv;
		this.addSlot(new Slot(mutationAcceleratorInv, 0, 56, 17));
		this.addSlot(new Slot(mutationAcceleratorInv, 1, 56, 53)
		{
			public boolean isItemValid(ItemStack stack)
			{
				return stack.getItem() == MutativeCerateItem.get();
			}
		});
		this.addSlot(new Slot(mutationAcceleratorInv, 2, 116, 35)
		{
			public boolean isItemValid(ItemStack stack)
			{
				return true;// false;
			}
		});

		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k)
		{
			this.addSlot(new Slot(playerInv, k, 8 + k * 18, 142));
		}
	}

	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (index == 2)
			{
				if (!this.mergeItemStack(itemstack1, 3, 39, true))
				{
					return ItemStack.EMPTY;
				}

				slot.onSlotChange(itemstack1, itemstack);
			} else if (index != 1 && index != 0)
			{
				if (itemstack1.getItem() == MutativeCerateItem.get())
				{
					if (!this.mergeItemStack(itemstack1, 1, 2, false))
					{
						return ItemStack.EMPTY;
					}
				} else if (!this.mergeItemStack(itemstack1, 0, 1, false))
				{
					return ItemStack.EMPTY;
				} else if (index >= 3 && index < 30)
				{
					if (!this.mergeItemStack(itemstack1, 30, 39, false))
					{
						return ItemStack.EMPTY;
					}
				} else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false))
				{
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 3, 39, false))
			{
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty())
			{
				slot.putStack(ItemStack.EMPTY);
			} else
			{
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount())
			{
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, itemstack1);
		}

		return itemstack;
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
}
