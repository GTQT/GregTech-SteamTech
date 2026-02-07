package keqing.gtsteam.common.metatileentities.multi.heat;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.HeatMultiblockController;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;
import keqing.gtsteam.client.textures.GTSteamTextures;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static keqing.gtsteam.common.block.GTSteamMetaBlocks.blockMultiblockCasing0;
import static keqing.gtsteam.common.block.blocks.BlockMultiblockCasing0.CasingType.TANK_WALL;

public class MetaTileEntityHeatDistillationTower extends HeatMultiblockController {

    private static final int PARALLEL_LIMIT = 16;

    public MetaTileEntityHeatDistillationTower(ResourceLocation metaTileEntityId) {
        //配方待定
        super(metaTileEntityId, RecipeMaps.DISTILLATION_RECIPES);
        recipeMapWorkable.setParallelLimit(PARALLEL_LIMIT);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CCCC", "    ", "    ", "    ", "FFFF", "    ", "    ", "    ", "FFFF", "    ", "    ", "    ", "FFFF", "    ", "    ")
                .aisle("CCCC", " PO ", " PO ", " PO ", "FPPF", " PO ", " PO ", " PO ", "FPPF", " PO ", " PO ", " PO ", "FPPF", " PP ", " PM ")
                .aisle("CCCC", " PP ", " PP ", " PP ", "FPPF", " PP ", " PP ", " PP ", "FPPF", " PP ", " PP ", " PP ", "FPPF", " PP ", " PP ")
                .aisle("CSCC", "    ", "    ", "    ", "FFFF", "    ", "    ", "    ", "FFFF", "    ", "    ", "    ", "FFFF", "    ", "    ")
                .where('S', selfPredicate())
                .where('C', states(getCasingState())
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS).setMaxGlobalLimited(1))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMaxGlobalLimited(1))
                        .or(abilities(MultiblockAbility.INPUT_HEAT).setExactLimit(1))
                )
                .where('P', states(getTankCasingState()))
                .where('O', states(getTankCasingState())
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMinGlobalLimited(3))
                )
                .where('M', abilities(MultiblockAbility.MUFFLER_HATCH))
                .where('F', frames(Materials.Steel))
                .where(' ', any())
                .build();
    }
    public IBlockState getTankCasingState() {
        return blockMultiblockCasing0.getState(TANK_WALL);
    }

    private static IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityHeatDistillationTower(metaTileEntityId);
    }

    private boolean isTankPart(IMultiblockPart sourcePart) {
        return isStructureFormed() && (((MetaTileEntity) sourcePart).getPos().getY() > getPos().getY());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        if (sourcePart != null && isTankPart(sourcePart)) {
            return GTSteamTextures.TANK_WALL;
        }
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    public SoundEvent getBreakdownSound() {
        return GTSoundEvents.BREAKDOWN_ELECTRICAL;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        TooltipBuilder.create().addHeatMachine(PARALLEL_LIMIT).build(this, tooltip);
    }
}
