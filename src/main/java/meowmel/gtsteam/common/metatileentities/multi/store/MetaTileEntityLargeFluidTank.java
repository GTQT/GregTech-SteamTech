package meowmel.gtsteam.common.metatileentities.multi.store;

import static gregtech.api.pattern.element.Elements.*;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.metatileentity.multiblock.ui.KeyManager;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.metatileentity.multiblock.ui.TemplateBarBuilder;
import gregtech.api.metatileentity.multiblock.ui.UISyncer;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuiTheme;
import gregtech.api.pattern.FormedStructureView;
import gregtech.api.pattern.MultiblockShapeInfo;
import gregtech.api.pattern.PieceTemplate;
import gregtech.api.pattern.StructureContributionKey;
import gregtech.api.pattern.StructureBuildResult;
import gregtech.api.pattern.StructureElementPreviewEntry;
import gregtech.api.pattern.StructureFailureTrace;
import gregtech.api.pattern.StructureHintResult;
import gregtech.api.pattern.StructureMatchCollector;
import gregtech.api.pattern.StructureOperationRequest;
import gregtech.api.pattern.StructureRuntime;
import gregtech.api.pattern.StructureRuntimeDetectionContext;
import gregtech.api.pattern.casing.GTStructureChannels;
import gregtech.api.pattern.casing.StructureChannel;
import gregtech.api.pattern.element.IStructureElement;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.util.GTLog;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
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

public class MetaTileEntityLargeFluidTank extends MultiblockWithDisplayBase implements ProgressBarMultiblock {
    private static final int MIN_STRUCTURE_SIZE = 3;
    private static final int MAX_STRUCTURE_SIZE = 15;
    private static final int DEFAULT_STRUCTURE_SIZE = 3;
    private static final long STRUCTURE_LOG_INTERVAL_TICKS = 100L;
    private static final String RUNTIME_PIECE = "runtime";
    private static final StructureContributionKey<TankDimensions, TankDimensions> DIMENSIONS_KEY =
            StructureContributionKey.uniform("gtsteam:large_fluid_tank/dimensions");
    private static final StructureContributionKey<Integer, Integer> WIDTH_KEY =
            StructureMatchCollector.channelValueKey(GTStructureChannels.STRUCTURE_WIDTH.getName());
    private static final StructureContributionKey<Integer, Integer> HEIGHT_KEY =
            StructureMatchCollector.channelValueKey(GTStructureChannels.STRUCTURE_HEIGHT.getName());
    private static final StructureContributionKey<Integer, Integer> LENGTH_KEY =
            StructureMatchCollector.channelValueKey(GTStructureChannels.STRUCTURE_LENGTH.getName());
    private static final StructureDefinition<MetaTileEntityLargeFluidTank> STRUCTURE_DEFINITION =
            StructureDefinition.getOrBuild("gtsteam:large_fluid_tank", () ->
                    StructureDefinition.<MetaTileEntityLargeFluidTank>builder(
                                    RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.BACK)
                            .piece(RUNTIME_PIECE, "S")
                            .where('S', self(MetaTileEntityLargeFluidTank.class))
                            .end()
                            .globalAbilityLimit(MultiblockAbility.IMPORT_FLUIDS, 1, 1)
                            .globalAbilityLimit(MultiblockAbility.EXPORT_FLUIDS, 1, 1)
                            .runtimeDetector(MetaTileEntityLargeFluidTank::detectRuntimeStructure)
                            .build());

    private int capacity = 0;
    private int Length = 0;
    private int Height = 0;
    private int Width = 0;
    private IFluidTank inputFluidsTank;
    private IFluidTank outputFluidsTank;
    private FluidTank StoragefluidTank = null;
    private long lastStructureFailureLogTick = Long.MIN_VALUE;
    private long lastStructureCheckLogTick = Long.MIN_VALUE;
    private long lastStructureDetectionLogTick = Long.MIN_VALUE;
    private long lastStructureAutoBuildLogTick = Long.MIN_VALUE;
    @Nullable
    private TankStructureFailure lastStructureFailure;

    public MetaTileEntityLargeFluidTank(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        resetTileAbilities();
    }

    public static String repeat(String a, int count) {
        //return String.valueOf(a).repeat(Math.max(0, count));
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(a);
        }
        return builder.toString();
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityLargeFluidTank(metaTileEntityId);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return GTSteamTextures.TANK_WALL;
    }

    @Override
    protected void updateFormedValid() {
    }

    @Override
    public void formStructure(@NotNull FormedStructureView formed) {
        super.formStructure(formed);
        TankDimensions dimensions = formed.getAggregate(DIMENSIONS_KEY);
        if (dimensions == null) {
            logStructureFormedMissingDimensions();
            invalidateStructure();
            return;
        }
        applyStructureDimensions(dimensions);
        initializeAbilities();
        logStructureFormed(dimensions, formed);
    }

    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.addCustom(this::addFluidAmountCapacity);
    }

    @Override
    protected void configureErrorText(MultiblockUIBuilder builder) {
        super.configureErrorText(builder);
        builder.addCustom((keyManager, syncer) -> {
            TankStructureFailure failure = getLastStructureFailure();
            if (failure != null) {
                failure.addTo(keyManager, syncer);
            }
        });
    }

    @Nullable
    private TankStructureFailure getLastStructureFailure() {
        if (isStructureFormed()) {
            return null;
        }
        if (lastStructureFailure != null) {
            return lastStructureFailure;
        }
        StructureRuntime runtime = getStructureRuntime();
        if (runtime == null) {
            return null;
        }
        StructureFailureTrace failure = runtime.getLastFailure();
        if (failure == null) {
            return null;
        }
        return TankStructureFailure.generic(
                failure.getErrorPos() == null ? getPos() : failure.getErrorPos(),
                getFrontFacing(), failure.getExpected(), failure.getActual());
    }

    private void addFluidAmountCapacity(KeyManager keyManager, UISyncer syncer) {
        if (isStructureFormed()) {
            int fluidAmountValue = syncer.syncInt(isStructureFormed() ? StoragefluidTank.getFluidAmount() : 0);
            int fluidCapacityValue = syncer.syncInt(isStructureFormed() ? capacity : 0);
            String FluidString;
            if (StoragefluidTank.getFluid() != null) {
                FluidString = syncer.syncString(StoragefluidTank.getFluid().getLocalizedName());
            } else {
                FluidString = syncer.syncString(I18n.format("gtsteam.machine.large_fluid_tank.empty"));
            }

            int length = syncer.syncInt(this.Length);
            int height = syncer.syncInt(this.Height);
            int width = syncer.syncInt(this.Width);

            keyManager.add(KeyUtil.lang(TextFormatting.GRAY, "gtsteam.machine.large_fluid_tank.size", length, height, width));
            keyManager.add(KeyUtil.lang(TextFormatting.GRAY, "gtsteam.machine.large_fluid_tank.fluid_name_text", FluidString));
            keyManager.add(KeyUtil.lang(TextFormatting.GRAY, "gtsteam.machine.large_fluid_tank.fluid_amount_text", fluidAmountValue));
            keyManager.add(KeyUtil.lang(TextFormatting.GRAY, "gtsteam.machine.large_fluid_tank.fluid_capacity_text", fluidCapacityValue));
        }
    }

    public IBlockState getTankCasingState() {
        return blockMultiblockCasing0.getState(TANK_WALL);
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
    }

    @Override
    public void checkStructurePattern() {
        logStructureCheckRequested();
        super.checkStructurePattern();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.capacity = compound.getInteger("FluidCapacity");
        this.Length = compound.getInteger("Length");
        this.Height = compound.getInteger("Height");
        this.Width = compound.getInteger("Width");

        StoragefluidTank = new FluidTank(capacity);
        if (StoragefluidTank.getFluid() == null)
            StoragefluidTank.readFromNBT(compound.getCompoundTag("StorageFluidTank"));

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("FluidCapacity", this.capacity);
        data.setInteger("Length", this.Length);
        data.setInteger("Height", this.Height);
        data.setInteger("Width", this.Width);

        data.setTag("StorageFluidTank", StoragefluidTank.writeToNBT(new NBTTagCompound()));
        return data;
    }

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    private static boolean detectRuntimeStructure(
            @NotNull StructureRuntimeDetectionContext<MetaTileEntityLargeFluidTank> context) {
        MetaTileEntityLargeFluidTank controller = context.getController();
        DimensionScanResult scan = controller.scanStructureDimensions();
        if (!scan.isSuccess()) {
            controller.logStructureFailure(scan.failurePos, scan.failureKind, scan.expected, scan.actual);
            return context.fail(scan.failurePos, scan.expected, scan.actual);
        }

        TankDimensions dimensions = scan.dimensions;
        context.emit(DIMENSIONS_KEY, dimensions);
        context.emit(WIDTH_KEY, dimensions.width);
        context.emit(HEIGHT_KEY, dimensions.height);
        context.emit(LENGTH_KEY, dimensions.length);

        RuntimeCellElements elements = controller.createRuntimeCellElements();
        RuntimeDetectionStats stats = new RuntimeDetectionStats();
        int center = dimensions.length / 2;
        for (int back = 0; back < dimensions.width; back++) {
            for (int vertical = 0; vertical < dimensions.height; vertical++) {
                for (int lateral = -center; lateral <= center; lateral++) {
                    TankCellType cellType = classifyTankCell(lateral, vertical, back, dimensions);
                    stats.recordCell(cellType);
                    BlockPos pos = context.localPos(
                            lateral, vertical, back,
                            RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.FRONT);
                    stats.recordWorldCell(context.getWorld(), pos);
                    IStructureElement<?> element = elements.get(cellType);
                    if (!context.match(pos, element)) {
                        String actual = String.valueOf(context.getWorld().getBlockState(pos));
                        controller.logStructureFailure(pos, cellType.failureKind, cellType.expected, actual);
                        return context.fail(pos, cellType.expected, actual);
                    }
                }
            }
        }
        if (stats.importFluidAbilities != 1 || stats.exportFluidAbilities != 1) {
            String actual = "found importFluid=" + stats.importFluidAbilities +
                    ", exportFluid=" + stats.exportFluidAbilities;
            controller.logStructureHatchFailure(
                    context.getControllerPos(), stats.importFluidAbilities, stats.exportFluidAbilities);
            return context.fail(context.getControllerPos(),
                    TankStructureFailureKind.HATCH_COUNTS.getDiagnosticExpected(), actual);
        }
        controller.logStructureDetectionMatched(dimensions, stats);
        return true;
    }

    @NotNull
    private RuntimeCellElements createRuntimeCellElements() {
        return new RuntimeCellElements(
                self(MetaTileEntityLargeFluidTank.class),
                blocks(getTankCasingState()),
                chain(blocks(getTankCasingState()),
                        abilities(MultiblockAbility.IMPORT_FLUIDS),
                        abilities(MultiblockAbility.EXPORT_FLUIDS),
                        blocks(Blocks.GLASS.getDefaultState())),
                air());
    }

    @NotNull
    private static TankCellType classifyTankCell(int lateral,
                                                 int vertical,
                                                 int back,
                                                 @NotNull TankDimensions dimensions) {
        if (back == 0 && vertical == 0 && lateral == 0) {
            return TankCellType.CONTROLLER;
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
            return TankCellType.EDGE;
        }
        if (boundaryCount == 1) {
            return TankCellType.FACE;
        }
        return TankCellType.INTERIOR;
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
        TankDimensions dimensions = resolveToolingDimensions(channelValues);
        StructureRuntime runtime = createToolingRuntime(dimensions);
        return Collections.singletonList(DynamicStructureTooling.previewShape(runtime, dimensions.width, channelValues));
    }

    @NotNull
    @Override
    public Map<BlockPos, StructureElementPreviewEntry> buildStructurePreviewEntries(
            @Nullable Map<String, Integer> channelValues) {
        TankDimensions dimensions = resolveToolingDimensions(channelValues);
        StructureRuntime runtime = createToolingRuntime(dimensions);
        return DynamicStructureTooling.buildPreviewEntries(runtime, dimensions.width, channelValues);
    }

    @Override
    public boolean autoBuildStructure(@NotNull StructureOperationRequest request) {
        request.requireBuildKind();
        StructureBuildResult result = createToolingRuntime(
                resolveToolingDimensions(request.getChannelValues())).buildAllPieces(request);
        logStructureAutoBuild(request, result);
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
    private StructureRuntime createToolingRuntime(@NotNull TankDimensions dimensions) {
        return createDynamicStructureRuntime(buildToolingDefinition(dimensions));
    }

    @NotNull
    private StructureDefinition<?> buildToolingDefinition(@NotNull TankDimensions dimensions) {
        return StructureDefinition.<MetaTileEntityLargeFluidTank>builder(
                        RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.BACK)
                .pieceFromTemplate(RUNTIME_PIECE, buildToolingTemplate(dimensions))
                .end()
                .globalAbilityLimit(MultiblockAbility.IMPORT_FLUIDS, 1, 1)
                .globalAbilityLimit(MultiblockAbility.EXPORT_FLUIDS, 1, 1)
                .build();
    }

    @NotNull
    private PieceTemplate buildToolingTemplate(@NotNull TankDimensions dimensions) {
        IStructureElement<?>[][][] template =
                new IStructureElement<?>[dimensions.width][dimensions.height][dimensions.length];
        RuntimeCellElements elements = createRuntimeCellElements();
        int center = dimensions.length / 2;
        for (int aisle = 0; aisle < dimensions.width; aisle++) {
            int back = dimensions.width - 1 - aisle;
            for (int vertical = 0; vertical < dimensions.height; vertical++) {
                for (int x = 0; x < dimensions.length; x++) {
                    int lateral = x - center;
                    template[aisle][vertical][x] =
                            elements.get(classifyTankCell(lateral, vertical, back, dimensions));
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
    private static TankDimensions resolveToolingDimensions(@Nullable Map<String, Integer> channelValues) {
        int width = DynamicStructureTooling.resolveChannel(
                channelValues, GTStructureChannels.STRUCTURE_WIDTH.getName(),
                DEFAULT_STRUCTURE_SIZE, MIN_STRUCTURE_SIZE, MAX_STRUCTURE_SIZE);
        int height = DynamicStructureTooling.resolveChannel(
                channelValues, GTStructureChannels.STRUCTURE_HEIGHT.getName(),
                DEFAULT_STRUCTURE_SIZE, MIN_STRUCTURE_SIZE, MAX_STRUCTURE_SIZE);
        int length = DynamicStructureTooling.resolveOddChannel(
                channelValues, GTStructureChannels.STRUCTURE_LENGTH.getName(),
                DEFAULT_STRUCTURE_SIZE, MIN_STRUCTURE_SIZE, MAX_STRUCTURE_SIZE);
        return new TankDimensions(length, width, height);
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        return getMatchingShapes(Collections.emptyMap());
    }

    public boolean isBlockEdge(@NotNull World world, @NotNull BlockPos.MutableBlockPos pos,
                               @NotNull EnumFacing direction) {
        IBlockState block = world.getBlockState(pos.move(direction));
        TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof IGregTechTileEntity iGregTechTileEntity) {
            MetaTileEntity metaTileEntity = iGregTechTileEntity.getMetaTileEntity();
            if (metaTileEntity instanceof IMultiblockAbilityPart<?> iMultiblockAbilityPart) {
                return false;
            } else {
                return !getTankCasingState().equals(block)
                        && !Blocks.GLASS.getDefaultState().equals(block);
            }
        } else {
            return !getTankCasingState().equals(block)
                    && !Blocks.GLASS.getDefaultState().equals(block);
        }
    }

    @NotNull
    private DimensionScanResult scanStructureDimensions() {
        World world = getWorld();
        if (world == null) {
            return DimensionScanResult.failure(
                    getPos(), TankStructureFailureKind.WORLD_UNAVAILABLE,
                    "large fluid tank controller has no world");
        }
        EnumFacing front = getFrontFacing();
        if (front == UP || front == DOWN) {
            return DimensionScanResult.failure(
                    getPos(), TankStructureFailureKind.HORIZONTAL_FACING, String.valueOf(front));
        }
        EnumFacing back = front.getOpposite();
        EnumFacing left = front.rotateYCCW();
        EnumFacing right = left.getOpposite();

        BlockPos.MutableBlockPos lPos = new BlockPos.MutableBlockPos(getPos());
        BlockPos.MutableBlockPos rPos = new BlockPos.MutableBlockPos(getPos());
        BlockPos.MutableBlockPos bPos = new BlockPos.MutableBlockPos(getPos());
        BlockPos.MutableBlockPos hPos = new BlockPos.MutableBlockPos(getPos());
        // 重置距离
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
            if ((isBlockEdge(world, bPos, back))) bDist = i;
            if (bDist != 0) break;
        }
        for (int i = 1; i <= 15; i++) {
            if (isBlockEdge(world, hPos, EnumFacing.UP)) hDist = i;
            if (hDist != 0) break;
        }
        TankDimensions dimensions = new TankDimensions(lDist + rDist - 1, bDist, hDist);
        if (dimensions.length < MIN_STRUCTURE_SIZE || dimensions.width < MIN_STRUCTURE_SIZE ||
                dimensions.height < MIN_STRUCTURE_SIZE) {
            return DimensionScanResult.failure(
                    getPos(), TankStructureFailureKind.MINIMUM_SIZE, dimensions.toString());
        }
        if (dimensions.length > MAX_STRUCTURE_SIZE || dimensions.width > MAX_STRUCTURE_SIZE ||
                dimensions.height > MAX_STRUCTURE_SIZE) {
            return DimensionScanResult.failure(
                    getPos(), TankStructureFailureKind.MAXIMUM_SIZE, dimensions.toString());
        }
        if (dimensions.length % 2 == 0) {
            return DimensionScanResult.failure(
                    getPos(), TankStructureFailureKind.ODD_LENGTH, dimensions.toString());
        }
        return DimensionScanResult.success(dimensions);
    }

    private void logStructureFailure(@NotNull BlockPos pos,
                                     @NotNull TankStructureFailureKind kind,
                                     @NotNull String expected,
                                     @NotNull String actual) {
        lastStructureFailure = TankStructureFailure.block(pos, getFrontFacing(), kind, actual);
        long now = getServerWorldTime();
        if (now == Long.MIN_VALUE) {
            return;
        }
        if (now - lastStructureFailureLogTick < STRUCTURE_LOG_INTERVAL_TICKS) {
            return;
        }
        lastStructureFailureLogTick = now;
        GTLog.logger.info("Large fluid tank structure check failed near controller {} at {}: expected {}, got {}",
                getPos(), pos, expected, actual);
    }

    private void logStructureHatchFailure(@NotNull BlockPos pos,
                                          int importFluidAbilities,
                                          int exportFluidAbilities) {
        TankStructureFailure failure = TankStructureFailure.hatches(
                pos, getFrontFacing(), importFluidAbilities, exportFluidAbilities);
        logStructureFailure(pos, TankStructureFailureKind.HATCH_COUNTS,
                TankStructureFailureKind.HATCH_COUNTS.getDiagnosticExpected(),
                "importFluid=" + importFluidAbilities + ", exportFluid=" + exportFluidAbilities);
        lastStructureFailure = failure;
    }

    private void logStructureAutoBuild(@NotNull StructureOperationRequest request,
                                       @NotNull StructureBuildResult result) {
        long now = getServerWorldTime();
        if (now == Long.MIN_VALUE || now - lastStructureAutoBuildLogTick < STRUCTURE_LOG_INTERVAL_TICKS) {
            return;
        }
        lastStructureAutoBuildLogTick = now;
        var orientation = request.requireOrientation();
        GTLog.logger.info("Large fluid tank auto-build near controller {}: controllerFront={}, requestFront={}, "
                        + "structureFront={}, up={}, flipped={}, noHatch={}, channels={}, {}",
                getPos(), getFrontFacing(), orientation.getFront(), orientation.getStructureFront(),
                orientation.getUp(), orientation.isFlipped(),
                StructureOperationRequest.isNoHatch(request.getChannelValues()),
                request.getChannelValues(), result.describeCounts());
    }

    private void logStructureCheckRequested() {
        long now = getServerWorldTime();
        if (now == Long.MIN_VALUE) {
            return;
        }
        if (now - lastStructureCheckLogTick < STRUCTURE_LOG_INTERVAL_TICKS) {
            return;
        }
        lastStructureCheckLogTick = now;
        GTLog.logger.info("Large fluid tank structure check requested near controller {}: formed={}, front={}",
                getPos(), isStructureFormed(), getFrontFacing());
    }

    private void logStructureDetectionMatched(@NotNull TankDimensions dimensions,
                                              @NotNull RuntimeDetectionStats stats) {
        lastStructureFailure = null;
        long now = getServerWorldTime();
        if (now == Long.MIN_VALUE) {
            return;
        }
        if (now - lastStructureDetectionLogTick < STRUCTURE_LOG_INTERVAL_TICKS) {
            return;
        }
        lastStructureDetectionLogTick = now;
        GTLog.logger.info("Large fluid tank runtime detector matched near controller {}: front={}, dimensions={}, {}",
                getPos(), getFrontFacing(), dimensions, stats);
    }

    private void logStructureFormedMissingDimensions() {
        if (getServerWorldTime() == Long.MIN_VALUE) {
            return;
        }
        GTLog.logger.info("Large fluid tank formStructure missing dimension aggregate near controller {}; invalidating",
                getPos());
    }

    private void logStructureFormed(@NotNull TankDimensions dimensions, @NotNull FormedStructureView formed) {
        if (getServerWorldTime() == Long.MIN_VALUE) {
            return;
        }
        GTLog.logger.info("Large fluid tank formed near controller {}: dimensions={}, importFluidAbilities={}, exportFluidAbilities={}",
                getPos(), dimensions,
                formed.getAbilityCount(MultiblockAbility.IMPORT_FLUIDS),
                formed.getAbilityCount(MultiblockAbility.EXPORT_FLUIDS));
    }

    private long getServerWorldTime() {
        World world = getWorld();
        if (world == null || world.isRemote) {
            return Long.MIN_VALUE;
        }
        return world.getTotalWorldTime();
    }

    private void applyStructureDimensions(@NotNull TankDimensions dimensions) {
        this.Length = dimensions.length;
        this.Width = dimensions.width;
        this.Height = dimensions.height;
    }

    private enum TankStructureFailureKind {

        CONTROLLER("gtsteam.machine.large_fluid_tank.structure_error.controller",
                "large fluid tank controller"),
        EDGE("gtsteam.machine.large_fluid_tank.structure_error.edge", "tank wall casing"),
        FACE("gtsteam.machine.large_fluid_tank.structure_error.face",
                "tank wall casing, glass, or fluid hatch"),
        INTERIOR("gtsteam.machine.large_fluid_tank.structure_error.interior", "air inside the tank"),
        HATCH_COUNTS("gtsteam.machine.large_fluid_tank.structure_error.hatches",
                "exactly 1 fluid import hatch and 1 fluid export hatch"),
        WORLD_UNAVAILABLE("gtsteam.machine.large_fluid_tank.structure_error.world", "loaded world"),
        HORIZONTAL_FACING("gtsteam.machine.large_fluid_tank.structure_error.facing",
                "horizontal controller facing"),
        MINIMUM_SIZE("gtsteam.machine.large_fluid_tank.structure_error.minimum_size",
                "tank dimensions at least 3x3x3"),
        MAXIMUM_SIZE("gtsteam.machine.large_fluid_tank.structure_error.maximum_size",
                "tank dimensions at most 15x15x15"),
        ODD_LENGTH("gtsteam.machine.large_fluid_tank.structure_error.odd_length",
                "odd tank length so the controller can be centered"),
        GENERIC("gtsteam.machine.large_fluid_tank.structure_error.generic", "structure component");

        @NotNull
        private final String langKey;
        @NotNull
        private final String diagnosticExpected;

        TankStructureFailureKind(@NotNull String langKey, @NotNull String diagnosticExpected) {
            this.langKey = langKey;
            this.diagnosticExpected = diagnosticExpected;
        }

        @NotNull
        private String getDiagnosticExpected() {
            return diagnosticExpected;
        }
    }

    private static final class TankStructureFailure {

        @NotNull
        private final BlockPos pos;
        @NotNull
        private final EnumFacing front;
        @NotNull
        private final TankStructureFailureKind kind;
        @NotNull
        private final String expected;
        @NotNull
        private final String actual;
        private final int importFluidAbilities;
        private final int exportFluidAbilities;

        private TankStructureFailure(@NotNull BlockPos pos,
                                     @NotNull EnumFacing front,
                                     @NotNull TankStructureFailureKind kind,
                                     @NotNull String expected,
                                     @NotNull String actual,
                                     int importFluidAbilities,
                                     int exportFluidAbilities) {
            this.pos = pos.toImmutable();
            this.front = front;
            this.kind = kind;
            this.expected = expected;
            this.actual = actual;
            this.importFluidAbilities = importFluidAbilities;
            this.exportFluidAbilities = exportFluidAbilities;
        }

        @NotNull
        private static TankStructureFailure block(@NotNull BlockPos pos,
                                                  @NotNull EnumFacing front,
                                                  @NotNull TankStructureFailureKind kind,
                                                  @NotNull String actual) {
            return new TankStructureFailure(pos, front, kind, "", actual, 0, 0);
        }

        @NotNull
        private static TankStructureFailure hatches(@NotNull BlockPos pos,
                                                    @NotNull EnumFacing front,
                                                    int importFluidAbilities,
                                                    int exportFluidAbilities) {
            return new TankStructureFailure(pos, front, TankStructureFailureKind.HATCH_COUNTS,
                    "", "", importFluidAbilities, exportFluidAbilities);
        }

        @NotNull
        private static TankStructureFailure generic(@NotNull BlockPos pos,
                                                    @NotNull EnumFacing front,
                                                    @Nullable String expected,
                                                    @Nullable String actual) {
            return new TankStructureFailure(pos, front, TankStructureFailureKind.GENERIC,
                    expected == null ? "" : expected, actual == null ? "" : actual, 0, 0);
        }

        private void addTo(@NotNull KeyManager keyManager, @NotNull UISyncer syncer) {
            int x = syncer.syncInt(pos.getX());
            int y = syncer.syncInt(pos.getY());
            int z = syncer.syncInt(pos.getZ());
            String facing = syncer.syncString(front.getName());
            if (kind == TankStructureFailureKind.HATCH_COUNTS) {
                keyManager.add(KeyUtil.lang(TextFormatting.RED, kind.langKey,
                        x, y, z, facing,
                        syncer.syncInt(importFluidAbilities),
                        syncer.syncInt(exportFluidAbilities)));
                return;
            }
            if (kind == TankStructureFailureKind.GENERIC) {
                keyManager.add(KeyUtil.lang(TextFormatting.RED, kind.langKey,
                        x, y, z, facing,
                        syncer.syncString(expected), syncer.syncString(actual)));
                return;
            }
            keyManager.add(KeyUtil.lang(TextFormatting.RED, kind.langKey,
                    x, y, z, facing, syncer.syncString(actual)));
        }
    }

    private enum TankCellType {

        CONTROLLER(TankStructureFailureKind.CONTROLLER),
        EDGE(TankStructureFailureKind.EDGE),
        FACE(TankStructureFailureKind.FACE),
        INTERIOR(TankStructureFailureKind.INTERIOR);

        @NotNull
        private final String expected;
        @NotNull
        private final TankStructureFailureKind failureKind;

        TankCellType(@NotNull TankStructureFailureKind failureKind) {
            this.failureKind = failureKind;
            this.expected = failureKind.getDiagnosticExpected();
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
        private IStructureElement<?> get(@NotNull TankCellType type) {
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
                    throw new IllegalStateException("Unhandled tank cell type " + type);
            }
        }
    }

    private static final class RuntimeDetectionStats {

        private int controllerCells;
        private int edgeCells;
        private int faceCells;
        private int interiorCells;
        private int abilityParts;
        private int importFluidAbilities;
        private int exportFluidAbilities;
        private int otherAbilities;

        private void recordCell(@NotNull TankCellType type) {
            switch (type) {
                case CONTROLLER:
                    controllerCells++;
                    break;
                case EDGE:
                    edgeCells++;
                    break;
                case FACE:
                    faceCells++;
                    break;
                case INTERIOR:
                    interiorCells++;
                    break;
                default:
                    throw new IllegalStateException("Unhandled tank cell type " + type);
            }
        }

        private void recordWorldCell(@NotNull World world, @NotNull BlockPos pos) {
            TileEntity entity = world.getTileEntity(pos);
            if (!(entity instanceof IGregTechTileEntity)) {
                return;
            }

            MetaTileEntity metaTileEntity = ((IGregTechTileEntity) entity).getMetaTileEntity();
            if (!(metaTileEntity instanceof IMultiblockAbilityPart<?>)) {
                return;
            }

            abilityParts++;
            IMultiblockAbilityPart<?> part = (IMultiblockAbilityPart<?>) metaTileEntity;
            for (MultiblockAbility<?> ability : part.getAbilities()) {
                if (ability == MultiblockAbility.IMPORT_FLUIDS) {
                    importFluidAbilities++;
                } else if (ability == MultiblockAbility.EXPORT_FLUIDS) {
                    exportFluidAbilities++;
                } else {
                    otherAbilities++;
                }
            }
        }

        @Override
        public String toString() {
            return "cells[controller=" + controllerCells +
                    ", edge=" + edgeCells +
                    ", face=" + faceCells +
                    ", interior=" + interiorCells +
                    "], abilities[parts=" + abilityParts +
                    ", importFluid=" + importFluidAbilities +
                    ", exportFluid=" + exportFluidAbilities +
                    ", other=" + otherAbilities + "]";
        }
    }

    private static final class TankDimensions {

        private final int length;
        private final int width;
        private final int height;

        private TankDimensions(int length, int width, int height) {
            this.length = length;
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString() {
            return "length=" + length + ", width=" + width + ", height=" + height;
        }
    }

    private static final class DimensionScanResult {

        @Nullable
        private final TankDimensions dimensions;
        @NotNull
        private final BlockPos failurePos;
        @NotNull
        private final String expected;
        @NotNull
        private final String actual;
        @NotNull
        private final TankStructureFailureKind failureKind;

        private DimensionScanResult(@Nullable TankDimensions dimensions,
                                    @NotNull BlockPos failurePos,
                                    @NotNull String expected,
                                    @NotNull String actual,
                                    @NotNull TankStructureFailureKind failureKind) {
            this.dimensions = dimensions;
            this.failurePos = failurePos.toImmutable();
            this.expected = expected;
            this.actual = actual;
            this.failureKind = failureKind;
        }

        @NotNull
        private static DimensionScanResult success(@NotNull TankDimensions dimensions) {
            return new DimensionScanResult(dimensions, BlockPos.ORIGIN,
                    "detected tank dimensions", "matched", TankStructureFailureKind.GENERIC);
        }

        @NotNull
        private static DimensionScanResult failure(@NotNull BlockPos pos,
                                                   @NotNull TankStructureFailureKind failureKind,
                                                   @NotNull String actual) {
            return new DimensionScanResult(null, pos, failureKind.getDiagnosticExpected(), actual, failureKind);
        }

        private boolean isSuccess() {
            return dimensions != null;
        }
    }

    private void initializeAbilities() {
        this.inputFluidsTank = firstAbilityTank(MultiblockAbility.IMPORT_FLUIDS);
        this.outputFluidsTank = firstAbilityTank(MultiblockAbility.EXPORT_FLUIDS);
        refreshCAP();
    }

    @NotNull
    private IFluidTank firstAbilityTank(@NotNull MultiblockAbility<IFluidTank> ability) {
        List<IFluidTank> tanks = this.getAbilities(ability);
        return tanks.isEmpty() ? new FluidTank(0) : tanks.get(0);
    }

    public IFluidTank getInputFluidsTank() {
        if (inputFluidsTank == null) inputFluidsTank = firstAbilityTank(MultiblockAbility.IMPORT_FLUIDS);
        return inputFluidsTank;
    }

    public IFluidTank getOutputFluidsTank() {
        if (outputFluidsTank == null) outputFluidsTank = firstAbilityTank(MultiblockAbility.EXPORT_FLUIDS);
        return outputFluidsTank;
    }

    public IFluidTank getStorageFluidTank() {
        if (StoragefluidTank == null) refreshCAP();
        return StoragefluidTank;
    }

    public void refreshCAP() {
        this.capacity = (Height - 2) * (Width - 2) * (Length - 2) * 16000;
        FluidStack fluidStack = StoragefluidTank.getFluid();
        this.StoragefluidTank = new FluidTank(capacity);
        if (fluidStack != null) StoragefluidTank.setFluid(fluidStack);

    }

    private void resetTileAbilities() {
        this.inputFluidsTank = new FluidTank(0);
        this.outputFluidsTank = new FluidTank(0);
        if (this.StoragefluidTank == null) {
            this.StoragefluidTank = new FluidTank(0);
        }
    }

    @Override
    public void update() {
        super.update();
        if (!this.getWorld().isRemote && this.getOffsetTimer() % 20L == 0L && this.isStructureFormed()) {
            IFluidTank inputFluidsTank = getInputFluidsTank();
            IFluidTank outputFluidsTank = getOutputFluidsTank();
            IFluidTank StorageFluidTank = getStorageFluidTank();

            FluidStack fluidStack = inputFluidsTank.getFluid();
            //inputFluidsTank.drain(FluidAmounts, true);
            int AcceptedFluidAmount = StorageFluidTank.fill(fluidStack, true);
            inputFluidsTank.drain(AcceptedFluidAmount, true);
            //outputFluidsTank.fill(fluidStack, true);
            int RemovedFluidAmount = outputFluidsTank.fill(StorageFluidTank.getFluid(), true);
            StorageFluidTank.drain(RemovedFluidAmount, true);
        }
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
        getFrontOverlay().renderSided(getFrontFacing(), renderState, translation, pipeline);
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
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gtsteam.machine.large_fluid_tank.tooltip.1"));
        tooltip.add(I18n.format("gtsteam.machine.large_fluid_tank.tooltip.2"));
        tooltip.add(I18n.format("gtsteam.machine.large_fluid_tank.tooltip.3"));
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if (isStructureFormed()) {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidInventory);
            } else {
                return null;
            }
        }
        return super.getCapability(capability, side);
    }

    @Override
    public int getProgressBarCount() {
        return 1;
    }

    @Override
    public void registerBars(List<UnaryOperator<TemplateBarBuilder>> bars, PanelSyncManager syncManager) {
        IntSyncValue fluidAmountValue = new IntSyncValue(() ->
                isStructureFormed() && StoragefluidTank != null ? StoragefluidTank.getFluidAmount() : 0);
        IntSyncValue fluidCapacityValue = new IntSyncValue(() ->
                isStructureFormed() ? capacity : 0);
        StringSyncValue fluidNameValue = new StringSyncValue(() ->
                isStructureFormed() && StoragefluidTank != null ?
                        StoragefluidTank.getFluid() != null ?
                                I18n.format("gtsteam.machine.large_fluid_tank.fluid_name_text",
                                        StoragefluidTank.getFluid().getLocalizedName()) :
                                I18n.format("gtsteam.machine.large_fluid_tank.empty") :
                        I18n.format("gtsteam.machine.large_fluid_tank.empty"));

        syncManager.syncValue("fluid_amount", fluidAmountValue);
        syncManager.syncValue("fluid_capacity", fluidCapacityValue);
        syncManager.syncValue("fluid_name", fluidNameValue);

        bars.add(barBuilder -> barBuilder
                .progress(() -> fluidCapacityValue.getIntValue() == 0 ? 0 :
                        (double) fluidAmountValue.getIntValue() / fluidCapacityValue.getIntValue())
                .texture(GTGuiTextures.PROGRESS_BAR_FLUID_RIG_DEPLETION)
                .tooltipBuilder(tooltip -> {
                    if (isStructureFormed()) {
                        tooltip.addLine(IKey.lang("%s",
                                fluidNameValue.getStringValue()));
                        tooltip.addLine(IKey.lang("gtsteam.machine.large_fluid_tank.amount",
                                fluidAmountValue.getIntValue(), fluidCapacityValue.getIntValue()));
                    } else {
                        tooltip.addLine(IKey.lang("gregtech.multiblock.invalid_structure"));
                    }
                }));
    }
}
