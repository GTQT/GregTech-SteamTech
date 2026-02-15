package meowmel.gtsteam.loader.recipes;

import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.ingredients.GTRecipeInput;
import gregtech.api.util.GTUtility;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.List;

import static gregtech.api.unification.material.Materials.Lava;
import static meowmel.gtsteam.api.recipes.GTSRecipeMaps.ALLOY_KILN;

public class AlloyKlinRecipes {

    public static void init() {

        Collection<Recipe> alloySmelterRecipesRecipeList = RecipeMaps.ALLOY_SMELTER_RECIPES.getRecipeList();
        for (Recipe recipe : alloySmelterRecipesRecipeList) {

            List<GTRecipeInput> itemInputs = recipe.getInputs();
            List<ItemStack> itemOutputs = recipe.getOutputs();

            long EUt = recipe.getEUt();

            int tier = GTUtility.getTierByVoltage(EUt) + 1;

            if (EUt > 128) return;

            int baseDuration = recipe.getDuration();

            ALLOY_KILN.recipeBuilder()
                    .duration(baseDuration / 20)
                    .fluidInputs(Lava.getFluid(Math.max(1, tier * baseDuration / 80)))
                    .inputIngredients(itemInputs)
                    .outputs(itemOutputs)
                    .buildAndRegister();

        }
    }
}
