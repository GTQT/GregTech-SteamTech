package keqing.gtsteam.common.metatileentities.multi.steam;

import com.google.common.base.Predicates;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.ParallelLogicType;
import gregtech.api.metatileentity.multiblock.RecipeMapSteamMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMaps;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import static gregtech.client.renderer.texture.Textures.BRONZE_PLATED_BRICKS;
import static gregtech.client.renderer.texture.Textures.SOLID_STEEL_CASING;

public class MetaTileEntitySteamCentrifuge extends RecipeMapSteamMultiblockController {

    private static final int PARALLEL_LIMIT = 8;

    public MetaTileEntitySteamCentrifuge(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.CENTRIFUGE_RECIPES, CONVERSION_RATE, ParallelLogicType.MULTIPLY);
        this.recipeMapWorkable.setParallelLimit(PARALLEL_LIMIT);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntitySteamCentrifuge(metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        TraceabilityPredicate predicate = states(getCasingState()).setMinGlobalLimited(20).or(autoAbilities());
        predicate = predicate.or(abilities(new MultiblockAbility[]{MultiblockAbility.EXPORT_FLUIDS}));
        predicate = predicate.or(abilities(new MultiblockAbility[]{MultiblockAbility.IMPORT_FLUIDS}));
        return FactoryBlockPattern.start()
                .aisle(" XXX ", " XXX ", "  X  ", "     ")
                .aisle("XXXXX", "XT TX", "     ", "  X  ")
                .aisle("XXXXX", "X X X", "X X X", " XXX ")
                .aisle("XXXXX", "XT TX", "     ", "  X  ")
                .aisle(" XXX ", " XSX ", "  X  ", "     ")
                .where('S', selfPredicate())
                .where('X', predicate)
                .where('T', states(getBoilerState()))
                .where(' ', any())
                .build();
    }

    private IBlockState getBoilerState() {
        return ConfigHolder.machines.steelSteamMultiblocks ?
                MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE) :
                MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.BRONZE_PIPE);
    }

    public IBlockState getCasingState() {
        return ConfigHolder.machines.steelSteamMultiblocks ?
                MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID) :
                MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.BRONZE_BRICKS);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return ConfigHolder.machines.steelSteamMultiblocks ? SOLID_STEEL_CASING : BRONZE_PLATED_BRICKS;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.CENTRIFUGE_OVERLAY;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public int getItemOutputLimit() {
        return 100;
    }
    @Override
    public int getFluidOutputLimit() {return 100000000;}

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.multiblock.steam_.duration_modifier"));
        tooltip.add(I18n.format("gregtech.universal.tooltip.parallel", PARALLEL_LIMIT));
        tooltip.add(TooltipHelper.BLINKING_ORANGE + I18n.format("gregtech.multiblock.require_steam_parts"));
    }
}