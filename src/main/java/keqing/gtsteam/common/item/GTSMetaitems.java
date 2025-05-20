package keqing.gtsteam.common.item;

import gregtech.api.items.metaitem.MetaItem;

public class GTSMetaitems {
    //  Covers
    public static MetaItem<?>.MetaValueItem ELECTRIC_MOTOR_ULV;
    public static MetaItem<?>.MetaValueItem ELECTRIC_PISTON_ULV;
    public static MetaItem<?>.MetaValueItem ELECTRIC_PUMP_ULV;
    public static MetaItem<?>.MetaValueItem CONVEYOR_MODULE_ULV;
    public static MetaItem<?>.MetaValueItem ROBOT_ARM_ULV;
    public static MetaItem<?>.MetaValueItem EMITTER_ULV;
    public static MetaItem<?>.MetaValueItem SENSOR_ULV;
    public static MetaItem<?>.MetaValueItem FIELD_GENERATOR_ULV;

    public static MetaItem<?>.MetaValueItem UNBURNED_CERAMIC_TILES;
    public static MetaItem<?>.MetaValueItem BURNED_CERAMIC_TILES;
    public static MetaItem<?>.MetaValueItem GALVANIZED_CERAMIC_TILE;

    public static GTSMetaitem GTS_META_ITEM;
    public static void initialization() {
        GTS_META_ITEM = new GTSMetaitem();

    }
    public static void initSubItems()
    {
        GTSMetaitem.registerItems();
    }

}
