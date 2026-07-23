package meowmel.gtsteam.common.item;

import gregtech.api.items.metaitem.StandardMetaItem;

public class GTSMetaItem extends StandardMetaItem {

    public GTSMetaItem() {
        this.setRegistryName("gts_meta_item_1");
    }

    public void registerSubItems() {
        //  Covers
        GTSMetaitems.ELECTRIC_MOTOR_ULV = this.addItem(0, "cover.electric_motor.ulv");
        GTSMetaitems.ELECTRIC_PISTON_ULV = this.addItem(1, "cover.electric_piston.ulv");
        GTSMetaitems.ELECTRIC_PUMP_ULV = this.addItem(2, "cover.electric_pump.ulv");
        GTSMetaitems.CONVEYOR_MODULE_ULV = this.addItem(3, "cover.conveyor_module.ulv");
        GTSMetaitems.ROBOT_ARM_ULV = this.addItem(4, "cover.robot_arm.ulv");
        GTSMetaitems.EMITTER_ULV = this.addItem(5, "cover.emitter.ulv");
        GTSMetaitems.SENSOR_ULV = this.addItem(6, "cover.sensor.ulv");
        GTSMetaitems.FIELD_GENERATOR_ULV = this.addItem(7, "cover.field_generator.ulv");

        //陶瓷线 20-
        //未烧制的陶瓷瓦
        GTSMetaitems.UNBURNED_CERAMIC_TILES = this.addItem(20, "unburned_ceramic_tiles");
        //已烧制陶瓷瓦
        GTSMetaitems.BURNED_CERAMIC_TILES = this.addItem(21, "burned_ceramic_tiles");
        //镀锌陶瓷瓦
        GTSMetaitems.GALVANIZED_CERAMIC_TILE = this.addItem(22, "galvanized_ceramic_tile");

        // ULV电路线
        // 25 真空管组件
        GTSMetaitems.VACUUM_TUBE_COMPONENTS = this.addItem(25, "component.vacuum_tube");

        //焦煤 30-
        // 30 仙人掌炭
        GTSMetaitems.CACTUS_CHARCOAL = this.addItem(30, "cactus_charcoal").setBurnValue(1800);
        // 31 仙人掌焦炭
        GTSMetaitems.CACTUS_COAL = this.addItem(31, "cactus_coal").setBurnValue(2400);
        // 32 糖炭
        GTSMetaitems.SUGAR_CHARCOAL = this.addItem(32, "sugar_charcoal").setBurnValue(1600);
        // 33 糖焦炭
        GTSMetaitems.SUGAR_COAL = this.addItem(33, "sugar_coal").setBurnValue(2000);
    }
}