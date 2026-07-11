package meowmel.gtsteam.common.metatileentities.multi.heat;

import static gregtech.api.pattern.element.Elements.*;
import gregtech.api.capability.impl.HeatMultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.HeatMultiblockController;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
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
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockFireboxCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;
import meowmel.gtsteam.api.recipes.GTSRecipeMaps;
import meowmel.gtsteam.common.block.GTSteamMetaBlocks;
import meowmel.gtsteam.common.block.blocks.BlockEvaporationBed;
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
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static gregtech.api.util.RelativeDirection.*;

public class MetaTileEntityHeatEvaporationPond extends HeatMultiblockController {
    private static final int MIN_TIER = 1;
    private static final int MAX_TIER = 5;
    private static final int DEFAULT_TIER = 1;
    private static final String RUNTIME_PIECE = "runtime";
    private static final StructureContributionKey<Integer, Integer> POND_TIER_KEY =
            StructureContributionKey.uniform("gtsteam:heat_evaporation_pond/tier");
    private static final StructureContributionKey<Integer, Integer> STRUCTURE_TIER_KEY =
            StructureMatchCollector.channelValueKey(GTStructureChannels.STRUCTURE_TIER.getName());
    private static final StructureDefinition<MetaTileEntityHeatEvaporationPond> STRUCTURE_DEFINITION =
            StructureDefinition.getOrBuild("gtsteam:heat_evaporation_pond", () ->
                    StructureDefinition.<MetaTileEntityHeatEvaporationPond>builder(RIGHT, UP, BACK)
                            .piece(RUNTIME_PIECE, "S")
                            .where('S', self(MetaTileEntityHeatEvaporationPond.class))
                            .end()
                            .globalAbilityLimit(MultiblockAbility.EXPORT_ITEMS, 0, 2)
                            .globalAbilityLimit(MultiblockAbility.IMPORT_ITEMS, 0, 1)
                            .globalAbilityLimit(MultiblockAbility.EXPORT_FLUIDS, 0, 2)
                            .globalAbilityLimit(MultiblockAbility.IMPORT_FLUIDS, 0, 1)
                            .globalAbilityLimit(MultiblockAbility.INPUT_HEAT, 1, 1)
                            .runtimeDetector(MetaTileEntityHeatEvaporationPond::detectRuntimeStructure)
                            .build());

    private int tier;

    public MetaTileEntityHeatEvaporationPond(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTSRecipeMaps.EVAPORATION_RECIPES);
        this.recipeMapWorkable = new HeatEvaporationPondMultiblockRecipeLogic(this);
    }

    private static IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    private static IBlockState getPipeState() {
        return GTSteamMetaBlocks.blockEvaporationBed.getState(BlockEvaporationBed.EvaporationBedType.DIRT);
    }

    public int getTier() {
        return tier;
    }

    @Override
    protected void formStructure(@NotNull FormedStructureView formed) {
        super.formStructure(formed);
        Integer matchedTier = formed.getAggregate(POND_TIER_KEY);
        if (matchedTier == null) {
            invalidateStructure();
            return;
        }
        this.tier = matchedTier;
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
            @NotNull StructureRuntimeDetectionContext<MetaTileEntityHeatEvaporationPond> context) {
        MetaTileEntityHeatEvaporationPond controller = context.getController();
        TierScanResult scan = controller.scanStructureTier();
        if (!scan.isSuccess()) {
            return context.fail(scan.failurePos, scan.expected, scan.actual);
        }

        int detectedTier = scan.tier;
        context.emit(POND_TIER_KEY, detectedTier);
        context.emit(STRUCTURE_TIER_KEY, detectedTier);

        RuntimeCellElements elements = controller.createRuntimeCellElements();
        int size = sizeForTier(detectedTier);
        int center = size / 2;
        int controllerAisle = controllerAisleForTier(detectedTier);
        for (int aisle = 0; aisle < size; aisle++) {
            int localBack = controllerAisle - aisle;
            for (int y = 0; y < 2; y++) {
                int localY = y - 1;
                for (int x = 0; x < size; x++) {
                    PondCellType cellType = classifyPondCell(x, y, aisle, detectedTier);
                    BlockPos pos = context.localPos(
                            x - center, localY, localBack,
                            RIGHT, UP, FRONT);
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
    private RuntimeCellElements createRuntimeCellElements() {
        return new RuntimeCellElements(
                self(MetaTileEntityHeatEvaporationPond.class),
                chain(blocks(getCasingState()),
                        abilities(0, 2, MultiblockAbility.EXPORT_ITEMS),
                        abilities(0, 1, MultiblockAbility.IMPORT_ITEMS),
                        abilities(0, 2, MultiblockAbility.EXPORT_FLUIDS),
                        abilities(0, 1, MultiblockAbility.IMPORT_FLUIDS),
                        abilities(1, 1, MultiblockAbility.INPUT_HEAT)),
                blocks(getFireBoxState()),
                blocks(getPipeState()),
                any());
    }

    @NotNull
    private static PondCellType classifyPondCell(int x, int y, int aisle, int tier) {
        int size = sizeForTier(tier);
        int center = size / 2;
        int controllerAisle = controllerAisleForTier(tier);

        if (y == 0) {
            if (aisle == 0 || aisle == size - 1 || x == 0 || x == size - 1) {
                return PondCellType.FIREBOX;
            }
            if (aisle >= 2 && aisle <= size - 3 && x >= 2 && x <= size - 3) {
                return PondCellType.PIPE;
            }
            return PondCellType.CASING;
        }

        if (aisle == controllerAisle && x == center) {
            return PondCellType.CONTROLLER;
        }
        if (aisle == 0 || aisle == size - 1 || x == 0 || x == size - 1) {
            return PondCellType.ANY;
        }
        if (aisle == 1 || aisle == controllerAisle || x == 1 || x == size - 2) {
            return PondCellType.CASING;
        }
        return PondCellType.ANY;
    }

    private static int sizeForTier(int tier) {
        return tier * 2 + 3;
    }

    private static int controllerAisleForTier(int tier) {
        return sizeForTier(tier) - 2;
    }

    @NotNull
    @Override
    public List<StructureChannel> getSupportedChannels() {
        return Collections.singletonList(GTStructureChannels.STRUCTURE_TIER);
    }

    @Override
    public int[] getChannelRange(@NotNull StructureChannel channel) {
        if (GTStructureChannels.STRUCTURE_TIER.getName().equals(channel.getName())) {
            return new int[] { MIN_TIER, MAX_TIER };
        }
        return super.getChannelRange(channel);
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes(@Nullable Map<String, Integer> channelValues) {
        int toolingTier = resolveToolingTier(channelValues);
        StructureRuntime runtime = createToolingRuntime(toolingTier);
        return Collections.singletonList(DynamicStructureTooling.previewShape(
                runtime, sizeForTier(toolingTier), channelValues));
    }

    @NotNull
    @Override
    public Map<BlockPos, StructureElementPreviewEntry> buildStructurePreviewEntries(
            @Nullable Map<String, Integer> channelValues) {
        int toolingTier = resolveToolingTier(channelValues);
        StructureRuntime runtime = createToolingRuntime(toolingTier);
        return DynamicStructureTooling.buildPreviewEntries(runtime, sizeForTier(toolingTier), channelValues);
    }

    @Override
    public boolean autoBuildStructure(@NotNull StructureOperationRequest request) {
        request.requireBuildKind();
        createToolingRuntime(resolveToolingTier(request.getChannelValues())).buildAllPieces(request);
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
        return createToolingRuntime(resolveToolingTier(request.getChannelValues())).hintAllPieces(request);
    }

    @NotNull
    private StructureRuntime createToolingRuntime(int toolingTier) {
        return createDynamicStructureRuntime(buildToolingDefinition(toolingTier));
    }

    @NotNull
    private StructureDefinition<?> buildToolingDefinition(int toolingTier) {
        return StructureDefinition.<MetaTileEntityHeatEvaporationPond>builder(RIGHT, UP, BACK)
                .pieceFromTemplate(RUNTIME_PIECE, buildToolingTemplate(toolingTier))
                .end()
                .globalAbilityLimit(MultiblockAbility.EXPORT_ITEMS, 0, 2)
                .globalAbilityLimit(MultiblockAbility.IMPORT_ITEMS, 0, 1)
                .globalAbilityLimit(MultiblockAbility.EXPORT_FLUIDS, 0, 2)
                .globalAbilityLimit(MultiblockAbility.IMPORT_FLUIDS, 0, 1)
                .globalAbilityLimit(MultiblockAbility.INPUT_HEAT, 1, 1)
                .build();
    }

    @NotNull
    private PieceTemplate buildToolingTemplate(int toolingTier) {
        int size = sizeForTier(toolingTier);
        int center = size / 2;
        int controllerAisle = controllerAisleForTier(toolingTier);
        IStructureElement<?>[][][] template = new IStructureElement<?>[size][2][size];
        RuntimeCellElements elements = createRuntimeCellElements();
        for (int aisle = 0; aisle < size; aisle++) {
            for (int y = 0; y < 2; y++) {
                for (int x = 0; x < size; x++) {
                    template[aisle][y][x] = elements.get(classifyPondCell(x, y, aisle, toolingTier));
                }
            }
        }

        int[][] repetitions = new int[size][2];
        for (int i = 0; i < repetitions.length; i++) {
            repetitions[i][0] = 1;
            repetitions[i][1] = 1;
        }
        int[] centerOffset = new int[] {
                center, 1, controllerAisle,
                controllerAisle, controllerAisle
        };
        return new PieceTemplate(
                template,
                new RelativeDirection[] { RIGHT, UP, BACK },
                repetitions,
                new String[repetitions.length],
                centerOffset,
                null);
    }

    private static int resolveToolingTier(@Nullable Map<String, Integer> channelValues) {
        return DynamicStructureTooling.resolveChannel(
                channelValues, GTStructureChannels.STRUCTURE_TIER.getName(),
                DEFAULT_TIER, MIN_TIER, MAX_TIER);
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        return getMatchingShapes(Collections.emptyMap());
    }

    @NotNull
    private TierScanResult scanStructureTier() {
        World world = getWorld();
        if (world == null) {
            return TierScanResult.failure(
                    getPos(), "loaded world", "heat evaporation pond controller has no world");
        }
        BlockPos.MutableBlockPos bPos = new BlockPos.MutableBlockPos(getPos());
        EnumFacing front = getFrontFacing();
        EnumFacing back = front.getOpposite();
        int detectedTier = 0;
        for (int i = 0; i < 16; i += 1) {
            if (isBlockEdge(world, bPos, back)) detectedTier = (i + 1) / 2;
        }
        if (detectedTier < MIN_TIER || detectedTier > MAX_TIER) {
            return TierScanResult.failure(
                    getPos(), "evaporation pond tier from 1 to 5", "detected tier " + detectedTier);
        }
        return TierScanResult.success(detectedTier);
    }

    public boolean isBlockEdge(@NotNull World world, @NotNull BlockPos.MutableBlockPos pos,
                               @NotNull EnumFacing direction) {
        IBlockState block = world.getBlockState(pos.move(direction));
        TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof IGregTechTileEntity iGregTechTileEntity) {
            MetaTileEntity metaTileEntity = iGregTechTileEntity.getMetaTileEntity();
            if (metaTileEntity instanceof IMultiblockAbilityPart<?>) {
                return true;
            } else {
                return getCasingState().equals(block);
            }
        } else {
            return getCasingState().equals(block);
        }
    }

    private enum PondCellType {

        CONTROLLER("heat evaporation pond controller"),
        CASING("steel casing or allowed hatch"),
        FIREBOX("steel firebox casing"),
        PIPE("evaporation bed"),
        ANY("any block");

        @NotNull
        private final String expected;

        PondCellType(@NotNull String expected) {
            this.expected = expected;
        }
    }

    private static final class RuntimeCellElements {

        @NotNull
        private final IStructureElement<?> controller;
        @NotNull
        private final IStructureElement<?> casing;
        @NotNull
        private final IStructureElement<?> firebox;
        @NotNull
        private final IStructureElement<?> pipe;
        @NotNull
        private final IStructureElement<?> any;

        private RuntimeCellElements(@NotNull IStructureElement<?> controller,
                                    @NotNull IStructureElement<?> casing,
                                    @NotNull IStructureElement<?> firebox,
                                    @NotNull IStructureElement<?> pipe,
                                    @NotNull IStructureElement<?> any) {
            this.controller = controller.compile();
            this.casing = casing.compile();
            this.firebox = firebox.compile();
            this.pipe = pipe.compile();
            this.any = any.compile();
        }

        @NotNull
        private IStructureElement<?> get(@NotNull PondCellType type) {
            switch (type) {
                case CONTROLLER:
                    return controller;
                case CASING:
                    return casing;
                case FIREBOX:
                    return firebox;
                case PIPE:
                    return pipe;
                case ANY:
                    return any;
                default:
                    throw new IllegalStateException("Unhandled pond cell type " + type);
            }
        }
    }

    private static final class TierScanResult {

        private final int tier;
        @NotNull
        private final BlockPos failurePos;
        @NotNull
        private final String expected;
        @NotNull
        private final String actual;

        private TierScanResult(int tier,
                               @NotNull BlockPos failurePos,
                               @NotNull String expected,
                               @NotNull String actual) {
            this.tier = tier;
            this.failurePos = failurePos.toImmutable();
            this.expected = expected;
            this.actual = actual;
        }

        @NotNull
        private static TierScanResult success(int tier) {
            return new TierScanResult(tier, BlockPos.ORIGIN, "detected pond tier", "matched");
        }

        @NotNull
        private static TierScanResult failure(@NotNull BlockPos pos,
                                              @NotNull String expected,
                                              @NotNull String actual) {
            return new TierScanResult(0, pos, expected, actual);
        }

        private boolean isSuccess() {
            return tier >= MIN_TIER && tier <= MAX_TIER;
        }
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    private IBlockState getFireBoxState() {
        return MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.STEEL_FIREBOX);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityHeatEvaporationPond(metaTileEntityId);
    }

    @SideOnly(Side.CLIENT)
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    public SoundEvent getBreakdownSound() {
        return GTSoundEvents.BREAKDOWN_ELECTRICAL;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("tier", tier);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        tier = data.getInteger("tier");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeVarInt(tier);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        tier = buf.readVarInt();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        TooltipBuilder.create().addHeatMachine(1).build(this, tooltip);
        TooltipBuilder.create().addSpecialLogic().build(this, tooltip);
        tooltip.add(I18n.format("多方块共5个等级，且为正方形，长宽必须相等"));
        tooltip.add(I18n.format("多方块结构长宽每拓展一次，配方并行翻倍"));
    }

    public class HeatEvaporationPondMultiblockRecipeLogic extends HeatMultiblockRecipeLogic {

        public HeatEvaporationPondMultiblockRecipeLogic(HeatMultiblockController tileEntity) {
            super(tileEntity);
        }

        //每一等级 并行翻倍
        @Override
        public int getParallelLimit() {
            return (int) Math.pow(2, getTier() - 1);
        }
    }
}
