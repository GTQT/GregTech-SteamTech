package meowmel.gtsteam.common.metatileentities.multi.generator;

import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import net.minecraft.block.state.IBlockState;

import static gregtech.common.blocks.BlockBoilerCasing.BoilerCasingType.*;
import static gregtech.common.blocks.BlockBoilerCasing.BoilerCasingType.TUNGSTENSTEEL_PIPE;
import static gregtech.common.blocks.BlockFireboxCasing.FireboxCasingType.*;
import static gregtech.common.blocks.BlockFireboxCasing.FireboxCasingType.TUNGSTENSTEEL_FIREBOX;
import static gregtech.common.blocks.BlockMetalCasing.MetalCasingType.*;
import static gregtech.common.blocks.BlockMetalCasing.MetalCasingType.TUNGSTENSTEEL_ROBUST;
import static gregtech.common.blocks.MetaBlocks.*;
import static gregtech.common.blocks.MetaBlocks.BOILER_CASING;

public enum CombustorType {

    BRONZE("bronze",1200, 1073,
            METAL_CASING.getState(BRONZE_BRICKS),
            BOILER_FIREBOX_CASING.getState(BRONZE_FIREBOX),
            BOILER_CASING.getState(BRONZE_PIPE),
            Textures.BRONZE_PLATED_BRICKS
    ),

    STEEL("steel",1800, 1273,
            METAL_CASING.getState(STEEL_SOLID),
            BOILER_FIREBOX_CASING.getState(STEEL_FIREBOX),
            BOILER_CASING.getState(STEEL_PIPE),
            Textures.SOLID_STEEL_CASING
    ),

    TITANIUM("titanium",3600, 1473,
            METAL_CASING.getState(TITANIUM_STABLE),
            BOILER_FIREBOX_CASING.getState(TITANIUM_FIREBOX),
            BOILER_CASING.getState(TITANIUM_PIPE),
            Textures.STABLE_TITANIUM_CASING
    ),

    TUNGSTENSTEEL("tungstensteel",7200, 1673,
            METAL_CASING.getState(TUNGSTENSTEEL_ROBUST),
            BOILER_FIREBOX_CASING.getState(TUNGSTENSTEEL_FIREBOX),
            BOILER_CASING.getState(TUNGSTENSTEEL_PIPE),
            Textures.ROBUST_TUNGSTENSTEEL_CASING
    );

    //
    public final String name;
    // Structure Data
    public final IBlockState casingState;
    public final IBlockState fireboxState;
    public final IBlockState pipeState;

    // Rendering Data
    public final ICubeRenderer casingRenderer;
    // Workable Data
    private final int heatPerTick;
    private final int maxTemp;

    CombustorType(String name,int heatPerTick, int maxTemp,
                  IBlockState casingState,
                  IBlockState fireboxState,
                  IBlockState pipeState,
                  ICubeRenderer casingRenderer) {
        this.name = name;

        this.heatPerTick = heatPerTick;
        this.maxTemp = maxTemp;

        this.casingState = casingState;
        this.fireboxState = fireboxState;
        this.pipeState = pipeState;

        this.casingRenderer = casingRenderer;
    }

    public String getName() {return name;}

    public int heatPerTick() {
        return heatPerTick;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public int runtimeBoost(int ticks) {
        switch (this) {
            case BRONZE:
                return ticks * 2;
            case STEEL:
                return ticks * 150 / 100;
            case TITANIUM:
                return ticks * 120 / 100;
            case TUNGSTENSTEEL:
                return ticks;
        }
        return 0;
    }
}
