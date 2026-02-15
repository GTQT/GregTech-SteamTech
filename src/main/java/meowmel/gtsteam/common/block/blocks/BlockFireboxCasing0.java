package meowmel.gtsteam.common.block.blocks;

import gregtech.api.block.IStateHarvestLevel;
import gregtech.api.block.VariantActiveBlock;
import gregtech.api.items.toolitem.ToolClasses;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

public class BlockFireboxCasing0 extends VariantActiveBlock<BlockFireboxCasing0.FireboxCasingType> {

    public BlockFireboxCasing0() {
        super(Material.IRON);
        setTranslationKey("boiler_casing");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setDefaultState(getState(BlockFireboxCasing0.FireboxCasingType.ITEM_FIREBOX));
    }

    @Override
    public boolean canCreatureSpawn(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull EntityLiving.SpawnPlacementType type) {
        return false;
    }

    public enum FireboxCasingType implements IStringSerializable, IStateHarvestLevel {

        ITEM_FIREBOX("item_firebox", 1),
        FLUID_FIREBOX("fluid_firebox", 1);

        private final String name;
        private final int harvestLevel;

        FireboxCasingType(String name, int harvestLevel) {
            this.name = name;
            this.harvestLevel = harvestLevel;
        }

        @NotNull
        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public int getHarvestLevel(IBlockState state) {
            return harvestLevel;
        }

        @Override
        public String getHarvestTool(IBlockState state) {
            return ToolClasses.WRENCH;
        }
    }
}
