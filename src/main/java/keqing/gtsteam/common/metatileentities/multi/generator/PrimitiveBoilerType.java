package keqing.gtsteam.common.metatileentities.multi.generator;

import gregtech.client.renderer.ICubeRenderer;
import keqing.gtsteam.client.textures.GTSteamTextures;
import net.minecraft.block.state.IBlockState;

import static keqing.gtsteam.common.block.GTSteamMetaBlocks.blockFireboxCasing0;
import static keqing.gtsteam.common.block.GTSteamMetaBlocks.blockMultiblockCasing1;
import static keqing.gtsteam.common.block.blocks.BlockFireboxCasing0.FireboxCasingType.FLUID_FIREBOX;
import static keqing.gtsteam.common.block.blocks.BlockFireboxCasing0.FireboxCasingType.ITEM_FIREBOX;
import static keqing.gtsteam.common.block.blocks.BlockMultiblockCasing1.CasingType.HIGH_PRESSURE_TANK;
import static keqing.gtsteam.common.block.blocks.BlockMultiblockCasing1.CasingType.LOW_PRESSURE_TANK;

public enum PrimitiveBoilerType {

    //低压固体
    LOW_PRESSURE_SOLID(400, 600,
            blockMultiblockCasing1.getState(LOW_PRESSURE_TANK),
            blockFireboxCasing0.getState(ITEM_FIREBOX),
            GTSteamTextures.LOW_PRESSURE_SIDE,
            GTSteamTextures.ITEM_FIREBOX_FRONT,
            GTSteamTextures.ITEM_FIREBOX_FRONT_ACTIVE),

    //低压液体
    LOW_PRESSURE_FLUID(400, 600,
            blockMultiblockCasing1.getState(LOW_PRESSURE_TANK),
            blockFireboxCasing0.getState(FLUID_FIREBOX),
            GTSteamTextures.LOW_PRESSURE_SIDE,
            GTSteamTextures.FLUID_FIREBOX_FRONT,
            GTSteamTextures.FLUID_FIREBOX_FRONT_ACTIVE),

    //高压固体
    HIGH_PRESSURE_SOLID(600, 800,
            blockMultiblockCasing1.getState(HIGH_PRESSURE_TANK),
            blockFireboxCasing0.getState(ITEM_FIREBOX),
            GTSteamTextures.HIGH_PRESSURE_SIDE,
            GTSteamTextures.ITEM_FIREBOX_FRONT,
            GTSteamTextures.ITEM_FIREBOX_FRONT_ACTIVE),

    //高压液体
    HIGH_PRESSURE_FLUID(600, 800,
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
    private final int steamPerTick;
    private final int ticksToBoiling;

    PrimitiveBoilerType(int steamPerTick, int ticksToBoiling,
                        IBlockState casingState,
                        IBlockState fireboxState,
                        ICubeRenderer casingRenderer,
                        ICubeRenderer fireboxIdleRenderer,
                        ICubeRenderer fireboxActiveRenderer) {

        this.steamPerTick = steamPerTick;
        this.ticksToBoiling = ticksToBoiling;

        this.casingState = casingState;
        this.fireboxState = fireboxState;

        this.casingRenderer = casingRenderer;
        this.fireboxIdleRenderer = fireboxIdleRenderer;
        this.fireboxActiveRenderer = fireboxActiveRenderer;
    }

    public int steamPerTick() {
        return steamPerTick;
    }

    public int getTicksToBoiling() {
        return ticksToBoiling;
    }

    public int runtimeBoost(int ticks) {
        return switch (this) {
            case LOW_PRESSURE_SOLID, LOW_PRESSURE_FLUID -> ticks * 4;
            case HIGH_PRESSURE_SOLID, HIGH_PRESSURE_FLUID -> ticks * 3;
        };
    }
}
