package meowmel.gtsteam.loader.recipes;

import gregtech.api.unification.ore.OrePrefix;
import meowmel.gtsteam.api.recipes.GTSRecipeMaps;
import net.minecraft.init.Items;

import static gregtech.api.unification.material.Materials.Charcoal;
import static gregtech.api.unification.material.Materials.Coal;
import static gregtech.common.items.MetaItems.*;

public class BrickKilnRecipes {
    public static void init() {
        GTSRecipeMaps.BRICK_KILN.recipeBuilder()
                .duration(100)
                .input(OrePrefix.gem,Charcoal)
                .inputs(COMPRESSED_COKE_CLAY.getStackForm(16))
                .outputs(COKE_OVEN_BRICK.getStackForm(16))
                .buildAndRegister();

        GTSRecipeMaps.BRICK_KILN.recipeBuilder()
                .duration(150)
                .input(OrePrefix.gem,Coal)
                .inputs(COMPRESSED_COKE_CLAY.getStackForm(16))
                .outputs(COKE_OVEN_BRICK.getStackForm(16))
                .buildAndRegister();

        GTSRecipeMaps.BRICK_KILN.recipeBuilder()
                .duration(100)
                .input(OrePrefix.gem,Charcoal)
                .inputs(COMPRESSED_FIRECLAY.getStackForm(16))
                .outputs(FIRECLAY_BRICK.getStackForm(16))
                .buildAndRegister();

        GTSRecipeMaps.BRICK_KILN.recipeBuilder()
                .duration(150)
                .input(OrePrefix.gem,Coal)
                .inputs(COMPRESSED_FIRECLAY.getStackForm(16))
                .outputs(FIRECLAY_BRICK.getStackForm(16))
                .buildAndRegister();

        GTSRecipeMaps.BRICK_KILN.recipeBuilder()
                .duration(100)
                .input(OrePrefix.gem,Charcoal)
                .input(Items.CLAY_BALL,16)
                .output(Items.BRICK,16)
                .buildAndRegister();

        GTSRecipeMaps.BRICK_KILN.recipeBuilder()
                .duration(150)
                .input(OrePrefix.gem,Coal)
                .input(Items.CLAY_BALL,16)
                .output(Items.BRICK,16)
                .buildAndRegister();
    }
}
