package keqing.gtsteam.common.metatileentities.combustor;


import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.ColourMultiplier;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import gregtech.api.GTValues;
import gregtech.api.capability.IHeatable;
import gregtech.api.capability.impl.HeatContainerHandler;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.IDataInfoProvider;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuiTheme;
import gregtech.api.mui.GTGuis;
import gregtech.api.unification.material.Material;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.GTUtility;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.particle.VanillaParticleEffects;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.client.renderer.texture.cube.SimpleSidedCubeRenderer;
import gregtech.common.ConfigHolder;
import gregtech.core.sound.GTSoundEvents;
import keqing.gtsteam.client.textures.GTSteamTextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gregtech.api.GTValues.V;
import static gregtech.api.capability.GregtechCapabilities.CAPABILITY_HEAT_CONTAINER;
import static gregtech.api.capability.GregtechDataCodes.IS_WORKING;

/**
 * 热量锅炉的抽象基类
 * 处理燃料燃烧、水加热、 热量生成等核心逻辑
 */
public abstract class Combustor extends MetaTileEntity implements IDataInfoProvider {

    // 用于GUI纹理路径替换的正则表达式模式
    private static final Pattern STRING_SUBSTITUTION_PATTERN = Pattern.compile("%s", Pattern.LITERAL);

    // GUI相关纹理
    public final TextureArea bronzeSlotBackgroundTexture;
    public final TextureArea slotFurnaceBackground;

    // 锅炉类型：高压还是低压
    protected final boolean isHighPressure;
    protected final SimpleOverlayRenderer render_side = GTSteamTextures.HU_BURRING_BOX_SIDE_OVERLAY;
    protected final SimpleOverlayRenderer renderer_full = GTSteamTextures.HU_BURRING_BOX_SIDE_FULL_OVERLAY;
    protected final IHeatable heatable;
    // 渲染器，用于显示锅炉状态
    private final ICubeRenderer renderer;
    // 容器物品栏，用于流体容器交互
    private final ItemStackHandler containerInventory;
    protected int tier;
    protected Material material;
    protected int color;
    // 燃料燃烧相关变量
    private int fuelBurnTimeLeft;       // 剩余燃烧时间
    private int fuelMaxBurnTime;        // 最大燃烧时间
    private int timeBeforeCoolingDown;  // 冷却倒计时
    // 锅炉状态
    private boolean isBurning;           // 是否正在燃烧
    private boolean wasBurningAndNeedsUpdate; // 燃烧状态需要更新标志

    /**
     * 构造函数
     *
     * @param metaTileEntityId 实体ID
     * @param isHighPressure   是否为高压锅炉
     * @param renderer         渲染器
     */
    public Combustor(ResourceLocation metaTileEntityId, boolean isHighPressure, ICubeRenderer renderer, int tier, Material material) {
        super(metaTileEntityId);
        this.renderer = renderer;
        this.isHighPressure = isHighPressure;
        // 获取GUI纹理
        this.bronzeSlotBackgroundTexture = getGuiTexture("slot_%s");
        this.slotFurnaceBackground = getGuiTexture("slot_%s_furnace_background");
        // 初始化物品栏：0号槽输入流体容器，1号槽输出空容器
        this.containerInventory = new GTItemStackHandler(this, 2);

        this.tier = tier;
        this.material = material;
        this.color = material.getMaterialRGB();

        heatable = HeatContainerHandler.emitterContainer(this, V[tier] * 64L, (tier + 1) * 200 + 273,
                V[tier] * 20);
        ((HeatContainerHandler) this.heatable).setSideOutputCondition(s -> s == EnumFacing.UP);
    }

    @Override
    public boolean isActive() {
        return isBurning;
    }

    /**
     * 客户端专用：获取基础渲染器
     * 根据锅炉类型返回对应的渲染器
     */
    @SideOnly(Side.CLIENT)
    protected SimpleSidedCubeRenderer getBaseRenderer() {
        return Textures.STEAM_BRICKED_CASING_STEEL;
    }

    /**
     * 获取粒子纹理
     */
    @SideOnly(Side.CLIENT)
    public Pair<TextureAtlasSprite, Integer> getParticleTexture() {
        return Pair.of(GTSteamTextures.HU_BURRING_BOX_SIDE_FULL_OVERLAY.getParticleSprite(), this.color);
    }

    /**
     * 渲染实体
     */
    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        IVertexOperation[] colouredPipeline = ArrayUtils.add(pipeline, new ColourMultiplier(GTUtility.convertRGBtoOpaqueRGBA_CL(this.getPaintingColorForRendering())));
        IVertexOperation[] pipeline1 = ArrayUtils.add(pipeline, new ColourMultiplier(GTUtility.convertRGBtoOpaqueRGBA_CL(this.color)));
        this.getBaseRenderer().render(renderState, translation, colouredPipeline);
        this.renderer_full.renderSided(EnumFacing.UP, renderState, translation, pipeline1);
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            this.render_side.renderSided(facing, renderState, translation, pipeline1);
        }
        this.renderer.renderOrientedState(renderState, translation, pipeline, this.getFrontFacing(), isBurning, isBurning);
    }

    @Override
    public int getDefaultPaintingColor() {
        return 0xFFFFFF; // 默认白色
    }

    /**
     * 保存数据到NBT
     */
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("FuelBurnTimeLeft", fuelBurnTimeLeft);
        data.setInteger("FuelMaxBurnTime", fuelMaxBurnTime);
        data.setTag("ContainerInventory", containerInventory.serializeNBT());
        return data;
    }

    /**
     * 从NBT加载数据
     */
    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.fuelBurnTimeLeft = data.getInteger("FuelBurnTimeLeft");
        this.fuelMaxBurnTime = data.getInteger("FuelMaxBurnTime");
        this.containerInventory.deserializeNBT(data.getCompoundTag("ContainerInventory"));
        // 如果有剩余燃烧时间，则锅炉正在燃烧
        this.isBurning = fuelBurnTimeLeft > 0;
    }

    /**
     * 写入初始同步数据（客户端-服务器同步）
     */
    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeBoolean(isBurning);
    }

    /**
     * 接收初始同步数据
     */
    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.isBurning = buf.readBoolean();
    }

    /**
     * 接收自定义数据（用于工作状态同步）
     */
    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == IS_WORKING) {
            this.isBurning = buf.readBoolean();
            scheduleRenderUpdate(); // 更新渲染
        }
    }

    /**
     * 设置燃料的最大燃烧时间
     */
    public void setFuelMaxBurnTime(int fuelMaxBurnTime) {
        this.fuelMaxBurnTime = fuelMaxBurnTime;
        this.fuelBurnTimeLeft = fuelMaxBurnTime;
        if (!getWorld().isRemote) {
            markDirty(); // 标记为需要保存
        }
    }

    /**
     * 每tick更新
     */
    @Override
    public void update() {
        super.update();
        // 仅服务端执行逻辑
        if (!getWorld().isRemote) {
            updateCurrentTemperature(); // 更新温度
            if (getOffsetTimer() % 10 == 0) {
                generateHeat(); // 每10tick生成 热量
            }

            // 从流体容器填充内部储罐
            GTTransferUtils.fillInternalTankFromFluidContainer(importFluids, containerInventory, 0, 1);

            // 如果没有燃料了，尝试消耗新燃料
            if (fuelMaxBurnTime <= 0) {
                tryConsumeNewFuel();
                if (fuelBurnTimeLeft > 0) {
                    if (wasBurningAndNeedsUpdate) {
                        this.wasBurningAndNeedsUpdate = false;
                    } else setBurning(true); // 开始燃烧
                }
            }

            // 处理燃烧状态更新
            if (wasBurningAndNeedsUpdate) {
                this.wasBurningAndNeedsUpdate = false;
                setBurning(false); // 停止燃烧
            }
        }
    }

    /**
     * 更新当前温度
     */
    private void updateCurrentTemperature() {
        if (fuelMaxBurnTime > 0) {
            // 有燃料时，每12tick更新一次
            if (getOffsetTimer() % 12 == 0) {
                // 每两次燃料消耗增加一次温度，直到达到最大值
                if (fuelBurnTimeLeft % 2 == 0 && getCurrentTemperature() < getMaxTemperate()) {
                    //每次增加的应该是12tick的量
                    heatable.changeHeat(V[tier - 1] * (isHighPressure ? 2 : 1) * 12);
                    heatable.setTemperature(getCurrentTemperature() + tier * (isHighPressure ? 10 : 5));
                }
                // 消耗燃料：高压锅炉消耗更快
                fuelBurnTimeLeft -= isHighPressure ? 2 : 1;
                if (fuelBurnTimeLeft <= 0) {
                    this.fuelMaxBurnTime = 0;
                    this.timeBeforeCoolingDown = getCooldownInterval(); // 开始冷却倒计时
                    // 锅炉没有燃料了，标记需要更新燃烧状态
                    this.wasBurningAndNeedsUpdate = true;
                }
            }
        } else if (timeBeforeCoolingDown == 0) {
            // 没有燃料且冷却倒计时为0时，开始降温
            if (getCurrentTemperature() > 293) {
                heatable.setTemperature(getCurrentTemperature() - getCoolDownRate());
                timeBeforeCoolingDown = getCooldownInterval();
            }
        } else {
            // 冷却倒计时减少
            timeBeforeCoolingDown--;
        }
    }

    /**
     * 抽象方法：获取基础 热量输出量
     */
    protected abstract int getBaseHeatOutput();

    /**
     * 返回当前每10tick的总 热量输出量
     */
    public int getTotalHeatOutput() {
        if (getCurrentTemperature() < 373) return 0; // 温度低于100度不产生 热量
        // 根据温度比例计算输出量
        return (int) (getBaseHeatOutput() * (getCurrentTemperature() / (getMaxTemperate() * 1.0)) / 2);
    }


    /**
     * 生成 热量
     */
    private void generateHeat() {
        if (getCurrentTemperature() >= 373) {
            int fillAmount = getTotalHeatOutput();
            heatable.changeHeat(fillAmount);
        }
    }

    /**
     * 检查是否正在燃烧
     */
    public boolean isBurning() {
        return isBurning;
    }

    /**
     * 设置燃烧状态
     */
    public void setBurning(boolean burning) {
        this.isBurning = burning;
        if (!getWorld().isRemote) {
            markDirty();
            // 同步状态到客户端
            writeCustomData(IS_WORKING, buf -> buf.writeBoolean(burning));
        }
    }

    // 抽象方法，由子类实现
    protected abstract void tryConsumeNewFuel(); // 尝试消耗新燃料

    protected abstract int getCooldownInterval(); // 获取冷却间隔

    protected abstract int getCoolDownRate();     // 获取冷却速率

    /**
     * 获取最大温度：高压1000度，低压500度
     */
    public int getMaxTemperate() {
        return heatable.getMaxTemperature();
    }

    /**
     * 获取温度百分比
     */
    public double getTemperaturePercent() {
        return getCurrentTemperature() / (getMaxTemperate() * 1.0);
    }

    /**
     * 获取当前温度
     */
    public int getCurrentTemperature() {
        return heatable.getTemperature();
    }

    /**
     * 获取剩余燃料百分比
     */
    public double getFuelLeftPercent() {
        return fuelMaxBurnTime == 0 ? 0.0 : fuelBurnTimeLeft / (fuelMaxBurnTime * 1.0);
    }

    /**
     * 获取GUI纹理
     */
    protected TextureArea getGuiTexture(String pathTemplate) {
        String type = isHighPressure ? "steel" : "bronze";
        // 替换路径中的%s为类型
        return TextureArea.fullImage(String.format("textures/gui/steam/%s/%s.png",
                type, STRING_SUBSTITUTION_PATTERN.matcher(pathTemplate).replaceAll(Matcher.quoteReplacement(type))));
    }

    @Override
    public boolean usesMui2() {
        return true; // 使用ModularUI 2.0
    }

    /**
     * 构建UI界面
     */
    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager guiSyncManager, UISettings settings) {
        return GTGuis.defaultPanel(this)
                .child(IKey.lang(getMetaFullName()).asWidget().pos(5, 5)) // 标题
                .child(new ProgressWidget()
                        .texture(getEmptyBarDrawable(), GTGuiTextures.PROGRESS_BAR_BOILER_HEAT, -1)
                        .direction(ProgressWidget.Direction.UP)
                        .debugName("temp")
                        .value(new DoubleSyncValue(this::getTemperaturePercent)) // 温度进度条
                        .pos(96, 26)
                        .size(10, 54))
                .child(new ItemSlot()
                        .debugName("fluid in")
                        .background(getSlotBackground(false))
                        .slot(new ModularSlot(containerInventory, 0)
                                .singletonSlotGroup()) // 流体输入槽
                        .pos(43, 26))
                .child(new ItemSlot()
                        .debugName("fluid out")
                        .background(getSlotBackground(true))
                        .slot(new ModularSlot(containerInventory, 1)
                                .accessibility(false, true)) // 流体输出槽，只可取出
                        .pos(43, 62))
                .child(new Widget<>()
                        .pos(43, 44)
                        .size(18)
                        .background(isHighPressure ? GTGuiTextures.CANISTER_OVERLAY_STEEL :
                                GTGuiTextures.CANISTER_OVERLAY_BRONZE)) // 中间装饰
                .bindPlayerInventory(); // 绑定玩家物品栏
    }

    @Override
    public GTGuiTheme getUITheme() {
        return isHighPressure ? GTGuiTheme.STEEL : GTGuiTheme.BRONZE; // 根据类型返回主题
    }

    /**
     * 获取空进度条纹理
     */
    protected UITexture getEmptyBarDrawable() {
        return isHighPressure ? GTGuiTextures.PROGRESS_BAR_BOILER_EMPTY_STEEL :
                GTGuiTextures.PROGRESS_BAR_BOILER_EMPTY_BRONZE;
    }

    /**
     * 获取槽位背景
     *
     * @param output 是否为输出槽
     */
    protected IDrawable getSlotBackground(boolean output) {
        UITexture base = isHighPressure ? GTGuiTextures.SLOT_STEEL : GTGuiTextures.SLOT_BRONZE;
        UITexture overlay;
        if (isHighPressure)
            overlay = output ? GTGuiTextures.OUT_SLOT_OVERLAY_STEEL : GTGuiTextures.IN_SLOT_OVERLAY_STEEL;
        else overlay = output ? GTGuiTextures.OUT_SLOT_OVERLAY_BRONZE : GTGuiTextures.IN_SLOT_OVERLAY_BRONZE;
        return IDrawable.of(base, overlay); // 基础纹理+覆盖层
    }

    /**
     * 添加物品信息提示
     */
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        // 显示每秒产生的热量
        tooltip.add(I18n.format("gregtech.universal.tooltip.heat_out_till", GTValues.V[tier] * 20));
        tooltip.add(I18n.format("gregtech.universal.tooltip.max_temperature", heatable.getMaxTemperature()));
        tooltip.add(I18n.format("gregtech.universal.tooltip.heat_storage_capacity", heatable.getHeatCapacity()));
        tooltip.add(I18n.format("gregtech.universal.tooltip.produces_heat", getBaseHeatOutput()));
        tooltip.add(I18n.format("metaitem.tool.tooltip.primary_material", material.getLocalizedName()));
    }

    /**
     * 添加工具使用提示
     */
    @Override
    public void addToolUsages(ItemStack stack, @Nullable World world, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.access_covers")); // 螺丝刀打开覆盖板
        tooltip.add(I18n.format("gregtech.tool_action.wrench.set_facing"));        // 扳手调整朝向
        super.addToolUsages(stack, world, tooltip, advanced);
    }

    /**
     * 获取工作声音
     */
    @Override
    public SoundEvent getSound() {
        return GTSoundEvents.BOILER; // 锅炉工作音效
    }

    /**
     * 清空机器物品栏
     */
    @Override
    public void clearMachineInventory(@NotNull List<@NotNull ItemStack> itemBuffer) {
        super.clearMachineInventory(itemBuffer);
        clearInventory(itemBuffer, containerInventory); // 清空容器物品栏
    }

    /**
     * 获取数据信息（用于TOP等显示）
     */
    @NotNull
    @Override
    public List<ITextComponent> getDataInfo() {
        return Collections.singletonList(new TextComponentTranslation("gregtech.machine.steam_boiler.heat_amount",
                TextFormattingUtil.formatNumbers((int) (this.getTemperaturePercent() * 100)))); // 显示热量百分比
    }

    /**
     * 客户端随机显示效果（粒子、声音等）
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick() {
        if (this.isActive()) {
            // 根据压力类型选择烟雾粒子
            EnumParticleTypes smokeParticle = isHighPressure ? EnumParticleTypes.SMOKE_LARGE :
                    EnumParticleTypes.SMOKE_NORMAL;
            // 在前方显示烟雾和火焰粒子
            VanillaParticleEffects.defaultFrontEffect(this, smokeParticle, EnumParticleTypes.FLAME);

            // 随机播放火焰音效
            if (ConfigHolder.machines.machineSounds && GTValues.RNG.nextDouble() < 0.1) {
                BlockPos pos = getPos();
                getWorld().playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
                        SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }
        }
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability.equals(CAPABILITY_HEAT_CONTAINER)) {
            return CAPABILITY_HEAT_CONTAINER.cast(heatable);
        }
        return super.getCapability(capability, side);
    }
}
