package keqing.gtsteam.loader.recipes;

import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.ingredients.GTRecipeInput;
import gregtech.api.util.GTUtility;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.List;

import static gregtech.api.unification.material.Materials.Lava;
import static keqing.gtsteam.api.recipes.GTSRecipeMaps.ALLOY_kILN;

public class AlloyKlinRecipes {

    public static void init() {

        Collection<Recipe> alloySmelterRecipesRecipeList = RecipeMaps.ALLOY_SMELTER_RECIPES.getRecipeList();
        for (Recipe recipe : alloySmelterRecipesRecipeList) {

            List<GTRecipeInput> itemInputs = recipe.getInputs();
            List<ItemStack> itemOutputs = recipe.getOutputs();

            int baseDuration = (int) (recipe.getDuration() * 2.4);

            long EUt = recipe.getEUt();
            if(EUt>128)return;

            ALLOY_kILN.recipeBuilder()
                    .duration(baseDuration)
                    .fluidInputs(Lava.getFluid(recipe.getDuration()/20 +1))
                    .inputIngredients(itemInputs)
                    .outputs(itemOutputs)
                    .buildAndRegister();

        }
    }
}
