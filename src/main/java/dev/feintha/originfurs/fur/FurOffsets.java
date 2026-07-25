package dev.feintha.originfurs.fur;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.Vec3d;

public record FurOffsets(Vec3d left, Vec3d right) {
    public static final Codec<FurOffsets> CODEC = RecordCodecBuilder.create(i -> i.group(
            Vec3d.CODEC.optionalFieldOf("left", Vec3d.ZERO).forGetter(FurOffsets::left),
            Vec3d.CODEC.optionalFieldOf("right", Vec3d.ZERO).forGetter(FurOffsets::right)
    ).apply(i, FurOffsets::new));
    public static final FurOffsets NONE = new FurOffsets(Vec3d.ZERO, Vec3d.ZERO);
}
