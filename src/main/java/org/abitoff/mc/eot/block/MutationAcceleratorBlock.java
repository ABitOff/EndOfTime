package org.abitoff.mc.eot.block;

import java.util.Random;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.tileentity.MutationAcceleratorTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MutationAcceleratorBlock extends ContainerBlock
{
	private static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_0_15;
	private static final BooleanProperty LIT = BlockStateProperties.LIT;
	private static final MutationAcceleratorBlock INSTANCE = (MutationAcceleratorBlock) new MutationAcceleratorBlock(
			Properties.create(Material.ROCK, MaterialColor.SAND).hardnessAndResistance(0.8f))
					.setRegistryName(Constants.MUTATION_ACCELERATOR_RL);

	protected MutationAcceleratorBlock(Properties builder)
	{
		super(builder);
		this.setDefaultState(
				this.stateContainer.getBaseState().with(LEVEL, Integer.valueOf(0)).with(LIT, Boolean.valueOf(false)));
	}

	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		// hack to make light pass through the sides, but not the bottom. also allows the sky light level at the same
		// position as the block to be 15. otherwise it would be 14, making calculating LEVEL awkward.
		return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 15.99999D, 16.0D);
	}

	public boolean func_220074_n(BlockState state)
	{
		return true;
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

	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if (stack.hasDisplayName())
		{
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof BeaconTileEntity)
			{
				((MutationAcceleratorTileEntity) tileentity).setCustomName(stack.getDisplayName());
			}
		}

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

	@SuppressWarnings("deprecation")
	public void onReplaced(BlockState oldState, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if (oldState.getBlock() != newState.getBlock())
		{
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof MutationAcceleratorTileEntity)
			{
				InventoryHelper.dropInventoryItems(worldIn, pos, (MutationAcceleratorTileEntity) tileentity);
				worldIn.updateComparatorOutputLevel(pos, this);
			}
			super.onReplaced(oldState, worldIn, pos, newState, isMoving);
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

	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	public boolean isSolid(BlockState state)
	{
		return true;
	}

	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos)
	{
		return false;
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(LEVEL, LIT);
	}

	public static MutationAcceleratorBlock get()
	{
		return INSTANCE;
	}

	public static void updateLevel(BlockState state, World world, BlockPos pos)
	{
		if (world.dimension.hasSkyLight())
		{
			int lightLevel = world.getLightFor(LightType.SKY, pos) - world.getSkylightSubtracted();
			float sunAngle = world.getCelestialAngleRadians(1);
			if (lightLevel > 0)
			{
				float f1 = sunAngle < Math.PI ? 0 : (float) Math.PI * 2;
				sunAngle = sunAngle + (f1 - sunAngle) * 0.2f;
				lightLevel = Math.round((float) lightLevel * MathHelper.cos(sunAngle));
			}

			lightLevel = MathHelper.clamp(lightLevel, 0, 15);
			if (state.get(LEVEL) != lightLevel)
			{
				world.setBlockState(pos, state.with(LEVEL, lightLevel), 3);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void onItemMutated(ClientWorld world, BlockPos pos)
	{
		if (world.getChunk(pos.getX() >> 4, pos.getZ() >> 4, null, false) == null)
			return;
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() != INSTANCE)
			return;
		Random rand = world.getRandom();
		int level = state.get(LEVEL);
		if (level > 0)
		{
			float volume = MathHelper.lerp(level / 15f, 0.125f, 1.125f);
			float pitch = MathHelper.lerp(level / 15f, 0.625f, 1.125f);
			world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST_FAR,
					SoundCategory.BLOCKS, volume, pitch, false);
			int n = level == 1 ? 1 : 1 + rand.nextInt((int) Math.ceil(level / 3d));
			for (int i = 0; i < n; i++)
			{
				double x = rand.nextDouble() / 4d + 0.375;
				double z = rand.nextDouble() / 4d + 0.375;
				world.addParticle(ParticleTypes.FIREWORK, pos.getX() + x, pos.getY() + 0.5, pos.getZ() + z,
						(x - 0.5) / 10d, (level + 4d) / 200d, (z - 0.5) / 10d);
			}
		}
	}

	public static void flipState(BlockState state, World world, BlockPos pos)
	{
		world.setBlockState(pos, state.with(LIT, !state.get(LIT)), 3);
	}

	public static boolean isLit(BlockState state)
	{
		return state.get(LIT);
	}
}
