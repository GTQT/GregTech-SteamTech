package meowmel.gtsteam.loader.recipes.chain;

import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import meowmel.gtsteam.api.recipes.GTSRecipeMaps;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

import static gregtech.api.unification.material.Materials.Concrete;
import static gregtech.api.unification.material.Materials.Water;
import static gregtech.common.blocks.BlockMetalCasing.MetalCasingType.PRIMITIVE_BRICKS;

public class PrimitiveChemicalReactorRecipes {
    public static void init() {
        if (ConfigHolder.recipes.harderBrickRecipes) {
            // 土高炉简化
            GTSRecipeMaps.HEAT_CHEMICAL_RECIPES.recipeBuilder()
                    .fluidInputs(Concrete.getFluid(1000))
                    .inputs(MetaItems.FIRECLAY_BRICK.getStackForm(6))
                    .input(OrePrefix.dust, Materials.Gypsum, 2)
                    .outputs(MetaBlocks.METAL_CASING.getItemVariant(PRIMITIVE_BRICKS))
                    .duration(100)
                    .Heat(473)
                    .Heat(15)
                    .buildAndRegister();

            // 砖块简化
            GTSRecipeMaps.HEAT_CHEMICAL_RECIPES.recipeBuilder()
                    .fluidInputs(Water.getFluid(1000))
                    .input(Items.BRICK, 8)
                    .output(Blocks.BRICK_BLOCK, 2)
                    .duration(75)
                    .Heat(473)
                    .Heat(15)
                    .buildAndRegister();
        }
    }
}
