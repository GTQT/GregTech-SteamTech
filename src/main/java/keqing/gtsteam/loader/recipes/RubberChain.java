package keqing.gtsteam.loader.recipes;

import gregtech.api.recipes.RecipeMaps;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.common.items.MetaItems.STICKY_RESIN;
import static keqing.gtsteam.api.recipes.GTSRecipeMaps.PRIMITIVE_CHEMICAL_RECIPES;
import static keqing.gtsteam.api.unification.GTSteamMaterials.*;

public class RubberChain {
    public static void init() {
        rawRubber();
    }
    public static void rawRubber() {
        //粘性树脂/橡胶木 到 橡胶的产线
        // 1. 粘性树脂 + Trona = 橡胶浆料
        PRIMITIVE_CHEMICAL_RECIPES.recipeBuilder().duration(200)
                .inputs(STICKY_RESIN.getStackForm(1))
                .input(dust,Trona)
                .fluidInputs(Water.getFluid(1000))
                .fluidOutputs(RubberPulp.getFluid(1000))
                .buildAndRegister();

        // 2. 橡胶浆料 + 岩盐 = 生橡胶乳清液 + 生橡胶沉淀
        PRIMITIVE_CHEMICAL_RECIPES.recipeBuilder().duration(400)
                .fluidInputs(RubberPulp.getFluid(1000))
                .input(dust,Salt,2)
                .notConsumable(stick,Iron)
                .fluidOutputs(RawRubberWhey.getFluid(1000))
                .output(ingot,RawRubberPrecipitate)
                .buildAndRegister();

        // 3. 生橡胶乳清液 离心 = 胶水 + 盐水
        RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder().duration(200)
                .EUt(30)
                .fluidInputs(RawRubberWhey.getFluid(1000))
                .fluidOutputs(Glue.getFluid(200))
                .fluidOutputs(SaltWater.getFluid(1000))
                .buildAndRegister();

        // 4. 生橡胶沉淀 粉碎 = 橡胶粉末
        RecipeMaps.MACERATOR_RECIPES.recipeBuilder().duration(10)
                .EUt(5)
                .input(ingot,RawRubberPrecipitate)
                .output(dust,RawRubber,3)
                .buildAndRegister();
    }
}
