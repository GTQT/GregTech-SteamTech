package meowmel.gtsteam.loader.recipes.chain;

import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.stack.UnificationEntry;
import meowmel.gtsteam.common.item.GTSMetaitems;

import static gregtech.api.GTValues.LV;
import static gregtech.api.GTValues.VA;
import static gregtech.api.recipes.RecipeMaps.ASSEMBLER_RECIPES;
import static gregtech.api.recipes.RecipeMaps.CHEMICAL_BATH_RECIPES;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.common.items.MetaItems.*;
import static meowmel.gtsteam.api.recipes.GTSRecipeMaps.ELECTRONIC_PROCESSOR_RECIPES;
import static meowmel.gtsteam.api.recipes.GTSRecipeMaps.HEAT_CHEMICAL_RECIPES;
import static meowmel.gtsteam.api.unification.GTSteamMaterials.SealedWood;
import static meowmel.gtsteam.common.item.GTSMetaitems.VACUUM_TUBE_COMPONENTS;
import static net.minecraft.init.Items.PAPER;
import static net.minecraftforge.fml.common.Loader.isModLoaded;

public class ULVCircuitChain {
    public static void init() {
        //覆膜电路基板
        //原：木板+粘性树脂
        //改：杂酚油 + 木板= 防腐木
        //改：防腐木 + 粘性树脂 = 覆膜电路基板

        // 封蜡木
        CHEMICAL_BATH_RECIPES.recipeBuilder()
                .input(plate, Wood)
                .output(plate, SealedWood)
                .fluidInputs(SeedOil.getFluid(120))
                .duration(100)
                .EUt(8)
                .buildAndRegister();

        CHEMICAL_BATH_RECIPES.recipeBuilder()
                .input(frameGt, Wood)
                .output(frameGt, SealedWood)
                .fluidInputs(SeedOil.getFluid(240))
                .duration(100)
                .EUt(8)
                .buildAndRegister();

        //LV电路板
        //原：合成台
        //改：热电子加工站（手搓电路配方改到这里+真空管）
        ModHandler.removeRecipeByName("gregtech:coated_board");
        ModHandler.removeRecipeByName("gregtech:coated_board_1x");

        ModHandler.addShapedRecipe("coated_board", COATED_BOARD.getStackForm(3),
                "RRR", "PPP", "RRR",
                'R', STICKY_RESIN.getStackForm(),
                'P', new UnificationEntry(plate, SealedWood));

        ModHandler.addShapelessRecipe("coated_board_1x", COATED_BOARD.getStackForm(),
                new UnificationEntry(plate, SealedWood),
                STICKY_RESIN.getStackForm(),
                STICKY_RESIN.getStackForm());

        ModHandler.removeRecipeByName("gregtech:basic_circuit_board");

        HEAT_CHEMICAL_RECIPES.recipeBuilder()
                .input(foil, Copper, 4)
                .input(COATED_BOARD)
                .fluidInputs(Glue.getFluid(100))
                .output(BASIC_CIRCUIT_BOARD)
                .duration(200)
                .Heat(20)
                .Temperature(498)
                .buildAndRegister();

        // 真空管组件
        ModHandler.removeRecipeByName("gregtech:vacuum_tube");

        // ULV 时代
        ELECTRONIC_PROCESSOR_RECIPES.recipeBuilder()
                .input(GLASS_TUBE)
                .input(bolt, Steel, 2)
                .input(foil, RedAlloy)
                .input(wireGtSingle, Copper, 2)
                .output(VACUUM_TUBE_COMPONENTS, 1)
                .duration(100)
                .Heat(15)
                .Temperature(473)
                .buildAndRegister();

        // LV 时代
        ASSEMBLER_RECIPES.recipeBuilder()
                .input(GLASS_TUBE)
                .input(bolt, Steel, 2)
                .input(wireGtSingle, Copper, 2)
                .fluidInputs(RedAlloy.getFluid(144))
                .output(VACUUM_TUBE_COMPONENTS, 2)
                .duration(200)
                .EUt(VA[LV])
                .buildAndRegister();

        ASSEMBLER_RECIPES.recipeBuilder()
                .input(GLASS_TUBE)
                .input(bolt, Steel, 2)
                .input(wireGtSingle, AnnealedCopper, 2)
                .fluidInputs(RedAlloy.getFluid(144))
                .output(VACUUM_TUBE_COMPONENTS, 4)
                .duration(200)
                .EUt(VA[LV])
                .buildAndRegister();

        // 真空管组件 - > 真空管
        // 在GTQTCore用真空泵
        // 这里热电子组装机
        if (!isModLoaded("gtqtcore")) {
            ELECTRONIC_PROCESSOR_RECIPES.recipeBuilder()
                    .input(GTSMetaitems.VACUUM_TUBE_COMPONENTS)
                    .input(ring, Rubber, 1)
                    .input(wireFine, Copper, 2)
                    .output(VACUUM_TUBE)
                    .duration(200)
                    .Heat(15)
                    .Temperature(473)
                    .buildAndRegister();

            ELECTRONIC_PROCESSOR_RECIPES.recipeBuilder()
                    .input(GTSMetaitems.VACUUM_TUBE_COMPONENTS)
                    .input(ring, Rubber, 1)
                    .input(wireFine, AnnealedCopper, 2)
                    .output(VACUUM_TUBE, 2)
                    .duration(200)
                    .Heat(15)
                    .Temperature(473)
                    .buildAndRegister();
        }

        // LV 电路
        ModHandler.removeRecipeByName("gregtech:electronic_circuit_lv");
        ELECTRONIC_PROCESSOR_RECIPES.recipeBuilder()
                .Heat(30)
                .Temperature(573)
                .duration(200)
                .input(BASIC_CIRCUIT_BOARD)
                .input(plate, Steel)
                .input(VACUUM_TUBE, 2)
                .input(RESISTOR, 2)
                .input(cableGtSingle, RedAlloy, 3)
                .output(ELECTRONIC_CIRCUIT_LV)
                .buildAndRegister();

        // MV 电路
        ModHandler.removeRecipeByName("gregtech:electronic_circuit_mv");
        ELECTRONIC_PROCESSOR_RECIPES.recipeBuilder()
                .Heat(120)
                .Temperature(773)
                .duration(300)
                .input(GOOD_CIRCUIT_BOARD)
                .input(plate, Steel)
                .input(ELECTRONIC_CIRCUIT_LV, 3)
                .input(DIODE, 2)
                .input(wireGtSingle, Copper, 2)
                .output(ELECTRONIC_CIRCUIT_MV)
                .buildAndRegister();

        // 电阻器修改RESISTOR
        // 原：工作台
        // 改：热电子加工站
        ModHandler.removeRecipeByName("gregtech:resistor_wire");
        ModHandler.removeRecipeByName("gregtech:resistor_wire_fine");
        ModHandler.removeRecipeByName("gregtech:resistor_wire_charcoal");
        ModHandler.removeRecipeByName("gregtech:resistor_wire_fine_charcoal");
        ModHandler.removeRecipeByName("gregtech:resistor_wire_carbon");
        ModHandler.removeRecipeByName("gregtech:resistor_wire_fine_carbon");

        ELECTRONIC_PROCESSOR_RECIPES.recipeBuilder()
                .input(PAPER, 2)
                .input(STICKY_RESIN, 2)
                .input(dust, Coal)
                .input(wireFine, Copper, 2)
                .output(RESISTOR, 2)
                .duration(240)
                .Heat(15)
                .Temperature(473)
                .buildAndRegister();

        ELECTRONIC_PROCESSOR_RECIPES.recipeBuilder()
                .input(PAPER, 2)
                .input(STICKY_RESIN, 2)
                .input(dust, Charcoal)
                .input(wireFine, Copper, 2)
                .output(RESISTOR, 2)
                .duration(240)
                .Heat(15)
                .Temperature(473)
                .buildAndRegister();

        ELECTRONIC_PROCESSOR_RECIPES.recipeBuilder()
                .input(PAPER, 2)
                .input(STICKY_RESIN, 2)
                .input(dust, Carbon)
                .input(wireFine, Copper, 2)
                .output(RESISTOR, 2)
                .duration(240)
                .Heat(15)
                .Temperature(473)
                .buildAndRegister();
    }
}
