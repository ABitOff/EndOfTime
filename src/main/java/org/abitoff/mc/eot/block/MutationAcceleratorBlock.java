package org.abitoff.mc.eot.block;

import java.util.Random;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.tileentity.MutationAcceleratorTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MutationAcceleratorBlock extends ContainerBlock
{
	private static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_0_15;
	private static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	private static final MutationAcceleratorBlock INSTANCE =
			(MutationAcceleratorBlock) new MutationAcceleratorBlock(Properties.create(Material.ROCK, MaterialColor.SAND)
					.hardnessAndResistance(0.8f).lightValue(15).tickRandomly())
							.setRegistryName(Constants.MUTATION_ACCELERATOR_RL);

	protected MutationAcceleratorBlock(Properties builder)
	{
		super(builder);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn)
	{
		return this.createTileEntity(null, worldIn);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new MutationAcceleratorTileEntity();
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	public int getLightValue(BlockState state)
	{
		return state.get(LEVEL);
	}

	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
			BlockRayTraceResult hit)
	{
		if (!worldIn.isRemote)
		{
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof MutationAcceleratorTileEntity)
			{
				player.openContainer((MutationAcceleratorTileEntity) tileentity);
			}
		}
		return true;
	}

	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@SuppressWarnings("deprecation")
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if (state.getBlock() != newState.getBlock())
		{
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof MutationAcceleratorTileEntity)
			{
				InventoryHelper.dropInventoryItems(worldIn, pos, (MutationAcceleratorTileEntity) tileentity);
				worldIn.updateComparatorOutputLevel(pos, this);
			}
			super.onReplaced(state, worldIn, pos, newState, isMoving);
		}
	}

	public boolean hasComparatorInputOverride(BlockState state)
	{
		return true;
	}

	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos)
	{
		return Container.calcRedstone(worldIn.getTileEntity(pos));
	}

	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}

	public BlockState rotate(BlockState state, Rotation rot)
	{
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn)
	{
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, LEVEL);
	}

	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
	{
		int level = stateIn.get(LEVEL);
		if (stateIn.get(LEVEL) > 0)
		{
			worldIn.addParticle(ParticleTypes.MYCELIUM, pos.getX() + rand.nextDouble(), pos.getY() + 1.1,
					pos.getZ() + rand.nextDouble(), 0.1 * level - 0.1, 0.1 * level - 0.1, 0.1 * level - 0.1);
		}
	}

	public void tick(BlockState state, World worldIn, BlockPos pos, Random random)
	{
		
	}

	public void randomTick(BlockState state, World worldIn, BlockPos pos, Random random)
	{

	}

	public static MutationAcceleratorBlock get()
	{
		return INSTANCE;
	}
}
