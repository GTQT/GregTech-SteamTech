package keqing.gtsteam.loader.recipes;

public class GTSRecipeManger {
    private GTSRecipeManger() {

    }
    public static void load() {
    }
    public static void init() {
        AlloyKlinRecipes.init();
        MiscRecipes.init();
        BiomimeticFactoryRecipes.init();
        CeramicChain.init();
        GalvanizedSteelLine.init();
        RubberChain.init();
    }
}
