package dev.ftb.mods.ftbevolutioncompanion.skills.handler;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;
import dev.ftb.mods.ftbevolutioncompanion.mixin.AbstractArrowInvoker;
import dev.ftb.mods.ftbevolutioncompanion.mixin.LivingEntityAccessor;
import dev.ftb.mods.ftbevolutioncompanion.skills.AllyCheck;
import dev.ftb.mods.ftbevolutioncompanion.skills.ApothicHooks;
import dev.ftb.mods.ftbevolutioncompanion.skills.CombatState;
import dev.ftb.mods.ftbevolutioncompanion.skills.HomingState;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillCooldowns;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsAbilities;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsHelper;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsRegistry;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityProcessor;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class CombatTicker {
    private static final Identifier FASTER_STRIKES_ID = FTBEvolutionCompanion.id("faster_strikes");
    private static final Identifier SHIELD_MASTERY_ARMOR_ID = FTBEvolutionCompanion.id("shield_mastery_armor");
    private static final Identifier SHIELD_MASTERY_TOUGHNESS_ID = FTBEvolutionCompanion.id("shield_mastery_toughness");
    private static final Identifier AXE_DESPERATION_ID = FTBEvolutionCompanion.id("axe_desperation");

    private static boolean applyingLightning;
    private static boolean applyingSkillDamage;

    private CombatTicker() {}

    public static boolean isApplyingSkillDamage() {
        return applyingSkillDamage;
    }

    public static void onDamagePost(LivingDamageEvent.Post event) {
        DamageSource source = event.getSource();
        if (OutgoingDamage.isApplyingEcho()
                || applyingLightning
                || applyingSkillDamage
                || !(source.getEntity() instanceof ServerPlayer attacker)
                || event.getEntity() == attacker) {
            return;
        }
        LivingEntity target = event.getEntity();
        CombatState state = attacker.getData(SkillsRegistry.COMBAT_STATE);
        long now = attacker.level().getGameTime();

        if (source.isDirect() && source.getDirectEntity() == attacker) {
            if (SkillsHelper.isUnarmed(attacker)) {
                state.unarmedHitCounter++;
                state.lastUnarmedHitTime = now;
                state.unarmedRampStacks =
                        Math.min(state.unarmedRampStacks + 1, CompanionConfig.UNARMED_RAMP_MAX_STACKS.get());
                if (SkillsHelper.attr(attacker, SkillsRegistry.FASTER_STRIKES) > 0.0
                        && SkillsAbilities.toggles(attacker).fasterStrikes()) {
                    state.fasterStrikesStacks =
                            Math.min(state.fasterStrikesStacks + 1, CompanionConfig.FASTER_STRIKES_MAX_STACKS.get());
                }
            } else {
                resetUnarmed(state);
            }
            if (SkillsHelper.isAxe(attacker.getMainHandItem())) {
                handleLightning(attacker, target, state);
            } else {
                state.axeHitCounter = 0;
            }
            if (SkillsHelper.isSword(attacker.getMainHandItem())) {
                state.swordHitCounter++;
            } else {
                state.swordHitCounter = 0;
            }
            if (SkillsHelper.isScythe(attacker.getMainHandItem())) {
                reapingArc(attacker, target, state.lastScytheHitDamage);
            }
        } else if (SkillsHelper.isThrownProjectile(source.getDirectEntity())) {
            resetUnarmed(state);
            int bounces = (int) SkillsHelper.attr(attacker, SkillsRegistry.RICOCHET);
            if (bounces > 0 && target.level() instanceof ServerLevel serverLevel) {
                ricochet(serverLevel, attacker, source.getDirectEntity(), target, event.getInflictedDamage(), bounces);
            }
        } else if (source.getDirectEntity() instanceof AbstractArrow arrow) {
            resetUnarmed(state);
            int maxStacks = (int) SkillsHelper.attr(attacker, SkillsRegistry.RAMPING_SHOTS);
            if (maxStacks > 0) {
                if (now - state.lastArcherHitTime > CompanionConfig.ARCHER_RAMP_TIMEOUT.get()) {
                    state.archerRampStacks = 0;
                }
                state.archerRampStacks = Math.min(state.archerRampStacks + 1, maxStacks);
                state.lastArcherHitTime = now;
            }
            ItemStack weapon = arrow.getWeaponItem();
            if (weapon != null && SkillsHelper.isCrossbow(weapon) && target.isAlive()) {
                if (SkillsHelper.attr(attacker, SkillsRegistry.MARKED_FOR_DEATH) > 0.0) {
                    target.addEffect(
                            new MobEffectInstance(
                                    SkillsRegistry.MARKED, CompanionConfig.MARKED_DURATION_TICKS.get(), 0),
                            attacker);
                }
                double rainChance = SkillsHelper.attr(attacker, SkillsRegistry.RAIN_OF_ARROWS);
                if (rainChance > 0.0
                        && SkillsAbilities.toggles(attacker).rainOfArrows()
                        && attacker.getRandom().nextDouble() < rainChance
                        && target.level() instanceof ServerLevel serverLevel) {
                    spawnRainOfArrows(serverLevel, attacker, target);
                }
            }
        }

        OutgoingDamage.applyEcho(attacker, target);
    }

    private static void resetUnarmed(CombatState state) {
        state.unarmedHitCounter = 0;
        state.unarmedRampStacks = 0;
        state.fasterStrikesStacks = 0;
    }

    private static void handleLightning(ServerPlayer attacker, LivingEntity target, CombatState state) {
        if (SkillsHelper.attr(attacker, SkillsRegistry.LIGHTNING_STRIKES) <= 0.0
                || !SkillsAbilities.toggles(attacker).lightning()) {
            state.axeHitCounter = 0;
            return;
        }
        state.axeHitCounter++;
        if (state.axeHitCounter < 3) {
            return;
        }
        state.axeHitCounter = 0;
        if (!SkillCooldowns.ready(attacker, SkillCooldowns.LIGHTNING)) {
            return;
        }
        if (attacker.level() instanceof ServerLevel serverLevel) {
            strikeLightning(serverLevel, attacker, target);
            SkillCooldowns.start(attacker, SkillCooldowns.LIGHTNING, CompanionConfig.LIGHTNING_COOLDOWN.get());
        }
    }

    private static void strikeLightning(ServerLevel level, ServerPlayer attacker, LivingEntity target) {
        Vec3 strike = Vec3.atBottomCenterOf(target.blockPosition());
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
        if (bolt != null) {
            bolt.snapTo(strike);
            bolt.setVisualOnly(true);
            level.addFreshEntity(bolt);
        }

        float damage = CompanionConfig.LIGHTNING_DAMAGE.get().floatValue();
        if (damage <= 0.0F) {
            return;
        }
        AABB area = new AABB(
                strike.x - 3.0, strike.y - 3.0, strike.z - 3.0, strike.x + 3.0, strike.y + 9.0, strike.z + 3.0);
        DamageSource source = level.damageSources().source(DamageTypes.LIGHTNING_BOLT, attacker);
        applyingLightning = true;
        try {
            for (LivingEntity victim : level.getEntitiesOfClass(
                    LivingEntity.class,
                    area,
                    entity -> entity != attacker
                            && entity.isAlive()
                            && !entity.isSpectator()
                            && !(entity instanceof Player other && !attacker.canHarmPlayer(other)))) {
                int invulnerableTime = victim.invulnerableTime;
                victim.invulnerableTime = 0;
                if (!victim.hurtServer(level, source, damage)) {
                    victim.invulnerableTime = invulnerableTime;
                }
            }
        } finally {
            applyingLightning = false;
        }
    }

    private static void reapingArc(ServerPlayer attacker, LivingEntity target, float damage) {
        double fraction = SkillsHelper.attr(attacker, SkillsRegistry.REAPING_ARC);
        if (fraction <= 0.0
                || damage <= 0.0F
                || !SkillsAbilities.toggles(attacker).scytheArc()
                || !(attacker.level() instanceof ServerLevel level)) {
            return;
        }
        double radius = CompanionConfig.SCYTHE_ARC_RADIUS.get();
        double minDot = Math.cos(Math.toRadians(CompanionConfig.SCYTHE_ARC_ANGLE.get() / 2.0));
        Vec3 look = attacker.getLookAngle();
        Vec3 facing = new Vec3(look.x, 0.0, look.z);
        boolean hasFacing = facing.lengthSqr() > 1.0E-4;
        if (hasFacing) {
            facing = facing.normalize();
        }
        Vec3 origin = attacker.position();
        DamageSource source = level.damageSources().playerAttack(attacker);
        float arcDamage = (float) (damage * fraction);
        int hits = 0;
        applyingSkillDamage = true;
        try {
            for (LivingEntity victim : level.getEntitiesOfClass(
                    LivingEntity.class,
                    attacker.getBoundingBox().inflate(radius, 1.0, radius),
                    entity -> entity != target && AllyCheck.isHostileTarget(attacker, entity))) {
                Vec3 offset = victim.position().subtract(origin);
                Vec3 horizontal = new Vec3(offset.x, 0.0, offset.z);
                double distance = horizontal.length();
                if (distance > radius) {
                    continue;
                }
                if (hasFacing && distance > 1.0E-4 && facing.dot(horizontal.scale(1.0 / distance)) < minDot) {
                    continue;
                }
                if (hurtIgnoringCooldown(level, victim, source, arcDamage)) {
                    hits++;
                }
            }
        } finally {
            applyingSkillDamage = false;
        }
        if (hits == 0) {
            return;
        }
        double heal = SkillsHelper.attr(attacker, SkillsRegistry.SOUL_HARVEST);
        if (heal > 0.0) {
            attacker.heal((float) (heal * hits));
        }
        double dx = -Math.sin(Math.toRadians(attacker.getYRot()));
        double dz = Math.cos(Math.toRadians(attacker.getYRot()));
        level.sendParticles(
                ParticleTypes.SWEEP_ATTACK,
                attacker.getX() + dx,
                attacker.getY(0.5),
                attacker.getZ() + dz,
                0,
                dx,
                0.0,
                dz,
                0.0);
        level.playSound(
                null,
                attacker.getX(),
                attacker.getY(),
                attacker.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS,
                1.0F,
                0.8F);
    }

    private static boolean hurtIgnoringCooldown(
            ServerLevel level, LivingEntity victim, DamageSource source, float damage) {
        int invulnerableTime = victim.invulnerableTime;
        victim.invulnerableTime = 0;
        if (victim.hurtServer(level, source, damage)) {
            return true;
        }
        victim.invulnerableTime = invulnerableTime;
        return false;
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer killer) || event.getEntity() == killer) {
            return;
        }
        if (SkillsHelper.isScythe(killer.getMainHandItem())) {
            onScytheKill(killer, event.getEntity());
            return;
        }
        if (SkillsHelper.isUnarmed(killer)) {
            double fraction = SkillsHelper.attr(killer, SkillsRegistry.UNARMED_KILL_HEAL);
            if (fraction > 0.0) {
                killer.heal((float) (fraction * killer.getMaxHealth()));
            }
        } else if (SkillsHelper.isAxe(killer.getMainHandItem())) {
            double flat = SkillsHelper.attr(killer, SkillsRegistry.BLOODLUST);
            if (flat > 0.0) {
                killer.heal((float) flat);
            }
        }
    }

    private static void onScytheKill(ServerPlayer killer, LivingEntity victim) {
        double rhythm = SkillsHelper.attr(killer, SkillsRegistry.REAP_RHYTHM);
        if (rhythm > 0.0 && killer.getRandom().nextDouble() < rhythm) {
            ((LivingEntityAccessor) killer)
                    .ftbevo$setAttackStrengthTicker((int) Math.ceil(killer.getCurrentItemAttackStrengthDelay()));
        }
        double toll = SkillsHelper.attr(killer, SkillsRegistry.DEATHS_TOLL);
        if (applyingSkillDamage
                || toll <= 0.0
                || !SkillCooldowns.ready(killer, SkillCooldowns.DEATHS_TOLL)
                || !(killer.level() instanceof ServerLevel level)) {
            return;
        }
        float damage = (float) (victim.getMaxHealth() * toll);
        double radius = CompanionConfig.DEATHS_TOLL_RADIUS.get();
        DamageSource source = level.damageSources().playerAttack(killer);
        applyingSkillDamage = true;
        try {
            for (LivingEntity nearby : level.getEntitiesOfClass(
                    LivingEntity.class,
                    victim.getBoundingBox().inflate(radius),
                    entity -> entity != victim
                            && entity.distanceToSqr(victim) <= radius * radius
                            && AllyCheck.isHostileTarget(killer, entity))) {
                hurtIgnoringCooldown(level, nearby, source, damage);
            }
        } finally {
            applyingSkillDamage = false;
        }
        SkillCooldowns.start(killer, SkillCooldowns.DEATHS_TOLL, CompanionConfig.DEATHS_TOLL_COOLDOWN.get());
        Vec3 center = victim.getBoundingBox().getCenter();
        level.sendParticles(
                ParticleTypes.SOUL, center.x, center.y, center.z, 30, radius / 3.0, 0.5, radius / 3.0, 0.05);
        level.playSound(null, center.x, center.y, center.z, SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 1.5F, 0.8F);
    }

    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        if (!(event.getAttackingPlayer() instanceof ServerPlayer player)
                || !SkillsHelper.isScythe(player.getMainHandItem())) {
            return;
        }
        double tithe = SkillsHelper.attr(player, SkillsRegistry.SOUL_TITHE);
        if (tithe > 0.0) {
            event.setDroppedExperience((int) Math.round(event.getDroppedExperience() * (1.0 + tithe)));
        }
    }

    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (projectile.hasData(SkillsRegistry.HOMING_STATE)
                && projectile.getData(SkillsRegistry.HOMING_STATE).volleyCopy
                && projectile.level() instanceof ServerLevel level) {
            if (event.getRayTraceResult().getType() == HitResult.Type.ENTITY) {
                level.getServer().schedule(new TickTask(level.getServer().getTickCount() + 1, projectile::discard));
            } else {
                event.setCanceled(true);
                projectile.discard();
            }
            return;
        }
        if (event.getProjectile() instanceof AbstractArrow arrow
                && arrow.getOwner() instanceof ServerPlayer player
                && event.getRayTraceResult().getType() == HitResult.Type.BLOCK) {
            if (tryHomingRedirect(arrow, player)) {
                event.setCanceled(true);
                return;
            }
            player.getData(SkillsRegistry.COMBAT_STATE).archerRampStacks = 0;
        }
    }

    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide()
                && !event.loadedFromDisk()
                && event.getEntity() instanceof Projectile projectile
                && SkillsHelper.isThrownProjectile(projectile)) {
            if (projectile.getOwner() instanceof ServerPlayer thrower
                    && event.getLevel() instanceof ServerLevel level
                    && !(projectile.hasData(SkillsRegistry.HOMING_STATE)
                            && projectile.getData(SkillsRegistry.HOMING_STATE).skillSpawned)) {
                applyThrownPerks(level, thrower, projectile);
            }
            return;
        }
        if (event.getLevel().isClientSide()
                || !(event.getEntity() instanceof AbstractArrow arrow)
                || arrow instanceof ThrownTrident
                || !(arrow.getOwner() instanceof ServerPlayer shooter)
                || (arrow.hasData(SkillsRegistry.HOMING_STATE)
                        && arrow.getData(SkillsRegistry.HOMING_STATE).skillSpawned)) {
            return;
        }
        ItemStack weapon = arrow.getWeaponItem();
        if (weapon != null && SkillsHelper.isCrossbow(weapon)) {
            applyCrossbowPerks(shooter, arrow);
        }
        if (SkillsHelper.attr(shooter, SkillsRegistry.HOMING_ARROWS) > 0.0) {
            LivingEntity target = findHomingTarget(shooter, arrow);
            if (target != null) {
                arrow.getData(SkillsRegistry.HOMING_STATE).targetId = target.getUUID();
            }
        }
    }

    private static void applyThrownPerks(ServerLevel level, ServerPlayer thrower, Projectile projectile) {
        double velocity = SkillsHelper.attr(thrower, SkillsRegistry.THROWN_VELOCITY);
        if (velocity > 0.0) {
            projectile.setDeltaMovement(projectile.getDeltaMovement().scale(1.0 + velocity));
            projectile.hurtMarked = true;
        }
        int runThrough = (int) SkillsHelper.attr(thrower, SkillsRegistry.RUN_THROUGH);
        if (runThrough > 0 && projectile instanceof AbstractArrow arrow) {
            ((AbstractArrowInvoker) arrow)
                    .ftbevo$setPierceLevel((byte) Math.min(Byte.MAX_VALUE, arrow.getPierceLevel() + runThrough));
        }
        double volley = SkillsHelper.attr(thrower, SkillsRegistry.VOLLEY);
        if (volley > 0.0 && thrower.getRandom().nextDouble() < volley) {
            throwVolley(level, projectile);
        }
    }

    private static void throwVolley(ServerLevel level, Projectile original) {
        Vec3 motion = original.getDeltaMovement();
        double speed = motion.length();
        if (speed < 1.0E-4) {
            return;
        }
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
        original.saveWithoutId(output);
        CompoundTag data = output.buildResult();
        data.remove("UUID");
        int extra = CompanionConfig.VOLLEY_EXTRA.get();
        double spread = CompanionConfig.VOLLEY_SPREAD_DEGREES.get();
        for (int i = 0; i < extra; i++) {
            double angle = Math.toRadians(spread * (i / 2 + 1) * (i % 2 == 0 ? 1 : -1));
            Entity loaded = EntityType.loadEntityRecursive(
                    original.getType(), data.copy(), level, EntitySpawnReason.TRIGGERED, EntityProcessor.NOP);
            if (!(loaded instanceof Projectile copy)) {
                continue;
            }
            HomingState state = copy.getData(SkillsRegistry.HOMING_STATE);
            state.skillSpawned = true;
            state.volleyCopy = true;
            if (copy instanceof AbstractArrow arrow) {
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            Vec3 direction = motion.yRot((float) angle);
            copy.setPos(original.position());
            copy.shoot(direction.x, direction.y, direction.z, (float) speed, 0.0F);
            level.addFreshEntity(copy);
        }
        level.playSound(
                null,
                original.getX(),
                original.getY(),
                original.getZ(),
                SoundEvents.TRIDENT_THROW.value(),
                SoundSource.PLAYERS,
                1.0F,
                1.2F);
    }

    private static void ricochet(
            ServerLevel level,
            ServerPlayer attacker,
            Entity projectile,
            LivingEntity first,
            float damage,
            int bounces) {
        float bounceDamage = (float) (damage * CompanionConfig.RICOCHET_DAMAGE_FRACTION.get());
        if (bounceDamage <= 0.0F) {
            return;
        }
        double radius = CompanionConfig.RICOCHET_RADIUS.get();
        DamageSource source = level.damageSources().thrown(projectile, attacker);
        Set<LivingEntity> hit = new HashSet<>();
        hit.add(first);
        LivingEntity from = first;
        applyingSkillDamage = true;
        try {
            for (int i = 0; i < bounces; i++) {
                LivingEntity origin = from;
                LivingEntity next = null;
                double best = radius * radius;
                for (LivingEntity candidate : level.getEntitiesOfClass(
                        LivingEntity.class,
                        origin.getBoundingBox().inflate(radius),
                        entity -> entity.isAlive()
                                && !hit.contains(entity)
                                && AllyCheck.isHostileTarget(attacker, entity))) {
                    double distance = candidate.distanceToSqr(origin);
                    if (distance < best) {
                        best = distance;
                        next = candidate;
                    }
                }
                if (next == null) {
                    break;
                }
                hit.add(next);
                Vec3 start = origin.getBoundingBox().getCenter();
                Vec3 end = next.getBoundingBox().getCenter();
                Vec3 step = end.subtract(start).scale(0.125);
                for (int p = 1; p <= 8; p++) {
                    Vec3 point = start.add(step.scale(p));
                    level.sendParticles(ParticleTypes.CRIT, point.x, point.y, point.z, 1, 0.0, 0.0, 0.0, 0.0);
                }
                hurtIgnoringCooldown(level, next, source, bounceDamage);
                level.playSound(null, end.x, end.y, end.z, SoundEvents.TRIDENT_HIT, SoundSource.PLAYERS, 0.8F, 1.3F);
                from = next;
            }
        } finally {
            applyingSkillDamage = false;
        }
    }

    private static void applyCrossbowPerks(ServerPlayer shooter, AbstractArrow arrow) {
        CombatState state = shooter.getData(SkillsRegistry.COMBAT_STATE);
        long now = shooter.level().getGameTime();
        if (now != state.lastCrossbowShotTick) {
            state.lastCrossbowShotTick = now;
            state.crossbowShotCounter++;
        }
        if (SkillsHelper.attr(shooter, SkillsRegistry.POWER_SHOT) > 0.0
                && state.crossbowShotCounter % CompanionConfig.POWER_SHOT_INTERVAL.get() == 0) {
            arrow.getData(SkillsRegistry.HOMING_STATE).powerShot = true;
        }
        int impale = (int) SkillsHelper.attr(shooter, SkillsRegistry.IMPALE);
        if (impale > 0) {
            ((AbstractArrowInvoker) arrow)
                    .ftbevo$setPierceLevel((byte) Math.min(Byte.MAX_VALUE, arrow.getPierceLevel() + impale));
        }
        double ballista = SkillsHelper.attr(shooter, SkillsRegistry.BALLISTA);
        if (ballista > 0.0) {
            arrow.setDeltaMovement(arrow.getDeltaMovement().scale(1.0 + ballista));
            arrow.hurtMarked = true;
        }
    }

    private static void spawnRainOfArrows(ServerLevel level, ServerPlayer shooter, LivingEntity target) {
        int count = CompanionConfig.RAIN_OF_ARROWS_COUNT.get();
        double radius = CompanionConfig.RAIN_OF_ARROWS_RADIUS.get();
        double height = CompanionConfig.RAIN_OF_ARROWS_HEIGHT.get();
        Vec3 center = target.getBoundingBox().getCenter();
        for (int i = 0; i < count; i++) {
            double angle = Math.PI * 2.0 * i / count;
            double x = target.getX() + Math.cos(angle) * radius;
            double y = target.getY() + height;
            double z = target.getZ() + Math.sin(angle) * radius;
            Arrow arrow = new Arrow(level, x, y, z, new ItemStack(Items.ARROW), null);
            arrow.setOwner(shooter);
            arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            arrow.getData(SkillsRegistry.HOMING_STATE).skillSpawned = true;
            Vec3 direction = center.subtract(x, y, z);
            if (direction.lengthSqr() < 1.0E-4) {
                direction = new Vec3(0.0, -1.0, 0.0);
            }
            arrow.setDeltaMovement(direction.normalize().scale(1.6));
            level.addFreshEntity(arrow);
        }
        level.playSound(
                null,
                target.getX(),
                target.getY(),
                target.getZ(),
                SoundEvents.CROSSBOW_SHOOT,
                SoundSource.PLAYERS,
                1.0F,
                0.8F);
    }

    private static LivingEntity findHomingTarget(ServerPlayer shooter, AbstractArrow arrow) {
        Vec3 direction = arrow.getDeltaMovement();
        if (direction.lengthSqr() < 1.0E-4) {
            direction = shooter.getLookAngle();
        }
        direction = direction.normalize();
        Vec3 start = arrow.position();
        double range = 48.0;
        AABB search =
                arrow.getBoundingBox().expandTowards(direction.scale(range)).inflate(8.0);
        LivingEntity best = null;
        double bestDot = 0.9;
        for (LivingEntity candidate : shooter.level()
                .getEntitiesOfClass(
                        LivingEntity.class,
                        search,
                        entity -> entity != shooter
                                && entity.isAlive()
                                && !entity.isSpectator()
                                && !(entity instanceof Player other && !shooter.canHarmPlayer(other)))) {
            Vec3 toCandidate = candidate.getBoundingBox().getCenter().subtract(start);
            double distance = toCandidate.length();
            if (distance < 1.0E-4 || distance > range) {
                continue;
            }
            double dot = toCandidate.normalize().dot(direction);
            if (dot > bestDot) {
                bestDot = dot;
                best = candidate;
            }
        }
        return best;
    }

    private static boolean tryHomingRedirect(AbstractArrow arrow, ServerPlayer player) {
        if (SkillsHelper.attr(player, SkillsRegistry.HOMING_ARROWS) <= 0.0
                || !arrow.hasData(SkillsRegistry.HOMING_STATE)
                || !(arrow.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        HomingState state = arrow.getData(SkillsRegistry.HOMING_STATE);
        if (state.targetId == null || state.retargets >= 3) {
            return false;
        }
        if (!(serverLevel.getEntity(state.targetId) instanceof LivingEntity target)
                || !target.isAlive()
                || arrow.distanceTo(target) > 64.0) {
            return false;
        }
        state.retargets++;
        double speed = Math.max(arrow.getDeltaMovement().length(), 1.0);
        Vec3 toTarget = target.getBoundingBox().getCenter().subtract(arrow.position());
        double horizontal = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
        Vec3 aimed = new Vec3(toTarget.x, toTarget.y + horizontal * 0.1, toTarget.z);
        if (aimed.lengthSqr() < 1.0E-4) {
            return false;
        }
        arrow.setDeltaMovement(aimed.normalize().scale(speed));
        arrow.hurtMarked = true;
        return true;
    }

    public static void onShieldBlock(LivingShieldBlockEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !event.getBlocked()) {
            return;
        }
        double stunChance = SkillsHelper.attr(player, SkillsRegistry.SHIELD_STUN);
        if (stunChance > 0.0
                && SkillCooldowns.ready(player, SkillCooldowns.SHIELD_STUN)
                && event.getDamageSource().getDirectEntity() instanceof LivingEntity attacker
                && attacker != player
                && player.getRandom().nextDouble() < stunChance) {
            attacker.addEffect(
                    new MobEffectInstance(SkillsRegistry.STUNNED, CompanionConfig.STUN_DURATION_TICKS.get(), 0),
                    player);
            SkillCooldowns.start(player, SkillCooldowns.SHIELD_STUN, CompanionConfig.SHIELD_STUN_COOLDOWN.get());
        }
        if (SkillsHelper.isSword(player.getUseItem())) {
            player.level()
                    .playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.SHIELD_BLOCK,
                            SoundSource.PLAYERS,
                            1.0F,
                            1.0F);
            if (SkillsHelper.attr(player, SkillsRegistry.RIPOSTE) > 0.0
                    && player.getTicksUsingItem() <= CompanionConfig.RIPOSTE_PARRY_WINDOW.get()
                    && SkillCooldowns.ready(player, SkillCooldowns.RIPOSTE)
                    && event.getDamageSource().getDirectEntity() instanceof LivingEntity meleeAttacker
                    && meleeAttacker != player) {
                meleeAttacker.addEffect(
                        new MobEffectInstance(SkillsRegistry.STUNNED, CompanionConfig.STUN_DURATION_TICKS.get(), 0),
                        player);
                player.getData(SkillsRegistry.COMBAT_STATE).riposteReadyUntil =
                        player.level().getGameTime() + CompanionConfig.RIPOSTE_BUFF_WINDOW.get();
                SkillCooldowns.start(player, SkillCooldowns.RIPOSTE, CompanionConfig.RIPOSTE_COOLDOWN.get());
                player.sendOverlayMessage(Component.translatable("ftbevolutioncompanion.skills.riposte.ready"));
            }
        }
    }

    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        CombatState combatState = player.getData(SkillsRegistry.COMBAT_STATE);
        if (combatState.lancerLeapPending) {
            combatState.lancerLeapPending = false;
            event.setDamageMultiplier(0.0F);
            if (SkillsHelper.isSpear(player.getMainHandItem())) {
                lancerSmash(serverLevel, player, event.getDistance());
            }
            return;
        }
        double attrValue = SkillsHelper.attr(player, SkillsRegistry.GROUND_SLAM);
        if (attrValue <= 0.0 || event.getDistance() < CompanionConfig.GROUND_SLAM_MIN_FALL.get()) {
            return;
        }
        double safe = player.getAttributeValue(Attributes.SAFE_FALL_DISTANCE);
        float fallDamage = (float) (Math.ceil(event.getDistance() - safe) * event.getDamageMultiplier());
        if (fallDamage <= 0.0F) {
            return;
        }
        float slamDamage = (float) (fallDamage * attrValue);
        double radius = CompanionConfig.GROUND_SLAM_RADIUS.get();
        for (LivingEntity victim : serverLevel.getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(radius, 2.0, radius),
                entity -> entity != player && entity.isAlive())) {
            victim.hurtServer(serverLevel, serverLevel.damageSources().playerAttack(player), slamDamage);
            victim.knockback(0.5 + attrValue, player.getX() - victim.getX(), player.getZ() - victim.getZ());
        }
        serverLevel.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.MACE_SMASH_GROUND,
                SoundSource.PLAYERS,
                1.0F,
                1.0F);
    }

    private static void lancerSmash(ServerLevel level, ServerPlayer player, double fallDistance) {
        double bonus;
        if (fallDistance <= 3.0) {
            bonus = 4.0 * fallDistance;
        } else if (fallDistance <= 8.0) {
            bonus = 12.0 + 2.0 * (fallDistance - 3.0);
        } else {
            bonus = 22.0 + fallDistance - 8.0;
        }
        double damage = (player.getAttributeValue(Attributes.ATTACK_DAMAGE) + bonus)
                * (1.0 + SkillsHelper.attr(player, SkillsRegistry.SPEAR_DAMAGE))
                * (1.0 + SkillsHelper.attr(player, SkillsRegistry.LANCER_DAMAGE))
                * CompanionConfig.LANCER_DAMAGE_SCALE.get();
        double radius = SkillsHelper.attr(player, SkillsRegistry.LANCER_RADIUS);
        double edge = CompanionConfig.LANCER_EDGE_DAMAGE.get();
        Vec3 impact = player.position();
        AABB search = radius > 0.0
                ? new AABB(impact, impact).inflate(radius, 2.0, radius)
                : player.getBoundingBox().inflate(0.25);
        DamageSource source = level.damageSources().playerAttack(player);
        double knockbackRadius = Math.max(radius, 1.5);
        applyingSkillDamage = true;
        try {
            for (LivingEntity victim : level.getEntitiesOfClass(
                    LivingEntity.class, search, entity -> AllyCheck.isHostileTarget(player, entity))) {
                Vec3 offset = victim.position().subtract(impact);
                double distance = Math.sqrt(offset.x * offset.x + offset.z * offset.z);
                double falloff = 1.0;
                if (radius > 0.0) {
                    if (distance > radius) {
                        continue;
                    }
                    falloff = 1.0 - (1.0 - edge) * (distance / radius);
                }
                if (!hurtIgnoringCooldown(level, victim, source, (float) (damage * falloff))) {
                    continue;
                }
                double power = 0.7
                        * (knockbackRadius - distance)
                        * (1.0 - victim.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                if (power > 0.0 && distance > 1.0E-4) {
                    victim.push(offset.x / distance * power, 0.7, offset.z / distance * power);
                    if (victim instanceof ServerPlayer hitPlayer) {
                        hitPlayer.connection.send(new ClientboundSetEntityMotionPacket(hitPlayer));
                    }
                }
            }
        } finally {
            applyingSkillDamage = false;
        }
        level.levelEvent(2013, player.getOnPos(), 750);
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.MACE_SMASH_GROUND_HEAVY,
                SoundSource.PLAYERS,
                1.0F,
                1.0F);
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % 10 != 0) {
            return;
        }
        CombatState state = player.getData(SkillsRegistry.COMBAT_STATE);
        long now = player.level().getGameTime();

        int protection = CompanionConfig.LANCER_PROTECTION_TICKS.get();
        if (state.lancerLeapPending && protection > 0 && now - state.lancerLeapStart > protection) {
            state.lancerLeapPending = false;
        }

        if (state.fasterStrikesStacks > 0
                && now - state.lastUnarmedHitTime > CompanionConfig.FASTER_STRIKES_TIMEOUT.get()) {
            state.fasterStrikesStacks = 0;
        }
        if (state.unarmedRampStacks > 0
                && now - state.lastUnarmedHitTime > CompanionConfig.FASTER_STRIKES_TIMEOUT.get()) {
            state.unarmedRampStacks = 0;
        }
        if (state.archerRampStacks > 0 && now - state.lastArcherHitTime > CompanionConfig.ARCHER_RAMP_TIMEOUT.get()) {
            state.archerRampStacks = 0;
        }

        double fasterStrikes = SkillsHelper.attr(player, SkillsRegistry.FASTER_STRIKES);
        boolean fasterActive = fasterStrikes > 0.0
                && state.fasterStrikesStacks > 0
                && SkillsAbilities.toggles(player).fasterStrikes();
        applyTransient(
                player.getAttribute(Attributes.ATTACK_SPEED),
                FASTER_STRIKES_ID,
                fasterStrikes * state.fasterStrikesStacks,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                fasterActive);

        double mastery = SkillsHelper.attr(player, SkillsRegistry.SHIELD_MASTERY);
        boolean masteryActive = mastery > 0.0 && player.isBlocking();
        applyTransient(
                player.getAttribute(Attributes.ARMOR),
                SHIELD_MASTERY_ARMOR_ID,
                mastery * 2.0,
                AttributeModifier.Operation.ADD_VALUE,
                masteryActive);
        applyTransient(
                player.getAttribute(Attributes.ARMOR_TOUGHNESS),
                SHIELD_MASTERY_TOUGHNESS_ID,
                mastery * 0.5,
                AttributeModifier.Operation.ADD_VALUE,
                masteryActive);

        double desperation = SkillsHelper.attr(player, SkillsRegistry.AXE_DESPERATION);
        boolean desperationActive = desperation > 0.0
                && SkillsHelper.isAxe(player.getMainHandItem())
                && SkillsHelper.healthFraction(player) < CompanionConfig.AXE_LIFESTEAL_THRESHOLD.get();
        ApothicHooks.lifeSteal()
                .ifPresent(lifeSteal -> applyTransient(
                        player.getAttribute(lifeSteal),
                        AXE_DESPERATION_ID,
                        desperation,
                        AttributeModifier.Operation.ADD_VALUE,
                        desperationActive));

        double recovery = SkillsHelper.attr(player, SkillsRegistry.SHIELD_RECOVERY);
        if (recovery > 0.0
                && SkillsHelper.isShield(player.getOffhandItem())
                && now - state.lastShieldHealTime >= CompanionConfig.SHIELD_HEAL_INTERVAL_TICKS.get()
                && player.getHealth() < player.getMaxHealth()) {
            state.lastShieldHealTime = now;
            player.heal((float) (recovery * player.getMaxHealth()));
        }

        if (player.isBlocking()
                && SkillsHelper.attr(player, SkillsRegistry.LIGHTS_SHIELD) > 0.0
                && SkillsHelper.healthFraction(player) < CompanionConfig.LIGHTS_SHIELD_THRESHOLD.get()
                && SkillCooldowns.ready(player, SkillCooldowns.LIGHTS_SHIELD)) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.RESISTANCE,
                    CompanionConfig.LIGHTS_SHIELD_RESISTANCE_TICKS.get(),
                    CompanionConfig.LIGHTS_SHIELD_RESISTANCE_AMPLIFIER.get(),
                    false,
                    false,
                    true));
            SkillCooldowns.start(player, SkillCooldowns.LIGHTS_SHIELD, CompanionConfig.LIGHTS_SHIELD_COOLDOWN.get());
            player.level()
                    .playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.BEACON_ACTIVATE,
                            SoundSource.PLAYERS,
                            1.0F,
                            1.5F);
        }
    }

    private static void applyTransient(
            AttributeInstance instance,
            Identifier id,
            double amount,
            AttributeModifier.Operation operation,
            boolean active) {
        if (instance == null) {
            return;
        }
        if (active) {
            instance.addOrUpdateTransientModifier(new AttributeModifier(id, amount, operation));
        } else if (instance.hasModifier(id)) {
            instance.removeModifier(id);
        }
    }

    public static void onLivingVisibility(LivingEvent.LivingVisibilityEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        double stealth = SkillsHelper.attr(player, SkillsRegistry.STEALTH);
        if (player instanceof ServerPlayer serverPlayer
                && serverPlayer.level().getGameTime() < serverPlayer.getData(SkillsRegistry.COMBAT_STATE).ninjaUntil) {
            stealth = 1.0;
        }
        if (stealth > 0.0) {
            event.modifyVisibility(Math.max(0.0, 1.0 - stealth));
        }
    }

    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        if (player.hasEffect(SkillsRegistry.STUNNED)) {
            event.setCanceled(true);
            return;
        }
        if (!(player instanceof ServerPlayer serverPlayer)
                || !(event.getTarget() instanceof LivingEntity target)
                || !serverPlayer.isShiftKeyDown()
                || !SkillsHelper.isSword(serverPlayer.getMainHandItem())
                || SkillsHelper.attr(serverPlayer, SkillsRegistry.SHADOW_STEP) <= 0.0
                || !SkillsAbilities.toggles(serverPlayer).shadowStep()
                || !SkillCooldowns.ready(serverPlayer, SkillCooldowns.SHADOW_STEP)) {
            return;
        }
        boolean inMeleeReach = SkillsAbilities.isWithinMeleeReach(serverPlayer, target);
        if (SkillsAbilities.shadowStep(serverPlayer, target) && !inMeleeReach) {
            event.setCanceled(true);
        }
    }

    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer killer)
                || !SkillsHelper.isSword(killer.getMainHandItem())) {
            return;
        }
        double chance = SkillsHelper.attr(killer, SkillsRegistry.SHAKEDOWN);
        if (chance <= 0.0 || event.getDrops().isEmpty() || killer.getRandom().nextDouble() >= chance) {
            return;
        }
        int index = killer.getRandom().nextInt(event.getDrops().size());
        ItemEntity picked = event.getDrops().stream().skip(index).findFirst().orElse(null);
        if (picked != null) {
            event.getDrops()
                    .add(new ItemEntity(
                            picked.level(),
                            picked.getX(),
                            picked.getY(),
                            picked.getZ(),
                            picked.getItem().copy()));
        }
    }
}
