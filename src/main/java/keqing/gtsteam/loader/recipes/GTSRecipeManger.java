package keqing.gtsteam.loader.recipes;

public class GTSRecipeManger {
    private GTSRecipeManger() {

    }
    public static void load() {
    }
    public static void init() {
        AlloyKlinRecipes.init();
        IntegratedMiningDivision.init();
        MiscRecipes.init();
        BiomimeticFactoryRecipes.init();
        SawmillChain.init();
        CeramicChain.init();
    }
}
