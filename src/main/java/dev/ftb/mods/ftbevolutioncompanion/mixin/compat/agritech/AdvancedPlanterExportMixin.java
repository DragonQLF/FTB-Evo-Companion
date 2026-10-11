package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.agritech;

import com.misterd.agritechevolved.blockentity.custom.AdvancedPlanterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AdvancedPlanterBlockEntity.class, remap = false)
public abstract class AdvancedPlanterExportMixin {
    @Shadow
    private static void tryOutputItemsBelow(Level level, BlockPos pos, AdvancedPlanterBlockEntity be) {}

    @Inject(method = "tick", at = @At("HEAD"))
    private static void ftbevo$exportWhileFull(
            Level level, BlockPos pos, BlockState state, AdvancedPlanterBlockEntity be, CallbackInfo ci) {
        if (!level.isClientSide() && be.inventory.size() >= 17 && (level.getGameTime() + pos.asLong()) % 20 == 0) {
            tryOutputItemsBelow(level, pos, be);
        }
    }
}
