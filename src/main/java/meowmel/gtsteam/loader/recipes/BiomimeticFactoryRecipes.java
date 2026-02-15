package meowmel.gtsteam.loader.recipes;

import meowmel.gtsteam.api.recipes.GTSRecipeMaps;
import net.minecraft.init.Blocks;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.ore;

public class BiomimeticFactoryRecipes {
    public static void init() {
        GTSRecipeMaps.BIOMIMETIC_FACTORY_RECIPES.recipeBuilder()
                .input(Blocks.STONE, 64)
                .output(ore, Iron, 4)
                .output(ore, Coal, 4)
                .output(ore, Redstone, 4)
                .output(ore, Gold, 4)
                .output(ore, Diamond, 4)
                .output(ore, Copper, 4)
                .output(ore, Tin, 4)
                .output(ore, Silver, 4)
                .output(ore, Lead, 4)
                .output(ore, Nickel, 4)
                .output(ore, Calcite, 4)
                .output(ore, Apatite, 4)
                .output(ore, Salt, 4)
                .output(ore, Lapis, 4)
                .output(ore, Sodalite, 4)
                .output(ore, Magnesite, 4)
                .duration(600)
                .EUt(7)
                .buildAndRegister();

        GTSRecipeMaps.BIOMIMETIC_FACTORY_RECIPES.recipeBuilder()
                .input(Blocks.NETHERRACK, 64)
                .output(ore, Ruby, 4)
                .output(ore, Saltpeter, 4)
                .output(ore, Diatomite, 4)
                .output(ore, Electrotine, 4)
                .output(ore, Alunite, 4)
                .output(ore, Topaz, 4)
                .output(ore, Chalcocite, 4)
                .output(ore, Bornite, 4)
                .output(ore, Beryllium, 4)
                .output(ore, Emerald, 4)
                .output(ore, NetherQuartz, 4)
                .output(ore, QuartzSand, 4)
                .output(ore, Quartzite, 4)
                .output(ore, Sulfur, 4)
                .output(ore, RockSalt, 4)
                .output(ore, Sphalerite, 4)
                .duration(600)
                .EUt(7)
                .buildAndRegister();
    }
}
