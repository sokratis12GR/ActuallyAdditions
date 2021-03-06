/*
 * This file ("ItemBlockBase.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.blocks.base;

import de.ellpeck.actuallyadditions.mod.util.Util;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemBlockBase extends ItemBlock{

    public ItemBlockBase(Block block){
        super(block);
        this.setHasSubtypes(false);
        this.setMaxDamage(0);
    }


    @Override
    public String getUnlocalizedName(ItemStack stack){
        return this.getUnlocalizedName();
    }

    @Override
    public int getMetadata(int damage){
        return damage;
    }


    @Override
    public EnumRarity getRarity(ItemStack stack){
        if(this.block instanceof BlockBase){
            return ((BlockBase)this.block).getRarity(stack);
        }
        else if(this.block instanceof BlockContainerBase){
            return ((BlockContainerBase)this.block).getRarity(stack);
        }
        else if(this.block instanceof BlockFluidFlowing){
            return ((BlockFluidFlowing)this.block).getRarity(stack);
        }
        else if(this.block instanceof BlockPlant){
            return ((BlockPlant)this.block).getRarity(stack);
        }
        else if(this.block instanceof BlockStair){
            return ((BlockStair)this.block).getRarity(stack);
        }
        else if(this.block instanceof BlockBushBase){
            return ((BlockBushBase)this.block).getRarity(stack);
        }
        else{
            return Util.FALLBACK_RARITY;
        }
    }
}
