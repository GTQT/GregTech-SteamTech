package meowmel.gtsteam.common.metatileentities.multi.primitive;

import gregtech.api.pattern.element.StructureDefinition;

import static gregtech.api.pattern.element.Elements.*;
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
import gregtech.api.pattern.TemplatePool;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.Elements;
import gregtech.api.util.tooltips.InformationHandler;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.common.mui.widget.GTFluidSlot;
import meowmel.gtsteam.api.recipes.GTSRecipeMaps;
import meowmel.gtsteam.client.textures.GTSteamTextures;
import meowmel.gtsteam.common.block.GTSteamMetaBlocks;
import meowmel.gtsteam.common.block.blocks.BlockMultiblockCasing0;
import meowmel.gtsteam.common.metatileentities.GTSteamMetaTileEntities;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static gregtech.api.util.RelativeDirection.*;

public class MetaTileEntityAlloyKiln extends RecipeMapPrimitiveMultiblockController {

    private static final StructureDefinition<?> DEFINITION = StructureDefinition.getOrBuild("gtsteam:alloy_kiln", () ->
            DeclarativePatternBuilder.start(RIGHT, UP, BACK)
                    .aisle("XXX", "XXX", "#X#")
                    .aisle("XXX", "X&X", "#X#")
                    .aisle("XXX", "XYX", "#X#")
                    .self('Y', MetaTileEntityAlloyKiln.class)
                    .casing('X', getCasingState())
                    .custom(Elements.metaTileEntities(0, 2, 2, GTSteamMetaTileEntities.PRIMITIVE_IMPORT_HATCH), 2)
                    .custom(Elements.metaTileEntities(0, 2, 2, GTSteamMetaTileEntities.PRIMITIVE_EXPORT_HATCH), 2)
                    .where('#', any())
                    .where('&', air())
                    .buildStructureDefinition()
    );

    public MetaTileEntityAlloyKiln(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTSRecipeMaps.ALLOY_KILN);
    }

    public static IBlockState getCasingState() {
        return GTSteamMetaBlocks.blockMultiblockCasing0.getState(BlockMultiblockCasing0.CasingType.GALVANIZED_PORCELAIN_TILES);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityAlloyKiln(metaTileEntityId);
    }

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return DEFINITION;
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
    protected @NotNull ICubeRenderer getFrontOverlay() {
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

    @Override
    public double getPollutionAmount() {
        return 0.0025;
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) {
        InformationHandler.topTooltips("大型原始人熔炉", tooltip);
        super.addInformation(stack, player, tooltip, advanced);
        TooltipBuilder.create().addSpecialLogic().build(this, tooltip);
        tooltip.add(I18n.format("gtsteam.machine.alloy_kiln.tooltip.1"));
        tooltip.add(I18n.format("gtsteam.machine.alloy_kiln.tooltip.2"));
        tooltip.add(I18n.format("gtsteam.machine.alloy_kiln.tooltip.3"));
    }
}
