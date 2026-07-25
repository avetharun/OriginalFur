package dev.feintha.originfurs.fur;

import net.minecraft.util.StringIdentifiable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum FurPartTypes implements StringIdentifiable {
    LEFT_ARM("leftArm"), RIGHT_ARM("rightArm"),
    LEFT_LEG("leftLeg"), RIGHT_LEG("rightLeg"),
    BODY("body"), HEAD("jacket"), HAT("hat"),
    RIGHT_SLEEVE("rightSleeve"), LEFT_SLEEVE("leftSleeve"),
    LEFT_PANTS("leftPants"), RIGHT_PANTS("rightPants"),;
    final String name;
    FurPartTypes(String name) {
        this.name = name;
    }
    @Override
    public String asString() {
        return name();
    }
    public static final com.mojang.serialization.Codec<FurPartTypes> CODEC = StringIdentifiable.createCodec(FurPartTypes::values);
    public static final com.mojang.serialization.Codec<EnumSet<FurPartTypes>> SET_CODEC = CODEC.listOf().xmap(
            list -> {
                EnumSet<FurPartTypes> set = EnumSet.noneOf(FurPartTypes.class);
                set.addAll(list);
                return set;
            },
            List::copyOf
    );
}
