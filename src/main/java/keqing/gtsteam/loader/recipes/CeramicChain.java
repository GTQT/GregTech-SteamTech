package keqing.gtsteam.loader.recipes;

import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.MetaBlocks;
import keqing.gtsteam.common.block.GTSteamMetaBlocks;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.common.blocks.BlockMetalCasing.MetalCasingType.PRIMITIVE_BRICKS;
import static gregtech.common.items.MetaItems.WOODEN_FORM_BRICK;
import static keqing.gtsteam.api.unification.GTSteamMaterials.BoneCeramicClay;
import static keqing.gtsteam.common.block.blocks.BlockMultiblockCasing0.CasingType.GALVANIZED_PORCELAIN_TILES;
import static keqing.gtsteam.common.item.GTSMetaitems.*;

public class CeramicChain {
    public static void init() {
        RecipeMaps.MIXER_RECIPES.recipeBuilder()
                .input(dust, Stone, 2)
                .input(dust, Clay, 2)
                .input(dust, Bone, 1)
                .output(dust, BoneCeramicClay, 5)
                .duration(100).EUt(8).buildAndRegister();

        ModHandler.addShapedRecipe("unburned_ceramic_tiles", UNBURNED_CERAMIC_TILES.getStackForm(3),
                "XXX", "XYX", "XXX",
                'Y', WOODEN_FORM_BRICK.getStackForm(),
                'X', new UnificationEntry(OrePrefix.dust, BoneCeramicClay));

        RecipeMaps.ALLOY_SMELTER_RECIPES.recipeBuilder()
                .input(UNBURNED_CERAMIC_TILES)
                .input(dustTiny, QuartzSand, 1)
                .output(BURNED_CERAMIC_TILES)
                .duration(100).EUt(8).buildAndRegister();

        RecipeMaps.ALLOY_SMELTER_RECIPES.recipeBuilder()
                .input(BURNED_CERAMIC_TILES)
                .input(dustTiny, Zinc, 1)
                .output(GALVANIZED_CERAMIC_TILE)
                .duration(100).EUt(8).buildAndRegister();

        ModHandler.addShapedRecipe("galvanized_porcelain_tiles", GTSteamMetaBlocks.blockMultiblockCasing0.getItemVariant(GALVANIZED_PORCELAIN_TILES, 2),
                "SXS", "XYX", "SXS",
                'Y', MetaBlocks.METAL_CASING.getItemVariant(PRIMITIVE_BRICKS, 1),
                'S', new UnificationEntry(screw, Iron),
                'X',GALVANIZED_CERAMIC_TILE);
    }
}
