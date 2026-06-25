package meowmel.gtsteam.common.metatileentities.multi.steam;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.ParallelLogicType;
import gregtech.api.metatileentity.multiblock.RecipeMapSteamMultiblockController;
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
import gregtech.common.blocks.BlockFireboxCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static gregtech.client.renderer.texture.Textures.BRONZE_PLATED_BRICKS;
import static gregtech.common.blocks.BlockBoilerCasing.BoilerCasingType.BRONZE_PIPE;

public class MetaTileEntitySteamLathe extends RecipeMapSteamMultiblockController {

    private static final int PARALLEL_LIMIT = 4;
    private static final SoftTemplate TEMPLATE = TemplatePool.getInstance().register("gtsteam:steam_lathe", () ->
            DeclarativePatternBuilder.start()
                    .aisle("FFFTTTFFF", "XXXXXXXXX", "FFFTTTFFF")
                    .aisle("XXXXXXXXX", "XPPPPPPPX", "XXXXXXXXX")
                    .aisle("FFFTTTFFF", "XXXXSXXXX", "FFFTTTFFF")
                    .self('S', MetaTileEntitySteamLathe.class)
                    .casing('X', CasingDefinition.simple(getCasingState()))
                    .hatch(MultiblockAbility.STEAM_IMPORT_ITEMS, 1, 2)
                    .hatch(MultiblockAbility.STEAM_EXPORT_ITEMS, 1, 2)
                    .hatch(MultiblockAbility.STEAM, 1)
                    .where('P', states(getBoilerState()))
                    .where('F', states(getFrameState()))
                    .where('T', states(getFireboxState()))
                    .where(' ', any())
                    .buildTemplate()
    );

    public MetaTileEntitySteamLathe(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.LATHE_RECIPES, CONVERSION_RATE, ParallelLogicType.MULTIPLY);
        this.recipeMapWorkable.setParallelLimit(PARALLEL_LIMIT);
    }

    private static IBlockState getFrameState() {
        return MetaBlocks.FRAMES.get(Materials.Bronze).getBlock(Materials.Bronze);
    }

    public static IBlockState getFireboxState() {
        return MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.BRONZE_FIREBOX);
    }

    private static IBlockState getBoilerState() {
        return MetaBlocks.BOILER_CASING.getState(BRONZE_PIPE);
    }

    public static IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.BRONZE_BRICKS);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntitySteamLathe(metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPatternTemplate createStructureTemplate() {
        return TEMPLATE.get();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return BRONZE_PLATED_BRICKS;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return Textures.ELECTRIC_FURNACE_OVERLAY;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }


    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        TooltipBuilder.create().addSteamMachine(PARALLEL_LIMIT).build(this, tooltip);
    }
}