package meowmel.gtsteam.loader.recipes;

import gregtech.api.GTValues;
import gregtech.api.items.OreDictNames;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import static gregtech.api.unification.material.MarkerMaterials.Tier.ULV;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.common.metatileentities.MetaTileEntities.HULL;
import static meowmel.gtsteam.common.item.GTSMetaitems.*;
import static meowmel.gtsteam.common.metatileentities.GTSteamMetaTileEntities.*;
import static meowmel.gtsteam.common.metatileentities.GTSteamMetaTileEntities.DENSE_LAVA_COMBUSTOR;

public class ULVAge {
    public static void init() {
        ULVPart();
        ULVStageMachines();
        CombustorRecipes();
    }

    private static void CombustorRecipes() {
        Material[] materials = new Material[]{Materials.Lead, Materials.Bronze, Materials.Steel, Materials.Invar, Materials.Chrome, Materials.Titanium};
        for (int i = 0; i < materials.length; i++) {
            ModHandler.addShapedRecipe(true, "coal_combustor."+materials[i].toString(),
                    COAL_COMBUSTOR[i].getStackForm(),
                    "PCP", "PwP", "PHP",
                    'C', new UnificationEntry(plate, Copper),
                    'P', new UnificationEntry(plate, materials[i]),
                    'H', MetaTileEntities.STEAM_BOILER_COAL_BRONZE.getStackForm());

            ModHandler.addShapedRecipe(true, "lava_combustor."+materials[i].toString(),
                    LAVA_COMBUSTOR[i].getStackForm(),
                    "PCP", "PwP", "PHP",
                    'C', new UnificationEntry(plate, Copper),
                    'P', new UnificationEntry(plate, materials[i]),
                    'H', MetaTileEntities.STEAM_BOILER_LAVA_BRONZE.getStackForm());

            ModHandler.addShapedRecipe(true, "solar_combustor."+materials[i].toString(),
                    SOLAR_COMBUSTOR[i].getStackForm(),
                    "PCP", "PwP", "PHP",
                    'C', new UnificationEntry(plate, Copper),
                    'P', new UnificationEntry(plate, materials[i]),
                    'H', MetaTileEntities.STEAM_BOILER_SOLAR_BRONZE.getStackForm());

            ModHandler.addShapedRecipe(true, "dense_coal_combustor."+materials[i].toString(),
                    DENSE_COAL_COMBUSTOR[i].getStackForm(),
                    "PCP", "PwP", "PHP",
                    'C', new UnificationEntry(plateDouble, Copper),
                    'P', new UnificationEntry(plateDouble, materials[i]),
                    'H', MetaTileEntities.STEAM_BOILER_COAL_STEEL.getStackForm());

            ModHandler.addShapedRecipe(true, "dense_lava_combustor."+materials[i].toString(),
                    DENSE_LAVA_COMBUSTOR[i].getStackForm(),
                    "PCP", "PwP", "PHP",
                    'C', new UnificationEntry(plateDouble, Copper),
                    'P', new UnificationEntry(plateDouble, materials[i]),
                    'H', MetaTileEntities.STEAM_BOILER_LAVA_STEEL.getStackForm());

            ModHandler.addShapedRecipe(true, "dense_solar_combustor."+materials[i].toString(),
                    DENSE_SOLAR_COMBUSTOR[i].getStackForm(),
                    "PCP", "PwP", "PHP",
                    'C', new UnificationEntry(plateDouble, Copper),
                    'P', new UnificationEntry(plateDouble, materials[i]),
                    'H', MetaTileEntities.STEAM_BOILER_SOLAR_STEEL.getStackForm());
        }
    }


    private static void ULVPart() {
        //  ULV
        ModHandler.addShapedRecipe(true, "electric_motor.ulv", ELECTRIC_MOTOR_ULV.getStackForm(),
                "CWR", "WMW", "RWC",
                'C', new UnificationEntry(cableGtSingle, Tin),
                'W', new UnificationEntry(wireGtSingle, Copper),
                'R', new UnificationEntry(stick, Iron),
                'M', new UnificationEntry(stick, IronMagnetic));

        //  ULV
        ModHandler.addShapedRecipe(true, "conveyor_module.ulv", CONVEYOR_MODULE_ULV.getStackForm(),
                "RRR", "MCM", "RRR",
                'R', new UnificationEntry(plate, Rubber),
                'C', new UnificationEntry(cableGtSingle, Tin),
                'M', ELECTRIC_MOTOR_ULV.getStackForm());

        //  ULV
        ModHandler.addShapedRecipe(true, "electric_piston.ulv", ELECTRIC_PISTON_ULV.getStackForm(),
                "PPP", "CRR", "CMG",
                'P', new UnificationEntry(plate, Steel),
                'C', new UnificationEntry(cableGtSingle, Tin),
                'R', new UnificationEntry(stick, Steel),
                'G', new UnificationEntry(gearSmall, Steel),
                'M', ELECTRIC_MOTOR_ULV.getStackForm());

        //  ULV
        ModHandler.addShapedRecipe(true, "robot_arm.ulv", ROBOT_ARM_ULV.getStackForm(),
                "CCC", "MRM", "PXR",
                'C', new UnificationEntry(cableGtSingle, Tin),
                'R', new UnificationEntry(stick, Steel),
                'M', ELECTRIC_MOTOR_ULV.getStackForm(),
                'P', ELECTRIC_PISTON_ULV.getStackForm(),
                'X', new UnificationEntry(circuit, MarkerMaterials.Tier.ULV));

        //  ULV
        ModHandler.addShapedRecipe(true, "electric_pump.ulv", ELECTRIC_PUMP_ULV.getStackForm(),
                "SXR", "dPw", "RMC",
                'S', new UnificationEntry(screw, Tin),
                'X', new UnificationEntry(rotor, Tin),
                'P', new UnificationEntry(pipeNormalFluid, Bronze),
                'R', new UnificationEntry(ring, Rubber),
                'C', new UnificationEntry(cableGtSingle, Tin),
                'M', ELECTRIC_MOTOR_ULV.getStackForm());

        //  ULV
        ModHandler.addShapedRecipe(true, "emitter.ulv", EMITTER_ULV.getStackForm(),
                "CRX", "RGR", "XRC",
                'R', new UnificationEntry(stick, Brass),
                'C', new UnificationEntry(cableGtSingle, Tin),
                'G', new UnificationEntry(gem, Quartzite),
                'X', new UnificationEntry(circuit, MarkerMaterials.Tier.ULV));

        //  ULV
        ModHandler.addShapedRecipe(true, "sensor.ulv", SENSOR_ULV.getStackForm(),
                "P G", "PR ", "XPP",
                'P', new UnificationEntry(plate, Steel),
                'R', new UnificationEntry(stick, Brass),
                'G', new UnificationEntry(gem, Quartzite),
                'X', new UnificationEntry(circuit, MarkerMaterials.Tier.ULV));

        //  ULV
        ModHandler.addShapedRecipe(true, "field_generator.ulv", FIELD_GENERATOR_ULV.getStackForm(),
                "WPW", "XGX", "WPW",
                'W', new UnificationEntry(wireGtQuadruple, RedAlloy),
                'P', new UnificationEntry(plate, Steel),
                'G', new UnificationEntry(gem, EnderPearl),
                'X', new UnificationEntry(circuit, MarkerMaterials.Tier.ULV));
    }

    private static void ULVStageMachines() {
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

        ModHandler.addShapedRecipe(true, "gas_turbine_lv", GAS_TURBINE.getStackForm(),
                "CRC", "RMR", "EWE",
                'M', MetaTileEntities.HULL[GTValues.ULV].getStackForm(),
                'E', ELECTRIC_MOTOR_ULV,
                'R', new UnificationEntry(OrePrefix.rotor, Materials.Tin),
                'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.ULV),
                'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy));

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
