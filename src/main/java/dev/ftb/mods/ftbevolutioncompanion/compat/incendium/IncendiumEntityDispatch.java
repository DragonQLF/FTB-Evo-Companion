package dev.ftb.mods.ftbevolutioncompanion.compat.incendium;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class IncendiumEntityDispatch {
    private static final TagKey<EntityType<?>> MOBS = tag("mobs");
    private static final TagKey<EntityType<?>> MOBS_NO_PLAYER = tag("mobs_no_player");
    private static final TagKey<EntityType<?>> OTHER = tag("other");

    private static final Identifier MOB_INIT = Identifier.fromNamespaceAndPath("incendium", "entity/mob/init");
    private static final Identifier MOB_MAIN = Identifier.fromNamespaceAndPath("incendium", "entity/mob/main");
    private static final Identifier EID_CHECK =
            Identifier.fromNamespaceAndPath("incendium", "technical/entity_id/check");
    private static final Identifier FROZEN = Identifier.fromNamespaceAndPath("ftb", "incendium/frozen");
    private static final Identifier OTHER_INIT = Identifier.fromNamespaceAndPath("incendium", "entity/other/init");
    private static final Identifier OTHER_MAIN = Identifier.fromNamespaceAndPath("incendium", "entity/other/main");

    private static final String CHECKED = "in.checked";
    private static final String TICKING = "in.ticking_entity";
    private static final ScoreHolder JAVA_FLAG = ScoreHolder.forNameOnly("$ftb.java");
    private static final ScoreHolder DISPATCH_FLAG = ScoreHolder.forNameOnly("$ftb.dispatch");
    private static final ScoreHolder TOGGLE = ScoreHolder.forNameOnly("$toggle");
    private static final int EID_CHECK_INTERVAL = 20;

    private static int eidCounter;

    private IncendiumEntityDispatch() {}

    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        Scoreboard scoreboard = server.getScoreboard();
        Objective dummy = scoreboard.getObjective("in.dummy");
        if (dummy == null) {
            return;
        }
        scoreboard.getOrCreatePlayerScore(JAVA_FLAG, dummy).set(1);
        if (score(scoreboard, DISPATCH_FLAG, dummy) != 1) {
            return;
        }
        scoreboard.getOrCreatePlayerScore(DISPATCH_FLAG, dummy).set(0);

        Objective constants = scoreboard.getObjective("in.constants");
        Objective eid = scoreboard.getObjective("in.eid");
        Objective frozen = scoreboard.getObjective("in.frozen");
        boolean toggle = constants != null && score(scoreboard, TOGGLE, constants) == 1;
        boolean eidTick = eidCounter++ % EID_CHECK_INTERVAL == 0;

        List<Entity> mobs = new ArrayList<>();
        List<Entity> mobsWithPlayers = new ArrayList<>();
        List<Entity> others = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity.is(MOBS)) {
                    mobsWithPlayers.add(entity);
                    if (entity.is(MOBS_NO_PLAYER)) {
                        mobs.add(entity);
                    }
                }
                if (entity.is(OTHER)) {
                    others.add(entity);
                }
            }
        }

        ServerFunctionManager functions = server.getFunctions();
        if (toggle) {
            run(functions, MOB_INIT, mobs, e -> !e.entityTags().contains(CHECKED), true);
        }
        if (eidTick) {
            run(functions, EID_CHECK, mobs, e -> !inRange(scoreboard, e, eid, 0, 32767), false);
        }
        run(functions, MOB_MAIN, mobs, e -> e.entityTags().contains(TICKING), true);
        if (frozen != null) {
            run(functions, FROZEN, mobsWithPlayers, e -> inRange(scoreboard, e, frozen, 1, Integer.MAX_VALUE), true);
        }
        if (toggle) {
            run(functions, OTHER_INIT, others, e -> !e.entityTags().contains(CHECKED), true);
        }
        run(functions, OTHER_MAIN, others, e -> e.entityTags().contains(TICKING), true);
    }

    private static void run(
            ServerFunctionManager functions,
            Identifier id,
            List<Entity> entities,
            Predicate<Entity> filter,
            boolean atEntity) {
        Optional<CommandFunction<CommandSourceStack>> function = functions.get(id);
        if (function.isEmpty()) {
            return;
        }
        CommandSourceStack base = functions.getGameLoopSender();
        for (Entity entity : entities) {
            if (!entity.isAlive() || !filter.test(entity)) {
                continue;
            }
            CommandSourceStack source = base.withEntity(entity);
            if (atEntity && entity.level() instanceof ServerLevel level) {
                source = source.withLevel(level)
                        .withPosition(entity.position())
                        .withRotation(entity.getRotationVector());
            }
            functions.execute(function.get(), source);
        }
    }

    private static boolean inRange(Scoreboard scoreboard, Entity entity, Objective objective, int min, int max) {
        if (objective == null) {
            return false;
        }
        ReadOnlyScoreInfo info = scoreboard.getPlayerScoreInfo(entity, objective);
        return info != null && info.value() >= min && info.value() <= max;
    }

    private static int score(Scoreboard scoreboard, ScoreHolder holder, Objective objective) {
        ReadOnlyScoreInfo info = scoreboard.getPlayerScoreInfo(holder, objective);
        return info == null ? 0 : info.value();
    }

    private static TagKey<EntityType<?>> tag(String path) {
        return TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("incendium", path));
    }
}
