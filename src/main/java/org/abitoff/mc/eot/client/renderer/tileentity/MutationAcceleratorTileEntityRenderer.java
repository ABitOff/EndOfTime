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
			GlStateManager.translatef((float) x + 0.5f, (float) y + 0.4375f + bob, (float) z + 0.5f);

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
			GlStateManager.translated(x + 0.5, y + 0.5, z + 0.5);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			float r = 1;
			float g = 1;
			float b = 1;
			float a = 0.625f;
			double radius = 0.375;
			double theta = (getWorld().getGameTime() + (double) partialTicks) / 50 + tileEntity.renderOffset;
			Vec3d p1 = new Vec3d(radius * Math.cos(theta), 0, radius * Math.sin(theta));
			Vec3d p2 = new Vec3d(-p1.z, 0, p1.x);
			Vec3d p3 = p1.scale(-1);
			Vec3d p4 = p2.scale(-1);
			GlStateManager.color4f(r, g, b, a);
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
			bufferbuilder.pos(0, 0, 0).endVertex();
			bufferbuilder.pos(p1.x, 0.5, p1.z).endVertex();
			bufferbuilder.pos(p2.x, 0.5, p2.z).endVertex();
			bufferbuilder.pos(0, 0, 0).endVertex();
			bufferbuilder.pos(0, 0, 0).endVertex();
			bufferbuilder.pos(p2.x, 0.5, p2.z).endVertex();
			bufferbuilder.pos(p3.x, 0.5, p3.z).endVertex();
			bufferbuilder.pos(0, 0, 0).endVertex();
			bufferbuilder.pos(0, 0, 0).endVertex();
			bufferbuilder.pos(p3.x, 0.5, p3.z).endVertex();
			bufferbuilder.pos(p4.x, 0.5, p4.z).endVertex();
			bufferbuilder.pos(0, 0, 0).endVertex();
			bufferbuilder.pos(0, 0, 0).endVertex();
			bufferbuilder.pos(p4.x, 0.5, p4.z).endVertex();
			bufferbuilder.pos(p1.x, 0.5, p1.z).endVertex();
			bufferbuilder.pos(0, 0, 0).endVertex();
			bufferbuilder.pos(p4.x, 0.5, p4.z).endVertex();
			bufferbuilder.pos(p3.x, 0.5, p3.z).endVertex();
			bufferbuilder.pos(p2.x, 0.5, p2.z).endVertex();
			bufferbuilder.pos(p1.x, 0.5, p1.z).endVertex();
			tessellator.draw();
			GlStateManager.popMatrix();
		}
	}

	public boolean isGlobalRenderer(MutationAcceleratorTileEntity te)
	{
		return true;
	}
}
