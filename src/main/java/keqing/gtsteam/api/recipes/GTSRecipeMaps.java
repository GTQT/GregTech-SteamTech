package keqing.gtsteam.api.recipes;

import gregtech.api.mui.GTGuiTextures;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMapBuilder;
import gregtech.api.recipes.builders.FuelRecipeBuilder;
import gregtech.api.recipes.builders.PrimitiveRecipeBuilder;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.core.sound.GTSoundEvents;

public class GTSRecipeMaps {
    public static final RecipeMap<SimpleRecipeBuilder> STEAM_BLAST_FURNACE_RECIPES;
    public static final RecipeMap<SimpleRecipeBuilder> BIOMIMETIC_FACTORY_RECIPES;
    public static final RecipeMap<SimpleRecipeBuilder> LAVA_FURNACE_RECIPES;
    public static final RecipeMap<PrimitiveRecipeBuilder> ALLOY_KILN;
    public static final RecipeMap<PrimitiveRecipeBuilder> SAW_MILL;
    public static final RecipeMap<FuelRecipeBuilder> PRIMITIVE_STEAM_TURBINE_FUELS;
    public static final RecipeMap<FuelRecipeBuilder> PRIMITIVE_COMBUSTION_GENERATOR_FUELS;

    private GTSRecipeMaps() {}
    static {
        PRIMITIVE_STEAM_TURBINE_FUELS = new RecipeMapBuilder<>("primitive_steam_turbine",
                new FuelRecipeBuilder())
                .fluidInputs(1)
                .fluidOutputs(1)
                .uiBuilder((b) -> b
                        .fluidSlotOverlay(GTGuiTextures.CENTRIFUGE_OVERLAY, false, true)
                        .progressBar(GTGuiTextures.PROGRESS_BAR_GAS_COLLECTOR)
                )
                .sound(GTSoundEvents.TURBINE)
                .allowEmptyOutputs()
                .generator()
                .build();

        PRIMITIVE_COMBUSTION_GENERATOR_FUELS = new RecipeMapBuilder<>(
                "primitive_combustion_generator", new FuelRecipeBuilder())
                .fluidInputs(1)
                .uiBuilder((b) -> b
                        .fluidSlotOverlay(GTGuiTextures.FURNACE_OVERLAY_2, false, true)
                        .progressBar(GTGuiTextures.PROGRESS_BAR_ARROW_MULTIPLE)
                )
                .sound(GTSoundEvents.COMBUSTION)
                .allowEmptyOutputs()
                .generator()
                .build();

        BIOMIMETIC_FACTORY_RECIPES = new RecipeMapBuilder<>("biomimetic_factory_recipes",
                new SimpleRecipeBuilder())
                .itemInputs(1)
                .itemOutputs(16)
                .sound(GTSoundEvents.ARC)
                .build();

        LAVA_FURNACE_RECIPES= new RecipeMapBuilder<>("lava_furnace",
                new SimpleRecipeBuilder())
                .itemInputs(1)
                .fluidOutputs(1)
                .sound(GTSoundEvents.FIRE)
                .build();

        STEAM_BLAST_FURNACE_RECIPES = new RecipeMapBuilder<>("steam_blast_furnace",
                new SimpleRecipeBuilder())
                .itemInputs(3)
                .itemOutputs(1)
                .sound(GTSoundEvents.FIRE)
                .build();

        ALLOY_KILN = new RecipeMapBuilder<>("alloy_klin",
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
                .uiBuilder((b) -> b
                        .progressBar(GTGuiTextures.PROGRESS_BAR_ARROW_MULTIPLE)
                )
                .sound(GTSoundEvents.CUT)
                .build();
    }
}
