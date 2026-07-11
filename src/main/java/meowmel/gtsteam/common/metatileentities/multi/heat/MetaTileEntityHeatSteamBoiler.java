package meowmel.gtsteam.common.metatileentities.multi.heat;

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
import com.cleanroommc.modularui.value.sync.*;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.SliderWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
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
import gregtech.api.pattern.FormedStructureView;
import gregtech.api.pattern.MultiblockShapeInfo;
import gregtech.api.pattern.PieceTemplate;
import gregtech.api.pattern.StructureContributionKey;
import gregtech.api.pattern.StructureElementPreviewEntry;
import gregtech.api.pattern.StructureHintResult;
import gregtech.api.pattern.StructureMatchCollector;
import gregtech.api.pattern.StructureOperationRequest;
import gregtech.api.pattern.StructureRuntime;
import gregtech.api.pattern.StructureRuntimeDetectionContext;
import gregtech.api.pattern.casing.GTStructureChannels;
import gregtech.api.pattern.casing.StructureChannel;
import gregtech.api.pattern.element.IStructureElement;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.util.KeyUtil;
import gregtech.api.util.RelativeDirection;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import meowmel.gtsteam.client.textures.GTSteamTextures;
import meowmel.gtsteam.common.metatileentities.multi.DynamicStructureTooling;
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
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import static meowmel.gtsteam.common.block.GTSteamMetaBlocks.blockMultiblockCasing0;
import static meowmel.gtsteam.common.block.blocks.BlockMultiblockCasing0.CasingType.TANK_WALL;
import static net.minecraft.util.EnumFacing.*;

public class MetaTileEntityHeatSteamBoiler extends MultiblockWithDisplayBase implements ProgressBarMultiblock, IControllable, ISteamMachine {
    private static final int MIN_STRUCTURE_SIZE = 3;
    private static final int MAX_STRUCTURE_SIZE = 15;
    private static final int DEFAULT_STRUCTURE_SIZE = 3;
    private static final String RUNTIME_PIECE = "runtime";
    private static final StructureContributionKey<BoilerDimensions, BoilerDimensions> DIMENSIONS_KEY =
            StructureContributionKey.uniform("gtsteam:heat_steam_boiler/dimensions");
    private static final StructureContributionKey<Integer, Integer> WIDTH_KEY =
            StructureMatchCollector.channelValueKey(GTStructureChannels.STRUCTURE_WIDTH.getName());
    private static final StructureContributionKey<Integer, Integer> HEIGHT_KEY =
            StructureMatchCollector.channelValueKey(GTStructureChannels.STRUCTURE_HEIGHT.getName());
    private static final StructureContributionKey<Integer, Integer> LENGTH_KEY =
            StructureMatchCollector.channelValueKey(GTStructureChannels.STRUCTURE_LENGTH.getName());
    private static final StructureDefinition<MetaTileEntityHeatSteamBoiler> STRUCTURE_DEFINITION =
            StructureDefinition.getOrBuild("gtsteam:heat_steam_boiler", () ->
                    StructureDefinition.<MetaTileEntityHeatSteamBoiler>builder(
                                    RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.BACK)
                            .piece(RUNTIME_PIECE, "S")
                            .where('S', self(MetaTileEntityHeatSteamBoiler.class))
                            .end()
                            .runtimeDetector(MetaTileEntityHeatSteamBoiler::detectRuntimeStructure)
                            .build());

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
    public void formStructure(@NotNull FormedStructureView formed) {
        super.formStructure(formed);
        BoilerDimensions dimensions = formed.getAggregate(DIMENSIONS_KEY);
        if (dimensions == null) {
            invalidateStructure();
            return;
        }
        applyStructureDimensions(dimensions);
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
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    private static boolean detectRuntimeStructure(
            @NotNull StructureRuntimeDetectionContext<MetaTileEntityHeatSteamBoiler> context) {
        MetaTileEntityHeatSteamBoiler controller = context.getController();
        DimensionScanResult scan = controller.scanStructureDimensions();
        if (!scan.isSuccess()) {
            return context.fail(scan.failurePos, scan.expected, scan.actual);
        }

        BoilerDimensions dimensions = scan.dimensions;
        context.emit(DIMENSIONS_KEY, dimensions);
        context.emit(WIDTH_KEY, dimensions.width);
        context.emit(HEIGHT_KEY, dimensions.height);
        context.emit(LENGTH_KEY, dimensions.length);

        RuntimeCellElements elements = controller.createRuntimeCellElements(dimensions);
        int center = dimensions.length / 2;
        for (int back = 0; back < dimensions.width; back++) {
            for (int vertical = 0; vertical < dimensions.height; vertical++) {
                for (int lateral = -center; lateral <= center; lateral++) {
                    BoilerCellType cellType = classifyBoilerCell(lateral, vertical, back, dimensions);
                    BlockPos pos = context.localPos(
                            lateral, vertical, back,
                            RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.FRONT);
                    IStructureElement<?> element = elements.get(cellType);
                    if (!context.match(pos, element)) {
                        return context.fail(pos, cellType.expected,
                                String.valueOf(context.getWorld().getBlockState(pos)));
                    }
                }
            }
        }
        return true;
    }

    @NotNull
    private RuntimeCellElements createRuntimeCellElements(@NotNull BoilerDimensions dimensions) {
        int minSize = dimensions.minSize();
        return new RuntimeCellElements(
                self(MetaTileEntityHeatSteamBoiler.class),
                blocks(getULVCasingState()),
                chain(blocks(getULVCasingState()),
                        abilities(1, minSize, MultiblockAbility.IMPORT_FLUIDS),
                        abilities(1, minSize, MultiblockAbility.EXPORT_FLUIDS),
                        abilities(1, minSize * 2, MultiblockAbility.INPUT_HEAT),
                        blocks(Blocks.GLASS.getDefaultState())),
                air());
    }

    @NotNull
    private static BoilerCellType classifyBoilerCell(int lateral,
                                                     int vertical,
                                                     int back,
                                                     @NotNull BoilerDimensions dimensions) {
        if (back == 0 && vertical == 0 && lateral == 0) {
            return BoilerCellType.CONTROLLER;
        }

        int boundaryCount = 0;
        if (back == 0 || back == dimensions.width - 1) {
            boundaryCount++;
        }
        if (vertical == 0 || vertical == dimensions.height - 1) {
            boundaryCount++;
        }
        if (Math.abs(lateral) == dimensions.length / 2) {
            boundaryCount++;
        }

        if (boundaryCount >= 2) {
            return BoilerCellType.EDGE;
        }
        if (boundaryCount == 1) {
            return BoilerCellType.FACE;
        }
        return BoilerCellType.INTERIOR;
    }

    @NotNull
    @Override
    public List<StructureChannel> getSupportedChannels() {
        return Arrays.asList(
                GTStructureChannels.STRUCTURE_WIDTH,
                GTStructureChannels.STRUCTURE_HEIGHT,
                GTStructureChannels.STRUCTURE_LENGTH);
    }

    @Override
    public int[] getChannelRange(@NotNull StructureChannel channel) {
        String channelName = channel.getName();
        if (GTStructureChannels.STRUCTURE_WIDTH.getName().equals(channelName) ||
                GTStructureChannels.STRUCTURE_HEIGHT.getName().equals(channelName) ||
                GTStructureChannels.STRUCTURE_LENGTH.getName().equals(channelName)) {
            return new int[] { MIN_STRUCTURE_SIZE, MAX_STRUCTURE_SIZE };
        }
        return super.getChannelRange(channel);
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes(@Nullable Map<String, Integer> channelValues) {
        BoilerDimensions dimensions = resolveToolingDimensions(channelValues);
        StructureRuntime runtime = createToolingRuntime(dimensions);
        return Collections.singletonList(DynamicStructureTooling.previewShape(runtime, dimensions.width, channelValues));
    }

    @NotNull
    @Override
    public Map<BlockPos, StructureElementPreviewEntry> buildStructurePreviewEntries(
            @Nullable Map<String, Integer> channelValues) {
        BoilerDimensions dimensions = resolveToolingDimensions(channelValues);
        StructureRuntime runtime = createToolingRuntime(dimensions);
        return DynamicStructureTooling.buildPreviewEntries(runtime, dimensions.width, channelValues);
    }

    @Override
    public boolean autoBuildStructure(@NotNull StructureOperationRequest request) {
        request.requireBuildKind();
        createToolingRuntime(resolveToolingDimensions(request.getChannelValues())).buildAllPieces(request);
        return true;
    }

    @Override
    public void spawnStructureHints(@NotNull StructureOperationRequest request) {
        hintStructure(request);
    }

    @Override
    @NotNull
    public StructureHintResult hintStructure(@NotNull StructureOperationRequest request) {
        request.requireKind(StructureOperationRequest.Kind.HINT);
        return createToolingRuntime(resolveToolingDimensions(request.getChannelValues())).hintAllPieces(request);
    }

    @NotNull
    private StructureRuntime createToolingRuntime(@NotNull BoilerDimensions dimensions) {
        return createDynamicStructureRuntime(buildToolingDefinition(dimensions));
    }

    @NotNull
    private StructureDefinition<?> buildToolingDefinition(@NotNull BoilerDimensions dimensions) {
        int minSize = dimensions.minSize();
        return StructureDefinition.<MetaTileEntityHeatSteamBoiler>builder(
                        RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.BACK)
                .pieceFromTemplate(RUNTIME_PIECE, buildToolingTemplate(dimensions))
                .end()
                .globalAbilityLimit(MultiblockAbility.IMPORT_FLUIDS, 1, minSize)
                .globalAbilityLimit(MultiblockAbility.EXPORT_FLUIDS, 1, minSize)
                .globalAbilityLimit(MultiblockAbility.INPUT_HEAT, 1, minSize * 2)
                .build();
    }

    @NotNull
    private PieceTemplate buildToolingTemplate(@NotNull BoilerDimensions dimensions) {
        IStructureElement<?>[][][] template =
                new IStructureElement<?>[dimensions.width][dimensions.height][dimensions.length];
        RuntimeCellElements elements = createRuntimeCellElements(dimensions);
        int center = dimensions.length / 2;
        for (int aisle = 0; aisle < dimensions.width; aisle++) {
            int back = dimensions.width - 1 - aisle;
            for (int vertical = 0; vertical < dimensions.height; vertical++) {
                for (int x = 0; x < dimensions.length; x++) {
                    int lateral = x - center;
                    template[aisle][vertical][x] =
                            elements.get(classifyBoilerCell(lateral, vertical, back, dimensions));
                }
            }
        }

        int[][] repetitions = new int[dimensions.width][2];
        for (int i = 0; i < repetitions.length; i++) {
            repetitions[i][0] = 1;
            repetitions[i][1] = 1;
        }
        int[] centerOffset = new int[] {
                center, 0, dimensions.width - 1,
                dimensions.width - 1, dimensions.width - 1
        };
        return new PieceTemplate(
                template,
                new RelativeDirection[] {
                        RelativeDirection.RIGHT,
                        RelativeDirection.UP,
                        RelativeDirection.BACK
                },
                repetitions,
                new String[repetitions.length],
                centerOffset,
                null);
    }

    @NotNull
    private static BoilerDimensions resolveToolingDimensions(@Nullable Map<String, Integer> channelValues) {
        int width = DynamicStructureTooling.resolveChannel(
                channelValues, GTStructureChannels.STRUCTURE_WIDTH.getName(),
                DEFAULT_STRUCTURE_SIZE, MIN_STRUCTURE_SIZE, MAX_STRUCTURE_SIZE);
        int height = DynamicStructureTooling.resolveChannel(
                channelValues, GTStructureChannels.STRUCTURE_HEIGHT.getName(),
                DEFAULT_STRUCTURE_SIZE, MIN_STRUCTURE_SIZE, MAX_STRUCTURE_SIZE);
        int length = DynamicStructureTooling.resolveOddChannel(
                channelValues, GTStructureChannels.STRUCTURE_LENGTH.getName(),
                DEFAULT_STRUCTURE_SIZE, MIN_STRUCTURE_SIZE, MAX_STRUCTURE_SIZE);
        return new BoilerDimensions(length, width, height);
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        return getMatchingShapes(Collections.emptyMap());
    }

    public boolean isBlockEdge(@NotNull World world, @NotNull BlockPos.MutableBlockPos pos, @NotNull EnumFacing direction) {
        IBlockState block = world.getBlockState(pos.move(direction));
        TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof IGregTechTileEntity iGregTechTileEntity) {
            MetaTileEntity metaTileEntity = iGregTechTileEntity.getMetaTileEntity();
            if (metaTileEntity instanceof IMultiblockAbilityPart<?>) {
                return false;
            } else {
                return !getULVCasingState().equals(block) && !Blocks.GLASS.getDefaultState().equals(block);
            }
        } else {
            return !getULVCasingState().equals(block) && !Blocks.GLASS.getDefaultState().equals(block);
        }
    }

    @NotNull
    private DimensionScanResult scanStructureDimensions() {
        World world = getWorld();
        if (world == null) {
            return DimensionScanResult.failure(
                    getPos(), "loaded world", "heat steam boiler controller has no world");
        }
        EnumFacing front = getFrontFacing();
        if (front == UP || front == DOWN) {
            return DimensionScanResult.failure(
                    getPos(), "horizontal controller facing", String.valueOf(front));
        }
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
        BoilerDimensions dimensions = new BoilerDimensions(lDist + rDist - 1, bDist, hDist);
        if (dimensions.length < MIN_STRUCTURE_SIZE || dimensions.width < MIN_STRUCTURE_SIZE ||
                dimensions.height < MIN_STRUCTURE_SIZE) {
            return DimensionScanResult.failure(
                    getPos(), "boiler dimensions at least 3x3x3", dimensions.toString());
        }
        if (dimensions.length > MAX_STRUCTURE_SIZE || dimensions.width > MAX_STRUCTURE_SIZE ||
                dimensions.height > MAX_STRUCTURE_SIZE) {
            return DimensionScanResult.failure(
                    getPos(), "boiler dimensions at most 15x15x15", dimensions.toString());
        }
        if (dimensions.length % 2 == 0) {
            return DimensionScanResult.failure(
                    getPos(), "odd boiler length so the controller can be centered", dimensions.toString());
        }
        return DimensionScanResult.success(dimensions);
    }

    private void applyStructureDimensions(@NotNull BoilerDimensions dimensions) {
        this.Length = dimensions.length;
        this.Width = dimensions.width;
        this.Height = dimensions.height;
    }

    private enum BoilerCellType {

        CONTROLLER("heat steam boiler controller"),
        EDGE("boiler wall casing"),
        FACE("boiler wall casing, glass, fluid hatch, or heat input hatch"),
        INTERIOR("air inside the boiler");

        @NotNull
        private final String expected;

        BoilerCellType(@NotNull String expected) {
            this.expected = expected;
        }
    }

    private static final class RuntimeCellElements {

        @NotNull
        private final IStructureElement<?> controller;
        @NotNull
        private final IStructureElement<?> edge;
        @NotNull
        private final IStructureElement<?> face;
        @NotNull
        private final IStructureElement<?> interior;

        private RuntimeCellElements(@NotNull IStructureElement<?> controller,
                                    @NotNull IStructureElement<?> edge,
                                    @NotNull IStructureElement<?> face,
                                    @NotNull IStructureElement<?> interior) {
            this.controller = controller.compile();
            this.edge = edge.compile();
            this.face = face.compile();
            this.interior = interior.compile();
        }

        @NotNull
        private IStructureElement<?> get(@NotNull BoilerCellType type) {
            switch (type) {
                case CONTROLLER:
                    return controller;
                case EDGE:
                    return edge;
                case FACE:
                    return face;
                case INTERIOR:
                    return interior;
                default:
                    throw new IllegalStateException("Unhandled boiler cell type " + type);
            }
        }
    }

    private static final class BoilerDimensions {

        private final int length;
        private final int width;
        private final int height;

        private BoilerDimensions(int length, int width, int height) {
            this.length = length;
            this.width = width;
            this.height = height;
        }

        private int minSize() {
            return Math.min(Math.min(width, height), length);
        }

        @Override
        public String toString() {
            return "length=" + length + ", width=" + width + ", height=" + height;
        }
    }

    private static final class DimensionScanResult {

        @Nullable
        private final BoilerDimensions dimensions;
        @NotNull
        private final BlockPos failurePos;
        @NotNull
        private final String expected;
        @NotNull
        private final String actual;

        private DimensionScanResult(@Nullable BoilerDimensions dimensions,
                                    @NotNull BlockPos failurePos,
                                    @NotNull String expected,
                                    @NotNull String actual) {
            this.dimensions = dimensions;
            this.failurePos = failurePos.toImmutable();
            this.expected = expected;
            this.actual = actual;
        }

        @NotNull
        private static DimensionScanResult success(@NotNull BoilerDimensions dimensions) {
            return new DimensionScanResult(dimensions, BlockPos.ORIGIN, "detected boiler dimensions", "matched");
        }

        @NotNull
        private static DimensionScanResult failure(@NotNull BlockPos pos,
                                                   @NotNull String expected,
                                                   @NotNull String actual) {
            return new DimensionScanResult(null, pos, expected, actual);
        }

        private boolean isSuccess() {
            return dimensions != null;
        }
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
