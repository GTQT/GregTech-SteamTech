package meowmel.gtsteam.common.metatileentities.multi.generator;

import static gregtech.api.pattern.element.Elements.*;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.modularui.api.GuiAxis;
import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.SliderWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import gregtech.api.capability.IControllable;
import gregtech.api.capability.ISteamMachine;
import gregtech.api.capability.impl.CommonFluidFilters;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.metatileentity.MTETrait;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.metatileentity.multiblock.ui.*;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuiTheme;
import gregtech.api.mui.GTGuis;
import gregtech.api.pattern.FormedStructureView;
import gregtech.api.pattern.MultiblockShapeInfo;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.util.KeyUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.blocks.BlockMachineCasing;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.core.sound.GTSoundEvents;
import meowmel.gtsteam.api.capability.impl.SolarBoilerRecipeLogic;
import meowmel.gtsteam.common.block.GTSteamMetaBlocks;
import meowmel.gtsteam.common.block.blocks.BlockMultiblockCasing1;
import meowmel.gtsteam.common.metatileentities.GTSteamMetaTileEntities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

import static gregtech.api.GTValues.ULV;
import static gregtech.common.blocks.MetaBlocks.MACHINE_CASING;
import static net.minecraft.util.EnumFacing.*;

public class MetaTileEntitySteamSolarBoiler extends MultiblockWithDisplayBase implements ProgressBarMultiblock,
        IControllable, ISteamMachine {
    public static final int STEAM_PER_BLOCK = 10;

    public static final int HEAT_INCREMENT_PER_BLOCK = 5;
    public static final int HEAT_REDUCTION_PER_BLOCK = 2;
    public static final int HEAT_MAXIMUM_PER_BLOCK = 10000;

    protected SolarBoilerRecipeLogic recipeLogic;
    private FluidTankList fluidImportInventory;
    private ItemHandlerList itemImportInventory;
    private FluidTankList steamOutputTank;

    private int Length = 0;
    private int Width = 0;

    private int throttlePercentage = 100;

    public MetaTileEntitySteamSolarBoiler(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        this.recipeLogic = new SolarBoilerRecipeLogic(this);
        resetTileAbilities();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntitySteamSolarBoiler(metaTileEntityId);
    }

    @Override
    protected void formStructure(@NotNull FormedStructureView formed) {
        super.formStructure(formed);
        initializeAbilities();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        resetTileAbilities();
        this.throttlePercentage = 100;
        this.recipeLogic.invalidate();
    }

    private void initializeAbilities() {
        this.fluidImportInventory = new FluidTankList(true, getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.itemImportInventory = new ItemHandlerList(getAbilities(MultiblockAbility.IMPORT_ITEMS));
        this.steamOutputTank = new FluidTankList(true, getAbilities(MultiblockAbility.EXPORT_FLUIDS));
    }

    private void resetTileAbilities() {
        this.fluidImportInventory = new FluidTankList(true);
        this.itemImportInventory = new ItemHandlerList(Collections.emptyList());
        this.steamOutputTank = new FluidTankList(true);
    }

    private TextFormatting getNumberColor(int number) {
        if (number == 0) {
            return TextFormatting.DARK_RED;
        } else if (number <= 40) {
            return TextFormatting.RED;
        } else if (number < 100) {
            return TextFormatting.YELLOW;
        } else {
            return TextFormatting.GREEN;
        }
    }

    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.setWorkingStatus(recipeLogic.isWorkingEnabled(), recipeLogic.isActive())
                .addCustom(this::addCustomData)
                .addWorkingStatusLine();
    }

    @Override
    protected void configureWarningText(MultiblockUIBuilder builder) {
        super.configureWarningText(builder);
        builder.addCustom((manager, syncer) -> {
            if (isStructureFormed() && syncer.syncBoolean(getWaterFilled() == 0)) {
                manager.add(KeyUtil.lang(TextFormatting.YELLOW,
                        "gregtech.multiblock.large_boiler.no_water"));
                manager.add(KeyUtil.lang(TextFormatting.GRAY,
                        "gregtech.multiblock.large_boiler.explosion_tooltip"));
            }
        });
    }

    @Override
    public GTGuiTheme getUITheme() {
        return GTGuiTheme.STEEL;
    }

    @Override
    protected MultiblockUIFactory createUIFactory() {
        return super.createUIFactory()
                .createFlexButton((guiData, syncManager) -> {
                    var throttle = syncManager.syncedPanel("throttle_panel", true, this::makeThrottlePanel);

                    return new ButtonWidget<>()
                            .size(18)
                            .overlay(GTGuiTextures.FILTER_SETTINGS_OVERLAY.asIcon().size(16))
                            .addTooltipLine(IKey.lang("gregtech.multiblock.large_boiler.throttle_button.tooltip"))
                            .onMousePressed(i -> {
                                if (throttle.isPanelOpen()) {
                                    throttle.closePanel();
                                } else {
                                    throttle.openPanel();
                                }
                                return true;
                            });
                });
    }

    private void addCustomData(KeyManager keyManager, UISyncer syncer) {
        if (isStructureFormed()) {
            int steam = syncer.syncInt(recipeLogic.getLastTickSteam());
            int heatScaled = syncer.syncInt(recipeLogic.getHeatScaled());
            int throttleAmt = syncer.syncInt(getThrottle());

            // Steam Output line
            IKey steamOutput = KeyUtil.number(TextFormatting.AQUA,
                    steam, " L/t");

            keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                    "gregtech.multiblock.large_boiler.steam_output", steamOutput));

            // Efficiency line
            IKey efficiency = KeyUtil.number(
                    getNumberColor(heatScaled), heatScaled, "%");
            keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                    "gregtech.multiblock.large_boiler.efficiency", efficiency));

            // Throttle line
            IKey throttle = KeyUtil.number(
                    getNumberColor(throttleAmt),
                    throttleAmt, "%");
            keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                    "gregtech.multiblock.large_boiler.throttle", throttle));

            int length = syncer.syncInt(this.Length);
            int width = syncer.syncInt(this.Width);

            keyManager.add(KeyUtil.lang(TextFormatting.GRAY, "gtsteam.machine.large_fluid_tank.size", length, 1, width));
        }
    }

    private ModularPanel makeThrottlePanel(PanelSyncManager syncManager, IPanelHandler syncHandler) {
        StringSyncValue throttleValue = new StringSyncValue(() -> throttlePercentage + "%", str -> {
            try {
                if (str.charAt(str.length() - 1) == '%') {
                    str = str.substring(0, str.length() - 1);
                }

                this.throttlePercentage = Integer.parseInt(str);
            } catch (NumberFormatException ignored) {

            }
        });
        DoubleSyncValue sliderValue = new DoubleSyncValue(
                () -> (double) getThrottlePercentage() / 100,
                d -> setThrottlePercentage((int) (d * 100)));

        return GTGuis.createPopupPanel("boiler_throttle", 116, 53)
                .child(Flow.row()
                        .pos(4, 4)
                        .height(16)
                        .coverChildrenWidth()
                        .child(new ItemDrawable(getStackForm())
                                .asWidget()
                                .size(16)
                                .marginRight(4))
                        .child(IKey.lang("gregtech.multiblock.large_boiler.throttle.title")
                                .asWidget()
                                .heightRel(1.0f)))
                .child(Flow.row()
                        .top(20)
                        .margin(4, 0)
                        .coverChildrenHeight()
                        .child(new SliderWidget()
                                .background(new Rectangle().setColor(Color.BLACK.brighter(2)).asIcon()
                                        .height(8))
                                .bounds(0.2, 1)
                                .setAxis(GuiAxis.X)
                                .value(sliderValue)
                                .widthRel(0.7f)
                                .height(20))
                        // todo switch this text field with GTTextFieldWidget in PR #2700
                        .child(new TextFieldWidget()
                                .widthRel(0.3f)
                                .height(20)
                                // TODO proper color
                                .setTextColor(Color.WHITE.darker(1))
                                .setValidator(str -> {
                                    if (str.charAt(str.length() - 1) == '%') {
                                        str = str.substring(0, str.length() - 1);
                                    }

                                    try {
                                        long l = Long.parseLong(str);
                                        if (l < 20) l = 20;
                                        else if (l > 100) l = 100;
                                        return String.valueOf(l);
                                    } catch (NumberFormatException ignored) {
                                        return throttleValue.getValue();
                                    }
                                })
                                .value(throttleValue)
                                .background(GTGuiTextures.DISPLAY)));
    }

    private int getThrottlePercentage() {
        return this.throttlePercentage;
    }

    private void setThrottlePercentage(int amount) {
        this.throttlePercentage = Math.max(20, Math.min(amount, 100));
    }

    @Override
    public boolean isActive() {
        return super.isActive() && recipeLogic.isActive() && recipeLogic.isWorkingEnabled();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.VOLTAGE_CASINGS[0];
    }

    public boolean isBlockEdge(@NotNull World world, @NotNull BlockPos.MutableBlockPos pos,
                               @NotNull EnumFacing direction) {
        IBlockState block = world.getBlockState(pos.move(direction));
        TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof IGregTechTileEntity iGregTechTileEntity) {
            MetaTileEntity metaTileEntity = iGregTechTileEntity.getMetaTileEntity();
            if (metaTileEntity instanceof IMultiblockAbilityPart<?>) {
                return false;
            } else {
                return (block != getULVCasingState())
                        && (block != getSolarCasingState());
            }
        } else {
            return (block != getULVCasingState())
                    && (block != getSolarCasingState());
        }
    }

    @Override
    public void checkStructurePattern() {
        if (!this.isStructureFormed()) {
            reinitializeStructurePattern();
        }
        super.checkStructurePattern();
    }

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        if (getWorld() != null) updateStructureDimensions();
        DeclarativePatternBuilder pattern = DeclarativePatternBuilder.start();

        if (Width < 3 || Length < 3) {
            Width = 3;
            Length = 3;
        }
        if (Width > 15 || Length > 15) {
            Width = 15;
            Length = 15;
        }

        // 创建单层太阳能结构
        for (int i = 0; i < Width; i++) {
            StringBuilder str = new StringBuilder();
            for (int j = 0; j < Length; j++) {
                // 最底行中间位置放控制器
                if (i == Width - 1 && j == Length / 2) {
                    str.append('S');
                }
                // 边界（第一行、最后一行、第一列、最后一列）使用X
                else if (i == 0 || i == Width - 1 || j == 0 || j == Length - 1) {
                    str.append('X');
                }
                // 中间区域使用Y
                else {
                    str.append('Y');
                }
            }
            pattern.aisle(str.toString());
        }

        return pattern
                .self('S', MetaTileEntitySteamSolarBoiler.class)
                .where('X', chain(blocks(getULVCasingState()),
                        abilities(1, -1, 1, MultiblockAbility.IMPORT_FLUIDS),
                        abilities(1, -1, 1, MultiblockAbility.EXPORT_FLUIDS)))
                .where('Y', blocks(getSolarCasingState()))
                .buildStructureDefinition();
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        List<MultiblockShapeInfo> shapeInfo = new ArrayList<>();

        // 生成从3到15的所有奇数尺寸预览结构
        for (int size = 3; size <= 15; size += 2) {
            MultiblockShapeInfo.Builder builder = MultiblockShapeInfo.builder();

            // 构建每一层
            for (int i = 0; i < size; i++) {
                StringBuilder aisle = new StringBuilder();
                for (int j = 0; j < size; j++) {
                    // 最底行中间位置放控制器，两侧放舱室
                    if (i == size - 1) {
                        if (j == size / 2) {
                            aisle.append('S'); // 控制器
                        } else if (j == size / 2 - 1) {
                            aisle.append('M'); // 流体输入舱
                        } else if (j == size / 2 + 1) {
                            aisle.append('Q'); // 流体输出舱
                        } else {
                            // 最底行的其他位置按边界处理
                            aisle.append('X');
                        }
                    } else {
                        // 其他行：边界用X，内部用Y
                        if (i == 0 || i == size - 1 || j == 0 || j == size - 1) {
                            aisle.append('X'); // 边界
                        } else {
                            aisle.append('Y'); // 内部
                        }
                    }
                }
                builder.aisle(aisle.toString());
            }

            // 设置方块映射
            builder
                    .where('S', GTSteamMetaTileEntities.STEAM_SOLAR_BOILER, SOUTH)
                    .where('M', MetaTileEntities.FLUID_IMPORT_HATCH[ULV], SOUTH)
                    .where('Q', MetaTileEntities.FLUID_EXPORT_HATCH[ULV], SOUTH)
                    .where('X', getULVCasingState())
                    .where('Y', getSolarCasingState());

            shapeInfo.add(builder.build());
        }

        return shapeInfo;
    }

    private void updateStructureDimensions() {
        World world = getWorld();
        EnumFacing front = getFrontFacing();
        if (front == UP || front == DOWN) return;
        EnumFacing back = front.getOpposite();
        EnumFacing left = front.rotateYCCW();
        EnumFacing right = left.getOpposite();

        BlockPos.MutableBlockPos lPos = new BlockPos.MutableBlockPos(getPos());
        BlockPos.MutableBlockPos rPos = new BlockPos.MutableBlockPos(getPos());
        BlockPos.MutableBlockPos bPos = new BlockPos.MutableBlockPos(getPos());
        // 重置距离
        int lDist = 0;
        int rDist = 0;
        int bDist = 0;

        for (int i = 1; i <= 8; i++) {
            if (lDist == 0 && isBlockEdge(world, lPos, left)) lDist = i;
            if (rDist == 0 && isBlockEdge(world, rPos, right)) rDist = i;
            if (lDist != 0 && rDist != 0) break;
        }
        for (int i = 1; i <= 8 * 2 - 1; i++) {
            if ((isBlockEdge(world, bPos, back))) bDist = i;
            if (bDist != 0) break;
        }
        this.Length = lDist + rDist - 1;
        this.Width = bDist;
    }


    public IBlockState getULVCasingState() {
        return MACHINE_CASING.getState(BlockMachineCasing.MachineCasingType.ULV);
    }

    public IBlockState getSolarCasingState() {
        return GTSteamMetaBlocks.blockMultiblockCasing1.getState(BlockMultiblockCasing1.CasingType.SOLAR_COLLECTOR);
    }

    @Override
    public String[] getDescription() {
        return new String[]{I18n.format("gtsteam.multiblock.steam_solar_boiler.description")};
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(
                I18n.format("gtsteam.multiblock.steam_solar_boiler.heat_time_tooltip", this.getTicksToBoiling() / 20));
        tooltip.add(I18n.format("gtsteam.multiblock.steam_solar_boiler.structure_tooltip"));
        tooltip.add(I18n.format("gtsteam.multiblock.steam_solar_boiler.final_tooltip", STEAM_PER_BLOCK));
        tooltip.add(TooltipHelper.BLINKING_RED + I18n.format("gregtech.multiblock.large_boiler.explosion_tooltip"));
    }

    public int getTicksToBoiling() {
        return HEAT_MAXIMUM_PER_BLOCK / HEAT_INCREMENT_PER_BLOCK;
    }

    public int steamPerTick() {
        return (Length - 2) * (Width - 2) * STEAM_PER_BLOCK;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(), isActive(),
                recipeLogic.isWorkingEnabled());
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.LARGE_STEEL_BOILER;
    }

    @Override
    public SoundEvent getSound() {
        return GTSoundEvents.BOILER;
    }

    @Override
    protected void updateFormedValid() {
        this.recipeLogic.update();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("ThrottlePercentage", throttlePercentage);
        data.setInteger("Width", Width);
        data.setInteger("Length", Length);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        throttlePercentage = data.getInteger("ThrottlePercentage");
        this.Width = data.getInteger("Width");
        this.Length = data.getInteger("Length");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeVarInt(throttlePercentage);
        buf.writeVarInt(Width);
        buf.writeVarInt(Length);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        throttlePercentage = buf.readVarInt();
        Width = buf.readVarInt();
        Length = buf.readVarInt();
    }

    public int getThrottle() {
        return throttlePercentage;
    }

    @Override
    public IItemHandlerModifiable getImportItems() {
        return itemImportInventory;
    }

    @Override
    public FluidTankList getImportFluids() {
        return fluidImportInventory;
    }

    @Override
    public FluidTankList getExportFluids() {
        return steamOutputTank;
    }

    @Override
    protected boolean shouldUpdate(MTETrait trait) {
        return !(trait instanceof SolarBoilerRecipeLogic);
    }

    @Override
    public boolean shouldShowVoidingModeButton() {
        return false;
    }

    @Override
    public int getProgressBarCount() {
        return 1;
    }

    @Override
    public void registerBars(List<UnaryOperator<TemplateBarBuilder>> bars, PanelSyncManager syncManager) {
        IntSyncValue waterFilledValue = new IntSyncValue(this::getWaterFilled);
        IntSyncValue waterCapacityValue = new IntSyncValue(this::getWaterCapacity);
        syncManager.syncValue("water_filled", waterFilledValue);
        syncManager.syncValue("water_capacity", waterCapacityValue);

        bars.add(barTest -> barTest
                .progress(() -> waterCapacityValue.getIntValue() == 0 ? 0 :
                        waterFilledValue.getIntValue() * 1.0 / waterCapacityValue.getIntValue())
                .texture(GTGuiTextures.PROGRESS_BAR_FLUID_RIG_DEPLETION)
                .tooltipBuilder(tooltip -> {
                    if (isStructureFormed()) {
                        if (waterFilledValue.getIntValue() == 0) {
                            tooltip.addLine(IKey.lang("gregtech.multiblock.large_boiler.no_water"));
                        } else {
                            tooltip.addLine(IKey.lang("gregtech.multiblock.large_boiler.water_bar_hover",
                                    waterFilledValue.getIntValue(), waterCapacityValue.getIntValue()));
                        }
                    } else {
                        tooltip.addLine(IKey.lang("gregtech.multiblock.invalid_structure"));
                    }
                }));
    }

    /**
     * @return the total amount of water filling the inputs
     */
    private int getWaterFilled() {
        if (!isStructureFormed()) return 0;
        List<IFluidTank> tanks = getAbilities(MultiblockAbility.IMPORT_FLUIDS);
        int filled = 0;
        for (IFluidTank tank : tanks) {
            if (tank == null || tank.getFluid() == null) continue;
            if (CommonFluidFilters.BOILER_FLUID.test(tank.getFluid())) {
                filled += tank.getFluidAmount();
            }
        }
        return filled;
    }

    /**
     * @return the total capacity for water-containing inputs
     */
    private int getWaterCapacity() {
        if (!isStructureFormed()) return 0;
        List<IFluidTank> tanks = getAbilities(MultiblockAbility.IMPORT_FLUIDS);
        int capacity = 0;
        for (IFluidTank tank : tanks) {
            if (tank == null || tank.getFluid() == null) continue;
            if (CommonFluidFilters.BOILER_FLUID.test(tank.getFluid())) {
                capacity += tank.getCapacity();
            }
        }
        return capacity;
    }

    @Override
    public boolean isWorkingEnabled() {
        return recipeLogic.isWorkingEnabled();
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        recipeLogic.setWorkingEnabled(isWorkingAllowed);
    }
    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }
    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }
}
