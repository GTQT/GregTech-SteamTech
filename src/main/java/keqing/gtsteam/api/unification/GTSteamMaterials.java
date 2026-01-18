package keqing.gtsteam.api.unification;

import gregtech.api.unification.material.Material;
import keqing.gtsteam.api.unification.matreials.FirstDegreeMaterials;
import keqing.gtsteam.api.unification.matreials.HigherDegreeMaterials;
import keqing.gtsteam.api.unification.matreials.SecondDegreeMaterials;

public class GTSteamMaterials {

    public static Material BoneCeramicClay;
    public static Material GalvanizedSteel;
    public static Material RubberPulp;
    public static Material RawRubberWhey;
    public static Material RawRubberPrecipitate;

    public GTSteamMaterials() {
    }

    public static void register() {
        FirstDegreeMaterials.register();
        SecondDegreeMaterials.register();
        HigherDegreeMaterials.register();
    }
}
