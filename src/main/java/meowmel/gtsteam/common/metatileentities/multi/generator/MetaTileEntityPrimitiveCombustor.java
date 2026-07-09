package meowmel.gtsteam.common.metatileentities.multi.generator;

import gregtech.api.pattern.element.StructureDefinition;

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
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.SliderWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import gregtech.api.capability.IControllable;
import gregtech.api.capability.IHeatMachine;
import gregtech.api.capability.IHeatable;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.metatileentity.MTETrait;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.metatileentity.multiblock.ProgressBarMultiblock;
import gregtech.api.metatileentity.multiblock.ui.*;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuiTheme;
import gregtech.api.mui.GTGuis;
import gregtech.api.pattern.FormedStructureView;
import gregtech.api.pattern.TemplatePool;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.util.KeyUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.api.util.tooltips.AbstractTooltipComponent;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.core.sound.GTSoundEvents;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import static gregtech.api.util.RelativeDirection.*;
import static meowmel.gtsteam.common.metatileentities.multi.generator.PrimitiveCombustorType.*;

public class MetaTileEntityPrimitiveCombustor extends MultiblockWithDisplayBase implements ProgressBarMultiblock,
        IControllable, IHeatMachine {

    private static final Map<PrimitiveCombustorType, StructureDefinition<?>> STRUCTURE_DEFINITIONS = new HashMap<>();

    static {
        STRUCTURE_DEFINITIONS.put(LOW_PRESSURE_SOLID, StructureDefinition.getOrBuild(
                "gtsteam:solid_combustor.low_pressure", () -> buildStructureDefinition(LOW_PRESSURE_SOLID)));
        STRUCTURE_DEFINITIONS.put(HIGH_PRESSURE_SOLID, StructureDefinition.getOrBuild(
                "gtsteam:solid_combustor.high_pressure", () -> buildStructureDefinition(HIGH_PRESSURE_SOLID)));
        STRUCTURE_DEFINITIONS.put(LOW_PRESSURE_FLUID, StructureDefinition.getOrBuild(
                "gtsteam:fluid_combustor.low_pressure", () -> buildStructureDefinition(LOW_PRESSURE_FLUID)));
        STRUCTURE_DEFINITIONS.put(HIGH_PRESSURE_FLUID, StructureDefinition.getOrBuild(
                "gtsteam:fluid_combustor.high_pressure", () -> buildStructureDefinition(HIGH_PRESSURE_FLUID)));
    }

    public final PrimitiveCombustorType boilerType;
    protected PrimitiveCombustorRecipeLogic recipeLogic;
    List<IHeatable> heatHatch = null;
    private FluidTankList fluidImportInventory;
    private ItemHandlerList itemImportInventory;
    private int throttlePercentage = 100;

    public MetaTileEntityPrimitiveCombustor(ResourceLocation metaTileEntityId, PrimitiveCombustorType combustorType) {
        super(metaTileEntityId);
        this.boilerType = combustorType;
        this.recipeLogic = new PrimitiveCombustorRecipeLogic(this, combustorType.isHighPressure());
        resetTileAbilities();
    }

    private static StructureDefinition<?> buildStructureDefinition(PrimitiveCombustorType boilerType) {

        if (boilerType == LOW_PRESSURE_SOLID || boilerType == HIGH_PRESSURE_SOLID) {
            return DeclarativePatternBuilder.start(RIGHT, UP, BACK)
                    .aisle("XXX", "CCC", "CCC", "CCC")
                    .aisle("XXX", "C C", "C C", "CCC")
                    .aisle("XXX", "CSC", "CCC", "CCC")
                    .self('S', MetaTileEntityPrimitiveCombustor.class)
                    .where('X', chain(blocks(boilerType.fireboxState),
                            abilities(1, 1, MultiblockAbility.IMPORT_ITEMS)))
                    .where('C', chain(blocks(boilerType.casingState),
                            abilities(1, 6, MultiblockAbility.OUTPUT_HEAT)))
                    .where(' ', air())
                    .buildStructureDefinition();
        } else {
            return DeclarativePatternBuilder.start(RIGHT, UP, BACK)
                    .aisle("XXX", "CCC", "CCC", "CCC")
                    .aisle("XXX", "C C", "C C", "CCC")
                    .aisle("XXX", "CSC", "CCC", "CCC")
                    .self('S', MetaTileEntityPrimitiveCombustor.class)
                    .where('X', chain(blocks(boilerType.fireboxState),
                            abilities(1, 1, MultiblockAbility.IMPORT_FLUIDS)))
                    .where('C', chain(blocks(boilerType.casingState),
                            abilities(1, 6, MultiblockAbility.OUTPUT_HEAT)))
                    .where(' ', air())
                    .buildStructureDefinition();
        }
    }

    @Override
    public IBlockState getCasingBlock() {
        return boilerType.casingState;
    }

    public List<IHeatable> getHeatHatch() {
        return heatHatch;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityPrimitiveCombustor(metaTileEntityId, boilerType);
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

        if (!getAbilities(MultiblockAbility.OUTPUT_HEAT).isEmpty())
            this.heatHatch = getAbilities(MultiblockAbility.OUTPUT_HEAT);
        else this.heatHatch = null;
    }

    private void resetTileAbilities() {
        this.fluidImportInventory = new FluidTankList(true);
        this.itemImportInventory = new ItemHandlerList(Collections.emptyList());
        this.heatHatch = null;
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
            if (isStructureFormed() && syncer.syncBoolean(getHeatStored() == 0)) {
                manager.add(KeyUtil.lang(TextFormatting.YELLOW,
                        "gregtech.multiblock.heat_multiblock.no_heat"));
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
                            .addTooltipLine(IKey.lang("gtsteam.multiblock.large_combustor.throttle_button.tooltip"))
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
            int heat = syncer.syncInt(recipeLogic.getLastTickHeat());
            int heatScaled = syncer.syncInt(recipeLogic.getHeatScaled());
            int throttleAmt = syncer.syncInt(getThrottle());

            // Steam Output line
            IKey steamOutput = KeyUtil.number(TextFormatting.AQUA,
                    heat, " HU/t");

            keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                    "热产量：%s", steamOutput));

            // Efficiency line
            IKey efficiency = KeyUtil.number(
                    getNumberColor(heatScaled), heatScaled, "%");
            keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                    "gtsteam.multiblock.large_combustor.efficiency", efficiency));

            // Throttle line
            IKey throttle = KeyUtil.number(
                    getNumberColor(throttleAmt),
                    throttleAmt, "%");
            keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                    "gtsteam.multiblock.large_combustor.throttle", throttle));
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
                        .child(IKey.lang("gtsteam.multiblock.large_combustor.throttle.title")
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

    @NotNull
    @Override
    protected StructureDefinition<?> createStructureDefinition() {
        StructureDefinition<?> definition = STRUCTURE_DEFINITIONS.get(boilerType);
        if (definition == null) {
            throw new IllegalStateException("Unknown turbine type: " + boilerType);
        }
        return definition;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    @Override
    public String[] getDescription() {
        return new String[]{I18n.format("gtsteam.multiblock.large_combustor.description")};
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        if (boilerType == LOW_PRESSURE_FLUID || boilerType == HIGH_PRESSURE_FLUID) {
            tooltip.add(TextFormatting.GREEN + I18n.format("-原始流体燃烧室："));
            tooltip.add(I18n.format("只运行流体配方，无法运行固体配方"));
        }
        if (boilerType == LOW_PRESSURE_SOLID || boilerType == HIGH_PRESSURE_SOLID) {
            tooltip.add(TextFormatting.GREEN + I18n.format("-原始固体锅炉燃烧室："));
            tooltip.add(I18n.format("只运行固体配方，无法运行流体配方"));
        }
        TooltipBuilder.create().add(new BoilerInformation()).build(this, tooltip);
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
        return Textures.PRIMITIVE_BLAST_FURNACE_OVERLAY;
    }

    @Override
    public IBlockState getCasingBlock(@Nullable IMultiblockPart sourcePart) {
        if (sourcePart != null && isFireboxPart(sourcePart)) {
            return boilerType.fireboxState;
        }
        return boilerType.casingState;
    }

    private boolean isFireboxPart(IMultiblockPart sourcePart) {
        return isStructureFormed() && (((MetaTileEntity) sourcePart).getPos().getY() < getPos().getY());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        if (sourcePart != null && isFireboxPart(sourcePart)) {
            return isActive() ? boilerType.fireboxActiveRenderer : boilerType.fireboxIdleRenderer;
        }
        return boilerType.casingRenderer;
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
        data.setInteger("ThrottlePercentage", throttlePercentage);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        throttlePercentage = data.getInteger("ThrottlePercentage");
        super.readFromNBT(data);
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeVarInt(throttlePercentage);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        throttlePercentage = buf.readVarInt();
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
    protected boolean shouldUpdate(MTETrait trait) {
        return !(trait instanceof PrimitiveCombustorRecipeLogic);
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
        LongSyncValue heatFilledValue = new LongSyncValue(this::getTemperature);
        LongSyncValue heatCapacityValue = new LongSyncValue(this::getMaxTemperature);
        syncManager.syncValue("heat_filled", heatFilledValue);
        syncManager.syncValue("heat_capacity", heatCapacityValue);

        bars.add(barTest -> barTest
                .progress(() -> heatCapacityValue.getIntValue() == 0 ? 0 :
                        heatFilledValue.getIntValue() * 1.0 / heatCapacityValue.getIntValue())
                .texture(GTGuiTextures.PROGRESS_BAR_HEAT_TEMP)
                .tooltipBuilder(tooltip -> {
                    if (isStructureFormed()) {
                        if (heatFilledValue.getIntValue() == 0) {
                            tooltip.addLine(IKey.lang("gregtech.multiblock.heat_multiblock.no_heat"));
                        } else {
                            tooltip.addLine(IKey.lang("gregtech.multiblock.heat_multiblock.heat_bar_hover",
                                    heatFilledValue.getIntValue(), heatCapacityValue.getIntValue()));
                        }
                    } else {
                        tooltip.addLine(IKey.lang("gregtech.multiblock.invalid_structure"));
                    }
                }));
    }

    public void changeHeat(long recipeEUt) {
        if (this.getHeatHatch() == null) return;
        long average = recipeEUt / heatHatch.size();
        this.getHeatHatch().forEach(hatch -> hatch.changeHeat(average));
    }

    public long getHeatStored() {
        if (this.getHeatHatch() == null) return 0;
        return this.getHeatHatch()
                .stream()
                .mapToLong(IHeatable::getHeatStored)
                .sum();
    }

    public long getHeatCapacity() {
        if (this.getHeatHatch() == null) return 0;
        return this.getHeatHatch()
                .stream()
                .mapToLong(IHeatable::getHeatCapacity)
                .sum();
    }

    public int getTemperature() {
        if (this.getHeatHatch() == null) return 293;
        return this.getHeatHatch()
                .stream()
                .mapToInt(IHeatable::getTemperature)
                .max()
                .orElse(293);
    }

    public int getMaxTemperature() {
        if (this.getHeatHatch() == null) return 293;
        return this.getHeatHatch()
                .stream()
                .mapToInt(IHeatable::getMaxTemperature)
                .max()
                .orElse(293);
    }

    @Override
    public boolean isWorkingEnabled() {
        return recipeLogic.isWorkingEnabled();
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        recipeLogic.setWorkingEnabled(isWorkingAllowed);
    }

    public class BoilerInformation extends AbstractTooltipComponent {

        @Override
        public void addInformation(MetaTileEntity metaTileEntity, List<String> tooltip) {
            tooltip.add(I18n.format("每块煤炭可以生产 %s HU 的热量",
                    TextFormattingUtil
                            .formatNumbers((int) (boilerType.heatPerTick() * 20 * boilerType.runtimeBoost(200) / 20.0))));
            tooltip.add(
                    I18n.format("锅炉最大温度：%s K", boilerType.getMaxTemp()));
            tooltip.add(I18n.format("gregtech.universal.tooltip.produces_heat", boilerType.heatPerTick()));
        }
    }
}
