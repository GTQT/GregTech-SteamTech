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

import static gregtech.api.gui.widgets.ProgressWidget.MoveType.HORIZONTAL;

public class GTSRecipeMaps {
    public static final RecipeMap<SimpleRecipeBuilder> STEAM_BLAST_FURNACE_RECIPES;
    public static final RecipeMap<SimpleRecipeBuilder> BIOMIMETIC_FACTORY_RECIPES;
    public static final RecipeMap<SimpleRecipeBuilder> STEAM_ORE_WASHER_RECIPES;
    public static final RecipeMap<PrimitiveRecipeBuilder> ALLOY_kILN;
    public static final RecipeMap<PrimitiveRecipeBuilder> SAW_MILL;
    public static final RecipeMap<FuelRecipeBuilder> PRIMITIVE_STEAM_TURBINE_FUELS;
    public static final RecipeMap<FuelRecipeBuilder> PRIMITIVE_COMBUSTION_GENERATOR_FUELS;

    private GTSRecipeMaps() {}
    static {
        PRIMITIVE_STEAM_TURBINE_FUELS = new RecipeMapBuilder<>("primitive_steam_turbine",
                new FuelRecipeBuilder())
                .fluidInputs(1)
                .fluidOutputs(1)
                .fluidSlotOverlay(GuiTextures.CENTRIFUGE_OVERLAY, false)
                .progressBar(GuiTextures.PROGRESS_BAR_GAS_COLLECTOR)
                .sound(GTSoundEvents.TURBINE)
                .allowEmptyOutputs()
                .generator()
                .build();

        PRIMITIVE_COMBUSTION_GENERATOR_FUELS = new RecipeMapBuilder<>(
                "primitive_combustion_generator", new FuelRecipeBuilder())
                .fluidInputs(1)
                .fluidSlotOverlay(GuiTextures.FURNACE_OVERLAY_2, false)
                .progressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE)
                .sound(GTSoundEvents.COMBUSTION)
                .allowEmptyOutputs()
                .generator()
                .build();

        BIOMIMETIC_FACTORY_RECIPES = new RecipeMapBuilder<>("biomimetic_factory_recipes",
                new SimpleRecipeBuilder())
                .itemInputs(1)
                .itemOutputs(16)
                .fluidInputs(0)
                .fluidOutputs(0)
                .sound(GTSoundEvents.ARC)
                .build();

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

        SAW_MILL = new RecipeMapBuilder<>("saw_mill",
                new PrimitiveRecipeBuilder())
                .itemInputs(2)
                .itemOutputs(2)
                .progressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, HORIZONTAL)
                .sound(GTSoundEvents.CUT)
                .build();
    }
}
