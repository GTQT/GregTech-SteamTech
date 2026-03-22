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
import static meowmel.gtsteam.api.recipes.GTSRecipeMaps.HEAT_CHEMICAL_RECIPES;
import static meowmel.gtsteam.api.unification.GTSteamMaterials.Resin;

public class ResinChain {
    public static void init() {
        GTRecipeHandler.removeRecipesByInputs(EXTRACTOR_RECIPES,
                OreDictUnifier.get(dust, Resin));

        // Coagulate resin liquid to dust.
        COAGULATION_RECIPES.recipeBuilder()
                .notConsumable(stick, Iron)
                .notConsumable(SulfuricAcid.getFluid(200))
                .fluidInputs(Resin.getFluid(1000))
                .output(dust, Resin)
                .duration(5 * SECOND)
                .buildAndRegister();

        COAGULATION_RECIPES.recipeBuilder()
                .notConsumable(stick, Iron)
                .notConsumable(AceticAcid.getFluid(200))
                .fluidInputs(Resin.getFluid(1000))
                .output(dust, Resin)
                .duration(SECOND)
                .buildAndRegister();

        HEAT_CHEMICAL_RECIPES.recipeBuilder()
                .notConsumable(SulfuricAcid.getFluid(200))
                .fluidInputs(Resin.getFluid(1000))
                .output(dust, Resin)
                .duration(SECOND)
                .Temperature(673)
                .Heat(30)
                .buildAndRegister();

        HEAT_CHEMICAL_RECIPES.recipeBuilder()
                .notConsumable(AceticAcid.getFluid(200))
                .fluidInputs(Resin.getFluid(1000))
                .output(dust, Resin)
                .duration(5 * TICK)
                .Temperature(673)
                .Heat(30)
                .buildAndRegister();
    }
}
