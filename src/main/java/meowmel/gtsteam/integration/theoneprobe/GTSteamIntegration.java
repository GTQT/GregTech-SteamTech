package meowmel.gtsteam.integration.theoneprobe;

import meowmel.gtsteam.integration.theoneprobe.provider.LargeFluidTankProvider;
import mcjty.theoneprobe.api.ITheOneProbe;


public class GTSteamIntegration {
    public GTSteamIntegration() {
    }

    public static void init() {

        ITheOneProbe oneProbe = mcjty.theoneprobe.TheOneProbe.theOneProbeImp;
        oneProbe.registerProvider(new LargeFluidTankProvider());
    }
}
