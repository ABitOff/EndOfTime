package org.abitoff.mc.eot.client.gui.screen.inventory;

import org.abitoff.mc.eot.Constants;
import org.abitoff.mc.eot.inventory.container.MutationAcceleratorContainer;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class MutationAcceleratorScreen extends ContainerScreen<MutationAcceleratorContainer>
		implements IHasContainer<MutationAcceleratorContainer>
{
	private static final ResourceLocation TEXTURE;
	private int tick = 0;

	static
	{
		ResourceLocation rl = new ResourceLocation(Constants.MUTATION_ACCELERATOR_RL);
		TEXTURE = new ResourceLocation(rl.getNamespace(), "textures/gui/container/" + rl.getPath() + ".png");
	}

	public MutationAcceleratorScreen(MutationAcceleratorContainer screenContainer, PlayerInventory inv,
			ITextComponent titleIn)
	{
		super(screenContainer, inv, titleIn);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String s = this.title.getFormattedText();
		this.font.drawString(s, (float) (this.xSize / 2 - this.font.getStringWidth(s) / 2), 6.0F, 4210752);
		this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F,
				(float) (this.ySize - 96 + 2), 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		blit(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);

		int x = guiLeft + 57;
		int y = guiTop + 36;
		int u = 194;
		float v = (tick / 1.5f) % 16f + partialTicks;
		int w = 14;
		int h = 14;
		{
			double x1 = x;
			double y1 = y;
			double x2 = x + w;
			double y2 = y + h;
			double u1 = u / 256f;
			double u2 = (u + w) / 256f;
			double v1 = v / 256f;
			double v2 = (v + h) / 256f;

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
			bufferbuilder.pos(x1, y2, this.blitOffset).tex(u1, v2).endVertex();
			bufferbuilder.pos(x2, y2, this.blitOffset).tex(u2, v2).endVertex();
			bufferbuilder.pos(x2, y1, this.blitOffset).tex(u2, v1).endVertex();
			bufferbuilder.pos(x1, y1, this.blitOffset).tex(u1, v1).endVertex();
			tessellator.draw();
		}

		blit(guiLeft + 55, guiTop + 16, 176, 0, 18, 54);
	}

	public void tick()
	{
		super.tick();
		tick++;
	}
}
