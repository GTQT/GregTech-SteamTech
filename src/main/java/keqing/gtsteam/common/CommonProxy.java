package keqing.gtsteam.common;

import gregtech.api.GregTechAPI;
import gregtech.api.block.VariantItemBlock;
import gregtech.api.cover.CoverDefinition;
import keqing.gtsteam.GTSteam;
import keqing.gtsteam.common.block.GTSteamMetaBlocks;
import keqing.gtsteam.common.covers.GTSCoverBehavior;
import keqing.gtsteam.loader.recipes.GTSRecipeManger;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;
import java.util.function.Function;

@Mod.EventBusSubscriber(
        modid = "gtsteam"
)
public class CommonProxy {
    public void init() {
        GTSRecipeManger.init();
    }
    @SubscribeEvent
    public static void registerCoverBehavior(GregTechAPI.RegisterEvent<CoverDefinition> event) {
        GTSCoverBehavior.init();
    }
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(GTSteamMetaBlocks.blockMultiblockCasing0);
    }
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(createItemBlock(GTSteamMetaBlocks.blockMultiblockCasing0, VariantItemBlock::new));
    }
    private static <T extends Block> ItemBlock createItemBlock(T block, Function<T, ItemBlock> producer) {
        ItemBlock itemBlock = producer.apply(block);
        itemBlock.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
        return itemBlock;
    }

    public void preLoad() {
    }
}