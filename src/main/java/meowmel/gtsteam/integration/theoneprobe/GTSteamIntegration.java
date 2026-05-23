package meowmel.gtsteam.integration.theoneprobe;

import mcjty.theoneprobe.api.ITheOneProbe;
import meowmel.gtsteam.integration.theoneprobe.provider.CombustorInfoProvider;
import meowmel.gtsteam.integration.theoneprobe.provider.LargeFluidTankProvider;


public class GTSteamIntegration {

    public GTSteamIntegration() {
    }

    public static void init() {

        ITheOneProbe oneProbe = mcjty.theoneprobe.TheOneProbe.theOneProbeImp;
        oneProbe.registerProvider(new LargeFluidTankProvider());
        oneProbe.registerProvider(new CombustorInfoProvider());
    }
}
