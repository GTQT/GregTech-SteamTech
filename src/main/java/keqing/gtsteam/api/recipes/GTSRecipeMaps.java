package keqing.gtsteam.api.recipes;

import gregtech.api.mui.GTGuiTextures;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMapBuilder;
import gregtech.api.recipes.builders.*;
import gregtech.api.recipes.ui.impl.CrackerUnitUI;
import gregtech.api.recipes.ui.impl.DistillationTowerUI;
import gregtech.core.sound.GTSoundEvents;

public class GTSRecipeMaps {
    public static final RecipeMap<SimpleRecipeBuilder> BIOMIMETIC_FACTORY_RECIPES;
    public static final RecipeMap<SimpleRecipeBuilder> LAVA_FURNACE_RECIPES;
    public static final RecipeMap<HeatRecipeBuilder> EVAPORATION_RECIPES;
    public static final RecipeMap<HeatRecipeBuilder> HEAT_CRACKING_RECIPES;
    public static final RecipeMap<HeatRecipeBuilder> HEAT_DISTILLATION_RECIPES;
    public static final RecipeMap<HeatRecipeBuilder> HEAT_CHEMICAL_RECIPES;
    public static final RecipeMap<PrimitiveRecipeBuilder> ALLOY_KILN;
    public static final RecipeMap<PrimitiveRecipeBuilder> COAGULATION_RECIPES;
    public static final RecipeMap<FuelRecipeBuilder> PRIMITIVE_STEAM_TURBINE_FUELS;
    public static final RecipeMap<FuelRecipeBuilder> PRIMITIVE_COMBUSTION_GENERATOR_FUELS;
    public static final RecipeMap<FuelRecipeBuilder> PRIMITIVE_SEMI_FLUID_GENERATOR_FUELS;


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
                .disableJeiOverclockButton()
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
                .disableJeiOverclockButton()
                .generator()
                .build();

        PRIMITIVE_SEMI_FLUID_GENERATOR_FUELS = new RecipeMapBuilder<>(
                "primitive_semi_fluid_generator", new FuelRecipeBuilder())
                .fluidInputs(1)
                .uiBuilder((b) -> b
                        .fluidSlotOverlay(GTGuiTextures.FURNACE_OVERLAY_2, false, true)
                        .progressBar(GTGuiTextures.PROGRESS_BAR_ARROW_MULTIPLE)
                )
                .sound(GTSoundEvents.COMBUSTION)
                .allowEmptyOutputs()
                .disableJeiOverclockButton()
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

        EVAPORATION_RECIPES = new RecipeMapBuilder<>("evaporation_pool",
                new HeatRecipeBuilder())
                .itemInputs(2)
                .itemOutputs(4)
                .fluidInputs(1)
                .fluidOutputs(1)
                .uiBuilder(builder -> builder
                        .progressBar(GTGuiTextures.PROGRESS_BAR_SIFT)
                )
                .sound(GTSoundEvents.CHEMICAL_REACTOR)
                .build();

        HEAT_CRACKING_RECIPES = new RecipeMapBuilder<>("heat_cracker",
                new HeatRecipeBuilder())
                .itemInputs(1)
                .fluidInputs(2)
                .fluidOutputs(2)
                .ui(CrackerUnitUI::new)
                .sound(GTSoundEvents.FIRE)
                .build();

        HEAT_DISTILLATION_RECIPES = new RecipeMapBuilder<>(
                "heat_distillation_tower", new HeatRecipeBuilder())
                .itemOutputs(1)
                .fluidInputs(1)
                .fluidOutputs(12)
                .ui(DistillationTowerUI::new)
                .sound(GTSoundEvents.CHEMICAL_REACTOR)
                .build();

        HEAT_CHEMICAL_RECIPES = new RecipeMapBuilder<>("heat_chemical_reactor",
                new HeatRecipeBuilder())
                .itemInputs(2)
                .itemOutputs(2)
                .fluidInputs(3)
                .fluidOutputs(2)
                .uiBuilder(b -> b
                        .itemSlotOverlay(GTGuiTextures.MOLECULAR_OVERLAY_1, false, false)
                        .itemSlotOverlay(GTGuiTextures.MOLECULAR_OVERLAY_2, false, true)
                        .itemSlotOverlay(GTGuiTextures.VIAL_OVERLAY_1, true)
                        .fluidSlotOverlay(GTGuiTextures.MOLECULAR_OVERLAY_3, false)
                        .fluidSlotOverlay(GTGuiTextures.MOLECULAR_OVERLAY_4, false)
                        .fluidSlotOverlay(GTGuiTextures.VIAL_OVERLAY_2, true)
                        .progressBar(GTGuiTextures.PROGRESS_BAR_ARROW_MULTIPLE))
                .sound(GTSoundEvents.CHEMICAL_REACTOR)
                .build();

        ALLOY_KILN = new RecipeMapBuilder<>("alloy_klin",
                new PrimitiveRecipeBuilder())
                .itemInputs(2)
                .itemOutputs(2)
                .fluidInputs(1)
                .fluidOutputs(0)
                .sound(GTSoundEvents.FIRE)
                .build();

        COAGULATION_RECIPES = new RecipeMapBuilder<>("coagulation",
                new PrimitiveRecipeBuilder())
                .itemInputs(3)
                .itemOutputs(3)
                .fluidInputs(3)
                .fluidOutputs(3)
                .sound(GTSoundEvents.FIRE)
                .disableJeiOverclockButton()
                .build();
    }
}
