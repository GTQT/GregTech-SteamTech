package keqing.gtsteam.integration.theoneprobe;

import keqing.gtsteam.integration.theoneprobe.provider.LargeFluidTankProvider;
import mcjty.theoneprobe.api.ITheOneProbe;


public class GTSteamIntegration {
    public static void init() {

        ITheOneProbe oneProbe = mcjty.theoneprobe.TheOneProbe.theOneProbeImp;
        oneProbe.registerProvider(new LargeFluidTankProvider());
    }


    public GTSteamIntegration() {}
}
