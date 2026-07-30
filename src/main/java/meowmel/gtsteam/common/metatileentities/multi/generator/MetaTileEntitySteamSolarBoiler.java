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
import gregtech.client.utils.TooltipHelper;
import gregtech.common.blocks.BlockMachineCasing;
import gregtech.core.sound.GTSoundEvents;
import meowmel.gtsteam.api.capability.impl.SolarBoilerRecipeLogic;
import meowmel.gtsteam.common.block.GTSteamMetaBlocks;
import meowmel.gtsteam.common.block.blocks.BlockMultiblockCasing1;
import meowmel.gtsteam.common.metatileentities.multi.DynamicStructureTooling;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import static gregtech.common.blocks.MetaBlocks.MACHINE_CASING;
import static net.minecraft.util.EnumFacing.*;

public class MetaTileEntitySteamSolarBoiler extends MultiblockWithDisplayBase implements ProgressBarMultiblock,
        IControllable, ISteamMachine {
    private static final int MIN_STRUCTURE_SIZE = 3;
    private static final int MAX_STRUCTURE_SIZE = 15;
    private static final int DEFAULT_STRUCTURE_SIZE = 3;
    private static final String RUNTIME_PIECE = "runtime";
    private static final StructureContributionKey<SolarDimensions, SolarDimensions> DIMENSIONS_KEY =
            StructureContributionKey.uniform("gtsteam:steam_solar_boiler/dimensions");
    private static final StructureContributionKey<Integer, Integer> WIDTH_KEY =
            StructureMatchCollector.channelValueKey(GTStructureChannels.STRUCTURE_WIDTH.getName());
    private static final StructureContributionKey<Integer, Integer> LENGTH_KEY =
            StructureMatchCollector.channelValueKey(GTStructureChannels.STRUCTURE_LENGTH.getName());
    private static final StructureDefinition<MetaTileEntitySteamSolarBoiler> STRUCTURE_DEFINITION =
            StructureDefinition.getOrBuild("gtsteam:steam_solar_boiler", () ->
                    StructureDefinition.<MetaTileEntitySteamSolarBoiler>builder(
                                    RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.BACK)
                            .piece(RUNTIME_PIECE, "S")
                            .where('S', self(MetaTileEntitySteamSolarBoiler.class))
                            .end()
                            .globalAbilityLimit(MultiblockAbility.IMPORT_FLUIDS, 1, -1)
                            .globalAbilityLimit(MultiblockAbility.EXPORT_FLUIDS, 1, -1)
                            .runtimeDetector(MetaTileEntitySteamSolarBoiler::detectRuntimeStructure)
                            .build());

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
        SolarDimensions dimensions = formed.getAggregate(DIMENSIONS_KEY);
        if (dimensions == null) {
            invalidateStructure();
            return;
        }
        applyStructureDimensions(dimensions);
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
                return (block != getCasingState())
                        && (block != getSolarCasingState());
            }
        } else {
            return (block != getCasingState())
                    && (block != getSolarCasingState());
        }
    }

    @Override
    public void checkStructurePattern() {
        super.checkStructurePattern();
    }

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    private static boolean detectRuntimeStructure(
            @NotNull StructureRuntimeDetectionContext<MetaTileEntitySteamSolarBoiler> context) {
        MetaTileEntitySteamSolarBoiler controller = context.getController();
        DimensionScanResult scan = controller.scanStructureDimensions();
        if (!scan.isSuccess()) {
            return context.fail(scan.failurePos, scan.expected, scan.actual);
        }

        SolarDimensions dimensions = scan.dimensions;
        context.emit(DIMENSIONS_KEY, dimensions);
        context.emit(WIDTH_KEY, dimensions.width);
        context.emit(LENGTH_KEY, dimensions.length);

        RuntimeCellElements elements = controller.createRuntimeCellElements();
        int center = dimensions.length / 2;
        for (int back = 0; back < dimensions.width; back++) {
            for (int lateral = -center; lateral <= center; lateral++) {
                SolarCellType cellType = classifySolarCell(lateral, back, dimensions);
                BlockPos pos = context.localPos(
                        lateral, 0, back,
                        RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.FRONT);
                IStructureElement<?> element = elements.get(cellType);
                if (!context.match(pos, element)) {
                    return context.fail(pos, cellType.expected,
                            String.valueOf(context.getWorld().getBlockState(pos)));
                }
            }
        }
        return true;
    }

    @NotNull
    private RuntimeCellElements createRuntimeCellElements() {
        return new RuntimeCellElements(
                self(MetaTileEntitySteamSolarBoiler.class),
                chain(blocks(getCasingState()),
                        abilities(1, -1, 1, MultiblockAbility.IMPORT_FLUIDS),
                        abilities(1, -1, 1, MultiblockAbility.EXPORT_FLUIDS)),
                blocks(getSolarCasingState()));
    }

    @NotNull
    private static SolarCellType classifySolarCell(int lateral,
                                                   int back,
                                                   @NotNull SolarDimensions dimensions) {
        if (back == 0 && lateral == 0) {
            return SolarCellType.CONTROLLER;
        }
        if (back == 0 || back == dimensions.width - 1 || Math.abs(lateral) == dimensions.length / 2) {
            return SolarCellType.FRAME;
        }
        return SolarCellType.COLLECTOR;
    }

    @NotNull
    @Override
    public List<StructureChannel> getSupportedChannels() {
        return Arrays.asList(
                GTStructureChannels.STRUCTURE_WIDTH,
                GTStructureChannels.STRUCTURE_LENGTH);
    }

    @Override
    public int[] getChannelRange(@NotNull StructureChannel channel) {
        String channelName = channel.getName();
        if (GTStructureChannels.STRUCTURE_WIDTH.getName().equals(channelName) ||
                GTStructureChannels.STRUCTURE_LENGTH.getName().equals(channelName)) {
            return new int[] { MIN_STRUCTURE_SIZE, MAX_STRUCTURE_SIZE };
        }
        return super.getChannelRange(channel);
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes(@Nullable Map<String, Integer> channelValues) {
        SolarDimensions dimensions = resolveToolingDimensions(channelValues);
        StructureRuntime runtime = createToolingRuntime(dimensions);
        return Collections.singletonList(DynamicStructureTooling.previewShape(runtime, dimensions.width, channelValues));
    }

    @NotNull
    @Override
    public Map<BlockPos, StructureElementPreviewEntry> buildStructurePreviewEntries(
            @Nullable Map<String, Integer> channelValues) {
        SolarDimensions dimensions = resolveToolingDimensions(channelValues);
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
    @Override
    protected StructureRuntime createToolingPreviewRuntime(
            @Nullable Map<String, Integer> channelValues) {
        return createToolingRuntime(resolveToolingDimensions(channelValues));
    }

    @NotNull
    private StructureRuntime createToolingRuntime(@NotNull SolarDimensions dimensions) {
        return createDynamicStructureRuntime(buildToolingDefinition(dimensions));
    }

    @NotNull
    private StructureDefinition<?> buildToolingDefinition(@NotNull SolarDimensions dimensions) {
        return StructureDefinition.<MetaTileEntitySteamSolarBoiler>builder(
                        RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.BACK)
                .pieceFromTemplate(RUNTIME_PIECE, buildToolingTemplate(dimensions))
                .end()
                .globalAbilityLimit(MultiblockAbility.IMPORT_FLUIDS, 1, -1)
                .globalAbilityLimit(MultiblockAbility.EXPORT_FLUIDS, 1, -1)
                .build();
    }

    @NotNull
    private PieceTemplate buildToolingTemplate(@NotNull SolarDimensions dimensions) {
        IStructureElement<?>[][][] template =
                new IStructureElement<?>[dimensions.width][1][dimensions.length];
        RuntimeCellElements elements = createRuntimeCellElements();
        int center = dimensions.length / 2;
        for (int aisle = 0; aisle < dimensions.width; aisle++) {
            int back = dimensions.width - 1 - aisle;
            for (int x = 0; x < dimensions.length; x++) {
                int lateral = x - center;
                template[aisle][0][x] = elements.get(classifySolarCell(lateral, back, dimensions));
            }
        }

        int[][] repetitions = new int[dimensions.width][2];
        for (int i = 0; i < repetitions.length; i++) {
            repetitions[i][0] = 1;
            repetitions[i][1] = 1;
        }
        int[] centerOffset = new int[] {
                center, 0, dimensions.width - 1, dimensions.width - 1, dimensions.width - 1
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
    private static SolarDimensions resolveToolingDimensions(@Nullable Map<String, Integer> channelValues) {
        int width = DynamicStructureTooling.resolveChannel(
                channelValues, GTStructureChannels.STRUCTURE_WIDTH.getName(),
                DEFAULT_STRUCTURE_SIZE, MIN_STRUCTURE_SIZE, MAX_STRUCTURE_SIZE);
        int length = DynamicStructureTooling.resolveOddChannel(
                channelValues, GTStructureChannels.STRUCTURE_LENGTH.getName(),
                DEFAULT_STRUCTURE_SIZE, MIN_STRUCTURE_SIZE, MAX_STRUCTURE_SIZE);
        return new SolarDimensions(length, width);
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        return getMatchingShapes(Collections.emptyMap());
    }

    @NotNull
    private DimensionScanResult scanStructureDimensions() {
        World world = getWorld();
        if (world == null) {
            return DimensionScanResult.failure(
                    getPos(), "loaded world", "steam solar boiler controller has no world");
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
        SolarDimensions dimensions = new SolarDimensions(lDist + rDist - 1, bDist);
        if (dimensions.length < MIN_STRUCTURE_SIZE || dimensions.width < MIN_STRUCTURE_SIZE) {
            return DimensionScanResult.failure(
                    getPos(), "solar boiler dimensions at least 3x3", dimensions.toString());
        }
        if (dimensions.length > MAX_STRUCTURE_SIZE || dimensions.width > MAX_STRUCTURE_SIZE) {
            return DimensionScanResult.failure(
                    getPos(), "solar boiler dimensions at most 15x15", dimensions.toString());
        }
        if (dimensions.length % 2 == 0) {
            return DimensionScanResult.failure(
                    getPos(), "odd solar boiler length so the controller can be centered", dimensions.toString());
        }
        return DimensionScanResult.success(dimensions);
    }

    private void applyStructureDimensions(@NotNull SolarDimensions dimensions) {
        this.Length = dimensions.length;
        this.Width = dimensions.width;
    }

    private enum SolarCellType {

        CONTROLLER("steam solar boiler controller"),
        FRAME("ULV casing or fluid hatch"),
        COLLECTOR("solar collector casing");

        @NotNull
        private final String expected;

        SolarCellType(@NotNull String expected) {
            this.expected = expected;
        }
    }

    private static final class RuntimeCellElements {

        @NotNull
        private final IStructureElement<?> controller;
        @NotNull
        private final IStructureElement<?> frame;
        @NotNull
        private final IStructureElement<?> collector;

        private RuntimeCellElements(@NotNull IStructureElement<?> controller,
                                    @NotNull IStructureElement<?> frame,
                                    @NotNull IStructureElement<?> collector) {
            this.controller = controller.compile();
            this.frame = frame.compile();
            this.collector = collector.compile();
        }

        @NotNull
        private IStructureElement<?> get(@NotNull SolarCellType type) {
            switch (type) {
                case CONTROLLER:
                    return controller;
                case FRAME:
                    return frame;
                case COLLECTOR:
                    return collector;
                default:
                    throw new IllegalStateException("Unhandled solar cell type " + type);
            }
        }
    }

    private static final class SolarDimensions {

        private final int length;
        private final int width;

        private SolarDimensions(int length, int width) {
            this.length = length;
            this.width = width;
        }

        @Override
        public String toString() {
            return "length=" + length + ", width=" + width;
        }
    }

    private static final class DimensionScanResult {

        @Nullable
        private final SolarDimensions dimensions;
        @NotNull
        private final BlockPos failurePos;
        @NotNull
        private final String expected;
        @NotNull
        private final String actual;

        private DimensionScanResult(@Nullable SolarDimensions dimensions,
                                    @NotNull BlockPos failurePos,
                                    @NotNull String expected,
                                    @NotNull String actual) {
            this.dimensions = dimensions;
            this.failurePos = failurePos.toImmutable();
            this.expected = expected;
            this.actual = actual;
        }

        @NotNull
        private static DimensionScanResult success(@NotNull SolarDimensions dimensions) {
            return new DimensionScanResult(dimensions, BlockPos.ORIGIN, "detected solar boiler dimensions", "matched");
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


    public static IBlockState getCasingState() {
        return MACHINE_CASING.getState(BlockMachineCasing.MachineCasingType.ULV);
    }

    public IBlockState getSolarCasingState() {
        return getStaticSolarCasingState();
    }

    private static IBlockState getStaticSolarCasingState() {
        return GTSteamMetaBlocks.blockMultiblockCasing1.getState(BlockMultiblockCasing1.CasingType.SOLAR_COLLECTOR);
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
