package keqing.gtsteam.common.block;

import gregtech.common.blocks.MetaBlocks;
import keqing.gtsteam.common.block.blocks.BlockMultiblockCasing0;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GTSteamMetaBlocks {
    public static BlockMultiblockCasing0 blockMultiblockCasing0;
    private GTSteamMetaBlocks() {
    }

    public static void init() {
        blockMultiblockCasing0 = new BlockMultiblockCasing0();
        blockMultiblockCasing0.setRegistryName("multiblock_casing0");
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemModels() {
        registerItemModel(blockMultiblockCasing0);
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
