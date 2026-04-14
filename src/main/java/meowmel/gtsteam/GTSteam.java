package meowmel.gtsteam;

import meowmel.gtsteam.client.ClientProxy;
import meowmel.gtsteam.common.CommonProxy;
import meowmel.gtsteam.common.block.GTSteamMetaBlocks;
import meowmel.gtsteam.common.item.GTSMetaitems;
import meowmel.gtsteam.common.metatileentities.GTSteamMetaTileEntities;
import meowmel.gtsteam.integration.theoneprobe.GTSteamIntegration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
        modid = "gtsteam",
        name = "GTSteam",
        acceptedMinecraftVersions = "[1.12.2,1.13)",
        version = "0.0.1-beta",
        dependencies = "required-after:gregtech@[1.9.0,);"
)
public class GTSteam {

    public static final String MODID = "gtsteam";
    public static final String NAME = "GTSteam";
    public static final String VERSION = "0.1.0";

    @Mod.Instance(GTSteam.MODID)
    public static GTSteam instance;

    @SidedProxy(
            clientSide = "meowmel.gtsteam.client.ClientProxy",
            serverSide = "meowmel.gtsteam.common.CommonProxy"
    )
    public static CommonProxy proxy;
    public static ClientProxy cproxy;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GTSMetaitems.initialization();
        GTSteamMetaBlocks.init();

        GTSteamMetaTileEntities.initialization();

        proxy.preLoad();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
        GTSteamIntegration.init();
    }
}

