package keqing.gtsteam.common.metatileentities.multi.primitive;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.impl.PrimitiveRecipeLogic;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.MultiblockShapeInfo;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockFireboxCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import keqing.gtsteam.api.metatileentity.multiblock.RecipeMapNoEnergyMultiblockController;
import keqing.gtsteam.api.pattern.TraceabilityPredicate;
import keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MetaTileEntityIndustrialPrimitiveBlastFurnace extends RecipeMapNoEnergyMultiblockController {

    private static final gregtech.api.pattern.TraceabilityPredicate SNOW_PREDICATE = new gregtech.api.pattern.TraceabilityPredicate(
            bws -> GTUtility.isBlockSnow(bws.getBlockState()));

    int thresholdPercentage = 1;
    int Temp = 3000;
    private byte auxiliaryBlastFurnaceNumber = 0;

    public MetaTileEntityIndustrialPrimitiveBlastFurnace(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.PRIMITIVE_BLAST_FURNACE_RECIPES);
        this.recipeMapWorkable = new IndustrialPrimitiveBlastFurnaceLogic(this, RecipeMaps.PRIMITIVE_BLAST_FURNACE_RECIPES);
    }

    private static IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.PRIMITIVE_BRICKS);
    }

    private static IBlockState getFrameState() {
        return MetaBlocks.FRAMES.get(Materials.Steel).getBlock(Materials.Steel);
    }

    private static IBlockState getBoilerState() {
        return MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE);
    }

    private static IBlockState getFireBoxState() {
        return MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.STEEL_FIREBOX);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("Temp", Temp);
        data.setInteger("thresholdPercentage", thresholdPercentage);
        return super.writeToNBT(data);
    }

    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        Temp = data.getInteger("Temp");
        thresholdPercentage = data.getInteger("thresholdPercentage");
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityIndustrialPrimitiveBlastFurnace(metaTileEntityId);
    }

    @Override
    @Nonnull
    protected Widget getFlexButton(int x, int y, int width, int height) {
        WidgetGroup group = new WidgetGroup(x, y, width, height);
        group.addWidget(new ClickButtonWidget(0, 0, 9, 18, "", this::decrementThreshold)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_MINUS)
                .setTooltipText("gtsteam.multiblock.ip.threshold_decrement"));
        group.addWidget(new ClickButtonWidget(9, 0, 9, 18, "", this::incrementThreshold)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_PLUS)
                .setTooltipText("gtsteam.multiblock.ip.threshold_increment"));
        return group;
    }

    private void incrementThreshold(Widget.ClickData clickData) {
        if (auxiliaryBlastFurnaceNumber == 0)
            this.thresholdPercentage = MathHelper.clamp(thresholdPercentage, 1, 1);
        if (auxiliaryBlastFurnaceNumber == 1)
            this.thresholdPercentage = MathHelper.clamp(thresholdPercentage + 1, 1, 3);
        else this.thresholdPercentage = MathHelper.clamp(thresholdPercentage + 1, 1, 4);
    }

    private void decrementThreshold(Widget.ClickData clickData) {
        if (auxiliaryBlastFurnaceNumber == 0)
            this.thresholdPercentage = MathHelper.clamp(thresholdPercentage, 1, 1);
        if (auxiliaryBlastFurnaceNumber == 1)
            this.thresholdPercentage = MathHelper.clamp(thresholdPercentage - 1, 1, 3);
        else this.thresholdPercentage = MathHelper.clamp(thresholdPercentage - 1, 1, 4);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        if (context.get("AuxiliaryBlastFurnace1") != null) {
            auxiliaryBlastFurnaceNumber += 1;
        }
        if (context.get("AuxiliaryBlastFurnace2") != null) {
            auxiliaryBlastFurnaceNumber += 1;
        }
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        auxiliaryBlastFurnaceNumber = 0;
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("     DDD     ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ")
                .aisle("    CDDDC    ", "    CDDDC    ", "    CDDDC    ", "     DDD     ", "             ", "             ", "             ", "             ", "             ")
                .aisle("AAAGDDDDDJFFF", "GGG D###D JJJ", " G  D###D  J ", " G  D###D  J ", " G   DDD   J ", " G    D    J ", "      D      ", "      D      ", "      D      ")
                .aisle("AAAGDDDDDJFFF", "G@GHD#&#DIJ$J", "G G D###D J J", "G*G D###D J!J", "G*G D###D J!J", "G*G  D#D  J!J", "     D#D     ", "     D#D     ", "     D#D     ")
                .aisle("AAAGDDDDDJFFF", "GGG D###D JJJ", " G  D###D  J ", " G  D###D  J ", " G   DDD   J ", " G    D    J ", "      D      ", "      D      ", "      D      ")
                .aisle("    CDDDC    ", "    CDSDC    ", "    CDDDC    ", "     DDD     ", "             ", "             ", "             ", "             ", "             ")
                .aisle("     DDD     ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ")
                .where('S', selfPredicate())
                .where('A', TraceabilityPredicate.optionalStates("AuxiliaryBlastFurnace1", getFireBoxState()))
                .where('C', states(getFrameState()))
                .where('D', states(getCasingState())
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMaxGlobalLimited(2))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS).setMaxGlobalLimited(2)))
                .where('F', TraceabilityPredicate.optionalStates("AuxiliaryBlastFurnace2", getFireBoxState()))
                .where('G', TraceabilityPredicate.optionalStates("AuxiliaryBlastFurnace1", getCasingState()))
                .where('H', TraceabilityPredicate.optionalStates("AuxiliaryBlastFurnace1", getBoilerState()))
                .where('I', TraceabilityPredicate.optionalStates("AuxiliaryBlastFurnace2", getBoilerState()))
                .where('J', TraceabilityPredicate.optionalStates("AuxiliaryBlastFurnace2", getCasingState()))
                .where('&', air().or(SNOW_PREDICATE))
                .where('#', air())
                .where('@', any())
                .where('*', any())
                .where('$', any())
                .where('!', any())
                .where(' ', any())
                .build();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.PRIMITIVE_BRICKS;
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

    public int cost() {
        return (int) Math.pow(2, (double) Temp / 7500);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        if (isStructureFormed()) {
            textList.add(new TextComponentTranslation("gtsteam.multiblock.ip1.amount", thresholdPercentage * 10, 40));
            textList.add(new TextComponentTranslation("gtsteam.multiblock.ip2.amount", Temp / 10, 3000));
            textList.add(new TextComponentTranslation("gtsteam.multiblock.ip3.amount", cost()));
            textList.add(new TextComponentTranslation("gtsteam.machine.industrial_primitive_blast_furnace.auxiliary_blast_furnace", auxiliaryBlastFurnaceNumber));
        }
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(TooltipHelper.RAINBOW_SLOW + I18n.format("跨纬度等泥土子锻炉", new Object[0]));
        tooltip.add(I18n.format("gtsteam.machine.industrial_primitive_blast_furnace.tooltip.1"));
        tooltip.add(I18n.format("gtsteam.machine.industrial_primitive_blast_furnace.tooltip.2"));
        tooltip.add(I18n.format("gtsteam.machine.industrial_primitive_blast_furnace.tooltip.3"));
        tooltip.add(I18n.format("gtsteam.machine.industrial_primitive_blast_furnace.tooltip.4"));
        tooltip.add(I18n.format("gtsteam.machine.industrial_primitive_blast_furnace.tooltip.5"));
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        ArrayList<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
        MultiblockShapeInfo.Builder builder = null;
        if (Blocks.AIR != null) {
            builder = MultiblockShapeInfo.builder()
                    .aisle("     DDD     ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ")
                    .aisle("    CDDDC    ", "    CXSYC    ", "    CDDDC    ", "     DDD     ", "             ", "             ", "             ", "             ", "             ")
                    .aisle("AAAGDDDDDJFFF", "GGG D   D JJJ", " G  D   D  J ", " G  D   D  J ", " G   DDD   J ", " G    D    J ", "      D      ", "      D      ", "      D      ")
                    .aisle("AAAGDDDDDJFFF", "G GHD   DIJ J", "G G D   D J J", "G*G D   D J!J", "G G D   D J J", "G G  D D  J J", "     D D     ", "     D D     ", "     D D     ")
                    .aisle("AAAGDDDDDJFFF", "GGG D   D JJJ", " G  D   D  J ", " G  D   D  J ", " G   DDD   J ", " G    D    J ", "      D      ", "      D      ", "      D      ")
                    .aisle("    CDDDC    ", "    CDDDC    ", "    CDDDC    ", "     DDD     ", "             ", "             ", "             ", "             ", "             ")
                    .aisle("     DDD     ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ")
                    .where('S', GTSteamMetaTileEntities.INDUSTRIAL_PRIMITIVE_BLAST_FURNACE, EnumFacing.SOUTH)
                    .where('C', getFrameState())
                    .where('D', getCasingState())
                    .where('X', MetaTileEntities.ITEM_IMPORT_BUS[GTValues.ULV], EnumFacing.SOUTH)
                    .where('Y', MetaTileEntities.ITEM_EXPORT_BUS[GTValues.ULV], EnumFacing.SOUTH)
                    .where(' ', Blocks.AIR.getDefaultState());
            shapeInfo.add(builder.build());
            shapeInfo.add(builder
                    .where('A', getFireBoxState())
                    .where('G', getCasingState())
                    .where('H', getBoilerState())
                    .build());
            shapeInfo.add(builder
                    .where('F', getFireBoxState())
                    .where('I', getBoilerState())
                    .where('J', getCasingState())
                    .build());
        }
        return shapeInfo;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public void update() {
        super.update();
        if (Temp > 3000 && Temp <= 12000) Temp--;
        if (Temp > 12000 && Temp <= 21000) Temp = Temp - 2;
        if (Temp > 21000 && Temp <= 30000) Temp = Temp - 3;

        if (this.isActive()) {
            if (getWorld().isRemote) {
                pollutionParticles();
            }
        }
    }

    private void pollutionParticles() {
        BlockPos pos = this.getPos();
        EnumFacing facing = this.getFrontFacing().getOpposite();
        float xPos = facing.getXOffset() * 2 + pos.getX() + 0.5F;
        float yPos = facing.getYOffset() * 2 + pos.getY() + 0.25F;
        float zPos = facing.getZOffset() * 2 + pos.getZ() + 0.5F;

        float ySpd = facing.getYOffset() * 0.7F + 0.7F + 0.8F * GTValues.RNG.nextFloat();

        arunMufflerEffect(xPos, yPos, zPos, 0, ySpd, 0);
        arunMufflerEffect(xPos, yPos, zPos, 0.1F, ySpd, 0.1F);
        arunMufflerEffect(xPos, yPos, zPos, -0.1F, ySpd, -0.1F);
        arunMufflerEffect(xPos, yPos, zPos, +0.1F, ySpd, -0.1F);
        arunMufflerEffect(xPos, yPos, zPos, -0.1F, ySpd, +0.1F);
    }

    public void arunMufflerEffect(float xPos, float yPos, float zPos, float xSpd, float ySpd, float zSpd) {
        this.getWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, xPos, yPos, zPos, xSpd, ySpd, zSpd);
    }

    protected class IndustrialPrimitiveBlastFurnaceLogic extends PrimitiveRecipeLogic {

        public IndustrialPrimitiveBlastFurnaceLogic(RecipeMapNoEnergyMultiblockController tileEntity, RecipeMap<?> recipeMap) {
            super(tileEntity, recipeMap);
        }

        @Override
        public void setMaxProgress(int maxProgress) {
            this.maxProgressTime = maxProgress / cost();
        }

        protected void updateRecipeProgress() {
            if (canRecipeProgress) {
                if (Temp < 30000) Temp = Math.min(Temp + thresholdPercentage, 30000);
                if (++progressTime > maxProgressTime) {
                    completeRecipe();
                }
            }

        }
    }

}