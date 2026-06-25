package meowmel.gtsteam.common.metatileentities.multi.heat;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.google.common.collect.Lists;
import gregtech.api.capability.*;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.FluidDrillLogic;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.metatileentity.multiblock.ProgressBarMultiblock;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.metatileentity.multiblock.ui.TemplateBarBuilder;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuiTheme;
import gregtech.api.pattern.BlockPatternTemplate;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.SoftTemplate;
import gregtech.api.pattern.TemplatePool;
import gregtech.api.pattern.casing.CasingDefinition;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.KeyUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.api.util.tooltips.AbstractTooltipComponent;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.MetaBlocks;
import lombok.Getter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

public class MetaTileEntityHeatFluidDrill extends MultiblockWithDisplayBase implements IWorkable, ProgressBarMultiblock {

    private static final SoftTemplate TEMPLATE = TemplatePool.getInstance().register("gtsteam:heat_fluid_drill", () ->
            DeclarativePatternBuilder.start()
                    .aisle("FF           FF", "FF           FF", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                    .aisle("FF           FF", "FF           FF", " FF         FF ", " FF         FF ", "               ", "     FFFFF     ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                    .aisle("               ", "               ", " FF         FF ", " FF         FF ", "  FFFFFFFFFFF  ", "    FF   FF    ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                    .aisle("               ", "               ", "               ", "               ", "  FFF     FFF  ", "   FFFFFFFFF   ", "   FF     FF   ", "   FF     FF   ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                    .aisle("               ", "               ", "               ", "               ", "  FF       FF  ", "  FF       FF  ", "   FF     FF   ", "   FF     FF   ", "    FF   FF    ", "    FF   FF    ", "               ", "               ", "               ", "               ", "               ", "               ")
                    .aisle("               ", "               ", "               ", "               ", "  F         F  ", " FFF  CCC  FFF ", "      CCC      ", "      CCC      ", "    FFCCCFF    ", "    FFF FFF    ", "     F F F     ", "     FF FF     ", "               ", "               ", "               ", "               ")
                    .aisle("               ", "               ", "       C       ", "       C       ", "  F   CCC   F  ", " F F CCCCC F F ", "     CPPPC     ", "     CPPPC     ", "     CCPCC     ", "     F C F     ", "       C       ", "     F C F     ", "       C       ", "       G       ", "       G       ", "      GGG      ")
                    .aisle("       P       ", "       P       ", "      CPC      ", "      CPC      ", "  F   CPC   F  ", " F F CCPCC F F ", "     CPPPC     ", "     CPPPC     ", "     CPPPC     ", "      CPC      ", "     FCPCF     ", "      CPC      ", "      CPC      ", "      GPG      ", "      GPG      ", "      GPG      ")
                    .aisle("               ", "               ", "       C       ", "       C       ", "  F   CCC   F  ", " F F CCCCC F F ", "     CPPPC     ", "     CPPPC     ", "     CCPCC     ", "     F C F     ", "       C       ", "     F C F     ", "       C       ", "       G       ", "       G       ", "      GGG      ")
                    .aisle("               ", "               ", "               ", "               ", "  F         F  ", " FFF  CCC  FFF ", "      CCC      ", "      CSC      ", "    FFCCCFF    ", "    FFF FFF    ", "     F F F     ", "     FF FF     ", "               ", "               ", "               ", "               ")
                    .aisle("               ", "               ", "               ", "               ", "  FF       FF  ", "  FF       FF  ", "   FF     FF   ", "   FF     FF   ", "    FF   FF    ", "    FF   FF    ", "               ", "               ", "               ", "               ", "               ", "               ")
                    .aisle("               ", "               ", "               ", "               ", "  FFF     FFF  ", "   FFFFFFFFF   ", "   FF     FF   ", "   FF     FF   ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                    .aisle("               ", "               ", " FF         FF ", " FF         FF ", "  FFFFFFFFFFF  ", "    FF   FF    ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                    .aisle("FF           FF", "FF           FF", " FF         FF ", " FF         FF ", "               ", "     FFFFF     ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                    .aisle("FF           FF", "FF           FF", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ", "               ")
                    .self('S', MetaTileEntityHeatFluidDrill.class)
                    .casing('C', CasingDefinition.simple(getCasingState()))
                    .fluidOutput(1)
                    .hatch(MultiblockAbility.INPUT_HEAT, 1)
                    .where('P', states(getPipeCasingState()))
                    .where('F', frames(Materials.Steel))
                    .where('G', states(getGrateState()))
                    .where(' ', any())
                    .buildTemplate()
    );
    private final HeatFluidDrillLogic minerLogic;
    protected IMultipleTankHandler inputFluidInventory;
    protected IMultipleTankHandler outputFluidInventory;
    protected IEnergyContainer energyContainer;
    @Getter
    protected List<IHeatable> heatHatch = null;

    public MetaTileEntityHeatFluidDrill(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        minerLogic = new HeatFluidDrillLogic(this);
    }

    protected static IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    protected static IBlockState getGrateState() {
        return MetaBlocks.MULTIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.GRATE_CASING);
    }

    protected static IBlockState getPipeCasingState() {
        return MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE);
    }

    protected void initializeAbilities() {
        this.inputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.outputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.EXPORT_FLUIDS));
        this.energyContainer = new EnergyContainerList(getAbilities(MultiblockAbility.INPUT_ENERGY));
        if (!this.getAbilities(MultiblockAbility.INPUT_HEAT).isEmpty()) {
            this.heatHatch = this.getAbilities(MultiblockAbility.INPUT_HEAT);
        } else if (!this.getAbilities(MultiblockAbility.OUTPUT_HEAT).isEmpty()) {
            this.heatHatch = this.getAbilities(MultiblockAbility.OUTPUT_HEAT);
        } else {
            this.heatHatch = null;
        }
    }

    private void resetTileAbilities() {
        this.inputFluidInventory = new FluidTankList(true);
        this.outputFluidInventory = new FluidTankList(true);
        this.energyContainer = new EnergyContainerList(Lists.newArrayList());
        this.heatHatch = null;
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        initializeAbilities();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        resetTileAbilities();
    }

    @Override
    public int getProgress() {
        return minerLogic.getProgressTime();
    }

    @Override
    public int getMaxProgress() {
        return FluidDrillLogic.MAX_PROGRESS;
    }

    @Override
    protected void updateFormedValid() {
        this.minerLogic.performDrilling();
        if (!getWorld().isRemote && this.minerLogic.wasActiveAndNeedsUpdate()) {
            this.minerLogic.setWasActiveAndNeedsUpdate(false);
            this.minerLogic.setActive(false);
        }
    }

    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.setWorkingStatus(minerLogic.isWorkingEnabled(), minerLogic.isActive())
                .setWorkingStatusKeys(
                        "gregtech.multiblock.idling",
                        "gregtech.multiblock.work_paused",
                        "gregtech.multiblock.miner.drilling")
                .addEnergyUsageLine(energyContainer)
                .addCustom((keyManager, syncer) -> {
                    if (!isStructureFormed()) return;

                    // Fluid name
                    Fluid drilledFluid = syncer.syncFluid(minerLogic.getDrilledFluid());
                    if (drilledFluid == null) {
                        IKey noFluid = KeyUtil.lang(TextFormatting.RED,
                                "gregtech.multiblock.fluid_rig.no_fluid_in_area");

                        keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                                "gregtech.multiblock.fluid_rig.drilled_fluid",
                                noFluid));
                        return;
                    }

                    IKey fluidInfo = KeyUtil.fluid(drilledFluid).style(TextFormatting.GREEN);

                    keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                            "gregtech.multiblock.fluid_rig.drilled_fluid",
                            fluidInfo));

                    int fluidProduce = syncer.syncInt(minerLogic.getFluidToProduce());

                    IKey amountInfo = KeyUtil.number(TextFormatting.BLUE,
                            fluidProduce * 20L / FluidDrillLogic.MAX_PROGRESS, " L/s");

                    keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                            "gregtech.multiblock.fluid_rig.fluid_amount",
                            amountInfo));
                })
                .addProgressLine(minerLogic.getProgressTime(), FluidDrillLogic.MAX_PROGRESS)
                .addWorkingStatusLine();
    }

    @Override
    protected void configureWarningText(MultiblockUIBuilder builder) {
        builder.addLowPowerLine(this::isStructureFormed)
                .addCustom((list, syncer) -> {
                    if (isStructureFormed() && syncer.syncBoolean(minerLogic.isInventoryFull())) {
                        list.add(KeyUtil.lang(TextFormatting.YELLOW, "gregtech.machine.miner.invfull"));
                    }
                });
        builder.addCustom((manager, syncer) -> {
            if (isStructureFormed() && syncer.syncBoolean(getHeatStored() == 0)) {
                manager.add(KeyUtil.lang(TextFormatting.YELLOW,
                        "gregtech.multiblock.heat_multiblock.no_heat"));
            }
        });
        super.configureWarningText(builder);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.PUMP_OVERLAY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                this.minerLogic.isActive(), this.minerLogic.isWorkingEnabled());
    }

    @Override
    public boolean isWorkingEnabled() {
        return this.minerLogic.isWorkingEnabled();
    }

    @Override
    public void setWorkingEnabled(boolean isActivationAllowed) {
        this.minerLogic.setWorkingEnabled(isActivationAllowed);
    }

    public boolean fillTanks(FluidStack stack, boolean simulate) {
        return GTTransferUtils.addFluidsToFluidHandler(outputFluidInventory, simulate,
                Collections.singletonList(stack));
    }

    @Override
    protected @NotNull BlockPatternTemplate createStructureTemplate() {
        return TEMPLATE.get();
    }


    public void changeHeat(long amount) {
        if (this.getHeatHatch() != null) {
            long average = amount / (long) this.getHeatHatch().size();
            this.getHeatHatch().forEach((hatch) -> hatch.changeHeat(average));
        }
    }

    public long getHeatStored() {
        return this.getHeatHatch() == null ? 0L : this.getHeatHatch().stream().mapToLong(IHeatable::getHeatStored).sum();
    }

    public long getHeatCapacity() {
        return this.getHeatHatch() == null ? 0L : this.getHeatHatch().stream().mapToLong(IHeatable::getHeatCapacity).sum();
    }

    public int getTemperature() {
        return this.getHeatHatch() == null ? 293 : this.getHeatHatch().stream().mapToInt(IHeatable::getTemperature).max().orElse(293);
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityHeatFluidDrill(metaTileEntityId);
    }

    @Override
    public int getProgressBarCount() {
        return 1;
    }

    @Override
    public GTGuiTheme getUITheme() {
        return GTGuiTheme.STEEL;
    }


    @Override
    public void registerBars(List<UnaryOperator<TemplateBarBuilder>> bars, PanelSyncManager syncManager) {
        LongSyncValue heatFilledValue = new LongSyncValue(this::getHeatStored);
        LongSyncValue heatCapacityValue = new LongSyncValue(this::getHeatCapacity);
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

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == GregtechTileCapabilities.CAPABILITY_WORKABLE)
            return GregtechTileCapabilities.CAPABILITY_WORKABLE.cast(this);
        if (capability == GregtechTileCapabilities.CAPABILITY_CONTROLLABLE)
            return GregtechTileCapabilities.CAPABILITY_CONTROLLABLE.cast(this);
        return super.getCapability(capability, side);
    }

    @Override
    public boolean shouldShowVoidingModeButton() {
        return false;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        return this.minerLogic.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.minerLogic.readFromNBT(data);
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        this.minerLogic.writeInitialSyncData(buf);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.minerLogic.receiveInitialSyncData(buf);
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        this.minerLogic.receiveCustomData(dataId, buf);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        TooltipBuilder.create().add(new DrillInformation()).addHeatMachine(1).build(this, tooltip);
    }

    @Override
    public void addToolUsages(ItemStack stack, @Nullable World world, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.access_covers"));
        tooltip.add(I18n.format("gregtech.tool_action.wrench.set_facing"));
        super.addToolUsages(stack, world, tooltip, advanced);
    }

    public static class DrillInformation extends AbstractTooltipComponent {

        @Override
        public void addInformation(MetaTileEntity metaTileEntity, List<String> tooltip) {
            tooltip.add(I18n.format("gregtech.machine.fluid_drilling_rig.description"));
            tooltip.add(I18n.format("gregtech.machine.fluid_drilling_rig.depletion", TextFormattingUtil.formatNumbers(100)));
            tooltip.add(I18n.format("gregtech.machine.fluid_drilling_rig.production", 1, TextFormattingUtil.formatNumbers(1.5)));
        }
    }
}
