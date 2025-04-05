package keqing.gtsteam.loader.recipes;

import gregtech.api.GTValues;
import gregtech.api.items.OreDictNames;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import keqing.gtsteam.api.recipes.GTSRecipeMaps;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import static gregtech.api.GTValues.V;
import static gregtech.api.unification.material.MarkerMaterials.Tier.LV;
import static gregtech.api.unification.material.MarkerMaterials.Tier.ULV;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.common.blocks.BlockMetalCasing.MetalCasingType.BRONZE_BRICKS;
import static gregtech.common.metatileentities.MetaTileEntities.*;

import static keqing.gtsteam.api.recipes.GTSRecipeMaps.PRIMITIVE_COMBUSTION_GENERATOR_FUELS;
import static keqing.gtsteam.api.recipes.GTSRecipeMaps.PRIMITIVE_STEAM_TURBINE_FUELS;
import static keqing.gtsteam.common.item.GTSMetaitems.*;
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
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.SIFTER;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.STEAM_TURBINE;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.WIREMILL;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.*;

public class MiscRecipes {
    public static void init() {
        GTSRecipeMaps.STEAM_BLAST_FURNACE_RECIPES.recipeBuilder()
                .input(ingot, WroughtIron)
                .output(ingot, Steel)
                .duration(3600)
                .EUt(24)
                .buildAndRegister();

        MachineRecipes();
        GenerateRecipes();

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
                .fluidInputs(Biomass.getFluid(10))
                .duration(20)
                .EUt(V[GTValues.ULV])
                .buildAndRegister();
    }

    private static void MachineRecipes() {
        //合金窑
        ModHandler.addShapedRecipe(true, "alloy_kiln", ALLOY_KILN.getStackForm(),
                "PIP", "IwI", "PIP",
                'P', MetaBlocks.METAL_CASING.getItemVariant(BlockMetalCasing.MetalCasingType.PRIMITIVE_BRICKS),
                'I', new UnificationEntry(OrePrefix.plate, Iron));

        ModHandler.addShapedRecipe(true, "alloy_kiln_import_hatch", ALLOY_KILN_IMPORT_HATCH.getStackForm(),
                "wh", "CB",
                'C', Blocks.CHEST,
                'B', MetaBlocks.METAL_CASING.getItemVariant(BlockMetalCasing.MetalCasingType.PRIMITIVE_BRICKS));

        ModHandler.addShapedRecipe(true, "alloy_kiln_export_hatch", ALLOY_KILN_EXPORT_HATCH.getStackForm(),
                "hw", "CB",
                'C', Blocks.CHEST,
                'B', MetaBlocks.METAL_CASING.getItemVariant(BlockMetalCasing.MetalCasingType.PRIMITIVE_BRICKS));

        //工业土高炉
        ModHandler.addShapedRecipe(true, "industrial_primitive_blast_furnace",
                INDUSTRIAL_PRIMITIVE_BLAST_FURNACE.getStackForm(),
                "PPP", "CFC", "BBB",
                'C', new UnificationEntry(circuit, LV),
                'P', new UnificationEntry(plate, Steel),
                'F', new UnificationEntry(frameGt, Steel),
                'B', PRIMITIVE_BLAST_FURNACE.getStackForm());

        //高级焦炉
        ModHandler.addShapedRecipe(true, "advanced_coke_oven",
                ADVANCED_COKE_OVEN.getStackForm(),
                "PPP", "CFC", "BBB",
                'C', new UnificationEntry(circuit, LV),
                'P', new UnificationEntry(plate, Steel),
                'F', new UnificationEntry(frameGt, Steel),
                'B', COKE_OVEN.getStackForm());

        //大型原始水泵
        ModHandler.addShapedRecipe(true, "large_primitive_water_pump", WATER_PUMP.getStackForm(),
                "PPP", "CFC", "BBB",
                'C', new UnificationEntry(circuit, LV),
                'P', new UnificationEntry(plate, Steel),
                'F', new UnificationEntry(frameGt, Steel),
                'B', PRIMITIVE_WATER_PUMP.getStackForm());

        ModHandler.addShapedRecipe(true, "steam_compressor", STEAM_COMPRESSOR.getStackForm(),
                "GGG", "CFC", "PPP",
                'G', ELECTRIC_PISTON_ULV,
                'P', ELECTRIC_MOTOR_ULV,
                'F', MetaTileEntities.STEAM_COMPRESSOR_BRONZE.getStackForm(),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS));

        ModHandler.addShapedRecipe(true, "steam_extractor", STEAM_EXTRACTOR.getStackForm(),
                "GGG", "CFC", "PPP",
                'G', ELECTRIC_PISTON_ULV,
                'P', ELECTRIC_PUMP_ULV,
                'F', MetaTileEntities.STEAM_EXTRACTOR_BRONZE.getStackForm(),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS));

        ModHandler.addShapedRecipe(true, "steam_ore_washer", STEAM_ORE_WASHER.getStackForm(),
                "GGG", "CFC", "PPP",
                'G', ELECTRIC_MOTOR_ULV,
                'P', ELECTRIC_PUMP_ULV,
                'F', STEAM_HAMMER_BRONZE.getStackForm(),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS));

        ModHandler.addShapedRecipe(true, "steam_hammer", STEAM_HAMMER.getStackForm(),
                "GGG", "CFC", "PPP",
                'G', ELECTRIC_MOTOR_ULV,
                'P', ELECTRIC_PISTON_ULV,
                'F', STEAM_HAMMER_BRONZE.getStackForm(),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS));

        ModHandler.addShapedRecipe(true, "steam_centrifuge", STEAM_CENTRIFUGE.getStackForm(),
                "GGG", "CFC", "PPP",
                'G', ELECTRIC_PUMP_ULV,
                'P', ELECTRIC_PISTON_ULV,
                'F', STEAM_MACERATOR_BRONZE.getStackForm(),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS));

        ModHandler.addShapedRecipe(true, "steam_mixer", STEAM_MIXER.getStackForm(),
                "GGG", "CFC", "PPP",
                'G', ELECTRIC_MOTOR_ULV,
                'P', ELECTRIC_PISTON_ULV,
                'F', STEAM_MACERATOR_BRONZE.getStackForm(),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS));

        ModHandler.addShapedRecipe(true, "steam_sifter", STEAM_SIFTER.getStackForm(),
                "GGG", "CFC", "PPP",
                'G', ELECTRIC_PISTON_ULV,
                'P', ELECTRIC_MOTOR_ULV,
                'F', STEAM_MACERATOR_BRONZE.getStackForm(),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS));

        ModHandler.addShapedRecipe(true, "steam_bender", STEAM_BENDER.getStackForm(),
                "GGG", "CFC", "PPP",
                'G', ELECTRIC_PISTON_ULV,
                'P', ELECTRIC_MOTOR_ULV,
                'F', STEAM_HAMMER_BRONZE.getStackForm(),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS));

        ModHandler.addShapedRecipe(true, "steam_wire_mill", STEAM_WIRE_MILL.getStackForm(),
                "GGG", "CFC", "PPP",
                'G', ELECTRIC_PISTON_ULV,
                'P', ELECTRIC_MOTOR_ULV,
                'F', STEAM_COMPRESSOR_BRONZE.getStackForm(),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS));

        ModHandler.addShapedRecipe(true, "steam_fermentation_vat", STEAM_FERMENTATION_VAT.getStackForm(),
                "GGG", "CFC", "PPP",
                'G', ELECTRIC_MOTOR_ULV,
                'P', ELECTRIC_PUMP_ULV,
                'F', STEAM_MIXER.getStackForm(),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS));

        ModHandler.addShapedRecipe(true, "steam_blast_furance", STEAM_BLAST_FURNACE.getStackForm(),
                "GGG", "CFC", "PPP",
                'G', CONVEYOR_MODULE_ULV,
                'P', ROBOT_ARM_ULV,
                'F', PRIMITIVE_BLAST_FURNACE.getStackForm(),
                'P', new UnificationEntry(circuit, LV),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS));

        ModHandler.addShapedRecipe(true, "bronze_multiblock_tank", BRONZE_TANK.getStackForm(), " R ",
                "hCw", " R ", 'R', new UnificationEntry(OrePrefix.ring, Materials.Bronze), 'C',
                MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS));

        ModHandler.addShapedRecipe(true, "bronze_tank_valve", BRONZE_TANK_VALVE.getStackForm(), " R ",
                "hCw", " O ", 'O', new UnificationEntry(OrePrefix.rotor, Materials.Bronze), 'R',
                new UnificationEntry(OrePrefix.ring, Materials.Bronze), 'C',
                MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS));

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
    }
}
