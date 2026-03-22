package meowmel.gtsteam.api.unification.matreials;

import gregtech.api.fluids.FluidBuilder;
import gregtech.api.unification.material.Material;
import meowmel.gtsteam.api.unification.GTSteamMaterials;

import static gregtech.api.unification.material.info.MaterialFlags.DISABLE_DECOMPOSITION;
import static meowmel.gtsteam.common.metatileentities.GTSteamMetaTileEntities.gtsId;

public class HigherDegreeMaterials {

    private static int startId = 200;

    private static final int END_ID = startId + 100;

    public HigherDegreeMaterials() {
    }

    private static int getMaterialsId() {
        if (startId < END_ID) {
            return startId++;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public static void register() {
        //橡胶浆料
        GTSteamMaterials.RubberPulp = new Material.Builder(getMaterialsId(), gtsId("rubber_pulp"))
                .fluid()
                .color(0xF5F5DC)
                .build()
                .setTooltips("最原始的橡胶浆料");

        // 生橡胶乳清液
        GTSteamMaterials.RawRubberWhey = new Material.Builder(getMaterialsId(), gtsId("raw_rubber_whey"))
                .fluid()
                .color(0xF0E68C)
                .build()
                .setTooltips("含橡胶颗粒的乳状液体，需要进一步分离");

        // 生橡胶沉淀
        GTSteamMaterials.RawRubberPrecipitate = new Material.Builder(getMaterialsId(), gtsId("raw_rubber_precipitate"))
                .ingot()
                .color(0xD2B48C)
                .build()
                .setTooltips("未纯化的橡胶沉淀，含有杂质");

        // 乳胶
        GTSteamMaterials.Latex = new Material.Builder(getMaterialsId(), gtsId("latex"))
                .dust()
                .polymer()
                .liquid(new FluidBuilder().temperature(373))
                .color(0xFFFADA)
                .build();

        // 树脂
        GTSteamMaterials.Resin = new Material.Builder(getMaterialsId(), gtsId("resin"))
                .dust()
                .polymer()
                .liquid(new FluidBuilder().temperature(373))
                .color(0xB5803A)
                .build();
    }
}
