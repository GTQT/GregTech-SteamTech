package keqing.gtsteam.common.metatileentities.multi.primitive;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.unification.material.Materials;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockSteamCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.blocks.wood.BlockGregPlanks;
import keqing.gtsteam.api.metatileentity.multiblock.RecipeMapNoEnergyMultiblockController;
import keqing.gtsteam.api.recipes.GTSRecipeMaps;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class MetaTileEntitySawMill extends RecipeMapNoEnergyMultiblockController {

    public MetaTileEntitySawMill(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTSRecipeMaps.SAW_MILL);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntitySawMill(metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("PPPPP", "    F", "    F")
                .aisle("PXXXP", "XX XF", "FFFFF")
                .aisle("PXXXP", "XX XF", " F  F")
                .aisle("PXXXP", "XX XF", "FFFFF")
                .aisle("PSPPP", "    F", "    F")
                .where('S', selfPredicate())
                .where('P', states(MetaBlocks.STEAM_CASING.getState(BlockSteamCasing.SteamCasingType.WOOD_WALL)))
                .where('F', states(MetaBlocks.FRAMES.get(Materials.TreatedWood).getBlock(Materials.TreatedWood)))
                .where('X', states(MetaBlocks.PLANKS.getState(BlockGregPlanks.BlockType.TREATED_PLANK))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setPreviewCount(1).setMaxGlobalLimited(3))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS).setPreviewCount(1).setMaxGlobalLimited(3))
                )
                .where(' ', any())
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.WOOD_WALL;
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.BLOWER_OVERLAY;
    }

    public boolean hasMaintenanceMechanics() {
        return false;
    }

    public boolean hasMufflerMechanics() {
        return false;
    }
}