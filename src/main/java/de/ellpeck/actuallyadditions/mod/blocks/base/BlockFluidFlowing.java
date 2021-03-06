/*
 * This file ("BlockFluidFlowing.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.blocks.base;


import de.ellpeck.actuallyadditions.mod.util.ItemUtil;
import de.ellpeck.actuallyadditions.mod.util.PosUtil;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidFlowing extends BlockFluidClassic{

    private final String name;

    public BlockFluidFlowing(Fluid fluid, Material material, String unlocalizedName){
        super(fluid, material);
        this.name = unlocalizedName;
        this.displacements.put(this, false);

        this.register();
    }

    private void register(){
        ItemUtil.registerBlock(this, this.getItemBlock(), this.getBaseName(), this.shouldAddCreative());
    }

    protected String getBaseName(){
        return this.name;
    }

    protected ItemBlockBase getItemBlock(){
        return new ItemBlockBase(this);
    }

    public boolean shouldAddCreative(){
        return false;
    }

    @Override
    public boolean canDisplace(IBlockAccess world, BlockPos pos){
        return !PosUtil.getMaterial(pos, world).isLiquid() && super.canDisplace(world, pos);
    }

    @Override
    public boolean displaceIfPossible(World world, BlockPos pos){
        return !PosUtil.getMaterial(pos, world).isLiquid() && super.displaceIfPossible(world, pos);
    }

    public EnumRarity getRarity(ItemStack stack){
        return EnumRarity.EPIC;
    }
}
