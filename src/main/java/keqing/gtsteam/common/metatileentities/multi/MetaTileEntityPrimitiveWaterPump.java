package keqing.gtsteam.common.metatileentities.multi;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.IPrimitivePump;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.LocalizationUtils;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import gregtech.common.blocks.wood.BlockGregPlanks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static gregtech.api.unification.material.Materials.Water;
import static gregtech.client.renderer.texture.Textures.BRONZE_PLATED_BRICKS;
import static gregtech.client.renderer.texture.Textures.SOLID_STEEL_CASING;

public class MetaTileEntityPrimitiveWaterPump extends MultiblockControllerBase implements IPrimitivePump {
    private IFluidTank waterTank;
    private int biomeModifier = 0;
    private int hatchModifier = 0;


    public MetaTileEntityPrimitiveWaterPump(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        resetTileAbilities();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityPrimitiveWaterPump(metaTileEntityId);
    }

    public void update() {
        super.update();
        if (!this.getWorld().isRemote && this.getOffsetTimer() % 20L == 0L && this.isStructureFormed()) {
            if (this.biomeModifier == 0) {
                this.biomeModifier = this.getAmount();
            } else if (this.biomeModifier > 0) {
                this.waterTank.fill(Materials.Water.getFluid(this.getFluidProduction()), true);
            }
        }

    }

    private int getAmount() {
        WorldProvider provider = this.getWorld().provider;
        if (!provider.isNether() && !provider.doesWaterVaporize()) {
            Biome biome = this.getWorld().getBiome(this.getPos());
            Set<BiomeDictionary.Type> biomeTypes = BiomeDictionary.getTypes(biome);
            if (biomeTypes.contains(BiomeDictionary.Type.NETHER)) {
                return 0;
            } else if (biomeTypes.contains(BiomeDictionary.Type.WATER)) {
                return 1000;
            } else if (!biomeTypes.contains(BiomeDictionary.Type.SWAMP) && !biomeTypes.contains(BiomeDictionary.Type.WET)) {
                if (biomeTypes.contains(BiomeDictionary.Type.JUNGLE)) {
                    return 350;
                } else if (biomeTypes.contains(BiomeDictionary.Type.SNOWY)) {
                    return 300;
                } else if (!biomeTypes.contains(BiomeDictionary.Type.PLAINS) && !biomeTypes.contains(BiomeDictionary.Type.FOREST)) {
                    if (biomeTypes.contains(BiomeDictionary.Type.COLD)) {
                        return 175;
                    } else {
                        return biomeTypes.contains(BiomeDictionary.Type.BEACH) ? 170 : 100;
                    }
                } else {
                    return 250;
                }
            } else {
                return 800;
            }
        } else {
            return 0;
        }
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return null;
    }

    @Override
    protected boolean openGUIOnRightClick() {
        return false;
    }

    @Override
    protected void updateFormedValid() {

    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        initializeAbilities();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        resetTileAbilities();
    }

    private void initializeAbilities() {
        List<IFluidTank> tanks = this.getAbilities(MultiblockAbility.PUMP_FLUID_HATCH);
        if (tanks != null && !tanks.isEmpty()) {
            this.hatchModifier = 1;
        } else {
            tanks = this.getAbilities(MultiblockAbility.EXPORT_FLUIDS);
            this.hatchModifier = tanks.get(0).getCapacity() == 8000 ? 2 : 4;
        }

        this.waterTank = tanks.get(0);
    }

    private void resetTileAbilities() {
        this.waterTank = new FluidTank(0);
    }
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("A   A", "A   A", "BBBBB", "A   A", "A   A", "BBBBB")
                .aisle("     ", "     ", "BBBBB", " CCC ", " CCC ", "BBBBB")
                .aisle("     ", "     ", "BBBBB", " CCC ", " CCC ", "BBBBB")
                .aisle("     ", "     ", "BBBBB", " CSC ", " CCC ", "BBBBB")
                .aisle("A   A", "A   A", "BBBBB", "A   A", "A   A", "BBBBB")
                .where('S', selfPredicate())
                .where('A', frames(Materials.TreatedWood))
                .where('B', states(MetaBlocks.PLANKS.getState(BlockGregPlanks.BlockType.TREATED_PLANK)))
                .where('C', states(getCasingState())
                        .or(metaTileEntities(MetaTileEntities.FLUID_EXPORT_HATCH[0], MetaTileEntities.FLUID_EXPORT_HATCH[1]).setExactLimit(1)))
                .where(' ', any())
                .build();
    }

    public IBlockState getCasingState() {
        return ConfigHolder.machines.steelSteamMultiblocks ?
                MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID) :
                MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.BRONZE_BRICKS);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return ConfigHolder.machines.steelSteamMultiblocks ? SOLID_STEEL_CASING : BRONZE_PLATED_BRICKS;
    }
    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.PRIMITIVE_PUMP_OVERLAY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(), true, true);
    }

    public String[] getDescription() {
        List<String> list = new ArrayList<>();
        list.add(I18n.format("gregtech.multiblock.primitive_water_pump.description"));
        Collections.addAll(list, LocalizationUtils.formatLines("gregtech.multiblock.primitive_water_pump.extra1"));
        Collections.addAll(list, LocalizationUtils.formatLines("gregtech.multiblock.primitive_water_pump.extra2"));
        return list.toArray(new String[0]);
    }

    private boolean isRainingInBiome() {
        World world = this.getWorld();
        return world.isRaining() && world.getBiome(this.getPos()).canRain();
    }

    public int getFluidProduction() {
        return (int)((double)(this.biomeModifier * this.hatchModifier) * (this.isRainingInBiome() ? 1.5 : 1.0))*4;
    }

    public boolean allowsExtendedFacing() {
        return false;
    }
}