package keqing.gtsteam.common.metatileentities.multi.primitive;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.widgets.ProgressWidget;
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
import gregtech.api.mui.widget.RecipeProgressWidget;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.util.tooltips.InformationHandler;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.common.mui.widget.GTFluidSlot;
import keqing.gtsteam.client.textures.GTSteamTextures;
import keqing.gtsteam.common.block.GTSteamMetaBlocks;
import keqing.gtsteam.common.block.blocks.BlockMultiblockCasing0;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;


public class MetaTileEntityAdvancedCokeOven extends RecipeMapPrimitiveMultiblockController {

    public MetaTileEntityAdvancedCokeOven(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.COKE_OVEN_RECIPES);
        recipeMapWorkable.setSpeedBonus(0.6);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityAdvancedCokeOven(metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXX", "XXX", "XXX")
                .aisle("XXX", "X#X", "XXX")
                .aisle("XXX", "XYX", "XXX")
                .where('X',
                        states(getCasingState())
                                .or(metaTileEntities(MetaTileEntities.COKE_OVEN_HATCH).setMaxGlobalLimited(5)))
                .where('#', air())
                .where('Y', selfPredicate())
                .build();
    }

    protected IBlockState getCasingState() {
        return GTSteamMetaBlocks.blockMultiblockCasing0.getState(BlockMultiblockCasing0.CasingType.GALVANIZED_PORCELAIN_TILES);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return GTSteamTextures.PORCELAIN_TILES;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                recipeMapWorkable.isActive(), recipeMapWorkable.isWorkingEnabled());
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.COKE_OVEN_OVERLAY;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
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
                            .child(new RecipeProgressWidget()
                                    .recipeMap(this.recipeMapWorkable.recipeMap)
                                    .size(20, 15)
                                    .pos(76, 32)
                                    .value(new DoubleSyncValue(recipeMapWorkable::getProgressPercent))
                                    .texture(GTGuiTextures.PRIMITIVE_BLAST_FURNACE_PROGRESS_BAR, -1)
                                    .direction(ProgressWidget.Direction.RIGHT))
                            .child(new ItemSlot()
                                    .slot(new ModularSlot(exportItems, 0)
                                            .accessibility(false, true))
                                    .pos(103, 30))
                            .child(new GTFluidSlot()
                                    .overlay(GTGuiTextures.PRIMITIVE_LARGE_FLUID_TANK_OVERLAY.asIcon()
                                            .alignment(Alignment.CenterLeft)
                                            .marginLeft(1))
                                    .syncHandler(GTFluidSlot.sync(exportFluids.getTankAt(0))
                                            .drawAlwaysFull(false)
                                            .showAmountOnSlot(false)
                                            .accessibility(true, false))
                                    .pos(134, 13)
                                    .size(20, 58));
                });
    }

    @Override
    public GTGuiTheme getUITheme() {
        return GTGuiTheme.PRIMITIVE;
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

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        InformationHandler.topTooltips("高级焦炉？", tooltip);
        super.addInformation(stack, player, tooltip, advanced);
        TooltipBuilder.create().addSpecialLogic().build(this, tooltip);
        tooltip.add(I18n.format("比普通的焦炉快40%%"));
    }
}

