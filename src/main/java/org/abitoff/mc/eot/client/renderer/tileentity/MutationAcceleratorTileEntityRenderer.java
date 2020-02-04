package org.abitoff.mc.eot.client.renderer.tileentity;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.block.MutationAcceleratorBlock;
import org.abitoff.mc.eot.tileentity.MutationAcceleratorTileEntity;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

@SuppressWarnings("deprecation")
public class MutationAcceleratorTileEntityRenderer extends TileEntityRenderer<MutationAcceleratorTileEntity>
{
	private ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

	@Override
	public void render(MutationAcceleratorTileEntity tileEntity, double x, double y, double z, float partialTicks,
			int destroyStage)
	{
		ItemStack displayItem = ItemStack.EMPTY;
		if (tileEntity.getResult() != null)
			displayItem = tileEntity.getResult().getDefaultInstance();
		if (tileEntity.getSpecimen() != null && displayItem.isEmpty())
			displayItem = tileEntity.getSpecimen().getDefaultInstance();

		if (!displayItem.isEmpty())
		{
			float blockScale = 0.25f;
			float bobScale = 0.0075f;
			float invFreq = 50;
			float bob = (float) Math.sin((getWorld().getGameTime() + partialTicks) / invFreq + tileEntity.renderOffset)
					* bobScale;

			GlStateManager.pushMatrix();
			GlStateManager.translatef((float) x + 0.5f, (float) y + 0.40625f + bob, (float) z + 0.5f);

			GlStateManager.pushMatrix();
			GlStateManager.rotatef(-90, 1F, 0F, 0F);
			GlStateManager.scalef(blockScale, blockScale, blockScale);

			this.itemRenderer.renderItem(displayItem, ItemCameraTransforms.TransformType.FIXED);

			GlStateManager.popMatrix();
			GlStateManager.popMatrix();
		}
		if (MutationAcceleratorBlock.isActive(tileEntity.getBlockState()))
		{
			this.bindTexture(new ResourceLocation(Constants.MOD_ID,
					"textures/tileentity/mutation_accelerator_magnification.png"));
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.disableLighting();
			GlStateManager.translated(x + 0.5, y + 0.4375, z + 0.5);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			float r = 1;
			float g = 1;
			float b = 1;
			float a = 0.625f;
			double radius = 0.375;
			double theta = (getWorld().getGameTime() + (double) partialTicks) / 50 + tileEntity.renderOffset;
			double top = 0.5;
			// p1
			double p1x = radius * Math.cos(theta);
			double p1z = radius * Math.sin(theta);
			// p1 rotated 90 degrees
			double p2x = -p1z;
			double p2z = p1x;
			// p1 rotated 180 degrees
			double p3x = -p1x;
			double p3z = -p1z;
			// p1 rotated 270 degrees
			double p4x = p1z;
			double p4z = -p1x;
			GlStateManager.color4f(r, g, b, a);
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
			bufferbuilder.pos(0, 0, 0).endVertex();
			bufferbuilder.pos(p1x, top, p1z).endVertex();
			bufferbuilder.pos(p2x, top, p2z).endVertex();
			bufferbuilder.pos(0, 0, 0).endVertex();
			bufferbuilder.pos(0, 0, 0).endVertex();
			bufferbuilder.pos(p2x, top, p2z).endVertex();
			bufferbuilder.pos(p3x, top, p3z).endVertex();
			bufferbuilder.pos(0, 0, 0).endVertex();
			bufferbuilder.pos(0, 0, 0).endVertex();
			bufferbuilder.pos(p3x, top, p3z).endVertex();
			bufferbuilder.pos(p4x, top, p4z).endVertex();
			bufferbuilder.pos(0, 0, 0).endVertex();
			bufferbuilder.pos(0, 0, 0).endVertex();
			bufferbuilder.pos(p4x, top, p4z).endVertex();
			bufferbuilder.pos(p1x, top, p1z).endVertex();
			bufferbuilder.pos(0, 0, 0).endVertex();
			bufferbuilder.pos(p4x, top, p4z).endVertex();
			bufferbuilder.pos(p3x, top, p3z).endVertex();
			bufferbuilder.pos(p2x, top, p2z).endVertex();
			bufferbuilder.pos(p1x, top, p1z).endVertex();
			tessellator.draw();
			GlStateManager.popMatrix();
		}
	}

	public boolean isGlobalRenderer(MutationAcceleratorTileEntity te)
	{
		return true;
	}
}
