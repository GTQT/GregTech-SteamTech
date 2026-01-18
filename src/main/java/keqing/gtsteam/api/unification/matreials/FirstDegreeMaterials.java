package keqing.gtsteam.api.unification.matreials;

import gregtech.api.fluids.FluidBuilder;
import gregtech.api.unification.material.Material;
import keqing.gtsteam.api.unification.GTSteamMaterials;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.Materials.Steel;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialFlags.GENERATE_RING;
import static gregtech.api.unification.material.info.MaterialIconSet.SHINY;
import static keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities.gtsId;

public class FirstDegreeMaterials {

    private static int startId = 0;

    private static final int END_ID = startId + 100;

    private static int getMaterialsId() {
        if (startId < END_ID) {
            return startId++;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public FirstDegreeMaterials() {
    }

    public static void register() {
        //骨陶瓷粘土
        GTSteamMaterials.BoneCeramicClay = new Material.Builder(getMaterialsId(), gtsId("bone_ceramic_clay"))
                .dust()
                .color(0xC4C4C4)
                .flags(DISABLE_DECOMPOSITION)
                .build()
                .setTooltips("最原始的复合陶瓷粘土");

        //镀锌钢
        GTSteamMaterials.GalvanizedSteel = new Material.Builder(getMaterialsId(), gtsId("galvanized_steel"))
                .ingot()
                .color(0xb5b5b5)
                .components(Iron, 9, Zinc, 1)
                .iconSet(SHINY)
                .arcSmeltInto(Steel)
                .flags(DISABLE_DECOMPOSITION, NO_WORKING, NO_SMASHING, NO_SMELTING, GENERATE_ROUND, GENERATE_FRAME, GENERATE_ROTOR, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_BOLT_SCREW, GENERATE_PLATE, GENERATE_SPRING_SMALL, GENERATE_SPRING, GENERATE_RING)
                .build();

        //橡胶浆料
        GTSteamMaterials.RubberPulp = new Material.Builder(getMaterialsId(), gtsId("rubber_pulp"))
                .fluid()
                .color(0xF5F5DC)
                .flags(DISABLE_DECOMPOSITION)
                .build()
                .setTooltips("最原始的橡胶浆料");

        // 生橡胶乳清液
        GTSteamMaterials.RawRubberWhey = new Material.Builder(getMaterialsId(), gtsId("raw_rubber_whey"))
                .fluid()
                .color(0xF0E68C)
                .flags(DISABLE_DECOMPOSITION)
                .build()
                .setTooltips("含橡胶颗粒的乳状液体，需要进一步分离");

        // 生橡胶沉淀
        GTSteamMaterials.RawRubberPrecipitate = new Material.Builder(getMaterialsId(), gtsId("raw_rubber_precipitate"))
                .ingot()
                .color(0xD2B48C)
                .flags(DISABLE_DECOMPOSITION)
                .build()
                .setTooltips("未纯化的橡胶沉淀，含有杂质");
    }
}
