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
import gregtech.common.blocks.MetaBlocks;
import meowmel.gtsteam.client.textures.GTSteamTextures;
import meowmel.gtsteam.common.block.GTSteamMetaBlocks;
import meowmel.gtsteam.common.block.blocks.BlockMultiblockCasing0;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MetaTileEntitySteamBrewing extends RecipeMapSteamMultiblockController {

    private static final int PARALLEL_LIMIT = 4;
    private static final StructureDefinition<?> DEFINITION = StructureDefinition.getOrBuild("gtsteam:steam_brewing", () ->
            DeclarativePatternBuilder.start()
                    .aisle("F#F", "BBB", "CCC", "CCC", "CCC")
                    .aisle("###", "BOB", "G#G", "G#G", "CCC")
                    .aisle("F#F", "BBB", "CSC", "CCC", "CCC")
                    .self('S', MetaTileEntitySteamBrewing.class)
                    .casing('C', getCasingState())
                    .hatch(MultiblockAbility.STEAM, 1)
                    .hatch(MultiblockAbility.STEAM_IMPORT_ITEMS, 1, 4)
                    .hatch(MultiblockAbility.STEAM_EXPORT_ITEMS, 1, 4)
                    .hatch(MultiblockAbility.STEAM_IMPORT_FLUID, 1, 4)
                    .where('O', chain(blocks(getCasingState()),
                            abilities(0, -1, 1, MultiblockAbility.STEAM_EXPORT_FLUID)))
                    .where('G', blocks(getGlassState()))
                    .where('B', blocks(getButtonState()))
                    .where('F', blocks(getFrameState()))
                    .where('#', air())
                    .buildStructureDefinition()
    );

    public MetaTileEntitySteamBrewing(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.BREWING_RECIPES, CONVERSION_RATE, ParallelLogicType.MULTIPLY);
        this.recipeMapWorkable.setParallelLimit(PARALLEL_LIMIT);
    }

    private static IBlockState getGlassState() {
        return Blocks.GLASS.getDefaultState();
    }

    private static IBlockState getFrameState() {
        return MetaBlocks.FRAMES.get(Materials.Steel).getBlock(Materials.Steel);
    }

    public static IBlockState getCasingState() {
        return GTSteamMetaBlocks.blockMultiblockCasing0.getState(BlockMultiblockCasing0.CasingType.SEALED_WOOD_WALL);
    }

    public static IBlockState getButtonState() {
        return GTSteamMetaBlocks.blockMultiblockCasing0.getState(BlockMultiblockCasing0.CasingType.SEALED_WOOD_BOTTOM);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntitySteamBrewing(metaTileEntityId);
    }

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return DEFINITION;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return GTSteamTextures.SEALED_WOOD_WALL;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return GTSteamTextures.LARGE_BREWERY_OVERLAY;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        TooltipBuilder.create().addSteamMachine(PARALLEL_LIMIT).build(this, tooltip);
    }
}
