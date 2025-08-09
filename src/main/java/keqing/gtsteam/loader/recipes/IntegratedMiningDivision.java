package keqing.gtsteam.loader.recipes;


import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.recipes.builders.FuelRecipeBuilder;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.api.recipes.ingredients.GTRecipeOreInput;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.OreProperty;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.unification.material.registry.MaterialRegistry;
import gregtech.api.unification.ore.OrePrefix;
import keqing.gtsteam.api.recipes.GTSRecipeMaps;

import static gregtech.api.GTValues.ZPM;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.ore;


public class IntegratedMiningDivision {
    public static void init() {
        addStaticRecipes();
    }

    private static void addStaticRecipes() {

        for (MaterialRegistry materialRegistry : GregTechAPI.materialManager.getRegistries())
        {
            for (Material material : materialRegistry) {
                if (material.hasProperty(PropertyKey.ORE)) {
                    addIntegratedMiningRecipe10(material, 1)
                            .EUt(16).duration(160)
                            .buildAndRegister();
                    addIntegratedMiningRecipe11(material, 1)
                            .EUt(16).duration(160)
                            .buildAndRegister();
                    addIntegratedMiningRecipe12(material, 1)
                            .EUt(16).duration(160)
                            .buildAndRegister();
                }
            }
        }
    }

    private static SimpleRecipeBuilder addIntegratedMiningRecipe10(Material material, int output) {
        return GTSRecipeMaps.STEAM_ORE_WASHER_RECIPES.recipeBuilder()
                .input(OrePrefix.crushed, material)
                .output(OrePrefix.crushedPurified, material, output);
    }
    private static SimpleRecipeBuilder addIntegratedMiningRecipe11(Material material, int output) {
        return GTSRecipeMaps.STEAM_ORE_WASHER_RECIPES.recipeBuilder()
                .input(OrePrefix.dustPure, material)
                .output(OrePrefix.dust, material, output);
    }
    private static SimpleRecipeBuilder addIntegratedMiningRecipe12(Material material, int output) {
        return GTSRecipeMaps.STEAM_ORE_WASHER_RECIPES.recipeBuilder()
                .input(OrePrefix.dustImpure, material)
                .output(OrePrefix.dust, material, output);
    }
}