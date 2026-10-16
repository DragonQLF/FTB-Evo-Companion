package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ultimine;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.ftb.mods.ftblibrary.util.Lazy;
import dev.ftb.mods.ftbultimine.FTBUltimine;
import dev.ftb.mods.ftbultimine.utils.ItemCollector;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = FTBUltimine.class, remap = false)
public abstract class UltimineReleaseDropsMixin {
    @Shadow
    private boolean isBreakingBlock;

    @Shadow
    private int tempBlockDroppedXp;

    @Shadow
    @Final
    private Lazy<ItemCollector> tempBlockDropsList;

    @WrapMethod(method = "handleBlockBreak")
    private boolean ftbevo$alwaysReleaseDrops(
            LevelAccessor level, BlockPos pos, BlockState state, ServerPlayer player, Operation<Boolean> original) {
        boolean alreadyBreaking = isBreakingBlock;
        try {
            return original.call(level, pos, state, player);
        } finally {
            if (!alreadyBreaking && isBreakingBlock) {
                isBreakingBlock = false;
                ServerLevel serverLevel = player.level();
                tempBlockDropsList.get().drop(serverLevel, pos);
                if (tempBlockDroppedXp > 0) {
                    serverLevel.addFreshEntity(new ExperienceOrb(
                            serverLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, tempBlockDroppedXp));
                    tempBlockDroppedXp = 0;
                }
            }
        }
    }
}
