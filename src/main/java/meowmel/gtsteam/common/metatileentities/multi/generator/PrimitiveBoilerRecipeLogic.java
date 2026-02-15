package meowmel.gtsteam.common.metatileentities.multi.generator;

import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.IHeatable;
import gregtech.api.capability.IMultiblockController;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.category.ICategoryOverride;
import gregtech.api.util.GTLog;
import gregtech.api.util.GTUtility;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static gregtech.api.capability.GregtechDataCodes.BOILER_LAST_TICK_STEAM;
import static meowmel.gtsteam.common.metatileentities.multi.generator.PrimitiveBoilerType.*;

/**
 * 原始锅炉配方逻辑类
 * 处理锅炉的燃料燃烧、水加热、热量生成等核心逻辑
 */
public class PrimitiveBoilerRecipeLogic extends AbstractRecipeLogic implements ICategoryOverride {

    // 液体燃料消耗倍率（用于计算燃料燃烧时间）
    private static final int FLUID_DRAIN_MULTIPLIER = 100;
    // 液体燃料燃烧时间到EU的转换系数
    private static final int FLUID_BURNTIME_TO_EU = 800 / FLUID_DRAIN_MULTIPLIER;
    private final boolean isHighPressure;
    MetaTileEntityPrimitiveBoiler metaTileEntity;
    // 上一tick产生的热量
    private int lastTickHeatOutput;
    // 多余的燃料量（用于固体燃料的精确计算）
    private int excessFuel;
    // 多余的预计EU（用于节流阀调节时的精确计算）
    private int excessProjectedEU;

    /**
     * 构造函数
     *
     * @param tileEntity 关联的原始锅炉实体
     */
    public PrimitiveBoilerRecipeLogic(MetaTileEntityPrimitiveBoiler tileEntity, boolean isHighPressure) {
        super(tileEntity, null);
        // 锅炉没有物品和流体输出，直接生成热量
        this.fluidOutputs = Collections.emptyList();
        this.itemOutputs = Collections.emptyList();
        this.isHighPressure = isHighPressure;
        metaTileEntity = tileEntity;
    }

    /**
     * 每tick更新锅炉状态
     * 如果锅炉不活跃或无法工作，则热量逐渐降低
     */
    @Override
    public void update() {
        if ((!isActive() || !canProgressRecipe() || !isWorkingEnabled()) && getCurrentTemp() > 293) {
            setTemp(getCurrentTemp() - 1); // 热量递减
            setLastTickHeat(0); // 没有热量产生
        }
        super.update();
    }

    /**
     * 检查是否可以继续处理配方
     *
     * @return 如果可以继续则返回true
     */
    @Override
    protected boolean canProgressRecipe() {
        return super.canProgressRecipe() && !(metaTileEntity instanceof IMultiblockController &&
                metaTileEntity.isStructureObstructed());
    }

    /**
     * 尝试搜索新的燃料配方并开始燃烧
     */
    @Override
    protected void trySearchNewRecipe() {
        MetaTileEntityPrimitiveBoiler boiler = metaTileEntity;
        IMultipleTankHandler importFluids = boiler.getImportFluids();
        boolean didStartRecipe = false;

        // 液体锅炉（低压流体或高压流体）
        if (boiler.boilerType == LOW_PRESSURE_FLUID || boiler.boilerType == HIGH_PRESSURE_FLUID) {
            for (IFluidTank fluidTank : importFluids.getFluidTanks()) {
                FluidStack fuelStack = fluidTank.drain(Integer.MAX_VALUE, false);
                // 跳过水等不可作为燃料的流体
                if (fuelStack == null) continue;

                // 尝试从内燃机燃料配方中查找匹配的配方
                Recipe dieselRecipe = RecipeMaps.COMBUSTION_GENERATOR_FUELS.findRecipe(
                        GTValues.V[GTValues.MAX], Collections.emptyList(), Collections.singletonList(fuelStack));
                // 如果找到配方且流体量足够（乘以倍数以减少整数除法误差）
                if (dieselRecipe != null &&
                        fuelStack.amount >= dieselRecipe.getFluidInputs().get(0).getAmount() * FLUID_DRAIN_MULTIPLIER) {
                    // 消耗燃料
                    fluidTank.drain(dieselRecipe.getFluidInputs().get(0).getAmount() * FLUID_DRAIN_MULTIPLIER, true);
                    // 计算燃烧时间：将配方EU和持续时间转换为燃烧时间，除以2（因为内燃机燃料燃烧时间减半）
                    // 并根据锅炉类型进行加速，最后根据节流阀调整
                    setMaxProgress(adjustBurnTimeForThrottle(Math.max(1, boiler.boilerType.runtimeBoost(
                            GTUtility.safeCastLongToInt((Math.abs(dieselRecipe.getEUt()) * dieselRecipe.getDuration()) /
                                    FLUID_BURNTIME_TO_EU / 2)))));
                    didStartRecipe = true;
                    break;
                }

                // 尝试从半流体发电机燃料配方中查找
                Recipe denseFuelRecipe = RecipeMaps.SEMI_FLUID_GENERATOR_FUELS.findRecipe(
                        GTValues.V[GTValues.MAX], Collections.emptyList(), Collections.singletonList(fuelStack));
                if (denseFuelRecipe != null &&
                        fuelStack.amount >= denseFuelRecipe.getFluidInputs().get(0).getAmount() * FLUID_DRAIN_MULTIPLIER) {
                    fluidTank.drain(denseFuelRecipe.getFluidInputs().get(0).getAmount() * FLUID_DRAIN_MULTIPLIER, true);
                    // 半流体燃料燃烧时间翻倍
                    setMaxProgress(adjustBurnTimeForThrottle(
                            Math.max(1,
                                    boiler.boilerType
                                            .runtimeBoost(GTUtility.safeCastLongToInt((Math.abs(denseFuelRecipe.getEUt()) *
                                                    denseFuelRecipe.getDuration() / FLUID_BURNTIME_TO_EU * 2))))));
                    didStartRecipe = true;
                    break;
                }
            }
        }
        // 固体锅炉（低压固体或高压固体）
        else if (boiler.boilerType == LOW_PRESSURE_SOLID || boiler.boilerType == HIGH_PRESSURE_SOLID) {
            IItemHandlerModifiable importItems = boiler.getImportItems();
            for (int i = 0; i < importItems.getSlots(); i++) {
                ItemStack stack = importItems.getStackInSlot(i);
                // 获取物品的燃烧时间（来自熔炉燃料）
                int fuelBurnTime = (int) Math.ceil(TileEntityFurnace.getItemBurnTime(stack));
                // 确保燃料至少能燃烧1tick（因为燃烧时间除以8）
                if (fuelBurnTime / 8 > 0) {
                    // 跳过流体容器（例如桶）
                    if (FluidUtil.getFluidHandler(stack) != null) continue;
                    // 处理多余的燃料时间（小于8的部分累加）
                    this.excessFuel += fuelBurnTime % 8;
                    int excessProgress = this.excessFuel / 8;
                    this.excessFuel %= 8;
                    // 设置总燃烧时间，包括累加的多余部分和本次燃料的燃烧时间，并根据锅炉类型加速和节流阀调整
                    setMaxProgress(excessProgress +
                            adjustBurnTimeForThrottle(boiler.boilerType.runtimeBoost(fuelBurnTime / 8)));
                    stack.shrink(1); // 消耗一个物品
                    didStartRecipe = true;
                    break;
                }
            }
        }

        // 如果成功开始燃烧
        if (didStartRecipe) {
            this.progressTime = 1; // 从1开始计数，因为当前tick已经消耗了燃料
            // 设置当前配方产生的热量（EU/t），并根据节流阀调整
            this.recipeEUt = adjustEUtForThrottle(boiler.boilerType.heatPerTick());
            if (wasActiveAndNeedsUpdate) {
                wasActiveAndNeedsUpdate = false;
            } else {
                setActive(true); // 激活锅炉
            }
        }
        // 清空通知列表，避免重复处理
        metaTileEntity.getNotifiedItemInputList().clear();
        metaTileEntity.getNotifiedFluidInputList().clear();
    }

    /**
     * 更新配方进度（每tick调用）
     * 产生热量并增加热量
     */
    @Override
    protected void updateRecipeProgress() {
        if (canRecipeProgress) {
            // 根据当前热量和维护情况计算实际产生的热量（线性插值）
            // 这里根据锅炉燃烧的温度来计算最大温度
            int generatedHeat = GTUtility
                    .safeCastLongToInt(this.recipeEUt * Math.max(getCurrentTemp() / getMaximumHeat(), 1));
            if (generatedHeat > 0) {
                // 成功产生热量
                setLastTickHeat(generatedHeat);
                // 将热量填充到输出储罐
                changeHeat(generatedHeat);

            }
            // 如果热量未达到最大，则增加热量
            if (getCurrentTemp() < getMaximumHeat()) {
                setTemp(Math.min(getCurrentTemp() + (isHighPressure ? 2 : 1), getMaxTemp()));
            }

            // 检查燃烧是否完成
            if (++progressTime > maxProgressTime) {
                completeRecipe();
            }
        }
    }

    /**
     * 根据节流阀调整EU/t（热量产量）
     *
     * @param rawEUt 原始EU/t
     * @return 调整后的EU/t
     */
    private int adjustEUtForThrottle(int rawEUt) {
        int throttle = metaTileEntity.getThrottle();
        // 节流阀百分比（0-100），最低保证25EU/t（1%节流阀时）
        return (int) Math.max(25, rawEUt * (throttle / 100.0));
    }

    /**
     * 根据节流阀调整燃烧时间
     * 节流阀降低时，热量产量减少，因此燃烧时间需要延长以保持总能量不变
     *
     * @param rawBurnTime 原始燃烧时间（tick）
     * @return 调整后的燃烧时间
     */
    private int adjustBurnTimeForThrottle(int rawBurnTime) {
        MetaTileEntityPrimitiveBoiler boiler = metaTileEntity;
        int EUt = boiler.boilerType.heatPerTick(); // 原始热量产量
        int adjustedEUt = adjustEUtForThrottle(EUt); // 调整后的热量产量
        // 计算调整后的燃烧时间，使总热量不变：原始EUt * 原始时间 = 调整后EUt * 调整后时间
        int adjustedBurnTime = rawBurnTime * EUt / adjustedEUt;
        // 处理由于整数除法产生的误差，累加到excessProjectedEU中
        this.excessProjectedEU += (EUt * rawBurnTime) - (adjustedEUt * adjustedBurnTime);
        // 如果累积的误差足够多，则增加燃烧时间
        adjustedBurnTime += this.excessProjectedEU / adjustedEUt;
        this.excessProjectedEU %= adjustedEUt;
        return adjustedBurnTime;
    }

    /**
     * 获取热量百分比（用于显示）
     */
    public int getHeatScaled() {
        return (int) Math.round(getCurrentTemp() / (1.0 * getMaxTemp()) * 100);
    }

    public void setTemp(int heat) {
        List<IHeatable> heatable = metaTileEntity.getHeatHatch();
        if (heatable == null) return;
        heatable.forEach(h -> h.setTemperature(heat));
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

    public void changeHeat(long heat) {
        List<IHeatable> heatable = metaTileEntity.getHeatHatch();
        if (heatable == null) return;
        long average = heat / heatable.size();
        heatable.forEach(h -> h.changeHeat(average));
    }

    public int getMaximumHeat() {
        //不是热源仓的温度，而是理论锅炉燃烧达到的温度
        return metaTileEntity.boilerType.getMaxTemp();
    }

    /**
     * 获取上一tick产生的热量
     */
    public int getLastTickHeat() {
        return lastTickHeatOutput;
    }

    /**
     * 设置上一tick产生的热量，并同步到客户端
     */
    public void setLastTickHeat(int lastTickSteamOutput) {
        if (lastTickSteamOutput != this.lastTickHeatOutput && !metaTileEntity.getWorld().isRemote) {
            writeCustomData(BOILER_LAST_TICK_STEAM, b -> b.writeInt(lastTickSteamOutput));
        }
        this.lastTickHeatOutput = lastTickSteamOutput;
    }

    /**
     * 用于信息显示（如TOP）的EU/t值，这里返回热量产量
     */
    @Override
    public long getInfoProviderEUt() {
        return this.lastTickHeatOutput;
    }

    /**
     * 锅炉不消耗能量（它消耗燃料产生热量）
     */
    @Override
    public boolean consumesEnergy() {
        return false;
    }

    /**
     * 无效化时重置上一tick热量产量
     */
    @Override
    public void invalidate() {
        super.invalidate();
        setLastTickHeat(0);
    }

    /**
     * 完成当前燃料燃烧，重置状态
     */
    @Override
    protected void completeRecipe() {
        progressTime = 0;
        setMaxProgress(0);
        recipeEUt = 0;
        wasActiveAndNeedsUpdate = true; // 标记需要更新活动状态
    }

    /**
     * 获取关联的原始锅炉实体
     */
    @NotNull
    @Override
    public MetaTileEntityPrimitiveBoiler getMetaTileEntity() {
        return (MetaTileEntityPrimitiveBoiler) super.getMetaTileEntity();
    }

    /**
     * 序列化NBT数据，用于保存
     */
    @NotNull
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        compound.setInteger("ExcessFuel", excessFuel);
        compound.setInteger("ExcessProjectedEU", excessProjectedEU);
        return compound;
    }

    /**
     * 反序列化NBT数据，用于加载
     */
    @Override
    public void deserializeNBT(@NotNull NBTTagCompound compound) {
        super.deserializeNBT(compound);
        this.excessFuel = compound.getInteger("ExcessFuel");
        this.excessProjectedEU = compound.getInteger("ExcessProjectedEU");
    }

    /**
     * 写入初始同步数据（客户端连接时发送）
     */
    @Override
    public void writeInitialSyncData(@NotNull PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(lastTickHeatOutput);
    }

    /**
     * 接收初始同步数据
     */
    @Override
    public void receiveInitialSyncData(@NotNull PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.lastTickHeatOutput = buf.readInt();
    }

    /**
     * 接收自定义数据包（用于同步热量和热量产量）
     */
    @Override
    public void receiveCustomData(int dataId, @NotNull PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == BOILER_LAST_TICK_STEAM) {
            this.lastTickHeatOutput = buf.readInt();
        }
    }

    // 以下方法覆盖了AbstractRecipeLogic中的能量相关方法，因为锅炉不使用电能
    // 如果被调用，则记录错误日志

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

    // JEI相关覆盖，显示锅炉可以使用的燃料配方

    @Override
    public @NotNull RecipeMap<?> @NotNull [] getJEIRecipeMapCategoryOverrides() {
        // 显示内燃机燃料和半流体燃料的配方
        return new RecipeMap<?>[]{RecipeMaps.COMBUSTION_GENERATOR_FUELS, RecipeMaps.SEMI_FLUID_GENERATOR_FUELS};
    }

    @Override
    public @NotNull String @NotNull [] getJEICategoryOverrides() {
        // 同时显示原版熔炉燃料配方
        return new String[]{"minecraft.fuel"};
    }
}