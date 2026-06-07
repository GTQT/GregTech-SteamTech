package meowmel.gtsteam.common.metatileentities.multi.primitive;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import gregtech.api.GTValues;
import gregtech.api.capability.impl.NotifiableItemStackHandler;
import gregtech.api.capability.impl.PrimitiveRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.RecipeMapPrimitiveMultiblockController;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIFactory;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuiTheme;
import gregtech.api.mui.widget.RecipeProgressWidget;
import gregtech.api.pattern.BlockPatternTemplate;
import gregtech.api.pattern.SoftTemplate;
import gregtech.api.pattern.TemplatePool;
import gregtech.api.pattern.casing.CasingDefinition;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.util.GTUtility;
import gregtech.api.util.tooltips.InformationHandler;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.particle.VanillaParticleEffects;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.ConfigHolder;
import meowmel.gtsteam.client.textures.GTSteamTextures;
import meowmel.gtsteam.common.block.GTSteamMetaBlocks;
import meowmel.gtsteam.common.block.blocks.BlockMultiblockCasing0;
import meowmel.gtsteam.common.metatileentities.GTSteamMetaTileEntities;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MetaTileEntityPrimitiveFurnace extends RecipeMapPrimitiveMultiblockController {

    private static final SoftTemplate TEMPLATE = TemplatePool.getInstance().register("gtsteam:primitive_furnace", () ->
            DeclarativePatternBuilder.start()
                    .aisle("XXX", "XXX", "XXX")
                    .aisle("XXX", "X X", "XXX")
                    .aisle("XXX", "XYX", "XXX")
                    .casing('X', CasingDefinition.simple(getCasingState()))
                    .custom(metaTileEntities(GTSteamMetaTileEntities.PRIMITIVE_IMPORT_HATCH), 2)
                    .custom(metaTileEntities(GTSteamMetaTileEntities.PRIMITIVE_EXPORT_HATCH), 1)
                    .where(' ', air())
                    .where('Y', selfPredicate(MetaTileEntityPrimitiveFurnace.class))
                    .buildTemplate()
    );
    protected IItemHandlerModifiable fuelStack;

    public MetaTileEntityPrimitiveFurnace(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.FURNACE_RECIPES);
        this.recipeMapWorkable = new PrimitiveFurnaceRecipeLogic(this);
        this.recipeMapWorkable.setSpeedBonus(0.5f);
    }

    protected static IBlockState getCasingState() {
        return GTSteamMetaBlocks.blockMultiblockCasing0.getState(BlockMultiblockCasing0.CasingType.GALVANIZED_PORCELAIN_TILES);
    }

    @Override
    protected void initializeAbilities() {
        super.initializeAbilities();
        this.fuelStack = new NotifiableItemStackHandler(this, 1, this, false);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        GTUtility.writeItems(this.fuelStack, "fuelStack", data);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        GTUtility.readItems(this.fuelStack, "fuelStack", data);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityPrimitiveFurnace(metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPatternTemplate createStructureTemplate() {
        return TEMPLATE.get();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return GTSteamTextures.PORCELAIN_TILES;
    }

    @Override
    protected MultiblockUIFactory createUIFactory() {
        return new MultiblockUIFactory(this)
                .setSize(176, 166)
                .disableDisplay()
                .disableButtons()
                .addScreenChildren((parent, syncManager) -> {

                    SlotGroup importGroup = new SlotGroup("import", 1, true);

                    parent.child(IKey.lang(getMetaFullName()).asWidget().pos(5, 5))
                            .child(new ItemSlot()
                                    .background(GTGuiTextures.SLOT_PRIMITIVE)
                                    .slot(new ModularSlot(importItems, 0)
                                            .slotGroup(importGroup)
                                            .accessibility(true, true))
                                    .pos(60, 20))

                            .child(new ItemSlot()
                                    .background(GTGuiTextures.SLOT_PRIMITIVE)
                                    .slot(new ModularSlot(fuelStack, 0)
                                            .slotGroup(importGroup)
                                            .accessibility(true, true))
                                    .pos(60, 40))

                            .child(new RecipeProgressWidget()
                                    .recipeMap(this.recipeMapWorkable.recipeMap)
                                    .size(20, 15)
                                    .pos(81, 32)
                                    .value(new DoubleSyncValue(recipeMapWorkable::getProgressPercent))
                                    .texture(GTGuiTextures.PRIMITIVE_BLAST_FURNACE_PROGRESS_BAR, -1)
                                    .direction(ProgressWidget.Direction.RIGHT))

                            .child(new ItemSlot()
                                    .background(GTGuiTextures.SLOT_PRIMITIVE)
                                    .slot(new ModularSlot(exportItems, 0)
                                            .accessibility(false, true))
                                    .pos(106, 30));
                });
    }

    @Override
    public GTGuiTheme getUITheme() {
        return GTGuiTheme.PRIMITIVE;
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.FURNACE_OVERLAY;
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
    public void update() {
        super.update();

        if (this.isActive()) {
            if (getWorld().isRemote) {
                VanillaParticleEffects.PBF_SMOKE.runEffect(this);
            } else {
                pollution(this.getPollutionAmount(), this.getPollutionTicks());
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick() {
        if (this.isActive()) {
            VanillaParticleEffects.defaultFrontEffect(this, 0.3F, EnumParticleTypes.SMOKE_LARGE,
                    EnumParticleTypes.FLAME);
            if (ConfigHolder.machines.machineSounds && GTValues.RNG.nextDouble() < 0.1) {
                BlockPos pos = getPos();
                getWorld().playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
                        SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }
        }
    }

    @NotNull
    @Override
    public SoundType getSoundType() {
        return SoundType.STONE;
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
        tooltip.add(I18n.format("gtsteam.machine.primitive_furnace.tooltip.1"));
        tooltip.add(I18n.format("gtsteam.machine.primitive_furnace.tooltip.2"));
        tooltip.add(I18n.format("gtsteam.machine.primitive_furnace.tooltip.3"));
    }

    public class PrimitiveFurnaceRecipeLogic extends PrimitiveRecipeLogic {

        private int fuelBurnTimeLeft;
        private int fuelMaxBurnTime;

        public PrimitiveFurnaceRecipeLogic(RecipeMapPrimitiveMultiblockController tileEntity) {
            super(tileEntity, RecipeMaps.FURNACE_RECIPES);
        }

        @Override
        protected void updateRecipeProgress() {
            if (fuelBurnTimeLeft > 0) {
                super.updateRecipeProgress();
                fuelBurnTimeLeft--;
            } else tryConsumeNewFuel();
        }

        protected void tryConsumeNewFuel() {
            ItemStack fuelInSlot = fuelStack.extractItem(0, 1, true);
            if (fuelInSlot.isEmpty()) return;

            int burnTime = TileEntityFurnace.getItemBurnTime(fuelInSlot) * 4;
            if (burnTime <= 0) return;
            fuelStack.extractItem(0, 1, false);
            setFuelMaxBurnTime(burnTime);
        }

        public void setFuelMaxBurnTime(int fuelMaxBurnTime) {
            this.fuelMaxBurnTime = fuelMaxBurnTime;
            this.fuelBurnTimeLeft = fuelMaxBurnTime;
            if (!getWorld().isRemote) {
                markDirty();
            }
        }

        @NotNull
        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound compound = super.serializeNBT();
            compound.setInteger("FuelBurnTimeLeft", fuelBurnTimeLeft);
            compound.setInteger("FuelMaxBurnTime", fuelMaxBurnTime);
            return compound;
        }

        @Override
        public void deserializeNBT(@NotNull NBTTagCompound compound) {
            super.deserializeNBT(compound);
            this.fuelBurnTimeLeft = compound.getInteger("FuelBurnTimeLeft");
            this.fuelMaxBurnTime = compound.getInteger("FuelMaxBurnTime");
        }
    }


}
