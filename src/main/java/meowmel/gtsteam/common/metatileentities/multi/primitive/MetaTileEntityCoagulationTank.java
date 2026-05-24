package meowmel.gtsteam.common.metatileentities.multi.primitive;

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

    private static final TraceabilityPredicate SNOW_PREDICATE = new TraceabilityPredicate(
            bws -> GTUtility.isBlockSnow(bws.getBlockState()));
    private static final SoftTemplate TEMPLATE = TemplatePool.getInstance().register("gtsteam:coagulation_tank", () ->
            DeclarativePatternBuilder.start(RIGHT, UP, FRONT)
                    .aisle("BBB", "XYX", "XXX")
                    .aisleRepeatable(1, 8, "BBB", "X&X", "X#X")
                    .withAisleChannel(GTStructureChannels.STRUCTURE_LENGTH.getName())
                    .aisle("BBB", "XXX", "XXX")
                    .where('Y', selfPredicate(MetaTileEntityCoagulationTank.class))
                    .where('B', states(getCasingBottomState()))
                    .casing('X', CasingDefinition.simple(getCasingState()))
                    .optionalItemInput(4)
                    .optionalItemOutput(4)
                    .optionalFluidInput(4)
                    .optionalFluidOutput(4)
                    .where('#', air())
                    .where('&', air().or(SNOW_PREDICATE)) // this won't stay in the structure, and will be broken while
                    .buildTemplate()
    );
    int size;

    public MetaTileEntityCoagulationTank(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTSRecipeMaps.COAGULATION_RECIPES);
    }

    protected static IBlockState getCasingState() {
        return GTSteamMetaBlocks.blockMultiblockCasing0.getState(BlockMultiblockCasing0.CasingType.REINFORCED_TREATED_WOOD_WALL);
    }

    protected static IBlockState getCasingBottomState() {
        return GTSteamMetaBlocks.blockMultiblockCasing0.getState(BlockMultiblockCasing0.CasingType.REINFORCED_TREATED_WOOD_BOTTOM);
    }

    @Override
    protected @NotNull BlockPatternTemplate createStructureTemplate() {
        return TEMPLATE.get();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityCoagulationTank(metaTileEntityId);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        if (multiblockState != null) {
            size = multiblockState.formedRepetitionCount[1] + 1;
        } else size = 1;
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
                Textures.RENDER_STATE.set(new CubeRendererState(op.layer, CubeRendererState.PASS_MASK, op.world));
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