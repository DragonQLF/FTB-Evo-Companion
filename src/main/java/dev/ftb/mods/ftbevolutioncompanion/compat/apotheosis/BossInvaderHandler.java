package dev.ftb.mods.ftbevolutioncompanion.compat.apotheosis;

import dev.ftb.mods.ftbevolutioncompanion.compat.oceanmobs.RiftArena;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.apotheosis.mobs.registries.InvaderRegistry;
import dev.shadowsoffire.apotheosis.mobs.types.Invader;
import dev.shadowsoffire.apotheosis.tiers.GenContext;
import dev.shadowsoffire.apotheosis.tiers.WorldTier;
import java.util.Set;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

public final class BossInvaderHandler {
    private static final String NAMESPACE = "ftb";
    private static final String PREFIX = "bosses/";
    private static final String CHECKED_KEY = "ftbevo.boss_invader_checked";
    private static final double PLAYER_RANGE = 128.0;

    private BossInvaderHandler() {}

    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        if (event.isCanceled() || event.isSpawnCancelled()) {
            return;
        }
        Mob mob = event.getEntity();
        EntitySpawnReason reason = event.getSpawnType();
        if (reason == EntitySpawnReason.SPAWNER
                || (reason == EntitySpawnReason.TRIAL_SPAWNER && !RiftArena.isRiftWeaver(mob.getType()))) {
            mob.getPersistentData().putBoolean(CHECKED_KEY, true);
            return;
        }
        apply(event.getLevel().getLevel(), mob);
    }

    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.loadedFromDisk()
                || !(event.getLevel() instanceof ServerLevel level)
                || !(event.getEntity() instanceof Mob mob)) {
            return;
        }
        apply(level, mob);
    }

    private static void apply(ServerLevel level, Mob mob) {
        if (mob.getPersistentData().getBooleanOr(CHECKED_KEY, false)
                || mob.getPersistentData().getBooleanOr(Invader.BOSS_KEY, false)) {
            return;
        }
        Invader invader = find(mob.getType());
        if (invader == null) {
            return;
        }
        mob.getPersistentData().putBoolean(CHECKED_KEY, true);
        Player player = level.getNearestPlayer(mob.getX(), mob.getY(), mob.getZ(), PLAYER_RANGE, false);
        GenContext ctx = player != null
                ? GenContext.forPlayerAtPos(mob.getRandom(), player, mob.blockPosition())
                : GenContext.standalone(mob.getRandom(), WorldTier.HAVEN, 0.0F, level, mob.blockPosition());
        Set<LootRarity> rarities = invader.stats().keySet();
        LootRarity rarity = rarities.size() == 1 ? rarities.iterator().next() : LootRarity.random(ctx, rarities);
        if (rarity == null) {
            rarity = rarities.iterator().next();
        }
        invader.initBoss(mob, ctx, rarity);
    }

    private static Invader find(EntityType<?> type) {
        for (Identifier id : InvaderRegistry.INSTANCE.getKeys()) {
            if (!id.getNamespace().equals(NAMESPACE) || !id.getPath().startsWith(PREFIX)) {
                continue;
            }
            Invader invader = InvaderRegistry.INSTANCE.getValue(id);
            if (invader != null && invader.entity() == type) {
                return invader;
            }
        }
        return null;
    }
}
