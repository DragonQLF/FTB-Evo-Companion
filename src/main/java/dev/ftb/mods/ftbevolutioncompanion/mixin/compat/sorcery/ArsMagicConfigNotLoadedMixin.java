package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.sorcery;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "at.minecraftschurli.mods.arsmagicalegacy.apiimpl.MagicHelperImpl", remap = false)
public abstract class ArsMagicConfigNotLoadedMixin {
    @WrapOperation(
            method = "knowsMagic",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/neoforged/neoforge/common/ModConfigSpec$ConfigValue;get()Ljava/lang/Object;"))
    private Object ftbevo$defaultBeforeConfigLoads(ModConfigSpec.ConfigValue<?> value, Operation<Object> original) {
        try {
            return original.call(value);
        } catch (IllegalStateException e) {
            return value.getDefault();
        }
    }
}
