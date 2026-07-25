package dev.feintha.originfurs.fur;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;

public record FurOffsets(int priority, Vec3d left, Vec3d right, Vec3d elytra, Vec3d cape, Vec3d left_elytra, Vec3d right_elytra) {
    public static final Codec<FurOffsets> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.optionalFieldOf("priority", 0).forGetter(FurOffsets::priority),
            Vec3d.CODEC.optionalFieldOf("left", Vec3d.ZERO).forGetter(FurOffsets::left),
            Vec3d.CODEC.optionalFieldOf("right", Vec3d.ZERO).forGetter(FurOffsets::right),
            Vec3d.CODEC.optionalFieldOf("elytra", Vec3d.ZERO).forGetter(FurOffsets::elytra),
            Vec3d.CODEC.optionalFieldOf("cape", Vec3d.ZERO).forGetter(FurOffsets::cape),
            Vec3d.CODEC.optionalFieldOf("left_elytra", Vec3d.ZERO).forGetter(FurOffsets::left_elytra),
            Vec3d.CODEC.optionalFieldOf("right_elytra", Vec3d.ZERO).forGetter(FurOffsets::right_elytra)
    ).apply(i, FurOffsets::new));
    public static final FurOffsets NONE = new FurOffsets(-32767, Vec3d.ZERO, Vec3d.ZERO, Vec3d.ZERO, Vec3d.ZERO, Vec3d.ZERO, Vec3d.ZERO);
    public static FurOffsets pickHighest(List<FurOffsets> list) {
        return list.stream().max((a, b) -> Integer.compare(b.priority(), a.priority())).orElse(NONE);
    }
}
