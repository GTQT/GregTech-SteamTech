package keqing.gtsteam.loader.recipes.chain;

import net.minecraft.init.Blocks;

import static gregtech.api.recipes.RecipeMaps.COKE_OVEN_RECIPES;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.loaders.recipe.MachineRecipeLoader.PrimitiveBlastFurnaceBuilder;
import static keqing.gtsteam.common.item.GTSMetaitems.*;

public class CokeOvenChain {
    public static void init() {
        //仙人掌
        COKE_OVEN_RECIPES.recipeBuilder()
                .input(Blocks.CACTUS)
                .output(CACTUS_CHARCOAL)
                .fluidOutputs(Creosote.getFluid(30))
                .duration(500)
                .buildAndRegister();

        COKE_OVEN_RECIPES.recipeBuilder()
                .input(CACTUS_CHARCOAL)
                .output(CACTUS_COAL)
                .fluidOutputs(Creosote.getFluid(30))
                .duration(500)
                .buildAndRegister();

        PrimitiveBlastFurnaceBuilder(CACTUS_CHARCOAL.getStackForm(), Ash, 2.25);
        PrimitiveBlastFurnaceBuilder(CACTUS_COAL.getStackForm(), Ash, 1.25);
        //甘蔗
        COKE_OVEN_RECIPES.recipeBuilder()
                .input(Blocks.REEDS)
                .output(SUGAR_CHARCOAL)
                .fluidOutputs(Creosote.getFluid(30))
                .duration(600)
                .buildAndRegister();

        COKE_OVEN_RECIPES.recipeBuilder()
                .input(SUGAR_CHARCOAL)
                .output(SUGAR_COAL)
                .fluidOutputs(Creosote.getFluid(30))
                .duration(600)
                .buildAndRegister();

        PrimitiveBlastFurnaceBuilder(SUGAR_CHARCOAL.getStackForm(), Ash, 2.25);
        PrimitiveBlastFurnaceBuilder(SUGAR_COAL.getStackForm(), Ash, 1.25);
    }
}
