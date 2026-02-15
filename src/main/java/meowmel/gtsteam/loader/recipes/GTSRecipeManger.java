package meowmel.gtsteam.loader.recipes;

import meowmel.gtsteam.loader.recipes.chain.*;
import meowmel.gtsteam.loader.recipes.chain.*;

public class GTSRecipeManger {
    private GTSRecipeManger() {

    }

    public static void load() {
    }

    public static void init() {
        AlloyKlinRecipes.init();
        MiscRecipes.init();
        LigniteChain.init();
        WoodChain.init();
        GenerateRecipes.init();
        CokeOvenChain.init();
        CoalChain.init();
        BiomimeticFactoryRecipes.init();
        CeramicChain.init();
        GalvanizedSteelChain.init();
        RubberChain.init();
        ULVCircuitChain.init();
        PrimitiveChemicalReactorRecipes.init();
    }
}
