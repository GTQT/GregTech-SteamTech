package meowmel.gtsteam.api.unification.matreials;

import gregtech.api.unification.material.Material;
import meowmel.gtsteam.api.unification.GTSteamMaterials;

import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.*;
import static gregtech.api.unification.material.info.MaterialIconSet.LIGNITE;
import static gregtech.api.unification.material.info.MaterialIconSet.SHINY;
import static meowmel.gtsteam.common.metatileentities.GTSteamMetaTileEntities.gtsId;

public class FirstDegreeMaterials {

    private static int startId = 0;

    private static final int END_ID = startId + 100;

    public FirstDegreeMaterials() {
    }

    private static int getMaterialsId() {
        if (startId < END_ID) {
            return startId++;
        }
        throw new ArrayIndexOutOfBoundsException();
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

        //褐煤
        GTSteamMaterials.Lignite = new Material.Builder(getMaterialsId(), gtsId("lignite"))
                .gem(1, 1200).ore(2, 1) // default coal burn time in vanilla
                .color(0x8B5A00).iconSet(LIGNITE)
                .flags(FLAMMABLE, NO_SMELTING, NO_SMASHING, MORTAR_GRINDABLE, EXCLUDE_BLOCK_CRAFTING_BY_HAND_RECIPES,
                        DISABLE_DECOMPOSITION)
                .components(Carbon, 1)
                .build()
                .setTooltips("煤化程度最低的矿产煤");

        //石灰浆
        GTSteamMaterials.Calcimine = new Material.Builder(getMaterialsId(), gtsId("calcimine"))
                .fluid()
                .color(0xE5E5E5)
                .flags(DISABLE_DECOMPOSITION)
                .build()
                .setTooltips("石灰的纯度较低的纯化物");
    }
}
