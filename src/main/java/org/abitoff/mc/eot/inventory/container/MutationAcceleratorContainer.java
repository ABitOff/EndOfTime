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
