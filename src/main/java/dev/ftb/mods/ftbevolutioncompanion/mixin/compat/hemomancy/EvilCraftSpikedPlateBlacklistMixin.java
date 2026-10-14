package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hemomancy;

import dev.ftb.mods.ftbevolutioncompanion.content.CompanionTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.cyclops.evilcraft.block.BlockSpikedPlate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockSpikedPlate.class, remap = false)
public abstract class EvilCraftSpikedPlateBlacklistMixin {
    @Inject(
            method = "damageEntity(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;"
                    + "Lnet/minecraft/core/BlockPos;)Z",
            at = @At("HEAD"),
            cancellable = true)
    private void ftbevo$skipBlacklisted(
            ServerLevel level, Entity entity, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (entity.getType().builtInRegistryHolder().is(CompanionTags.SPIKED_PLATE_BLACKLIST)) {
            cir.setReturnValue(false);
        }
    }
}
