package keqing.gtsteam.common.metatileentities.multi.steam;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.ParallelLogicType;
import gregtech.api.metatileentity.multiblock.RecipeMapSteamMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockFireboxCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import static gregtech.client.renderer.texture.Textures.BRONZE_PLATED_BRICKS;
import static gregtech.client.renderer.texture.Textures.SOLID_STEEL_CASING;
import static gregtech.common.blocks.BlockBoilerCasing.BoilerCasingType.BRONZE_PIPE;
import static gregtech.common.blocks.BlockBoilerCasing.BoilerCasingType.STEEL_PIPE;

public class MetaTileEntitySteamBender extends RecipeMapSteamMultiblockController {

    private static final int PARALLEL_LIMIT = 4;

    public MetaTileEntitySteamBender(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.BENDER_RECIPES, CONVERSION_RATE, ParallelLogicType.MULTIPLY);
        this.recipeMapWorkable.setParallelLimit(PARALLEL_LIMIT);
    }

    private static IBlockState getFrameState() {
        return ConfigHolder.machines.steelSteamMultiblocks ?
                MetaBlocks.FRAMES.get(Materials.Steel).getBlock(Materials.Steel) :
                MetaBlocks.FRAMES.get(Materials.Bronze).getBlock(Materials.Bronze);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntitySteamBender(metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("TTTFFFF", "XXXXXXX", "XXXXXXX")
                .aisle("TXT    ", "XPPPPPX", "XXXXXXX")
                .aisle("TTTFFFF", "XSXXXXX", "XXXXXXX")
                .where('S', selfPredicate())
                .where('X', states(getCasingState()).setMinGlobalLimited(20).or(autoAbilities()))
                .where('P', states(getBoilerState()))
                .where('F', states(getFrameState()))
                .where('T', states(getFireboxState()))
                .where(' ', any())
                .build();
    }

    private IBlockState getBoilerState() {
        return ConfigHolder.machines.steelSteamMultiblocks ?
                MetaBlocks.BOILER_CASING.getState(STEEL_PIPE) :
                MetaBlocks.BOILER_CASING.getState(BRONZE_PIPE);
    }

    public IBlockState getCasingState() {
        return ConfigHolder.machines.steelSteamMultiblocks ?
                MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID) :
                MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.BRONZE_BRICKS);
    }

    public IBlockState getFireboxState() {
        return ConfigHolder.machines.steelSteamMultiblocks ?
                MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.STEEL_FIREBOX) :
                MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.BRONZE_FIREBOX);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return ConfigHolder.machines.steelSteamMultiblocks ? SOLID_STEEL_CASING : BRONZE_PLATED_BRICKS;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.BENDER_OVERLAY;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }


    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        TooltipBuilder.create().addSteamMachine(PARALLEL_LIMIT).build(this, tooltip);
    }
}