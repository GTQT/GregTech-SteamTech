package keqing.gtsteam.common.metatileentities;

import keqing.gtsteam.common.metatileentities.multi.primitive.MetaTileEntityAdvancedCokeOven;
import keqing.gtsteam.common.metatileentities.multi.primitive.MetaTileEntityAlloyKiln;
import keqing.gtsteam.common.metatileentities.multi.primitive.MetaTileEntityIndustrialPrimitiveBlastFurnace;
import keqing.gtsteam.common.metatileentities.multi.primitive.MetaTileEntityPrimitiveWaterPump;
import keqing.gtsteam.common.metatileentities.multi.multipart.MetaTileEntityAlloyKilnExportHatch;
import keqing.gtsteam.common.metatileentities.multi.multipart.MetaTileEntityAlloyKilnImportHatch;
import keqing.gtsteam.common.metatileentities.multi.steam.*;
import keqing.gtsteam.common.metatileentities.multi.steam.advanced.MetaTileEntitySteamBiomimeticFactory;
import keqing.gtsteam.common.metatileentities.multi.steam.advanced.MetaTileEntitySteamTranscendentPlasmaForge;
import keqing.gtsteam.common.metatileentities.multi.store.MetaTileEntityMultiblockTank;
import keqing.gtsteam.common.metatileentities.multi.store.MetaTileEntityTankValve;
import net.minecraft.util.ResourceLocation;

import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;
import static keqing.gtsteam.GTSteam.MODID;

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

    public static MetaTileEntitySteamTranscendentPlasmaForge STEAM_TRANSCENDENT_PLASMA_FORGE;
    public static MetaTileEntitySteamBiomimeticFactory STEAM_BIOMIMETIC_FACTORY;

    public static MetaTileEntityPrimitiveWaterPump WATER_PUMP;
    public static MetaTileEntitySteamFermentationVat STEAM_FERMENTATION_VAT;
    public static MetaTileEntityAdvancedCokeOven ADVANCED_COKE_OVEN;

    public static MetaTileEntityTankValve BRONZE_TANK_VALVE;
    public static MetaTileEntityMultiblockTank BRONZE_TANK;

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

        WATER_PUMP = registerMetaTileEntity(20, new MetaTileEntityPrimitiveWaterPump(gtsId("primitive_water_pump")));
        STEAM_FERMENTATION_VAT = registerMetaTileEntity(21, new MetaTileEntitySteamFermentationVat(gtsId("steam_fermentation_vat")));
        ADVANCED_COKE_OVEN= registerMetaTileEntity(22, new MetaTileEntityAdvancedCokeOven(gtsId("advanced_coke_oven")));

        BRONZE_TANK_VALVE = registerMetaTileEntity(25, new MetaTileEntityTankValve(gtsId("tank_valve.bronze")));
        BRONZE_TANK = registerMetaTileEntity(26, new MetaTileEntityMultiblockTank(gtsId("tank.bronze"), 750 * 1000));

        STEAM_TRANSCENDENT_PLASMA_FORGE= registerMetaTileEntity(30, new MetaTileEntitySteamTranscendentPlasmaForge(gtsId("steam_transcendent_plasma_forge")));
        STEAM_BIOMIMETIC_FACTORY = registerMetaTileEntity(31, new MetaTileEntitySteamBiomimeticFactory(gtsId("steam_biomimetic_factory")));
    }
}
