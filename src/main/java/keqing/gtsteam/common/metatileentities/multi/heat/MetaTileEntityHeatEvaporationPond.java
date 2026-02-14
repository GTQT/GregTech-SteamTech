package keqing.gtsteam.common.metatileentities.multi.heat;

import gregtech.api.capability.impl.HeatMultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.HeatMultiblockController;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.MultiblockShapeInfo;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockFireboxCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;
import gtqt.common.metatileentities.GTQTMetaTileEntities;
import keqing.gtsteam.api.recipes.GTSRecipeMaps;
import keqing.gtsteam.common.block.GTSteamMetaBlocks;
import keqing.gtsteam.common.block.blocks.BlockEvaporationBed;
import keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.EnumFacing.NORTH;
import static net.minecraft.util.EnumFacing.SOUTH;

public class MetaTileEntityHeatEvaporationPond extends HeatMultiblockController {

    private int tier;

    public MetaTileEntityHeatEvaporationPond(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTSRecipeMaps.EVAPORATION_RECIPES);
        this.recipeMapWorkable = new HeatEvaporationPondMultiblockRecipeLogic(this);
    }

    public int getTier() {
        return tier;
    }

    private static IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    private static IBlockState getPipeState() {
        return GTSteamMetaBlocks.blockEvaporationBed.getState(BlockEvaporationBed.EvaporationBedType.DIRT);
    }

    @Override
    public void checkStructurePattern() {
        if (!this.isStructureFormed()) {
            reinitializeStructurePattern();
        }
        super.checkStructurePattern();
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        if (getWorld() != null) updateStructureDimensions();
        var pattern = FactoryBlockPattern.start();
        if (tier < 1 || tier > 5) tier = 1;

        if (tier == 1)//TIER 1
            pattern = pattern
                    .aisle("FFFFF", "     ")
                    .aisle("FCCCF", " CCC ")
                    .aisle("FCPCF", " C C ")
                    .aisle("FCCCF", " CSC ")
                    .aisle("FFFFF", "     ");
        else if (tier == 2)//TIER 2
            pattern = pattern
                    .aisle("FFFFFFF", "       ")
                    .aisle("FCCCCCF", " CCCCC ")
                    .aisle("FCPPPCF", " C   C ")
                    .aisle("FCPPPCF", " C   C ")
                    .aisle("FCPPPCF", " C   C ")
                    .aisle("FCCCCCF", " CCSCC ")
                    .aisle("FFFFFFF", "       ");
        else if (tier == 3)//TIER 3
            pattern = pattern
                    .aisle("FFFFFFFFF", "         ")
                    .aisle("FCCCCCCCF", " CCCCCCC ")
                    .aisle("FCPPPPPCF", " C     C ")
                    .aisle("FCPPPPPCF", " C     C ")
                    .aisle("FCPPPPPCF", " C     C ")
                    .aisle("FCPPPPPCF", " C     C ")
                    .aisle("FCPPPPPCF", " C     C ")
                    .aisle("FCCCCCCCF", " CCCSCCC ")
                    .aisle("FFFFFFFFF", "         ");

        else if (tier == 4)//TIER 4
            pattern = pattern
                    .aisle("FFFFFFFFFFF", "           ")
                    .aisle("FCCCCCCCCCF", " CCCCCCCCC ")
                    .aisle("FCPPPPPPPCF", " C       C ")
                    .aisle("FCPPPPPPPCF", " C       C ")
                    .aisle("FCPPPPPPPCF", " C       C ")
                    .aisle("FCPPPPPPPCF", " C       C ")
                    .aisle("FCPPPPPPPCF", " C       C ")
                    .aisle("FCPPPPPPPCF", " C       C ")
                    .aisle("FCPPPPPPPCF", " C       C ")
                    .aisle("FCCCCCCCCCF", " CCCCSCCCC ")
                    .aisle("FFFFFFFFFFF", "           ");

        else if (tier == 5)//TIER 5
            pattern = pattern
                    .aisle("FFFFFFFFFFFFF", "             ")
                    .aisle("FCCCCCCCCCCCF", " CCCCCCCCCCC ")
                    .aisle("FCPPPPPPPPPCF", " C         C ")
                    .aisle("FCPPPPPPPPPCF", " C         C ")
                    .aisle("FCPPPPPPPPPCF", " C         C ")
                    .aisle("FCPPPPPPPPPCF", " C         C ")
                    .aisle("FCPPPPPPPPPCF", " C         C ")
                    .aisle("FCPPPPPPPPPCF", " C         C ")
                    .aisle("FCPPPPPPPPPCF", " C         C ")
                    .aisle("FCPPPPPPPPPCF", " C         C ")
                    .aisle("FCPPPPPPPPPCF", " C         C ")
                    .aisle("FCCCCCCCCCCCF", " CCCCCSCCCCC ")
                    .aisle("FFFFFFFFFFFFF", "             ");

        return pattern.where('S', selfPredicate())
                .where('C', states(getCasingState())
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS).setMaxGlobalLimited(2))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMaxGlobalLimited(1))
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMaxGlobalLimited(2))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMaxGlobalLimited(1))
                        .or(abilities(MultiblockAbility.INPUT_HEAT).setExactLimit(1))
                )
                .where('F', states(getFireBoxState()))
                .where('P', states(getPipeState()))
                .where(' ', any())
                .build();

    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        List<MultiblockShapeInfo> shapeInfo = new ArrayList<>();

        MultiblockShapeInfo.Builder builder = MultiblockShapeInfo.builder();

        // TIER 1
        builder
                .aisle("FFFFF", "     ")
                .aisle("FCCCF", " CIC ")
                .aisle("FCPCF", " C C ")
                .aisle("FCCCF", " CSC ")
                .aisle("FFFFF", "     ")
                .where('S', GTSteamMetaTileEntities.HEAT_EVAPORATION_POND, SOUTH)
                .where('I', GTQTMetaTileEntities.HEAT_INPUT_HATCH[0], NORTH)
                .where('C', getCasingState())
                .where('F', getFireBoxState())
                .where('P', getPipeState())
                .where(' ', Blocks.AIR.getDefaultState());
        shapeInfo.add(builder.build());

        // TIER 2
        builder = MultiblockShapeInfo.builder()
                .aisle("FFFFFFF", "       ")
                .aisle("FCCCCCF", " CCICC ")
                .aisle("FCPPPCF", " C   C ")
                .aisle("FCPPPCF", " C   C ")
                .aisle("FCPPPCF", " C   C ")
                .aisle("FCCCCCF", " CCSCC ")
                .aisle("FFFFFFF", "       ")
                .where('S', GTSteamMetaTileEntities.HEAT_EVAPORATION_POND, SOUTH)
                .where('I', GTQTMetaTileEntities.HEAT_INPUT_HATCH[0], NORTH)
                .where('C', getCasingState())
                .where('F', getFireBoxState())
                .where('P', getPipeState())
                .where(' ', Blocks.AIR.getDefaultState());
        shapeInfo.add(builder.build());

        // TIER 3
        builder = MultiblockShapeInfo.builder()
                .aisle("FFFFFFFFF", "         ")
                .aisle("FCCCCCCCF", " CCCICCC ")
                .aisle("FCPPPPPCF", " C     C ")
                .aisle("FCPPPPPCF", " C     C ")
                .aisle("FCPPPPPCF", " C     C ")
                .aisle("FCPPPPPCF", " C     C ")
                .aisle("FCPPPPPCF", " C     C ")
                .aisle("FCCCCCCCF", " CCCSCCC ")
                .aisle("FFFFFFFFF", "         ")
                .where('S', GTSteamMetaTileEntities.HEAT_EVAPORATION_POND, SOUTH)
                .where('I', GTQTMetaTileEntities.HEAT_INPUT_HATCH[0], NORTH)
                .where('C', getCasingState())
                .where('F', getFireBoxState())
                .where('P', getPipeState())
                .where(' ', Blocks.AIR.getDefaultState());
        shapeInfo.add(builder.build());

        // TIER 4
        builder = MultiblockShapeInfo.builder()
                .aisle("FFFFFFFFFFF", "           ")
                .aisle("FCCCCCCCCCF", " CCCCICCCC ")
                .aisle("FCPPPPPPPCF", " C       C ")
                .aisle("FCPPPPPPPCF", " C       C ")
                .aisle("FCPPPPPPPCF", " C       C ")
                .aisle("FCPPPPPPPCF", " C       C ")
                .aisle("FCPPPPPPPCF", " C       C ")
                .aisle("FCPPPPPPPCF", " C       C ")
                .aisle("FCPPPPPPPCF", " C       C ")
                .aisle("FCCCCCCCCCF", " CCCCSCCCC ")
                .aisle("FFFFFFFFFFF", "           ")
                .where('S', GTSteamMetaTileEntities.HEAT_EVAPORATION_POND, SOUTH)
                .where('I', GTQTMetaTileEntities.HEAT_INPUT_HATCH[0], NORTH)
                .where('C', getCasingState())
                .where('F', getFireBoxState())
                .where('P', getPipeState())
                .where(' ', Blocks.AIR.getDefaultState());
        shapeInfo.add(builder.build());

        // TIER 5
        builder = MultiblockShapeInfo.builder()
                .aisle("FFFFFFFFFFFFF", "             ")
                .aisle("FCCCCCCCCCCCF", " CCCCCICCCCC ")
                .aisle("FCPPPPPPPPPCF", " C         C ")
                .aisle("FCPPPPPPPPPCF", " C         C ")
                .aisle("FCPPPPPPPPPCF", " C         C ")
                .aisle("FCPPPPPPPPPCF", " C         C ")
                .aisle("FCPPPPPPPPPCF", " C         C ")
                .aisle("FCPPPPPPPPPCF", " C         C ")
                .aisle("FCPPPPPPPPPCF", " C         C ")
                .aisle("FCPPPPPPPPPCF", " C         C ")
                .aisle("FCPPPPPPPPPCF", " C         C ")
                .aisle("FCCCCCCCCCCCF", " CCCCCSCCCCC ")
                .aisle("FFFFFFFFFFFFF", "             ")
                .where('S', GTSteamMetaTileEntities.HEAT_EVAPORATION_POND, SOUTH)
                .where('I', GTQTMetaTileEntities.HEAT_INPUT_HATCH[0], NORTH)
                .where('C', getCasingState())
                .where('F', getFireBoxState())
                .where('P', getPipeState())
                .where(' ', Blocks.AIR.getDefaultState());
        shapeInfo.add(builder.build());

        return shapeInfo;
    }

    private void updateStructureDimensions() {
        World world = getWorld();
        BlockPos.MutableBlockPos bPos = new BlockPos.MutableBlockPos(getPos());
        EnumFacing front = getFrontFacing();
        EnumFacing back = front.getOpposite();
        tier = 0;
        for (int i = 0; i < 16; i += 1) {
            if ((isBlockEdge(world, bPos, back))) tier = (i + 1) / 2;
        }
    }

    public boolean isBlockEdge(@NotNull World world, @NotNull BlockPos.MutableBlockPos pos,
                               @NotNull EnumFacing direction) {
        IBlockState block = world.getBlockState(pos.move(direction));
        TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof IGregTechTileEntity iGregTechTileEntity) {
            MetaTileEntity metaTileEntity = iGregTechTileEntity.getMetaTileEntity();
            if (metaTileEntity instanceof IMultiblockAbilityPart<?>) {
                return true;
            } else {
                return block == getCasingState();
            }
        } else {
            return block == getCasingState();
        }
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    private IBlockState getFireBoxState() {
        return MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.STEEL_FIREBOX);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityHeatEvaporationPond(metaTileEntityId);
    }

    @SideOnly(Side.CLIENT)
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    public SoundEvent getBreakdownSound() {
        return GTSoundEvents.BREAKDOWN_ELECTRICAL;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("tier", tier);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        tier = data.getInteger("tier");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeVarInt(tier);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        tier = buf.readVarInt();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        TooltipBuilder.create().addHeatMachine(1).build(this, tooltip);
        TooltipBuilder.create().addSpecialLogic().build(this, tooltip);
        tooltip.add(I18n.format("多方块共5个等级，且为正方形，长宽必须相等"));
        tooltip.add(I18n.format("多方块结构长宽每拓展一次，配方并行翻倍"));
    }

    public class HeatEvaporationPondMultiblockRecipeLogic extends HeatMultiblockRecipeLogic{

        public HeatEvaporationPondMultiblockRecipeLogic(HeatMultiblockController tileEntity) {
            super(tileEntity);
        }

        //每一等级 并行翻倍
        @Override
        public int getParallelLimit() {
            return (int) Math.pow(2, getTier()-1);
        }
    }
}
