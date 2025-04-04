package keqing.gtsteam.loader.recipes;

import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import keqing.gtsteam.api.recipes.GTSRecipeMaps;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import static gregtech.api.unification.material.MarkerMaterials.Tier.LV;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.api.unification.ore.OrePrefix.gem;
import static gregtech.common.blocks.BlockMetalCasing.MetalCasingType.BRONZE_BRICKS;
import static gregtech.common.metatileentities.MetaTileEntities.*;
import static keqing.gtsteam.common.item.GTSMetaitems.*;
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
                'P',  new UnificationEntry(circuit, LV),
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
                'X', new UnificationEntry(circuit, MarkerMaterials.Tier.ULV));

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
                'X', new UnificationEntry(circuit, MarkerMaterials.Tier.ULV));


        //  ULV
        ModHandler.addShapedRecipe(true, "sensor.ulv", SENSOR_ULV.getStackForm(),
                "P G", "PR ", "XPP",
                'P', new UnificationEntry(plate, Potin),
                'R', new UnificationEntry(stick, TinAlloy),
                'G', new UnificationEntry(gem, Sapphire),
                'X', new UnificationEntry(circuit, MarkerMaterials.Tier.ULV));


        //  ULV
        ModHandler.addShapedRecipe(true, "field_generator.ulv", FIELD_GENERATOR_ULV.getStackForm(),
                "WPW", "XGX", "WPW",
                'W', new UnificationEntry(pipeLargeFluid, Lead),
                'P', new UnificationEntry(plate, Potin),
                'G', new UnificationEntry(gem, Ruby),
                'X', new UnificationEntry(circuit, MarkerMaterials.Tier.ULV));
    }

}
