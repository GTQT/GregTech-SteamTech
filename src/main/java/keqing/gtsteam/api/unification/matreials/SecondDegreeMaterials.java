package keqing.gtsteam.api.unification.matreials;

import gregtech.api.unification.material.Material;
import keqing.gtsteam.api.unification.GTSteamMaterials;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.util.GTUtility.gregtechId;

public class SecondDegreeMaterials {
    private static int startId = 100;

    private static final int END_ID = startId + 100;

    public SecondDegreeMaterials() {
    }

    private static int getMaterialsId() {
        if (startId < END_ID) {
            return startId++;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public static void register() {
        //粗煤气
        GTSteamMaterials.RawCoalGas = Material.builder(getMaterialsId(), gregtechId("raw_coal_gas"))
                .gas()
                .color(0x333333)
                .build();

        //粗煤焦油
        GTSteamMaterials.RawCoalTar = Material.builder(getMaterialsId(), gregtechId("raw_coal_tar"))
                .fluid().color(0x1A1A1A).flags(STICKY, FLAMMABLE).build();

        // 含硫废液
        GTSteamMaterials.SulfuricWasteFluid = Material.builder(getMaterialsId(), gregtechId("sulfuric_waste_fluid"))
                .fluid()
                .color(0x8B4513)
                .flags(DISABLE_DECOMPOSITION)
                .components(Sulfur, 1, SodiumHydroxide, 1)
                .build()
                .setTooltips("煤化工脱硫副产物");

        // 沥青
        GTSteamMaterials.Asphalt = Material.builder(getMaterialsId(), gregtechId("asphalt"))
                .fluid().dust()
                .color(0x2F4F4F)
                .flags(DISABLE_DECOMPOSITION)
                .components(Carbon, 15, Hydrogen, 1)
                .build()
                .setTooltips("煤焦油脱沥青的产物");

        // 富乙烯气
        GTSteamMaterials.EthyleneRichGas = Material.builder(getMaterialsId(), gregtechId("ethylene_rich_gas"))
                .gas()
                .color(0xA0D6B4)
                .flags(DISABLE_DECOMPOSITION)
                .components(Carbon, 2, Hydrogen, 4)
                .build()
                .setTooltips("富含乙烯的气体混合物，可直接燃烧或作为化工原料");

        // 富甲烷气
        GTSteamMaterials.MethaneRichGas = Material.builder(getMaterialsId(), gregtechId("methane_rich_gas"))
                .gas()
                .color(0xCCCCCC)
                .flags(DISABLE_DECOMPOSITION)
                .components(Carbon, 1, Hydrogen, 4)
                .build()
                .setTooltips("富含甲烷的气体混合物，高热值清洁燃料");

        // 合成气
        GTSteamMaterials.SynthesisGas = Material.builder(getMaterialsId(), gregtechId("synthesis_gas"))
                .gas()
                .color(0xE0E0FF)
                .flags(DISABLE_DECOMPOSITION)
                .components(CarbonMonoxide, 1, Hydrogen, 2)
                .build()
                .setTooltips("一氧化碳和氢气的混合物，可用于燃烧、制氢或化学合成");
    }
}
