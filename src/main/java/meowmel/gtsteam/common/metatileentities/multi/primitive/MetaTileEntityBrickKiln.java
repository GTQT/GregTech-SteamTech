package meowmel.gtsteam.common.metatileentities.multi.primitive;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.RecipeMapPrimitiveMultiblockController;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIFactory;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.pattern.BlockPatternTemplate;
import gregtech.api.pattern.SoftTemplate;
import gregtech.api.pattern.TemplatePool;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.util.GTUtility;
import gregtech.api.util.tooltips.InformationHandler;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.particle.VanillaParticleEffects;
import gregtech.client.renderer.CubeRendererState;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.cclop.ColourOperation;
import gregtech.client.renderer.cclop.LightMapOperation;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.utils.BloomEffectUtil;
import gregtech.common.ConfigHolder;
import meowmel.gtsteam.api.recipes.GTSRecipeMaps;
import meowmel.gtsteam.client.textures.GTSteamTextures;
import meowmel.gtsteam.common.block.GTSteamMetaBlocks;
import meowmel.gtsteam.common.block.blocks.BlockMultiblockCasing0;
import meowmel.gtsteam.common.metatileentities.GTSteamMetaTileEntities;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MetaTileEntityBrickKiln extends RecipeMapPrimitiveMultiblockController {


    private static final SoftTemplate TEMPLATE = TemplatePool.getInstance().register("gtsteam:brick_kiln", () ->
            DeclarativePatternBuilder.start()
                    .aisle("XXX", "XXX", "#X#")
                    .aisle("XXX", "X&X", "X&X")
                    .aisle("XXX", "XYX", "#X#")
                    .self('Y', MetaTileEntityBrickKiln.class)
                    .where('X', states(getCasingState())
                            .or(metaTileEntities(GTSteamMetaTileEntities.PRIMITIVE_IMPORT_HATCH).setMaxGlobalLimited(2))
                            .or(metaTileEntities(GTSteamMetaTileEntities.PRIMITIVE_EXPORT_HATCH).setMaxGlobalLimited(1))
                    )
                    .where('#', any())
                    .where('&', air())
                    .buildTemplate()
    );

    public MetaTileEntityBrickKiln(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTSRecipeMaps.BRICK_KILN);
    }

    protected static IBlockState getCasingState() {
        return GTSteamMetaBlocks.blockMultiblockCasing0.getState(BlockMultiblockCasing0.CasingType.GALVANIZED_PORCELAIN_TILES);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityBrickKiln(metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPatternTemplate createStructureTemplate() {
        return TEMPLATE.get();
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
                                    .pos(103, 48));
                });
    }


    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                recipeMapWorkable.isActive(), recipeMapWorkable.isWorkingEnabled());
        if (recipeMapWorkable.isActive() && isStructureFormed()) {
            EnumFacing back = getFrontFacing().getOpposite();
            Matrix4 offset = translation.copy().translate(back.getXOffset(), -0.3, back.getZOffset());
            CubeRendererState op = Textures.RENDER_STATE.get();
            Textures.RENDER_STATE.set(new CubeRendererState(op.layer, CubeRendererState.PASS_MASK, op.world));
            Textures.renderFace(renderState, offset,
                    ArrayUtils.addAll(pipeline, new LightMapOperation(240, 240), new ColourOperation(0xFFFFFFFF)),
                    EnumFacing.UP, Cuboid6.full, TextureUtils.getBlockTexture("lava_still"),
                    BloomEffectUtil.getEffectiveBloomLayer());
            Textures.RENDER_STATE.set(op);
        }
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.PRIMITIVE_BLAST_FURNACE_OVERLAY;
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
                damageEntitiesAndBreakSnow();
                pollution(this.getPollutionAmount(), this.getPollutionTicks());
            }
        }
    }

    private void damageEntitiesAndBreakSnow() {
        BlockPos middlePos = this.getPos();
        middlePos = middlePos.offset(getFrontFacing().getOpposite());
        this.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(middlePos))
                .forEach(entity -> entity.attackEntityFrom(DamageSource.LAVA, 3.0f));

        if (getOffsetTimer() % 10 == 0) {
            IBlockState state = getWorld().getBlockState(middlePos);
            GTUtility.tryBreakSnow(getWorld(), middlePos, state, true);
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

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return GTSteamTextures.PORCELAIN_TILES;
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) {
        InformationHandler.topTooltips("黑窑厂", tooltip);
        super.addInformation(stack, player, tooltip, advanced);
        TooltipBuilder.create().addSpecialLogic().build(this, tooltip);
        tooltip.add(I18n.format("gtsteam.machine.brick_kiln.tooltip.1"));
        tooltip.add(I18n.format("gtsteam.machine.brick_kiln.tooltip.2"));
    }
}