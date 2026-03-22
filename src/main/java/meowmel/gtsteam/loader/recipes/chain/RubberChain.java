package meowmel.gtsteam.loader.recipes.chain;

import gregtech.api.recipes.GTRecipeHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.OreDictUnifier;
import gregtech.common.blocks.MetaBlocks;
import gregtech.loaders.recipe.chemistry.RubberRecipes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static gregtech.api.GTValues.SECOND;
import static gregtech.api.GTValues.TICK;
import static gregtech.api.recipes.RecipeMaps.CENTRIFUGE_RECIPES;
import static gregtech.api.recipes.RecipeMaps.EXTRACTOR_RECIPES;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.common.items.MetaItems.PLANT_BALL;
import static gregtech.common.items.MetaItems.STICKY_RESIN;
import static meowmel.gtsteam.api.recipes.GTSRecipeMaps.*;
import static meowmel.gtsteam.api.unification.GTSteamMaterials.*;

public class RubberChain {
    public static void init() {
        collectRubber();
        rawRubber();
    }

    private static void collectRubber() {
        SAP_COLLECTOR_RECIPES.recipeBuilder()
                .fluidInputs(Water.getFluid(10))
                .fluidOutputs(Latex.getFluid(100))
                .blockStates("latex_logs", MetaBlocks.RUBBER_LOG.getBlockState())
                .duration(400)
                .EUt(30)
                .buildAndRegister();

        SAP_COLLECTOR_RECIPES.recipeBuilder()
                .fluidInputs(DistilledWater.getFluid(10))
                .fluidOutputs(Resin.getFluid(100))
                .blockStates("extractable_logs_1", Blocks.LOG.getBlockState())
                .duration(400)
                .EUt(30)
                .buildAndRegister();

        SAP_COLLECTOR_RECIPES.recipeBuilder()
                .fluidInputs(Lubricant.getFluid(10))
                .fluidOutputs(Resin.getFluid(100))
                .blockStates("extractable_logs_2", Blocks.LOG2.getBlockState())
                .duration(400)
                .EUt(30)
                .buildAndRegister();
    }

    public static void rawRubber() {
        //粘性树脂/橡胶木 到 橡胶的产线
        //蒸汽时代的配方
        // 1. 粘性树脂 + Trona = 橡胶浆料
        COAGULATION_RECIPES.recipeBuilder().duration(200)
                .inputs(STICKY_RESIN.getStackForm(1))
                .input(dust, Trona)
                .fluidInputs(Water.getFluid(1000))
                .fluidOutputs(RubberPulp.getFluid(1000))
                .buildAndRegister();

        // 2. 橡胶浆料 + 盐 = 生橡胶乳清液 + 生橡胶沉淀
        COAGULATION_RECIPES.recipeBuilder().duration(400)
                .fluidInputs(RubberPulp.getFluid(1000))
                .input(dust, Salt, 2)
                .notConsumable(stick, Iron)
                .fluidOutputs(RawRubberWhey.getFluid(1000))
                .output(ingot, RawRubberPrecipitate)
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
                .input(ingot, RawRubberPrecipitate)
                .output(dust, RawRubber, 3)
                .buildAndRegister();

        //ULV时代的配方
        //1.凝固法
        HEAT_CHEMICAL_RECIPES.recipeBuilder()
                .duration(100)
                .Temperature(573)
                .Heat(14)
                .inputs(STICKY_RESIN.getStackForm(3))
                .input(dust, Trona)
                .fluidInputs(Water.getFluid(3000))
                .fluidOutputs(RubberPulp.getFluid(3000))
                .buildAndRegister();

        HEAT_CHEMICAL_RECIPES.recipeBuilder().duration(200)
                .Temperature(573)
                .Heat(14)
                .fluidInputs(RubberPulp.getFluid(1000))
                .input(dust, Salt, 2)
                .notConsumable(stick, Iron)
                .fluidOutputs(RawRubberWhey.getFluid(1000))
                .output(ingot, RawRubberPrecipitate, 2)
                .buildAndRegister();

        //2.酸法
        HEAT_CHEMICAL_RECIPES.recipeBuilder().duration(400)
                .Temperature(673)
                .Heat(30)
                .inputs(STICKY_RESIN.getStackForm(3))
                .input(dust, Salt, 2)
                .fluidInputs(Water.getFluid(1000))
                .notConsumable(AceticAcid.getFluid(2002))
                .fluidOutputs(RawRubberWhey.getFluid(1000))
                .output(ingot, RawRubberPrecipitate, 2)
                .buildAndRegister();

        //LV时代配方
        // Coagulation processing of liquid latex.
        GTRecipeHandler.removeRecipesByInputs(EXTRACTOR_RECIPES,
                OreDictUnifier.get(dust, Latex));

        COAGULATION_RECIPES.recipeBuilder()
                .notConsumable(stick, Iron)
                .notConsumable(SulfuricAcid.getFluid(200))
                .fluidInputs(Latex.getFluid(1000))
                .output(dust, Latex)
                .duration(5 * SECOND)
                .buildAndRegister();

        COAGULATION_RECIPES.recipeBuilder()
                .notConsumable(stick, Iron)
                .notConsumable(AceticAcid.getFluid(200))
                .fluidInputs(Latex.getFluid(1000))
                .output(dust, Latex)
                .duration(SECOND)
                .buildAndRegister();

        HEAT_CHEMICAL_RECIPES.recipeBuilder()
                .notConsumable(SulfuricAcid.getFluid(200))
                .fluidInputs(Latex.getFluid(1000))
                .output(dust, Latex)
                .duration(SECOND)
                .Temperature(673)
                .Heat(30)
                .buildAndRegister();

        HEAT_CHEMICAL_RECIPES.recipeBuilder()
                .notConsumable(AceticAcid.getFluid(200))
                .fluidInputs(Latex.getFluid(1000))
                .output(dust, Latex)
                .duration(5 * TICK)
                .Temperature(673)
                .Heat(30)
                .buildAndRegister();

        RubberRecipes.registerRecipes(Latex,Rubber,0);

        // 对原版配方的修正
        CENTRIFUGE_RECIPES.recipeBuilder().duration(400).EUt(30)
                .input(STICKY_RESIN)
                .output(dust, RawRubber, 3)
                .chancedOutput(PLANT_BALL, 1000, 850)
                .fluidOutputs(Glue.getFluid(100))
                .buildAndRegister();

        CENTRIFUGE_RECIPES.recipeBuilder().duration(200).EUt(30)
                .inputs(new ItemStack(MetaBlocks.RUBBER_LOG))
                .chancedOutput(PLANT_BALL, 3750, 900)
                .chancedOutput(dust, Carbon, 2500, 600)
                .chancedOutput(dust, Wood, 2500, 700)
                .chancedFluidOutput(Latex.getFluid(200), 5000, 1200)
                .buildAndRegister();

        CENTRIFUGE_RECIPES.recipeBuilder().duration(200).EUt(30)
                .inputs(new ItemStack(MetaBlocks.RUBBER_LEAVES))
                .chancedOutput(PLANT_BALL, 7500, 500)
                .chancedOutput(dust, Carbon, 5000, 500)
                .chancedOutput(dust, Wood, 5000, 500)
                .fluidOutputs(Methane.getFluid(120))
                .buildAndRegister();

        EXTRACTOR_RECIPES.recipeBuilder()
                .inputs(STICKY_RESIN.getStackForm())
                .fluidOutputs(Latex.getFluid(600))
                .duration(150).EUt(2)
                .buildAndRegister();

        EXTRACTOR_RECIPES.recipeBuilder().duration(300).EUt(2)
                .inputs(new ItemStack(MetaBlocks.RUBBER_LEAVES, 16))
                .fluidOutputs(Latex.getFluid(200))
                .buildAndRegister();

        EXTRACTOR_RECIPES.recipeBuilder().duration(300).EUt(2)
                .inputs(new ItemStack(MetaBlocks.RUBBER_LOG))
                .fluidOutputs(Latex.getFluid(200))
                .buildAndRegister();

        EXTRACTOR_RECIPES.recipeBuilder().duration(300).EUt(2)
                .inputs(new ItemStack(MetaBlocks.RUBBER_SAPLING))
                .fluidOutputs(Latex.getFluid(200))
                .buildAndRegister();

        EXTRACTOR_RECIPES.recipeBuilder().duration(150).EUt(2)
                .inputs(new ItemStack(Items.SLIME_BALL))
                .fluidOutputs(Latex.getFluid(400))
                .buildAndRegister();
    }
}
