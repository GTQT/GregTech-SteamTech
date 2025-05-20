package keqing.gtsteam.common.metatileentities;

import gregtech.api.metatileentity.SimpleGeneratorMetaTileEntity;
import gregtech.api.metatileentity.SimpleMachineMetaTileEntity;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.electric.MetaTileEntitySingleCombustion;
import gregtech.common.metatileentities.electric.MetaTileEntitySingleTurbine;
import keqing.gtsteam.common.metatileentities.multi.multipart.MetaTileEntityAlloyKilnExportHatch;
import keqing.gtsteam.common.metatileentities.multi.multipart.MetaTileEntityAlloyKilnImportHatch;
import keqing.gtsteam.common.metatileentities.multi.primitive.*;
import keqing.gtsteam.common.metatileentities.multi.steam.*;
import keqing.gtsteam.common.metatileentities.multi.steam.advanced.MetaTileEntitySteamBiomimeticFactory;
import keqing.gtsteam.common.metatileentities.multi.steam.advanced.MetaTileEntitySteamTranscendentPlasmaForge;
import keqing.gtsteam.common.metatileentities.multi.store.MetaTileEntityMultiblockTank;
import keqing.gtsteam.common.metatileentities.multi.store.MetaTileEntityTankValve;
import net.minecraft.util.ResourceLocation;

import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;
import static keqing.gtsteam.GTSteam.MODID;
import static keqing.gtsteam.api.recipes.GTSRecipeMaps.PRIMITIVE_COMBUSTION_GENERATOR_FUELS;
import static keqing.gtsteam.api.recipes.GTSRecipeMaps.PRIMITIVE_STEAM_TURBINE_FUELS;

public class GTSteamMetaTileEntities {
    public static MetaTileEntityAlloyKiln ALLOY_KILN;
    public static MetaTileEntityAlloyKilnImportHatch ALLOY_KILN_IMPORT_HATCH;
    public static MetaTileEntityAlloyKilnExportHatch ALLOY_KILN_EXPORT_HATCH;

    public static MetaTileEntityIndustrialPrimitiveBlastFurnace INDUSTRIAL_PRIMITIVE_BLAST_FURNACE;

    public static MetaTileEntitySteamCompressor STEAM_COMPRESSOR;
    public static MetaTileEntitySteamExtractor STEAM_EXTRACTOR;
    public static MetaTileEntitySteamBlastFurnace STEAM_BLAST_FURNACE;
    public static MetaTileEntitySteamOreWasher STEAM_ORE_WASHER;
    public static MetaTileEntitySteamHammer STEAM_HAMMER;
    public static MetaTileEntitySteamMixer STEAM_MIXER;
    public static MetaTileEntitySteamCentrifuge STEAM_CENTRIFUGE;
    public static MetaTileEntitySteamWireMill STEAM_WIRE_MILL;
    public static MetaTileEntitySteamBender STEAM_BENDER;
    public static MetaTileEntitySteamSifter STEAM_SIFTER;
    public static MetaTileEntitySteamLathe STEAM_LATHE;
    public static MetaTileEntitySawMill SAW_MILL;;

    public static MetaTileEntitySteamTranscendentPlasmaForge STEAM_TRANSCENDENT_PLASMA_FORGE;
    public static MetaTileEntitySteamBiomimeticFactory STEAM_BIOMIMETIC_FACTORY;

    public static MetaTileEntityPrimitiveWaterPump WATER_PUMP;
    public static MetaTileEntitySteamFermentationVat STEAM_FERMENTATION_VAT;
    public static MetaTileEntityAdvancedCokeOven ADVANCED_COKE_OVEN;

    public static MetaTileEntityTankValve BRONZE_TANK_VALVE;
    public static MetaTileEntityMultiblockTank BRONZE_TANK;

    // SIMPLE MACHINES SECTION
    public static SimpleMachineMetaTileEntity ELECTRIC_FURNACE;
    public static SimpleMachineMetaTileEntity MACERATOR;
    public static SimpleMachineMetaTileEntity ALLOY_SMELTER;
    public static SimpleMachineMetaTileEntity BENDER;
    public static SimpleMachineMetaTileEntity BREWERY;
    public static SimpleMachineMetaTileEntity CENTRIFUGE;
    public static SimpleMachineMetaTileEntity CHEMICAL_BATH;
    public static SimpleMachineMetaTileEntity COMPRESSOR;
    public static SimpleMachineMetaTileEntity CUTTER;
    public static SimpleMachineMetaTileEntity EXTRACTOR;
    public static SimpleMachineMetaTileEntity FERMENTER;
    public static SimpleMachineMetaTileEntity FORGE_HAMMER;
    public static SimpleMachineMetaTileEntity LATHE;
    public static SimpleMachineMetaTileEntity MIXER;
    public static SimpleMachineMetaTileEntity ORE_WASHER;
    public static SimpleMachineMetaTileEntity PACKER;
    public static SimpleMachineMetaTileEntity SIFTER;
    public static SimpleMachineMetaTileEntity WIREMILL;

    public static SimpleGeneratorMetaTileEntity STEAM_TURBINE;
    public static SimpleGeneratorMetaTileEntity COMBUSTION_GENERATOR;

    public static ResourceLocation gtsId(String id) {
        return new ResourceLocation(MODID, id);
    }

    public static void initialization() {
        ALLOY_KILN = registerMetaTileEntity(1, new MetaTileEntityAlloyKiln(gtsId("alloy_klin")));
        ALLOY_KILN_IMPORT_HATCH = registerMetaTileEntity(2, new MetaTileEntityAlloyKilnImportHatch(gtsId("alloy_klin_import_hatch")));
        ALLOY_KILN_EXPORT_HATCH = registerMetaTileEntity(3, new MetaTileEntityAlloyKilnExportHatch(gtsId("alloy_klin_export_hatch")));
        INDUSTRIAL_PRIMITIVE_BLAST_FURNACE = registerMetaTileEntity(4, new MetaTileEntityIndustrialPrimitiveBlastFurnace(gtsId("industrial_primitive_blast_furnace")));
        STEAM_COMPRESSOR = registerMetaTileEntity(5, new MetaTileEntitySteamCompressor(gtsId("steam_compressor")));
        STEAM_EXTRACTOR = registerMetaTileEntity(6, new MetaTileEntitySteamExtractor(gtsId("steam_extractor")));
        STEAM_BLAST_FURNACE = registerMetaTileEntity(7, new MetaTileEntitySteamBlastFurnace(gtsId("steam_blast_furnace")));
        STEAM_ORE_WASHER = registerMetaTileEntity(8, new MetaTileEntitySteamOreWasher(gtsId("steam_ore_washer")));
        STEAM_HAMMER = registerMetaTileEntity(9, new MetaTileEntitySteamHammer(gtsId("steam_hammer")));
        STEAM_CENTRIFUGE = registerMetaTileEntity(10, new MetaTileEntitySteamCentrifuge(gtsId("steam_centrifuge")));
        STEAM_MIXER = registerMetaTileEntity(11, new MetaTileEntitySteamMixer(gtsId("steam_mixer")));
        STEAM_WIRE_MILL = registerMetaTileEntity(12, new MetaTileEntitySteamWireMill(gtsId("steam_wire_mill")));
        STEAM_BENDER = registerMetaTileEntity(13, new MetaTileEntitySteamBender(gtsId("steam_bender")));
        STEAM_SIFTER = registerMetaTileEntity(14, new MetaTileEntitySteamSifter(gtsId("steam_sifter")));
        STEAM_LATHE =  registerMetaTileEntity(15, new MetaTileEntitySteamLathe(gtsId("steam_lathe")));
        SAW_MILL=  registerMetaTileEntity(16, new MetaTileEntitySawMill(gtsId("saw_mill")));

        WATER_PUMP = registerMetaTileEntity(20, new MetaTileEntityPrimitiveWaterPump(gtsId("primitive_water_pump")));
        STEAM_FERMENTATION_VAT = registerMetaTileEntity(21, new MetaTileEntitySteamFermentationVat(gtsId("steam_fermentation_vat")));
        ADVANCED_COKE_OVEN = registerMetaTileEntity(22, new MetaTileEntityAdvancedCokeOven(gtsId("advanced_coke_oven")));

        BRONZE_TANK_VALVE = registerMetaTileEntity(25, new MetaTileEntityTankValve(gtsId("tank_valve.bronze")));
        BRONZE_TANK = registerMetaTileEntity(26, new MetaTileEntityMultiblockTank(gtsId("tank.bronze"), 750 * 1000));

        STEAM_TRANSCENDENT_PLASMA_FORGE = registerMetaTileEntity(30, new MetaTileEntitySteamTranscendentPlasmaForge(gtsId("steam_transcendent_plasma_forge")));
        STEAM_BIOMIMETIC_FACTORY = registerMetaTileEntity(31, new MetaTileEntitySteamBiomimeticFactory(gtsId("steam_biomimetic_factory")));

        //单方块ULV
        ELECTRIC_FURNACE = registerMetaTileEntity(40, new SimpleMachineMetaTileEntity(gtsId("electric_furnace.ulv"), RecipeMaps.FURNACE_RECIPES, Textures.FURNACE_OVERLAY, 0, false));
        MACERATOR = registerMetaTileEntity(41, new SimpleMachineMetaTileEntity(gtsId("macerator.ulv"), RecipeMaps.MACERATOR_RECIPES, Textures.MACERATOR_OVERLAY, 0, false));
        ALLOY_SMELTER = registerMetaTileEntity(42, new SimpleMachineMetaTileEntity(gtsId("alloy_smelter.ulv"), RecipeMaps.ALLOY_SMELTER_RECIPES, Textures.ALLOY_SMELTER_OVERLAY, 0, false));
        BENDER = registerMetaTileEntity(43, new SimpleMachineMetaTileEntity(gtsId("bender.ulv"), RecipeMaps.BENDER_RECIPES, Textures.BENDER_OVERLAY, 0, true));
        BREWERY = registerMetaTileEntity(44, new SimpleMachineMetaTileEntity(gtsId("brewery.ulv"), RecipeMaps.BREWING_RECIPES, Textures.BREWERY_OVERLAY, 0, true, GTUtility.hvCappedTankSizeFunction));
        CENTRIFUGE = registerMetaTileEntity(45, new SimpleMachineMetaTileEntity(gtsId("centrifuge.ulv"), RecipeMaps.CENTRIFUGE_RECIPES, Textures.CENTRIFUGE_OVERLAY, 0, false, GTUtility.largeTankSizeFunction));
        CHEMICAL_BATH = registerMetaTileEntity(46, new SimpleMachineMetaTileEntity(gtsId("chemical_bath.ulv"), RecipeMaps.CHEMICAL_BATH_RECIPES, Textures.CHEMICAL_BATH_OVERLAY, 0, true, GTUtility.hvCappedTankSizeFunction));
        COMPRESSOR = registerMetaTileEntity(47, new SimpleMachineMetaTileEntity(gtsId("compressor.ulv"), RecipeMaps.COMPRESSOR_RECIPES, Textures.COMPRESSOR_OVERLAY, 0, true));
        CUTTER = registerMetaTileEntity(48, new SimpleMachineMetaTileEntity(gtsId("cutter.ulv"), RecipeMaps.CUTTER_RECIPES, Textures.CUTTER_OVERLAY, 0, true));
        EXTRACTOR = registerMetaTileEntity(49, new SimpleMachineMetaTileEntity(gtsId("extractor.ulv"), RecipeMaps.EXTRACTOR_RECIPES, Textures.EXTRACTOR_OVERLAY, 0, true));
        FERMENTER = registerMetaTileEntity(50, new SimpleMachineMetaTileEntity(gtsId("fermenter.ulv"), RecipeMaps.FERMENTING_RECIPES, Textures.FERMENTER_OVERLAY, 0, true, GTUtility.hvCappedTankSizeFunction));
        FORGE_HAMMER = registerMetaTileEntity(51, new SimpleMachineMetaTileEntity(gtsId("forge_hammer.ulv"), RecipeMaps.FORGE_HAMMER_RECIPES, Textures.FORGE_HAMMER_OVERLAY, 0, true));
        LATHE = registerMetaTileEntity(52, new SimpleMachineMetaTileEntity(gtsId("lathe.ulv"), RecipeMaps.LATHE_RECIPES, Textures.LATHE_OVERLAY, 0, true));
        MIXER = registerMetaTileEntity(53, new SimpleMachineMetaTileEntity(gtsId("mixer.ulv"), RecipeMaps.MIXER_RECIPES, Textures.MIXER_OVERLAY, 0, false, GTUtility.hvCappedTankSizeFunction));
        ORE_WASHER = registerMetaTileEntity(54, new SimpleMachineMetaTileEntity(gtsId("ore_washer.ulv"), RecipeMaps.ORE_WASHER_RECIPES, Textures.ORE_WASHER_OVERLAY, 0, true));
        PACKER = registerMetaTileEntity(55, new SimpleMachineMetaTileEntity(gtsId("packer.ulv"), RecipeMaps.PACKER_RECIPES, Textures.PACKER_OVERLAY, 0, true));
        SIFTER = registerMetaTileEntity(56, new SimpleMachineMetaTileEntity(gtsId("sifter.ulv"), RecipeMaps.SIFTER_RECIPES, Textures.SIFTER_OVERLAY, 0, true));
        WIREMILL = registerMetaTileEntity(57, new SimpleMachineMetaTileEntity(gtsId("wiremill.ulv"), RecipeMaps.WIREMILL_RECIPES, Textures.WIREMILL_OVERLAY, 0, true));

        //发电机ULV
        STEAM_TURBINE = registerMetaTileEntity(60, new MetaTileEntitySingleTurbine(gtsId("steam_turbine.ulv"), PRIMITIVE_STEAM_TURBINE_FUELS, Textures.STEAM_TURBINE_OVERLAY, 0, tier -> 2000));
        COMBUSTION_GENERATOR = registerMetaTileEntity(61, new MetaTileEntitySingleCombustion(gtsId("combustion_generator.ulv"), PRIMITIVE_COMBUSTION_GENERATOR_FUELS, Textures.COMBUSTION_GENERATOR_OVERLAY, 0, tier -> 2000));

    }
}
