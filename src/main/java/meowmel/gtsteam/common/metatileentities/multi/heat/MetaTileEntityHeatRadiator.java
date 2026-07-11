package meowmel.gtsteam.common.metatileentities.multi.heat;

import static gregtech.api.pattern.element.Elements.*;

import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.capability.IHeatable;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MetaTileEntityBaseWithControl;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;

import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
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
import gregtech.api.util.RelativeDirection;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockMetalCasing.MetalCasingType;
import gregtech.common.blocks.MetaBlocks;
import lombok.Getter;
import meowmel.gtsteam.client.textures.GTSteamTextures;
import meowmel.gtsteam.common.block.GTSteamMetaBlocks;
import meowmel.gtsteam.common.block.blocks.BlockSerpentine;
import meowmel.gtsteam.common.metatileentities.multi.DynamicStructureTooling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static gregtech.api.util.RelativeDirection.*;


public class MetaTileEntityHeatRadiator extends MetaTileEntityBaseWithControl {

    public static final int MIN_RADIUS = 1;
    public static final int MIN_HEIGHT = 1;
    private static final int MAX_RADIUS = 5;
    private static final int MAX_BODY_HEIGHT = 15;
    private static final int DEFAULT_WIDTH = 3;
    private static final int DEFAULT_STRUCTURE_HEIGHT = 3;
    private static final String RUNTIME_PIECE = "runtime";
    private static final StructureContributionKey<RadiatorDimensions, RadiatorDimensions> DIMENSIONS_KEY =
            StructureContributionKey.uniform("gtsteam:heat_radiator/dimensions");
    private static final StructureContributionKey<Integer, Integer> WIDTH_KEY =
            StructureMatchCollector.channelValueKey(GTStructureChannels.STRUCTURE_WIDTH.getName());
    private static final StructureContributionKey<Integer, Integer> HEIGHT_KEY =
            StructureMatchCollector.channelValueKey(GTStructureChannels.STRUCTURE_HEIGHT.getName());
    private static final StructureDefinition<MetaTileEntityHeatRadiator> STRUCTURE_DEFINITION =
            StructureDefinition.getOrBuild("gtsteam:heat_radiator", () ->
                    StructureDefinition.<MetaTileEntityHeatRadiator>builder(RIGHT, FRONT, UP)
                            .piece(RUNTIME_PIECE, "S")
                            .where('S', self(MetaTileEntityHeatRadiator.class))
                            .end()
                            .globalAbilityLimit(MultiblockAbility.INPUT_HEAT, 1, -1)
                            .globalAbilityLimit(MultiblockAbility.IMPORT_FLUIDS, 1, -1)
                            .globalAbilityLimit(MultiblockAbility.EXPORT_FLUIDS, 1, -1)
                            .runtimeDetector(MetaTileEntityHeatRadiator::detectRuntimeStructure)
                            .build());
    private int sDist = 0;
    private int bDist = 0;
    private int area;
    @Getter
    List<IHeatable> heatHatch = null;

    public MetaTileEntityHeatRadiator(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    protected void formStructure(@NotNull FormedStructureView formed) {
        super.formStructure(formed);
        RadiatorDimensions dimensions = formed.getAggregate(DIMENSIONS_KEY);
        if (dimensions == null) {
            invalidateStructure();
            return;
        }
        applyStructureDimensions(dimensions);
    }

    @Override
    protected void initializeAbilities() {
        if (!getAbilities(MultiblockAbility.INPUT_HEAT).isEmpty())
            this.heatHatch = getAbilities(MultiblockAbility.INPUT_HEAT);
        else this.heatHatch = null;
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.heatHatch = null;
    }

    @Override
    protected void updateFormedValid() {
        if(getHeatHatch()!=null && !heatHatch.isEmpty())
        {
            for (IHeatable heatHatch : heatHatch)
            {
                if(heatHatch.getHeatStored()>0)
                {
                    heatHatch.changeHeat(-area* 8L);
                    return;
                }
            }
        }
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityHeatRadiator(metaTileEntityId);
    }

    @Override
    public @NotNull List<ITextComponent> getDataInfo() {
        return Collections.emptyList();
    }

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    private static boolean detectRuntimeStructure(
            @NotNull StructureRuntimeDetectionContext<MetaTileEntityHeatRadiator> context) {
        MetaTileEntityHeatRadiator controller = context.getController();
        DimensionScanResult scan = controller.scanStructureDimensions();
        if (!scan.isSuccess()) {
            return context.fail(scan.failurePos, scan.expected, scan.actual);
        }

        RadiatorDimensions dimensions = scan.dimensions;
        context.emit(DIMENSIONS_KEY, dimensions);
        context.emit(WIDTH_KEY, dimensions.getStructureWidth());
        context.emit(HEIGHT_KEY, dimensions.getStructureHeight());

        RuntimeCellElements elements = controller.createRuntimeCellElements();
        for (int vertical = 0; vertical < dimensions.getStructureHeight(); vertical++) {
            for (int lateral = -dimensions.radius; lateral <= dimensions.radius; lateral++) {
                RadiatorCellType cellType = classifyRadiatorCell(lateral, vertical, dimensions);
                BlockPos pos = context.localPos(
                        lateral, 0, vertical,
                        RIGHT, FRONT, UP);
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
                self(MetaTileEntityHeatRadiator.class),
                chain(blocks(getCasingState()),
                        abilities(1, -1, 1, MultiblockAbility.INPUT_HEAT)),
                blocks(getRadiatorElementState()),
                chain(blocks(getCasingState()),
                        abilities(1, -1, 1, MultiblockAbility.IMPORT_FLUIDS),
                        abilities(1, -1, 1, MultiblockAbility.EXPORT_FLUIDS)));
    }

    @NotNull
    private static RadiatorCellType classifyRadiatorCell(int lateral,
                                                         int vertical,
                                                         @NotNull RadiatorDimensions dimensions) {
        if (vertical == 0) {
            return lateral == 0 ? RadiatorCellType.CONTROLLER : RadiatorCellType.CASING_OR_HEAT;
        }
        if (vertical == dimensions.getStructureHeight() - 1) {
            return RadiatorCellType.CASING_OR_HEAT;
        }
        return Math.abs(lateral) == dimensions.radius
                ? RadiatorCellType.CASING_OR_FLUID
                : RadiatorCellType.RADIATOR_ELEMENT;
    }

    @NotNull
    @Override
    public List<StructureChannel> getSupportedChannels() {
        return Arrays.asList(
                GTStructureChannels.STRUCTURE_WIDTH,
                GTStructureChannels.STRUCTURE_HEIGHT);
    }

    @Override
    public int[] getChannelRange(@NotNull StructureChannel channel) {
        String channelName = channel.getName();
        if (GTStructureChannels.STRUCTURE_WIDTH.getName().equals(channelName)) {
            return new int[] { MIN_RADIUS * 2 + 1, MAX_RADIUS * 2 + 1 };
        }
        if (GTStructureChannels.STRUCTURE_HEIGHT.getName().equals(channelName)) {
            return new int[] { MIN_HEIGHT + 2, MAX_BODY_HEIGHT + 2 };
        }
        return super.getChannelRange(channel);
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes(@Nullable Map<String, Integer> channelValues) {
        RadiatorDimensions dimensions = resolveToolingDimensions(channelValues);
        StructureRuntime runtime = createToolingRuntime(dimensions);
        return Collections.singletonList(DynamicStructureTooling.previewShape(
                runtime, dimensions.getStructureHeight(), channelValues));
    }

    @NotNull
    @Override
    public Map<BlockPos, StructureElementPreviewEntry> buildStructurePreviewEntries(
            @Nullable Map<String, Integer> channelValues) {
        RadiatorDimensions dimensions = resolveToolingDimensions(channelValues);
        StructureRuntime runtime = createToolingRuntime(dimensions);
        return DynamicStructureTooling.buildPreviewEntries(runtime, dimensions.getStructureHeight(), channelValues);
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
    private StructureRuntime createToolingRuntime(@NotNull RadiatorDimensions dimensions) {
        return createDynamicStructureRuntime(buildToolingDefinition(dimensions));
    }

    @NotNull
    private StructureDefinition<?> buildToolingDefinition(@NotNull RadiatorDimensions dimensions) {
        return StructureDefinition.<MetaTileEntityHeatRadiator>builder(RIGHT, FRONT, UP)
                .pieceFromTemplate(RUNTIME_PIECE, buildToolingTemplate(dimensions))
                .end()
                .globalAbilityLimit(MultiblockAbility.INPUT_HEAT, 1, -1)
                .globalAbilityLimit(MultiblockAbility.IMPORT_FLUIDS, 1, -1)
                .globalAbilityLimit(MultiblockAbility.EXPORT_FLUIDS, 1, -1)
                .build();
    }

    @NotNull
    private PieceTemplate buildToolingTemplate(@NotNull RadiatorDimensions dimensions) {
        int structureHeight = dimensions.getStructureHeight();
        int structureWidth = dimensions.getStructureWidth();
        IStructureElement<?>[][][] template = new IStructureElement<?>[structureHeight][1][structureWidth];
        RuntimeCellElements elements = createRuntimeCellElements();
        for (int vertical = 0; vertical < dimensions.getStructureHeight(); vertical++) {
            for (int lateral = -dimensions.radius; lateral <= dimensions.radius; lateral++) {
                template[vertical][0][lateral + dimensions.radius] =
                        elements.get(classifyRadiatorCell(lateral, vertical, dimensions));
            }
        }

        int[][] repetitions = new int[structureHeight][2];
        for (int i = 0; i < repetitions.length; i++) {
            repetitions[i][0] = 1;
            repetitions[i][1] = 1;
        }
        return new PieceTemplate(
                template,
                new RelativeDirection[] { RIGHT, FRONT, UP },
                repetitions,
                new String[repetitions.length],
                new int[] { dimensions.radius, 0, 0, 0, 0 },
                null);
    }

    @NotNull
    private static RadiatorDimensions resolveToolingDimensions(@Nullable Map<String, Integer> channelValues) {
        int width = DynamicStructureTooling.resolveOddChannel(
                channelValues, GTStructureChannels.STRUCTURE_WIDTH.getName(),
                DEFAULT_WIDTH, MIN_RADIUS * 2 + 1, MAX_RADIUS * 2 + 1);
        int structureHeight = DynamicStructureTooling.resolveChannel(
                channelValues, GTStructureChannels.STRUCTURE_HEIGHT.getName(),
                DEFAULT_STRUCTURE_HEIGHT, MIN_HEIGHT + 2, MAX_BODY_HEIGHT + 2);
        return new RadiatorDimensions((width - 1) / 2, structureHeight - 2);
    }

    @NotNull
    protected DimensionScanResult scanStructureDimensions() {
        World world = getWorld();
        if (world == null) {
            return DimensionScanResult.failure(
                    getPos(), "loaded world", "heat radiator controller has no world");
        }
        EnumFacing front = getFrontFacing();
        EnumFacing up = UP.getRelativeFacing(this.getFrontFacing(), this.getUpwardsFacing(), this.isFlipped()); // From
        // the
        // flare
        // stack,
        // I
        // hate
        // free
        // rotation.
        EnumFacing left = front.rotateAround(up.getAxis());
        EnumFacing right = left.getOpposite();

        BlockPos.MutableBlockPos lPos = new BlockPos.MutableBlockPos(getPos().offset(up));
        BlockPos.MutableBlockPos rPos = new BlockPos.MutableBlockPos(getPos().offset(up));
        BlockPos.MutableBlockPos uPos = new BlockPos.MutableBlockPos(getPos());

        int sDist = 0;
        int bDist = 0;

        // find the left, right, and upper distances for the structure pattern
        // maximum size is 11x16 including walls
        for (int i = 0; i < 16; ++i) {
            if (isBlockEdge(world, uPos, up)) {
                bDist = i;
                break;
            }
        }

        for (int i = 1; i < 6; i++) { // start at 1 for an off-by-one error
            if (isBlockEdge(world, lPos, left) & isBlockEdge(world, rPos, right)) {
                sDist = i; // The & is absolutely *essential* here.
                break;
            }
        }

        RadiatorDimensions dimensions = new RadiatorDimensions(sDist, bDist);
        if (dimensions.radius < MIN_RADIUS || dimensions.bodyHeight < MIN_HEIGHT) {
            return DimensionScanResult.failure(
                    getPos(), "radiator radius and body height at least 1", dimensions.toString());
        }
        if (dimensions.radius > MAX_RADIUS || dimensions.bodyHeight > MAX_BODY_HEIGHT) {
            return DimensionScanResult.failure(
                    getPos(), "radiator radius up to 5 and body height up to 15", dimensions.toString());
        }
        return DimensionScanResult.success(dimensions);
    }

    private void applyStructureDimensions(@NotNull RadiatorDimensions dimensions) {
        this.sDist = dimensions.radius;
        this.bDist = dimensions.bodyHeight;
        this.area = dimensions.bodyHeight * (2 * dimensions.radius - 1);

        if (this.getWorld() != null && !this.getWorld().isRemote) {
            writeCustomData(GregtechDataCodes.UPDATE_STRUCTURE_SIZE, buf -> {
                buf.writeInt(this.sDist);
                buf.writeInt(this.bDist);
                buf.writeInt(this.area);
            });
        }
    }

    private enum RadiatorCellType {

        CONTROLLER("heat radiator controller"),
        CASING_OR_HEAT("steel casing or heat input hatch"),
        RADIATOR_ELEMENT("radiator element"),
        CASING_OR_FLUID("steel casing or fluid hatch");

        @NotNull
        private final String expected;

        RadiatorCellType(@NotNull String expected) {
            this.expected = expected;
        }
    }

    private static final class RuntimeCellElements {

        @NotNull
        private final IStructureElement<?> controller;
        @NotNull
        private final IStructureElement<?> casingOrHeat;
        @NotNull
        private final IStructureElement<?> radiatorElement;
        @NotNull
        private final IStructureElement<?> casingOrFluid;

        private RuntimeCellElements(@NotNull IStructureElement<?> controller,
                                    @NotNull IStructureElement<?> casingOrHeat,
                                    @NotNull IStructureElement<?> radiatorElement,
                                    @NotNull IStructureElement<?> casingOrFluid) {
            this.controller = controller.compile();
            this.casingOrHeat = casingOrHeat.compile();
            this.radiatorElement = radiatorElement.compile();
            this.casingOrFluid = casingOrFluid.compile();
        }

        @NotNull
        private IStructureElement<?> get(@NotNull RadiatorCellType type) {
            switch (type) {
                case CONTROLLER:
                    return controller;
                case CASING_OR_HEAT:
                    return casingOrHeat;
                case RADIATOR_ELEMENT:
                    return radiatorElement;
                case CASING_OR_FLUID:
                    return casingOrFluid;
                default:
                    throw new IllegalStateException("Unhandled radiator cell type " + type);
            }
        }
    }

    private static final class RadiatorDimensions {

        private final int radius;
        private final int bodyHeight;

        private RadiatorDimensions(int radius, int bodyHeight) {
            this.radius = radius;
            this.bodyHeight = bodyHeight;
        }

        private int getStructureWidth() {
            return radius * 2 + 1;
        }

        private int getStructureHeight() {
            return bodyHeight + 2;
        }

        @Override
        public String toString() {
            return "radius=" + radius + ", bodyHeight=" + bodyHeight;
        }
    }

    protected static final class DimensionScanResult {

        @Nullable
        private final RadiatorDimensions dimensions;
        @NotNull
        private final BlockPos failurePos;
        @NotNull
        private final String expected;
        @NotNull
        private final String actual;

        private DimensionScanResult(@Nullable RadiatorDimensions dimensions,
                                    @NotNull BlockPos failurePos,
                                    @NotNull String expected,
                                    @NotNull String actual) {
            this.dimensions = dimensions;
            this.failurePos = failurePos.toImmutable();
            this.expected = expected;
            this.actual = actual;
        }

        @NotNull
        private static DimensionScanResult success(@NotNull RadiatorDimensions dimensions) {
            return new DimensionScanResult(dimensions, BlockPos.ORIGIN, "detected radiator dimensions", "matched");
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

    @Override
    public void checkStructurePattern() {
        super.checkStructurePattern();
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        return getMatchingShapes(Collections.emptyMap());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("sDist", this.sDist);
        data.setInteger("bDist", this.bDist);
        data.setInteger("area", this.area);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.sDist = data.getInteger("sDist");
        this.bDist = data.getInteger("bDist");
        this.area = data.getInteger("area");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(this.sDist);
        buf.writeInt(this.bDist);
        buf.writeInt(this.area);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.sDist = buf.readInt();
        this.bDist = buf.readInt();
        this.area = buf.readInt();
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GregtechDataCodes.UPDATE_STRUCTURE_SIZE) {
            this.sDist = buf.readInt();
            this.bDist = buf.readInt();
            this.area = buf.readInt();
        } else if (dataId == GregtechDataCodes.WORKABLE_ACTIVE) {
            scheduleRenderUpdate();
        } else if (dataId == GregtechDataCodes.WORKING_ENABLED) {
            scheduleRenderUpdate();
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gtsteam.multiblock.heat_radiator.tooltip.1"));
        tooltip.add(I18n.format("gtsteam.multiblock.heat_radiator.tooltip.2"));
    }

    public boolean isBlockEdge(@Nonnull World world, @Nonnull BlockPos.MutableBlockPos pos,
                               @Nonnull EnumFacing direction) {
        return getCasingState().equals(world.getBlockState(pos.move(direction))) ||
                world.getTileEntity(pos) instanceof MetaTileEntityHolder;
    }

    public IBlockState getRadiatorElementState() {
        return GTSteamMetaBlocks.blockSerpentine.getState(BlockSerpentine.SerpentineType.BASIC);
    }

    public IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(MetalCasingType.STEEL_SOLID);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return GTSteamTextures.RADIATOR_OVERLAY;
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
