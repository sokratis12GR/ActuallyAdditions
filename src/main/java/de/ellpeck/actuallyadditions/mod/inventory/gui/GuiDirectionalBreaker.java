/*
 * This file ("GuiDirectionalBreaker.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.inventory.gui;

import de.ellpeck.actuallyadditions.mod.inventory.ContainerDirectionalBreaker;
import de.ellpeck.actuallyadditions.mod.tile.TileEntityBase;
import de.ellpeck.actuallyadditions.mod.tile.TileEntityDirectionalBreaker;
import de.ellpeck.actuallyadditions.mod.util.AssetUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;

@SideOnly(Side.CLIENT)
public class GuiDirectionalBreaker extends GuiContainer{

    private static final ResourceLocation resLoc = AssetUtil.getGuiLocation("guiDirectionalBreaker");
    private final TileEntityDirectionalBreaker breaker;

    public GuiDirectionalBreaker(InventoryPlayer inventory, TileEntityBase tile){
        super(new ContainerDirectionalBreaker(inventory, tile));
        this.breaker = (TileEntityDirectionalBreaker)tile;
        this.xSize = 176;
        this.ySize = 93+86;
    }

    @Override
    public void drawScreen(int x, int y, float f){
        super.drawScreen(x, y, f);

        String text1 = this.breaker.storage.getEnergyStored()+"/"+this.breaker.storage.getMaxEnergyStored()+" RF";
        if(x >= this.guiLeft+43 && y >= this.guiTop+6 && x <= this.guiLeft+58 && y <= this.guiTop+88){
            this.drawHoveringText(Collections.singletonList(text1), x, y);
        }
    }

    @Override
    public void drawGuiContainerForegroundLayer(int x, int y){
        AssetUtil.displayNameString(this.fontRendererObj, this.xSize, -10, this.breaker.getName());
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float f, int x, int y){
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(AssetUtil.GUI_INVENTORY_LOCATION);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop+93, 0, 0, 176, 86);

        this.mc.getTextureManager().bindTexture(resLoc);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 176, 93);

        if(this.breaker.storage.getEnergyStored() > 0){
            int i = this.breaker.getEnergyScaled(83);
            this.drawTexturedModalRect(this.guiLeft+43, this.guiTop+89-i, 176, 29, 16, i);
        }
    }
}