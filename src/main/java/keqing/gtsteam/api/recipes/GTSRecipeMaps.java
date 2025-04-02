package keqing.gtsteam.api.recipes;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMapBuilder;
import gregtech.api.recipes.builders.FuelRecipeBuilder;
import gregtech.api.recipes.builders.PrimitiveRecipeBuilder;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.api.recipes.ui.impl.CokeOvenUI;
import gregtech.core.sound.GTSoundEvents;

public class GTSRecipeMaps {
    public static final RecipeMap<SimpleRecipeBuilder> STEAM_BLAST_FURNACE_RECIPES;
    public static final RecipeMap<SimpleRecipeBuilder> STEAM_ORE_WASHER_RECIPES;
    public static final RecipeMap<PrimitiveRecipeBuilder> ALLOY_kILN;

    private GTSRecipeMaps() {}
    static {

        STEAM_BLAST_FURNACE_RECIPES = new RecipeMapBuilder<>("steam_blast_furnace",
                new SimpleRecipeBuilder())
                .itemInputs(3)
                .itemOutputs(1)
                .fluidInputs(0)
                .fluidOutputs(0)
                .sound(GTSoundEvents.FIRE)
                .build();

        STEAM_ORE_WASHER_RECIPES = new RecipeMapBuilder<>("steam_ore_washer",
                new SimpleRecipeBuilder())
                .itemInputs(3)
                .itemOutputs(1)
                .fluidInputs(1)
                .fluidOutputs(0)
                .sound(GTSoundEvents.FIRE)
                .build();


        ALLOY_kILN = new RecipeMapBuilder<>("alloy_klin",
                new PrimitiveRecipeBuilder())
                .itemInputs(2)
                .itemOutputs(2)
                .fluidInputs(1)
                .fluidOutputs(0)
                .sound(GTSoundEvents.FIRE)
                .build();
    }
}
