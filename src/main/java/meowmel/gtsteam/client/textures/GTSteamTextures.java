package meowmel.gtsteam.client.textures;

import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.client.renderer.texture.cube.SidedCubeRenderer;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.client.renderer.texture.custom.FireboxActiveRenderer;

public class GTSteamTextures {
    public static final SimpleOverlayRenderer PORCELAIN_TILES = new SimpleOverlayRenderer("casings/galvanized_porcelain_tiles");
    public static final SimpleOverlayRenderer TANK_WALL = new SimpleOverlayRenderer("casings/tank_wall");
    public static final SimpleOverlayRenderer REINFORCED_TREATED_WOOD_WALL = new SimpleOverlayRenderer("casings/reinforced_treated_wood_wall");
    public static final SimpleOverlayRenderer SEALED_WOOD_WALL = new SimpleOverlayRenderer("casings/sealed_wood_wall");

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

    public static final OrientedOverlayRenderer HU_BASE_BURRING_BOX = new OrientedOverlayRenderer("machines/hu_base_burring_box");
    public static final OrientedOverlayRenderer HU_BASE_BURRING_BOX_LIQUID = new OrientedOverlayRenderer("machines/hu_base_burring_box_liquid");
    public static final OrientedOverlayRenderer HU_BASE_BURRING_BOX_SOLAR = new OrientedOverlayRenderer("machines/hu_base_burring_box_solar");

    public static final SimpleOverlayRenderer HU_BURRING_BOX_SIDE_OVERLAY = new SimpleOverlayRenderer("casings/hu_burring_box_side_overlay");
    public static final SimpleOverlayRenderer HU_BURRING_BOX_SIDE_FULL_OVERLAY = new SimpleOverlayRenderer("casings/hu_burring_box_side_full_overlay");

    public static void init() {

    }
}
