package keqing.gtsteam.common.covers;

import gregtech.api.GTValues;
import gregtech.api.cover.CoverDefinition;
import gregtech.api.items.behavior.CoverItemBehavior;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.common.covers.CoverConveyor;
import gregtech.common.covers.CoverPump;
import gregtech.common.covers.CoverRoboticArm;
import keqing.gtsteam.GTSteam;
import keqing.gtsteam.common.item.GTSMetaitems;
import net.minecraft.util.ResourceLocation;

import static gregtech.common.covers.CoverBehaviors.registerCover;

public class GTSCoverBehavior {

    public static void init() {
        
        registerBehavior(new ResourceLocation(GTSteam.MODID, "pump.ulv"), GTSMetaitems.ELECTRIC_PUMP_ULV,
                (def, tile, side) -> new CoverPump(def, tile, side, GTValues.ULV, 320));
        registerBehavior(new ResourceLocation(GTSteam.MODID, "conveyor.ulv"), GTSMetaitems.CONVEYOR_MODULE_ULV,
                (def, tile, side) -> new CoverConveyor(def, tile, side, GTValues.ULV, 4));
        registerBehavior(new ResourceLocation(GTSteam.MODID, "robot_arm.ulv"), GTSMetaitems.ROBOT_ARM_ULV,
                (def, tile, side) -> new CoverRoboticArm(def, tile, side, GTValues.ULV, 4));

    }


    @SuppressWarnings("rawtypes")
    public static void registerBehavior(ResourceLocation coverId,
                                        MetaItem.MetaValueItem placerItem,
                                        CoverDefinition.CoverCreator behaviorCreator) {
        CoverDefinition coverDefinition = registerCover(coverId, placerItem.getStackForm(), behaviorCreator);
        placerItem.addComponents(new CoverItemBehavior(coverDefinition));
    }
}

