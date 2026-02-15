package meowmel.gtsteam.loader.recipes.chain;

import gregtech.api.unification.ore.OrePrefix;

import static gregtech.api.GTValues.M;
import static gregtech.api.recipes.RecipeMaps.ALLOY_SMELTER_RECIPES;
import static gregtech.api.recipes.RecipeMaps.CHEMICAL_BATH_RECIPES;
import static gregtech.api.unification.material.Materials.Steel;
import static gregtech.api.unification.material.Materials.Zinc;
import static gregtech.api.unification.ore.OrePrefix.*;
import static meowmel.gtsteam.api.unification.GTSteamMaterials.GalvanizedSteel;

public class GalvanizedSteelChain {
    public static void init() {
        registerGalvanizedSteel();
    }

    private static void registerGalvanizedSteel() {
        OrePrefix[] galvanizedSteelPrefix = new OrePrefix[]{ingot, plate, stick, stickLong, bolt, screw, ring, gear, gearSmall, rotor, round};
        int tinyQuantity;
        for (OrePrefix prefix : galvanizedSteelPrefix) {
            tinyQuantity = (int) ((prefix.getMaterialAmount(Steel)) / M) + 1;
            ALLOY_SMELTER_RECIPES.recipeBuilder()
                    .duration(tinyQuantity * 320)
                    .EUt(8)
                    .input(prefix, Steel, 1)
                    .input(dustTiny, Zinc, tinyQuantity)
                    .output(prefix, GalvanizedSteel)
                    .buildAndRegister();

            CHEMICAL_BATH_RECIPES.recipeBuilder()
                    .duration(tinyQuantity * 80)
                    .EUt(32)
                    .input(prefix, Steel)
                    .fluidInputs(Zinc.getFluid(16))
                    .output(prefix, GalvanizedSteel)
                    .buildAndRegister();
        }
    }
}
