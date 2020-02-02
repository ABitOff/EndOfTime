package org.abitoff.mc.eot.client.renderer.tileentity;

import org.abitoff.mc.eot.tileentity.MutationAcceleratorTileEntity;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class MutationAcceleratorTileEntityRenderer extends TileEntityRenderer<MutationAcceleratorTileEntity>
{
	@Override
	public void render(MutationAcceleratorTileEntity tileEntityIn, double x, double y, double z, float partialTicks,
			int destroyStage)
	{
		// super.render(tileEntityIn, x, y, z, partialTicks, destroyStage);
		renderBeamSegment(x, y, z, partialTicks, 1, getWorld().getGameTime(), 0, 1, new float[] {1, 0, 0}, 0.2, 0.25);
	}

	public void renderBeamSegment(double x, double y, double z, double partialTicks, double textureScale,
			long totalWorldTime, int yOffset, int height, float[] colors, double beamRadius, double glowRadius)
	{
		this.bindTexture(new ResourceLocation("textures/particle/generic_0"));
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.pushMatrix();
		GlStateManager.translated(x + 0.5, y, z + 0.5);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		double d0 = Math.floorMod(totalWorldTime, 40) + partialTicks;
		float r = colors[0];
		float g = colors[1];
		float b = colors[2];
		GlStateManager.pushMatrix();
		GlStateManager.rotated(d0 * 2.25 - 45, 0, 1, 0);
		double d12 = -1;
		double d13 = 1;
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(0, 0.5, 0).tex(0, d12).color(r, g, b, 1).endVertex();
		bufferbuilder.pos(beamRadius, height, 0).tex(1, d12).color(r, g, b, 1).endVertex();
		bufferbuilder.pos(0, height, beamRadius).tex(0, d13).color(r, g, b, 1).endVertex();
		bufferbuilder.pos(0, 0.5, 0).tex(1, d13).color(r, g, b, 1).endVertex();
		tessellator.draw();
		GlStateManager.popMatrix();
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture();
		GlStateManager.depthMask(true);
	}

	public boolean isGlobalRenderer(MutationAcceleratorTileEntity te)
	{
		return true;
	}
}
