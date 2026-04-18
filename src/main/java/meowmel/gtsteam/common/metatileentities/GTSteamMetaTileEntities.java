package meowmel.gtsteam.common.metatileentities;

import gregtech.api.metatileentity.SimpleMachineMetaTileEntity;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import gregtech.client.particle.VanillaParticleEffects;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.electric.MetaTileEntitySingleCombustion;
import gregtech.common.metatileentities.electric.MetaTileEntitySingleTurbine;
import gregtech.common.metatileentities.electric.SimpleMachineMetaTileEntityResizable;
import meowmel.gtsteam.common.metatileentities.combustor.CoalCombustor;
import meowmel.gtsteam.common.metatileentities.combustor.LavaCombustor;
import meowmel.gtsteam.common.metatileentities.combustor.SolarCombustor;
import meowmel.gtsteam.common.metatileentities.multi.generator.*;
import meowmel.gtsteam.common.metatileentities.multi.heat.*;
import meowmel.gtsteam.common.metatileentities.multi.multipart.MetaTileEntityPrimitiveExportHatch;
import meowmel.gtsteam.common.metatileentities.multi.multipart.MetaTileEntityPrimitiveImportHatch;
import meowmel.gtsteam.common.metatileentities.multi.primitive.*;
import meowmel.gtsteam.common.metatileentities.multi.steam.*;
import meowmel.gtsteam.common.metatileentities.multi.steam.advanced.MetaTileEntitySteamBiomimeticFactory;
import meowmel.gtsteam.common.metatileentities.multi.steam.advanced.MetaTileEntitySteamTranscendentPlasmaForge;
import meowmel.gtsteam.common.metatileentities.multi.store.*;
import meowmel.gtsteam.common.metatileentities.single.MetaTileEntityLatexCollector;
import net.minecraft.util.ResourceLocation;

import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;
import static meowmel.gtsteam.GTSteam.MODID;
import static meowmel.gtsteam.api.recipes.GTSRecipeMaps.*;

public class GTSteamMetaTileEntities {

    public static MetaTileEntityAlloyKiln ALLOY_KILN;
    public static MetaTileEntityPrimitiveFurnace PRIMITIVE_FURNACE;
    public static MetaTileEntityPrimitiveImportHatch PRIMITIVE_IMPORT_HATCH;
    public static MetaTileEntityPrimitiveExportHatch PRIMITIVE_EXPORT_HATCH;

    public static MetaTileEntityIndustrialPrimitiveBlastFurnace INDUSTRIAL_PRIMITIVE_BLAST_FURNACE;
    public static MetaTileEntityIndustrialCokeOven INDUSTRIAL_COKE_OVEN;
    public static MetaTileEntityCoagulationTank COAGULATION_TANK;

    public static MetaTileEntitySteamCompressor STEAM_COMPRESSOR;
    public static MetaTileEntitySteamExtractor STEAM_EXTRACTOR;
    public static MetaTileEntitySteamOreWasher STEAM_ORE_WASHER;
    public static MetaTileEntitySteamHammer STEAM_HAMMER;
    public static MetaTileEntitySteamMixer STEAM_MIXER;
    public static MetaTileEntitySteamCentrifuge STEAM_CENTRIFUGE;
    public static MetaTileEntitySteamWireMill STEAM_WIRE_MILL;
    public static MetaTileEntitySteamBender STEAM_BENDER;
    public static MetaTileEntitySteamAlloyFurnace STEAM_ALLOY_FURNACE;
    public static MetaTileEntitySteamSifter STEAM_SIFTER;
    public static MetaTileEntitySteamLathe STEAM_LATHE;
    public static MetaTileEntitySteamBrewing STEAM_BREWING;
    public static MetaTileEntitySteamLavaFurnace STEAM_LAVA_FURNACE;
    public static MetaTileEntitySteamWaterPump STEAM_WATER_PUMP;

    public static MetaTileEntitySteamTranscendentPlasmaForge STEAM_TRANSCENDENT_PLASMA_FORGE;
    public static MetaTileEntitySteamBiomimeticFactory STEAM_BIOMIMETIC_FACTORY;

    public static MetaTileEntityHeatFurnace HEAT_FURNACE;
    public static MetaTileEntityHeatAlloyFurnace HEAT_ALLOY_FURNACE;
    public static MetaTileEntityHeatCokeOven HEAT_COKE_OVEN;
    public static MetaTileEntityHeatDistillationTower HEAT_DISTILLATION_TOWER;
    public static MetaTileEntityHeatCrackingUnit HEAT_CRACKING_UNIT;
    public static MetaTileEntityHeatBrewingVat HEAT_BREWING_VAT;
    public static MetaTileEntityHeatFermenter HEAT_FERMENTER;
    public static MetaTileEntityHeatEvaporationPond HEAT_EVAPORATION_POND;
    public static MetaTileEntityHeatChemicalReactor HEAT_CHEMICAL_REACTOR;
    public static MetaTileEntityHeatThermalCentrifuge HEAT_THERMAL_CENTRIFUGE;
    public static MetaTileEntityHeatLavaFurnace HEAT_LAVA_FURNACE;
    public static MetaTileEntityHeatElectronicProcessor HEAT_ELECTRONIC_PROCESSOR;

    public static MetaTileEntityHeatFluidDrill HEAT_FLUID_DRILL;

    public static MetaTileEntitySteamSolarBoiler STEAM_SOLAR_BOILER;

    public static MetaTileEntityHeatRadiator HEAT_RADIATOR;

    public static MetaTileEntityLargeFluidTank LARGE_FLUID_TANK;

    //MIT License //Author iristhepianist //https://github.com/iristhepianist/ScalableStorageCEu/
    public static MetaTileEntityScalableStorage SCALABLE_STORAGE;

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
    public static SimpleMachineMetaTileEntity THERMAL_CENTRIFUGE;
    public static SimpleMachineMetaTileEntity WIREMILL;

    public static MetaTileEntitySingleTurbine STEAM_TURBINE;
    public static MetaTileEntitySingleTurbine GAS_TURBINE;
    public static MetaTileEntitySingleCombustion COMBUSTION_GENERATOR;
    public static MetaTileEntitySingleCombustion SEMI_FLUID_GENERATOR;

    public static MetaTileEntityPrimitiveCombustor LOW_PRESSURE_SOLID_COMBUSTOR;
    public static MetaTileEntityPrimitiveCombustor HIGH_PRESSURE_SOLID_COMBUSTOR;
    public static MetaTileEntityPrimitiveCombustor LOW_PRESSURE_FLUID_COMBUSTOR;
    public static MetaTileEntityPrimitiveCombustor HIGH_PRESSURE_FLUID_COMBUSTOR;

    public static MetaTileEntityLargeCombustor LARGE_BRONZE_COMBUSTOR;
    public static MetaTileEntityLargeCombustor LARGE_STEEL_COMBUSTOR;
    public static MetaTileEntityLargeCombustor LARGE_TITANIUM_COMBUSTOR;
    public static MetaTileEntityLargeCombustor LARGE_TUNGSTENSTEEL_COMBUSTOR;

    public static MetaTileEntityCombustionCombustor COMBUSTION_COMBUSTOR;
    public static MetaTileEntityCombustionCombustor EXTREME_COMBUSTION_COMBUSTOR;

    public static MetaTileEntityHeatSteamBoiler HEAT_STEAM_BOILER;
    //热学
    static Material[] materials = new Material[]{Materials.Lead, Materials.Bronze, Materials.Steel, Materials.Invar, Materials.Chrome, Materials.Titanium};
    public static CoalCombustor[] COAL_COMBUSTOR = new CoalCombustor[materials.length];
    public static LavaCombustor[] LAVA_COMBUSTOR = new LavaCombustor[materials.length];
    public static SolarCombustor[] SOLAR_COMBUSTOR = new SolarCombustor[materials.length];
    public static CoalCombustor[] DENSE_COAL_COMBUSTOR = new CoalCombustor[materials.length];
    public static LavaCombustor[] DENSE_LAVA_COMBUSTOR = new LavaCombustor[materials.length];
    public static SolarCombustor[] DENSE_SOLAR_COMBUSTOR = new SolarCombustor[materials.length];
    //
    public static MetaTileEntityLatexCollector[] LATEX_COLLECTOR = new MetaTileEntityLatexCollector[5];

    public static ResourceLocation gtsId(String id) {
        return new ResourceLocation(MODID, id);
    }

    public static void initialization() {
        ALLOY_KILN = registerMetaTileEntity(1, new MetaTileEntityAlloyKiln(gtsId("alloy_klin")));
        PRIMITIVE_FURNACE = registerMetaTileEntity(2, new MetaTileEntityPrimitiveFurnace(gtsId("primitive_furnace")));

        PRIMITIVE_IMPORT_HATCH = registerMetaTileEntity(4, new MetaTileEntityPrimitiveImportHatch(gtsId("primitive_import_hatch")));
        PRIMITIVE_EXPORT_HATCH = registerMetaTileEntity(5, new MetaTileEntityPrimitiveExportHatch(gtsId("primitive_export_hatch")));


        INDUSTRIAL_PRIMITIVE_BLAST_FURNACE = registerMetaTileEntity(6, new MetaTileEntityIndustrialPrimitiveBlastFurnace(gtsId("industrial_primitive_blast_furnace")));
        INDUSTRIAL_COKE_OVEN = registerMetaTileEntity(7, new MetaTileEntityIndustrialCokeOven(gtsId("industrial_coke_oven")));
        COAGULATION_TANK = registerMetaTileEntity(8, new MetaTileEntityCoagulationTank(gtsId("coagulation_tank")));

        STEAM_COMPRESSOR = registerMetaTileEntity(10, new MetaTileEntitySteamCompressor(gtsId("steam_compressor")));
        STEAM_EXTRACTOR = registerMetaTileEntity(11, new MetaTileEntitySteamExtractor(gtsId("steam_extractor")));
        STEAM_ALLOY_FURNACE = registerMetaTileEntity(12, new MetaTileEntitySteamAlloyFurnace(gtsId("steam_alloy_furnace")));
        STEAM_ORE_WASHER = registerMetaTileEntity(13, new MetaTileEntitySteamOreWasher(gtsId("steam_ore_washer")));
        STEAM_HAMMER = registerMetaTileEntity(14, new MetaTileEntitySteamHammer(gtsId("steam_hammer")));
        STEAM_CENTRIFUGE = registerMetaTileEntity(15, new MetaTileEntitySteamCentrifuge(gtsId("steam_centrifuge")));
        STEAM_MIXER = registerMetaTileEntity(16, new MetaTileEntitySteamMixer(gtsId("steam_mixer")));
        STEAM_WIRE_MILL = registerMetaTileEntity(17, new MetaTileEntitySteamWireMill(gtsId("steam_wire_mill")));
        STEAM_BENDER = registerMetaTileEntity(18, new MetaTileEntitySteamBender(gtsId("steam_bender")));
        STEAM_SIFTER = registerMetaTileEntity(19, new MetaTileEntitySteamSifter(gtsId("steam_sifter")));
        STEAM_LATHE = registerMetaTileEntity(20, new MetaTileEntitySteamLathe(gtsId("steam_lathe")));
        STEAM_BREWING = registerMetaTileEntity(21, new MetaTileEntitySteamBrewing(gtsId("steam_brewing")));
        STEAM_LAVA_FURNACE = registerMetaTileEntity(22, new MetaTileEntitySteamLavaFurnace(gtsId("steam_lava_furnace")));

        SCALABLE_STORAGE = registerMetaTileEntity(25, new MetaTileEntityScalableStorage(gtsId("scalable_storage")));

        STEAM_SOLAR_BOILER = registerMetaTileEntity(30, new MetaTileEntitySteamSolarBoiler(gtsId("steam_solar_boiler")));

        HEAT_RADIATOR = registerMetaTileEntity(40, new MetaTileEntityHeatRadiator(gtsId("heat_radiator")));

        LARGE_FLUID_TANK = registerMetaTileEntity(45, new MetaTileEntityLargeFluidTank(gtsId("large_fluid_tank")));

        STEAM_WATER_PUMP = registerMetaTileEntity(50, new MetaTileEntitySteamWaterPump(gtsId("steam_water_pump")));

        STEAM_TRANSCENDENT_PLASMA_FORGE = registerMetaTileEntity(60, new MetaTileEntitySteamTranscendentPlasmaForge(gtsId("steam_transcendent_plasma_forge")));
        STEAM_BIOMIMETIC_FACTORY = registerMetaTileEntity(61, new MetaTileEntitySteamBiomimeticFactory(gtsId("steam_biomimetic_factory")));

        // ID 70：
        HEAT_FURNACE = registerMetaTileEntity(70, new MetaTileEntityHeatFurnace(gtsId("heat_furnace")));
        HEAT_ALLOY_FURNACE = registerMetaTileEntity(71, new MetaTileEntityHeatAlloyFurnace(gtsId("heat_alloy_furnace")));
        HEAT_COKE_OVEN = registerMetaTileEntity(72, new MetaTileEntityHeatCokeOven(gtsId("heat_coke_oven")));
        HEAT_DISTILLATION_TOWER = registerMetaTileEntity(73, new MetaTileEntityHeatDistillationTower(gtsId("heat_distillation_tower")));
        HEAT_CRACKING_UNIT = registerMetaTileEntity(74, new MetaTileEntityHeatCrackingUnit(gtsId("heat_cracking_unit")));
        HEAT_BREWING_VAT = registerMetaTileEntity(75, new MetaTileEntityHeatBrewingVat(gtsId("heat_brewing_vat")));
        HEAT_FERMENTER = registerMetaTileEntity(76, new MetaTileEntityHeatFermenter(gtsId("heat_fermenter")));
        HEAT_EVAPORATION_POND = registerMetaTileEntity(77, new MetaTileEntityHeatEvaporationPond(gtsId("heat_evaporation_pond")));
        HEAT_CHEMICAL_REACTOR = registerMetaTileEntity(78, new MetaTileEntityHeatChemicalReactor(gtsId("heat_chemical_reactor")));
        HEAT_THERMAL_CENTRIFUGE = registerMetaTileEntity(79, new MetaTileEntityHeatThermalCentrifuge(gtsId("heat_thermal_centrifuge")));
        HEAT_LAVA_FURNACE = registerMetaTileEntity(80, new MetaTileEntityHeatLavaFurnace(gtsId("heat_lava_furnace")));
        HEAT_ELECTRONIC_PROCESSOR = registerMetaTileEntity(81, new MetaTileEntityHeatElectronicProcessor(gtsId("heat_electronic_processor")));

        HEAT_FLUID_DRILL = registerMetaTileEntity(90, new MetaTileEntityHeatFluidDrill(gtsId("heat_fluid_drill")));

        //单方块ULV
        ELECTRIC_FURNACE = registerMetaTileEntity(100, new SimpleMachineMetaTileEntity(gtsId("electric_furnace.ulv"), RecipeMaps.FURNACE_RECIPES, Textures.FURNACE_OVERLAY, 0, false));
        MACERATOR = registerMetaTileEntity(101, new SimpleMachineMetaTileEntityResizable(gtsId("macerator.ulv"), RecipeMaps.MACERATOR_RECIPES, -1, 1, Textures.MACERATOR_OVERLAY, 0, true, GTUtility.defaultTankSizeFunction, VanillaParticleEffects.TOP_SMOKE_SMALL, null));
        ALLOY_SMELTER = registerMetaTileEntity(102, new SimpleMachineMetaTileEntity(gtsId("alloy_smelter.ulv"), RecipeMaps.ALLOY_SMELTER_RECIPES, Textures.ALLOY_SMELTER_OVERLAY, 0, false));
        BENDER = registerMetaTileEntity(103, new SimpleMachineMetaTileEntity(gtsId("bender.ulv"), RecipeMaps.BENDER_RECIPES, Textures.BENDER_OVERLAY, 0, true));
        BREWERY = registerMetaTileEntity(104, new SimpleMachineMetaTileEntity(gtsId("brewery.ulv"), RecipeMaps.BREWING_RECIPES, Textures.BREWERY_OVERLAY, 0, true, GTUtility.hvCappedTankSizeFunction));
        CENTRIFUGE = registerMetaTileEntity(105, new SimpleMachineMetaTileEntity(gtsId("centrifuge.ulv"), RecipeMaps.CENTRIFUGE_RECIPES, Textures.CENTRIFUGE_OVERLAY, 0, false, GTUtility.largeTankSizeFunction));
        CHEMICAL_BATH = registerMetaTileEntity(106, new SimpleMachineMetaTileEntity(gtsId("chemical_bath.ulv"), RecipeMaps.CHEMICAL_BATH_RECIPES, Textures.CHEMICAL_BATH_OVERLAY, 0, true, GTUtility.hvCappedTankSizeFunction));
        COMPRESSOR = registerMetaTileEntity(107, new SimpleMachineMetaTileEntity(gtsId("compressor.ulv"), RecipeMaps.COMPRESSOR_RECIPES, Textures.COMPRESSOR_OVERLAY, 0, true));
        CUTTER = registerMetaTileEntity(108, new SimpleMachineMetaTileEntity(gtsId("cutter.ulv"), RecipeMaps.CUTTER_RECIPES, Textures.CUTTER_OVERLAY, 0, true));
        EXTRACTOR = registerMetaTileEntity(109, new SimpleMachineMetaTileEntity(gtsId("extractor.ulv"), RecipeMaps.EXTRACTOR_RECIPES, Textures.EXTRACTOR_OVERLAY, 0, true));
        FERMENTER = registerMetaTileEntity(110, new SimpleMachineMetaTileEntity(gtsId("fermenter.ulv"), RecipeMaps.FERMENTING_RECIPES, Textures.FERMENTER_OVERLAY, 0, true, GTUtility.hvCappedTankSizeFunction));
        FORGE_HAMMER = registerMetaTileEntity(111, new SimpleMachineMetaTileEntity(gtsId("forge_hammer.ulv"), RecipeMaps.FORGE_HAMMER_RECIPES, Textures.FORGE_HAMMER_OVERLAY, 0, true));
        LATHE = registerMetaTileEntity(112, new SimpleMachineMetaTileEntity(gtsId("lathe.ulv"), RecipeMaps.LATHE_RECIPES, Textures.LATHE_OVERLAY, 0, true));
        MIXER = registerMetaTileEntity(113, new SimpleMachineMetaTileEntity(gtsId("mixer.ulv"), RecipeMaps.MIXER_RECIPES, Textures.MIXER_OVERLAY, 0, false, GTUtility.hvCappedTankSizeFunction));
        ORE_WASHER = registerMetaTileEntity(114, new SimpleMachineMetaTileEntity(gtsId("ore_washer.ulv"), RecipeMaps.ORE_WASHER_RECIPES, Textures.ORE_WASHER_OVERLAY, 0, true));
        PACKER = registerMetaTileEntity(115, new SimpleMachineMetaTileEntity(gtsId("packer.ulv"), RecipeMaps.PACKER_RECIPES, Textures.PACKER_OVERLAY, 0, true));
        SIFTER = registerMetaTileEntity(116, new SimpleMachineMetaTileEntity(gtsId("sifter.ulv"), RecipeMaps.SIFTER_RECIPES, Textures.SIFTER_OVERLAY, 0, true));
        THERMAL_CENTRIFUGE = registerMetaTileEntity(117, new SimpleMachineMetaTileEntity(gtsId("thermal_centrifuge.ulv"), RecipeMaps.THERMAL_CENTRIFUGE_RECIPES, Textures.THERMAL_CENTRIFUGE_OVERLAY, 0, true));
        WIREMILL = registerMetaTileEntity(118, new SimpleMachineMetaTileEntity(gtsId("wiremill.ulv"), RecipeMaps.WIREMILL_RECIPES, Textures.WIREMILL_OVERLAY, 0, true));

        //发电机ULV
        STEAM_TURBINE = registerMetaTileEntity(120, new MetaTileEntitySingleTurbine(gtsId("steam_turbine.ulv"), PRIMITIVE_STEAM_TURBINE_FUELS, Textures.STEAM_TURBINE_OVERLAY, 0, tier -> 2000, 1.0));
        GAS_TURBINE = registerMetaTileEntity(121, new MetaTileEntitySingleTurbine(gtsId("gas_turbine.ulv"), PRIMITIVE_GAS_TURBINE_FUELS, Textures.GAS_TURBINE_OVERLAY, 0, tier -> 1000, 1.0));
        COMBUSTION_GENERATOR = registerMetaTileEntity(122, new MetaTileEntitySingleCombustion(gtsId("combustion_generator.ulv"), PRIMITIVE_COMBUSTION_GENERATOR_FUELS, Textures.COMBUSTION_GENERATOR_OVERLAY, 0, tier -> 1000, 1.0));
        SEMI_FLUID_GENERATOR = registerMetaTileEntity(123, new MetaTileEntitySingleCombustion(gtsId("semi_fluid_generator.ulv"), PRIMITIVE_SEMI_FLUID_GENERATOR_FUELS, Textures.SEMI_FLUID_OVERLAY, 0, tier -> 1000, 1.0));


        //热学系统
        for (int i = 0; i < materials.length; i++) {
            String materialName = materials[i].toString().toLowerCase();
            COAL_COMBUSTOR[i] = registerMetaTileEntity(200 + i, new CoalCombustor(gtsId("coal_combustor." + materialName), false, i + 1, materials[i]));
            DENSE_COAL_COMBUSTOR[i] = registerMetaTileEntity(210 + i, new CoalCombustor(gtsId("dense_coal_combustor." + materialName), true, i + 1, materials[i]));

            LAVA_COMBUSTOR[i] = registerMetaTileEntity(220 + i, new LavaCombustor(gtsId("lava_combustor." + materialName), false, i + 1, materials[i]));
            DENSE_LAVA_COMBUSTOR[i] = registerMetaTileEntity(230 + i, new LavaCombustor(gtsId("dense_lava_combustor." + materialName), true, i + 1, materials[i]));

            SOLAR_COMBUSTOR[i] = registerMetaTileEntity(240 + i, new SolarCombustor(gtsId("solar_combustor." + materialName), false, i + 1, materials[i]));
            DENSE_SOLAR_COMBUSTOR[i] = registerMetaTileEntity(250 + i, new SolarCombustor(gtsId("dense_solar_combustor." + materialName), true, i + 1, materials[i]));
        }

        //燃烧室
        LOW_PRESSURE_SOLID_COMBUSTOR = registerMetaTileEntity(300, new MetaTileEntityPrimitiveCombustor(gtsId("solid_combustor.low_pressure"), PrimitiveCombustorType.LOW_PRESSURE_SOLID));
        HIGH_PRESSURE_SOLID_COMBUSTOR = registerMetaTileEntity(301, new MetaTileEntityPrimitiveCombustor(gtsId("solid_combustor.high_pressure"), PrimitiveCombustorType.HIGH_PRESSURE_SOLID));
        LOW_PRESSURE_FLUID_COMBUSTOR = registerMetaTileEntity(302, new MetaTileEntityPrimitiveCombustor(gtsId("fluid_combustor.low_pressure"), PrimitiveCombustorType.LOW_PRESSURE_FLUID));
        HIGH_PRESSURE_FLUID_COMBUSTOR = registerMetaTileEntity(303, new MetaTileEntityPrimitiveCombustor(gtsId("fluid_combustor.high_pressure"), PrimitiveCombustorType.HIGH_PRESSURE_FLUID));

        LARGE_BRONZE_COMBUSTOR = registerMetaTileEntity(304,
                new MetaTileEntityLargeCombustor(gtsId("large_combustor.bronze"), CombustorType.BRONZE));
        LARGE_STEEL_COMBUSTOR = registerMetaTileEntity(305,
                new MetaTileEntityLargeCombustor(gtsId("large_combustor.steel"), CombustorType.STEEL));
        LARGE_TITANIUM_COMBUSTOR = registerMetaTileEntity(306,
                new MetaTileEntityLargeCombustor(gtsId("large_combustor.titanium"), CombustorType.TITANIUM));
        LARGE_TUNGSTENSTEEL_COMBUSTOR = registerMetaTileEntity(307,
                new MetaTileEntityLargeCombustor(gtsId("large_combustor.tungstensteel"), CombustorType.TUNGSTENSTEEL));

        COMBUSTION_COMBUSTOR = registerMetaTileEntity(308,
                new MetaTileEntityCombustionCombustor(gtsId("combustion_combustor"), CombustorType.TITANIUM));
        EXTREME_COMBUSTION_COMBUSTOR = registerMetaTileEntity(309,
                new MetaTileEntityCombustionCombustor(gtsId("extreme_combustion_combustor"), CombustorType.TUNGSTENSTEEL));

        //锅炉
        HEAT_STEAM_BOILER = registerMetaTileEntity(310, new MetaTileEntityHeatSteamBoiler(gtsId("heat_steam_boiler")));

        LATEX_COLLECTOR[0] = registerMetaTileEntity(500, new MetaTileEntityLatexCollector(gtsId("latex_collector.lv"), 1));
        LATEX_COLLECTOR[1] = registerMetaTileEntity(501, new MetaTileEntityLatexCollector(gtsId("latex_collector.mv"), 2));
        LATEX_COLLECTOR[2] = registerMetaTileEntity(502, new MetaTileEntityLatexCollector(gtsId("latex_collector.hv"), 3));
        LATEX_COLLECTOR[3] = registerMetaTileEntity(503, new MetaTileEntityLatexCollector(gtsId("latex_collector.ev"), 4));
        LATEX_COLLECTOR[4] = registerMetaTileEntity(504, new MetaTileEntityLatexCollector(gtsId("latex_collector.iv"), 5));
    }
}
