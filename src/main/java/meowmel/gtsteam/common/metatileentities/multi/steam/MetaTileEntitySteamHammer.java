package meowmel.gtsteam.common.metatileentities.multi.steam;

import gregtech.api.pattern.element.StructureDefinition;

import static gregtech.api.pattern.element.Elements.*;
import gregtech.api.GTValues;
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
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import meowmel.gtsteam.client.textures.GTSteamTextures;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static gregtech.api.util.RelativeDirection.*;
import static gregtech.client.renderer.texture.Textures.BRONZE_PLATED_BRICKS;
import static gregtech.common.blocks.BlockBoilerCasing.BoilerCasingType.BRONZE_PIPE;

public class MetaTileEntitySteamHammer extends RecipeMapSteamMultiblockController {

    private static final int PARALLEL_LIMIT = 8;
    private static final StructureDefinition<?> DEFINITION = StructureDefinition.getOrBuild("gtsteam:steam_hammer", () ->
            DeclarativePatternBuilder.start()
                    .aisle(" XXX ", "     ", "     ", "     ", "     ", "     ", "     ")
                    .aisle("XXXXX", " XXX ", "     ", "     ", "     ", "  X  ", "     ")
                    .aisle("XXXXX", "XX XX", "X   X", "X B X", "X B X", "XXXXX", "  P  ")
                    .aisle("XXXXX", " XXX ", "     ", "     ", "     ", "  X  ", "     ")
                    .aisle(" XSX ", "     ", "     ", "     ", "     ", "     ", "     ")
                    .self('S', MetaTileEntitySteamHammer.class)
                    .casing('X', getCasingState())
                    .hatch(MultiblockAbility.STEAM_IMPORT_ITEMS, 1, 2)
                    .hatch(MultiblockAbility.STEAM_EXPORT_ITEMS, 1, 2)
                    .hatch(MultiblockAbility.STEAM, 1)
                    .where('F', blocks(getFrameState()))
                    .where('P', blocks(getBoilerState()))
                    .where('B', blocks(getBlockState()))
                    .where(' ', any())
                    .buildStructureDefinition()
    );

    public MetaTileEntitySteamHammer(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.FORGE_HAMMER_RECIPES, CONVERSION_RATE, ParallelLogicType.APPEND_ITEMS);
        this.recipeMapWorkable.setParallelLimit(PARALLEL_LIMIT);
    }

    private static IBlockState getFrameState() {
        return MetaBlocks.FRAMES.get(Materials.Bronze).getBlock(Materials.Bronze);
    }

    private static IBlockState getBlockState() {
        return MetaBlocks.COMPRESSED.get(Materials.Iron).getBlock(Materials.Iron);
    }

    private static IBlockState getBoilerState() {
        return MetaBlocks.BOILER_CASING.getState(BRONZE_PIPE);
    }


    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntitySteamHammer(metaTileEntityId);
    }

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return DEFINITION;
    }

    public static IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.BRONZE_BRICKS);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return BRONZE_PLATED_BRICKS;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return GTSteamTextures.LARGE_COMPRESSOR_OVERLAY;
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

    @Override
    public void randomDisplayTick() {
        if (this.isActive()) {
            final BlockPos pos = getPos();
            float x = pos.getX() + 0.5F;
            float z = pos.getZ() + 0.5F;

            final EnumFacing facing = getFrontFacing();
            final float horizontalOffset = GTValues.RNG.nextFloat() * 0.6F - 0.3F;
            final float y = pos.getY() + GTValues.RNG.nextFloat() * 0.375F + 0.3F;

            if (facing.getAxis() == EnumFacing.Axis.X) {
                if (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) x += 0.52F;
                else x -= 0.52F;
                z += horizontalOffset;
            } else if (facing.getAxis() == EnumFacing.Axis.Z) {
                if (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) z += 0.52F;
                else z -= 0.52F;
                x += horizontalOffset;
            }
            if (ConfigHolder.machines.machineSounds && GTValues.RNG.nextDouble() < 0.1) {
                getWorld().playSound(x, y, z, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }
            getWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, 0, 0, 0);
            getWorld().spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0, 0, 0);
        }
    }
}
