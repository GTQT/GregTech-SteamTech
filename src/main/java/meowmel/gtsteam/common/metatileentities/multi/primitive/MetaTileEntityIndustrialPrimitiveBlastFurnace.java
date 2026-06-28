package meowmel.gtsteam.common.metatileentities.multi.primitive;

import gregtech.api.GTValues;
import gregtech.api.capability.impl.NoEnergyMultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.NoEnergyMultiblockController;
import gregtech.api.metatileentity.multiblock.ui.KeyManager;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.metatileentity.multiblock.ui.UISyncer;
import gregtech.api.mui.GTGuiTheme;
import gregtech.api.pattern.*;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.StructureDefinition;
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
import meowmel.gtsteam.client.textures.GTSteamTextures;
import meowmel.gtsteam.common.block.GTSteamMetaBlocks;
import meowmel.gtsteam.common.block.blocks.BlockMultiblockCasing0;
import meowmel.gtsteam.common.metatileentities.GTSteamMetaTileEntities;
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

import static gregtech.api.recipes.RecipeMaps.PRIMITIVE_BLAST_FURNACE_RECIPES;

public class MetaTileEntityIndustrialPrimitiveBlastFurnace extends NoEnergyMultiblockController {

    private static final gregtech.api.pattern.TraceabilityPredicate SNOW_PREDICATE = new gregtech.api.pattern.TraceabilityPredicate(
            bws -> GTUtility.isBlockSnow(bws.getBlockState()));

    private static final StructureDefinition<?> DEFINITION = StructureDefinition.getOrBuild(
            "gtsteam:industrial_primitive_blast_furnace", () ->
                    DeclarativePatternBuilder.start()
                            .aisle("     DDD     ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ")
                            .aisle("    CDDDC    ", "    CDDDC    ", "    CDDDC    ", "     DDD     ", "             ", "             ", "             ", "             ", "             ")
                            .aisle("AAADDDDDDAHHH", "DDD D###D DDD", " D  D###D  D ", " D  D###D  D ", " D   DDD   D ", " D    D    D ", "      D      ", "      D      ", "      D      ")
                            .aisle("AAADDDDDDAHHH", "D@DHD#&#DHH$D", "D D D###D D D", "D*D D###D D!D", "D*D D###D D!D", "D*D  D#D  D!D", "     D#D     ", "     D#D     ", "     D#D     ")
                            .aisle("AAADDDDDDAHHH", "DDD D###D DDD", " D  D###D  D ", " D  D###D  D ", " D   DDD   D ", " D    D    D ", "      D      ", "      D      ", "      D      ")
                            .aisle("    CDDDC    ", "    CDSDC    ", "    CDDDC    ", "     DDD     ", "             ", "             ", "             ", "             ", "             ")
                            .aisle("     DDD     ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ")
                            .self('S', MetaTileEntityIndustrialPrimitiveBlastFurnace.class)
                            .where('A', states(getFireBoxState()))
                            .where('C', states(getFrameState()))
                            .casing('D', getCasingState())
                            .optionalItemInput(4)
                            .optionalItemOutput(4)
                            .where('H', states(getBoilerState()))
                            .where('&', air().or(SNOW_PREDICATE))
                            .where('#', air())
                            .where('@', any())
                            .where('*', any())
                            .where('$', any())
                            .where('!', any())
                            .where(' ', any())
                            .buildStructureDefinition()
    );
    int TEMP = 300;
    int MIN_TEMP = 300;
    int MAX_TEMP = 1500;

    public MetaTileEntityIndustrialPrimitiveBlastFurnace(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, PRIMITIVE_BLAST_FURNACE_RECIPES);
        this.recipeMapWorkable = new IndustrialPrimitiveBlastFurnaceLogic(this);
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

    public static IBlockState getCasingState() {
        return GTSteamMetaBlocks.blockMultiblockCasing0.getState(BlockMultiblockCasing0.CasingType.GALVANIZED_PORCELAIN_TILES);
    }

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return DEFINITION;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("Temp", TEMP);
        return super.writeToNBT(data);
    }

    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        TEMP = data.getInteger("Temp");
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityIndustrialPrimitiveBlastFurnace(metaTileEntityId);
    }
    @Override
    protected void formStructure(@NotNull FormedStructureView formed) {
        super.formStructure(formed);
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
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
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive())
                .addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.getMaxVoltage()))
                .addCustom(this::addCustomCapacity)
                .addParallelsLine(recipeMapWorkable.getParallelLimit())
                .addWorkingStatusLine()
                .addProgressLine(recipeMapWorkable.getProgress(), recipeMapWorkable.getMaxProgress())
                .addRecipeOutputLine(recipeMapWorkable);
    }

    public void addCustomCapacity(KeyManager keyManager, UISyncer syncer) {
        keyManager.add(KeyUtil.lang(TextFormatting.GRAY, "gtsteam.multiblock.ip.amount.1", syncer.syncInt(TEMP), MAX_TEMP));
    }

    @Override
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip, boolean advanced) {
        InformationHandler.topTooltips("前期批发钢材的最好选择", tooltip);
        super.addInformation(stack, player, tooltip, advanced);
        TooltipBuilder.create().addSpecialLogic().build(this, tooltip);
        tooltip.add(I18n.format("gtsteam.machine.industrial_primitive_blast_furnace.tooltip.1"));
        tooltip.add(I18n.format("gtsteam.machine.industrial_primitive_blast_furnace.tooltip.2"));
        tooltip.add(I18n.format("gtsteam.machine.industrial_primitive_blast_furnace.tooltip.3"));
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        ArrayList<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
        if (Blocks.AIR != null) {
            shapeInfo.add(MultiblockShapeInfo.builder()
                    .aisle("     DDD     ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ")
                    .aisle("    CDDDC    ", "    CDDDC    ", "    CDDDC    ", "     DDD     ", "             ", "             ", "             ", "             ", "             ")
                    .aisle("AAADDDDDDAHHH", "DDD D   D DDD", " D  D   D  D ", " D  D   D  D ", " D   DDD   D ", " D    D    D ", "      D      ", "      D      ", "      D      ")
                    .aisle("AAADDDDDDAHHH", "D DHD   DHH D", "D D D   D D D", "D*D D   D D!D", "D D D   D D D", "D D  D D  D D", "     D D     ", "     D D     ", "     D D     ")
                    .aisle("AAADDDDDDAHHH", "DDD D   D DDD", " D  D   D  D ", " D  D   D  D ", " D   DDD   D ", " D    D    D ", "      D      ", "      D      ", "      D      ")
                    .aisle("    CDDDC    ", "    CXSYC    ", "    CDDDC    ", "     DDD     ", "             ", "             ", "             ", "             ", "             ")
                    .aisle("     DDD     ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ")
                    .where('S', GTSteamMetaTileEntities.INDUSTRIAL_PRIMITIVE_BLAST_FURNACE, EnumFacing.SOUTH)
                    .where('C', getFrameState())
                    .where('D', getCasingState())
                    .where('A', getFireBoxState())
                    .where('H', getBoilerState())
                    .where('X', MetaTileEntities.ITEM_IMPORT_BUS[GTValues.ULV], EnumFacing.SOUTH)
                    .where('Y', MetaTileEntities.ITEM_EXPORT_BUS[GTValues.ULV], EnumFacing.SOUTH)
                    .where('@', Blocks.AIR.getDefaultState())
                    .where('*', Blocks.AIR.getDefaultState())
                    .where('$', Blocks.AIR.getDefaultState())
                    .where('!', Blocks.AIR.getDefaultState())
                    .where('#', Blocks.AIR.getDefaultState())
                    .where('&', Blocks.AIR.getDefaultState())
                    .where(' ', Blocks.AIR.getDefaultState())
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
        if (TEMP > MIN_TEMP) {
            if (TEMP > 300) TEMP--;
            if (TEMP > 600) TEMP -= 2;
            if (TEMP > 900) TEMP = TEMP - 3;
            TEMP = Math.max(MIN_TEMP, TEMP);
        }


        if (this.isActive()) {
            if (getOffsetTimer() % 20 == 0 && getWorld().isRemote) {
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

    @Override
    public boolean isBatchAllowed() {
        return false;
    }

    @Override
    public double getPollutionAmount() {
        return 0.0075;
    }

    @Override
    public GTGuiTheme getUITheme() {
        return GTGuiTheme.BRONZE;
    }

    protected class IndustrialPrimitiveBlastFurnaceLogic extends NoEnergyMultiblockRecipeLogic {

        public IndustrialPrimitiveBlastFurnaceLogic(NoEnergyMultiblockController tileEntity) {
            super(tileEntity);
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
                //持续工作温度增加
                if (getOffsetTimer() % 20 == 0) {
                    if (TEMP < MAX_TEMP) {
                        TEMP += 5;
                        TEMP = Math.min(MAX_TEMP, TEMP);
                    }
                }
                if (++progressTime > maxProgressTime) {
                    completeRecipe();
                }
            }
        }
    }
}