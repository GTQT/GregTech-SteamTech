package meowmel.gtsteam.common.metatileentities.multi.steam;

import gregtech.api.pattern.element.StructureDefinition;

import static gregtech.api.pattern.element.Elements.*;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.ParallelLogicType;
import gregtech.api.metatileentity.multiblock.RecipeMapSteamMultiblockController;
import gregtech.api.pattern.TemplatePool;
import gregtech.api.pattern.casing.CasingDefinition;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockFireboxCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import meowmel.gtsteam.client.textures.GTSteamTextures;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

import static gregtech.api.util.RelativeDirection.*;
import static gregtech.client.renderer.texture.Textures.BRONZE_PLATED_BRICKS;
import static gregtech.common.blocks.BlockBoilerCasing.BoilerCasingType.BRONZE_PIPE;

public class MetaTileEntitySteamOreWasher extends RecipeMapSteamMultiblockController {

    private static final int PARALLEL_LIMIT = 8;
    private static final StructureDefinition<?> DEFINITION = StructureDefinition.getOrBuild("gtsteam:steam_ore_washer", () ->
            DeclarativePatternBuilder.start(RIGHT, UP, BACK)
                    .aisle("MTTTM", "MMMMM", "MMMMM")
                    .aisle("TPPPT", "MFFFM", "M###M")
                    .aisle("TPPPT", "MFFFM", "M###M")
                    .aisle("TPPPT", "MFFFM", "M###M")
                    .aisle("MTTTM", "MMCMM", "MMMMM")
                    .self('C', MetaTileEntitySteamOreWasher.class)
                    .casing('M', getCasingState())
                    .optionalHatch(MultiblockAbility.STEAM_IMPORT_ITEMS, 4)
                    .optionalHatch(MultiblockAbility.STEAM_EXPORT_ITEMS, 4)
                    .optionalHatch(MultiblockAbility.STEAM_IMPORT_FLUID, 4)
                    .optionalHatch(MultiblockAbility.STEAM_EXPORT_FLUID, 4)
                    .hatch(MultiblockAbility.STEAM, 1)
                    .where('F', blocks(getFrameState()))
                    .where('P', blocks(getBoilerState()))
                    .where('T', blocks(getFireboxState()))
                    .where('#', air())
                    .buildStructureDefinition()
    );

    public MetaTileEntitySteamOreWasher(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.ORE_WASHER_RECIPES, CONVERSION_RATE, ParallelLogicType.MULTIPLY);
        this.recipeMapWorkable.setParallelLimit(PARALLEL_LIMIT);
    }

    private static IBlockState getFrameState() {
        return MetaBlocks.FRAMES.get(Materials.Bronze).getBlock(Materials.Bronze);
    }

    public static IBlockState getFireboxState() {
        return MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.BRONZE_FIREBOX);
    }

    private static IBlockState getBoilerState() {
        return MetaBlocks.BOILER_CASING.getState(BRONZE_PIPE);
    }

    public static IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.BRONZE_BRICKS);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntitySteamOreWasher(metaTileEntityId);
    }

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return DEFINITION;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return BRONZE_PLATED_BRICKS;
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return GTSteamTextures.LARGE_ORE_WASHER_OVERLAY;
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        TooltipBuilder.create().addSteamMachine(PARALLEL_LIMIT).build(this, tooltip);
    }
}
