package keqing.gtsteam.common.metatileentities.multi.store;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import gregtech.api.GTValues;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.LabelWidget;
import gregtech.api.gui.widgets.TankWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.metatileentity.multiblock.ui.KeyManager;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIBuilder;
import gregtech.api.metatileentity.multiblock.ui.TemplateBarBuilder;
import gregtech.api.metatileentity.multiblock.ui.UISyncer;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.MultiblockShapeInfo;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.util.KeyUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.MetaTileEntities;
import keqing.gtsteam.client.textures.GTSteamTextures;
import keqing.gtsteam.common.metatileentities.GTSteamMetaTileEntities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import static keqing.gtsteam.common.block.GTSteamMetaBlocks.blockMultiblockCasing0;
import static keqing.gtsteam.common.block.blocks.BlockMultiblockCasing0.CasingType.TANK_WALL;
import static net.minecraft.util.EnumFacing.*;

public class MetaTileEntityLargeFluidTank extends MultiblockWithDisplayBase implements ProgressBarMultiblock {
    private int capacity = 0;
    private int Length = 0;
    private int Height = 0;
    private int Width = 0;
    private IFluidTank inputFluidsTank;
    private IFluidTank outputFluidsTank;
    private FluidTank StoragefluidTank = null;

    public MetaTileEntityLargeFluidTank(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        resetTileAbilities();
    }

    public static String repeat(String a, int count) {
        //return String.valueOf(a).repeat(Math.max(0, count));
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(a);
        }
        return builder.toString();
    }

    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityLargeFluidTank(metaTileEntityId);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return GTSteamTextures.TANK_WALL;
    }

    @Override
    protected void updateFormedValid() {
    }

    @Override
    public void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        initializeAbilities();
    }

    @Override
    protected void configureDisplayText(MultiblockUIBuilder builder) {
        builder.addCustom(this::addFluidAmountCapacity);
    }

    private void addFluidAmountCapacity(KeyManager keyManager, UISyncer syncer) {
        if (isStructureFormed()) {
            var FluidAmountString = KeyUtil.number(TextFormatting.WHITE,
                    syncer.syncInt(this.StoragefluidTank.getFluidAmount()), "L");
            String FluidString;
            if (StoragefluidTank.getFluid() != null) {
                FluidString = syncer.syncString(StoragefluidTank.getFluid().getLocalizedName());
                keyManager.add(FluidAmountString);
            } else {
                FluidString = syncer.syncString(I18n.format("gtsteam.machine.large_fluid_tank.empty"));
            }
            keyManager.add(KeyUtil.lang(TextFormatting.GRAY, "gtsteam.machine.large_fluid_tank.fluid_amount_text", FluidString));
            keyManager.add(KeyUtil.lang(TextFormatting.GRAY, "gtsteam.machine.large_fluid_tank.fluid_capacity_text", syncer.syncInt(this.capacity)));
        }
    }

    public IBlockState getULVCasingState() {
        return blockMultiblockCasing0.getState(TANK_WALL);
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
    }

    @Override
    public void checkStructurePattern() {
        if (!this.isStructureFormed()) {
            reinitializeStructurePattern();
        }
        super.checkStructurePattern();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.capacity = compound.getInteger("FluidCapacity");
        this.Length = compound.getInteger("Length");
        this.Height = compound.getInteger("Height");
        this.Width = compound.getInteger("Width");
        String FluidType = compound.getString("FluidType");
        reinitializeStructurePattern();
        this.StoragefluidTank = new FluidTank(this.capacity);
        int StorageFluidTankAmount = compound.getInteger("StorageFluidTankAmount");
        if (StorageFluidTankAmount > 0) {
            this.StoragefluidTank.fill(FluidRegistry.getFluidStack(FluidType, StorageFluidTankAmount), true);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("FluidCapacity", this.capacity);
        data.setInteger("Length", this.Length);
        data.setInteger("Height", this.Height);
        data.setInteger("Width", this.Width);
        data.setInteger("StorageFluidTankAmount", this.StoragefluidTank.getFluidAmount());
        if (this.StoragefluidTank.getFluid() != null) {
            data.setString("FluidType", FluidRegistry.getFluidName(this.StoragefluidTank.getFluid()));
        } else {
            data.setString("FluidType", "Empty");
        }
        //this.StoragefluidTank.writeToNBT(data);
        return data;
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        if (getWorld() != null) updateStructureDimensions();
        var pattern = FactoryBlockPattern.start();
        if (Length % 2 == 0) {
            Width = 3;
            Height = 3;
            Length = 3;
        }
        if (Width < 3 || Height < 3 || Length < 3) {
            Width = 3;
            Height = 3;
            Length = 3;
        }
        if (Width > 15 || Height > 15 || Length > 15) {
            Width = 3;
            Height = 3;

        }
        for (int i = 1; i <= Width; i++) {
            String[] PatternStringLayer = new String[Height];
            for (int j = 1; j <= Height; j++) {
                StringBuilder str = new StringBuilder();
                if (i == 1 || i == Width) {
                    if (j == 1 || j == Height) {
                        if (i == Width && j == 1) {
                            str.append(repeat("A", Length / 2)).append("#").append(repeat("A", Length / 2));
                        } else {
                            str.append(repeat("A", Length));
                        }
                    } else {
                        str.append(repeat("A", 1)).append(repeat("B", Length - 2)).append(repeat("A", 1));
                    }
                } else {
                    if (j == 1 || j == Height) {
                        str.append(repeat("A", 1)).append(repeat("B", Length - 2)).append(repeat("A", 1));
                    } else {
                        str.append(repeat("B", 1)).append(repeat("C", Length - 2)).append(repeat("B", 1));
                    }
                }
                PatternStringLayer[j - 1] = str.toString();
            }
            pattern.aisle(PatternStringLayer);
        }
        return pattern
                .where('#', selfPredicate())
                .where('A', states(getULVCasingState())
                )
                .where('B', states(getULVCasingState())
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setExactLimit(1))
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setExactLimit(1))
                        .or(states(Blocks.GLASS.getDefaultState())))
                .where('C', air())
                .build();
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        List<MultiblockShapeInfo> shapeInfo = new ArrayList<>();

        MultiblockShapeInfo.Builder builder = MultiblockShapeInfo.builder();
        builder.aisle("CCC", "CNC", "CCC");
        builder.aisle("CMC", "G G", "CGC");
        builder.aisle("CSC", "CGC", "CCC");

        // 设置方块映射
        builder
                .where('S', GTSteamMetaTileEntities.LARGE_FLUID_TANK, SOUTH)
                .where('M', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.ULV], DOWN)
                .where('N', MetaTileEntities.FLUID_EXPORT_HATCH[GTValues.ULV], NORTH)
                .where('C', getULVCasingState())
                .where('G', Blocks.GLASS.getDefaultState())
                .where(' ', Blocks.AIR.getDefaultState());

        shapeInfo.add(builder.build());

        builder = MultiblockShapeInfo.builder();
        builder.aisle("CCCCC", "CGGGC", "CGGGC", "CGGGC", "CCCCC");
        builder.aisle("CCCCC", "G   G", "G   G", "G   G", "CCCCC");
        builder.aisle("CCCCC", "G   G", "G   G", "G   G", "CCCCC");
        builder.aisle("CCCCC", "G   G", "G   G", "G   G", "CCCCC");
        builder.aisle("CCSCC", "CMGNC", "CGGGC", "CGGGC", "CCCCC");

        // 设置方块映射
        builder
                .where('S', GTSteamMetaTileEntities.LARGE_FLUID_TANK, SOUTH)
                .where('M', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.ULV], SOUTH)
                .where('N', MetaTileEntities.FLUID_EXPORT_HATCH[GTValues.ULV], SOUTH)
                .where('C', getULVCasingState())
                .where('G', Blocks.GLASS.getDefaultState())
                .where(' ', Blocks.AIR.getDefaultState());

        shapeInfo.add(builder.build());

        builder = MultiblockShapeInfo.builder();
        builder.aisle("CCCCCCC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CCCCCCC");
        builder.aisle("CCCCCCC", "G     G", "G     G", "G     G", "G     G", "G     G", "CCCCCCC");
        builder.aisle("CCCCCCC", "G     G", "G     G", "G     G", "G     G", "G     G", "CCCCCCC");
        builder.aisle("CCCCCCC", "G     G", "G     G", "G     G", "G     G", "G     G", "CCCCCCC");
        builder.aisle("CCCCCCC", "G     G", "G     G", "G     G", "G     G", "G     G", "CCCCCCC");
        builder.aisle("CCCCCCC", "G     G", "G     G", "G     G", "G     G", "G     G", "CCCCCCC");
        builder.aisle("CCCSCCC", "CMGGGNC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CCCCCCC");

        // 设置方块映射
        builder
                .where('S', GTSteamMetaTileEntities.LARGE_FLUID_TANK, SOUTH)
                .where('M', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.ULV], SOUTH)
                .where('N', MetaTileEntities.FLUID_EXPORT_HATCH[GTValues.ULV], SOUTH)
                .where('C', getULVCasingState())
                .where('G', Blocks.GLASS.getDefaultState())
                .where(' ', Blocks.AIR.getDefaultState());

        shapeInfo.add(builder.build());

        // 7x7x7 结构
        builder = MultiblockShapeInfo.builder();
        builder.aisle("CCCCCCC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CCCCCCC");
        builder.aisle("CCCCCCC", "G     G", "G     G", "G     G", "G     G", "G     G", "CCCCCCC");
        builder.aisle("CCCCCCC", "G     G", "G     G", "G     G", "G     G", "G     G", "CCCCCCC");
        builder.aisle("CCCCCCC", "G     G", "G     G", "G     G", "G     G", "G     G", "CCCCCCC");
        builder.aisle("CCCCCCC", "G     G", "G     G", "G     G", "G     G", "G     G", "CCCCCCC");
        builder.aisle("CCCCCCC", "G     G", "G     G", "G     G", "G     G", "G     G", "CCCCCCC");
        builder.aisle("CCCSCCC", "CMGGGNC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CCCCCCC");
        builder
                .where('S', GTSteamMetaTileEntities.LARGE_FLUID_TANK, SOUTH)
                .where('M', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.ULV], SOUTH)
                .where('N', MetaTileEntities.FLUID_EXPORT_HATCH[GTValues.ULV], SOUTH)
                .where('C', getULVCasingState())
                .where('G', Blocks.GLASS.getDefaultState())
                .where(' ', Blocks.AIR.getDefaultState());
        shapeInfo.add(builder.build());

        // 9x9x9 结构
        builder = MultiblockShapeInfo.builder();
        builder.aisle("CCCCCCCCC", "CGGGGGGGC", "CGGGGGGGC", "CGGGGGGGC", "CGGGGGGGC", "CGGGGGGGC", "CGGGGGGGC", "CGGGGGGGC", "CCCCCCCCC");
        builder.aisle("CCCCCCCCC", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "CCCCCCCCC");
        builder.aisle("CCCCCCCCC", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "CCCCCCCCC");
        builder.aisle("CCCCCCCCC", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "CCCCCCCCC");
        builder.aisle("CCCCCCCCC", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "CCCCCCCCC");
        builder.aisle("CCCCCCCCC", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "CCCCCCCCC");
        builder.aisle("CCCCCCCCC", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "CCCCCCCCC");
        builder.aisle("CCCCCCCCC", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "G       G", "CCCCCCCCC");
        builder.aisle("CCCCSCCCC", "CMGGGGGNC", "CGGGGGGGC", "CGGGGGGGC", "CGGGGGGGC", "CGGGGGGGC", "CGGGGGGGC", "CGGGGGGGC", "CCCCCCCCC");
        builder
                .where('S', GTSteamMetaTileEntities.LARGE_FLUID_TANK, SOUTH)
                .where('M', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.ULV], SOUTH)
                .where('N', MetaTileEntities.FLUID_EXPORT_HATCH[GTValues.ULV], SOUTH)
                .where('C', getULVCasingState())
                .where('G', Blocks.GLASS.getDefaultState())
                .where(' ', Blocks.AIR.getDefaultState());
        shapeInfo.add(builder.build());

        // 11x11x11 结构
        builder = MultiblockShapeInfo.builder();
        builder.aisle("CCCCCCCCCCC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CCCCCCCCCCC");
        builder.aisle("CCCCCCCCCCC", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "CCCCCCCCCCC");
        builder.aisle("CCCCCCCCCCC", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "CCCCCCCCCCC");
        builder.aisle("CCCCCCCCCCC", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "CCCCCCCCCCC");
        builder.aisle("CCCCCCCCCCC", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "CCCCCCCCCCC");
        builder.aisle("CCCCCCCCCCC", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "CCCCCCCCCCC");
        builder.aisle("CCCCCCCCCCC", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "CCCCCCCCCCC");
        builder.aisle("CCCCCCCCCCC", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "CCCCCCCCCCC");
        builder.aisle("CCCCCCCCCCC", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "CCCCCCCCCCC");
        builder.aisle("CCCCCCCCCCC", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "G         G", "CCCCCCCCCCC");
        builder.aisle("CCCCCSCCCCC", "CMGGGGGGGNC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CGGGGGGGGGC", "CCCCCCCCCCC");
        builder
                .where('S', GTSteamMetaTileEntities.LARGE_FLUID_TANK, SOUTH)
                .where('M', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.ULV], SOUTH)
                .where('N', MetaTileEntities.FLUID_EXPORT_HATCH[GTValues.ULV], SOUTH)
                .where('C', getULVCasingState())
                .where('G', Blocks.GLASS.getDefaultState())
                .where(' ', Blocks.AIR.getDefaultState());
        shapeInfo.add(builder.build());

        return shapeInfo;
    }

    public boolean isBlockEdge(@NotNull World world, @NotNull BlockPos.MutableBlockPos pos,
                               @NotNull EnumFacing direction) {
        IBlockState block = world.getBlockState(pos.move(direction));
        TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof IGregTechTileEntity iGregTechTileEntity) {
            MetaTileEntity metaTileEntity = iGregTechTileEntity.getMetaTileEntity();
            if (metaTileEntity instanceof IMultiblockAbilityPart<?> iMultiblockAbilityPart) {
                return false;
            } else {
                return (block != getULVCasingState())
                        && (block != Blocks.GLASS.getDefaultState());
            }
        } else {
            return (block != getULVCasingState())
                    && (block != Blocks.GLASS.getDefaultState());
        }
    }

    private void updateStructureDimensions() {
        World world = getWorld();
        EnumFacing front = getFrontFacing();
        EnumFacing back = front.getOpposite();
        EnumFacing left = front.rotateYCCW();
        EnumFacing right = left.getOpposite();

        BlockPos.MutableBlockPos lPos = new BlockPos.MutableBlockPos(getPos());
        BlockPos.MutableBlockPos rPos = new BlockPos.MutableBlockPos(getPos());
        BlockPos.MutableBlockPos fPos = new BlockPos.MutableBlockPos(getPos());
        BlockPos.MutableBlockPos bPos = new BlockPos.MutableBlockPos(getPos());
        BlockPos.MutableBlockPos hPos = new BlockPos.MutableBlockPos(getPos());
        // 重置距离
        int lDist = 0;
        int rDist = 0;
        int hDist = 0;
        int MAX_RADIUS = 8;
        int bDist = 0;
        for (int i = 1; i <= MAX_RADIUS; i++) {
            if (lDist == 0 && isBlockEdge(world, lPos, left)) lDist = i;
            if (rDist == 0 && isBlockEdge(world, rPos, right)) rDist = i;
            if (lDist != 0 && rDist != 0) break;
        }
        this.Length = lDist + rDist - 1;
        for (int i = 1; i <= MAX_RADIUS * 2 - 1; i++) {
            if ((isBlockEdge(world, bPos, back))) bDist = i;
            if (bDist != 0) break;
        }
        for (int i = 1; i <= 15; i++) {
            if (isBlockEdge(world, hPos, EnumFacing.UP)) hDist = i;
            if (hDist != 0) break;
        }
        this.Length = lDist + rDist - 1;
        this.Width = bDist;
        this.Height = hDist;
    }

    private void initializeAbilities() {
        this.inputFluidsTank = this.getAbilities(MultiblockAbility.IMPORT_FLUIDS).get(0);
        this.outputFluidsTank = this.getAbilities(MultiblockAbility.EXPORT_FLUIDS).get(0);
        this.capacity = Height * Width * Length * 16000;
        if (this.StoragefluidTank == null || this.StoragefluidTank.getCapacity() == 0) {
            this.StoragefluidTank = new FluidTank(capacity);
        }
    }

    private void resetTileAbilities() {
        this.inputFluidsTank = new FluidTank(0);
        this.outputFluidsTank = new FluidTank(0);
        if (this.StoragefluidTank == null) {
            this.StoragefluidTank = new FluidTank(capacity);
        }
    }

    @Override
    public void update() {
        super.update();
        if (!this.getWorld().isRemote && this.getOffsetTimer() % 20L == 0L && this.isStructureFormed()) {
            int FluidAmounts = inputFluidsTank.getFluidAmount();
            FluidStack fluidStack = inputFluidsTank.getFluid();
            //inputFluidsTank.drain(FluidAmounts, true);
            int AcceptedFluidAmount = StoragefluidTank.fill(fluidStack, true);
            inputFluidsTank.drain(AcceptedFluidAmount, true);
            //outputFluidsTank.fill(fluidStack, true);
            int RemovedFluidAmount = outputFluidsTank.fill(StoragefluidTank.getFluid(), true);
            StoragefluidTank.drain(RemovedFluidAmount, true);
        }
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
    public void addInformation(ItemStack stack, World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gtsteam.machine.large_fluid_tank.tooltip.1"));
        tooltip.add(I18n.format("gtsteam.machine.large_fluid_tank.tooltip.2"));
        tooltip.add(I18n.format("gtsteam.machine.large_fluid_tank.tooltip.3"));
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

    @Override
    public int getProgressBarCount() {
        return 1;
    }

    @Override
    public void registerBars(List<UnaryOperator<TemplateBarBuilder>> bars, PanelSyncManager syncManager) {
        IntSyncValue fluidAmountValue = new IntSyncValue(() ->
                isStructureFormed() && StoragefluidTank != null ? StoragefluidTank.getFluidAmount() : 0);
        IntSyncValue fluidCapacityValue = new IntSyncValue(() ->
                isStructureFormed() ? capacity : 0);

        syncManager.syncValue("fluid_amount", fluidAmountValue);
        syncManager.syncValue("fluid_capacity", fluidCapacityValue);

        bars.add(barBuilder -> barBuilder
                .progress(() -> fluidCapacityValue.getIntValue() == 0 ? 0 :
                        (double) fluidAmountValue.getIntValue() / fluidCapacityValue.getIntValue())
                .texture(GTGuiTextures.PROGRESS_BAR_FLUID_RIG_DEPLETION) // 或者使用其他合适的进度条纹理
                .tooltipBuilder(tooltip -> {
                    if (isStructureFormed()) {
                        String fluidName = StoragefluidTank.getFluid() != null ?
                                StoragefluidTank.getFluid().getLocalizedName() :
                                I18n.format("gtsteam.machine.large_fluid_tank.empty");

                        tooltip.addLine(IKey.lang("gtsteam.machine.large_fluid_tank.fluid_type",
                                fluidName));
                        tooltip.addLine(IKey.lang("gtsteam.machine.large_fluid_tank.amount",
                                fluidAmountValue.getIntValue(), fluidCapacityValue.getIntValue()));
                    } else {
                        tooltip.addLine(IKey.lang("gregtech.multiblock.invalid_structure"));
                    }
                }));
    }
}
