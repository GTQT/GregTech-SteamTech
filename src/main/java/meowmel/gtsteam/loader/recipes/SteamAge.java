package meowmel.gtsteam.loader.recipes;

import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.BlockSteamCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.Materials.Bronze;
import static gregtech.api.unification.material.Materials.Copper;
import static gregtech.api.unification.material.Materials.Iron;
import static gregtech.api.unification.material.Materials.Lead;
import static gregtech.api.unification.material.Materials.Potin;
import static gregtech.api.unification.material.Materials.Sapphire;
import static gregtech.api.unification.material.Materials.TinAlloy;
import static gregtech.api.unification.material.Materials.WroughtIron;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.api.unification.ore.OrePrefix.gem;
import static gregtech.common.items.MetaItems.GLASS_TUBE;
import static meowmel.gtsteam.common.item.GTSMetaitems.*;
import static meowmel.gtsteam.common.item.GTSMetaitems.FIELD_GENERATOR_STEAM;

public class SteamAge {
    public static void init() {
        SteamPart();
        SteamStageMachines();

    }

    private static void MultiblockRecipes() {
    }

    private static void SteamPart() {
        //  Steam
        ModHandler.addShapedRecipe(true, "electric_motor.steam", ELECTRIC_MOTOR_STEAM.getStackForm(),
                "CWR", "WMW", "RWC",
                'C', new UnificationEntry(pipeTinyFluid, Bronze),
                'W', new UnificationEntry(wireGtSingle, Lead),
                'M', new UnificationEntry(stick, IronMagnetic),
                'R', new UnificationEntry(stick, Potin));

        //  Steam
        ModHandler.addShapedRecipe(true, "conveyor_module.steam", CONVEYOR_MODULE_STEAM.getStackForm(),
                "RRR", "MCM", "RRR",
                'R', "wool",
                'M', ELECTRIC_MOTOR_STEAM.getStackForm(),
                'C', new UnificationEntry(pipeTinyFluid, Bronze));

        //  Steam
        ModHandler.addShapedRecipe(true, "electric_piston.steam", ELECTRIC_PISTON_STEAM.getStackForm(),
                "PPP", "CRR", "CMG",
                'P', new UnificationEntry(plate, Potin),
                'C', new UnificationEntry(pipeTinyFluid, Bronze),
                'R', new UnificationEntry(stick, WroughtIron),
                'M', ELECTRIC_MOTOR_STEAM.getStackForm(),
                'G', new UnificationEntry(gearSmall, Iron));

        //  Steam
        ModHandler.addShapedRecipe(true, "robot_arm.steam", ROBOT_ARM_STEAM.getStackForm(),
                "CCC", "MRM", "PXR",
                'C', new UnificationEntry(pipeTinyFluid, Bronze),
                'M', ELECTRIC_MOTOR_STEAM.getStackForm(),
                'R', new UnificationEntry(stick, Potin),
                'P', ELECTRIC_PISTON_STEAM.getStackForm(),
                'X', GLASS_TUBE.getStackForm());

        //  Steam
        ModHandler.addShapedRecipe(true, "electric_pump.steam", ELECTRIC_PUMP_STEAM.getStackForm(),
                "SXR", "dPw", "RMC",
                'S', new UnificationEntry(screw, Potin),
                'X', new UnificationEntry(rotor, Iron),
                'P', new UnificationEntry(pipeNormalFluid, Copper),
                'R', "wool",
                'C', new UnificationEntry(pipeTinyFluid, Bronze),
                'M', ELECTRIC_MOTOR_STEAM.getStackForm());


        //  Steam
        ModHandler.addShapedRecipe(true, "emitter.steam", EMITTER_STEAM.getStackForm(),
                "CRX", "RGR", "XRC",
                'R', new UnificationEntry(stick, TinAlloy),
                'C', new UnificationEntry(pipeTinyFluid, Bronze),
                'G', new UnificationEntry(gem, Sapphire),
                'X', GLASS_TUBE.getStackForm());


        //  Steam
        ModHandler.addShapedRecipe(true, "sensor.steam", SENSOR_STEAM.getStackForm(),
                "P G", "PR ", "XPP",
                'P', new UnificationEntry(plate, Potin),
                'R', new UnificationEntry(stick, TinAlloy),
                'G', new UnificationEntry(gem, Sapphire),
                'X', GLASS_TUBE.getStackForm());


        //  Steam
        ModHandler.addShapedRecipe(true, "field_generator.steam", FIELD_GENERATOR_STEAM.getStackForm(),
                "WPW", "XGX", "WPW",
                'W', new UnificationEntry(pipeLargeFluid, Lead),
                'P', new UnificationEntry(plate, Potin),
                'G', new UnificationEntry(gem, Emerald),
                'X', GLASS_TUBE.getStackForm());
    }

    private static void SteamStageMachines() {
        //  Steam Coal Boiler
        ModHandler.removeRecipeByName("gregtech:steam_boiler_coal_bronze");
        ModHandler.addShapedRecipe(true, "steam_boiler_coal_bronze", MetaTileEntities.STEAM_BOILER_COAL_BRONZE.getStackForm(),
                "PPP", "CHC", "BFB",
                'H', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.BRONZE_BRICKS_HULL),
                'C', CONVEYOR_MODULE_STEAM,
                'P', new UnificationEntry(plate, Bronze),
                'B', new UnificationEntry(block, Brick),
                'F', "craftingFurnace");

        //  High Pressure Steam Coal Boiler
        ModHandler.removeRecipeByName("gregtech:steam_boiler_coal_steel");
        ModHandler.addShapedRecipe(true, "steam_boiler_coal_steel", MetaTileEntities.STEAM_BOILER_COAL_STEEL.getStackForm(),
                "PPP", "RHC", "BFB",
                'H', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.STEEL_BRICKS_HULL),
                'C', CONVEYOR_MODULE_STEAM,
                'R', ROBOT_ARM_STEAM,
                'P', new UnificationEntry(plate, Steel),
                'B', new UnificationEntry(block, Brick),
                'F', "craftingFurnace");

        //  Steam Solar Boiler
        ModHandler.removeRecipeByName("gregtech:steam_boiler_solar_bronze");
        ModHandler.addShapedRecipe(true, "steam_boiler_solar_bronze", MetaTileEntities.STEAM_BOILER_SOLAR_BRONZE.getStackForm(),
                "PPP", "CHU", "BAB",
                'H', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.BRONZE_BRICKS_HULL),
                'C', CONVEYOR_MODULE_STEAM,
                'U', ELECTRIC_PUMP_STEAM,
                'P', new UnificationEntry(plate, Bronze),
                'B', new UnificationEntry(block, Brick),
                'A', new UnificationEntry(pipeSmallFluid, Bronze));

        //  High Pressure Steam Solar Boiler
        ModHandler.removeRecipeByName("gregtech:steam_boiler_solar_steel");
        ModHandler.addShapedRecipe(true, "steam_boiler_solar_steel", MetaTileEntities.STEAM_BOILER_SOLAR_STEEL.getStackForm(),
                "PGP", "CHU", "BRB",
                'H', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.STEEL_BRICKS_HULL),
                'C', CONVEYOR_MODULE_STEAM,
                'U', ELECTRIC_PUMP_STEAM,
                'R', ROBOT_ARM_STEAM,
                'B', new UnificationEntry(pipeSmallFluid, TinAlloy),
                'P', new UnificationEntry(plate, Steel),
                'G', new UnificationEntry(plate, Glass));

        //  Steam Lava Boiler
        ModHandler.removeRecipeByName("gregtech:steam_boiler_lava_bronze");
        ModHandler.addShapedRecipe(true, "steam_boiler_lava_bronze", MetaTileEntities.STEAM_BOILER_LAVA_BRONZE.getStackForm(),
                "PPP", "UHU", "BAB",
                'H', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.BRONZE_BRICKS_HULL),
                'U', ELECTRIC_PUMP_STEAM,
                'P', new UnificationEntry(plate, Bronze),
                'B', new UnificationEntry(block, Brick),
                'A', new UnificationEntry(pipeSmallFluid, Bronze));

        //  High Pressure Steam Lava Boiler
        ModHandler.removeRecipeByName("gregtech:steam_boiler_lava_steel");
        ModHandler.addShapedRecipe(true, "steam_boiler_lava_steel", MetaTileEntities.STEAM_BOILER_LAVA_STEEL.getStackForm(),
                "PPP", "UHR", "BAB",
                'H', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.STEEL_BRICKS_HULL),
                'U', ELECTRIC_PUMP_STEAM,
                'R', ROBOT_ARM_STEAM,
                'P', new UnificationEntry(plate, Steel),
                'B', new UnificationEntry(block, Brick),
                'A', new UnificationEntry(pipeSmallFluid, TinAlloy));

        //  Steam Macerator
        ModHandler.removeRecipeByName("gregtech:steam_macerator_bronze");
        ModHandler.addShapedRecipe(true, "steam_macerator_bronze", MetaTileEntities.STEAM_MACERATOR_BRONZE.getStackForm(),
                "IMG", "PPC", "RRP",
                'C', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.BRONZE_HULL),
                'P', new UnificationEntry(pipeTinyFluid, Bronze),
                'G', new UnificationEntry(gem, Diamond),
                'I', ELECTRIC_PISTON_STEAM,
                'M', ELECTRIC_MOTOR_STEAM,
                'R', new UnificationEntry(plate, Bronze));

        //  High Pressure Steam Macerator
        ModHandler.removeRecipeByName("gregtech:steam_macerator_steel");
        ModHandler.addShapedRecipe(true, "steam_macerator_steel", MetaTileEntities.STEAM_MACERATOR_STEEL.getStackForm(),
                "IMG", "PPC", "RRP",
                'C', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.STEEL_HULL),
                'P', new UnificationEntry(pipeTinyFluid, TinAlloy),
                'G', new UnificationEntry(gem, Diamond),
                'I', ELECTRIC_PISTON_STEAM,
                'M', ELECTRIC_MOTOR_STEAM,
                'R', new UnificationEntry(plate, Steel));

        //  Steam Compressor
        ModHandler.removeRecipeByName("gregtech:steam_compressor_bronze");
        ModHandler.addShapedRecipe(true, "steam_compressor_bronze", MetaTileEntities.STEAM_COMPRESSOR_BRONZE.getStackForm(),
                " R ", "PCP", "TRT",
                'R', new UnificationEntry(plate, Bronze),
                'C', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.BRONZE_HULL),
                'P', ELECTRIC_PISTON_STEAM,
                'T', new UnificationEntry(pipeTinyFluid, Bronze));

        //  High Pressure Steam Compressor
        ModHandler.removeRecipeByName("gregtech:steam_compressor_steel");
        ModHandler.addShapedRecipe(true, "steam_compressor_steel", MetaTileEntities.STEAM_COMPRESSOR_STEEL.getStackForm(),
                " R ", "PCP", "TRT",
                'R', new UnificationEntry(plate, Steel),
                'C', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.STEEL_HULL),
                'P', ELECTRIC_PISTON_STEAM,
                'T', new UnificationEntry(pipeTinyFluid, TinAlloy));

        //  Steam Alloy Smelter
        ModHandler.removeRecipeByName("gregtech:steam_alloy_smelter_bronze");
        ModHandler.addShapedRecipe(true, "steam_alloy_smelter_bronze", MetaTileEntities.STEAM_ALLOY_SMELTER_BRONZE.getStackForm(),
                "RWR", "WCW", "TWT",
                'C', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.BRONZE_BRICKS_HULL),
                'W', new UnificationEntry(wireGtQuadruple, Lead),
                'T', new UnificationEntry(pipeTinyFluid, Bronze),
                'R', new UnificationEntry(plate, Bronze));

        //  High Pressure Steam Alloy Smelter
        ModHandler.removeRecipeByName("gregtech:steam_alloy_smelter_steel");
        ModHandler.addShapedRecipe(true, "steam_alloy_smelter_steel", MetaTileEntities.STEAM_ALLOY_SMELTER_STEEL.getStackForm(),
                "RWR", "WCW", "TWT",
                'C', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.STEEL_BRICKS_HULL),
                'W', new UnificationEntry(wireGtQuadruple, Lead),
                'T', new UnificationEntry(pipeTinyFluid, TinAlloy),
                'R', new UnificationEntry(plate, Steel));

        //  Steam Furnace
        ModHandler.removeRecipeByName("gregtech:steam_furnace_bronze");
        ModHandler.addShapedRecipe(true, "steam_furnace_bronze", MetaTileEntities.STEAM_FURNACE_BRONZE.getStackForm(),
                "RWR", "WCW", "TWT",
                'C', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.BRONZE_BRICKS_HULL),
                'W', new UnificationEntry(wireGtDouble, Lead),
                'T', new UnificationEntry(pipeTinyFluid, Bronze),
                'R', new UnificationEntry(plate, Bronze));

        //  High Pressure Steam Furnace
        ModHandler.removeRecipeByName("gregtech:steam_furnace_steel");
        ModHandler.addShapedRecipe(true, "steam_furnace_steel", MetaTileEntities.STEAM_FURNACE_STEEL.getStackForm(),
                "RWR", "WCW", "TWT",
                'C', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.STEEL_BRICKS_HULL),
                'W', new UnificationEntry(wireGtDouble, Lead),
                'T', new UnificationEntry(pipeTinyFluid, TinAlloy),
                'R', new UnificationEntry(plate, Steel));

        //  Steam Hammer
        ModHandler.removeRecipeByName("gregtech:steam_hammer_bronze");
        ModHandler.addShapedRecipe(true, "steam_hammer_bronze", MetaTileEntities.STEAM_HAMMER_BRONZE.getStackForm(),
                "TPT", "RCR", "TXT",
                'C', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.BRONZE_HULL),
                'P', ELECTRIC_PISTON_STEAM,
                'X', "craftingAnvil",
                'T', new UnificationEntry(pipeTinyFluid, Bronze),
                'R', new UnificationEntry(plate, Bronze));

        //  High Pressure Steam Hammer
        ModHandler.removeRecipeByName("gregtech:steam_hammer_steel");
        ModHandler.addShapedRecipe(true, "steam_hammer_steel", MetaTileEntities.STEAM_HAMMER_STEEL.getStackForm(),
                "TPT", "RCR", "TXT",
                'C', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.STEEL_HULL),
                'P', ELECTRIC_PISTON_STEAM,
                'X', "craftingAnvil",
                'T', new UnificationEntry(pipeTinyFluid, TinAlloy),
                'R', new UnificationEntry(plate, Steel));

        //  Steam Rock Breaker
        ModHandler.removeRecipeByName("gregtech:steam_rock_breaker_bronze");
        ModHandler.addShapedRecipe(true, "steam_rock_breaker_bronze", MetaTileEntities.STEAM_ROCK_BREAKER_BRONZE.getStackForm(),
                "IMD", "TCT", "GGG",
                'C', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.BRONZE_HULL),
                'T', new UnificationEntry(pipeTinyFluid, Bronze),
                'I', ELECTRIC_PISTON_STEAM,
                'M', ELECTRIC_MOTOR_STEAM,
                'D', new UnificationEntry(gem, Diamond),
                'G', new ItemStack(Blocks.GLASS));

        //  High Pressure Steam Rock Breaker
        ModHandler.removeRecipeByName("gregtech:steam_rock_breaker_steel");
        ModHandler.addShapedRecipe(true, "steam_rock_breaker_steel", MetaTileEntities.STEAM_ROCK_BREAKER_STEEL.getStackForm(),
                "IMD", "TCT", "GGG",
                'C', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.STEEL_HULL),
                'T', new UnificationEntry(pipeTinyFluid, TinAlloy),
                'I', ELECTRIC_PISTON_STEAM,
                'M', ELECTRIC_MOTOR_STEAM,
                'D', new UnificationEntry(gem, Diamond),
                'G', new ItemStack(Blocks.GLASS));

        //  Steam Extractor
        ModHandler.removeRecipeByName("gregtech:steam_extractor_bronze");
        ModHandler.addShapedRecipe(true, "steam_extractor_bronze", MetaTileEntities.STEAM_EXTRACTOR_BRONZE.getStackForm(),
                "GRG", "SHP", "TRT",
                'G', new ItemStack(Blocks.GLASS),
                'R', new UnificationEntry(plate, Bronze),
                'H', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.BRONZE_HULL),
                'S', ELECTRIC_PISTON_STEAM,
                'P', ELECTRIC_PUMP_STEAM,
                'T', new UnificationEntry(pipeTinyFluid, Bronze));

        //  High Pressure Extractor
        ModHandler.removeRecipeByName("gregtech:steam_extractor_steel");
        ModHandler.addShapedRecipe(true, "steam_extractor_steel", MetaTileEntities.STEAM_EXTRACTOR_STEEL.getStackForm(),
                "GRG", "SHP", "TRT",
                'G', new ItemStack(Blocks.GLASS),
                'R', new UnificationEntry(plate, Steel),
                'H', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.STEEL_HULL),
                'S', ELECTRIC_PISTON_STEAM,
                'P', ELECTRIC_PUMP_STEAM,
                'T', new UnificationEntry(pipeTinyFluid, TinAlloy));

        //  Steam Miner
        ModHandler.removeRecipeByName("gregtech:steam_miner");
        ModHandler.addShapedRecipe(true, "steam_miner", MetaTileEntities.STEAM_MINER.getStackForm(),
                "MMM", "PHP", "XSX",
                'M', ELECTRIC_MOTOR_STEAM,
                'P', new UnificationEntry(pipeTinyFluid, Bronze),
                'H', MetaBlocks.STEAM_CASING.getItemVariant(BlockSteamCasing.SteamCasingType.BRONZE_HULL),
                'X', new UnificationEntry(circuit, MarkerMaterials.Tier.ULV),
                'S', SENSOR_STEAM);
    }
}
