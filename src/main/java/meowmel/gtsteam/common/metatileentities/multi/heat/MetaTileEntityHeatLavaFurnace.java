package meowmel.gtsteam.common.metatileentities.multi.heat;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.HeatMultiblockController;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockFireboxCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import meowmel.gtsteam.api.recipes.GTSRecipeMaps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class MetaTileEntityHeatLavaFurnace extends HeatMultiblockController {

    public MetaTileEntityHeatLavaFurnace(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTSRecipeMaps.LAVA_FURNACE_RECIPES);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityHeatLavaFurnace(metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXX", "CCC", "#C#")
                .aisle("XCX", "C#C", "CCC")
                .aisle("XXX", "CSC", "#C#")
                .where('S', selfPredicate())
                .where('X', states(getFireboxState()))
                .where('C', states(getCasingState()).setMinGlobalLimited(6)
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMinGlobalLimited(1).setMaxGlobalLimited(3))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setMaxGlobalLimited(3))
                        .or(abilities(MultiblockAbility.INPUT_HEAT).setExactLimit(1))
                )
                .where('#', any())
                .build();
    }

    public IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    public IBlockState getFireboxState() {
        return MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.STEEL_FIREBOX);
    }

    private boolean isFireboxPart(IMultiblockPart sourcePart) {
        return isStructureFormed() && (((MetaTileEntity) sourcePart).getPos().getY() < getPos().getY());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        if (sourcePart != null && isFireboxPart(sourcePart)) {
            return lastActive ? Textures.STEEL_FIREBOX_ACTIVE : Textures.STEEL_FIREBOX;
        }
        return Textures.SOLID_STEEL_CASING;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.ELECTRIC_FURNACE_OVERLAY;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }


    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        TooltipBuilder.create().addSteamMachine(1).build(this, tooltip);
    }

}
