package keqing.gtsteam.api.capability.impl;

import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.IMultiblockController;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.recipes.category.ICategoryOverride;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTLog;
import gregtech.api.util.GTUtility;
import gregtech.common.ConfigHolder;
import keqing.gtsteam.common.metatileentities.multi.steam.MetaTileEntitySteamSolarBoiler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

import static gregtech.api.capability.GregtechDataCodes.BOILER_HEAT;
import static gregtech.api.capability.GregtechDataCodes.BOILER_LAST_TICK_STEAM;

public class SolarBoilerRecipeLogic extends AbstractRecipeLogic implements ICategoryOverride {

    private static final int STEAM_PER_WATER = 160;

    private static final int FLUID_DRAIN_MULTIPLIER = 100;
    private static final int FLUID_BURNTIME_TO_EU = 800 / FLUID_DRAIN_MULTIPLIER;

    private int currentHeat;
    private int lastTickSteamOutput;
    private int excessProjectedEU;
    private int excessWater;
    private boolean isWorkingEnabled;
    private boolean bombFlag = false;

    public SolarBoilerRecipeLogic(MetaTileEntitySteamSolarBoiler tileEntity) {
        super(tileEntity, null);
        this.fluidOutputs = Collections.emptyList();
        this.itemOutputs = Collections.emptyList();
    }

    /**
     * @param fluidHandler the handler to drain from
     * @param amount       the amount to drain
     * @return a valid boiler fluid from a container
     */
    @Nullable
    private static FluidStack getBoilerFluidFromContainer(@NotNull IFluidHandler fluidHandler, int amount) {
        if (amount == 0) return null;
        FluidStack drainedWater = fluidHandler.drain(Materials.Water.getFluid(amount), true);
        if (drainedWater == null || drainedWater.amount == 0) {
            drainedWater = fluidHandler.drain(Materials.DistilledWater.getFluid(amount), true);
        }
        if (drainedWater == null || drainedWater.amount == 0) {
            for (String fluidName : ConfigHolder.machines.boilerFluids) {
                Fluid fluid = FluidRegistry.getFluid(fluidName);
                if (fluid != null) {
                    drainedWater = fluidHandler.drain(new FluidStack(fluid, amount), true);
                    if (drainedWater != null && drainedWater.amount > 0) {
                        break;
                    }
                }
            }
        }
        return drainedWater;
    }

    @Override
    public void update() {
        if ((!isActive() || !canProgressRecipe() || !isWorkingEnabled()) && currentHeat > 0) {
            setHeat(currentHeat - getHeatReduction());
            setLastTickSteam(0);
        }
        super.update();
    }


    public boolean wasActiveAndNeedsUpdate() {
        return this.wasActiveAndNeedsUpdate;
    }

    @Override
    protected boolean canProgressRecipe() {
        return super.canProgressRecipe() && !(metaTileEntity instanceof IMultiblockController &&
                ((IMultiblockController) metaTileEntity).isStructureObstructed());
    }

    @Override
    protected void trySearchNewRecipe() {
        MetaTileEntitySteamSolarBoiler boiler = (MetaTileEntitySteamSolarBoiler) metaTileEntity;

        //IMultipleTankHandler importFluids = boiler.getImportFluids();
        boolean didStartRecipe = false;
        if (metaTileEntity.getWorld().isDaytime()&&!metaTileEntity.getWorld().isRaining()) {
            FluidStack drainedWater = getBoilerFluidFromContainer(getInputTank(), 1);
            if (!(drainedWater == null || drainedWater.amount < 1)) {
                didStartRecipe = true;
            }
        }
        if (progressTime != 0) return;
        if (didStartRecipe) {
            //GTLog.logger.warn("Recipe Start Meow !");
            this.progressTime = 1;
            this.maxProgressTime = 100;
            this.recipeEUt = adjustEUtForThrottle(boiler.steamPerTick());
            if (wasActiveAndNeedsUpdate) {
                wasActiveAndNeedsUpdate = false;
            } else {
                setActive(true);
            }
        }
        metaTileEntity.getNotifiedItemInputList().clear();
        metaTileEntity.getNotifiedFluidInputList().clear();
    }

    @Override
    protected void updateRecipeProgress() {
        if (canRecipeProgress) {
            int generatedSteam = GTUtility
                    .safeCastLongToInt(this.recipeEUt * getMaximumHeatFromMaintenance() / getMaximumHeat());
            if (generatedSteam > 0) {
                int amount = (generatedSteam + STEAM_PER_WATER) / STEAM_PER_WATER;
                excessWater += amount * STEAM_PER_WATER - generatedSteam;
                amount -= excessWater / STEAM_PER_WATER;
                excessWater %= STEAM_PER_WATER;
                FluidStack drainedWater = getBoilerFluidFromContainer(getInputTank(), amount);
                if (amount != 0 && (drainedWater == null || drainedWater.amount < amount)) {
                    if (drainedWater == null) {
                        //GTLog.logger.warn("MeowBomb because is DW is Null " + amount);
                    } else {
                        //GTLog.logger.warn("MeowBomb because DW AMOUNT:" + drainedWater.amount + " < "+amount);
                    }
                    //GTLog.logger.warn("Warning Bomb!" + drainedWater.toString() + " " + drainedWater.amount + " " + amount);
                    if (!bombFlag) {
                        getMetaTileEntity().explodeMultiblock((1.0f * currentHeat / getMaximumHeat()) * 8.0f);
                    } else {
                        bombFlag = false;
                    }
                } else {
                    setLastTickSteam(generatedSteam);
                    getOutputTank().fill(Materials.Steam.getFluid(generatedSteam), true);
                }
            }
            if (currentHeat < getMaximumHeat()) {
                setHeat(Math.min(currentHeat + getHeatIncrement(), getMaximumHeat()));
            }

            if (++progressTime > maxProgressTime) {
                completeRecipe();
            }
        }
    }

    private int getHeatIncrement() {
        return MetaTileEntitySteamSolarBoiler.HEAT_INCREMENT_PER_BLOCK;
    }

    private int getHeatReduction() {
        return MetaTileEntitySteamSolarBoiler.HEAT_REDUCTION_PER_BLOCK;
    }

    private int getMaximumHeat() {
        return MetaTileEntitySteamSolarBoiler.HEAT_MAXIMUM_PER_BLOCK;
    }

    private int getReductionWater(int generatedSteam) {
        return (generatedSteam / STEAM_PER_WATER);
        //每1L蒸汽用2L水
        //recipeEUt此时为蒸汽输出量
    }

    private int getMaximumHeatFromMaintenance() {
        return currentHeat;
    }

    private int adjustEUtForThrottle(int rawEUt) {
        int throttle = ((MetaTileEntitySteamSolarBoiler) metaTileEntity).getThrottle();
        return (int) (rawEUt * (throttle / 100.0));
    }


    public int getHeatScaled() {
        return (int) Math.round(currentHeat / (1.0 * getMaximumHeat()) * 100);
    }

    public void setHeat(int heat) {
        if (heat != this.currentHeat && !metaTileEntity.getWorld().isRemote) {
            writeCustomData(BOILER_HEAT, b -> b.writeVarInt(heat));
        }
        this.currentHeat = heat;
    }

    public int getLastTickSteam() {
        return lastTickSteamOutput;
    }

    public void setLastTickSteam(int lastTickSteamOutput) {
        if (lastTickSteamOutput != this.lastTickSteamOutput && !metaTileEntity.getWorld().isRemote) {
            writeCustomData(BOILER_LAST_TICK_STEAM, b -> b.writeInt(lastTickSteamOutput));
        }
        this.lastTickSteamOutput = lastTickSteamOutput;
    }

    @Override
    public long getInfoProviderEUt() {
        return this.lastTickSteamOutput;
    }

    @Override
    public boolean consumesEnergy() {
        return false;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        setLastTickSteam(0);
    }

    @Override
    protected void completeRecipe() {
        progressTime = 0;
        setMaxProgress(0);
        recipeEUt = 0;
        wasActiveAndNeedsUpdate = true;
    }

    @NotNull
    @Override
    public MetaTileEntitySteamSolarBoiler getMetaTileEntity() {
        return (MetaTileEntitySteamSolarBoiler) super.getMetaTileEntity();
    }

    @NotNull
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        compound.setInteger("Heat", currentHeat);
        compound.setInteger("ExcessWater", excessWater * 1000);
        compound.setInteger("ExcessProjectedEU", excessProjectedEU);
        return compound;
    }

    @Override
    public void deserializeNBT(@NotNull NBTTagCompound compound) {
        super.deserializeNBT(compound);
        this.currentHeat = compound.getInteger("Heat");
        this.excessWater = compound.getInteger("ExcessWater");
        this.excessProjectedEU = compound.getInteger("ExcessProjectedEU");
        bombFlag = true;
    }

    @Override
    public void writeInitialSyncData(@NotNull PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeVarInt(currentHeat);
        buf.writeInt(lastTickSteamOutput);
    }

    @Override
    public void receiveInitialSyncData(@NotNull PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.currentHeat = buf.readVarInt();
        this.lastTickSteamOutput = buf.readInt();
    }

    // Required overrides to use RecipeLogic, but all of them are redirected by the above overrides.

    @Override
    public void receiveCustomData(int dataId, @NotNull PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == BOILER_HEAT) {
            this.currentHeat = buf.readVarInt();
        } else if (dataId == BOILER_LAST_TICK_STEAM) {
            this.lastTickSteamOutput = buf.readInt();
        }
    }

    @Override
    protected long getEnergyInputPerSecond() {
        GTLog.logger.error("Large Boiler called getEnergyInputPerSecond(), this should not be possible!");
        return 0;
    }

    @Override
    protected long getEnergyStored() {
        GTLog.logger.error("Large Boiler called getEnergyStored(), this should not be possible!");
        return 0;
    }

    @Override
    protected long getEnergyCapacity() {
        GTLog.logger.error("Large Boiler called getEnergyCapacity(), this should not be possible!");
        return 0;
    }

    @Override
    protected boolean drawEnergy(long recipeEUt, boolean simulate) {
        GTLog.logger.error("Large Boiler called drawEnergy(), this should not be possible!");
        return false;
    }

    @Override
    public long getMaxVoltage() {
        GTLog.logger.error("Large Boiler called getMaxVoltage(), this should not be possible!");
        return 0;
    }

    @Override
    protected IEnergyContainer getEnergyContainer() {
        GTLog.logger.error("Large Boiler called getEnergyContainer(), this should not be possible!");
        return super.getEnergyContainer();
    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setBoolean("isActive", this.isActive);
        data.setBoolean("isWorkingEnabled", this.isWorkingEnabled());
        data.setBoolean("wasActiveAndNeedsUpdate", this.wasActiveAndNeedsUpdate);
        data.setInteger("progressTime", progressTime);
        data.setInteger("maxProgress", this.maxProgressTime);
        return data;
    }

    public void readFromNBT(NBTTagCompound data) {
        this.isActive = data.getBoolean("isActive");
        this.isWorkingEnabled = data.getBoolean("isWorkingEnabled");
        this.wasActiveAndNeedsUpdate = data.getBoolean("wasActiveAndNeedsUpdate");
        this.progressTime = data.getInteger("progressTime");
        this.maxProgressTime = data.getInteger("maxProgress");
    }
}
