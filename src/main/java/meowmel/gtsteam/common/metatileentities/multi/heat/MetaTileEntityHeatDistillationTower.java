package meowmel.gtsteam.common.metatileentities.multi.heat;

import static gregtech.api.pattern.element.Elements.*;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.HeatMultiblockController;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.SoftReferenceHolder;
import gregtech.api.pattern.TemplatePool;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.casing.GTStructureChannels;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockFireboxCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;
import meowmel.gtsteam.api.recipes.GTSRecipeMaps;
import meowmel.gtsteam.client.textures.GTSteamTextures;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static gregtech.api.util.RelativeDirection.*;
import static meowmel.gtsteam.common.block.GTSteamMetaBlocks.blockMultiblockCasing0;
import static meowmel.gtsteam.common.block.blocks.BlockMultiblockCasing0.CasingType.TANK_WALL;

public class MetaTileEntityHeatDistillationTower extends HeatMultiblockController {

    private static final SoftReferenceHolder<? extends StructureDefinition<?>> STRUCTURE_DEFINITION =
            TemplatePool.getInstance().registerStructure("gtsteam:heat_distillation_tower", () ->
                    DeclarativePatternBuilder.start(RIGHT, FRONT, UP)
                            .piece("bottom")
                            .aisle("FFF", "FCF", "FFF")
                            .aisle("CSC", "C#C", "CCC")
                            .repeatablePiece("body", 1, 8)
                            .aisle("XXX", "X#X", "XXX")
                            .withAisleChannel(GTStructureChannels.STRUCTURE_HEIGHT.getName())
                            .piece("top")
                            .aisle("XXX", "XXX", "XXX")
                            .self('S', MetaTileEntityHeatDistillationTower.class)
                            .where('F', blocks(getFireBoxState()))
                            .where('C', chain(blocks(getCasingState()),
                                    abilities(0, 1, MultiblockAbility.EXPORT_ITEMS),
                                    abilities(1, 3, MultiblockAbility.INPUT_HEAT),
                                    abilities(1, 1, MultiblockAbility.IMPORT_FLUIDS)))
                            .where('X', chain(blocks(getTankCasingState()),
                                    abilitiesPerLayer(0, 1, 1, MultiblockAbility.EXPORT_FLUIDS)))
                            .where('#', air())
                            .buildStructureDefinition()
            );

    public MetaTileEntityHeatDistillationTower(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTSRecipeMaps.HEAT_DISTILLATION_RECIPES);
    }

    public static IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    public static IBlockState getTankCasingState() {
        return blockMultiblockCasing0.getState(TANK_WALL);
    }

    private static IBlockState getFireBoxState() {
        return MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.STEEL_FIREBOX);
    }

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return STRUCTURE_DEFINITION.get();
    }

    @Override
    public EnumFacing getFrontFacingForStructure() {
        return getFrontFacing().getOpposite();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityHeatDistillationTower(metaTileEntityId);
    }

    private boolean isTankPart(IMultiblockPart sourcePart) {
        return isStructureFormed() && (((MetaTileEntity) sourcePart).getPos().getY() > getPos().getY());
    }

    @Override
    public IBlockState getCasingBlock(@Nullable IMultiblockPart sourcePart) {
        if (sourcePart != null && isTankPart(sourcePart)) {
            return getTankCasingState();
        }
        return getCasingState();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        if (sourcePart != null && isTankPart(sourcePart)) {
            return GTSteamTextures.TANK_WALL;
        }
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    public SoundEvent getBreakdownSound() {
        return GTSoundEvents.BREAKDOWN_ELECTRICAL;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        TooltipBuilder.create().addHeatMachine(1).build(this, tooltip);
    }
}
