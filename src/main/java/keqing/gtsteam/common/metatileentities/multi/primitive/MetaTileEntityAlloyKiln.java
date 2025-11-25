package keqing.gtsteam.common.metatileentities.multi.primitive;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityUIFactory;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.RecipeMapPrimitiveMultiblockController;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIFactory;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuiTheme;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.mui.widget.GTFluidSlot;
import keqing.gtsteam.api.recipes.GTSRecipeMaps;
import keqing.gtsteam.client.textures.GTSteamTextures;
import keqing.gtsteam.common.block.GTSteamMetaBlocks;
import keqing.gtsteam.common.block.blocks.BlockMultiblockCasing0;
import keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaTileEntityAlloyKiln extends RecipeMapPrimitiveMultiblockController {


    public MetaTileEntityAlloyKiln(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTSRecipeMaps.ALLOY_KILN);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityAlloyKiln(metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXX", "XXX", "#X#")
                .aisle("XXX", "X&X", "#X#")
                .aisle("XXX", "XYX", "#X#")
                .where('X', states(getCasingState())
                        .or(metaTileEntities(GTSteamMetaTileEntities.ALLOY_KILN_IMPORT_HATCH).setMaxGlobalLimited(2))
                        .or(metaTileEntities(GTSteamMetaTileEntities.ALLOY_KILN_EXPORT_HATCH).setMaxGlobalLimited(1))
                )
                .where('#', any())
                .where('&', air())

                .where('Y', selfPredicate())
                .build();
    }

    protected IBlockState getCasingState() {
        return GTSteamMetaBlocks.blockMultiblockCasing0.getState(BlockMultiblockCasing0.CasingType.GALVANIZED_PORCELAIN_TILES);
    }

    @Override
    protected MultiblockUIFactory createUIFactory() {
        return new MultiblockUIFactory(this)
                .disableButtons()
                .disableDisplay()
                .setSize(176, 166)
                .addScreenChildren((parent, syncManager) -> {
                    parent.child(IKey.lang(getMetaFullName()).asWidget().pos(5, 5))
                            .child(new ItemSlot()
                                    .slot(new ModularSlot(importItems, 0)
                                            .singletonSlotGroup())
                                    .pos(52, 30))
                            .child(new ItemSlot()
                                    .slot(new ModularSlot(importItems, 1)
                                            .singletonSlotGroup())
                                    .pos(52, 48))
                            .child(new gregtech.api.mui.widget.RecipeProgressWidget()
                                    .recipeMap(this.recipeMapWorkable.recipeMap)
                                    .size(20, 15)
                                    .pos(76, 41)
                                    .value(new DoubleSyncValue(recipeMapWorkable::getProgressPercent))
                                    .texture(GTGuiTextures.PRIMITIVE_BLAST_FURNACE_PROGRESS_BAR, -1)
                                    .direction(com.cleanroommc.modularui.widgets.ProgressWidget.Direction.RIGHT))
                            .child(new ItemSlot()
                                    .slot(new ModularSlot(exportItems, 0)
                                            .accessibility(false, true))
                                    .pos(103, 30))
                            .child(new ItemSlot()
                                    .slot(new ModularSlot(exportItems, 1)
                                            .accessibility(false, true))
                                    .pos(103, 48))
                            .child(new GTFluidSlot()
                                    .overlay(GTGuiTextures.PRIMITIVE_LARGE_FLUID_TANK_OVERLAY.asIcon()
                                            .alignment(Alignment.CenterRight)
                                            .marginLeft(1))
                                    .syncHandler(GTFluidSlot.sync(importFluids.getTankAt(0))
                                            .drawAlwaysFull(false)
                                            .showAmountOnSlot(false)
                                            .accessibility(true, true))
                                    .pos(10, 22)
                                    .size(20, 58));
                });
    }


    @Override
    public GTGuiTheme getUITheme() {
        return GTGuiTheme.PRIMITIVE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return GTSteamTextures.PORCELAIN_TILES;
    }


    @SideOnly(Side.CLIENT)
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.PRIMITIVE_BLAST_FURNACE_OVERLAY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                recipeMapWorkable.isActive(), recipeMapWorkable.isWorkingEnabled());
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean onRightClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                CuboidRayTraceResult hitResult) {
        // try to fill a bucket (or similar) with creosote on right click (if not sneaking)
        if (playerIn.getHeldItem(hand).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            if (!playerIn.isSneaking()) {
                return getWorld().isRemote || FluidUtil.interactWithFluidHandler(playerIn, hand, getFluidInventory());
            } else {
                // allow opening UI on shift-right-click with fluid container item
                if (getWorld() != null && !getWorld().isRemote) {
                    MetaTileEntityUIFactory.INSTANCE.openUI(getHolder(), (EntityPlayerMP) playerIn);
                }
                return true;
            }
        }
        return super.onRightClick(playerIn, hand, facing, hitResult);
    }
}