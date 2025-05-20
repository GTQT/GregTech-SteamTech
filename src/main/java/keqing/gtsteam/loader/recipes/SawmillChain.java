package keqing.gtsteam.loader.recipes;

import net.minecraft.init.Items;

import static gregtech.api.unification.material.Materials.Wood;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.api.unification.ore.OrePrefix.plate;
import static keqing.gtsteam.api.recipes.GTSRecipeMaps.SAW_MILL;

public class SawmillChain {
    public static void init() {
        SAW_MILL.recipeBuilder()
                .input(log, Wood)
                .output(plank, Wood,4)
                .duration(200).EUt(8).buildAndRegister();

        SAW_MILL.recipeBuilder()
                .input(plank, Wood)
                .output(plate,Wood,1)
                .duration(100).EUt(8).buildAndRegister();

        SAW_MILL.recipeBuilder()
                .input(plate,Wood,1)
                .output(Items.STICK, 2)
                .duration(100).EUt(8).buildAndRegister();
    }
}
