package meowmel.gtsteam.common.metatileentities.multi.steam;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.ParallelLogicType;
import gregtech.api.metatileentity.multiblock.RecipeMapSteamMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
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

import java.util.List;

public class MetaTileEntitySteamBrewing extends RecipeMapSteamMultiblockController {

    private static final int PARALLEL_LIMIT = 4;

    public MetaTileEntitySteamBrewing(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.BREWING_RECIPES, CONVERSION_RATE, ParallelLogicType.MULTIPLY);
        this.recipeMapWorkable.setParallelLimit(PARALLEL_LIMIT);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntitySteamBrewing(metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("F#F", "BBB", "CCC", "CCC", "CCC")
                .aisle("###", "BOB", "G#G", "G#G", "CCC")
                .aisle("F#F", "BBB", "CSC", "CCC", "CCC")
                .where('S', selfPredicate())
                .where('C', states(getCasingState())
                        .or(abilities(MultiblockAbility.STEAM_IMPORT_ITEMS).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.STEAM_EXPORT_ITEMS).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.STEAM_IMPORT_FLUID).setPreviewCount(1))
                        .or(abilities(MultiblockAbility.STEAM).setPreviewCount(1))
                )
                .where('O', states(getCasingState())
                        .or(abilities(MultiblockAbility.STEAM_EXPORT_FLUID).setPreviewCount(1))
                )
                .where('G', states(getGlassState()))
                .where('B', states(getButtonState()))
                .where('F', states(getFrameState()))
                .where('#', air())
                .build();
    }

    private static IBlockState getGlassState() {
        return Blocks.GLASS.getDefaultState();
    }

    private static IBlockState getFrameState() {
        return MetaBlocks.FRAMES.get(Materials.Steel).getBlock(Materials.Steel);
    }

    public IBlockState getCasingState() {
        return GTSteamMetaBlocks.blockMultiblockCasing0.getState(BlockMultiblockCasing0.CasingType.SEALED_WOOD_WALL);
    }
    public IBlockState getButtonState() {
        return GTSteamMetaBlocks.blockMultiblockCasing0.getState(BlockMultiblockCasing0.CasingType.SEALED_WOOD_BOTTOM);
    }
    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return GTSteamTextures.SEALED_WOOD_WALL;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.PRIMITIVE_PUMP_OVERLAY;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        TooltipBuilder.create().addSteamMachine(PARALLEL_LIMIT).build(this, tooltip);
    }
}