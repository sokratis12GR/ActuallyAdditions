/*
 * This file ("ItemCrystal.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.items;

import de.ellpeck.actuallyadditions.mod.ActuallyAdditions;
import de.ellpeck.actuallyadditions.mod.blocks.BlockCrystal;
import de.ellpeck.actuallyadditions.mod.items.base.ItemBase;
import de.ellpeck.actuallyadditions.mod.util.StringUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemCrystal extends ItemBase{

    public ItemCrystal(String name){
        super(name);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public int getMetadata(int damage){
        return damage;
    }


    @Override
    public String getUnlocalizedName(ItemStack stack){
        return stack.getItemDamage() >= BlockCrystal.allCrystals.length ? StringUtil.BUGGED_ITEM_NAME : this.getUnlocalizedName()+BlockCrystal.allCrystals[stack.getItemDamage()].name;
    }


    @Override
    public EnumRarity getRarity(ItemStack stack){
        return stack.getItemDamage() >= BlockCrystal.allCrystals.length ? EnumRarity.COMMON : BlockCrystal.allCrystals[stack.getItemDamage()].rarity;
    }

    @SuppressWarnings("all")
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list){
        for(int j = 0; j < BlockCrystal.allCrystals.length; j++){
            list.add(new ItemStack(this, 1, j));
        }
    }

    @Override
    protected void registerRendering(){
        for(int i = 0; i < BlockCrystal.allCrystals.length; i++){
            String name = this.getRegistryName()+BlockCrystal.allCrystals[i].name;
            ActuallyAdditions.proxy.addRenderRegister(new ItemStack(this, 1, i), new ModelResourceLocation(name, "inventory"));
        }
    }
}