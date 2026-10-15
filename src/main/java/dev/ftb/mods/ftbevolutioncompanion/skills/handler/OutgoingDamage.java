package dev.ftb.mods.ftbevolutioncompanion.skills.handler;

import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;
import dev.ftb.mods.ftbevolutioncompanion.skills.CombatState;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillCooldowns;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsAbilities;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsHelper;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public final class OutgoingDamage {
    private static boolean applyingEcho;

    private OutgoingDamage() {}

    public static boolean isApplyingEcho() {
        return applyingEcho;
    }

    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (applyingEcho || CombatTicker.isApplyingSkillDamage()) {
            return;
        }
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof ServerPlayer attacker) || attacker == target) {
            return;
        }

        CombatState state = attacker.getData(SkillsRegistry.COMBAT_STATE);
        double mult = 1.0;
        double bonus = 0.0;
        boolean echoProc = false;

        if (source.isDirect() && source.getDirectEntity() == attacker) {
            ItemStack mainHand = attacker.getMainHandItem();
            if (attacker.level().isBrightOutside()) {
                mult *= 1.0 + SkillsHelper.attr(attacker, SkillsRegistry.DAY_DAMAGE);
            }
            if (SkillsHelper.isSword(mainHand)) {
                if (attacker.level().isDarkOutside()) {
                    mult *= 1.0 + SkillsHelper.attr(attacker, SkillsRegistry.NIGHT_DAMAGE);
                }
                if (SkillsHelper.isBehind(target, attacker)) {
                    mult *= 1.0 + SkillsHelper.attr(attacker, SkillsRegistry.BACKSTAB);
                }
                if (state.riposteReadyUntil >= attacker.level().getGameTime()
                        && SkillsHelper.attr(attacker, SkillsRegistry.RIPOSTE) > 0.0) {
                    state.riposteReadyUntil = 0;
                    mult *= CompanionConfig.RIPOSTE_DAMAGE_MULT.get();
                }
                double echo = SkillsHelper.attr(attacker, SkillsRegistry.ECHO_STRIKES);
                echoProc = echo > 0.0 && attacker.getRandom().nextDouble() < echo;
                applyBleed(attacker, target);
            } else if (SkillsHelper.isSpear(mainHand)) {
                mult *= 1.0 + SkillsHelper.attr(attacker, SkillsRegistry.SPEAR_DAMAGE);
                if (!attacker.isUsingItem()) {
                    long now = attacker.level().getGameTime();
                    if (state.lastJabTick != now) {
                        state.lastJabTick = now;
                        state.jabTargetsThisTick = 0;
                    }
                    mult *= 1.0 + SkillsHelper.attr(attacker, SkillsRegistry.SKEWER) * state.jabTargetsThisTick;
                    state.jabTargetsThisTick++;
                    double pin = SkillsHelper.attr(attacker, SkillsRegistry.PIN);
                    if (pin > 0.0
                            && canReceiveDebuff(target)
                            && attacker.getRandom().nextDouble() < pin) {
                        target.addEffect(
                                new MobEffectInstance(
                                        SkillsRegistry.PINNED, CompanionConfig.PIN_DURATION_TICKS.get(), 0),
                                attacker);
                    }
                }
                double distance = SkillsHelper.attr(attacker, SkillsRegistry.SPEAR_DISTANCE);
                if (distance > 0.0) {
                    mult *= 1.0 + distance * reachFraction(attacker, target, mainHand);
                }
                if (!target.onGround()) {
                    mult *= 1.0 + SkillsHelper.attr(attacker, SkillsRegistry.DRAGOON);
                }
            } else if (SkillsHelper.isScythe(mainHand)) {
                mult *= 1.0 + SkillsHelper.attr(attacker, SkillsRegistry.SCYTHE_DAMAGE);
                double wither = SkillsHelper.attr(attacker, SkillsRegistry.WITHERING_EDGE);
                if (wither > 0.0
                        && canReceiveDebuff(target)
                        && attacker.getRandom().nextDouble() < wither) {
                    applyWither(attacker, target);
                }
            } else if (SkillsHelper.isAxe(mainHand)) {
                if (SkillsHelper.healthFraction(target) < CompanionConfig.DEATH_BLOW_THRESHOLD.get()) {
                    mult *= 1.0 + SkillsHelper.attr(attacker, SkillsRegistry.DEATH_BLOW);
                }
                if (SkillsHelper.healthFraction(attacker) < CompanionConfig.AXE_FRENZY_THRESHOLD.get()) {
                    mult *= 1.0 + SkillsHelper.attr(attacker, SkillsRegistry.AXE_FRENZY);
                }
                if (SkillsHelper.isAxe(attacker.getOffhandItem())) {
                    mult *= 1.0 + SkillsHelper.attr(attacker, SkillsRegistry.DUAL_WIELD);
                }
            } else if (SkillsHelper.isUnarmed(attacker)) {
                if ((state.unarmedHitCounter + 1) % 3 == 0) {
                    mult *= 1.0 + SkillsHelper.attr(attacker, SkillsRegistry.COMBO_PUNCH);
                }
                if (state.unarmedRampStacks > 0
                        && SkillsAbilities.toggles(attacker).unarmedRamp()) {
                    mult *= 1.0 + SkillsHelper.attr(attacker, SkillsRegistry.UNARMED_RAMP) * state.unarmedRampStacks;
                }
            }
        } else if (SkillsHelper.isThrownProjectile(source.getDirectEntity())) {
            mult *= 1.0 + SkillsHelper.attr(attacker, SkillsRegistry.THROWN_DAMAGE);
            double pin = SkillsHelper.attr(attacker, SkillsRegistry.THROWN_PIN);
            if (pin > 0.0 && canReceiveDebuff(target) && attacker.getRandom().nextDouble() < pin) {
                target.addEffect(
                        new MobEffectInstance(SkillsRegistry.PINNED, CompanionConfig.PIN_DURATION_TICKS.get(), 0),
                        attacker);
            }
            double doom = SkillsHelper.attr(attacker, SkillsRegistry.DOOM);
            if (doom > 0.0 && SkillCooldowns.ready(attacker, SkillCooldowns.DOOM)) {
                mult *= 1.0 + doom * (1.0 - SkillsHelper.healthFraction(target));
                applyDoom(attacker, target);
            }
        } else if (source.getDirectEntity() instanceof AbstractArrow arrow) {
            if (target.getHealth() >= target.getMaxHealth()) {
                mult *= 1.0 + SkillsHelper.attr(attacker, SkillsRegistry.FIRST_STRIKE);
            }
            int maxStacks = (int) SkillsHelper.attr(attacker, SkillsRegistry.RAMPING_SHOTS);
            if (maxStacks > 0) {
                long now = attacker.level().getGameTime();
                if (now - state.lastArcherHitTime > CompanionConfig.ARCHER_RAMP_TIMEOUT.get()) {
                    state.archerRampStacks = 0;
                }
                if (state.archerRampStacks > 0) {
                    mult *= 1.0
                            + CompanionConfig.ARCHER_RAMP_PER_STACK.get() * Math.min(state.archerRampStacks, maxStacks);
                }
            }
            ItemStack weapon = arrow.getWeaponItem();
            if (weapon != null && SkillsHelper.isCrossbow(weapon)) {
                if (arrow.hasData(SkillsRegistry.HOMING_STATE)
                        && arrow.getData(SkillsRegistry.HOMING_STATE).powerShot) {
                    mult *= 1.0 + SkillsHelper.attr(attacker, SkillsRegistry.POWER_SHOT);
                }
                double vital = SkillsHelper.attr(attacker, SkillsRegistry.VITAL_SHOT);
                if (vital > 0.0 && attacker.getRandom().nextDouble() < vital) {
                    bonus += target.getMaxHealth() * CompanionConfig.VITAL_SHOT_FRACTION.get();
                }
            }
        }

        if (mult != 1.0 || bonus > 0.0) {
            event.setAmount(event.getAmount() * (float) mult + (float) bonus);
        }

        if (source.isDirect()
                && source.getDirectEntity() == attacker
                && SkillsHelper.isScythe(attacker.getMainHandItem())) {
            state.lastScytheHitDamage = event.getAmount();
        }

        if (echoProc) {
            state.pendingEchoTarget = target.getUUID();
            state.pendingEchoAmount = event.getAmount();
            state.pendingEchoTick = attacker.level().getGameTime();
        }
    }

    public static void applyEcho(ServerPlayer attacker, LivingEntity target) {
        CombatState state = attacker.getData(SkillsRegistry.COMBAT_STATE);
        if (state.pendingEchoTarget == null
                || !state.pendingEchoTarget.equals(target.getUUID())
                || state.pendingEchoTick != attacker.level().getGameTime()) {
            return;
        }
        float amount = state.pendingEchoAmount;
        state.pendingEchoTarget = null;
        state.pendingEchoAmount = 0.0F;
        if (amount <= 0.0F || !target.isAlive() || !(target.level() instanceof ServerLevel level)) {
            return;
        }

        int invulnerableTime = target.invulnerableTime;
        boolean hurt;
        applyingEcho = true;
        try {
            target.invulnerableTime = 0;
            hurt = target.hurtServer(level, level.damageSources().playerAttack(attacker), amount);
        } finally {
            applyingEcho = false;
        }

        if (hurt) {
            level.playSound(
                    null,
                    target.getX(),
                    target.getY(),
                    target.getZ(),
                    SoundEvents.PLAYER_ATTACK_CRIT,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.2F);
        } else {
            target.invulnerableTime = invulnerableTime;
        }
    }

    private static void applyDoom(ServerPlayer attacker, LivingEntity target) {
        SkillCooldowns.start(attacker, SkillCooldowns.DOOM, CompanionConfig.DOOM_COOLDOWN.get());
        if (canReceiveDebuff(target)) {
            int ticks = CompanionConfig.DOOM_EFFECT_TICKS.get();
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, ticks, 0), attacker);
            target.addEffect(
                    new MobEffectInstance(MobEffects.SLOWNESS, ticks, CompanionConfig.DOOM_SLOWNESS_AMPLIFIER.get()),
                    attacker);
        }
        if (target.level() instanceof ServerLevel level) {
            Vec3 center = target.getBoundingBox().getCenter();
            level.sendParticles(ParticleTypes.SCULK_SOUL, center.x, center.y, center.z, 20, 0.4, 0.6, 0.4, 0.05);
            level.sendParticles(ParticleTypes.LARGE_SMOKE, center.x, center.y, center.z, 15, 0.4, 0.6, 0.4, 0.02);
            level.playSound(
                    null, center.x, center.y, center.z, SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 0.8F, 0.6F);
        }
    }

    private static boolean canReceiveDebuff(LivingEntity target) {
        return !(target instanceof Player player) || (!player.isCreative() && !player.isSpectator());
    }

    private static double reachFraction(ServerPlayer attacker, LivingEntity target, ItemStack weapon) {
        double maxReach = attacker.getAttackRangeWith(weapon).effectiveMaxRange(attacker);
        if (maxReach <= 0.0) {
            return 0.0;
        }
        double distance = Math.sqrt(target.getBoundingBox().distanceToSqr(attacker.getEyePosition()));
        return Math.clamp(distance / maxReach, 0.0, 1.0);
    }

    private static void applyWither(ServerPlayer attacker, LivingEntity target) {
        MobEffectInstance current = target.getEffect(MobEffects.WITHER);
        int amplifier = current == null
                ? 0
                : Math.min(current.getAmplifier() + 1, CompanionConfig.WITHERING_MAX_STACKS.get() - 1);
        target.addEffect(
                new MobEffectInstance(MobEffects.WITHER, CompanionConfig.WITHERING_DURATION_TICKS.get(), amplifier),
                attacker);
    }

    private static void applyBleed(ServerPlayer attacker, LivingEntity target) {
        if (SkillsHelper.attr(attacker, SkillsRegistry.BLADEMASTER) <= 0.0
                || !SkillsAbilities.toggles(attacker).blademaster()) {
            return;
        }
        MobEffectInstance current = target.getEffect(SkillsRegistry.BLEEDING);
        int amplifier =
                current == null ? 0 : Math.min(current.getAmplifier() + 1, CompanionConfig.BLEED_MAX_STACKS.get() - 1);
        target.addEffect(
                new MobEffectInstance(SkillsRegistry.BLEEDING, CompanionConfig.BLEED_DURATION_TICKS.get(), amplifier),
                attacker);
    }
}
