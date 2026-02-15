package meowmel.gtsteam.common.metatileentities.multi.store;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.impl.FilteredFluidHandler;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.LabelWidget;
import gregtech.api.gui.widgets.TankWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.metatileentity.multiblock.ui.KeyManager;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.metatileentity.multiblock.ui.UISyncer;
import gregtech.api.mui.GTGuiTheme;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.KeyUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import meowmel.gtsteam.common.metatileentities.GTSteamMetaTileEntities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static gregtech.client.renderer.texture.Textures.BRONZE_PLATED_BRICKS;
import static gregtech.client.renderer.texture.Textures.SOLID_STEEL_CASING;
import static gregtech.common.blocks.BlockBoilerCasing.BoilerCasingType.BRONZE_PIPE;
import static gregtech.common.blocks.BlockBoilerCasing.BoilerCasingType.STEEL_PIPE;

public class MetaTileEntityLargeSteamTank extends MultiblockWithDisplayBase {

    private final int capacity;

    public MetaTileEntityLargeSteamTank(ResourceLocation metaTileEntityId, int capacity) {
        super(metaTileEntityId);
        this.capacity = capacity;
        initializeInventory();
    }

    private static IBlockState getFrameState() {
        return ConfigHolder.machines.steelSteamMultiblocks ?
                MetaBlocks.FRAMES.get(Materials.Steel).getBlock(Materials.Steel) :
                MetaBlocks.FRAMES.get(Materials.Bronze).getBlock(Materials.Bronze);
    }

    public IBlockState getCasingState() {
        return ConfigHolder.machines.steelSteamMultiblocks ?
                MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID) :
                MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.BRONZE_BRICKS);
    }

    private IBlockState getBoilerState() {
        return ConfigHolder.machines.steelSteamMultiblocks ?
                MetaBlocks.BOILER_CASING.getState(STEEL_PIPE) :
                MetaBlocks.BOILER_CASING.getState(BRONZE_PIPE);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityLargeSteamTank(metaTileEntityId, capacity);
    }

    @Override
    protected void updateFormedValid() {
    }

    @Override
    protected void initializeInventory() {
        super.initializeInventory();

        FilteredFluidHandler tank = new FilteredFluidHandler(Materials.Steam.getFluid(0), capacity);
        this.exportFluids = this.importFluids = new FluidTankList(true, tank);
        this.fluidInventory = tank;
    }

    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.addCustom(this::addFluidAmountCapacity);
    }

    private void addFluidAmountCapacity(KeyManager keyManager, UISyncer syncer) {
        if (isStructureFormed()) {
            //GregTechMod.LOGGER.warn(this.importFluids.getTankAt(0).getFluidAmount() + "meow");
            var steamFLuidAmountString = KeyUtil.number(TextFormatting.WHITE,
                    syncer.syncInt(this.importFluids.getTankAt(0).getFluidAmount()), "L");

            keyManager.add(KeyUtil.lang(TextFormatting.GRAY, "gtsteam.machine.large_steam_tank.fluid_amount_text"));
            keyManager.add(steamFLuidAmountString);
        }
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle(" AAA ", " ABA ", " ABA ", " A A ", " A A ", " A A ", " CCC ")
                .aisle("A A A", "ADEDA", "ADDDA", "ADFDA", "ADFDA", "ADFDA", "C D C")
                .aisle("AAAAA", "BEEEB", "BDEDB", " F F ", " F F ", " F F ", "CDBDC")
                .aisle("A A A", "ADEDA", "ADDDA", "ADFDA", "ADFDA", "ADFDA", "C D C")
                .aisle(" AAA ", " AGA ", " ABA ", " A A ", " A A ", " A A ", " CCC ")
                .where('A', states(getFrameState()))
                .where('B', states(getCasingState()).or(metaTileEntities(getValve()).setMaxGlobalLimited(2)))
                .where('C', states(getFrameState()))
                .where('D', states(getCasingState()))
                .where('E', states(getBoilerState()))
                .where('F', states(Blocks.GLASS.getDefaultState()))
                .where('G', selfPredicate())
                .where(' ', any())
                .build();
    }

    private MetaTileEntity getValve() {
        return ConfigHolder.machines.steelSteamMultiblocks ? MetaTileEntities.STEEL_TANK_VALVE : GTSteamMetaTileEntities.BRONZE_TANK_VALVE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return ConfigHolder.machines.steelSteamMultiblocks ? SOLID_STEEL_CASING : BRONZE_PLATED_BRICKS;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean onRightClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                CuboidRayTraceResult hitResult) {
        if (!isStructureFormed())
            return false;
        return super.onRightClick(playerIn, hand, facing, hitResult);
    }

    @Override
    protected boolean openGUIOnRightClick() {
        return isStructureFormed();
    }

    @Override
    protected ModularUI.Builder createUITemplate(EntityPlayer entityPlayer) {
        return ModularUI.defaultBuilder()
                .widget(new LabelWidget(6, 6, getMetaFullName()))
                .widget(new TankWidget(importFluids.getTankAt(0), 52, 18, 72, 61)
                        .setBackgroundTexture(GuiTextures.SLOT)
                        .setContainerClicking(true, true))
                .bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, 0);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return Textures.MULTIBLOCK_TANK_OVERLAY;
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.multiblock.tank.tooltip"));
        tooltip.add(I18n.format("gregtech.universal.tooltip.fluid_storage_capacity", capacity));
    }

    @Override
    public GTGuiTheme getUITheme() {
        return GTGuiTheme.BRONZE;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if (isStructureFormed()) {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidInventory);
            } else {
                return null;
            }
        }
        return super.getCapability(capability, side);
    }
}
