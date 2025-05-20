package keqing.gtsteam.client;

import keqing.gtsteam.client.textures.GTSteamTextures;
import keqing.gtsteam.common.CommonProxy;
import keqing.gtsteam.common.block.GTSteamMetaBlocks;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber({Side.CLIENT})
public class ClientProxy extends CommonProxy {
    public ClientProxy() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        GTSteamMetaBlocks.registerItemModels();
    }
    public void preLoad()
    {
        super.preLoad();
        GTSteamTextures.init();
    }
}