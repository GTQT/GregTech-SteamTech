package meowmel.gtsteam.common.metatileentities.multi.primitive;

import gregtech.api.pattern.element.StructureDefinition;

import static gregtech.api.pattern.element.Elements.*;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.NoEnergyMultiblockController;
import gregtech.api.pattern.TemplatePool;
import gregtech.api.pattern.casing.CasingDefinition;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.util.tooltips.InformationHandler;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.MetaBlocks;
import meowmel.gtsteam.client.textures.GTSteamTextures;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

import static gregtech.common.blocks.BlockWireCoil.CoilType.CUPRONICKEL;

public class MetaTileEntitySepticTank extends NoEnergyMultiblockController {

    private static final StructureDefinition<?> DEFINITION = StructureDefinition.getOrBuild("gtsteam:septic_tank", () ->
            DeclarativePatternBuilder.start()
                    .aisle("CCCCC", "CFFFC", "CFFFC", "CCCCC")
                    .aisle("CCCCC", "F###F", "F###F", "CCCCC")
                    .aisle("CCCCC", "F###F", "F###F", "CCCCC")
                    .aisle("CCCCC", "F###F", "F###F", "CCCCC")
                    .aisle("CCSCC", "CFFFC", "CFFFC", "CCCCC")
                    .self('S', MetaTileEntitySepticTank.class)
                    .casing('C', getCasingState())
                    .itemInput(1, 3)
                    .itemOutput(1, 3)
                    .fluidInput(1, 3)
                    .fluidOutput(1, 3)
                    .where('F', blocks(getCoilState()))
                    .where('#', any())
                    .buildStructureDefinition()
    );

    public MetaTileEntitySepticTank(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.FERMENTING_RECIPES);
    }

    private static IBlockState getCoilState() {
        return MetaBlocks.WIRE_COIL.getState(CUPRONICKEL);
    }

    public static IBlockState getCasingState() {
        return Blocks.BRICK_BLOCK.getDefaultState();
    }

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return DEFINITION;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return GTSteamTextures.BRICK_CASING;
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.PYROLYSE_OVEN_OVERLAY;
    }


    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntitySepticTank(this.metaTileEntityId);
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) {
        InformationHandler.topTooltips("神奇的微生物在哪里", tooltip);
        super.addInformation(stack, player, tooltip, advanced);
    }

    @Override
    public double getPollutionAmount() {
        return 0.0025;
    }

    @Override
    public boolean isBatchAllowed() {
        return false;
    }

}
