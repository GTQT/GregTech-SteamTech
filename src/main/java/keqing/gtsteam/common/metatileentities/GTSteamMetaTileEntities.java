package keqing.gtsteam.common.metatileentities;

import gregtech.api.metatileentity.registry.MTEManager;
import gregtech.api.util.GTUtility;
import gregtech.common.metatileentities.multi.MetaTileEntityMultiblockTank;
import gregtech.common.metatileentities.multi.MetaTileEntityTankValve;
import keqing.gtsteam.common.metatileentities.multi.MetaTileEntityAlloyKiln;
import keqing.gtsteam.common.metatileentities.multi.MetaTileEntityIndustrialPrimitiveBlastFurnace;
import keqing.gtsteam.common.metatileentities.multi.MetaTileEntityPrimitiveWaterPump;
import keqing.gtsteam.common.metatileentities.multi.multipart.MetaTileEntityAlloyKilnExportHatch;
import keqing.gtsteam.common.metatileentities.multi.multipart.MetaTileEntityAlloyKilnImportHatch;
import keqing.gtsteam.common.metatileentities.multi.steam.*;
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
    public static MetaTileEntityPrimitiveWaterPump WATER_PUMP;
    public static MetaTileEntitySteamCentrifuge STEAM_CENTRIFUGE;
    public static MetaTileEntityTankValve BRONZE_TANK_VALVE;
    public static MetaTileEntityMultiblockTank BRONZE_TANK;

    public static ResourceLocation gtsId(String id) {
        return new ResourceLocation(MODID, id);
    }
    public static void initialization() {


        ALLOY_KILN = registerMetaTileEntity(1, new MetaTileEntityAlloyKiln(gtsId("alloy_klin")));
        ALLOY_KILN_IMPORT_HATCH= registerMetaTileEntity(2, new MetaTileEntityAlloyKilnImportHatch(gtsId("alloy_klin_import_hatch")));
        ALLOY_KILN_EXPORT_HATCH= registerMetaTileEntity(3, new MetaTileEntityAlloyKilnExportHatch(gtsId("alloy_klin_export_hatch")));
        INDUSTRIAL_PRIMITIVE_BLAST_FURNACE= registerMetaTileEntity(4, new MetaTileEntityIndustrialPrimitiveBlastFurnace(gtsId("industrial_primitive_blast_furnace")));
        STEAM_COMPRESSOR = registerMetaTileEntity(5, new MetaTileEntitySteamCompressor(gtsId("steam_compressor")));
        STEAM_EXTRACTOR = registerMetaTileEntity(6, new MetaTileEntitySteamExtractor(gtsId("steam_extractor")));
        STEAM_BLAST_FURNACE = registerMetaTileEntity(7, new MetaTileEntitySteamBlastFurnace(gtsId("steam_blast_furnace")));
        STEAM_ORE_WASHER = registerMetaTileEntity(8, new MetaTileEntitySteamOreWasher(gtsId("steam_ore_washer")));
        STEAM_HAMMER = registerMetaTileEntity(9, new MetaTileEntitySteamHammer(gtsId("steam_hammer")));
        STEAM_CENTRIFUGE = registerMetaTileEntity(10, new MetaTileEntitySteamCentrifuge(gtsId("steam_centrifuge")));
        STEAM_MIXER = registerMetaTileEntity(11, new MetaTileEntitySteamMixer(gtsId("steam_mixer")));

        WATER_PUMP = registerMetaTileEntity(12, new MetaTileEntityPrimitiveWaterPump(gtsId("primitive_water_pump")));

        BRONZE_TANK_VALVE = registerMetaTileEntity(20, new MetaTileEntityTankValve(gtsId("tank_valve.bronze"), true));
        BRONZE_TANK = registerMetaTileEntity(21, new MetaTileEntityMultiblockTank(gtsId("tank.bronze"), true, 800000));
    }
}
