package meowmel.gtsteam.common.metatileentities.multi.advanced;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.NoEnergyMultiblockController;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.mui.GTGuiTheme;
import gregtech.api.pattern.FormedStructureView;
import gregtech.api.pattern.SoftReferenceHolder;
import gregtech.api.pattern.StructurePieceKey;
import gregtech.api.pattern.TemplatePool;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.casing.GTStructureChannels;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import gregtech.api.util.tooltips.InformationHandler;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static gregtech.api.pattern.element.Elements.*;
import static gregtech.api.recipes.RecipeMaps.PRIMITIVE_BLAST_FURNACE_RECIPES;
import static gregtech.api.util.RelativeDirection.*;
import static gregtech.common.blocks.MetaBlocks.METAL_CASING;

public class MetaTileEntityLeapForwardOneBlastFurnace extends NoEnergyMultiblockController {

    private static final int PARALLEL_LIMIT = 32768;
    /**
     * Piece name for the repeatable body section
     */
    private static final String PIECE_BODY = "body";
    private static final StructurePieceKey BODY_PIECE = StructurePieceKey.of(PIECE_BODY);

    private static final SoftReferenceHolder<? extends StructureDefinition<?>> STRUCTURE_DEFINITION =
            TemplatePool.getInstance().registerStructure("gtsteam:leap_forward_one_blast_furnace", () ->
                    DeclarativePatternBuilder.start(RIGHT, BACK, UP)
                            .piece("bottom")
                            .aisle("     AAAAA     ", "  DDDDDDDDDDD  ", " DDDDDDDDDDDDD ", " DDDDDDDDDDDDD ", " DDDDDDDDDDDDD ", "ADDDDDDDDDDDDDA", "ADDDDDDDDDDDDDA", "ADDDDDDDDDDDDDA", "ADDDDDDDDDDDDDA", "ADDDDDDDDDDDDDA", " DDDDDDDDDDDDD ", " DDDDDDDDDDDDD ", " DDDDDDDDDDDDD ", "  DDDDDDDDDDD  ", "     AAAAA     ")
                            .aisle("     AAGAA     ", "    DEEEEED    ", "   DEJJJJJED   ", "  DEJJJJJJJED  ", " DEJJJJJJJJJED ", "AEJJJJJJJJJJJEA", "AEJJJJJJJJJJJEA", "AEJJJJJJJJJJJEA", "AEJJJJJJJJJJJEA", "AEJJJJJJJJJJJEA", " DEJJJJJJJJJED ", "  DEJJJJJJJED  ", "   DEJJJJJED   ", "    DEEEEED    ", "     AAAAA     ")
                            .aisle("     BCCCB     ", "    DJJJJJD    ", "   DJJJJJJJD   ", "  DJJJJJJJJJD  ", " DJJJJJJJJJJJD ", "BJJJJJJJJJJJJJB", "CJJJJJJJJJJJJJC", "CJJJJJJJJJJJJJC", "CJJJJJJJJJJJJJC", "BJJJJJJJJJJJJJB", " DJJJJJJJJJJJD ", "  DJJJJJJJJJD  ", "   DJJJJJJJD   ", "    DJJJJJD    ", "     BCCCB     ")
                            .aisle("     BCCCB     ", "    DJJJJJD    ", "   DJJJJJJJD   ", "  DJJJJJJJJJD  ", " DJJJJJJJJJJJD ", "BJJJJJJJJJJJJJB", "CJJJJJJJJJJJJJC", "CJJJJJJJJJJJJJC", "CJJJJJJJJJJJJJC", "BJJJJJJJJJJJJJB", " DJJJJJJJJJJJD ", "  DJJJJJJJJJD  ", "   DJJJJJJJD   ", "    DJJJJJD    ", "     BCCCB     ")
                            .aisle("     DDDDD     ", "    DEEEEED    ", "   DEJJJJJED   ", "  DEJJJJJJJED  ", " DEJJJJJJJJJED ", "DEJJJJJJJJJJJED", "DEJJJJJJJJJJJED", "DEJJJJJJJJJJJED", "DEJJJJJJJJJJJED", "DEJJJJJJJJJJJED", " DEJJJJJJJJJED ", "  DEJJJJJJJED  ", "   DEJJJJJED   ", "    DEEEEED    ", "     DDDDD     ")
                            .aisle("               ", "     DDDDD     ", "    DDEEEDD    ", "   DEDFFFDED   ", "  DEEDFFFDEED  ", " DDDDDDDDDDDDD ", " DEFFDEJEDFFED ", " DEFFDJJJDFFED ", " DEFFDEJEDFFED ", " DDDDDDDDDDDDD ", "  DEEDFFFDEED  ", "   DEDFFFDED   ", "    DDEEEDD    ", "     DDDDD     ", "     DFFFD     ")
                            .aisle("               ", "       D       ", "      EDE      ", "    EEJJJEE    ", "   EEJJJJJEE   ", "   EJJJJJJJE   ", "  EJJJJEJJJJE  ", " DDJJJEJEJJJDD ", "  EJJJJEJJJJE  ", "   EJJJJJJJE   ", "   EEJJJJJEE   ", "    EEJJJEE    ", "      EDE      ", "       D       ", "      F F      ")
                            .repeatablePiece("body", 2, 32)
                            .aisle("               ", "               ", "      EDE      ", "     EJJJE     ", "    EJJJJJE    ", "   EJJJJJJJE   ", "  EJJJJEJJJJE  ", "  DJJJEJEJJJD  ", "  EJJJJEJJJJE  ", "   EJJJJJJJE   ", "    EJJJJJE    ", "     EJJJE     ", "      EDE      ", "               ", "      F H      ")
                            .withAisleChannel(GTStructureChannels.STRUCTURE_HEIGHT.getName())
                            .piece("top")
                            .aisle("               ", "               ", "      DDD      ", "     DEEED     ", "    DJJJJJD    ", "   DJJJJJJJD   ", "  DEJJJEJJJED  ", "  DEJJEJEJJED  ", "  DEJJJEJJJED  ", "   DJJJJJJJD   ", "    DJJJJJD    ", "     DEEED     ", "      DDD      ", "               ", "      F F      ")
                            .aisle("               ", "               ", "     FFFFF     ", "    FDEDEDF    ", "   FDEEJEEDF   ", "  FDEEJJJEEDF  ", "  FEEJJEJJEEF  ", "  FDJJEJEJJDF  ", "  FEEJJEJJEEF  ", "  FDEEJJJEEDF  ", "   FDEEJEEDF   ", "    FDEDEDF    ", "     FFFFF     ", "      FJF      ", "      FFF      ")
                            .aisle("               ", "               ", "               ", "      EDE      ", "     EEEEE     ", "    EEEEEEE    ", "   EEEEEEEEE   ", "   DEEEJEEED   ", "   EEEEEEEEE   ", "    EEEEEEE    ", "     EEEEE     ", "      EDE      ", "               ", "               ", "               ")
                            .aisle("               ", "               ", "               ", "      EEE      ", "     E   E     ", "    E     E    ", "   E       E   ", "   E       E   ", "   E       E   ", "    E     E    ", "     E   E     ", "      EEE      ", "               ", "               ", "               ")
                            .self('G', MetaTileEntityLeapForwardOneBlastFurnace.class)
                            .where('C', blocks(getFireBoxState()))
                            .where('J', blocks(getBoilerState()))
                            .where('F', blocks(getFrameState()))
                            .casing('A', getCasingState())
                            .optionalItemInput(4)
                            .optionalItemOutput(4)
                            .where('B', blocks(getSteelHull()))
                            .where('D', blocks(getStoneState()))
                            .where('E', blocks(getCasingState()))
                            .where('H', blocks(getFrameState()))
                            .where(' ', any())
                            .buildStructureDefinition()
            );

    int size;

    public MetaTileEntityLeapForwardOneBlastFurnace(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, PRIMITIVE_BLAST_FURNACE_RECIPES);
        this.recipeMapWorkable.setParallelLimit(PARALLEL_LIMIT);
    }

    private static IBlockState getStoneState() {
        return Blocks.STONEBRICK.getDefaultState();
    }

    private static IBlockState getSteelHull() {
        return METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
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
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.PRIMITIVE_BRICKS);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityLeapForwardOneBlastFurnace(metaTileEntityId);
    }

    @Override
    protected void formStructure(@NotNull FormedStructureView formed) {
        formRecipeMapStructure(formed);
        size = formed.getPieceRepeat(BODY_PIECE, 0);
    }

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION.get();
    }


    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.PRIMITIVE_BRICKS;
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
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

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean isBatchAllowed() {
        return false;
    }

    @Override
    public double getPollutionAmount() {
        return 0.01;
    }

    @Override
    public GTGuiTheme getUITheme() {
        return GTGuiTheme.PRIMITIVE;
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        InformationHandler.topTooltips("相当于整个河北省的炼钢量", tooltip);
        super.addInformation(stack, player, tooltip, advanced);
        TooltipBuilder.create().addParallel(PARALLEL_LIMIT).build(this, tooltip);
    }
}
