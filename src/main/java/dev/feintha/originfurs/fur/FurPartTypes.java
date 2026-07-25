package dev.feintha.originfurs.fur;

import net.minecraft.util.StringIdentifiable;

import java.util.EnumSet;
import java.util.List;

public enum FurPartTypes implements StringIdentifiable {
    leftArm("leftArm"), rightArm("rightArm"),
    leftLeg("leftLeg"), rightLeg("rightLeg"),
    body("body"), head("head"), hat("hat"), jacket("jacket"),
    rightSleeve("rightSleeve"), leftSleeve("leftSleeve"),
    leftPants("leftPants"), rightPants("rightPants"),;
    final String codecName;
    FurPartTypes(String codecName) {
        this.codecName = codecName;
    }
    @Override
    public String asString() {
        return codecName;
    }
    

    public static final com.mojang.serialization.Codec<FurPartTypes> CODEC = StringIdentifiable.createCodec(FurPartTypes::values);

    public static final com.mojang.serialization.Codec<EnumSet<FurPartTypes>> SET_CODEC = CODEC.listOf().xmap(a -> {
        return a.isEmpty() ? EnumSet.noneOf(FurPartTypes.class) : EnumSet.copyOf(a);
    }, List::copyOf);
}
