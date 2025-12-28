package keqing.gtsteam.common.block;

import gregtech.common.blocks.MetaBlocks;
import keqing.gtsteam.common.block.blocks.BlockFireboxCasing0;
import keqing.gtsteam.common.block.blocks.BlockMultiblockCasing0;
import keqing.gtsteam.common.block.blocks.BlockMultiblockCasing1;
import keqing.gtsteam.common.item.storageupdate.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GTSteamMetaBlocks {
    public static BlockMultiblockCasing0 blockMultiblockCasing0;
    public static BlockMultiblockCasing1 blockMultiblockCasing1;
    public static BlockFireboxCasing0 blockFireboxCasing0;

    private GTSteamMetaBlocks() {
    }

    public static void init() {
        blockMultiblockCasing0 = new BlockMultiblockCasing0();
        blockMultiblockCasing0.setRegistryName("multiblock_casing0");

        blockMultiblockCasing1 = new BlockMultiblockCasing1();
        blockMultiblockCasing1.setRegistryName("multiblock_casing1");

        blockFireboxCasing0 = new BlockFireboxCasing0();
        blockFireboxCasing0.setRegistryName("firebox_casing0");


    }

    @SideOnly(Side.CLIENT)
    public static void registerItemModels() {
        registerItemModel(blockMultiblockCasing0);
        registerItemModel(blockMultiblockCasing1);

        blockFireboxCasing0.onModelRegister();

        registerModel(ModItems.STORAGE_UPGRADE_TIER_1);
        registerModel(ModItems.STORAGE_UPGRADE_TIER_2);
        registerModel(ModItems.STORAGE_UPGRADE_TIER_3);
        registerModel(ModItems.VOID_UPGRADE);
    }

    private static void registerModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0,
                new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemModel(Block block) {

        for (IBlockState state : block.getBlockState().getValidStates()) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),
                    block.getMetaFromState(state),
                    new ModelResourceLocation(block.getRegistryName(),
                            MetaBlocks.statePropertiesToString(state.getProperties())));
        }

    }
}
