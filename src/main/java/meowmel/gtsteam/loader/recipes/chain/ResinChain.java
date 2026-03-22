package meowmel.gtsteam.loader.recipes.chain;

import gregtech.api.recipes.GTRecipeHandler;
import gregtech.api.unification.OreDictUnifier;

import static gregtech.api.GTValues.SECOND;
import static gregtech.api.GTValues.TICK;
import static gregtech.api.recipes.RecipeMaps.EXTRACTOR_RECIPES;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.dust;
import static gregtech.api.unification.ore.OrePrefix.stick;
import static meowmel.gtsteam.api.recipes.GTSRecipeMaps.COAGULATION_RECIPES;
import static meowmel.gtsteam.api.unification.GTSteamMaterials.Resin;

public class ResinChain {
    public static void init() {
        GTRecipeHandler.removeRecipesByInputs(EXTRACTOR_RECIPES,
                OreDictUnifier.get(dust, Resin));

        // Coagulate resin liquid to dust.
        COAGULATION_RECIPES.recipeBuilder()
                .circuitMeta(1)
                .notConsumable(stick, Iron)
                .fluidInputs(Resin.getFluid(1000))
                .output(dust, Resin)
                .duration(5 * SECOND)
                .buildAndRegister();

        COAGULATION_RECIPES.recipeBuilder()
                .notConsumable(stick, Iron)
                .notConsumable(dust, CalciumChloride)
                .fluidInputs(Resin.getFluid(1000))
                .output(dust, Resin)
                .duration(2 * SECOND + 5 * TICK)
                .buildAndRegister();

        COAGULATION_RECIPES.recipeBuilder()
                .notConsumable(stick, Iron)
                .notConsumable(SulfuricAcid.getFluid(1))
                .fluidInputs(Resin.getFluid(1000))
                .output(dust, Resin)
                .duration(1 * SECOND)
                .buildAndRegister();

        COAGULATION_RECIPES.recipeBuilder()
                .notConsumable(stick, Iron)
                .notConsumable(AceticAcid.getFluid(1))
                .fluidInputs(Resin.getFluid(1000))
                .output(dust, Resin)
                .duration(5 * TICK)
                .buildAndRegister();
    }
}
