package meowmel.gtsteam.loader.recipes.chain;

import static gregtech.api.recipes.RecipeMaps.COKE_OVEN_RECIPES;
import static gregtech.api.recipes.RecipeMaps.PYROLYSE_RECIPES;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.block;
import static gregtech.api.unification.ore.OrePrefix.gem;
import static gregtech.loaders.recipe.MachineRecipeLoader.PrimitiveBlastFurnaceBuilder;
import static meowmel.gtsteam.api.unification.GTSteamMaterials.Lignite;

public class LigniteChain {

    public static void init() {
        PrimitiveBlastFurnaceBuilder(Lignite, Ash, 2.5);

        COKE_OVEN_RECIPES.recipeBuilder().input(gem, Lignite).output(gem, Coke).fluidOutputs(Creosote.getFluid(400))
                .duration(900).buildAndRegister();
        COKE_OVEN_RECIPES.recipeBuilder().input(block, Lignite).output(block, Coke).fluidOutputs(Creosote.getFluid(3600))
                .duration(8100).buildAndRegister();

        // Creosote
        PYROLYSE_RECIPES.recipeBuilder().circuitMeta(1)
                .input(gem, Lignite, 16)
                .output(gem, Coke, 16)
                .fluidOutputs(Creosote.getFluid(6400))
                .duration(640).EUt(64)
                .buildAndRegister();

        PYROLYSE_RECIPES.recipeBuilder().circuitMeta(2)
                .input(gem, Lignite, 16)
                .fluidInputs(Nitrogen.getFluid(1000))
                .output(gem, Coke, 16)
                .fluidOutputs(Creosote.getFluid(6400))
                .duration(320).EUt(96)
                .buildAndRegister();

        PYROLYSE_RECIPES.recipeBuilder().circuitMeta(1)
                .input(block, Lignite, 8)
                .output(block, Coke, 8)
                .fluidOutputs(Creosote.getFluid(25600))
                .duration(2560).EUt(64)
                .buildAndRegister();

        // From Coal
        PYROLYSE_RECIPES.recipeBuilder().circuitMeta(22)
                .input(gem, Lignite, 16)
                .fluidInputs(Steam.getFluid(1000))
                .output(gem, Coke, 16)
                .fluidOutputs(CoalGas.getFluid(4000))
                .duration(320).EUt(96)
                .buildAndRegister();

        PYROLYSE_RECIPES.recipeBuilder().circuitMeta(22)
                .input(block, Lignite, 8)
                .fluidInputs(Steam.getFluid(4000))
                .output(block, Coke, 8)
                .fluidOutputs(CoalGas.getFluid(16000))
                .duration(1280).EUt(96)
                .buildAndRegister();
    }
}
