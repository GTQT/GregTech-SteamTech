package keqing.gtsteam.common.item;

import gregtech.api.GregTechAPI;
import gregtech.api.items.metaitem.StandardMetaItem;

public class GTSMetaitem extends StandardMetaItem {

    public GTSMetaitem() {
        this.setRegistryName("gts_meta_item_1");
    }


    public void registerSubItems() {
        //  Covers
        GTSMetaitems.ELECTRIC_MOTOR_ULV = this.addItem(1, "cover.electric_motor.ulv");
        GTSMetaitems.ELECTRIC_PISTON_ULV = this.addItem(2, "cover.electric_piston.ulv");
        GTSMetaitems.ELECTRIC_PUMP_ULV = this.addItem(3, "cover.electric_pump.ulv");
        GTSMetaitems.CONVEYOR_MODULE_ULV = this.addItem(4, "cover.conveyor_module.ulv");
        GTSMetaitems.ROBOT_ARM_ULV = this.addItem(5, "cover.robot_arm.ulv");
        GTSMetaitems.EMITTER_ULV = this.addItem(6, "cover.emitter.ulv");
        GTSMetaitems.SENSOR_ULV = this.addItem(7, "cover.sensor.ulv");
        GTSMetaitems.FIELD_GENERATOR_ULV = this.addItem(8, "cover.field_generator.ulv");

        //陶瓷线 10-
        //未烧制的陶瓷瓦
        GTSMetaitems.UNBURNED_CERAMIC_TILES = this.addItem(10, "unburned_ceramic_tiles");
        //已烧制陶瓷瓦
        GTSMetaitems.BURNED_CERAMIC_TILES = this.addItem(11, "burned_ceramic_tiles");
        //镀锌陶瓷瓦
        GTSMetaitems.GALVANIZED_CERAMIC_TILE = this.addItem(12, "galvanized_ceramic_tile");


    }
}