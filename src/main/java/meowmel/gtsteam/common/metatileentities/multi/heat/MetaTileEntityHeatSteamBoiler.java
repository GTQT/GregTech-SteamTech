package meowmel.gtsteam.common.metatileentities.multi.heat;

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
import com.cleanroommc.modularui.value.sync.*;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.SliderWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import gregtech.api.GTValues;
import gregtech.api.capability.IControllable;
import gregtech.api.capability.IHeatable;
import gregtech.api.capability.ISteamMachine;
import gregtech.api.capability.impl.CommonFluidFilters;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.metatileentity.multiblock.ui.*;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuiTheme;
import gregtech.api.mui.GTGuis;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.MultiblockShapeInfo;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.util.KeyUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.MetaTileEntities;
import meowmel.gtsteam.client.textures.GTSteamTextures;
import meowmel.gtsteam.common.metatileentities.GTSteamMetaTileEntities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import static gregtech.common.metatileentities.MetaTileEntities.HEAT_INPUT_HATCH;
import static meowmel.gtsteam.common.block.GTSteamMetaBlocks.blockMultiblockCasing0;
import static meowmel.gtsteam.common.block.blocks.BlockMultiblockCasing0.CasingType.TANK_WALL;
import static net.minecraft.util.EnumFacing.*;

public class MetaTileEntityHeatSteamBoiler extends MultiblockWithDisplayBase implements ProgressBarMultiblock, IControllable, ISteamMachine {

    protected HeatSteamBoilerRecipeLogic recipeLogic;
    private List<IHeatable> heatHatch = null;
    // 结构尺寸
    private int capacity = 0;
    private int Length = 0;
    private int Height = 0;
    private int Width = 0;
    // 流体接口
    private FluidTankList fluidImportInventory;
    private FluidTankList steamOutputTank;
    private int throttlePercentage = 100;

    public MetaTileEntityHeatSteamBoiler(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        recipeLogic = new HeatSteamBoilerRecipeLogic(this);
        resetTileAbilities();
    }

    public static String repeat(String a, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(a);
        }
        return builder.toString();
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityHeatSteamBoiler(metaTileEntityId);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return GTSteamTextures.TANK_WALL;
    }

    @Override
    protected void updateFormedValid() {
        this.recipeLogic.update();
    }

    @Override
    public void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        initializeAbilities();
        refreshCAP();
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


    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.setWorkingStatus(recipeLogic.isWorkingEnabled(), recipeLogic.isActive())
                .addCustom(this::addBoilerThermalInfo)
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
        builder.addCustom((manager, syncer) -> {
            if (isStructureFormed() && syncer.syncBoolean(getWaterFilled() == 0)) {
                manager.add(KeyUtil.lang(TextFormatting.YELLOW,
                        "gregtech.multiblock.large_boiler.no_water"));
                manager.add(KeyUtil.lang(TextFormatting.GRAY,
                        "gregtech.multiblock.large_boiler.explosion_tooltip"));
            }
        });
    }

    private void addBoilerThermalInfo(KeyManager keyManager, UISyncer syncer) {
        if (!isStructureFormed()) return;

        int length = syncer.syncInt(this.Length);
        int height = syncer.syncInt(this.Height);
        int width = syncer.syncInt(this.Width);

        keyManager.add(KeyUtil.lang(TextFormatting.WHITE, "gtsteam.multiblock.boiler.ui.size",
                length, height, width));


        int steam = syncer.syncInt(recipeLogic.getLastTickSteam());
        int heatScaled = syncer.syncInt(recipeLogic.getHeatScaled());
        int throttleAmt = syncer.syncInt(getThrottle());

        int currentTemp = syncer.syncInt(recipeLogic.getMaximumHeatFromMaintenance());
        if (currentTemp < 373) {
            keyManager.add(KeyUtil.lang(TextFormatting.YELLOW, "gtsteam.multiblock.boiler.preheating"));
        } else {
            keyManager.add(KeyUtil.lang(TextFormatting.YELLOW, "gtsteam.multiblock.boiler.boiling"));

            // Steam Output line
            IKey steamOutput = KeyUtil.number(TextFormatting.AQUA,
                    steam, " L/t");

            keyManager.add(KeyUtil.lang(TextFormatting.GRAY,
                    "gregtech.multiblock.large_boiler.steam_output", steamOutput));
        }

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

    public IBlockState getULVCasingState() {
        return blockMultiblockCasing0.getState(TANK_WALL);
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        resetTileAbilities();
        this.throttlePercentage = 100;
        this.recipeLogic.invalidate();
    }

    @Override
    public void checkStructurePattern() {
        if (!this.isStructureFormed()) {
            reinitializeStructurePattern();
        }
        super.checkStructurePattern();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.capacity = compound.getInteger("FluidCapacity");
        this.Length = compound.getInteger("Length");
        this.Height = compound.getInteger("Height");
        this.Width = compound.getInteger("Width");
        throttlePercentage = compound.getInteger("ThrottlePercentage");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("FluidCapacity", this.capacity);
        data.setInteger("Length", this.Length);
        data.setInteger("Height", this.Height);
        data.setInteger("Width", this.Width);
        data.setInteger("ThrottlePercentage", throttlePercentage);
        return data;
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
    protected @NotNull BlockPattern createStructurePattern() {
        if (getWorld() != null) updateStructureDimensions();
        var pattern = FactoryBlockPattern.start();
        if (Length % 2 == 0) {
            Width = 3;
            Height = 3;
            Length = 3;
        }
        if (Width < 3 || Height < 3 || Length < 3) {
            Width = 3;
            Height = 3;
            Length = 3;
        }
        if (Width > 15 || Height > 15 || Length > 15) {
            Width = 3;
            Height = 3;
        }

        int minSize = Math.min(Math.min(Width, Height), Length);

        for (int i = 1; i <= Width; i++) {
            String[] PatternStringLayer = new String[Height];
            for (int j = 1; j <= Height; j++) {
                StringBuilder str = new StringBuilder();
                if (i == 1 || i == Width) {
                    if (j == 1 || j == Height) {
                        if (i == Width && j == 1) {
                            str.append(repeat("A", Length / 2)).append("#").append(repeat("A", Length / 2));
                        } else {
                            str.append(repeat("A", Length));
                        }
                    } else {
                        str.append(repeat("A", 1)).append(repeat("B", Length - 2)).append(repeat("A", 1));
                    }
                } else {
                    if (j == 1 || j == Height) {
                        str.append(repeat("A", 1)).append(repeat("B", Length - 2)).append(repeat("A", 1));
                    } else {
                        str.append(repeat("B", 1)).append(repeat("C", Length - 2)).append(repeat("B", 1));
                    }
                }
                PatternStringLayer[j - 1] = str.toString();
            }
            pattern.aisle(PatternStringLayer);
        }
        return pattern
                .where('#', selfPredicate())
                .where('A', states(getULVCasingState()))
                .where('B', states(getULVCasingState())
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMinGlobalLimited(1).setMaxGlobalLimited(minSize))
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMinGlobalLimited(1).setMaxGlobalLimited(minSize))
                        .or(abilities(MultiblockAbility.INPUT_HEAT).setMinGlobalLimited(1).setMaxGlobalLimited(minSize * 2))
                        .or(states(Blocks.GLASS.getDefaultState())))
                .where('C', air())
                .build();
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        List<MultiblockShapeInfo> shapeInfo = new ArrayList<>();

        // 3x3x3 示例结构
        {
            MultiblockShapeInfo.Builder builder = MultiblockShapeInfo.builder();
            builder.aisle("CCC", "CNC", "CCC");
            builder.aisle("CMC", "G H", "CGC");
            builder.aisle("CSC", "CGC", "CCC");
            builder
                    .where('S', GTSteamMetaTileEntities.HEAT_STEAM_BOILER, SOUTH)
                    .where('M', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.ULV], DOWN)
                    .where('N', MetaTileEntities.FLUID_EXPORT_HATCH[GTValues.ULV], NORTH)
                    .where('H', HEAT_INPUT_HATCH[GTValues.ULV], WEST)
                    .where('C', getULVCasingState())
                    .where('G', Blocks.GLASS.getDefaultState())
                    .where(' ', Blocks.AIR.getDefaultState());
            shapeInfo.add(builder.build());
        }

        // 5x5x5 结构
        {
            MultiblockShapeInfo.Builder builder = MultiblockShapeInfo.builder();
            builder.aisle("CCCCC", "CGGGC", "CGGGC", "CGGGC", "CCCCC");
            builder.aisle("CCCCC", "G   G", "G   G", "G   G", "CCCCC");
            builder.aisle("CCCCC", "G   G", "G   G", "G   G", "CCCCC");
            builder.aisle("CCCCC", "G   G", "G   G", "G   G", "CCCCC");
            builder.aisle("CCSCC", "CMHNC", "CGGGC", "CGGGC", "CCCCC");
            builder
                    .where('S', GTSteamMetaTileEntities.HEAT_STEAM_BOILER, SOUTH)
                    .where('M', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.ULV], SOUTH)
                    .where('N', MetaTileEntities.FLUID_EXPORT_HATCH[GTValues.ULV], SOUTH)
                    .where('H', HEAT_INPUT_HATCH[GTValues.ULV], SOUTH)
                    .where('C', getULVCasingState())
                    .where('G', Blocks.GLASS.getDefaultState())
                    .where(' ', Blocks.AIR.getDefaultState());
            shapeInfo.add(builder.build());
        }

        // 7x7x7 结构
        {
            MultiblockShapeInfo.Builder builder = MultiblockShapeInfo.builder();
            builder.aisle("CCCCCCC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CCCCCCC");
            builder.aisle("CCCCCCC", "G     G", "G     G", "G     G", "G     G", "G     G", "CCCCCCC");
            builder.aisle("CCCCCCC", "G     G", "G     G", "G     G", "G     G", "G     G", "CCCCCCC");
            builder.aisle("CCCCCCC", "G     G", "G     G", "G     G", "G     G", "G     G", "CCCCCCC");
            builder.aisle("CCCCCCC", "G     G", "G     G", "G     G", "G     G", "G     G", "CCCCCCC");
            builder.aisle("CCCCCCC", "G     G", "G     G", "G     G", "G     G", "G     G", "CCCCCCC");
            builder.aisle("CCCSCCC", "CMHGGNC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CCCCCCC");
            builder
                    .where('S', GTSteamMetaTileEntities.HEAT_STEAM_BOILER, SOUTH)
                    .where('M', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.ULV], SOUTH)
                    .where('N', MetaTileEntities.FLUID_EXPORT_HATCH[GTValues.ULV], SOUTH)
                    .where('H', HEAT_INPUT_HATCH[GTValues.ULV], SOUTH)
                    .where('C', getULVCasingState())
                    .where('G', Blocks.GLASS.getDefaultState())
                    .where(' ', Blocks.AIR.getDefaultState());
            shapeInfo.add(builder.build());
        }

        // 9x9x9 结构
        {
            MultiblockShapeInfo.Builder builder = MultiblockShapeInfo.builder();
            builder.aisle("CCCCCCCCC", "CGGGGGGGC", "CGGGGGGGC", "CGGGGGGGC", "CGGGGGGGC", "CGGGGGGGC", "CGGGGGGGC", "CGGGGGGGC", "CCCCCCCCC");
            builder.aisle("CCCCCCCCC", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "CCCCCCCCC");
            builder.aisle("CCCCCCCCC", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "CCCCCCCCC");
            builder.aisle("CCCCCCCCC", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "CCCCCCCCC");
            builder.aisle("CCCCCCCCC", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "CCCCCCCCC");
            builder.aisle("CCCCCCCCC", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "CCCCCCCCC");
            builder.aisle("CCCCCCCCC", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "CCCCCCCCC");
            builder.aisle("CCCCCCCCC", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "CCCCCCCCC");
            builder.aisle("CCCCSCCCC", "CMHGGGGNC", "CGGGGGGGC", "CGGGGGGGC", "CGGGGGGGC", "CGGGGGGGC", "CGGGGGGGC", "CGGGGGGGC", "CCCCCCCCC");
            builder
                    .where('S', GTSteamMetaTileEntities.HEAT_STEAM_BOILER, SOUTH)
                    .where('M', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.ULV], SOUTH)
                    .where('N', MetaTileEntities.FLUID_EXPORT_HATCH[GTValues.ULV], SOUTH)
                    .where('H', HEAT_INPUT_HATCH[GTValues.ULV], SOUTH)
                    .where('C', getULVCasingState())
                    .where('G', Blocks.GLASS.getDefaultState())
                    .where(' ', Blocks.AIR.getDefaultState());
            shapeInfo.add(builder.build());
        }

        // 11x11x11 结构
        {
            MultiblockShapeInfo.Builder builder = MultiblockShapeInfo.builder();
            builder.aisle("CCCCCCCCCCC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CCCCCCCCCCC");
            builder.aisle("CCCCCCCCCCC", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "CCCCCCCCCCC");
            builder.aisle("CCCCCCCCCCC", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "CCCCCCCCCCC");
            builder.aisle("CCCCCCCCCCC", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "CCCCCCCCCCC");
            builder.aisle("CCCCCCCCCCC", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "CCCCCCCCCCC");
            builder.aisle("CCCCCCCCCCC", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "CCCCCCCCCCC");
            builder.aisle("CCCCCCCCCCC", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "CCCCCCCCCCC");
            builder.aisle("CCCCCCCCCCC", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "CCCCCCCCCCC");
            builder.aisle("CCCCCCCCCCC", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "CCCCCCCCCCC");
            builder.aisle("CCCCCCCCCCC", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "CCCCCCCCCCC");
            builder.aisle("CCCCCSCCCCC", "CMHGGGGGGNC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CCCCCCCCCCC");
            builder
                    .where('S', GTSteamMetaTileEntities.HEAT_STEAM_BOILER, SOUTH)
                    .where('M', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.ULV], SOUTH)
                    .where('N', MetaTileEntities.FLUID_EXPORT_HATCH[GTValues.ULV], SOUTH)
                    .where('H', HEAT_INPUT_HATCH[GTValues.ULV], SOUTH)
                    .where('C', getULVCasingState())
                    .where('G', Blocks.GLASS.getDefaultState())
                    .where(' ', Blocks.AIR.getDefaultState());
            shapeInfo.add(builder.build());
        }

        return shapeInfo;
    }

    public boolean isBlockEdge(@NotNull World world, @NotNull BlockPos.MutableBlockPos pos, @NotNull EnumFacing direction) {
        IBlockState block = world.getBlockState(pos.move(direction));
        TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof IGregTechTileEntity iGregTechTileEntity) {
            MetaTileEntity metaTileEntity = iGregTechTileEntity.getMetaTileEntity();
            if (metaTileEntity instanceof IMultiblockAbilityPart<?>) {
                return false;
            } else {
                return (block != getULVCasingState()) && (block != Blocks.GLASS.getDefaultState());
            }
        } else {
            return (block != getULVCasingState()) && (block != Blocks.GLASS.getDefaultState());
        }
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
        BlockPos.MutableBlockPos hPos = new BlockPos.MutableBlockPos(getPos());

        int lDist = 0;
        int rDist = 0;
        int hDist = 0;
        int MAX_RADIUS = 8;
        int bDist = 0;

        for (int i = 1; i <= MAX_RADIUS; i++) {
            if (lDist == 0 && isBlockEdge(world, lPos, left)) lDist = i;
            if (rDist == 0 && isBlockEdge(world, rPos, right)) rDist = i;
            if (lDist != 0 && rDist != 0) break;
        }
        for (int i = 1; i <= MAX_RADIUS * 2 - 1; i++) {
            if (isBlockEdge(world, bPos, back)) bDist = i;
            if (bDist != 0) break;
        }
        for (int i = 1; i <= 15; i++) {
            if (isBlockEdge(world, hPos, EnumFacing.UP)) hDist = i;
            if (hDist != 0) break;
        }
        this.Length = lDist + rDist - 1;
        this.Width = bDist;
        this.Height = hDist;
    }

    private void initializeAbilities() {
        this.fluidImportInventory = new FluidTankList(true, getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.steamOutputTank = new FluidTankList(true, getAbilities(MultiblockAbility.EXPORT_FLUIDS));
        this.heatHatch = getAbilities(MultiblockAbility.INPUT_HEAT);
    }

    @Override
    public FluidTankList getImportFluids() {
        return fluidImportInventory;
    }

    @Override
    public FluidTankList getExportFluids() {
        return steamOutputTank;
    }

    public void refreshCAP() {
        this.capacity = (Height - 2) * (Width - 2) * (Length - 2);
    }

    private void resetTileAbilities() {
        this.fluidImportInventory = new FluidTankList(true);
        this.steamOutputTank = new FluidTankList(true);
        this.heatHatch = null;
    }

    @Override
    public void update() {
        super.update();
    }

    public int steamPerTick() {
        if (getTemperature() < 373) return 0;
        double steam = 600 * Math.pow(capacity / 27.0, 0.65) * (1.0 + (getTemperature() - 373) * 0.0015);
        return (int) Math.round(steam);
    }

    @Override
    public boolean shouldShowVoidingModeButton() {
        return false;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(), isActive(),
                recipeLogic.isWorkingEnabled());
    }

    @Override
    public GTGuiTheme getUITheme() {
        return GTGuiTheme.STEEL;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return Textures.MULTIBLOCK_TANK_OVERLAY;
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(TextFormatting.GREEN + I18n.format("gregtech.multiblock.heat_steam_boiler.title"));
        tooltip.add(I18n.format("gregtech.multiblock.heat_steam_boiler.structure_size"));
        tooltip.add(I18n.format("gregtech.multiblock.heat_steam_boiler.base_output"));
        tooltip.add(I18n.format("gregtech.multiblock.heat_steam_boiler.theoretical_output"));
        tooltip.add(I18n.format("gregtech.multiblock.heat_steam_boiler.heat_requirement"));
        tooltip.add(I18n.format("gregtech.multiblock.heat_steam_boiler.temperature_dynamics"));
        tooltip.add(I18n.format("gregtech.multiblock.heat_steam_boiler.production_ratio"));
        tooltip.add(I18n.format("gregtech.multiblock.heat_steam_boiler.heat_steam_ratio"));
        tooltip.add(I18n.format("gregtech.multiblock.heat_steam_boiler.input_warnings"));
    }

    @Override
    public int getProgressBarCount() {
        return 1;
    }

    @Override
    public void registerBars(List<UnaryOperator<TemplateBarBuilder>> bars, PanelSyncManager syncManager) {
        LongSyncValue heatFilledValue = new LongSyncValue(this::getTemperature);
        LongSyncValue heatCapacityValue = new LongSyncValue(this::getMaxTemperature);
        IntSyncValue boilerHeatValue = new IntSyncValue(this::getBoilerTemp);
        syncManager.syncValue("heat_filled", heatFilledValue);
        syncManager.syncValue("heat_capacity", heatCapacityValue);
        syncManager.syncValue("boiler_filled", boilerHeatValue);

        bars.add(barBuilder -> barBuilder
                .progress(() -> heatCapacityValue.getIntValue() == 0 ? 0 :
                        heatFilledValue.getIntValue() * 1.0 / heatCapacityValue.getIntValue())
                .texture(GTGuiTextures.PROGRESS_BAR_HEAT_TEMP)
                .tooltipBuilder(tooltip -> {

                    tooltip.addLine(IKey.lang("gregtech.multiblock.heat_multiblock.heat_bar_hover",
                            heatFilledValue.getIntValue(), heatCapacityValue.getIntValue()));
                    tooltip.addLine(IKey.lang("gregtech.multiblock.heat_multiblock.heat_bar_hover",
                            boilerHeatValue.getIntValue(), heatFilledValue.getIntValue()));
                }));
    }

    public void changeHeat(long amount) {
        if (heatHatch == null) return;
        long average = amount / heatHatch.size();
        heatHatch.forEach(hatch -> hatch.changeHeat(average));
    }

    public long getHeatStored() {
        if (heatHatch == null) return 0;
        return heatHatch.stream().mapToLong(IHeatable::getHeatStored).sum();
    }

    public long getHeatCapacity() {
        if (heatHatch == null) return 0;
        return heatHatch.stream().mapToLong(IHeatable::getHeatCapacity).sum();
    }

    public int getTemperature() {
        if (heatHatch == null || heatHatch.isEmpty()) return 293;
        return heatHatch.stream().mapToInt(IHeatable::getTemperature).max().orElse(293);
    }

    public int getMaxTemperature() {
        if (heatHatch == null || heatHatch.isEmpty()) return 293;
        return heatHatch.stream().mapToInt(IHeatable::getMaxTemperature).max().orElse(293);
    }

    public int getBoilerTemp() {
        return recipeLogic.getMaximumHeatFromMaintenance();
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

    @Override
    public boolean isWorkingEnabled() {
        return recipeLogic.isWorkingEnabled();
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        recipeLogic.setWorkingEnabled(isWorkingAllowed);
    }

    public List<IHeatable> getHeatHatch() {
        return heatHatch;
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
}