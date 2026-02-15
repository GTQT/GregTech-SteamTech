package meowmel.gtsteam.api.unification;

import gregtech.api.unification.material.Material;
import meowmel.gtsteam.api.unification.matreials.FirstDegreeMaterials;
import meowmel.gtsteam.api.unification.matreials.HigherDegreeMaterials;
import meowmel.gtsteam.api.unification.matreials.SecondDegreeMaterials;

public class GTSteamMaterials {

    public static Material BoneCeramicClay;
    public static Material GalvanizedSteel;
    public static Material Lignite;
    public static Material RubberPulp;
    public static Material RawRubberWhey;
    public static Material RawRubberPrecipitate;

    public static Material Calcimine;

    public static Material RawCoalGas;
    public static Material RawCoalTar;
    public static Material RawWoodGas;
    public static Material RawWoodVinegar;
    public static Material SulfuricWasteFluid;
    public static Material CalciumAcetateSlurry;
    public static Material Asphalt;
    public static Material EthyleneRichGas;
    public static Material MethaneRichGas;
    public static Material SynthesisGas;
    public static Material Stillage;

    public GTSteamMaterials() {
    }

    public static void register() {
        FirstDegreeMaterials.register();
        SecondDegreeMaterials.register();
        HigherDegreeMaterials.register();
    }
}
