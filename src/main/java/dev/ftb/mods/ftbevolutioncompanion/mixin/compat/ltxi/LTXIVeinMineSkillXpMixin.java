package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ltxi;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftbevolutioncompanion.skills.MiningSkillXp;
import liedge.ltxindustries.item.tool.ModularEnergyMiningItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ModularEnergyMiningItem.class, remap = false)
public abstract class LTXIVeinMineSkillXpMixin {
    @WrapOperation(
            method = "runVeinMineEffect",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean ftbevo$veinMineSkillXp(
            ServerLevel level,
            BlockPos pos,
            BlockState replacement,
            int flags,
            Operation<Boolean> original,
            @Local(argsOnly = true) Player player,
            @Local(argsOnly = true) ItemStack tool) {
        BlockState mined = level.getBlockState(pos);
        boolean harvestable =
                player instanceof ServerPlayer && EventHooks.doPlayerHarvestCheck(player, mined, level, pos);
        boolean removed = original.call(level, pos, replacement, flags);
        if (removed && harvestable) {
            MiningSkillXp.award((ServerPlayer) player, mined, tool);
        }
        return removed;
    }
}
