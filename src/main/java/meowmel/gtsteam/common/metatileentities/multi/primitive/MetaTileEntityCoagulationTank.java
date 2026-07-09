package meowmel.gtsteam.common.metatileentities.multi.primitive;

import static gregtech.api.pattern.element.Elements.*;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Matrix4;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.NoEnergyMultiblockController;
import gregtech.api.mui.GTGuiTheme;
import gregtech.api.pattern.*;
import gregtech.api.pattern.casing.CasingDefinition;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.casing.GTStructureChannels;
import gregtech.api.pattern.element.IStructureElement;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.util.GTUtility;
import gregtech.api.util.tooltips.InformationHandler;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.renderer.CubeRendererState;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.cclop.ColourOperation;
import gregtech.client.renderer.cclop.LightMapOperation;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.utils.BloomEffectUtil;
import meowmel.gtsteam.api.recipes.GTSRecipeMaps;
import meowmel.gtsteam.client.textures.GTSteamTextures;
import meowmel.gtsteam.common.block.GTSteamMetaBlocks;
import meowmel.gtsteam.common.block.blocks.BlockMultiblockCasing0;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static gregtech.api.util.RelativeDirection.*;

public class MetaTileEntityCoagulationTank extends NoEnergyMultiblockController {

    private static final IStructureElement SNOW_PREDICATE = blockPredicate(GTUtility::isBlockSnow);
    /** Piece name for the repeatable body section */
    private static final String PIECE_BODY = "body";
    private static final StructurePieceKey BODY_PIECE = StructurePieceKey.of(PIECE_BODY);
    private static final SoftReferenceHolder<? extends StructureDefinition<?>> STRUCTURE_DEFINITION =
            TemplatePool.getInstance().registerStructure("gtsteam:coagulation_tank", () ->
                    DeclarativePatternBuilder.start(RIGHT, UP, FRONT)
                            .piece("bottom")
                            .aisle("BBB", "XYX", "XXX")
                            .repeatablePiece(PIECE_BODY, 1, 8)
                            .aisle("BBB", "X&X", "X#X")
                            .withAisleChannel(GTStructureChannels.STRUCTURE_LENGTH.getName())
                            .piece("top")
                            .aisle("BBB", "XXX", "XXX")
                            .self('Y', MetaTileEntityCoagulationTank.class)
                            .where('B', blocks(getCasingBottomState()))
                            .casing('X', getCasingState())
                            .optionalItemInput(4)
                            .optionalItemOutput(4)
                            .optionalFluidInput(4)
                            .optionalFluidOutput(4)
                            .where('#', air())
                            .where('&', chain(air(), SNOW_PREDICATE))
                            .buildStructureDefinition()
            );
    int size;

    public MetaTileEntityCoagulationTank(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTSRecipeMaps.COAGULATION_RECIPES);
    }

    public static IBlockState getCasingState() {
        return GTSteamMetaBlocks.blockMultiblockCasing0.getState(BlockMultiblockCasing0.CasingType.REINFORCED_TREATED_WOOD_WALL);
    }

    protected static IBlockState getCasingBottomState() {
        return GTSteamMetaBlocks.blockMultiblockCasing0.getState(BlockMultiblockCasing0.CasingType.REINFORCED_TREATED_WOOD_BOTTOM);
    }

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION.get();
    }

    @Override
    public EnumFacing getFrontFacingForStructure() {
        return getFrontFacing().getOpposite();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityCoagulationTank(metaTileEntityId);
    }

    @Override
    protected void formStructure(@NotNull FormedStructureView formed) {
        formRecipeMapStructure(formed);
        this.size = formed.getPieceRepeat(BODY_PIECE, 0);
        this.recipeMapWorkable.setParallelLimit(size);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return GTSteamTextures.REINFORCED_TREATED_WOOD_WALL;
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) {
        InformationHandler.topTooltips("村里的大缸（远离司马光）", tooltip);
        super.addInformation(stack, player, tooltip, advanced);
        TooltipBuilder.create().addSpecialLogic().build(this, tooltip);
        tooltip.add(I18n.format("长度每拓展一格，并行+1"));
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                recipeMapWorkable.isActive(), recipeMapWorkable.isWorkingEnabled());
        if (recipeMapWorkable.isActive() && isStructureFormed()) {
            EnumFacing back = getFrontFacing().getOpposite();
            for (int i = 1; i <= size; i++) {
                Matrix4 offset = translation.copy().translate(back.getXOffset() * size, -0.3, back.getZOffset() * size);
                CubeRendererState op = Textures.RENDER_STATE.get();
                Textures.RENDER_STATE.set(new CubeRendererState(op.layer, CubeRendererState.PASS_MASK, op.world, op.pos));
                Textures.renderFace(renderState, offset,
                        ArrayUtils.addAll(pipeline, new LightMapOperation(240, 240), new ColourOperation(0xFFFFFFFF)),
                        EnumFacing.UP, Cuboid6.full, TextureUtils.getBlockTexture("water_still"),
                        BloomEffectUtil.getEffectiveBloomLayer());
                Textures.RENDER_STATE.set(op);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return Textures.PRIMITIVE_PUMP_OVERLAY;
    }

    @Override
    public GTGuiTheme getUITheme() {
        return GTGuiTheme.PRIMITIVE;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean isBatchAllowed() {
        return false;
    }
}
