/*
 * This file ("TileEntityAtomicReconstructor.java") is part of the Actually Additions Mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense/
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.tile;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import de.ellpeck.actuallyadditions.api.Position;
import de.ellpeck.actuallyadditions.api.internal.IAtomicReconstructor;
import de.ellpeck.actuallyadditions.api.lens.ILensItem;
import de.ellpeck.actuallyadditions.api.lens.Lens;
import de.ellpeck.actuallyadditions.api.tile.IEnergyDisplay;
import de.ellpeck.actuallyadditions.mod.items.lens.Lenses;
import de.ellpeck.actuallyadditions.mod.network.PacketHandler;
import de.ellpeck.actuallyadditions.mod.network.PacketParticle;
import de.ellpeck.actuallyadditions.mod.util.ModUtil;
import de.ellpeck.actuallyadditions.mod.util.WorldUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityAtomicReconstructor extends TileEntityInventoryBase implements IEnergyReceiver, IEnergySaver, IRedstoneToggle, IEnergyDisplay, IAtomicReconstructor{

    public static final int ENERGY_USE = 1000;
    public EnergyStorage storage = new EnergyStorage(300000);
    private int currentTime;
    private boolean activateOnceWithSignal;
    private int oldEnergy;

    public TileEntityAtomicReconstructor(){
        super(1, "reconstructor");
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
                    this.currentTime = 100;
                }
            }

            if(this.oldEnergy != this.storage.getEnergyStored() && this.sendUpdateWithInterval()){
                this.oldEnergy = this.storage.getEnergyStored();
            }
        }

    }

    private void doWork(){
        if(this.storage.getEnergyStored() >= ENERGY_USE){
            Position thisPos = Position.fromTileEntity(this);
            EnumFacing sideToManipulate = WorldUtil.getDirectionByPistonRotation(thisPos.getMetadata(worldObj));
            //Extract energy for shooting the laser itself too!
            this.storage.extractEnergy(ENERGY_USE, false);

            //The Lens the Reconstructor currently has installed
            Lens currentLens = this.getCurrentLens();
            int distance = currentLens.getDistance();
            for(int i = 0; i < distance; i++){
                Position hitBlock = WorldUtil.getCoordsFromSide(sideToManipulate, thisPos, i);

                if(currentLens.invoke(hitBlock, this)){
                    this.shootLaser(hitBlock.getX(), hitBlock.getY(), hitBlock.getZ(), currentLens);
                    break;
                }
                else if(i >= distance-1){
                    this.shootLaser(hitBlock.getX(), hitBlock.getY(), hitBlock.getZ(), currentLens);
                }
            }
        }
    }

    public Lens getCurrentLens(){
        if(this.slots[0] != null){
            if(this.slots[0].getItem() instanceof ILensItem){
                return ((ILensItem)this.slots[0].getItem()).getLens();
            }
        }
        return Lenses.LENS_NONE;
    }

    private void shootLaser(int endX, int endY, int endZ, Lens currentLens){
        this.worldObj.playSoundEffect(this.getX(), this.getY(), this.getZ(), ModUtil.MOD_ID_LOWER+":reconstructor", 0.35F, 1.0F);
        PacketHandler.theNetwork.sendToAllAround(new PacketParticle(this.getX(), this.getY(), this.getZ(), endX, endY, endZ, currentLens.getColor(), 8, 2F), new NetworkRegistry.TargetPoint(worldObj.provider.getDimensionId(), this.getX(), this.getY(), this.getZ(), 64));
    }

    @Override
    public void writeSyncableNBT(NBTTagCompound compound, boolean sync){
        super.writeSyncableNBT(compound, sync);
        compound.setInteger("CurrentTime", this.currentTime);
        this.storage.writeToNBT(compound);
    }

    @Override
    public boolean shouldSyncSlots(){
        return true;
    }

    @Override
    public void readSyncableNBT(NBTTagCompound compound, boolean sync){
        super.readSyncableNBT(compound, sync);
        this.currentTime = compound.getInteger("CurrentTime");
        this.storage.readFromNBT(compound);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack stack){
        super.setInventorySlotContents(i, stack);
        this.sendUpdate();
    }

    @Override
    public ItemStack decrStackSize(int i, int j){
        this.sendUpdate();
        return super.decrStackSize(i, j);
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
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side){
        return this.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack){
        return stack != null && stack.getItem() instanceof ILensItem;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side){
        return true;
    }

    @Override
    public int getX(){
        return this.getPos().getX();
    }

    @Override
    public int getY(){
        return this.getPos().getY();
    }

    @Override
    public int getZ(){
        return this.getPos().getZ();
    }

    @Override
    public World getWorldObject(){
        return this.getWorld();
    }

    @Override
    public void extractEnergy(int amount){
        this.storage.extractEnergy(amount, false);
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
    @SideOnly(Side.CLIENT)
    public int getMaxEnergy(){
        return this.storage.getMaxEnergyStored();
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