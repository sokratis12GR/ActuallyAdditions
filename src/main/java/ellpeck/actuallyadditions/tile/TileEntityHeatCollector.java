package ellpeck.actuallyadditions.tile;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import ellpeck.actuallyadditions.config.values.ConfigIntValues;
import ellpeck.actuallyadditions.util.WorldPos;
import ellpeck.actuallyadditions.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Random;

public class TileEntityHeatCollector extends TileEntityBase implements IEnergyProvider{

    private int randomChance = ConfigIntValues.HEAT_COLLECTOR_LAVA_CHANCE.getValue();
    private int blocksNeeded = ConfigIntValues.HEAT_COLLECTOR_BLOCKS.getValue();

    public EnergyStorage storage = new EnergyStorage(30000);

    public static int energyProducedPerTick = ConfigIntValues.HEAT_COLLECTOR_ENERGY_PRODUCED.getValue();

    @Override
    public void updateEntity(){
        if(!worldObj.isRemote){
            ArrayList<Integer> blocksAround = new ArrayList<Integer>();
            if(energyProducedPerTick <= this.getMaxEnergyStored(ForgeDirection.UNKNOWN)-this.getEnergyStored(ForgeDirection.UNKNOWN)){
                for(int i = 1; i <= 5; i++){
                    WorldPos coords = WorldUtil.getCoordsFromSide(WorldUtil.getDirectionBySidesInOrder(i), worldObj, xCoord, yCoord, zCoord);
                    if(coords != null){
                        Block block = worldObj.getBlock(coords.getX(), coords.getY(), coords.getZ());
                        if(block != null && block.getMaterial() == Material.lava && worldObj.getBlockMetadata(coords.getX(), coords.getY(), coords.getZ()) == 0){
                            blocksAround.add(i);
                        }
                    }
                }

                if(blocksAround.size() >= blocksNeeded){
                    this.storage.receiveEnergy(energyProducedPerTick, false);
                    this.markDirty();

                    Random rand = new Random();
                    if(rand.nextInt(randomChance) == 0){
                        int randomSide = blocksAround.get(rand.nextInt(blocksAround.size()));
                        WorldUtil.breakBlockAtSide(WorldUtil.getDirectionBySidesInOrder(randomSide), worldObj, xCoord, yCoord, zCoord);
                    }
                }
                if(this.getEnergyStored(ForgeDirection.UNKNOWN) > 0){
                    WorldUtil.pushEnergy(worldObj, xCoord, yCoord, zCoord, ForgeDirection.UP, this.storage);
                }
            }
        }
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate){
        return this.storage.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored(ForgeDirection from){
        return this.storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from){
        return this.storage.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from){
        return from == ForgeDirection.UP;
    }
}
