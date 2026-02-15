package meowmel.gtsteam.common.metatileentities.multi.heat;

import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.IHeatable;
import gregtech.api.capability.IMultiblockController;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.recipes.category.ICategoryOverride;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTLog;
import gregtech.api.util.GTUtility;
import gregtech.common.ConfigHolder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static gregtech.api.capability.GregtechDataCodes.BOILER_HEAT;
import static gregtech.api.capability.GregtechDataCodes.BOILER_LAST_TICK_STEAM;

public class HeatSteamBoilerRecipeLogic extends AbstractRecipeLogic implements ICategoryOverride {

    private static final int STEAM_PER_WATER = 160;
    MetaTileEntityHeatSteamBoiler metaTileEntity;
    private int currentHeat;
    private int lastTickSteamOutput;
    private int excessProjectedEU;
    private int excessWater;
    private boolean bombFlag = false;

    public HeatSteamBoilerRecipeLogic(MetaTileEntityHeatSteamBoiler tileEntity) {
        super(tileEntity, null);
        this.fluidOutputs = Collections.emptyList();
        this.itemOutputs = Collections.emptyList();
        this.metaTileEntity = tileEntity;
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

    @Override
    protected boolean canProgressRecipe() {
        return super.canProgressRecipe() && !(metaTileEntity instanceof IMultiblockController &&
                metaTileEntity.isStructureObstructed());
    }

    @Override
    protected void trySearchNewRecipe() {
        MetaTileEntityHeatSteamBoiler boiler = metaTileEntity;
        boolean didStartRecipe = false;
        FluidStack drainedWater = getBoilerFluidFromContainer(getInputTank(), 1);
        if (!(drainedWater == null || drainedWater.amount < 1)) {
            didStartRecipe = true;
        }
        if (progressTime != 0) return;
        if (didStartRecipe) {
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
            // 获取当前可用热量
            double availableHeat = getHeat();

            //没热量 或者热源温度不够，不预热
            if (availableHeat == 0 || getCurrentTemp() < 373) {
                return;
            }

            // 满足条件
            // 计算理论蒸汽产量，默认0
            int generatedSteam = 0;

            // 锅炉温度到达373k，开始蒸汽产出
            if (currentHeat > 373) {
                generatedSteam = GTUtility.safeCastLongToInt(this.recipeEUt * currentHeat / getCurrentTemp());
            }

            if (generatedSteam > 0) {

                double temperatureFactor;
                if (currentHeat >= 973)
                    temperatureFactor = 1;
                else
                    temperatureFactor = (currentHeat - 373.0) / 600;

                double ratio = 4.0f - 3.0f * temperatureFactor;

                // 计算消耗热量
                double requiredHeat = generatedSteam * ratio;

                // 如果热量不足，按比例降低蒸汽产量
                // 因为只要有热，就一定产生蒸汽
                if (availableHeat < requiredHeat) {
                    generatedSteam = (int) (availableHeat / ratio);
                    changeHeat((long) -availableHeat);
                } else {
                    changeHeat((long) -requiredHeat);
                }

                // 如果有蒸汽要产生，则处理水和蒸汽生成
                if (generatedSteam > 0) {
                    int amount = (generatedSteam + STEAM_PER_WATER) / STEAM_PER_WATER;
                    excessWater += amount * STEAM_PER_WATER - generatedSteam;
                    amount -= excessWater / STEAM_PER_WATER;
                    excessWater %= STEAM_PER_WATER;
                    FluidStack drainedWater = getBoilerFluidFromContainer(getInputTank(), amount);
                    if (amount != 0 && (drainedWater == null || drainedWater.amount < amount)) {
                        if (!bombFlag) {
                            getMetaTileEntity().explodeMultiblock((1.0f * currentHeat / getCurrentTemp()) * 8.0f);
                        } else {
                            bombFlag = false;
                        }
                    } else {
                        setLastTickSteam(generatedSteam);
                        getOutputTank().fill(Materials.Steam.getFluid(generatedSteam), true);
                    }
                }
            }

            //有热量 锅炉升温
            if (currentHeat < getCurrentTemp()) {
                setHeat(Math.min(currentHeat + getHeatIncrement(), getMaximumHeat()));
            }

            if (++progressTime > maxProgressTime) {
                completeRecipe();
            }
        }
    }

    private int getHeatIncrement() {
        int deltaT = getCurrentTemp() - currentHeat;
        // 基础加热增量
        int increase = 1;
        // 温度差越大，加热越快
        increase += deltaT >> 7;
        // 机器容量越大，加热越慢（使用位运算优化除法）
        increase -= metaTileEntity.getCapacity() >> 6;
        return increase > 0 ? increase : 1;
    }

    private int getHeatReduction() {
        return (int) Math.sqrt(metaTileEntity.getCapacity());
    }

    private int getMaximumHeat() {
        return getMaxTemp();
    }

    public int getMaximumHeatFromMaintenance() {
        return currentHeat;
    }

    public int getCurrentTemp() {
        List<IHeatable> heatable = metaTileEntity.getHeatHatch();
        if (heatable == null) return 293;
        return heatable
                .stream()
                .mapToInt(IHeatable::getTemperature)
                .max()
                .orElse(293);
    }

    public int getMaxTemp() {
        List<IHeatable> heatable = metaTileEntity.getHeatHatch();
        if (heatable == null) return 293;
        return heatable
                .stream()
                .mapToInt(IHeatable::getMaxTemperature)
                .max()
                .orElse(293);
    }

    public long getHeat() {
        List<IHeatable> heatable = metaTileEntity.getHeatHatch();
        if (heatable == null) return 0;
        return heatable
                .stream()
                .mapToLong(IHeatable::getHeatStored)
                .sum();
    }

    public void setHeat(int heat) {
        if (heat != this.currentHeat && !metaTileEntity.getWorld().isRemote) {
            writeCustomData(BOILER_HEAT, b -> b.writeVarInt(heat));
        }
        this.currentHeat = heat;
    }

    public void changeHeat(long heat) {
        List<IHeatable> heatable = metaTileEntity.getHeatHatch();
        if (heatable == null) return;
        long average = heat / heatable.size();
        heatable.forEach(h -> h.changeHeat(average));
    }

    private int adjustEUtForThrottle(int rawEUt) {
        int throttle = metaTileEntity.getThrottle();
        return (int) (rawEUt * (throttle / 100.0));
    }

    public int getHeatScaled() {
        return (int) Math.round(currentHeat / (1.0 * getMaximumHeat()) * 100);
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
    public MetaTileEntityHeatSteamBoiler getMetaTileEntity() {
        return (MetaTileEntityHeatSteamBoiler) super.getMetaTileEntity();
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
}
