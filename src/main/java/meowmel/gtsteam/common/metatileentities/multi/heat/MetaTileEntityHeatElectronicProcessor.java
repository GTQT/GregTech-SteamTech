package meowmel.gtsteam.common.metatileentities.multi.heat;

import gregtech.api.pattern.element.StructureDefinition;

import static gregtech.api.pattern.element.Elements.*;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.HeatMultiblockController;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.TemplatePool;
import gregtech.api.pattern.casing.CasingDefinition;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.tooltips.TooltipBuilder;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockFireboxCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;
import meowmel.gtsteam.api.recipes.GTSRecipeMaps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static gregtech.api.util.RelativeDirection.*;

public class MetaTileEntityHeatElectronicProcessor extends HeatMultiblockController {

    private static final StructureDefinition<?> DEFINITION = StructureDefinition.getOrBuild("gtsteam:heat_electronic_processor", () ->
            DeclarativePatternBuilder.start(RIGHT, UP, BACK)
                    .aisle("FFFGGG", "CCCCCC", "CCCCCC")
                    .aisle("FCF###", "CPPPPC", "CCCCCC")
                    .aisle("FFFGGG", "CSCCCC", "CCCCCC")
                    .self('S', MetaTileEntityHeatElectronicProcessor.class)
                    .casing('C', getCasingState())
                    .itemInput(1, 3)
                    .itemOutput(1, 3)
                    .hatch(MultiblockAbility.INPUT_HEAT, 1)
                    .where('F', blocks(getFireBoxState()))
                    .where('G', blocks(getFrameState()))
                    .where('P', blocks(getPipeState()))
                    .where('#', any())
                    .buildStructureDefinition()
    );

    public MetaTileEntityHeatElectronicProcessor(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTSRecipeMaps.ELECTRONIC_PROCESSOR_RECIPES);
    }

    private static IBlockState getFrameState() {
        return MetaBlocks.FRAMES.get(Materials.Steel).getBlock(Materials.Steel);
    }

    public static IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    private static IBlockState getPipeState() {
        return MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE);
    }

    private static IBlockState getFireBoxState() {
        return MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.STEEL_FIREBOX);
    }

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return DEFINITION;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityHeatElectronicProcessor(metaTileEntityId);
    }

    @SideOnly(Side.CLIENT)
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
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
