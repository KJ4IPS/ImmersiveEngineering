/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.client.render;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.blocks.IEBlocks.MetalMultiblocks;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMixer;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class TileRenderMixer extends TileEntityRenderer<TileEntityMixer>
{
	@Override
	public void render(TileEntityMixer te, double x, double y, double z, float partialTicks, int destroyStage)
	{
		if(!te.formed||te.isDummy()||!te.getWorld().isBlockLoaded(te.getPos(), false))
			return;

		final BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
		BlockPos blockPos = te.getPos();
		IBlockState state = getWorld().getBlockState(blockPos);
		if(state.getBlock()!=MetalMultiblocks.mixer)
			return;
		state = state.with(IEProperties.DYNAMICRENDER, true);
		IBakedModel model = blockRenderer.getBlockModelShapes().getModel(state);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder worldRenderer = tessellator.getBuffer();

		ClientUtils.bindAtlas();
		GlStateManager.pushMatrix();
		GlStateManager.translated(x+.5, y+.5, z+.5);

		if(te.mirrored)
			GlStateManager.scalef(te.facing.getXOffset()==0?-1: 1, 1, te.facing.getZOffset()==0?-1: 1);

		GlStateManager.pushMatrix();
		GlStateManager.translated(te.facing==EnumFacing.SOUTH||te.facing==EnumFacing.WEST?-.5: .5, 0, te.facing==EnumFacing.SOUTH||te.facing==EnumFacing.EAST?.5: -.5);
		float agitator = te.animation_agitator-(!te.shouldRenderAsActive()?0: (1-partialTicks)*9f);
		GlStateManager.rotatef(agitator, 0, 1, 0);

		RenderHelper.disableStandardItemLighting();
		GlStateManager.blendFunc(770, 771);
		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		if(Minecraft.isAmbientOcclusionEnabled())
			GlStateManager.shadeModel(7425);
		else
			GlStateManager.shadeModel(7424);
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		worldRenderer.setTranslation(-.5-blockPos.getX(), -.5-blockPos.getY(), -.5-blockPos.getZ());
		worldRenderer.color(255, 255, 255, 255);
		blockRenderer.getBlockModelRenderer().renderModel(te.getWorld(), model, state, blockPos, worldRenderer, true,
				Utils.RAND, 0, EmptyModelData.INSTANCE);
		worldRenderer.setTranslation(0.0D, 0.0D, 0.0D);
		tessellator.draw();
		RenderHelper.enableStandardItemLighting();

		GlStateManager.popMatrix();

		switch(te.facing)
		{
			case NORTH:
				break;
			case SOUTH:
				GlStateManager.rotatef(180, 0, 1, 0);
				break;
			case WEST:
				GlStateManager.rotatef(90, 0, 1, 0);
				break;
			case EAST:
				GlStateManager.rotatef(-90, 0, 1, 0);
				break;
		}

		GlStateManager.scalef(.0625f, 1, .0625f);
		GlStateManager.rotatef(90, 1, 0, 0);
		GlStateManager.translated(8, -8, .625f);

		RenderHelper.disableStandardItemLighting();

		for(int i = te.tank.getFluidTypes()-1; i >= 0; i--)
		{
			FluidStack fs = te.tank.fluids.get(i);
			if(fs!=null&&fs.getFluid()!=null)
			{
				int col = fs.getFluid().getColor(fs);
				GlStateManager.color3f((col >> 16&255)/255.0f, (col >> 8&255)/255.0f, (col&255)/255.0f);

				float yy = fs.amount/(float)te.tank.getCapacity()*1.125f;
				GlStateManager.translated(0, 0, -yy);
				float w = (i < te.tank.getFluidTypes()-1||yy >= .125)?26: 16+yy/.0125f;
				ClientUtils.drawRepeatedFluidSprite(fs, -w/2, -w/2, w, w);
			}
		}

		GlStateManager.popMatrix();
	}
}