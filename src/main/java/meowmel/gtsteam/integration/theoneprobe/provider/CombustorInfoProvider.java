package meowmel.gtsteam.integration.theoneprobe.provider;

import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.TextFormattingUtil;
import mcjty.theoneprobe.api.*;
import meowmel.gtsteam.common.metatileentities.combustor.Combustor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class CombustorInfoProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return GTValues.MODID + ":combustor_provider";
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState state,
                             IProbeHitData data) {
        if (state.getBlock().hasTileEntity(state)) {
            TileEntity te = world.getTileEntity(data.getPos());
            if (te instanceof IGregTechTileEntity igtte) {
                MetaTileEntity mte = igtte.getMetaTileEntity();
                if (mte instanceof Combustor combustor) {
                    int steamOutput = combustor.getTotalHeatOutput();
                    // If we are producing steam, or we have fuel
                    if (steamOutput > 0 || combustor.isBurning()) {
                        // Creating steam
                        if (steamOutput > 0) {
                            probeInfo.text(TextStyleClass.INFO + "{*gregtech.top.energy_production*} " +
                                    TextFormatting.AQUA + TextFormattingUtil.formatNumbers(steamOutput / 10) +
                                    TextStyleClass.INFO + " L/t" + " {*" +
                                    Materials.Steam.getUnlocalizedName() + "*}");
                        }

                        // Cooling Down
                        if (!combustor.isBurning()) {
                            probeInfo.text(TextStyleClass.INFO.toString() + TextFormatting.RED +
                                    "{*gregtech.top.steam_cooling_down*}");
                        }

                        // Initial heat-up
                        if (steamOutput <= 0 && combustor.getCurrentTemperature() > 0) {
                            // Current Temperature = the % until the boiler reaches 100
                            probeInfo.text(TextStyleClass.INFO.toString() + TextFormatting.RED +
                                    "{*gregtech.top.steam_heating_up*} " +
                                    TextFormattingUtil.formatNumbers(combustor.getCurrentTemperature()) + "%");
                        }

                        if (combustor.isBurning()) {
                            int color = combustor.isBurning() ? 0xFF4CBB17 : 0xFFBB1C28;
                            probeInfo.progress((int) (combustor.getFuelLeftPercent() * 100.0), 100, probeInfo.defaultProgressStyle()
                                    .suffix(" % Fuel remains")
                                    .filledColor(color)
                                    .alternateFilledColor(color)
                                    .borderColor(0xFF555555).numberFormat(NumberFormat.COMMAS));
                        }
                    }
                }
            }
        }
    }
}
