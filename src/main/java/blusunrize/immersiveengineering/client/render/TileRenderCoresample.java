/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.client.render;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.blocks.stone.TileEntityCoresample;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.EnumFacing;

public class TileRenderCoresample extends TileEntityRenderer<TileEntityCoresample>
{
	@Override
	public void render(TileEntityCoresample tile, double x, double y, double z, float partialTicks, int destroyStage)
	{
		if(!tile.getWorld().isBlockLoaded(tile.getPos(), false)||tile.coresample==null)
			return;

		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translated(x+.5, y+.54864, z+.52903);
		GlStateManager.rotatef(tile.facing==EnumFacing.NORTH?180: tile.facing==EnumFacing.WEST?-90: tile.facing==EnumFacing.EAST?90: 0, 0, 1, 0);
		GlStateManager.rotatef(-45, 1, 0, 0);
		ClientUtils.mc().getItemRenderer().renderItem(tile.coresample, TransformType.FIXED);
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}
}