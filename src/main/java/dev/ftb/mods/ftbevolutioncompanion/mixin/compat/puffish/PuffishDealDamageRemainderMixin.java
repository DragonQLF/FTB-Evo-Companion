package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.puffish;

import dev.ftb.mods.ftbevolutioncompanion.skills.SkillXpRemainder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.puffish.skillsmod.experience.source.builtin.DealDamageExperienceSource", remap = false)
public abstract class PuffishDealDamageRemainderMixin {
    @Unique
    private Object ftbevo$wrappedCalculation;

    @Inject(
            method = "calculation()Lnet/puffish/skillsmod/api/calculation/Calculation;",
            at = @At("RETURN"),
            cancellable = true)
    private void ftbevo$carryFractions(CallbackInfoReturnable<Object> cir) {
        if (ftbevo$wrappedCalculation == null) {
            ftbevo$wrappedCalculation = SkillXpRemainder.carryAll(cir.getReturnValue());
        }
        cir.setReturnValue(ftbevo$wrappedCalculation);
    }
}
