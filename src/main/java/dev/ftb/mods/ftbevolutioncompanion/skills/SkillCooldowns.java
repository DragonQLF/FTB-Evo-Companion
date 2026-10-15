package dev.ftb.mods.ftbevolutioncompanion.skills;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;

public record SkillCooldowns(Map<String, Long> readyAt) {
    public static final SkillCooldowns EMPTY = new SkillCooldowns(Map.of());

    public static final MapCodec<SkillCooldowns> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.unboundedMap(Codec.STRING, Codec.LONG)
                            .optionalFieldOf("ready_at", Map.of())
                            .forGetter(SkillCooldowns::readyAt))
                    .apply(instance, SkillCooldowns::new));

    public static final String CHEAT_DEATH = "cheat_death";
    public static final String LIGHTNING = "lightning";
    public static final String SHADOW_STEP = "shadow_step";
    public static final String SHIELD_STUN = "shield_stun";
    public static final String LIGHTS_SHIELD = "lights_shield";
    public static final String NINJA = "ninja";
    public static final String RIPOSTE = "riposte";
    public static final String LANCER = "lancer";
    public static final String DEATHS_TOLL = "deaths_toll";
    public static final String DOOM = "doom";

    public static boolean ready(ServerPlayer player, String key) {
        return remaining(player, key) <= 0;
    }

    public static long remaining(ServerPlayer player, String key) {
        long readyTime = player.getData(SkillsRegistry.COOLDOWNS).readyAt().getOrDefault(key, 0L);
        return readyTime - player.level().getGameTime();
    }

    public static void start(ServerPlayer player, String key, long ticks) {
        Map<String, Long> next =
                new HashMap<>(player.getData(SkillsRegistry.COOLDOWNS).readyAt());
        next.put(key, player.level().getGameTime() + ticks);
        player.setData(SkillsRegistry.COOLDOWNS, new SkillCooldowns(Map.copyOf(next)));
    }
}
