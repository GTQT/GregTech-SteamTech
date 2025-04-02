package keqing.gtsteam.common;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import gregtech.api.GregTechAPI;
import gregtech.api.metatileentity.registry.MTEManager;
import static keqing.gtsteam.GTSteam.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class EventHandlers {

    @SubscribeEvent
    public static void registerMTERegistry(MTEManager.MTERegistryEvent event) {
        GregTechAPI.mteManager.createRegistry(MODID);
    }
}
