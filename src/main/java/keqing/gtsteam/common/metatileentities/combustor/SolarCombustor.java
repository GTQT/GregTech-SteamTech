package keqing.gtsteam.common.metatileentities.combustor;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import gregtech.api.capability.impl.HeatContainerHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.unification.material.Material;
import gregtech.api.util.GTUtility;
import keqing.gtsteam.client.textures.GTSteamTextures;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SolarCombustor extends Combustor {

    public SolarCombustor(ResourceLocation metaTileEntityId, boolean isHighPressure, int tier, Material material) {
        super(metaTileEntityId, isHighPressure, GTSteamTextures.HU_BASE_BURRING_BOX_SOLAR, tier, material);
        ((HeatContainerHandler) this.heatable).setSideOutputCondition(s -> s == getFrontFacing().getOpposite());
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new SolarCombustor(metaTileEntityId, isHighPressure, tier, material);
    }

    @Override
    protected int getBaseHeatOutput() {
        return isHighPressure ? 160 : 80;
    }

    @Override
    protected void tryConsumeNewFuel() {
        if (GTUtility.canSeeSunClearly(getWorld(), getPos())) {
            setFuelMaxBurnTime(20);
        }
    }

    @Override
    protected int getCooldownInterval() {
        return isHighPressure ? 50 : 45;
    }

    @Override
    protected int getCoolDownRate() {
        return 3;
    }

    @Override
    public ModularPanel buildUI(PosGuiData guiData, PanelSyncManager guiSyncManager, UISettings settings) {
        return super.buildUI(guiData, guiSyncManager, settings)
                .child(new ProgressWidget()
                        .value(new DoubleSyncValue(() -> GTUtility.canSeeSunClearly(getWorld(), getPos()) ? 1.0 : 0.0))
                        .pos(114, 44)
                        .size(20)
                        .texture(isHighPressure ?
                                GTGuiTextures.PROGRESS_BAR_SOLAR_STEEL :
                                GTGuiTextures.PROGRESS_BAR_SOLAR_BRONZE, -1));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick() {
        // Solar boilers do not display particles
    }
}
