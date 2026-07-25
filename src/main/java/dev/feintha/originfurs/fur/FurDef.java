package dev.feintha.originfurs.fur;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.EnumSet;
import java.util.Optional;

public record FurDef(Optional<Identifier> model, Optional<Identifier> texture,
                     Optional<Identifier> fullbrightTexture, Optional<Identifier> animation,
                     Optional<Identifier> elytraTexture, boolean playerInvisible,
                     Optional<Identifier> overlay, Optional<Identifier> emissive_overlay,
                     EnumSet<FurPartTypes> hiddenParts, FurOffsets offsets) {

    public FurDef{
    }
    public static Codec<FurDef> CODEC = RecordCodecBuilder.create(i -> i.group(
            Identifier.CODEC.optionalFieldOf("model").forGetter(FurDef::model),
            Identifier.CODEC.optionalFieldOf("texture").forGetter(FurDef::texture),
            Identifier.CODEC.optionalFieldOf("fullbrightTexture").forGetter(FurDef::fullbrightTexture),
            Identifier.CODEC.optionalFieldOf("animation").forGetter(FurDef::animation),
            Identifier.CODEC.optionalFieldOf("elytraTexture").forGetter(FurDef::elytraTexture),
            Codec.BOOL.optionalFieldOf("playerInvisible", false).forGetter(FurDef::playerInvisible),
            Identifier.CODEC.optionalFieldOf("overlay").forGetter(FurDef::overlay),
            Identifier.CODEC.optionalFieldOf("emissive_overlay").forGetter(FurDef::emissive_overlay),
            FurPartTypes.SET_CODEC.optionalFieldOf("hidden", EnumSet.noneOf(FurPartTypes.class)).forGetter(FurDef::hiddenParts),
            FurOffsets.CODEC.optionalFieldOf("offsets", FurOffsets.NONE).forGetter(FurDef::offsets)
    ).apply(i, FurDef::new));
    @Override
    public String toString() {
        return "FurDef{" +
                "model=" + model +
                ", texture=" + texture +
                ", fullbrightTexture=" + fullbrightTexture +
                ", animation=" + animation +
                ", elytraTexture=" + elytraTexture +
                ", playerInvisible=" + playerInvisible +
                ", overlay=" + overlay +
                ", emissive_overlay=" + emissive_overlay +
                ", hiddenParts=" + hiddenParts +
                ", offsets=" + offsets +
                '}';
    }
}
