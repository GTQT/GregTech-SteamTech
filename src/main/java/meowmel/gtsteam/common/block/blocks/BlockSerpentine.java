package meowmel.gtsteam.common.block.blocks;

import javax.annotation.Nonnull;

import gregtech.client.model.ActiveVariantBlockBakedModel;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import gregtech.api.block.VariantActiveBlock;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class BlockSerpentine extends VariantActiveBlock<BlockSerpentine.SerpentineType> {

    public BlockSerpentine() {
        super(Material.IRON);
        setTranslationKey("serpentine");
        setHardness(0.5f);
        setResistance(0.5f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(SerpentineType.BASIC));
    }

    @Override
    public boolean canCreatureSpawn(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    EntityLiving.@NotNull SpawnPlacementType type) {
        return false;
    }

    @Override
    public boolean isBloomEnabled(BlockSerpentine.SerpentineType value) {
        return false;
    }

    public enum SerpentineType implements IStringSerializable {

        BASIC("basic");

        public final String name;

        SerpentineType(String name) {
            this.name = name;
        }

        @Nonnull
        @Override
        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.getName();
        }

    }
}