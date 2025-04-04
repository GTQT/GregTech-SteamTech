package keqing.gtsteam.loader.recipes;

import keqing.gtsteam.api.recipes.GTSRecipeMaps;
import net.minecraft.init.Blocks;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.ingot;
import static gregtech.api.unification.ore.OrePrefix.ore;

public class BiomimeticFactoryRecipes {
    public static void init() {
        GTSRecipeMaps.BIOMIMETIC_FACTORY_RECIPES.recipeBuilder()
                .input(Blocks.STONE, 64)
                .output(ore, Iron,64)
                .output(ore, Coal,64)
                .output(ore, Redstone,64)
                .output(ore, Gold,64)
                .output(ore, Diamond,64)
                .output(ore, Copper,64)
                .output(ore, Tin,64)
                .output(ore, Silver,64)
                .output(ore, Lead,64)
                .output(ore, Nickel,64)
                .output(ore, Calcite,64)
                .output(ore, Sulfur,64)
                .output(ore, Quartzite,64)
                .output(ore, Salt,64)
                .output(ore, RockSalt,64)
                .output(ore, Sphalerite,64)
                .duration(1200)
                .EUt(30)
                .buildAndRegister();
    }
}
