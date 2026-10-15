package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.sorcery;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "at.minecraftschurli.mods.arsmagicalegacy.spell.component.Dig", remap = false)
public abstract class ArsMagicDigBlockEntityDropMixin {
    @WrapOperation(
            method = "castBlock",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lat/minecraftschurli/mods/arsmagicalegacy/util/AMUtil;cancelDestroyBlock(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean ftbevo$captureBlockEntity(
            Level level,
            BlockPos pos,
            BlockState state,
            Player player,
            ItemStack stack,
            Operation<Boolean> original,
            @Share("blockEntity") LocalRef<BlockEntity> blockEntity) {
        blockEntity.set(level.getBlockEntity(pos));
        return original.call(level, pos, state, player, stack);
    }

    @WrapOperation(
            method = "castBlock",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/server/level/ServerLevel;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    private BlockEntity ftbevo$useCapturedBlockEntity(
            ServerLevel level,
            BlockPos pos,
            Operation<BlockEntity> original,
            @Share("blockEntity") LocalRef<BlockEntity> blockEntity) {
        BlockEntity current = original.call(level, pos);
        return current != null ? current : blockEntity.get();
    }
}
