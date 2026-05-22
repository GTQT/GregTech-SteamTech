package meowmel.gtsteam.common.metatileentities.multi.heat;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.HeatMultiblockController;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPatternTemplate;
import gregtech.api.pattern.SoftTemplate;
import gregtech.api.pattern.TemplatePool;
import gregtech.api.pattern.casing.CasingDefinition;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMachineCasing.MachineCasingType;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class MetaTileEntityHeatBrewingVat extends HeatMultiblockController {

    private static final int PARALLEL_LIMIT = 16;
    private static final SoftTemplate TEMPLATE = TemplatePool.getInstance().register("gtsteam:heat_brewing_vat", () ->
            DeclarativePatternBuilder.start()
                    .aisle("     ", "     ", " XXX ", " XXX ", " XXX ", "     ")
                    .aisle(" F F ", " XXX ", "X###X", "X###X", "X###X", " XXX ")
                    .aisle("     ", " XXX ", "X###X", "X###X", "X###X", " XXX ")
                    .aisle(" F F ", " XXX ", "X###X", "X###X", "X###X", " XXX ")
                    .aisle("     ", "     ", " XXX ", " XSX ", " XXX ", "     ")
                    .where('S', selfPredicate(MetaTileEntityHeatBrewingVat.class))
                    .casing('X', CasingDefinition.simple(getCasingState()))
                    .optionalItemInput(2)
                    .optionalItemOutput(2)
                    .optionalFluidInput(2)
                    .optionalFluidOutput(2)
                    .hatch(MultiblockAbility.INPUT_HEAT, 1)
                    .where('F', frames(Materials.Steel))
                    .where(' ', any())
                    .where('#', air())
                    .buildTemplate()
    );

    public MetaTileEntityHeatBrewingVat(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.BREWING_RECIPES);
        recipeMapWorkable.setParallelLimit(PARALLEL_LIMIT);
    }

    private static @NotNull IBlockState getCasingState() {
        return MetaBlocks.MACHINE_CASING.getState(MachineCasingType.ULV);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityHeatBrewingVat(metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPatternTemplate createStructureTemplate() {
        return TEMPLATE.get();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.VOLTAGE_CASINGS[0];
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.PYROLYSE_OVEN_OVERLAY;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        TooltipBuilder.create().addHeatMachine(PARALLEL_LIMIT).build(this, tooltip);
    }
}