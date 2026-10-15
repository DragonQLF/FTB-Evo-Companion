package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.witchery;

import dev.sterner.witchery.feature.affliction.vampire.children.VampireChildrenHuntLevelAttachment;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = VampireChildrenHuntLevelAttachment.Data.class, remap = false)
public abstract class VampireHuntDataMutableMapMixin {
    @ModifyVariable(method = "<init>(Ljava/util/Map;)V", at = @At("HEAD"), argsOnly = true)
    private static Map<UUID, List<VampireChildrenHuntLevelAttachment.HuntData>> ftbevolutioncompanion$mutableData(
            Map<UUID, List<VampireChildrenHuntLevelAttachment.HuntData>> data) {
        return data instanceof LinkedHashMap ? data : new LinkedHashMap<>(data);
    }
}
