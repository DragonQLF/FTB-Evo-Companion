package dev.ftb.mods.ftbevolutioncompanion.skills;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class AxePlunder {
    private static final ThreadLocal<ServerPlayer> KILLER = new ThreadLocal<>();

    private AxePlunder() {}

    public static void onLivingDeath(LivingDeathEvent event) {
        KILLER.remove();
        if (event.isCanceled()
                || !(event.getSource().getEntity() instanceof ServerPlayer player)
                || !SkillsHelper.isAxe(player.getMainHandItem())
                || SkillsHelper.attr(player, SkillsRegistry.AXE_PLUNDER) <= 0.0) {
            return;
        }
        KILLER.set(player);
    }

    public static void onLivingDrops(LivingDropsEvent event) {
        KILLER.remove();
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        KILLER.remove();
    }

    public static void onEnchantmentLevel(GetEnchantmentLevelEvent event) {
        ServerPlayer player = KILLER.get();
        if (player == null
                || !event.isTargetting(Enchantments.LOOTING)
                || !(event.getStack() instanceof ItemStack stack)
                || stack != player.getMainHandItem()) {
            return;
        }
        event.getHolder(Enchantments.FORTUNE).ifPresent(fortune -> {
            int cap = (int) SkillsHelper.attr(player, SkillsRegistry.AXE_PLUNDER);
            int bonus = Math.min(stack.getEnchantmentLevel(fortune) * 2, cap);
            if (bonus <= 0) {
                return;
            }
            event.getHolder(Enchantments.LOOTING)
                    .ifPresent(looting -> event.getEnchantments()
                            .set(looting, event.getEnchantments().getLevel(looting) + bonus));
        });
    }
}
