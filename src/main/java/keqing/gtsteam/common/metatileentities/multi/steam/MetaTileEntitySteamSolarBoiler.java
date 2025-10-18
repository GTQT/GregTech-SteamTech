package keqing.gtsteam.common.metatileentities.multi.steam;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.groovyscript.compat.mods.draconicevolution.helpers.BlockStates;
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
import gregtech.GregTechMod;
import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.capability.IControllable;
import gregtech.api.capability.impl.*;
import gregtech.api.metatileentity.MTETrait;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.metatileentity.multiblock.ui.*;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuiTheme;
import gregtech.api.mui.GTGuis;
import gregtech.api.pattern.*;
import gregtech.api.util.GTLog;
import gregtech.api.util.KeyUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.blocks.BlockMachineCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.core.sound.GTSoundEvents;
import keqing.gtsteam.GTSteam;
import keqing.gtsteam.api.capability.impl.SolarBoilerRecipeLogic;
import keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
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
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static gregtech.api.GTValues.LV;
import static gregtech.api.GTValues.ULV;
import static gregtech.common.blocks.MetaBlocks.MACHINE_CASING;

public class MetaTileEntitySteamSolarBoiler extends MultiblockWithDisplayBase implements ProgressBarMultiblock,
        IControllable {
    public static final int STEAM_PER_BLOCK =  20;
    //private static final ICubeRenderer ULV_CASING_TEXTURES = new SimpleOverlayRenderer("casings/ulv_machine_hull");;
    public static final int HEAT_INCREMENT_PER_BLOCK = 5;
    public static final int HEAT_REDUCTION_PER_BLOCK = 2;
    public static final int HEAT_MAXIMUM_PER_BLOCK = 10000;
    protected SolarBoilerRecipeLogic recipeLogic;
    private FluidTankList fluidImportInventory;
    private ItemHandlerList itemImportInventory;
    private FluidTankList steamOutputTank;
    private int radius = 0;
    private int lDist = 0;
    private int rDist = 0;
    private int bDist = 0;
    private int fDist = 0;
    private int hDist = 0;
    private int throttlePercentage = 100;
    private final int MIN_RADIUS = 2;
    private final int MAX_RADIUS = 8;
    private @NotNull ICubeRenderer frontOverlay;
    public int getlDist(){
        return this.lDist;
    }
    public MetaTileEntitySteamSolarBoiler(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        this.frontOverlay = Textures.LARGE_STEEL_BOILER;
        this.recipeLogic = new SolarBoilerRecipeLogic(this);
        resetTileAbilities();
    }
    public int runtimeBoost(int tick) {
        return tick;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntitySteamSolarBoiler(metaTileEntityId);
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
                        "gtsteam.multiblock.steam_solar_boiler.no_water"));
                manager.add(KeyUtil.lang(TextFormatting.GRAY,
                        "gtsteam.multiblock.steam_solar_boiler.explosion_tooltip"));
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
                    var throttle = syncManager.panel("throttle_panel", this::makeThrottlePanel, true);

                    return new ButtonWidget<>()
                            .size(18)
                            .overlay(GTGuiTextures.FILTER_SETTINGS_OVERLAY.asIcon().size(16))
                            .addTooltipLine(IKey.lang("gtsteam.multiblock.steam_solar_boiler.throttle_button.tooltip"))
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
                    "gtsteam.multiblock.steam_solar_boiler.steam_output", steamOutput));

            // Efficiency line
            IKey efficiency = KeyUtil.number(
                    getNumberColor(heatScaled), heatScaled, "%");
            keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                    "gtsteam.multiblock.steam_solar_boiler.efficiency", efficiency));

            // Throttle line
            IKey throttle = KeyUtil.number(
                    getNumberColor(throttleAmt),
                    throttleAmt, "%");
            keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                    "gtsteam.multiblock.steam_solar_boiler.throttle", throttle));
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
                        .child(IKey.lang("gtsteam.multiblock.steam_solar_boiler.throttle.title")
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

    private void setThrottlePercentage(int amount) {
        this.throttlePercentage = Math.max(20, Math.min(amount, 100));
    }

    private int getThrottlePercentage() {
        return this.throttlePercentage;
    }
    public static String repeat(String a,int count){
        String b = "";
        for (int i = 0; i < count; i++) {
            b += a;
        }
        return b;
    }
    @Override
    public boolean isActive() {
        return super.isActive() && recipeLogic.isActive() && recipeLogic.isWorkingEnabled();
    }

    @Override
    protected BlockPattern createStructurePattern() {
        if (getWorld() != null) updateStructureDimensions();
        var pattern = FactoryBlockPattern.start();
        if (lDist < MIN_RADIUS) lDist = MIN_RADIUS;
        if (rDist < MIN_RADIUS) rDist = MIN_RADIUS;
        if (this.frontFacing == EnumFacing.EAST || this.frontFacing == EnumFacing.WEST) {
            int tmp = lDist;
            lDist = rDist;
            rDist = tmp;
        }

        if(lDist > MAX_RADIUS) lDist = MAX_RADIUS;
        for(int i = 0; i < lDist * 2 - 1;i++) {
            StringBuilder str = new StringBuilder();
            for (int j = 0; j < 1; j++) {
                if (i == (lDist * 2 - 1) - 1) {
                    str.append(repeat("X",(lDist - 1))).append("S").append(repeat("X",lDist - 1));
                    continue;
                }
                str.append(repeat("X",lDist * 2 - 1));
            }
            pattern = pattern.aisle(str.toString());
        }
       return    pattern
                .where('S',selfPredicate())
                .where('X',states(getULVCasingState())
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMinGlobalLimited(1,1))
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMinGlobalLimited(1,1))
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setMinGlobalLimited(1,1))
                        .or(abilities(MultiblockAbility.MUFFLER_HATCH).setMinGlobalLimited(1,1))
                )
                .build();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.VOLTAGE_CASINGS[0];
    }

    public boolean isBlockEdge(@NotNull World world, @NotNull BlockPos.MutableBlockPos pos,
                               @NotNull EnumFacing direction) {
        return world.getBlockState(pos.move(direction)).getBlock() == Blocks.AIR;
    }
    @Override
    public void checkStructurePattern() {
        if (!this.isStructureFormed()) {
            reinitializeStructurePattern();
        }
        super.checkStructurePattern();
    }
    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        ArrayList<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
        for(int lDist = 2;lDist <= MAX_RADIUS;lDist++){
            var pattern = MultiblockShapeInfo.builder();
            for(int i = 0; i < lDist * 2 - 1;i++) {
                StringBuilder str = new StringBuilder();
                for (int j = 0; j < 1; j++) {
                    if (i == (lDist * 2 - 1) - 1) {
                        str.append("M").append(repeat("X", (lDist - 2))).append("S").append(repeat("X", lDist - 2)).append("Q");
                        continue;
                    }
                    if(i == 0){
                        str.append("L").append(repeat("X", lDist * 2 - 3)).append("P");
                        continue;
                    }
                    str.append(repeat("X",lDist * 2 - 1));
                }
                pattern = pattern.aisle(str.toString());
            }
                    shapeInfo.add(pattern
                                    .where('S', GTSteamMetaTileEntities.STEAM_SOLAR_BOILER,EnumFacing.SOUTH)
                                    .where('X',getULVCasingState())
                                    .where('L', MetaTileEntities.MAINTENANCE_HATCH,EnumFacing.NORTH)
                                    .where('P',MetaTileEntities.MUFFLER_HATCH[LV],EnumFacing.NORTH)
                                    .where('M',MetaTileEntities.FLUID_IMPORT_HATCH[LV],EnumFacing.SOUTH)
                                    .where('Q',MetaTileEntities.FLUID_EXPORT_HATCH[LV],EnumFacing.SOUTH)

                    .build());
        }
        return shapeInfo;
    }
    private boolean updateStructureDimensions() {
        World world = getWorld();
        EnumFacing front = getFrontFacing();
        EnumFacing back = front.getOpposite();
        EnumFacing left = front.rotateYCCW();
        EnumFacing right = left.getOpposite();

        BlockPos.MutableBlockPos lPos = new BlockPos.MutableBlockPos(getPos());
        BlockPos.MutableBlockPos rPos = new BlockPos.MutableBlockPos(getPos());
        BlockPos.MutableBlockPos fPos = new BlockPos.MutableBlockPos(getPos());
        BlockPos.MutableBlockPos bPos = new BlockPos.MutableBlockPos(getPos());
        BlockPos.MutableBlockPos hPos = new BlockPos.MutableBlockPos(getPos());

        // find the distances from the controller to the plascrete blocks on one horizontal axis and the Y axis
        // repeatable aisles take care of the second horizontal axis
        int lDist = 0;
        int rDist = 0;
        int bDist = 0;
        int fDist = 0;
        int hDist = 0;

        // find the left, right, back, and front distances for the structure pattern
        // maximum size is 15x15x15 including walls, so check 7 block radius around the controller for blocks
        for (int i = 1; i <= MAX_RADIUS; i++) {
            if (lDist == 0 && isBlockEdge(world, lPos, left)) lDist = i;
            if(rDist == 0 && isBlockEdge(world, rPos, right)) rDist = i;
            if(lDist != 0 && rDist != 0) break;
        }

        // height is diameter instead of radius, so it needs to be done separately

        if (lDist < MIN_RADIUS || rDist < MIN_RADIUS) {
            invalidateStructure();
            return false;
        }
        this.lDist = lDist;
        this.rDist = rDist;
        return true;
    }

    public IBlockState getULVCasingState() {
        return MACHINE_CASING.getState(BlockMachineCasing.MachineCasingType.ULV);
    }
    @Override
    public String[] getDescription() {
        return new String[] { I18n.format("gtsteam.multiblock.steam_solar_boiler.description") };
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(
                I18n.format("gtsteam.multiblock.steam_solar_boiler.heat_time_tooltip", this.getTicksToBoiling() / 20));
        tooltip.add(I18n.format("gtsteam.multiblock.steam_solar_boiler.structure_tooltip"));
        tooltip.add(TooltipHelper.BLINKING_RED + I18n.format("gtsteam.multiblock.steam_solar_boiler.explosion_tooltip"));
        tooltip.add(I18n.format("gtsteam.multiblock.steam_solar_boiler.final_tooltip"));
    }
    public int getTicksToBoiling() {
         return HEAT_MAXIMUM_PER_BLOCK / HEAT_INCREMENT_PER_BLOCK;
    }

    public int steamPerTick() {
        int tmp = lDist * 2 - 1;
        return  tmp * tmp * STEAM_PER_BLOCK;
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
        return this.frontOverlay;
    }

    private boolean isFireboxPart(IMultiblockPart sourcePart) {
        return isStructureFormed() && (((MetaTileEntity) sourcePart).getPos().getY() < getPos().getY());
    }


    @Override
    public boolean hasMufflerMechanics() {
        return true;
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
        data.setInteger("lDist",lDist);
        data.setInteger("rDist",rDist);
        data.setInteger("bDist",bDist);
        data.setInteger("fDist",fDist);
        data.setInteger("hDist",hDist);
        return this.recipeLogic.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        throttlePercentage = data.getInteger("ThrottlePercentage");
        this.lDist = data.hasKey("lDist") ? data.getInteger("lDist") : this.lDist;
        this.rDist = data.hasKey("rDist") ? data.getInteger("rDist") : this.rDist;
        this.hDist = data.hasKey("hDist") ? data.getInteger("hDist") : this.hDist;
        this.bDist = data.hasKey("bDist") ? data.getInteger("bDist") : this.bDist;
        this.fDist = data.hasKey("fDist") ? data.getInteger("fDist") : this.fDist;
        this.recipeLogic.readFromNBT(data);
        reinitializeStructurePattern();

    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeVarInt(throttlePercentage);
        buf.writeVarInt(lDist);
        buf.writeVarInt(rDist);
        buf.writeVarInt(bDist);
        buf.writeVarInt(fDist);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        throttlePercentage = buf.readVarInt();
        lDist = buf.readVarInt();
        rDist = buf.readVarInt();
        bDist = buf.readVarInt();
        fDist = buf.readVarInt();
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
        return !(trait instanceof BoilerRecipeLogic);
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
                            tooltip.addLine(IKey.lang("gregtech.multiblock.large_solar_boiler.no_water"));
                        } else {
                            tooltip.addLine(IKey.lang("gregtech.multiblock.large_solar_boiler.water_bar_hover",
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

    public int getMaximumHeat() {
        int tmp = lDist * 2 - 1;
        return tmp * tmp * HEAT_MAXIMUM_PER_BLOCK;
    }

}
