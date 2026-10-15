package dev.ftb.mods.ftbevolutioncompanion.skills;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.skills.effect.BleedingEffect;
import dev.ftb.mods.ftbevolutioncompanion.skills.effect.MarkedEffect;
import dev.ftb.mods.ftbevolutioncompanion.skills.effect.PinnedEffect;
import dev.ftb.mods.ftbevolutioncompanion.skills.effect.StunnedEffect;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class SkillsRegistry {
    public static final String NAMESPACE = "ftb";

    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, NAMESPACE);
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, NAMESPACE);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, FTBEvolutionCompanion.MOD_ID);

    private static final List<DeferredHolder<Attribute, Attribute>> PLAYER_ATTRIBUTES = new ArrayList<>();

    public static final DeferredHolder<Attribute, Attribute> ARROW_SAVE = attr("arrow_save", 1.0);
    public static final DeferredHolder<Attribute, Attribute> MULTISHOT = attr("multishot", 16.0);
    public static final DeferredHolder<Attribute, Attribute> RAMPING_SHOTS = attr("ramping_shots", 10.0);
    public static final DeferredHolder<Attribute, Attribute> FIRST_STRIKE = attr("first_strike", 2.0);
    public static final DeferredHolder<Attribute, Attribute> BOW_DURABILITY = attr("bow_durability", 20.0);
    public static final DeferredHolder<Attribute, Attribute> CROSSBOW_DURABILITY = attr("crossbow_durability", 20.0);
    public static final DeferredHolder<Attribute, Attribute> HOMING_ARROWS = attr("homing_arrows", 1.0);
    public static final DeferredHolder<Attribute, Attribute> RAIN_OF_ARROWS = attr("rain_of_arrows", 1.0);
    public static final DeferredHolder<Attribute, Attribute> POWER_SHOT = attr("power_shot", 5.0);
    public static final DeferredHolder<Attribute, Attribute> VITAL_SHOT = attr("vital_shot", 1.0);
    public static final DeferredHolder<Attribute, Attribute> IMPALE = attr("impale", 5.0);
    public static final DeferredHolder<Attribute, Attribute> MARKED_FOR_DEATH = attr("marked_for_death", 1.0);
    public static final DeferredHolder<Attribute, Attribute> BALLISTA = attr("ballista", 3.0);

    public static final DeferredHolder<Attribute, Attribute> UNARMED_RESISTANCE = attr("unarmed_resistance", 1.0);
    public static final DeferredHolder<Attribute, Attribute> UNARMED_KILL_HEAL = attr("unarmed_kill_heal", 1.0);
    public static final DeferredHolder<Attribute, Attribute> COMBO_PUNCH = attr("combo_punch", 2.0);
    public static final DeferredHolder<Attribute, Attribute> FASTER_STRIKES = attr("faster_strikes", 1.0);
    public static final DeferredHolder<Attribute, Attribute> UNARMED_RAMP = attr("unarmed_ramp", 1.0);

    public static final DeferredHolder<Attribute, Attribute> AXE_FRENZY = attr("axe_frenzy", 2.0);
    public static final DeferredHolder<Attribute, Attribute> AXE_DESPERATION = attr("axe_desperation", 10.0);
    public static final DeferredHolder<Attribute, Attribute> CHEAT_DEATH = attr("cheat_death", 1.0);
    public static final DeferredHolder<Attribute, Attribute> DEATH_BLOW = attr("death_blow", 3.0);
    public static final DeferredHolder<Attribute, Attribute> DUAL_WIELD = attr("dual_wield", 2.0);
    public static final DeferredHolder<Attribute, Attribute> LIGHTNING_STRIKES = attr("lightning_strikes", 1.0);
    public static final DeferredHolder<Attribute, Attribute> BLOODLUST = attr("bloodlust", 100.0);
    public static final DeferredHolder<Attribute, Attribute> AXE_DURABILITY = attr("axe_durability", 20.0);
    public static final DeferredHolder<Attribute, Attribute> AXE_PLUNDER = attr("axe_plunder", 20.0);

    public static final DeferredHolder<Attribute, Attribute> SHIELD_STUN = attr("shield_stun", 1.0);
    public static final DeferredHolder<Attribute, Attribute> SHIELD_RECOVERY = attr("shield_recovery", 1.0);
    public static final DeferredHolder<Attribute, Attribute> GROUND_SLAM = attr("ground_slam", 3.0);
    public static final DeferredHolder<Attribute, Attribute> LIGHTS_SHIELD = attr("lights_shield", 1.0);
    public static final DeferredHolder<Attribute, Attribute> SHIELD_MASTERY = attr("shield_mastery", 20.0);
    public static final DeferredHolder<Attribute, Attribute> SHIELD_DURABILITY = attr("shield_durability", 20.0);
    public static final DeferredHolder<Attribute, Attribute> DAY_DAMAGE = attr("day_damage", 2.0);

    public static final DeferredHolder<Attribute, Attribute> BACKSTAB = attr("backstab", 3.0);
    public static final DeferredHolder<Attribute, Attribute> SHADOW_STEP = attr("shadow_step", 1.0);
    public static final DeferredHolder<Attribute, Attribute> ECHO_STRIKES = attr("echo_strikes", 1.0);
    public static final DeferredHolder<Attribute, Attribute> SWORD_DURABILITY = attr("sword_durability", 20.0);
    public static final DeferredHolder<Attribute, Attribute> STEALTH = attr("stealth", 1.0);
    public static final DeferredHolder<Attribute, Attribute> NINJA = attr("ninja", 60.0);
    public static final DeferredHolder<Attribute, Attribute> SHAKEDOWN = attr("shakedown", 1.0);
    public static final DeferredHolder<Attribute, Attribute> NIGHT_DAMAGE = attr("night_damage", 2.0);
    public static final DeferredHolder<Attribute, Attribute> BLADEMASTER = attr("blademaster", 1.0);
    public static final DeferredHolder<Attribute, Attribute> SWORD_BLOCK = attr("sword_block", 1.0);
    public static final DeferredHolder<Attribute, Attribute> RIPOSTE = attr("riposte", 1.0);
    public static final DeferredHolder<Attribute, Attribute> PIERCING_STRIKE = attr("piercing_strike", 1.0);

    public static final DeferredHolder<Attribute, Attribute> SPEAR_DAMAGE = attr("spear_damage", 5.0);
    public static final DeferredHolder<Attribute, Attribute> SPEAR_DURABILITY = attr("spear_durability", 20.0);
    public static final DeferredHolder<Attribute, Attribute> SKEWER = attr("skewer", 2.0);
    public static final DeferredHolder<Attribute, Attribute> SPEAR_DISTANCE = attr("spear_distance", 2.0);
    public static final DeferredHolder<Attribute, Attribute> PIN = attr("pin", 1.0);
    public static final DeferredHolder<Attribute, Attribute> DRAGOON = attr("dragoon", 3.0);
    public static final DeferredHolder<Attribute, Attribute> PHALANX = attr("phalanx", 0.9);
    public static final DeferredHolder<Attribute, Attribute> SPEAR_REACH = attr("spear_reach", 3.0);
    public static final DeferredHolder<Attribute, Attribute> LANCER = attr("lancer", 1.0);
    public static final DeferredHolder<Attribute, Attribute> LANCER_RADIUS = attr("lancer_radius", 5.0);
    public static final DeferredHolder<Attribute, Attribute> LANCER_DAMAGE = attr("lancer_damage", 3.0);
    public static final DeferredHolder<Attribute, Attribute> LANCER_COOLDOWN = attr("lancer_cooldown", 0.9);

    public static final DeferredHolder<Attribute, Attribute> THROWN_DAMAGE = attr("thrown_damage", 5.0);
    public static final DeferredHolder<Attribute, Attribute> THROWN_VELOCITY = attr("thrown_velocity", 2.0);
    public static final DeferredHolder<Attribute, Attribute> THROWN_DURABILITY = attr("thrown_durability", 20.0);
    public static final DeferredHolder<Attribute, Attribute> RUN_THROUGH = attr("run_through", 5.0);
    public static final DeferredHolder<Attribute, Attribute> THROWN_PIN = attr("thrown_pin", 1.0);
    public static final DeferredHolder<Attribute, Attribute> RICOCHET = attr("ricochet", 5.0);
    public static final DeferredHolder<Attribute, Attribute> DOOM = attr("doom", 5.0);
    public static final DeferredHolder<Attribute, Attribute> VOLLEY = attr("volley", 1.0);

    public static final DeferredHolder<Attribute, Attribute> SCYTHE_DAMAGE = attr("scythe_damage", 5.0);
    public static final DeferredHolder<Attribute, Attribute> SCYTHE_DURABILITY = attr("scythe_durability", 20.0);
    public static final DeferredHolder<Attribute, Attribute> REAPING_ARC = attr("reaping_arc", 1.0);
    public static final DeferredHolder<Attribute, Attribute> SOUL_HARVEST = attr("soul_harvest", 20.0);
    public static final DeferredHolder<Attribute, Attribute> REAP_RHYTHM = attr("reap_rhythm", 1.0);
    public static final DeferredHolder<Attribute, Attribute> WITHERING_EDGE = attr("withering_edge", 1.0);
    public static final DeferredHolder<Attribute, Attribute> SOUL_TITHE = attr("soul_tithe", 5.0);
    public static final DeferredHolder<Attribute, Attribute> DEATHS_TOLL = attr("deaths_toll", 1.0);

    public static final DeferredHolder<Attribute, Attribute> ARMOR_DURABILITY = attr("armor_durability", 20.0);
    public static final DeferredHolder<Attribute, Attribute> MINING_FORTUNE = attr("mining_fortune", 20.0);
    public static final DeferredHolder<Attribute, Attribute> PICKAXE_DURABILITY = attr("pickaxe_durability", 20.0);

    public static final DeferredHolder<MobEffect, MobEffect> STUNNED = EFFECTS.register("stunned", StunnedEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> BLEEDING =
            EFFECTS.register("bleeding", BleedingEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> MARKED = EFFECTS.register("marked", MarkedEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> PINNED = EFFECTS.register("pinned", PinnedEffect::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SkillToggles>> TOGGLES = ATTACHMENTS.register(
            "skill_toggles",
            () -> AttachmentType.builder(() -> SkillToggles.DEFAULT)
                    .serialize(SkillToggles.CODEC)
                    .copyOnDeath()
                    .build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SkillCooldowns>> COOLDOWNS =
            ATTACHMENTS.register(
                    "skill_cooldowns",
                    () -> AttachmentType.builder(() -> SkillCooldowns.EMPTY)
                            .serialize(SkillCooldowns.CODEC)
                            .copyOnDeath()
                            .build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CombatState>> COMBAT_STATE =
            ATTACHMENTS.register(
                    "skills_combat_state",
                    () -> AttachmentType.builder(CombatState::new).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HomingState>> HOMING_STATE =
            ATTACHMENTS.register(
                    "homing_state",
                    () -> AttachmentType.builder(HomingState::new).build());

    private SkillsRegistry() {}

    private static DeferredHolder<Attribute, Attribute> attr(String name, double max) {
        DeferredHolder<Attribute, Attribute> holder = ATTRIBUTES.register(
                name, () -> new RangedAttribute("attribute.name.ftb." + name, 0.0, 0.0, max).setSyncable(true));
        PLAYER_ATTRIBUTES.add(holder);
        return holder;
    }

    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        for (DeferredHolder<Attribute, Attribute> holder : PLAYER_ATTRIBUTES) {
            event.add(EntityType.PLAYER, holder);
        }
    }
}
