package keqing.gtsteam.common.metatileentities.multi.primitive;

import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.ui.KeyManager;
import gregtech.api.metatileentity.multiblock.ui.UISyncer;
import gregtech.api.mui.GTGuiTheme;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.logic.OCResult;
import gregtech.api.recipes.properties.RecipePropertyStorage;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import gregtech.api.util.KeyUtil;
import gregtech.api.util.tooltips.InformationHandler;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockFireboxCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import keqing.gtsteam.api.capability.impl.NoEnergyMultiblockRecipeLogic;
import keqing.gtsteam.api.metatileentity.multiblock.NoEnergyMultiblockController;
import keqing.gtsteam.api.pattern.TraceabilityPredicate;
import keqing.gtsteam.client.textures.GTSteamTextures;
import keqing.gtsteam.common.block.GTSteamMetaBlocks;
import keqing.gtsteam.common.block.blocks.BlockMultiblockCasing0;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static gregtech.api.recipes.RecipeMaps.COKE_OVEN_RECIPES;
import static gregtech.api.util.RelativeDirection.*;

public class MetaTileEntityIndustrialCokeOven extends NoEnergyMultiblockController {

    private static final gregtech.api.pattern.TraceabilityPredicate SNOW_PREDICATE = new gregtech.api.pattern.TraceabilityPredicate(
            bws -> GTUtility.isBlockSnow(bws.getBlockState()));

    private static final int MIN_TEMP = 300;
    private static final int MAX_TEMP = 1500;
    private int TEMP = MIN_TEMP;
    int size;

    public MetaTileEntityIndustrialCokeOven(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, COKE_OVEN_RECIPES);
        this.recipeMapWorkable = new IndustrialCokeOvenLogic(this, COKE_OVEN_RECIPES);
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

    protected IBlockState getCasingState() {
        return GTSteamMetaBlocks.blockMultiblockCasing0.getState(BlockMultiblockCasing0.CasingType.GALVANIZED_PORCELAIN_TILES);
    }


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("Temp", TEMP);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        TEMP = data.getInteger("Temp");
        TEMP = Math.max(MIN_TEMP, Math.min(MAX_TEMP, TEMP)); // 安全边界
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityIndustrialCokeOven(metaTileEntityId);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        size = structurePattern.formedRepetitionCount[1];
    }

    @Override
    protected BlockPattern createStructurePattern() {
        FactoryBlockPattern pattern = FactoryBlockPattern.start(RIGHT, UP, FRONT)
                .aisle("XXXXX", "#XYX#", "#XXX#", "#####", "#####", "#####")
                .aisle("XXXXX", "#XPX#", "#XXX#", "#####", "#####", "#####")
                .aisle("XXXXX", "FXPXF", "FXXXF", "FFFFF", "#####", "#####")
                .aisle("BXXXB", "X#P#X", "X###X", "FXXXF", "##X##", "##X##")
                .aisle("BXXXB", "X#P#X", "X###X", "FX&XF", "#X#X#", "#X#X#").setRepeatable(1, 8)
                .aisle("BXXXB", "X#P#X", "X###X", "FXXXF", "##X##", "##X##")
                .aisle("XXXXX", "FXXXF", "FXXXF", "FFFFF", "#####", "#####")
                .where('B',  states(getFireBoxState()))
                .where('P',  states(getBoilerState()))
                .where('F', states(getFrameState()))
                .where('X', states(getCasingState())
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setPreviewCount(1).setMaxGlobalLimited(4))
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setPreviewCount(1).setMaxGlobalLimited(4))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS).setPreviewCount(1).setMaxGlobalLimited(4)))
                .where('#', any())
                .where('&', air().or(SNOW_PREDICATE)) // this won't stay in the structure, and will be broken while
                // running
                .where('Y', selfPredicate());
        return pattern.build();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return GTSteamTextures.PORCELAIN_TILES;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.COKE_OVEN_OVERLAY;
    }

    @Override
    public void addCustomCapacity(KeyManager keyManager, UISyncer syncer) {
        keyManager.add(KeyUtil.lang(TextFormatting.GRAY, "gtsteam.multiblock.ip.amount.1", syncer.syncInt(TEMP), MAX_TEMP));
        keyManager.add(KeyUtil.lang(TextFormatting.GRAY, "gtsteam.machine.industrial_coke_oven.auxiliary_count", syncer.syncInt(size)));
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, boolean advanced) {
        InformationHandler.topTooltips("高效批量生产焦炭与副产品的工业级解决方案", tooltip);
        super.addInformation(stack, player, tooltip, advanced);
        TooltipBuilder.create().addSpecialLogic().build(this, tooltip);
        tooltip.add(I18n.format("gtsteam.machine.industrial_coke_oven.tooltip.1"));
        tooltip.add(I18n.format("gtsteam.machine.industrial_coke_oven.tooltip.2"));
        tooltip.add(I18n.format("gtsteam.machine.industrial_coke_oven.tooltip.3"));
    }



    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public void update() {
        super.update();

        // 温度自然衰减（非工作状态）
        if (!this.isActive() && TEMP > MIN_TEMP) {
            int delta = (TEMP - MIN_TEMP) / 300 + 1; // 每300K温差增加1点衰减
            TEMP = Math.max(MIN_TEMP, TEMP - delta);
        }

        // 工作状态温度上升与粒子效果
        if (this.isActive()) {
            if (getOffsetTimer() % 20 == 0) {
                // 温度上升：基础1度 + 每个附属1度
                if (TEMP < MAX_TEMP) {
                    TEMP += (size + 1);
                    TEMP = Math.min(MAX_TEMP, TEMP);
                }
            }

            // 热浪粒子效果（顶部排气口）
            if (getOffsetTimer() % 10 == 0 && getWorld().isRemote) {
                pollutionParticles();
            }
        }
    }

    private void pollutionParticles() {
        BlockPos pos = this.getPos();
        EnumFacing facing = this.getFrontFacing().getOpposite();
        for (int i = 0; i < size; i++) {
            float xPos = facing.getXOffset() * (4 + i) + pos.getX() + 0.5F;
            float yPos = facing.getYOffset() * (4 + i) + pos.getY() + 0.25F;
            float zPos = facing.getZOffset() * (4 + i) + pos.getZ() + 0.5F;

            float ySpd = facing.getYOffset() * 0.7F + 0.7F + 0.8F * GTValues.RNG.nextFloat();

            arunMufflerEffect(xPos, yPos, zPos, 0, ySpd, 0);
            arunMufflerEffect(xPos, yPos, zPos, 0.1F, ySpd, 0.1F);
            arunMufflerEffect(xPos, yPos, zPos, -0.1F, ySpd, -0.1F);
            arunMufflerEffect(xPos, yPos, zPos, +0.1F, ySpd, -0.1F);
            arunMufflerEffect(xPos, yPos, zPos, -0.1F, ySpd, +0.1F);
        }
    }

    public void arunMufflerEffect(float xPos, float yPos, float zPos, float xSpd, float ySpd, float zSpd) {
        this.getWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, xPos, yPos, zPos, xSpd, ySpd, zSpd);
    }

    @Override
    public GTGuiTheme getUITheme() {
        return GTGuiTheme.BRONZE;
    }


    protected class IndustrialCokeOvenLogic extends NoEnergyMultiblockRecipeLogic {

        public IndustrialCokeOvenLogic(NoEnergyMultiblockController tileEntity, RecipeMap<?> recipeMap) {
            super(tileEntity, recipeMap);
        }

        @Override
        protected void modifyOverclockPost(@NotNull OCResult ocResult, @NotNull RecipePropertyStorage storage) {
            super.modifyOverclockPost(ocResult, storage);

            // 温度加速机制：每200K温差减少20%时间（乘算）
            if (TEMP > MIN_TEMP) {
                int tempDiff = TEMP - MIN_TEMP;
                int bonusSteps = tempDiff / 200;
                double multiplier = Math.pow(0.8, bonusSteps);
                int newDuration = (int) (ocResult.duration() * multiplier);
                ocResult.setDuration(Math.max(1, newDuration));
            }
        }

        @Override
        protected void updateRecipeProgress() {
            if (canRecipeProgress) {
                if (++progressTime > maxProgressTime) {
                    completeRecipe();
                }
            }
        }
    }
}