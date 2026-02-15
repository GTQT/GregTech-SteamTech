package meowmel.gtsteam.loader.recipes;

import gregtech.api.GTValues;
import gregtech.api.recipes.RecipeMaps;

import static gregtech.api.GTValues.LV;
import static gregtech.api.GTValues.V;
import static gregtech.api.unification.material.Materials.*;
import static meowmel.gtsteam.api.recipes.GTSRecipeMaps.*;
import static meowmel.gtsteam.api.unification.GTSteamMaterials.*;

public class GenerateRecipes {
    public static void init() {
        primitive();
        // gas turbine fuels
        // 富甲烷气燃料配方
        RecipeMaps.GAS_TURBINE_FUELS.recipeBuilder()
                .fluidInputs(MethaneRichGas.getFluid(8))
                .duration(5)
                .EUt(V[LV])
                .buildAndRegister();

        // 富乙烯气燃料配方
        RecipeMaps.GAS_TURBINE_FUELS.recipeBuilder()
                .fluidInputs(EthyleneRichGas.getFluid(6))
                .duration(5)
                .EUt(V[LV])
                .buildAndRegister();

        // 合成气燃料配方
        RecipeMaps.GAS_TURBINE_FUELS.recipeBuilder()
                .fluidInputs(SynthesisGas.getFluid(16))
                .duration(5)
                .EUt(V[LV])
                .buildAndRegister();
    }

    private static void primitive() {
        // steam generator fuels
        PRIMITIVE_STEAM_TURBINE_FUELS.recipeBuilder()
                .fluidInputs(Steam.getFluid(160))
                .fluidOutputs(DistilledWater.getFluid(1))
                .duration(10)
                .EUt(V[GTValues.ULV])
                .buildAndRegister();

        PRIMITIVE_COMBUSTION_GENERATOR_FUELS.recipeBuilder()
                .fluidInputs(RawOil.getFluid(16))
                .duration(15)
                .EUt(V[GTValues.ULV])
                .buildAndRegister();

        PRIMITIVE_GAS_TURBINE_FUELS.recipeBuilder()
                .fluidInputs(NaturalGas.getFluid(8))
                .duration(20)
                .EUt(V[GTValues.ULV])
                .buildAndRegister();

        PRIMITIVE_GAS_TURBINE_FUELS.recipeBuilder()
                .fluidInputs(MethaneRichGas.getFluid(8))
                .duration(20)
                .EUt(V[GTValues.ULV])
                .buildAndRegister();

        PRIMITIVE_GAS_TURBINE_FUELS.recipeBuilder()
                .fluidInputs(EthyleneRichGas.getFluid(6))
                .duration(20)
                .EUt(V[GTValues.ULV])
                .buildAndRegister();

        PRIMITIVE_GAS_TURBINE_FUELS.recipeBuilder()
                .fluidInputs(SynthesisGas.getFluid(16))
                .duration(20)
                .EUt(V[GTValues.ULV])
                .buildAndRegister();

        PRIMITIVE_GAS_TURBINE_FUELS.recipeBuilder()
                .fluidInputs(LPG.getFluid(1))
                .duration(40)
                .EUt(V[GTValues.ULV])
                .buildAndRegister();

        PRIMITIVE_GAS_TURBINE_FUELS.recipeBuilder()
                .fluidInputs(Methane.getFluid(2))
                .duration(28)
                .EUt(V[GTValues.ULV])
                .buildAndRegister();

        PRIMITIVE_SEMI_FLUID_GENERATOR_FUELS.recipeBuilder()
                .fluidInputs(Biomass.getFluid(4))
                .duration(4)
                .EUt(V[GTValues.ULV])
                .buildAndRegister();

        PRIMITIVE_SEMI_FLUID_GENERATOR_FUELS.recipeBuilder()
                .fluidInputs(Creosote.getFluid(16))
                .duration(4)
                .EUt(V[GTValues.ULV])
                .buildAndRegister();

        PRIMITIVE_SEMI_FLUID_GENERATOR_FUELS.recipeBuilder()
                .fluidInputs(Lava.getFluid(5))
                .duration(4)
                .EUt(V[GTValues.ULV])
                .buildAndRegister();
    }
}
