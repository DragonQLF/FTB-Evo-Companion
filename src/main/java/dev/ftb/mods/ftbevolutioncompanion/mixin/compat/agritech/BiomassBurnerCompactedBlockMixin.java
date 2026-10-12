package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.agritech;

import com.misterd.agritechevolved.blockentity.custom.BiomassBurnerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = BiomassBurnerBlockEntity.class, remap = false)
public abstract class BiomassBurnerCompactedBlockMixin {
    @ModifyConstant(method = "startBurning", constant = @Constant(intValue = 180, ordinal = 1), require = 0)
    private int ftbevo$compactedBlockBaseDuration(int baseDuration) {
        return 1800;
    }
}
