package meowmel.gtsteam.common.metatileentities.multi.generator;

import gregtech.client.renderer.ICubeRenderer;
import meowmel.gtsteam.client.textures.GTSteamTextures;
import net.minecraft.block.state.IBlockState;

import static meowmel.gtsteam.common.block.GTSteamMetaBlocks.blockFireboxCasing0;
import static meowmel.gtsteam.common.block.GTSteamMetaBlocks.blockMultiblockCasing1;
import static meowmel.gtsteam.common.block.blocks.BlockFireboxCasing0.FireboxCasingType.FLUID_FIREBOX;
import static meowmel.gtsteam.common.block.blocks.BlockFireboxCasing0.FireboxCasingType.ITEM_FIREBOX;
import static meowmel.gtsteam.common.block.blocks.BlockMultiblockCasing1.CasingType.HIGH_PRESSURE_TANK;
import static meowmel.gtsteam.common.block.blocks.BlockMultiblockCasing1.CasingType.LOW_PRESSURE_TANK;

public enum PrimitiveBoilerType {

    //低压固体
    LOW_PRESSURE_SOLID(1200, 473, false,
            blockMultiblockCasing1.getState(LOW_PRESSURE_TANK),
            blockFireboxCasing0.getState(ITEM_FIREBOX),
            GTSteamTextures.LOW_PRESSURE_SIDE,
            GTSteamTextures.ITEM_FIREBOX_FRONT,
            GTSteamTextures.ITEM_FIREBOX_FRONT_ACTIVE),

    //低压液体
    LOW_PRESSURE_FLUID(1200, 473, false,
            blockMultiblockCasing1.getState(LOW_PRESSURE_TANK),
            blockFireboxCasing0.getState(FLUID_FIREBOX),
            GTSteamTextures.LOW_PRESSURE_SIDE,
            GTSteamTextures.FLUID_FIREBOX_FRONT,
            GTSteamTextures.FLUID_FIREBOX_FRONT_ACTIVE),

    //高压固体
    HIGH_PRESSURE_SOLID(2400, 873, true,
            blockMultiblockCasing1.getState(HIGH_PRESSURE_TANK),
            blockFireboxCasing0.getState(ITEM_FIREBOX),
            GTSteamTextures.HIGH_PRESSURE_SIDE,
            GTSteamTextures.ITEM_FIREBOX_FRONT,
            GTSteamTextures.ITEM_FIREBOX_FRONT_ACTIVE),

    //高压液体
    HIGH_PRESSURE_FLUID(2400, 873, true,
            blockMultiblockCasing1.getState(HIGH_PRESSURE_TANK),
            blockFireboxCasing0.getState(FLUID_FIREBOX),
            GTSteamTextures.HIGH_PRESSURE_SIDE,
            GTSteamTextures.FLUID_FIREBOX_FRONT,
            GTSteamTextures.FLUID_FIREBOX_FRONT_ACTIVE);

    // Structure Data
    public final IBlockState casingState;
    public final IBlockState fireboxState;
    // Rendering Data
    public final ICubeRenderer casingRenderer;
    public final ICubeRenderer fireboxIdleRenderer;
    public final ICubeRenderer fireboxActiveRenderer;
    // Workable Data
    private final int heatPerTick;
    private final int maxTemp;
    private final boolean isHighPressure;

    PrimitiveBoilerType(int heatPerTick, int maxTemp, boolean isHighPressure,
                        IBlockState casingState,
                        IBlockState fireboxState,
                        ICubeRenderer casingRenderer,
                        ICubeRenderer fireboxIdleRenderer,
                        ICubeRenderer fireboxActiveRenderer) {

        this.heatPerTick = heatPerTick;
        this.maxTemp = maxTemp;

        this.isHighPressure = isHighPressure;

        this.casingState = casingState;
        this.fireboxState = fireboxState;

        this.casingRenderer = casingRenderer;
        this.fireboxIdleRenderer = fireboxIdleRenderer;
        this.fireboxActiveRenderer = fireboxActiveRenderer;
    }

    public int heatPerTick() {
        return heatPerTick;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public boolean isHighPressure() {
        return isHighPressure;
    }

    public int runtimeBoost(int ticks) {
        return switch (this) {
            case LOW_PRESSURE_SOLID, LOW_PRESSURE_FLUID -> ticks * 4;
            case HIGH_PRESSURE_SOLID, HIGH_PRESSURE_FLUID -> ticks * 3;
        };
    }
}
