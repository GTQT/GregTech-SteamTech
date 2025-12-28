package keqing.gtsteam.client.textures;

import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.cube.SidedCubeRenderer;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.client.renderer.texture.custom.FireboxActiveRenderer;

public class GTSteamTextures {
    public static final SimpleOverlayRenderer PORCELAIN_TILES = new SimpleOverlayRenderer("casings/galvanized_porcelain_tiles");
    public static final SimpleOverlayRenderer TANK_WALL = new SimpleOverlayRenderer("casings/tank_wall");

    public static final SimpleOverlayRenderer LOW_PRESSURE_SIDE = new SimpleOverlayRenderer("casings/boiler_tank_pressure_low_side");
    public static final SimpleOverlayRenderer HIGH_PRESSURE_SIDE = new SimpleOverlayRenderer("casings/boiler_tank_pressure_high_side");

    public static final ICubeRenderer ITEM_FIREBOX_FRONT = new SidedCubeRenderer(
            "firebox/item_firebox");
    public static final ICubeRenderer ITEM_FIREBOX_FRONT_ACTIVE = new FireboxActiveRenderer(
            "firebox/item_firebox/active");
    public static final ICubeRenderer FLUID_FIREBOX_FRONT = new SidedCubeRenderer(
            "firebox/fluid_firebox");
    public static final ICubeRenderer FLUID_FIREBOX_FRONT_ACTIVE = new FireboxActiveRenderer(
            "firebox/fluid_firebox/active");

    public static void init() {

    }
}
