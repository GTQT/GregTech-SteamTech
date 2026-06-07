package meowmel.gtsteam.loader.recipes;

import meowmel.gtsteam.loader.recipes.chain.*;

public class GTSRecipeManger {
    private GTSRecipeManger() {

    }

    public static void load() {
    }

    public static void init() {
        AlloyKilnRecipes.init();
        BrickKilnRecipes.init();
        MiscRecipes.init();
        MachineRecipes.init();
        ULVAge.init();
        SteamAge.init();
        LigniteChain.init();
        WoodChain.init();
        GenerateRecipes.init();
        CokeOvenChain.init();
        CoalChain.init();
        BiomimeticFactoryRecipes.init();
        CeramicChain.init();
        GalvanizedSteelChain.init();
        RubberChain.init();
        ResinChain.init();
        ULVCircuitChain.init();
        PrimitiveChemicalReactorRecipes.init();
    }
}
