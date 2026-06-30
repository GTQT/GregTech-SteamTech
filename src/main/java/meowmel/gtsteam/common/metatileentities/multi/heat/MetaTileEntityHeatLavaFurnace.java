package meowmel.gtsteam.common.metatileentities.multi.heat;

import gregtech.api.pattern.element.StructureDefinition;

import static gregtech.api.pattern.element.Elements.*;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.HeatMultiblockController;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.TemplatePool;
import gregtech.api.pattern.casing.CasingDefinition;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockFireboxCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import meowmel.gtsteam.api.recipes.GTSRecipeMaps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MetaTileEntityHeatLavaFurnace extends HeatMultiblockController {

    private static final int PARALLEL_LIMIT = 4;
    private static final StructureDefinition<?> DEFINITION = StructureDefinition.getOrBuild("gtsteam:heat_lava_furnace", () ->
            DeclarativePatternBuilder.start()
                    .aisle("XXX", "CCC", "CCC")
                    .aisle("XCX", "C#C", "CCC")
                    .aisle("XXX", "CSC", "CCC")
                    .self('S', MetaTileEntityHeatLavaFurnace.class)
                    .where('X', blocks(getFireboxState()))
                    .casing('C', getCasingState())
                    .itemInput(1, 2)
                    .fluidOutput(1, 2)
                    .hatch(MultiblockAbility.INPUT_HEAT, 1)
                    .where('#', any())
                    .buildStructureDefinition()
    );

    public MetaTileEntityHeatLavaFurnace(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTSRecipeMaps.LAVA_FURNACE_RECIPES);
        recipeMapWorkable.setParallelLimit(PARALLEL_LIMIT);
    }

    public static IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    public static IBlockState getFireboxState() {
        return MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.STEEL_FIREBOX);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityHeatLavaFurnace(metaTileEntityId);
    }

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return DEFINITION;
    }

    private boolean isFireboxPart(IMultiblockPart sourcePart) {
        return isStructureFormed() && (((MetaTileEntity) sourcePart).getPos().getY() < getPos().getY());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        if (sourcePart != null && isFireboxPart(sourcePart)) {
            return lastActive ? Textures.STEEL_FIREBOX_ACTIVE : Textures.STEEL_FIREBOX;
        }
        return Textures.SOLID_STEEL_CASING;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return Textures.ELECTRIC_FURNACE_OVERLAY;
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        TooltipBuilder.create().addHeatMachine(PARALLEL_LIMIT).build(this, tooltip);
    }

}
