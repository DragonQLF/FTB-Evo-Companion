package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.energymeter;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(targets = "com.almostreliable.energymeter.block.component.MeterEnergyHandler", remap = false)
public abstract class MeterEnergyRecursionGuardMixin {
    @Unique
    private boolean ftbevo$forwarding;

    @WrapMethod(method = "forwardEnergy")
    private int ftbevo$refuseReentrantForward(int energy, TransactionContext transaction, Operation<Integer> original) {
        if (ftbevo$forwarding) {
            return 0;
        }
        ftbevo$forwarding = true;
        try {
            return original.call(energy, transaction);
        } finally {
            ftbevo$forwarding = false;
        }
    }
}
