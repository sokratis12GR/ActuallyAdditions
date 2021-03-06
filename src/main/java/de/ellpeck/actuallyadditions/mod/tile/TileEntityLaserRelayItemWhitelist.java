/*
 * This file ("TileEntityLaserRelayItemWhitelist.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.tile;

import de.ellpeck.actuallyadditions.mod.network.gui.IButtonReactor;
import de.ellpeck.actuallyadditions.mod.util.ItemUtil;
import de.ellpeck.actuallyadditions.mod.util.StringUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class TileEntityLaserRelayItemWhitelist extends TileEntityLaserRelayItem implements IButtonReactor{

    public final IInventory filterInventory;
    public boolean isLeftWhitelist;
    public boolean isRightWhitelist;
    private ItemStack[] slots = new ItemStack[24];
    private boolean lastLeftWhitelist;
    private boolean lastRightWhitelist;

    public TileEntityLaserRelayItemWhitelist(){
        super("laserRelayItemWhitelist");

        this.filterInventory = new IInventory(){

            private TileEntityLaserRelayItemWhitelist tile;

            private IInventory setTile(TileEntityLaserRelayItemWhitelist tile){
                this.tile = tile;
                return this;
            }


            @Override
            public String getName(){
                return this.tile.name;
            }

            @Override
            public int getInventoryStackLimit(){
                return 64;
            }

            @Override
            public void markDirty(){

            }

            @Override
            public boolean isUseableByPlayer(EntityPlayer player){
                return this.tile.canPlayerUse(player);
            }

            @Override
            public void openInventory(EntityPlayer player){

            }

            @Override
            public void closeInventory(EntityPlayer player){

            }

            @Override
            public int getField(int id){
                return 0;
            }

            @Override
            public void setField(int id, int value){

            }

            @Override
            public int getFieldCount(){
                return 0;
            }

            @Override
            public void clear(){
                int length = this.tile.slots.length;
                this.tile.slots = new ItemStack[length];
            }

            @Override
            public void setInventorySlotContents(int i, ItemStack stack){
                this.tile.slots[i] = stack;
                this.markDirty();
            }

            @Override
            public int getSizeInventory(){
                return this.tile.slots.length;
            }

            @Override
            public ItemStack getStackInSlot(int i){
                if(i < this.getSizeInventory()){
                    return this.tile.slots[i];
                }
                return null;
            }

            @Override
            public ItemStack decrStackSize(int i, int j){
                if(this.tile.slots[i] != null){
                    ItemStack stackAt;
                    if(this.tile.slots[i].stackSize <= j){
                        stackAt = this.tile.slots[i];
                        this.tile.slots[i] = null;
                        this.markDirty();
                        return stackAt;
                    }
                    else{
                        stackAt = this.tile.slots[i].splitStack(j);
                        if(this.tile.slots[i].stackSize <= 0){
                            this.tile.slots[i] = null;
                        }
                        this.markDirty();
                        return stackAt;
                    }
                }
                return null;
            }

            @Override
            public ItemStack removeStackFromSlot(int index){
                ItemStack stack = this.tile.slots[index];
                this.tile.slots[index] = null;
                return stack;
            }

            @Override
            public boolean hasCustomName(){
                return false;
            }


            @Override
            public ITextComponent getDisplayName(){
                return new TextComponentTranslation(this.getName());
            }

            @Override
            public boolean isItemValidForSlot(int index, ItemStack stack){
                return false;
            }
        }.setTile(this);
    }

    @Override
    public boolean isWhitelisted(ItemStack stack){
        return this.checkFilter(stack, true, this.isLeftWhitelist) || this.checkFilter(stack, false, this.isRightWhitelist);
    }

    private boolean checkFilter(ItemStack stack, boolean left, boolean isWhitelist){
        int slotStart = left ? 0 : 12;
        int slotStop = slotStart+12;

        for(int i = slotStart; i < slotStop; i++){
            if(this.slots[i] != null && this.slots[i].isItemEqual(stack)){
                return isWhitelist;
            }
        }
        return !isWhitelist;
    }

    @Override
    public void writeSyncableNBT(NBTTagCompound compound, boolean isForSync){
        super.writeSyncableNBT(compound, isForSync);
        if(!isForSync){
            TileEntityInventoryBase.saveSlots(this.slots, compound);
        }
        compound.setBoolean("LeftWhitelist", this.isLeftWhitelist);
        compound.setBoolean("RightWhitelist", this.isRightWhitelist);
    }

    @Override
    public void readSyncableNBT(NBTTagCompound compound, boolean isForSync){
        super.readSyncableNBT(compound, isForSync);
        if(!isForSync){
            TileEntityInventoryBase.loadSlots(this.slots, compound);
        }
        this.isLeftWhitelist = compound.getBoolean("LeftWhitelist");
        this.isRightWhitelist = compound.getBoolean("RightWhitelist");
    }

    @Override
    public void onButtonPressed(int buttonID, EntityPlayer player){
        if(buttonID == 0){
            this.isLeftWhitelist = !this.isLeftWhitelist;
        }
        else if(buttonID == 1){
            this.isRightWhitelist = !this.isRightWhitelist;
        }
        else if(buttonID == 2){
            this.addWhitelistSmart();
        }
    }

    private void addWhitelistSmart(){
        List<IItemHandler> handlers = this.getAllHandlersAround();
        for(IItemHandler handler : handlers){
            for(int i = 0; i < handler.getSlots(); i++){
                ItemStack stack = handler.getStackInSlot(i);
                if(stack != null){
                    if(!ItemUtil.contains(this.slots, stack, false)){
                        for(int j = 0; j < this.slots.length; j++){
                            if(this.slots[j] == null || this.slots[j].stackSize <= 0){
                                ItemStack whitelistStack = stack.copy();
                                whitelistStack.stackSize = 1;
                                this.slots[j] = whitelistStack;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateEntity(){
        super.updateEntity();

        if(!this.worldObj.isRemote){
            if((this.isLeftWhitelist != this.lastLeftWhitelist || this.isRightWhitelist != this.lastRightWhitelist) && this.sendUpdateWithInterval()){
                this.lastLeftWhitelist = this.isLeftWhitelist;
                this.lastRightWhitelist = this.isRightWhitelist;
            }
        }
    }
}
