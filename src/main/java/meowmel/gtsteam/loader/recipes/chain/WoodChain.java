package meowmel.gtsteam.loader.recipes.chain;

import meowmel.gtsteam.api.recipes.GTSRecipeMaps;
import net.minecraft.init.Items;

import static gregtech.api.recipes.RecipeMaps.FERMENTING_RECIPES;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.dust;
import static meowmel.gtsteam.api.unification.GTSteamMaterials.*;

public class WoodChain {
    public static void init() {
        //初级干馏
        primitiveDistillation();
        //净化处理
        purification();
        //裂化
        cracking();
        //精制气体
        gas();
    }

    private static void gas() {
        // 木醋酸蒸馏 产物：乙酸 木焦油 甲醇 水
        GTSRecipeMaps.HEAT_DISTILLATION_RECIPES.recipeBuilder()
                .fluidInputs(WoodVinegar.getFluid(1000))
                .fluidOutputs(WoodTar.getFluid(100))
                .fluidOutputs(AceticAcid.getFluid(350))
                .fluidOutputs(Methanol.getFluid(300))
                .fluidOutputs(Water.getFluid(250))
                .duration(800)
                .Temperature(598)
                .Heat(25)
                .buildAndRegister();

        // 木煤气蒸馏
        GTSRecipeMaps.HEAT_DISTILLATION_RECIPES.recipeBuilder()
                .fluidInputs(WoodGas.getFluid(1000))
                .fluidOutputs(MethaneRichGas.getFluid(500))
                .fluidOutputs(EthyleneRichGas.getFluid(300))
                .fluidOutputs(SynthesisGas.getFluid(200))
                .duration(300)
                .Temperature(573)
                .Heat(10)
                .buildAndRegister();

        //发酵生物质蒸馏 产物：乙醇 乙酸 杂酚油 酒糟液（加速发酵）
        GTSRecipeMaps.HEAT_DISTILLATION_RECIPES.recipeBuilder()
                .fluidInputs(FermentedBiomass.getFluid(1000))
                .fluidOutputs(Ethanol.getFluid(120))
                .fluidOutputs(AceticAcid.getFluid(200))
                .fluidOutputs(Creosote.getFluid(50))
                .fluidOutputs(Stillage.getFluid(600))
                .duration(300)
                .Temperature(573)
                .Heat(10)
                .buildAndRegister();

        FERMENTING_RECIPES.recipeBuilder()
                .fluidInputs(Stillage.getFluid(100))
                .fluidOutputs(FermentedBiomass.getFluid(100))
                .duration(40).EUt(2).buildAndRegister();
    }

    private static void cracking() {
        //木焦油加蒸汽裂化 产合成气 杂酚油
        GTSRecipeMaps.HEAT_CRACKING_RECIPES.recipeBuilder()
                .fluidInputs(WoodTar.getFluid(1000))
                .fluidInputs(Steam.getFluid(1000))
                .fluidOutputs(SynthesisGas.getFluid(1000))
                .duration(80)
                .Temperature(598)
                .Heat(30)
                .buildAndRegister();

        //煤焦油加氢裂化 富甲烷气
        GTSRecipeMaps.HEAT_CRACKING_RECIPES.recipeBuilder()
                .fluidInputs(WoodTar.getFluid(1000))
                .fluidInputs(Hydrogen.getFluid(100))
                .fluidOutputs(MethaneRichGas.getFluid(1000))
                .duration(60)
                .Temperature(598)
                .Heat(30)
                .buildAndRegister();

        GTSRecipeMaps.HEAT_DISTILLATION_RECIPES.recipeBuilder()
                .fluidInputs(WoodTar.getFluid(1000))
                .fluidOutputs(Creosote.getFluid(200))
                .output(dust, Asphalt, 8)
                .duration(100)
                .Temperature(598)
                .Heat(20)
                .buildAndRegister();
    }

    private static void purification() {
        // 原始化反
        // 粗木醋酸脱乙酸
        GTSRecipeMaps.HEAT_CHEMICAL_RECIPES.recipeBuilder()
                .fluidInputs(RawWoodVinegar.getFluid(1000))
                .fluidInputs(Calcimine.getFluid(100))
                .fluidOutputs(WoodVinegar.getFluid(850))
                .fluidOutputs(CalciumAcetateSlurry.getFluid(250))
                .duration(400)
                .Temperature(573)
                .Heat(10)
                .buildAndRegister();

        // 粗生物气脱乙酸
        GTSRecipeMaps.HEAT_CHEMICAL_RECIPES.recipeBuilder()
                .fluidInputs(RawWoodGas.getFluid(1000))
                .fluidInputs(Calcimine.getFluid(100))
                .fluidOutputs(WoodGas.getFluid(850))
                .fluidOutputs(CalciumAcetateSlurry.getFluid(250))
                .duration(400)
                .Temperature(573)
                .Heat(10)
                .buildAndRegister();

        //含硫废液处理
        //热力蒸馏塔 产物：硫粉 石灰乳 水
        GTSRecipeMaps.HEAT_DISTILLATION_RECIPES.recipeBuilder()
                .fluidInputs(CalciumAcetateSlurry.getFluid(2500))
                .output(dust, Quicklime, 1)
                .fluidOutputs(Acetone.getFluid(1000))
                .fluidOutputs(Water.getFluid(1000))
                .fluidOutputs(Methane.getFluid(1000))
                .fluidOutputs(CarbonMonoxide.getFluid(1000))
                .duration(250)
                .Temperature(573)
                .Heat(15)
                .buildAndRegister();
    }

    private static void primitiveDistillation() {
        //热力蒸馏塔
        GTSRecipeMaps.HEAT_DISTILLATION_RECIPES.recipeBuilder()
                .fluidInputs(Biomass.getFluid(2000))
                .fluidOutputs(RawWoodGas.getFluid(800))
                .fluidOutputs(RawWoodVinegar.getFluid(800))
                .fluidOutputs(WoodTar.getFluid(400))
                .output(Items.COAL, 8, 1)
                .duration(400)
                .Temperature(573)
                .Heat(15)
                .buildAndRegister();
    }
}
