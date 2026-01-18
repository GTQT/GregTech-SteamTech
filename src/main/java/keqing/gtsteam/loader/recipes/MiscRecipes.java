package keqing.gtsteam.loader.recipes;

import gregtech.api.GTValues;
import gregtech.api.items.OreDictNames;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.api.util.GTUtility;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import gtqt.api.util.recipeUtility;
import keqing.gtsteam.common.block.GTSteamMetaBlocks;
import keqing.gtsteam.common.block.blocks.BlockMultiblockCasing0;
import keqing.gtsteam.common.block.blocks.BlockMultiblockCasing1;
import keqing.gtsteam.common.item.storageupdate.ModItems;
import keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static gregtech.api.GTValues.V;
import static gregtech.api.unification.material.MarkerMaterials.Tier.LV;
import static gregtech.api.unification.material.MarkerMaterials.Tier.ULV;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.common.blocks.BlockFireboxCasing.FireboxCasingType.BRONZE_FIREBOX;
import static gregtech.common.blocks.BlockFireboxCasing.FireboxCasingType.STEEL_FIREBOX;
import static gregtech.common.blocks.BlockMetalCasing.MetalCasingType.BRONZE_BRICKS;
import static gregtech.common.blocks.BlockMetalCasing.MetalCasingType.STEEL_SOLID;
import static gregtech.common.metatileentities.MetaTileEntities.*;
import static keqing.gtsteam.api.recipes.GTSRecipeMaps.*;
import static keqing.gtsteam.api.unification.GTSteamMaterials.GalvanizedSteel;
import static keqing.gtsteam.common.block.GTSteamMetaBlocks.blockFireboxCasing0;
import static keqing.gtsteam.common.block.GTSteamMetaBlocks.blockMultiblockCasing1;
import static keqing.gtsteam.common.block.blocks.BlockFireboxCasing0.FireboxCasingType.FLUID_FIREBOX;
import static keqing.gtsteam.common.block.blocks.BlockFireboxCasing0.FireboxCasingType.ITEM_FIREBOX;
import static keqing.gtsteam.common.block.blocks.BlockMultiblockCasing0.CasingType.GALVANIZED_PORCELAIN_TILES;
import static keqing.gtsteam.common.block.blocks.BlockMultiblockCasing1.CasingType.SOLAR_COLLECTOR;
import static keqing.gtsteam.common.item.GTSMetaitems.*;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.*;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.ALLOY_SMELTER;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.BENDER;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.BREWERY;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.CENTRIFUGE;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.CHEMICAL_BATH;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.COMBUSTION_GENERATOR;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.COMPRESSOR;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.CUTTER;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.ELECTRIC_FURNACE;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.EXTRACTOR;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.FERMENTER;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.FORGE_HAMMER;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.LATHE;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.MACERATOR;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.MIXER;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.ORE_WASHER;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.PACKER;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.SEMI_FLUID_GENERATOR;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.SIFTER;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.STEAM_TURBINE;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.WIREMILL;

public class MiscRecipes {
    public static void init() {
        MachineRecipes();
        GenerateRecipes();
        CasingRecipes();
        LavaFurnaceRecipes();
        ItemRecipes();
    }

    private static void ItemRecipes() {
        //  ULV
        ModHandler.addShapedRecipe(true, "electric_motor.ulv", ELECTRIC_MOTOR_ULV.getStackForm(),
                "CWR", "WMW", "RWC",
                'C', new UnificationEntry(pipeTinyFluid, Bronze),
                'W', new UnificationEntry(wireGtSingle, Lead),
                'M', new UnificationEntry(stick, IronMagnetic),
                'R', new UnificationEntry(stick, Potin));

        //  ULV
        ModHandler.addShapedRecipe(true, "conveyor_module.ulv", CONVEYOR_MODULE_ULV.getStackForm(),
                "RRR", "MCM", "RRR",
                'R', "wool",
                'M', ELECTRIC_MOTOR_ULV.getStackForm(),
                'C', new UnificationEntry(pipeTinyFluid, Bronze));

        //  ULV
        ModHandler.addShapedRecipe(true, "electric_piston.ulv", ELECTRIC_PISTON_ULV.getStackForm(),
                "PPP", "CRR", "CMG",
                'P', new UnificationEntry(plate, Potin),
                'C', new UnificationEntry(pipeTinyFluid, Bronze),
                'R', new UnificationEntry(stick, WroughtIron),
                'M', ELECTRIC_MOTOR_ULV.getStackForm(),
                'G', new UnificationEntry(gearSmall, Iron));

        //  ULV
        ModHandler.addShapedRecipe(true, "robot_arm.ulv", ROBOT_ARM_ULV.getStackForm(),
                "CCC", "MRM", "PXR",
                'C', new UnificationEntry(pipeTinyFluid, Bronze),
                'M', ELECTRIC_MOTOR_ULV.getStackForm(),
                'R', new UnificationEntry(stick, Potin),
                'P', ELECTRIC_PISTON_ULV.getStackForm(),
                'X', new UnificationEntry(circuit, ULV));

        //  ULV
        ModHandler.addShapedRecipe(true, "electric_pump.ulv", ELECTRIC_PUMP_ULV.getStackForm(),
                "SXR", "dPw", "RMC",
                'S', new UnificationEntry(screw, Potin),
                'X', new UnificationEntry(rotor, Iron),
                'P', new UnificationEntry(pipeNormalFluid, Copper),
                'R', "wool",
                'C', new UnificationEntry(pipeTinyFluid, Bronze),
                'M', ELECTRIC_MOTOR_ULV.getStackForm());


        //  ULV
        ModHandler.addShapedRecipe(true, "emitter.ulv", EMITTER_ULV.getStackForm(),
                "CRX", "RGR", "XRC",
                'R', new UnificationEntry(stick, TinAlloy),
                'C', new UnificationEntry(pipeTinyFluid, Bronze),
                'G', new UnificationEntry(gem, Sapphire),
                'X', new UnificationEntry(circuit, ULV));


        //  ULV
        ModHandler.addShapedRecipe(true, "sensor.ulv", SENSOR_ULV.getStackForm(),
                "P G", "PR ", "XPP",
                'P', new UnificationEntry(plate, Potin),
                'R', new UnificationEntry(stick, TinAlloy),
                'G', new UnificationEntry(gem, Sapphire),
                'X', new UnificationEntry(circuit, ULV));


        //  ULV
        ModHandler.addShapedRecipe(true, "field_generator.ulv", FIELD_GENERATOR_ULV.getStackForm(),
                "WPW", "XGX", "WPW",
                'W', new UnificationEntry(pipeLargeFluid, Lead),
                'P', new UnificationEntry(plate, Potin),
                'G', new UnificationEntry(gem, Ruby),
                'X', new UnificationEntry(circuit, ULV));

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
                .EUt(V[GTValues.ULV])
                .buildAndRegister();

        LAVA_FURNACE_RECIPES.recipeBuilder()
                .input("stoneCobble")
                .fluidOutputs(Lava.getFluid(1000))
                .duration(1200)
                .EUt(V[GTValues.ULV])
                .buildAndRegister();

        LAVA_FURNACE_RECIPES.recipeBuilder()
                .input("stoneSmooth")
                .fluidOutputs(Lava.getFluid(1000))
                .duration(1200)
                .EUt(V[GTValues.ULV])
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

    private static void GenerateRecipes() {
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

        PRIMITIVE_COMBUSTION_GENERATOR_FUELS.recipeBuilder()
                .fluidInputs(NaturalGas.getFluid(8))
                .duration(20)
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

    private static void MachineRecipes() {
        //合金窑
        ModHandler.addShapedRecipe(true, "alloy_kiln", GTSteamMetaTileEntities.ALLOY_KILN.getStackForm(),
                "PIP", "IwI", "PIP",
                'P', GTSteamMetaBlocks.blockMultiblockCasing0.getItemVariant(GALVANIZED_PORCELAIN_TILES),
                'I', new UnificationEntry(OrePrefix.plate, Iron));

        ModHandler.addShapedRecipe(true, "alloy_kiln_import_hatch", ALLOY_KILN_IMPORT_HATCH.getStackForm(),
                "wh", "CB",
                'C', Blocks.CHEST,
                'B', GTSteamMetaBlocks.blockMultiblockCasing0.getItemVariant(GALVANIZED_PORCELAIN_TILES));

        ModHandler.addShapedRecipe(true, "alloy_kiln_export_hatch", ALLOY_KILN_EXPORT_HATCH.getStackForm(),
                "hw", "CB",
                'C', Blocks.CHEST,
                'B', GTSteamMetaBlocks.blockMultiblockCasing0.getItemVariant(GALVANIZED_PORCELAIN_TILES));

        //基高级土高炉
        ModHandler.addShapedRecipe(true, "advance_primitive_blast_furnace",
                ADVANCE_PRIMITIVE_BLAST_FURNACE.getStackForm(), "hRS", "PBR", "dRS", 'R',
                new UnificationEntry(OrePrefix.stick, Lead), 'S',
                new UnificationEntry(OrePrefix.screw, Materials.Lead), 'P',
                new UnificationEntry(OrePrefix.plate, Materials.Lead), 'B',
                PRIMITIVE_BLAST_FURNACE.getStackForm());

        //高级焦炉
        ModHandler.addShapedRecipe(true, "advanced_coke_oven",
                ADVANCED_COKE_OVEN.getStackForm(), "hRS", "PBR", "dRS", 'R',
                new UnificationEntry(OrePrefix.stick, Lead), 'S',
                new UnificationEntry(OrePrefix.screw, Materials.Lead), 'P',
                new UnificationEntry(OrePrefix.plate, Materials.Lead), 'B',
                COKE_OVEN.getStackForm());

        //工业土高炉
        ModHandler.addShapedRecipe(true, "industrial_primitive_blast_furnace",
                INDUSTRIAL_PRIMITIVE_BLAST_FURNACE.getStackForm(), "PPP", "CFC", "BBB",
                'C', new UnificationEntry(circuit, LV),
                'P', new UnificationEntry(plate, Steel),
                'F', new UnificationEntry(frameGt, Steel),
                'B', PRIMITIVE_BLAST_FURNACE.getStackForm());

        //原始化学反应釜
        ModHandler.addShapedRecipe(true, "primitive_chemical_reactor", PRIMITIVE_CHEMICAL_REACTOR.getStackForm(),
                "PRP", "sQh", "PSP",
                'P', new UnificationEntry(plate, TreatedWood),
                'Q', new UnificationEntry(pipeLargeFluid, TreatedWood),
                'R', new UnificationEntry(rotor, Steel),
                'S', new UnificationEntry(screw, Steel));

        //大型储罐
        ModHandler.addShapedRecipe(true, "large_fluid_tank", LARGE_FLUID_TANK.getStackForm(),
                "PPP", "CFC", "BBB",
                'C', new UnificationEntry(circuit, ULV),
                'P', new UnificationEntry(plate, Steel),
                'F', new UnificationEntry(frameGt, Steel),
                'B', STEEL_TANK.getStackForm());

        if (ConfigHolder.machines.steelSteamMultiblocks) {
            ModHandler.addShapedRecipe(true, "steam_compressor", STEAM_COMPRESSOR.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Steel),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                    'M', MetaTileEntities.STEAM_COMPRESSOR_STEEL.getStackForm(),
                    'G', new UnificationEntry(circuit, LV));


            ModHandler.addShapedRecipe(true, "steam_extractor", STEAM_EXTRACTOR.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Steel),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                    'M', MetaTileEntities.STEAM_EXTRACTOR_STEEL.getStackForm(),
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_ore_washer", STEAM_ORE_WASHER.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Steel),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                    'M', ELECTRIC_PUMP_ULV,
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_hammer", STEAM_HAMMER.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Steel),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                    'M', MetaTileEntities.STEAM_HAMMER_STEEL.getStackForm(),
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_centrifuge", STEAM_CENTRIFUGE.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Steel),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                    'M', ELECTRIC_MOTOR_ULV,
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_mixer", STEAM_MIXER.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Steel),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                    'M', CONVEYOR_MODULE_ULV,
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_sifter", STEAM_SIFTER.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Steel),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                    'M', ROBOT_ARM_ULV,
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_lathe", STEAM_LATHE.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Steel),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                    'M', ELECTRIC_PISTON_ULV,
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_alloy_furnace", STEAM_ALLOY_FURNACE.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', MetaBlocks.BOILER_FIREBOX_CASING.getItemVariant(STEEL_FIREBOX),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                    'M', MetaTileEntities.STEAM_ALLOY_SMELTER_STEEL.getStackForm(),
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_lava_furnace", STEAM_LAVA_FURNACE.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', MetaBlocks.BOILER_FIREBOX_CASING.getItemVariant(STEEL_FIREBOX),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                    'M', STEAM_BOILER_LAVA_STEEL.getStackForm(),
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_bender", STEAM_BENDER.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Steel),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                    'M', ELECTRIC_PISTON_ULV,
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_wire_mill", STEAM_WIRE_MILL.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Steel),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                    'M', ELECTRIC_MOTOR_ULV,
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_fermentation_vat", STEAM_FERMENTATION_VAT.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Steel),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                    'M', ELECTRIC_PUMP_ULV,
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "bronze_multiblock_tank", BRONZE_TANK.getStackForm(), " R ",
                    "hCw", " R ", 'R', new UnificationEntry(OrePrefix.ring, Steel), 'C',
                    MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID));

            ModHandler.addShapedRecipe(true, "bronze_tank_valve", BRONZE_TANK_VALVE.getStackForm(), " R ",
                    "hCw", " O ", 'O', new UnificationEntry(OrePrefix.rotor, Materials.Steel), 'R',
                    new UnificationEntry(OrePrefix.ring, Materials.Steel), 'C',
                    MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID));
        } else {

            ModHandler.addShapedRecipe(true, "steam_compressor", STEAM_COMPRESSOR.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Bronze),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                    'M', STEAM_COMPRESSOR_BRONZE.getStackForm(),
                    'G', new UnificationEntry(circuit, LV));


            ModHandler.addShapedRecipe(true, "steam_extractor", STEAM_EXTRACTOR.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Bronze),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                    'M', STEAM_EXTRACTOR_BRONZE.getStackForm(),
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_ore_washer", STEAM_ORE_WASHER.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Bronze),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                    'M', ELECTRIC_PUMP_ULV,
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_hammer", STEAM_HAMMER.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Bronze),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                    'M', MetaTileEntities.STEAM_HAMMER_BRONZE.getStackForm(),
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_centrifuge", STEAM_CENTRIFUGE.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Bronze),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                    'M', ELECTRIC_MOTOR_ULV,
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_mixer", STEAM_MIXER.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Bronze),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                    'M', CONVEYOR_MODULE_ULV,
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_sifter", STEAM_SIFTER.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Bronze),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                    'M', ROBOT_ARM_ULV,
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_lathe", STEAM_LATHE.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Bronze),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                    'M', ELECTRIC_PISTON_ULV,
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_alloy_furnace", STEAM_ALLOY_FURNACE.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', MetaBlocks.BOILER_FIREBOX_CASING.getItemVariant(BRONZE_FIREBOX),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                    'M', MetaTileEntities.STEAM_ALLOY_SMELTER_BRONZE.getStackForm(),
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_lava_furnace", STEAM_LAVA_FURNACE.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', MetaBlocks.BOILER_FIREBOX_CASING.getItemVariant(BRONZE_FIREBOX),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                    'M', STEAM_BOILER_LAVA_BRONZE.getStackForm(),
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_bender", STEAM_BENDER.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Steel),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                    'M', ELECTRIC_PISTON_ULV,
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_wire_mill", STEAM_WIRE_MILL.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Steel),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                    'M', ELECTRIC_MOTOR_ULV,
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "steam_fermentation_vat", STEAM_FERMENTATION_VAT.getStackForm(),
                    "CGC", "FMF", "CGC",
                    'F', new UnificationEntry(frameGt, Steel),
                    'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                    'M', ELECTRIC_PUMP_ULV,
                    'G', new UnificationEntry(circuit, LV));

            ModHandler.addShapedRecipe(true, "bronze_multiblock_tank", BRONZE_TANK.getStackForm(), " R ",
                    "hCw", " R ", 'R', new UnificationEntry(OrePrefix.ring, Materials.Bronze), 'C',
                    MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS));

            ModHandler.addShapedRecipe(true, "bronze_tank_valve", BRONZE_TANK_VALVE.getStackForm(), " R ",
                    "hCw", " O ", 'O', new UnificationEntry(OrePrefix.rotor, Materials.Bronze), 'R',
                    new UnificationEntry(OrePrefix.ring, Materials.Bronze), 'C',
                    MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS));

        }

        ModHandler.addShapedRecipe(true, "large_steam_tank", LARGE_STEAM_TANK.getStackForm(),
                "PRP", "hCw", "PRP",
                'R', new UnificationEntry(screw, Iron),
                'P', new UnificationEntry(pipeNormalFluid, Materials.Bronze),
                'C', BRONZE_TANK.getStackForm());

        //锅炉
        ModHandler.addShapedRecipe(true, "steam_solar_boiler", STEAM_SOLAR_BOILER.getStackForm(),
                "PSP", "SAS", "PSP",
                'P', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Steel),
                'S', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.ULV),
                'A', blockMultiblockCasing1.getItemVariant(SOLAR_COLLECTOR));

        ModHandler.addShapedRecipe(true, "low_pressure_solid_boiler", LOW_PRESSURE_SOLID_BOILER.getStackForm(),
                "PSP", "SAS", "PSP",
                'P',new UnificationEntry(plate, Steel),
                'S', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.ULV),
                'A', blockFireboxCasing0.getItemVariant(ITEM_FIREBOX));

        ModHandler.addShapedRecipe(true, "high_pressure_solid_boiler", HIGH_PRESSURE_SOLID_BOILER.getStackForm(),
                "PSP", "SAS", "PSP",
                'P', new UnificationEntry(plate, GalvanizedSteel),
                'S', ELECTRIC_PUMP_ULV,
                'A', LOW_PRESSURE_SOLID_BOILER.getStackForm());

        ModHandler.addShapedRecipe(true, "low_pressure_fluid_boiler", LOW_PRESSURE_FLUID_BOILER.getStackForm(),
                "PSP", "SAS", "PSP",
                'P', new UnificationEntry(plate, Steel),
                'S', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.ULV),
                'A', blockFireboxCasing0.getItemVariant(FLUID_FIREBOX));

        ModHandler.addShapedRecipe(true, "high_pressure_fluid_boiler", HIGH_PRESSURE_FLUID_BOILER.getStackForm(),
                "PSP", "SAS", "PSP",
                'P', new UnificationEntry(plate, GalvanizedSteel),
                'S', ELECTRIC_PUMP_ULV,
                'A', LOW_PRESSURE_FLUID_BOILER.getStackForm());

        // MACHINES
        ModHandler.addShapedRecipe(true, "alloy_smelter.ulv", ALLOY_SMELTER.getStackForm(),
                "ECE", "CMC", "WCW",
                'M', HULL[0].getStackForm(),
                'E', new UnificationEntry(circuit, ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy),
                'C', new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.Copper));

        ModHandler.addShapedRecipe(true, "bender.ulv", BENDER.getStackForm(),
                "PWP", "CMC", "EBE",
                'M', HULL[0].getStackForm(),
                'E', ELECTRIC_MOTOR_ULV,
                'P', ELECTRIC_PISTON_ULV,
                'C', new UnificationEntry(circuit, ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy),
                'B', new UnificationEntry(OrePrefix.plate, Materials.WroughtIron));

        ModHandler.addShapedRecipe(true, "compressor.ulv", COMPRESSOR.getStackForm()
                , " C ", "PMP", "WCW",
                'M', HULL[0].getStackForm(),
                'P', ELECTRIC_PISTON_ULV,
                'C', new UnificationEntry(circuit, ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy));

        ModHandler.addShapedRecipe(true, "cutter.ulv", CUTTER.getStackForm(),
                "WCG", "VMB", "CWE",
                'M', HULL[0].getStackForm(),
                'E', ELECTRIC_MOTOR_ULV,
                'V', CONVEYOR_MODULE_ULV,
                'C', new UnificationEntry(circuit, ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy),
                'G', new ItemStack(Blocks.GLASS, 1),
                'B', new UnificationEntry(OrePrefix.toolHeadBuzzSaw, Materials.Bronze));

        ModHandler.addShapedRecipe(true, "electric_furnace.ulv", ELECTRIC_FURNACE.getStackForm(),
                "ECE", "CMC", "WCW",
                'M', HULL[0].getStackForm(),
                'E', new UnificationEntry(circuit, ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy),
                'C', new UnificationEntry(OrePrefix.wireGtDouble, Materials.Copper));

        ModHandler.addShapedRecipe(true, "extractor.ulv", EXTRACTOR.getStackForm(),
                "GCG", "EMP", "WCW",
                'M', HULL[0].getStackForm(),
                'E', ELECTRIC_PISTON_ULV,
                'P', ELECTRIC_PUMP_ULV,
                'C', new UnificationEntry(circuit, ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy),
                'G', new ItemStack(Blocks.GLASS, 1));

        ModHandler.addShapedRecipe(true, "lathe.ulv", LATHE.getStackForm(),
                "WCW", "EMD", "CWP",
                'M', HULL[0].getStackForm(),
                'E', ELECTRIC_MOTOR_ULV,
                'P', ELECTRIC_PISTON_ULV,
                'C', new UnificationEntry(circuit, ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy),
                'D', new UnificationEntry(OrePrefix.gem, Materials.Diamond));

        ModHandler.addShapedRecipe(true, "macerator.ulv", MACERATOR.getStackForm(),
                "PEG", "WWM", "CCW",
                'M', HULL[0].getStackForm(),
                'E', ELECTRIC_MOTOR_ULV,
                'P', ELECTRIC_PISTON_ULV,
                'C', new UnificationEntry(circuit, ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy),
                'G', new UnificationEntry(OrePrefix.gem, Materials.Diamond));


        ModHandler.addShapedRecipe(true, "wiremill.ulv", WIREMILL.getStackForm(),
                "EWE", "CMC", "EWE",
                'M', HULL[0].getStackForm(),
                'E', ELECTRIC_MOTOR_ULV,
                'C', new UnificationEntry(circuit, ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy));

        ModHandler.addShapedRecipe(true, "centrifuge.ulv", CENTRIFUGE.getStackForm(),
                "CEC", "WMW", "CEC",
                'M', HULL[0].getStackForm(),
                'E', ELECTRIC_MOTOR_ULV,
                'C', new UnificationEntry(circuit, ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy));

        ModHandler.addShapedRecipe(true, "ore_washer.ulv", ORE_WASHER.getStackForm(),
                "RGR", "CEC", "WMW",
                'M', HULL[0].getStackForm(),
                'R', new UnificationEntry(OrePrefix.rotor, Materials.Tin),
                'E', ELECTRIC_MOTOR_ULV,
                'C', new UnificationEntry(circuit, ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy),
                'G', new ItemStack(Blocks.GLASS, 1));

        ModHandler.addShapedRecipe(true, "packer.ulv", PACKER.getStackForm(),
                "BCB", "RMV", "WCW",
                'M', HULL[0].getStackForm(),
                'R', ROBOT_ARM_ULV,
                'V', CONVEYOR_MODULE_ULV,
                'C', new UnificationEntry(circuit, ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy),
                'B', OreDictNames.chestWood);

        ModHandler.addShapedRecipe(true, "brewery.ulv", BREWERY.getStackForm(),
                "GPG", "WMW", "CBC",
                'M', HULL[0].getStackForm(),
                'P', ELECTRIC_PUMP_ULV,
                'B', new UnificationEntry(OrePrefix.stick, Materials.Blaze),
                'C', new UnificationEntry(circuit, ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy),
                'G', new ItemStack(Blocks.GLASS, 1));

        ModHandler.addShapedRecipe(true, "fermenter.ulv", FERMENTER.getStackForm(),
                "WPW", "GMG", "WCW",
                'M', HULL[0].getStackForm(),
                'P', ELECTRIC_PUMP_ULV,
                'C', new UnificationEntry(circuit, ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy),
                'G', new ItemStack(Blocks.GLASS, 1));

        ModHandler.addShapedRecipe(true, "chemical_bath.ulv", CHEMICAL_BATH.getStackForm(),
                "VGW", "PGV", "CMC",
                'M', HULL[0].getStackForm(),
                'P', ELECTRIC_PUMP_ULV,
                'V', CONVEYOR_MODULE_ULV,
                'C', new UnificationEntry(circuit, ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy),
                'G', new ItemStack(Blocks.GLASS, 1));

        ModHandler.addShapedRecipe(true, "mixer.ulv", MIXER.getStackForm(),
                "GRG", "GEG", "CMC",
                'M', HULL[0].getStackForm(),
                'E', ELECTRIC_MOTOR_ULV,
                'R', new UnificationEntry(OrePrefix.rotor, Materials.Tin),
                'C', new UnificationEntry(circuit, ULV),
                'G', new ItemStack(Blocks.GLASS, 1));

        ModHandler.addShapedRecipe(true, "forge_hammer.ulv", FORGE_HAMMER.getStackForm(),
                "WPW", "CMC", "WAW",
                'M', HULL[0].getStackForm(),
                'P', ELECTRIC_PISTON_ULV,
                'C', new UnificationEntry(circuit, ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy),
                'A', OreDictNames.craftingAnvil);

        ModHandler.addShapedRecipe(true, "sifter.ulv", SIFTER.getStackForm(),
                "WFW", "PMP", "CFC",
                'M', HULL[0].getStackForm(),
                'P', ELECTRIC_PISTON_ULV,
                'F', MetaItems.ITEM_FILTER,
                'C', new UnificationEntry(circuit, ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy));

        ModHandler.addShapedRecipe(true, "steam_turbine_ulv", STEAM_TURBINE.getStackForm(),
                "PCP", "RMR", "EWE",
                'M', MetaTileEntities.HULL[GTValues.ULV].getStackForm(),
                'E', ELECTRIC_MOTOR_ULV,
                'R', new UnificationEntry(OrePrefix.rotor, Materials.Tin),
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy),
                'P', new UnificationEntry(OrePrefix.pipeNormalFluid, Copper));

        ModHandler.addShapedRecipe(true, "diesel_generator_ulv", COMBUSTION_GENERATOR.getStackForm(),
                "PCP", "EME", "GWG",
                'M', MetaTileEntities.HULL[GTValues.ULV].getStackForm(),
                'P', ELECTRIC_PISTON_ULV,
                'E', ELECTRIC_MOTOR_ULV,
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, RedAlloy),
                'G', new UnificationEntry(OrePrefix.gear, WroughtIron));

        ModHandler.addShapedRecipe(true, "semi_fluid_generator_ulv", SEMI_FLUID_GENERATOR.getStackForm(),
                "PCP", "EME", "GWG",
                'M', MetaTileEntities.HULL[GTValues.ULV].getStackForm(),
                'P', ELECTRIC_PISTON_ULV,
                'E', ELECTRIC_PUMP_ULV,
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy),
                'G', new UnificationEntry(OrePrefix.gear, Materials.WroughtIron));
    }
}
