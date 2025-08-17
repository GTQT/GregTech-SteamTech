package keqing.gtsteam.common.metatileentities.multi.steam;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.ParallelLogicType;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.metatileentity.multiblock.RecipeMapSteamMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.recipes.Recipe;
import gregtech.api.unification.material.Materials;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockFireboxCasing;
import gregtech.common.blocks.BlockMachineCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import keqing.gtsteam.api.recipes.GTSRecipeMaps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static gregtech.client.renderer.texture.Textures.BRONZE_PLATED_BRICKS;
import static gregtech.client.renderer.texture.Textures.SOLID_STEEL_CASING;
import static gregtech.common.blocks.BlockBoilerCasing.BoilerCasingType.BRONZE_PIPE;
import static gregtech.common.blocks.BlockBoilerCasing.BoilerCasingType.STEEL_PIPE;

public class MetaTileEntitySteamOreWasher extends RecipeMapMultiblockController {

    public MetaTileEntitySteamOreWasher(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTSRecipeMaps.STEAM_ORE_WASHER_RECIPES);
        this.recipeMapWorkable = new OreWasherWorkableHandler(this);
    }

    private static IBlockState getFrameState() {
        return MetaBlocks.FRAMES.get(Materials.Steel).getBlock(Materials.Steel);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntitySteamOreWasher(metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("MMMMM", "MMMMM", "MMMMM")
                .aisle("MMMMM", "MFFFM", "M###M")
                .aisle("MMMMM", "MFFFM", "M###M")
                .aisle("MMMMM", "MFFFM", "M###M")
                .aisle("MMMMM", "MMCMM", "MMMMM")
                .where('C', selfPredicate())
                .where('M', states(getCasingState()).setMinGlobalLimited(40).or(autoAbilities()))
                .where('F', states(getFrameState()))
                .where('#', air())
                .build();
    }

    public IBlockState getCasingState() {
        return MetaBlocks.MACHINE_CASING.getState(BlockMachineCasing.MachineCasingType.ULV);
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

    public boolean hasMaintenanceMechanics() {
        return false;
    }

    public boolean hasMufflerMechanics() {
        return true;
    }

    protected static class OreWasherWorkableHandler extends MultiblockRecipeLogic {

        public OreWasherWorkableHandler(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }

        public boolean checkRecipe(Recipe recipe) {
            return true;
        }

        public long getMaxVoltage() {
            return 30;
        }

        @Override
        public int getParallelLimit() {
            return 8;
        }

        @Override
        public void setMaxProgress(int maxProgress) {
            this.maxProgressTime = maxProgress * 4;
        }
    }
}