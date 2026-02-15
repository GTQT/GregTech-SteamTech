package meowmel.gtsteam.loader.recipes.chain;

import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.stack.UnificationEntry;
import meowmel.gtsteam.api.recipes.GTSRecipeMaps;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static gregtech.api.recipes.RecipeMaps.COKE_OVEN_RECIPES;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.dust;
import static gregtech.api.unification.ore.OrePrefix.gem;
import static meowmel.gtsteam.api.unification.GTSteamMaterials.*;

public class CoalChain {
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
        // 富乙烯气 -> 乙烯 甲烷 氢气 一氧化碳
        GTSRecipeMaps.HEAT_DISTILLATION_RECIPES.recipeBuilder()
                .fluidInputs(EthyleneRichGas.getFluid(4000))
                .fluidOutputs(Ethylene.getFluid(2000))
                .fluidOutputs(Methane.getFluid(800))
                .fluidOutputs(Hydrogen.getFluid(800))
                .fluidOutputs(CarbonMonoxide.getFluid(400))
                .duration(800)
                .Temperature(598)
                .Heat(25)
                .buildAndRegister();

        // 富甲烷气 -> 甲烷 LPG
        GTSRecipeMaps.HEAT_DISTILLATION_RECIPES.recipeBuilder()
                .fluidInputs(MethaneRichGas.getFluid(1000))
                .fluidOutputs(Methane.getFluid(700))
                .fluidOutputs(LPG.getFluid(300))
                .duration(40)
                .Temperature(598)
                .Heat(25)
                .buildAndRegister();

        // 煤气 -> 富乙烯气 富甲烷气 合成气
        GTSRecipeMaps.HEAT_DISTILLATION_RECIPES.recipeBuilder()
                .fluidInputs(CoalGas.getFluid(1000))
                .fluidOutputs(MethaneRichGas.getFluid(500))
                .fluidOutputs(EthyleneRichGas.getFluid(300))
                .fluidOutputs(SynthesisGas.getFluid(200))
                .duration(300)
                .Temperature(573)
                .Heat(10)
                .buildAndRegister();

        // 合成气 -> 氢气 一氧化碳 甲烷
        GTSRecipeMaps.HEAT_DISTILLATION_RECIPES.recipeBuilder()
                .fluidInputs(SynthesisGas.getFluid(1000))
                .fluidOutputs(Hydrogen.getFluid(600))
                .fluidOutputs(CarbonMonoxide.getFluid(300))
                .fluidOutputs(Methane.getFluid(100))
                .duration(60)
                .Temperature(573)
                .Heat(20)
                .buildAndRegister();
    }

    private static void cracking() {
        //煤焦油蒸汽裂化 合成气
        GTSRecipeMaps.HEAT_CRACKING_RECIPES.recipeBuilder()
                .fluidInputs(CoalTar.getFluid(1000))
                .fluidInputs(Steam.getFluid(1000))
                .fluidOutputs(SynthesisGas.getFluid(1000))
                .duration(80)
                .Temperature(598)
                .Heat(30)
                .buildAndRegister();

        //煤焦油加氢裂化 富甲烷气
        GTSRecipeMaps.HEAT_CRACKING_RECIPES.recipeBuilder()
                .fluidInputs(CoalTar.getFluid(1000))
                .fluidInputs(Hydrogen.getFluid(100))
                .fluidOutputs(MethaneRichGas.getFluid(1000))
                .duration(60)
                .Temperature(598)
                .Heat(30)
                .buildAndRegister();

        GTSRecipeMaps.HEAT_DISTILLATION_RECIPES.recipeBuilder()
                .fluidInputs(CoalTar.getFluid(1000))
                .fluidOutputs(Creosote.getFluid(200))
                .output(dust, Asphalt, 8)
                .duration(100)
                .Temperature(598)
                .Heat(20)
                .buildAndRegister();
    }

    private static void purification() {
        //石灰浆
        //搅拌 水+生石灰
        RecipeMaps.MIXER_RECIPES.recipeBuilder()
                .fluidInputs(Water.getFluid(1000))
                .input(dust, Quicklime, 2)
                .fluidOutputs(Calcimine.getFluid(1000))
                .EUt(7)
                .duration(100)
                .buildAndRegister();

        //石灰
        GameRegistry.addSmelting(OreDictUnifier.get(new UnificationEntry(dust, Stone)),
                OreDictUnifier.get(new UnificationEntry(dust, Calcite)), 0.15F);

        //石灰窑
        COKE_OVEN_RECIPES.recipeBuilder()
                .input(dust, Calcite, 5)
                .output(dust, Quicklime, 2)
                .fluidOutputs(CarbonDioxide.getFluid(1000))
                .duration(200)
                .buildAndRegister();

        //原始化反
        //粗煤气脱硫
        GTSRecipeMaps.HEAT_CHEMICAL_RECIPES.recipeBuilder()
                .fluidInputs(RawCoalGas.getFluid(1000))
                .fluidInputs(Calcimine.getFluid(100))
                .fluidOutputs(CoalGas.getFluid(850))
                .fluidOutputs(SulfuricWasteFluid.getFluid(150))
                .duration(400)
                .Temperature(573)
                .Heat(10)
                .buildAndRegister();

        //煤焦油脱沥青/脱硫
        GTSRecipeMaps.HEAT_CHEMICAL_RECIPES.recipeBuilder()
                .fluidInputs(RawCoalTar.getFluid(1000))
                .fluidInputs(Calcimine.getFluid(250))
                .fluidOutputs(CoalTar.getFluid(800))
                .fluidOutputs(SulfuricWasteFluid.getFluid(50))
                .output(dust, Asphalt, 4)
                .duration(500)
                .Temperature(573)
                .Heat(10)
                .buildAndRegister();

        //含硫废液处理
        //热力蒸馏塔 产物：硫粉 石灰乳 水
        GTSRecipeMaps.HEAT_DISTILLATION_RECIPES.recipeBuilder()
                .fluidInputs(SulfuricWasteFluid.getFluid(4000))
                .output(dust, Sulfur, 4)
                .fluidOutputs(HydrogenSulfide.getFluid(2000))
                .fluidOutputs(Calcimine.getFluid(1000))
                .fluidOutputs(Water.getFluid(1000))
                .duration(800)
                .Temperature(623)
                .Heat(20)
                .buildAndRegister();
    }

    private static void primitiveDistillation() {
        //热力蒸馏塔
        // 输入：煤 8 个
        // 输出：粗煤气 煤焦油 焦炭
        GTSRecipeMaps.HEAT_DISTILLATION_RECIPES.recipeBuilder()
                .input(gem, Coal, 8)
                .fluidOutputs(RawCoalGas.getFluid(4000))
                .fluidOutputs(RawCoalTar.getFluid(4000))
                .fluidOutputs(Creosote.getFluid(4000))
                .output(gem, Coke, 2)
                .duration(1000)
                .Temperature(573)
                .Heat(15)
                .buildAndRegister();

        GTSRecipeMaps.HEAT_DISTILLATION_RECIPES.recipeBuilder()
                .input(gem, Lignite, 8)
                .fluidOutputs(RawCoalGas.getFluid(6000))
                .fluidOutputs(RawCoalTar.getFluid(6000))
                .fluidOutputs(Creosote.getFluid(3200))
                .output(gem, Coke, 2)
                .duration(1200)
                .Temperature(573)
                .Heat(15)
                .buildAndRegister();
    }
}
