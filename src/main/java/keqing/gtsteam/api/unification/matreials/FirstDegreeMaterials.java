package keqing.gtsteam.api.unification.matreials;

import gregtech.api.fluids.FluidBuilder;
import gregtech.api.unification.material.Material;
import keqing.gtsteam.api.unification.GTSteamMaterials;

import static gregtech.api.unification.material.Materials.Water;
import static gregtech.api.unification.material.info.MaterialFlags.DISABLE_DECOMPOSITION;
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
                .build();

    }
}
