package keqing.gtsteam.integration.theoneprobe.provider;

import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import keqing.gtsteam.GTSteam;
import keqing.gtsteam.common.metatileentities.multi.store.MetaTileEntityLargeFluidTank;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;

public class LargeFluidTankProvider implements IProbeInfoProvider {
    @Override
    public String getID() {
        return GTSteam.MODID + ":large_fluid_tank";
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState state,
                             IProbeHitData data) {
        if (state.getBlock().hasTileEntity(state)) {
            TileEntity te = world.getTileEntity(data.getPos());
            if (te instanceof IGregTechTileEntity igtte) {
                MetaTileEntity mte = igtte.getMetaTileEntity();
                if (mte instanceof MetaTileEntityLargeFluidTank fluidTank) {

                    IFluidTank tank = fluidTank.getStorageFluidTank();
                    int capacity = tank.getCapacity();
                    int amount = tank.getFluidAmount();
                    String fluidType = tank.getFluid() == null ? "" : tank.getFluid().getLocalizedName();

                    int color = amount>capacity*0.75 ? 0xFF4CBB17 : 0xFFBB1C28;
                    probeInfo.progress(amount, capacity, probeInfo.defaultProgressStyle()
                            .suffix(" /"+ capacity+ " mb "+ fluidType)
                            .filledColor(color)
                            .alternateFilledColor(color)
                            .borderColor(0xFF555555).numberFormat(NumberFormat.COMMAS));
                }
            }
        }
    }
}
