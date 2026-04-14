package meowmel.gtsteam.loader.recipes;

import gregtech.api.GTValues;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.api.util.GTUtility;
import gregtech.common.ConfigHolder;
import meowmel.gtsteam.api.recipes.GTSRecipeMaps;
import meowmel.gtsteam.common.block.GTSteamMetaBlocks;
import meowmel.gtsteam.common.block.blocks.BlockEvaporationBed;
import meowmel.gtsteam.common.block.blocks.BlockMultiblockCasing0;
import meowmel.gtsteam.common.block.blocks.BlockMultiblockCasing1;
import meowmel.gtsteam.common.item.storageupdate.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static gregtech.api.GTValues.VA;
import static gregtech.api.recipes.RecipeMaps.MIXER_RECIPES;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static meowmel.gtsteam.api.recipes.GTSRecipeMaps.EVAPORATION_RECIPES;
import static meowmel.gtsteam.api.recipes.GTSRecipeMaps.LAVA_FURNACE_RECIPES;
import static meowmel.gtsteam.api.unification.GTSteamMaterials.GalvanizedSteel;
import static meowmel.gtsteam.api.unification.GTSteamMaterials.SealedWood;
import static meowmel.gtsteam.common.block.GTSteamMetaBlocks.blockEvaporationBed;
import static meowmel.gtsteam.common.block.GTSteamMetaBlocks.blockFireboxCasing0;
import static meowmel.gtsteam.common.block.blocks.BlockFireboxCasing0.FireboxCasingType.FLUID_FIREBOX;
import static meowmel.gtsteam.common.block.blocks.BlockFireboxCasing0.FireboxCasingType.ITEM_FIREBOX;
import static net.minecraft.init.Blocks.DIRT;

public class MiscRecipes {
    public static void init() {
        MachineRecipes();
        CasingRecipes();
        LavaFurnaceRecipes();
        EvaporationRecipes();
        ItemRecipes();
    }


    private static void ItemRecipes() {
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.plate, Materials.Steel, 4)
                .input(OrePrefix.wireFine, Materials.RedAlloy, 16)
                .input(OrePrefix.gear, Materials.WroughtIron, 2)
                .fluidInputs(Materials.SolderingAlloy.getFluid(144))
                .outputs(new ItemStack(ModItems.STORAGE_UPGRADE_TIER_1))
                .duration(200)
                .EUt(30)
                .buildAndRegister();

        // Advanced Storage Upgrade - MV Tier (120 EU/t)
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.plate, Materials.Aluminium, 4)
                .input(OrePrefix.wireFine, Materials.Aluminium, 16)
                .input(OrePrefix.plate, Materials.RoseGold, 4)
                .input(OrePrefix.gear, Materials.Steel, 2)
                .fluidInputs(Materials.SolderingAlloy.getFluid(288))
                .outputs(new ItemStack(ModItems.STORAGE_UPGRADE_TIER_2))
                .duration(400)
                .EUt(120)
                .buildAndRegister();

        // Elite Storage Upgrade - HV Tier (480 EU/t)
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.plate, Materials.StainlessSteel, 4)
                .input(OrePrefix.wireFine, Materials.Electrum, 8)
                .input(OrePrefix.gear, Materials.StainlessSteel, 2)
                .fluidInputs(Materials.SolderingAlloy.getFluid(576))
                .outputs(new ItemStack(ModItems.STORAGE_UPGRADE_TIER_3))
                .duration(600)
                .EUt(480)
                .buildAndRegister();

        // Void Upgrade - MV Tier (120 EU/t)
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(OrePrefix.plate, Materials.Iron, 4)
                .input(OrePrefix.wireFine, Materials.Copper, 8)
                .input(Items.GLASS_BOTTLE, 1)
                .input(OrePrefix.gear, Materials.Bronze, 1)
                .fluidInputs(Materials.SolderingAlloy.getFluid(144))
                .outputs(new ItemStack(ModItems.VOID_UPGRADE))
                .duration(300)
                .EUt(120)
                .buildAndRegister();
    }

    private static void LavaFurnaceRecipes() {

        LAVA_FURNACE_RECIPES.recipeBuilder()
                .input(dust, Stone)
                .fluidOutputs(Lava.getFluid(1000))
                .duration(1000)
                .Heat(10)
                .Temperature(473)
                .buildAndRegister();

        LAVA_FURNACE_RECIPES.recipeBuilder()
                .input("stoneCobble")
                .fluidOutputs(Lava.getFluid(1000))
                .duration(1200)
                .Heat(10)
                .Temperature(473)
                .buildAndRegister();

        LAVA_FURNACE_RECIPES.recipeBuilder()
                .input("stoneSmooth")
                .fluidOutputs(Lava.getFluid(1000))
                .duration(1200)
                .Heat(10)
                .Temperature(473)
                .buildAndRegister();
    }

    private static void EvaporationRecipes() {
        EVAPORATION_RECIPES.recipeBuilder()
                .fluidInputs(SaltWater.getFluid(2000))
                .chancedOutput(dust, Salt, 5000, 500)
                .chancedOutput(dust, Salt, 5000, 500)
                .chancedOutput(dust, Salt, 5000, 500)
                .chancedOutput(dust, Salt, 5000, 500)
                .duration(200)
                .Heat(30)
                .Temperature(573)
                .buildAndRegister();
    }

    private static void CasingRecipes() {
        ModHandler.addShapedRecipe(true, "reinforced_treated_wood_wall", GTSteamMetaBlocks.blockMultiblockCasing0.getItemVariant(BlockMultiblockCasing0.CasingType.REINFORCED_TREATED_WOOD_WALL),
                "PhP", "QFQ", "PwP",
                'P', new UnificationEntry(plate, TreatedWood),
                'Q', new UnificationEntry(plate, Steel),
                'F', new UnificationEntry(frameGt, TreatedWood));

        ModHandler.addShapedRecipe(true, "reinforced_treated_wood_bottom", GTSteamMetaBlocks.blockMultiblockCasing0.getItemVariant(BlockMultiblockCasing0.CasingType.REINFORCED_TREATED_WOOD_BOTTOM),
                "PhP", "QFQ", "PwP",
                'P', new UnificationEntry(plate, TreatedWood),
                'Q', new UnificationEntry(stick, Iron),
                'F', new UnificationEntry(frameGt, Steel));

        ModHandler.addShapedRecipe(true, "sealed_wood_wall", GTSteamMetaBlocks.blockMultiblockCasing0.getItemVariant(BlockMultiblockCasing0.CasingType.SEALED_WOOD_WALL),
                "PhP", "PFP", "PwP",
                'P', new UnificationEntry(plate, SealedWood),
                'F', new UnificationEntry(frameGt, TreatedWood));

        ModHandler.addShapedRecipe(true, "sealed_wood_bottom", GTSteamMetaBlocks.blockMultiblockCasing0.getItemVariant(BlockMultiblockCasing0.CasingType.SEALED_WOOD_BOTTOM),
                "PQP", "hFw", "PQP",
                'P', new UnificationEntry(plate, SealedWood),
                'Q', new UnificationEntry(screw, Iron),
                'F', new UnificationEntry(frameGt, Iron));

        //蓄水库外壁
        ModHandler.addShapedRecipe(true, "tank_wall", GTSteamMetaBlocks.blockMultiblockCasing0.getItemVariant(BlockMultiblockCasing0.CasingType.TANK_WALL),
                "PhP", "QFQ", "PwP",
                'P', new UnificationEntry(plate, Rubber),
                'Q', new UnificationEntry(screw, Iron),
                'F', new UnificationEntry(frameGt, Iron));

        //低压锅炉外壁
        ModHandler.addShapedRecipe(true, "low_pressure_tank", GTSteamMetaBlocks.blockMultiblockCasing1.getItemVariant(BlockMultiblockCasing1.CasingType.LOW_PRESSURE_TANK),
                "PhP", "QFQ", "PwP",
                'P', new UnificationEntry(screw, Iron),
                'Q', new UnificationEntry(plate, Steel),
                'F', new UnificationEntry(frameGt, Steel));

        ModHandler.addShapedRecipe(true, "high_pressure_tank", GTSteamMetaBlocks.blockMultiblockCasing1.getItemVariant(BlockMultiblockCasing1.CasingType.HIGH_PRESSURE_TANK),
                "PhP", "QFQ", "PwP",
                'P', new UnificationEntry(screw, Iron),
                'Q', new UnificationEntry(plate, GalvanizedSteel),
                'F', new UnificationEntry(frameGt, GalvanizedSteel));

        //太阳能集热器
        ModHandler.addShapedRecipe(true, "solar_collector", GTSteamMetaBlocks.blockMultiblockCasing1.getItemVariant(BlockMultiblockCasing1.CasingType.SOLAR_COLLECTOR),
                "PhP", "QFQ", "PwP",
                'P', new UnificationEntry(screw, Iron),
                'Q', new UnificationEntry(round, Tin),
                'F', new UnificationEntry(pipeNormalFluid, Copper));

        ModHandler.addShapedRecipe(true, "item_firebox",
                GTUtility.copy(ConfigHolder.recipes.casingsPerCraft, blockFireboxCasing0.getItemVariant(ITEM_FIREBOX)),
                "PSP", "SFS", "PSP",
                'P', new UnificationEntry(gearSmall, Steel),
                'F', new UnificationEntry(OrePrefix.frameGt, GalvanizedSteel),
                'S', new UnificationEntry(OrePrefix.stick, Steel));

        ModHandler.addShapedRecipe(true, "fluid_firebox",
                GTUtility.copy(ConfigHolder.recipes.casingsPerCraft, blockFireboxCasing0.getItemVariant(FLUID_FIREBOX)),
                "PSP", "SFS", "PSP",
                'P', new UnificationEntry(pipeSmallFluid, Steel),
                'F', new UnificationEntry(OrePrefix.frameGt, GalvanizedSteel),
                'S', new UnificationEntry(OrePrefix.stick, Steel));
    }

    private static void MachineRecipes() {
        MIXER_RECIPES.recipeBuilder()
                .input(DIRT, 1)
                .input(dust, Stone, 2)
                .input(dust, SiliconDioxide, 2)
                .circuitMeta(5)
                .output(blockEvaporationBed.getState(BlockEvaporationBed.EvaporationBedType.DIRT).getBlock())
                .EUt(VA[GTValues.ULV])
                .duration(200)
                .buildAndRegister();

        ModHandler.addShapedRecipe(true, "evaporation_bed_dirt",
                blockEvaporationBed.getItemVariant(BlockEvaporationBed.EvaporationBedType.DIRT),
                " S ", "IDI", " S ",
                'D', DIRT,
                'I', new UnificationEntry(OrePrefix.dust, SiliconDioxide),
                'S', new UnificationEntry(OrePrefix.dust, Stone));
    }
}
