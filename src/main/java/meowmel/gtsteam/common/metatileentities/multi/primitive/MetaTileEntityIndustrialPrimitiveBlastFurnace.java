package meowmel.gtsteam.common.metatileentities.multi.primitive;

import static gregtech.api.pattern.element.Elements.*;
import gregtech.api.GTValues;
import gregtech.api.capability.impl.NoEnergyMultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.NoEnergyMultiblockController;
import gregtech.api.metatileentity.multiblock.ui.KeyManager;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.metatileentity.multiblock.ui.UISyncer;
import gregtech.api.mui.GTGuiTheme;
import gregtech.api.pattern.*;
import gregtech.api.pattern.casing.CasingDefinition;
import gregtech.api.pattern.casing.GTStructureChannels;
import gregtech.api.pattern.casing.StructureChannel;
import gregtech.api.pattern.element.IStructureElement;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.pattern.element.impl.CasingElement;
import gregtech.api.pattern.element.impl.HatchElement;
import gregtech.api.recipes.logic.OCResult;
import gregtech.api.recipes.properties.RecipePropertyStorage;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.BlockInfo;
import gregtech.api.util.GTUtility;
import gregtech.api.util.KeyUtil;
import gregtech.api.util.RelativeDirection;
import gregtech.api.util.tooltips.InformationHandler;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockFireboxCasing;
import gregtech.common.blocks.MetaBlocks;
import meowmel.gtsteam.client.textures.GTSteamTextures;
import meowmel.gtsteam.common.block.GTSteamMetaBlocks;
import meowmel.gtsteam.common.block.blocks.BlockMultiblockCasing0;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static gregtech.api.recipes.RecipeMaps.PRIMITIVE_BLAST_FURNACE_RECIPES;

public class MetaTileEntityIndustrialPrimitiveBlastFurnace extends NoEnergyMultiblockController {

    private static final IStructureElement SNOW_PREDICATE = blockPredicate(GTUtility::isBlockSnow);
    private static final RelativeDirection[] STRUCTURE_DIRECTIONS = new RelativeDirection[]{
            RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.BACK};
    private static final int CONTROLLER_X = 6;
    private static final int CONTROLLER_Y = 1;
    private static final int CONTROLLER_Z = 6;
    private static final int CORE_CASING_COUNT = 87;
    private static final int OPTIONAL_ITEM_INPUTS = 4;
    private static final int OPTIONAL_ITEM_OUTPUTS = 4;

    private static final StructureDefinition<?> DEFINITION = StructureDefinition.getOrBuild(
            "gtsteam:industrial_primitive_blast_furnace",
            MetaTileEntityIndustrialPrimitiveBlastFurnace::buildStructureDefinition);
    int TEMP = 300;
    int MIN_TEMP = 300;
    int MAX_TEMP = 1500;
    private int auxiliaryBlastFurnaces = 0;

    public MetaTileEntityIndustrialPrimitiveBlastFurnace(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, PRIMITIVE_BLAST_FURNACE_RECIPES);
        this.recipeMapWorkable = new IndustrialPrimitiveBlastFurnaceLogic(this);
    }

    private static IBlockState getFrameState() {
        return MetaBlocks.FRAMES.get(Materials.Steel).getBlock(Materials.Steel);
    }

    private static IBlockState getBoilerState() {
        return MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE);
    }

    private static IBlockState getFireBoxState() {
        return MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.STEEL_FIREBOX);
    }

    public static IBlockState getCasingState() {
        return GTSteamMetaBlocks.blockMultiblockCasing0.getState(BlockMultiblockCasing0.CasingType.GALVANIZED_PORCELAIN_TILES);
    }

    private static StructureDefinition<?> buildStructureDefinition() {
        StructureDefinition.Builder<MetaTileEntityIndustrialPrimitiveBlastFurnace> builder =
                StructureDefinition.builder(
                        RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.BACK);

        builder.piece("core", new String[][]{
                        {"     DDD     ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             "},
                        {"    CDDDC    ", "    CDDDC    ", "    CDDDC    ", "     DDD     ", "             ", "             ", "             ", "             ", "             "},
                        {"    DDDDD    ", "    D###D    ", "    D###D    ", "    D###D    ", "     DDD     ", "      D      ", "      D      ", "      D      ", "      D      "},
                        {"    DDDDD    ", "    D#&#D    ", "    D###D    ", "    D###D    ", "    D###D    ", "     D#D     ", "     D#D     ", "     D#D     ", "     D#D     "},
                        {"    DDDDD    ", "    D###D    ", "    D###D    ", "    D###D    ", "     DDD     ", "      D      ", "      D      ", "      D      ", "      D      "},
                        {"    CDDDC    ", "    CDSDC    ", "    CDDDC    ", "     DDD     ", "             ", "             ", "             ", "             ", "             "},
                        {"     DDD     ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             "}
                }, Vec3i.NULL_VECTOR)
                .where('S', self(MetaTileEntityIndustrialPrimitiveBlastFurnace.class))
                .where('C', blocks(getFrameState()))
                .where('D', coreCasingWithBuses())
                .where('&', chain(air(), SNOW_PREDICATE))
                .where('#', air())
                .where(' ', any())
                .end();

        builder.piece("aux_left", new String[][]{
                        {"             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             "},
                        {"             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             "},
                        {"aaad         ", "dddl         ", "ldll         ", "ldll         ", "ldll         ", "ldll         ", "             ", "             ", "             "},
                        {"aaad         ", "dldh         ", "dldl         ", "dldl         ", "dldl         ", "dldl         ", "             ", "             ", "             "},
                        {"aaad         ", "dddl         ", "ldll         ", "ldll         ", "ldll         ", "ldll         ", "             ", "             ", "             "},
                        {"             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             "},
                        {"             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             "}
                }, Vec3i.NULL_VECTOR)
                .centerOffset(CONTROLLER_X, CONTROLLER_Y, CONTROLLER_Z)
                .where('a', optionalAuxiliaryBlock(getFireBoxState()))
                .where('d', optionalAuxiliaryBlock(getCasingState()))
                .where('h', optionalAuxiliaryBlock(getBoilerState()))
                .where('l', optionalAuxiliaryAny())
                .where(' ', any())
                .end();

        builder.piece("aux_right", new String[][]{
                        {"             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             "},
                        {"             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             "},
                        {"         qrrr", "         meee", "         mmem", "         mmem", "         mmem", "         mmem", "             ", "             ", "             "},
                        {"         qrrr", "         rrme", "         meme", "         meme", "         meme", "         meme", "             ", "             ", "             "},
                        {"         qrrr", "         meee", "         mmem", "         mmem", "         mmem", "         mmem", "             ", "             ", "             "},
                        {"             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             "},
                        {"             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             "}
                }, Vec3i.NULL_VECTOR)
                .centerOffset(CONTROLLER_X, CONTROLLER_Y, CONTROLLER_Z)
                .where('q', optionalAuxiliaryBlock(getFireBoxState()))
                .where('e', optionalAuxiliaryBlock(getCasingState()))
                .where('r', optionalAuxiliaryBlock(getBoilerState()))
                .where('m', optionalAuxiliaryAny())
                .where(' ', any())
                .end();

        return builder.build();
    }

    private static IStructureElement coreCasingWithBuses() {
        int minCasings = Math.max(0, CORE_CASING_COUNT - OPTIONAL_ITEM_INPUTS - OPTIONAL_ITEM_OUTPUTS);
        return chain(
                new CasingElement(CasingDefinition.simple(getCasingState()), minCasings),
                new HatchElement(MultiblockAbility.IMPORT_ITEMS, 0, OPTIONAL_ITEM_INPUTS, 1),
                new HatchElement(MultiblockAbility.EXPORT_ITEMS, 0, OPTIONAL_ITEM_OUTPUTS, 1));
    }

    private static IStructureElement<MetaTileEntityIndustrialPrimitiveBlastFurnace> optionalAuxiliaryBlock(
            IBlockState expectedState) {
        return new IStructureElement<>() {
            @NotNull
            @Override
            public StructureIncrementalSupport getIncrementalSupport() {
                return StructureIncrementalSupport.OPAQUE;
            }

            @NotNull
            @Override
            public Set<StructureDependency> getDependencies() {
                return Collections.emptySet();
            }

            @Override
            public boolean hasExplicitIncrementalContract() {
                return true;
            }

            @Override
            public boolean check(@NotNull StructureEvaluationContext<MetaTileEntityIndustrialPrimitiveBlastFurnace> context) {
                if (context.getOperation().isBuild()) {
                    return expectedState.equals(context.getBlockState());
                }
                return context.getController() != null;
            }

            @Override
            public BlockInfo[] getCandidates() {
                return new BlockInfo[]{new BlockInfo(expectedState)};
            }
        };
    }

    private static IStructureElement<MetaTileEntityIndustrialPrimitiveBlastFurnace> optionalAuxiliaryAny() {
        return new IStructureElement<>() {
            @NotNull
            @Override
            public StructureIncrementalSupport getIncrementalSupport() {
                return StructureIncrementalSupport.OPAQUE;
            }

            @NotNull
            @Override
            public Set<StructureDependency> getDependencies() {
                return Collections.emptySet();
            }

            @Override
            public boolean hasExplicitIncrementalContract() {
                return true;
            }

            @Override
            public boolean check(@NotNull StructureEvaluationContext<MetaTileEntityIndustrialPrimitiveBlastFurnace> context) {
                return context.getController() != null;
            }

            @Override
            public BlockInfo[] getCandidates() {
                return new BlockInfo[0];
            }
        };
    }

    private boolean isAuxiliaryModuleInstalled(boolean left) {
        if (getWorld() == null) {
            return false;
        }
        AuxiliaryBlock[] blocks = left ? LEFT_AUXILIARY_BLOCKS : RIGHT_AUXILIARY_BLOCKS;
        for (AuxiliaryBlock block : blocks) {
            IBlockState actual = getWorld().getBlockState(getAuxiliaryBlockPos(block.x, block.y, block.z));
            if (!actual.equals(block.state)) {
                return false;
            }
        }
        return true;
    }

    private void refreshAuxiliaryBlastFurnaces() {
        int auxiliaryCount = 0;
        if (isAuxiliaryModuleInstalled(true)) {
            auxiliaryCount++;
        }
        if (isAuxiliaryModuleInstalled(false)) {
            auxiliaryCount++;
        }
        auxiliaryBlastFurnaces = auxiliaryCount;
    }

    private BlockPos getAuxiliaryBlockPos(int x, int y, int z) {
        BlockPos offset = RelativeDirection.setActualRelativeOffset(
                x - CONTROLLER_X, y - CONTROLLER_Y, z - CONTROLLER_Z,
                getFrontFacingForStructure(), getUpwardsFacing(), isFlipped(), STRUCTURE_DIRECTIONS);
        return getPos().add(offset);
    }

    private static final AuxiliaryBlock[] LEFT_AUXILIARY_BLOCKS = new AuxiliaryBlock[]{
            aux(0, 0, 2, getFireBoxState()), aux(1, 0, 2, getFireBoxState()), aux(2, 0, 2, getFireBoxState()),
            aux(3, 0, 2, getCasingState()), aux(0, 1, 2, getCasingState()), aux(1, 1, 2, getCasingState()),
            aux(2, 1, 2, getCasingState()), aux(1, 2, 2, getCasingState()), aux(1, 3, 2, getCasingState()),
            aux(1, 4, 2, getCasingState()), aux(1, 5, 2, getCasingState()),

            aux(0, 0, 3, getFireBoxState()), aux(1, 0, 3, getFireBoxState()), aux(2, 0, 3, getFireBoxState()),
            aux(3, 0, 3, getCasingState()), aux(0, 1, 3, getCasingState()), aux(2, 1, 3, getCasingState()),
            aux(3, 1, 3, getBoilerState()), aux(0, 2, 3, getCasingState()), aux(2, 2, 3, getCasingState()),
            aux(0, 3, 3, getCasingState()), aux(2, 3, 3, getCasingState()), aux(0, 4, 3, getCasingState()),
            aux(2, 4, 3, getCasingState()), aux(0, 5, 3, getCasingState()), aux(2, 5, 3, getCasingState()),

            aux(0, 0, 4, getFireBoxState()), aux(1, 0, 4, getFireBoxState()), aux(2, 0, 4, getFireBoxState()),
            aux(3, 0, 4, getCasingState()), aux(0, 1, 4, getCasingState()), aux(1, 1, 4, getCasingState()),
            aux(2, 1, 4, getCasingState()), aux(1, 2, 4, getCasingState()), aux(1, 3, 4, getCasingState()),
            aux(1, 4, 4, getCasingState()), aux(1, 5, 4, getCasingState())
    };

    private static final AuxiliaryBlock[] RIGHT_AUXILIARY_BLOCKS = new AuxiliaryBlock[]{
            aux(9, 0, 2, getFireBoxState()), aux(10, 0, 2, getBoilerState()), aux(11, 0, 2, getBoilerState()),
            aux(12, 0, 2, getBoilerState()), aux(10, 1, 2, getCasingState()), aux(11, 1, 2, getCasingState()),
            aux(12, 1, 2, getCasingState()), aux(11, 2, 2, getCasingState()), aux(11, 3, 2, getCasingState()),
            aux(11, 4, 2, getCasingState()), aux(11, 5, 2, getCasingState()),

            aux(9, 0, 3, getFireBoxState()), aux(10, 0, 3, getBoilerState()), aux(11, 0, 3, getBoilerState()),
            aux(12, 0, 3, getBoilerState()), aux(9, 1, 3, getBoilerState()), aux(10, 1, 3, getBoilerState()),
            aux(12, 1, 3, getCasingState()), aux(10, 2, 3, getCasingState()), aux(12, 2, 3, getCasingState()),
            aux(10, 3, 3, getCasingState()), aux(12, 3, 3, getCasingState()), aux(10, 4, 3, getCasingState()),
            aux(12, 4, 3, getCasingState()), aux(10, 5, 3, getCasingState()), aux(12, 5, 3, getCasingState()),

            aux(9, 0, 4, getFireBoxState()), aux(10, 0, 4, getBoilerState()), aux(11, 0, 4, getBoilerState()),
            aux(12, 0, 4, getBoilerState()), aux(10, 1, 4, getCasingState()), aux(11, 1, 4, getCasingState()),
            aux(12, 1, 4, getCasingState()), aux(11, 2, 4, getCasingState()), aux(11, 3, 4, getCasingState()),
            aux(11, 4, 4, getCasingState()), aux(11, 5, 4, getCasingState())
    };

    private static AuxiliaryBlock aux(int x, int y, int z, IBlockState state) {
        return new AuxiliaryBlock(x, y, z, state);
    }

    private static class AuxiliaryBlock {

        private final int x;
        private final int y;
        private final int z;
        private final IBlockState state;

        private AuxiliaryBlock(int x, int y, int z, IBlockState state) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.state = state;
        }
    }

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return DEFINITION;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("Temp", TEMP);
        return super.writeToNBT(data);
    }

    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        TEMP = data.getInteger("Temp");
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityIndustrialPrimitiveBlastFurnace(metaTileEntityId);
    }
    @Override
    protected void formStructure(@NotNull FormedStructureView formed) {
        super.formStructure(formed);
        refreshAuxiliaryBlastFurnaces();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        auxiliaryBlastFurnaces = 0;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return GTSteamTextures.PORCELAIN_TILES;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return Textures.PRIMITIVE_BLAST_FURNACE_OVERLAY;
    }

    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive())
                .addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.getMaxVoltage()))
                .addCustom(this::addCustomCapacity)
                .addParallelsLine(recipeMapWorkable.getParallelLimit())
                .addWorkingStatusLine()
                .addProgressLine(recipeMapWorkable.getProgress(), recipeMapWorkable.getMaxProgress())
                .addRecipeOutputLine(recipeMapWorkable);
    }

    public void addCustomCapacity(KeyManager keyManager, UISyncer syncer) {
        keyManager.add(KeyUtil.lang(TextFormatting.GRAY, "gtsteam.multiblock.ip.amount.1", syncer.syncInt(TEMP), MAX_TEMP));
        keyManager.add(KeyUtil.lang(TextFormatting.GRAY, "gtsteam.machine.industrial_primitive_blast_furnace.auxiliary_blast_furnace", syncer.syncInt(auxiliaryBlastFurnaces)));
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) {
        InformationHandler.topTooltips("前期批发钢材的最好选择", tooltip);
        super.addInformation(stack, player, tooltip, advanced);
        TooltipBuilder.create().addSpecialLogic().build(this, tooltip);
        tooltip.add(I18n.format("gtsteam.machine.industrial_primitive_blast_furnace.tooltip.1"));
        tooltip.add(I18n.format("gtsteam.machine.industrial_primitive_blast_furnace.tooltip.2"));
        tooltip.add(I18n.format("gtsteam.machine.industrial_primitive_blast_furnace.tooltip.3"));
    }

    @Override
    public List<StructureChannel> getSupportedChannels() {
        List<StructureChannel> channels = new ArrayList<>(super.getSupportedChannels());
        if (!channels.contains(GTStructureChannels.STRUCTURE_PIECE)) {
            channels.add(GTStructureChannels.STRUCTURE_PIECE);
        }
        return channels;
    }

    @Override
    public int[] getChannelRange(@NotNull StructureChannel channel) {
        if (channel == GTStructureChannels.STRUCTURE_PIECE) {
            return new int[]{0, multiPiecePattern != null ? multiPiecePattern.getToolingPieceCount() : 0};
        }
        return super.getChannelRange(channel);
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public void update() {
        super.update();
        if (TEMP > MIN_TEMP) {
            if (TEMP > 300) TEMP--;
            if (TEMP > 600) TEMP -= 2;
            if (TEMP > 900) TEMP = TEMP - 3;
            TEMP = Math.max(MIN_TEMP, TEMP);
        }

        if (!getWorld().isRemote && isStructureFormed() && getOffsetTimer() % 20 == 0) {
            refreshAuxiliaryBlastFurnaces();
        }

        if (this.isActive()) {
            if (getOffsetTimer() % 20 == 0 && getWorld().isRemote) {
                pollutionParticles();
            }
        }
    }

    private void pollutionParticles() {
        BlockPos pos = this.getPos();
        EnumFacing facing = this.getFrontFacing().getOpposite();
        float xPos = facing.getXOffset() * 2 + pos.getX() + 0.5F;
        float yPos = facing.getYOffset() * 2 + pos.getY() + 0.25F;
        float zPos = facing.getZOffset() * 2 + pos.getZ() + 0.5F;

        float ySpd = facing.getYOffset() * 0.7F + 0.7F + 0.8F * GTValues.RNG.nextFloat();

        arunMufflerEffect(xPos, yPos, zPos, 0, ySpd, 0);
        arunMufflerEffect(xPos, yPos, zPos, 0.1F, ySpd, 0.1F);
        arunMufflerEffect(xPos, yPos, zPos, -0.1F, ySpd, -0.1F);
        arunMufflerEffect(xPos, yPos, zPos, +0.1F, ySpd, -0.1F);
        arunMufflerEffect(xPos, yPos, zPos, -0.1F, ySpd, +0.1F);
    }

    public void arunMufflerEffect(float xPos, float yPos, float zPos, float xSpd, float ySpd, float zSpd) {
        this.getWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, xPos, yPos, zPos, xSpd, ySpd, zSpd);
    }

    @Override
    public boolean isBatchAllowed() {
        return false;
    }

    @Override
    public double getPollutionAmount() {
        return 0.0075;
    }

    @Override
    public GTGuiTheme getUITheme() {
        return GTGuiTheme.BRONZE;
    }

    protected class IndustrialPrimitiveBlastFurnaceLogic extends NoEnergyMultiblockRecipeLogic {

        public IndustrialPrimitiveBlastFurnaceLogic(NoEnergyMultiblockController tileEntity) {
            super(tileEntity);
        }

        @Override
        protected void modifyOverclockPost(@NotNull OCResult ocResult, @NotNull RecipePropertyStorage storage) {
            super.modifyOverclockPost(ocResult, storage);

            // 温度加速机制：每200K温差减少20%时间（乘算）
            if (TEMP > MIN_TEMP) {
                int tempDiff = TEMP - MIN_TEMP;
                int bonusSteps = tempDiff / 200;
                double multiplier = Math.pow(0.8, bonusSteps);
                int newDuration = (int) (ocResult.duration() * multiplier);
                ocResult.setDuration(Math.max(1, newDuration));
            }
        }

        @Override
        protected void updateRecipeProgress() {
            if (canRecipeProgress) {
                //持续工作温度增加
                if (getOffsetTimer() % 20 == 0) {
                    if (TEMP < MAX_TEMP) {
                        TEMP += auxiliaryBlastFurnaces + 1;
                        TEMP = Math.min(MAX_TEMP, TEMP);
                    }
                }
                if (++progressTime > maxProgressTime) {
                    completeRecipe();
                }
            }
        }
    }
}
