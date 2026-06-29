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
    public static final SimpleOverlayRenderer BRICK_CASING = new SimpleOverlayRenderer("casings/brick_casing");

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
    public static final OrientedOverlayRenderer RADIATOR_OVERLAY = new OrientedOverlayRenderer("machines/radiator");
    public static final OrientedOverlayRenderer SAP_COLLECTOR_OVERLAY = new OrientedOverlayRenderer("machines/sap_collector");

    public static final SimpleOverlayRenderer HU_BURRING_BOX_SIDE_OVERLAY = new SimpleOverlayRenderer("casings/hu_burring_box_side_overlay");
    public static final SimpleOverlayRenderer HU_BURRING_BOX_SIDE_FULL_OVERLAY = new SimpleOverlayRenderer("casings/hu_burring_box_side_full_overlay");


    public static final OrientedOverlayRenderer LARGE_ORE_WASHER_OVERLAY = new OrientedOverlayRenderer(
            "gtsteam_multi/large_ore_washer");
    public static final OrientedOverlayRenderer LARGE_SIFTER_OVERLAY = new OrientedOverlayRenderer(
            "gtsteam_multi/large_sifter");
    public static final OrientedOverlayRenderer LARGE_COMPRESSOR_OVERLAY = new OrientedOverlayRenderer(
            "gtsteam_multi/large_compressor");
    public static final OrientedOverlayRenderer INDUSTRIAL_COKE_OVEN_OVERLAY = new OrientedOverlayRenderer(
            "gtsteam_multi/industrial_coke_oven");
    public static final OrientedOverlayRenderer LARGE_BREWERY_OVERLAY = new OrientedOverlayRenderer(
            "gtsteam_multi/large_brewery");

    public static void init() {

    }
}
