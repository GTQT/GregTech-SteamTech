package meowmel.gtsteam.loader.recipes;

import gregtech.api.GTValues;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.loaders.recipe.CraftingComponent;
import gtqt.common.metatileentities.GTQTMetaTileEntities;
import meowmel.gtsteam.common.block.GTSteamMetaBlocks;
import meowmel.gtsteam.common.block.blocks.BlockMultiblockCasing0;
import meowmel.gtsteam.common.item.GTSMetaitems;
import meowmel.gtsteam.common.metatileentities.GTSteamMetaTileEntities;
import net.minecraft.init.Blocks;

import static gregtech.api.unification.material.MarkerMaterials.Tier.LV;
import static gregtech.api.unification.material.MarkerMaterials.Tier.ULV;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.common.blocks.BlockFireboxCasing.FireboxCasingType.BRONZE_FIREBOX;
import static gregtech.common.blocks.BlockFireboxCasing.FireboxCasingType.STEEL_FIREBOX;
import static gregtech.common.blocks.BlockMetalCasing.MetalCasingType.BRONZE_BRICKS;
import static gregtech.common.blocks.BlockMetalCasing.MetalCasingType.STEEL_SOLID;
import static gregtech.common.blocks.BlockWireCoil.CoilType.CUPRONICKEL;
import static gregtech.common.metatileentities.MetaTileEntities.*;
import static gregtech.loaders.recipe.MetaTileEntityLoader.registerMachineRecipe;
import static meowmel.gtsteam.api.unification.GTSteamMaterials.GalvanizedSteel;
import static meowmel.gtsteam.api.unification.GTSteamMaterials.SealedWood;
import static meowmel.gtsteam.common.block.GTSteamMetaBlocks.blockFireboxCasing0;
import static meowmel.gtsteam.common.block.GTSteamMetaBlocks.blockMultiblockCasing1;
import static meowmel.gtsteam.common.block.blocks.BlockFireboxCasing0.FireboxCasingType.FLUID_FIREBOX;
import static meowmel.gtsteam.common.block.blocks.BlockFireboxCasing0.FireboxCasingType.ITEM_FIREBOX;
import static meowmel.gtsteam.common.block.blocks.BlockMultiblockCasing0.CasingType.GALVANIZED_PORCELAIN_TILES;
import static meowmel.gtsteam.common.block.blocks.BlockMultiblockCasing1.CasingType.SOLAR_COLLECTOR;
import static meowmel.gtsteam.common.item.GTSMetaitems.*;
import static meowmel.gtsteam.common.metatileentities.GTSteamMetaTileEntities.*;
import static meowmel.gtsteam.common.metatileentities.GTSteamMetaTileEntities.ALLOY_SMELTER;
import static meowmel.gtsteam.common.metatileentities.GTSteamMetaTileEntities.BREWERY;
import static meowmel.gtsteam.common.metatileentities.GTSteamMetaTileEntities.ELECTRIC_FURNACE;
import static meowmel.gtsteam.common.metatileentities.GTSteamMetaTileEntities.FERMENTER;

public class MachineRecipes {
    public static void init() {
        PrimitiveRecipes();
        HeatMultiblockRecipes();
        SteamMultiblockRecipes();
    }

    private static void SteamMultiblockRecipes() {
        ModHandler.addShapedRecipe(true, "steam_compressor", STEAM_COMPRESSOR.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', new UnificationEntry(frameGt, Bronze),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                'M', STEAM_COMPRESSOR_BRONZE.getStackForm(),
                'G', new UnificationEntry(circuit, ULV));

        ModHandler.addShapedRecipe(true, "steam_extractor", STEAM_EXTRACTOR.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', new UnificationEntry(frameGt, Bronze),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                'M', STEAM_EXTRACTOR_BRONZE.getStackForm(),
                'G', new UnificationEntry(circuit, ULV));

        ModHandler.addShapedRecipe(true, "steam_ore_washer", STEAM_ORE_WASHER.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', new UnificationEntry(frameGt, Bronze),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                'M', ELECTRIC_PUMP_STEAM,
                'G', new UnificationEntry(circuit, ULV));

        ModHandler.addShapedRecipe(true, "steam_hammer", STEAM_HAMMER.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', new UnificationEntry(frameGt, Bronze),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                'M', MetaTileEntities.STEAM_HAMMER_BRONZE.getStackForm(),
                'G', new UnificationEntry(circuit, ULV));

        ModHandler.addShapedRecipe(true, "steam_centrifuge", STEAM_CENTRIFUGE.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', new UnificationEntry(frameGt, Bronze),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                'M', ELECTRIC_MOTOR_STEAM,
                'G', new UnificationEntry(circuit, ULV));

        ModHandler.addShapedRecipe(true, "steam_mixer", STEAM_MIXER.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', new UnificationEntry(frameGt, Bronze),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                'M', CONVEYOR_MODULE_STEAM,
                'G', new UnificationEntry(circuit, ULV));

        ModHandler.addShapedRecipe(true, "steam_sifter", STEAM_SIFTER.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', new UnificationEntry(frameGt, Bronze),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                'M', ROBOT_ARM_STEAM,
                'G', new UnificationEntry(circuit, ULV));

        ModHandler.addShapedRecipe(true, "steam_brewing", STEAM_BREWING.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', new UnificationEntry(frameGt, SealedWood),
                'C', GTSteamMetaBlocks.blockMultiblockCasing0.getItemVariant(BlockMultiblockCasing0.CasingType.SEALED_WOOD_WALL),
                'M', ELECTRIC_PUMP_STEAM,
                'G', new UnificationEntry(circuit, ULV));

        ModHandler.addShapedRecipe(true, "steam_lava_furnace", STEAM_LAVA_FURNACE.getStackForm(),
                "GPG", "CFC", "GPG",
                'G', ELECTRIC_PISTON_STEAM,
                'P', ELECTRIC_MOTOR_STEAM,
                'F', STEAM_FURNACE_STEEL.getStackForm(),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS));

        ModHandler.addShapedRecipe(true, "steam_lathe", STEAM_LATHE.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', new UnificationEntry(frameGt, Bronze),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                'M', ELECTRIC_PISTON_STEAM,
                'G', new UnificationEntry(circuit, ULV));

        ModHandler.addShapedRecipe(true, "steam_alloy_furnace", STEAM_ALLOY_FURNACE.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', MetaBlocks.BOILER_FIREBOX_CASING.getItemVariant(BRONZE_FIREBOX),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                'M', MetaTileEntities.STEAM_ALLOY_SMELTER_BRONZE.getStackForm(),
                'G', new UnificationEntry(circuit, ULV));

        ModHandler.addShapedRecipe(true, "steam_bender", STEAM_BENDER.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', new UnificationEntry(frameGt, Steel),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                'M', ELECTRIC_PISTON_STEAM,
                'G', new UnificationEntry(circuit, ULV));

        ModHandler.addShapedRecipe(true, "steam_wire_mill", STEAM_WIRE_MILL.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', new UnificationEntry(frameGt, Steel),
                'C', MetaBlocks.METAL_CASING.getItemVariant(BRONZE_BRICKS),
                'M', ELECTRIC_MOTOR_STEAM,
                'G', new UnificationEntry(circuit, ULV));
    }

    private static void HeatMultiblockRecipes() {
        //锅炉
        ModHandler.addShapedRecipe(true, "steam_solar_boiler", STEAM_SOLAR_BOILER.getStackForm(),
                "PSP", "SAS", "PSP",
                'P', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Steel),
                'S', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.ULV),
                'A', blockMultiblockCasing1.getItemVariant(SOLAR_COLLECTOR));

        ModHandler.addShapedRecipe(true, "low_pressure_solid_boiler", LOW_PRESSURE_SOLID_BOILER.getStackForm(),
                "PSP", "SAS", "PSP",
                'P', new UnificationEntry(plate, Steel),
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

        //热力多方块
        ModHandler.addShapedRecipe(true, "heat_furnace", HEAT_FURNACE.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', MetaBlocks.BOILER_FIREBOX_CASING.getItemVariant(STEEL_FIREBOX),
                'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                'M', ELECTRIC_FURNACE.getStackForm(),
                'G', new UnificationEntry(OrePrefix.gear, Materials.Invar));

        ModHandler.addShapedRecipe(true, "heat_alloy_furnace", HEAT_ALLOY_FURNACE.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', MetaBlocks.BOILER_FIREBOX_CASING.getItemVariant(STEEL_FIREBOX),
                'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                'M', ALLOY_SMELTER.getStackForm(),
                'G', new UnificationEntry(circuit, LV));

        ModHandler.addShapedRecipe(true, "heat_coke_oven", HEAT_COKE_OVEN.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', MetaBlocks.BOILER_FIREBOX_CASING.getItemVariant(STEEL_FIREBOX),
                'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                'M', COKE_OVEN.getStackForm(),
                'G', new UnificationEntry(circuit, LV));

        ModHandler.addShapedRecipe(true, "distillation_tower", MetaTileEntities.DISTILLATION_TOWER.getStackForm(),
                "CBC", "FMF", "CBC",
                'M', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                'B', new UnificationEntry(OrePrefix.pipeLargeFluid, Steel),
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.LV),
                'F', ELECTRIC_PUMP_ULV);

        ModHandler.addShapedRecipe(true, "heat_cracking_unit", HEAT_CRACKING_UNIT.getStackForm(),
                "CEC", "PHP", "CEC",
                'C', new UnificationEntry(spring, Copper),
                'E', ELECTRIC_PUMP_ULV,
                'P', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.LV),
                'H', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID));

        ModHandler.addShapedRecipe(true, "heat_brewing_vat", HEAT_BREWING_VAT.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', new UnificationEntry(rotor, Tin),
                'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                'M', BREWERY.getStackForm(),
                'G', new UnificationEntry(circuit, LV));

        ModHandler.addShapedRecipe(true, "heat_fermenter", HEAT_FERMENTER.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', new UnificationEntry(screw, Tin),
                'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                'M', FERMENTER.getStackForm(),
                'G', new UnificationEntry(circuit, LV));

        ModHandler.addShapedRecipe(true, "heat_evaporation_pond", HEAT_EVAPORATION_POND.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', new UnificationEntry(frameGt, Steel),
                'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                'M', CONVEYOR_MODULE_ULV.getStackForm(),
                'G', new UnificationEntry(circuit, LV));

        ModHandler.addShapedRecipe(true, "heat_chemical_reactor", HEAT_CHEMICAL_REACTOR.getStackForm(),
                "CRC", "PMP", "CHC",
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.LV),
                'R', OreDictUnifier.get(OrePrefix.rotor, Steel),
                'P', OreDictUnifier.get(OrePrefix.pipeLargeFluid, Steel),
                'M', ELECTRIC_MOTOR_ULV.getStackForm(),
                'H', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID));

        ModHandler.addShapedRecipe(true, "heat_thermal_centrifuge", HEAT_THERMAL_CENTRIFUGE.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', new UnificationEntry(screw, Steel),
                'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                'M', ELECTRIC_MOTOR_ULV.getStackForm(),
                'G', new UnificationEntry(circuit, LV));

        ModHandler.addShapedRecipe(true, "heat_lava_furnace", HEAT_LAVA_FURNACE.getStackForm(),
                "CGC", "FMF", "CGC",
                'F', new UnificationEntry(spring, Copper),
                'C', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID),
                'M', STEAM_LAVA_FURNACE.getStackForm(),
                'G', new UnificationEntry(circuit, LV));

        ModHandler.addShapedRecipe(true, "heat_electronic_processor", HEAT_ELECTRONIC_PROCESSOR.getStackForm(),
                "CEC", "PHP", "CEC",
                'C', ROBOT_ARM_ULV,
                'E', CONVEYOR_MODULE_ULV,
                'P', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.LV),
                'H', MetaBlocks.METAL_CASING.getItemVariant(STEEL_SOLID));

        registerMachineRecipe(GTSteamMetaTileEntities.LATEX_COLLECTOR,
                "PCP", "AMA", "PCP",
                'M', CraftingComponent.HULL,
                'A', CraftingComponent.PIPE_NORMAL,
                'C', CraftingComponent.GLASS,
                'P', CraftingComponent.PUMP);
    }

    private static void PrimitiveRecipes() {
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

        //工业土高炉
        ModHandler.addShapedRecipe(true, "industrial_primitive_blast_furnace",
                INDUSTRIAL_PRIMITIVE_BLAST_FURNACE.getStackForm(), "PPP", "CFC", "BBB",
                'C', new UnificationEntry(circuit, LV),
                'P', new UnificationEntry(plate, Steel),
                'F', new UnificationEntry(frameGt, Steel),
                'B', PRIMITIVE_BLAST_FURNACE.getStackForm());

        //原始化学反应釜
        ModHandler.addShapedRecipe(true, "primitive_chemical_reactor", COAGULATION_TANK.getStackForm(),
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
    }
}
