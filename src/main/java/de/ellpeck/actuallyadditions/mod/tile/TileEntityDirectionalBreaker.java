/*
 * This file ("TileEntityDirectionalBreaker.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.tile;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import de.ellpeck.actuallyadditions.mod.config.ConfigValues;
import de.ellpeck.actuallyadditions.mod.util.PosUtil;
import de.ellpeck.actuallyadditions.mod.util.Util;
import de.ellpeck.actuallyadditions.mod.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class TileEntityDirectionalBreaker extends TileEntityInventoryBase implements IEnergyReceiver, IEnergySaver, IRedstoneToggle{

    public static final int RANGE = 8;
    public static final int ENERGY_USE = 5;
    public final EnergyStorage storage = new EnergyStorage(10000);
    private int lastEnergy;
    private int currentTime;
    private boolean activateOnceWithSignal;

    public TileEntityDirectionalBreaker(){
        super(9, "directionalBreaker");
    }

    @Override
    public void writeSyncableNBT(NBTTagCompound compound, boolean sync){
        super.writeSyncableNBT(compound, sync);
        this.storage.writeToNBT(compound);
        compound.setInteger("CurrentTime", this.currentTime);
    }

    @Override
    public void readSyncableNBT(NBTTagCompound compound, boolean sync){
        super.readSyncableNBT(compound, sync);
        this.storage.readFromNBT(compound);
        this.currentTime = compound.getInteger("CurrentTime");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateEntity(){
        super.updateEntity();
        if(!this.worldObj.isRemote){
            if(!this.isRedstonePowered && !this.activateOnceWithSignal){
                if(this.currentTime > 0){
                    this.currentTime--;
                    if(this.currentTime <= 0){
                        this.doWork();
                    }
                }
                else{
                    this.currentTime = 15;
                }
            }

            if(this.storage.getEnergyStored() != this.lastEnergy && this.sendUpdateWithInterval()){
                this.lastEnergy = this.storage.getEnergyStored();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void doWork(){
        if(this.storage.getEnergyStored() >= ENERGY_USE*RANGE){
            EnumFacing sideToManipulate = WorldUtil.getDirectionByPistonRotation(PosUtil.getMetadata(this.pos, this.worldObj));

            for(int i = 0; i < RANGE; i++){
                BlockPos coordsBlock = WorldUtil.getCoordsFromSide(sideToManipulate, this.pos, i);
                Block blockToBreak = PosUtil.getBlock(coordsBlock, this.worldObj);
                if(blockToBreak != null && !(blockToBreak instanceof BlockAir) && blockToBreak.getBlockHardness(this.worldObj.getBlockState(coordsBlock), this.worldObj, this.pos) > -1.0F){
                    List<ItemStack> drops = blockToBreak.getDrops(this.worldObj, coordsBlock, this.worldObj.getBlockState(coordsBlock), 0);
                    float chance = ForgeEventFactory.fireBlockHarvesting(drops, this.worldObj, coordsBlock, this.worldObj.getBlockState(coordsBlock), 0, 1, false, null);

                    if(Util.RANDOM.nextFloat() <= chance){
                        if(WorldUtil.addToInventory(this, drops, false, true)){
                            if(!ConfigValues.lessBlockBreakingEffects){
                                this.worldObj.playBroadcastSound(2001, coordsBlock, Block.getStateId(this.worldObj.getBlockState(coordsBlock)));
                            }
                            WorldUtil.breakBlockAtSide(sideToManipulate, this.worldObj, this.getPos(), i);
                            WorldUtil.addToInventory(this, drops, true, true);
                            this.storage.extractEnergy(ENERGY_USE, false);
                            this.markDirty();
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack){
        return false;
    }

    @SideOnly(Side.CLIENT)
    public int getEnergyScaled(int i){
        return this.storage.getEnergyStored()*i/this.storage.getMaxEnergyStored();
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side){
        return this.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side){
        return true;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate){
        return this.storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from){
        return this.storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from){
        return this.storage.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from){
        return true;
    }

    @Override
    public int getEnergy(){
        return this.storage.getEnergyStored();
    }

    @Override
    public void setEnergy(int energy){
        this.storage.setEnergyStored(energy);
    }

    @Override
    public void toggle(boolean to){
        this.activateOnceWithSignal = to;
    }

    @Override
    public boolean isPulseMode(){
        return this.activateOnceWithSignal;
    }

    @Override
    public void activateOnPulse(){
        this.doWork();
    }
}
