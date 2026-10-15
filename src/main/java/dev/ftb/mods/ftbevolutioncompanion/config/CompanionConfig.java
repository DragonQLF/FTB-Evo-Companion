package dev.ftb.mods.ftbevolutioncompanion.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class CompanionConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue METEORITE_SPACING;
    public static final ModConfigSpec.IntValue METEORITE_SEPARATION;
    public static final ModConfigSpec.BooleanValue NETHER_METEORITE_RELOCATE;
    public static final ModConfigSpec.IntValue NETHER_METEORITE_MIN_Y;
    public static final ModConfigSpec.IntValue NETHER_METEORITE_MAX_Y;
    public static final ModConfigSpec.BooleanValue NETHER_METEORITE_SUPPRESS_CRATER;

    public static final ModConfigSpec.IntValue ODD_BERRY_BUSH_GROWTH_CHANCE;
    public static final ModConfigSpec.BooleanValue ODD_BERRY_BUSH_SPREAD;
    public static final ModConfigSpec.IntValue ODD_BERRY_BUSH_SPREAD_CHANCE;
    public static final ModConfigSpec.IntValue ODD_BERRY_BUSH_MAX_NEARBY;

    public static final ModConfigSpec.DoubleValue AXE_FRENZY_THRESHOLD;
    public static final ModConfigSpec.DoubleValue AXE_LIFESTEAL_THRESHOLD;
    public static final ModConfigSpec.DoubleValue DEATH_BLOW_THRESHOLD;
    public static final ModConfigSpec.DoubleValue LIGHTS_SHIELD_THRESHOLD;
    public static final ModConfigSpec.IntValue CHEAT_DEATH_COOLDOWN;
    public static final ModConfigSpec.IntValue ROOTS_POTENCY_CAP;
    public static final ModConfigSpec.IntValue ROOTS_LILAC_POTENCY_CAP;
    public static final ModConfigSpec.IntValue WITCH_POWER_CAP;
    public static final ModConfigSpec.BooleanValue RITUAL_OWNER_OFFLINE_FULL_COST;
    public static final ModConfigSpec.IntValue CHEAT_DEATH_INVULN_TICKS;
    public static final ModConfigSpec.IntValue LIGHTNING_COOLDOWN;
    public static final ModConfigSpec.DoubleValue LIGHTNING_DAMAGE;
    public static final ModConfigSpec.DoubleValue MULTISHOT_SPREAD;
    public static final ModConfigSpec.BooleanValue MULTISHOT_X_PATTERN;
    public static final ModConfigSpec.DoubleValue EXTRA_JUMP_HEIGHT;
    public static final ModConfigSpec.IntValue SHIELD_STUN_COOLDOWN;
    public static final ModConfigSpec.IntValue SHADOW_STEP_COOLDOWN;
    public static final ModConfigSpec.DoubleValue SHADOW_STEP_RANGE;
    public static final ModConfigSpec.IntValue LIGHTS_SHIELD_COOLDOWN;
    public static final ModConfigSpec.IntValue NINJA_COOLDOWN;
    public static final ModConfigSpec.DoubleValue ARCHER_RAMP_PER_STACK;
    public static final ModConfigSpec.IntValue ARCHER_RAMP_TIMEOUT;
    public static final ModConfigSpec.IntValue FASTER_STRIKES_MAX_STACKS;
    public static final ModConfigSpec.IntValue FASTER_STRIKES_TIMEOUT;
    public static final ModConfigSpec.IntValue UNARMED_RAMP_MAX_STACKS;
    public static final ModConfigSpec.IntValue STUN_DURATION_TICKS;
    public static final ModConfigSpec.IntValue SHIELD_HEAL_INTERVAL_TICKS;
    public static final ModConfigSpec.DoubleValue GROUND_SLAM_RADIUS;
    public static final ModConfigSpec.DoubleValue GROUND_SLAM_MIN_FALL;
    public static final ModConfigSpec.IntValue LIGHTS_SHIELD_RESISTANCE_TICKS;
    public static final ModConfigSpec.IntValue LIGHTS_SHIELD_RESISTANCE_AMPLIFIER;
    public static final ModConfigSpec.DoubleValue BLEED_FRACTION;
    public static final ModConfigSpec.IntValue BLEED_INTERVAL_TICKS;
    public static final ModConfigSpec.IntValue BLEED_DURATION_TICKS;
    public static final ModConfigSpec.IntValue BLEED_MAX_STACKS;
    public static final ModConfigSpec.IntValue POWER_SHOT_INTERVAL;
    public static final ModConfigSpec.DoubleValue VITAL_SHOT_FRACTION;
    public static final ModConfigSpec.IntValue MARKED_DURATION_TICKS;
    public static final ModConfigSpec.DoubleValue MARKED_DAMAGE_BONUS;
    public static final ModConfigSpec.IntValue RAIN_OF_ARROWS_COUNT;
    public static final ModConfigSpec.DoubleValue RAIN_OF_ARROWS_RADIUS;
    public static final ModConfigSpec.DoubleValue RAIN_OF_ARROWS_HEIGHT;
    public static final ModConfigSpec.IntValue PIERCING_STRIKE_INTERVAL;
    public static final ModConfigSpec.IntValue RIPOSTE_PARRY_WINDOW;
    public static final ModConfigSpec.DoubleValue RIPOSTE_DAMAGE_MULT;
    public static final ModConfigSpec.IntValue RIPOSTE_BUFF_WINDOW;
    public static final ModConfigSpec.IntValue RIPOSTE_COOLDOWN;
    public static final ModConfigSpec.IntValue PIN_DURATION_TICKS;
    public static final ModConfigSpec.DoubleValue RICOCHET_RADIUS;
    public static final ModConfigSpec.DoubleValue RICOCHET_DAMAGE_FRACTION;
    public static final ModConfigSpec.IntValue DOOM_COOLDOWN;
    public static final ModConfigSpec.IntValue DOOM_EFFECT_TICKS;
    public static final ModConfigSpec.IntValue DOOM_SLOWNESS_AMPLIFIER;
    public static final ModConfigSpec.IntValue VOLLEY_EXTRA;
    public static final ModConfigSpec.DoubleValue VOLLEY_SPREAD_DEGREES;
    public static final ModConfigSpec.DoubleValue LANCER_LEAP_HEIGHT;
    public static final ModConfigSpec.DoubleValue LANCER_FORWARD_SPEED;
    public static final ModConfigSpec.DoubleValue LANCER_DAMAGE_SCALE;
    public static final ModConfigSpec.DoubleValue LANCER_EDGE_DAMAGE;
    public static final ModConfigSpec.IntValue LANCER_COOLDOWN;
    public static final ModConfigSpec.IntValue LANCER_PROTECTION_TICKS;
    public static final ModConfigSpec.DoubleValue SCYTHE_ARC_RADIUS;
    public static final ModConfigSpec.DoubleValue SCYTHE_ARC_ANGLE;
    public static final ModConfigSpec.IntValue WITHERING_DURATION_TICKS;
    public static final ModConfigSpec.IntValue WITHERING_MAX_STACKS;
    public static final ModConfigSpec.DoubleValue DEATHS_TOLL_RADIUS;
    public static final ModConfigSpec.IntValue DEATHS_TOLL_COOLDOWN;

    public static final ModConfigSpec.BooleanValue ENFORCE_WORLD_SPAWN;
    public static final ModConfigSpec.BooleanValue PYRAMID_BIOME;
    public static final ModConfigSpec.IntValue PYRAMID_MIN_X;
    public static final ModConfigSpec.IntValue PYRAMID_MAX_X;
    public static final ModConfigSpec.IntValue PYRAMID_MIN_Y;
    public static final ModConfigSpec.IntValue PYRAMID_MAX_Y;
    public static final ModConfigSpec.IntValue PYRAMID_MIN_Z;
    public static final ModConfigSpec.IntValue PYRAMID_MAX_Z;
    public static final ModConfigSpec.IntValue SPAWN_X;
    public static final ModConfigSpec.IntValue SPAWN_Y;
    public static final ModConfigSpec.IntValue SPAWN_Z;

    public static final ModConfigSpec.ConfigValue<String> CHALLENGE_CHAPTER;
    public static final ModConfigSpec.IntValue CHALLENGE_REFRESH_SECONDS;
    public static final ModConfigSpec.IntValue CHALLENGE_BOARD_SIZE;
    public static final ModConfigSpec.IntValue CHALLENGE_ANNOUNCE_MINUTES;
    public static final ModConfigSpec.BooleanValue CHALLENGE_ANNOUNCE_ONLY_ON_CHANGE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("World spawn handling.").push("spawn");

        ENFORCE_WORLD_SPAWN = builder.comment(
                        "Put players with no bed or respawn anchor exactly on the world spawn point, on first join and on every respawn.",
                        "Vanilla searches the surface heightmap near the spawn point instead, which puts players on top of the",
                        "spawn pyramid rather than inside it.")
                .define("enforce_world_spawn", true);

        PYRAMID_BIOME = builder.comment(
                        "Generate the ftb:pyramid biome inside the box below during overworld worldgen, so the spawn pyramid",
                        "has no mob spawns or weather without a fillbiome pass. The box must match where the pack places the pyramid.")
                .define("pyramid_biome", true);
        PYRAMID_MIN_X = builder.defineInRange("pyramid_min_x", -62, -30000000, 30000000);
        PYRAMID_MAX_X = builder.defineInRange("pyramid_max_x", 73, -30000000, 30000000);
        PYRAMID_MIN_Y = builder.defineInRange("pyramid_min_y", 220, -64, 319);
        PYRAMID_MAX_Y = builder.defineInRange("pyramid_max_y", 319, -64, 319);
        PYRAMID_MIN_Z = builder.defineInRange("pyramid_min_z", -65, -30000000, 30000000);
        PYRAMID_MAX_Z = builder.defineInRange("pyramid_max_z", 70, -30000000, 30000000);

        SPAWN_X = builder.comment(
                        "World spawn point, the spot inside the pyramid players start at. Set on overworld load so it is",
                        "correct on the very first join, before the structure finishes placing.")
                .defineInRange("spawn_x", 0, -30000000, 30000000);
        SPAWN_Y = builder.defineInRange("spawn_y", 266, -64, 2031);
        SPAWN_Z = builder.defineInRange("spawn_z", 47, -30000000, 30000000);

        builder.pop();

        builder.comment("Challenge boards: the leaderboard screens in the spawn pyramid.")
                .push("challenge");

        CHALLENGE_CHAPTER = builder.comment(
                        "Filename of the FTB Quests chapter the boards rank teams on (config/ftbquests/quests/chapters/<name>.json5).")
                .define("chapter", "pyramid_challenge");

        CHALLENGE_REFRESH_SECONDS = builder.comment(
                        "How often the server re-ranks the teams and pushes the result to every client.")
                .defineInRange("refresh_seconds", 60, 5, 3600);

        CHALLENGE_BOARD_SIZE = builder.comment(
                        "How many ranks the leaderboard tracks. A board set to a rank above this never shows a team.")
                .defineInRange("board_size", 8, 1, 64);

        CHALLENGE_ANNOUNCE_MINUTES = builder.comment(
                        "Minutes between leaderboard announcements in chat. An announcement names each team whose rank changed",
                        "since the last one. 0 announces as soon as a leaderboard refresh finds a change; -1 turns announcements off.")
                .defineInRange("announce_interval_minutes", 60, -1, 10080);

        CHALLENGE_ANNOUNCE_ONLY_ON_CHANGE = builder.comment(
                        "Only announce when at least one rank changed since the last announcement.",
                        "When false, every interval posts the whole leaderboard, marking the ranks that moved.")
                .define("announce_only_on_change", true);

        builder.pop();

        builder.comment("Applied Energistics 2 meteorite worldgen.").push("meteorites");

        METEORITE_SPACING = builder.comment("Average distance in chunks between meteorites. AE2's own default is 32.")
                .defineInRange("spacing", 48, 1, 4096);

        METEORITE_SEPARATION = builder.comment(
                        "Minimum distance in chunks between meteorites. Must be less than spacing.")
                .defineInRange("separation", 12, 0, 4095);

        NETHER_METEORITE_RELOCATE = builder.comment(
                        "Place meteorites in a low Y band near bedrock instead of on the world surface heightmap.",
                        "Without this, Nether meteorites bury themselves in the ceiling because the world-surface",
                        "heightmap returns the roof rather than the walkable floor.")
                .define("relocate_in_nether", true);

        NETHER_METEORITE_MIN_Y = builder.comment("Lowest Y the core of a Nether meteorite may be placed at.")
                .defineInRange("nether_min_y", 8, -64, 320);

        NETHER_METEORITE_MAX_Y = builder.comment(
                        "Highest Y the core of a Nether meteorite may be placed at.",
                        "The Nether lava sea sits at Y 31, so staying below that keeps meteorites buried under it.")
                .defineInRange("nether_max_y", 24, -64, 320);

        NETHER_METEORITE_SUPPRESS_CRATER = builder.comment(
                        "Skip crater excavation and decay for meteorites in dimensions with a ceiling.",
                        "AE2 clears every non-bedrock block from the impact site up to the dimension height limit,",
                        "which in the Nether would carve a shaft through the lava sea and the ceiling.")
                .define("suppress_crater_in_ceiling_dimensions", true);

        builder.pop();

        builder.comment(
                        "Odd Berry Bush, the Roots Classic berry source that replaces berries dropping from leaves.",
                        "A bush placed by a player is tended and spreads; right clicking it toggles that off again.")
                .push("odd_berry_bush");

        ODD_BERRY_BUSH_GROWTH_CHANCE = builder.comment(
                        "A bush advances one growth stage on 1 in this many random ticks, in light level 9 or brighter.",
                        "Higher is slower. Bone meal always advances a stage.")
                .defineInRange("growth_chance", 12, 1, 4096);

        ODD_BERRY_BUSH_SPREAD = builder.comment(
                        "Allow tended bushes to spread onto nearby blocks in the",
                        "ftbevolutioncompanion:odd_berry_bush_spreadable tag.")
                .define("spread", true);

        ODD_BERRY_BUSH_SPREAD_CHANCE = builder.comment(
                        "A tended bush attempts to spread on 1 in this many random ticks. Higher is slower.")
                .defineInRange("spread_chance", 25, 1, 4096);

        ODD_BERRY_BUSH_MAX_NEARBY = builder.comment(
                        "Bushes allowed in the 9x3x9 area around a bush before it stops spreading.")
                .defineInRange("max_nearby", 5, 1, 256);

        builder.pop();

        builder.comment(
                        "Skill node mechanics. Magnitudes come from the ftb: attributes granted by the skill tree;",
                        "these settings control conditions, timings, and stack caps.")
                .push("skills");

        AXE_FRENZY_THRESHOLD = builder.comment(
                        "Health fraction the player must be below for the axe frenzy damage bonus.")
                .defineInRange("axe_frenzy_threshold", 0.50, 0.0, 1.0);

        AXE_LIFESTEAL_THRESHOLD = builder.comment(
                        "Health fraction the player must be below for axe desperation lifesteal.")
                .defineInRange("axe_lifesteal_threshold", 0.40, 0.0, 1.0);

        DEATH_BLOW_THRESHOLD = builder.comment("Target health fraction below which death blow bonus damage applies.")
                .defineInRange("death_blow_threshold", 0.25, 0.0, 1.0);

        LIGHTS_SHIELD_THRESHOLD = builder.comment("Health fraction below which Light's Shield triggers while blocking.")
                .defineInRange("lights_shield_threshold", 0.25, 0.0, 1.0);

        CHEAT_DEATH_COOLDOWN = builder.comment("Cooldown in ticks after cheat death triggers.")
                .defineInRange("cheat_death_cooldown", 2400, 0, 1728000);

        CHEAT_DEATH_INVULN_TICKS = builder.comment(
                        "Duration in ticks of the resistance granted when cheat death triggers.")
                .defineInRange("cheat_death_invuln_ticks", 60, 0, 12000);

        LIGHTNING_COOLDOWN = builder.comment("Cooldown in ticks between lightning strike procs.")
                .defineInRange("lightning_cooldown", 100, 0, 1728000);

        LIGHTNING_DAMAGE = builder.comment(
                        "Damage dealt by a Storm Caller strike to each entity in range. Strikes do not set fires.")
                .defineInRange("lightning_damage", 5.0, 0.0, 1024.0);

        SHADOW_STEP_COOLDOWN = builder.comment("Cooldown in ticks between shadow step teleports.")
                .defineInRange("shadow_step_cooldown", 200, 0, 1728000);

        SHADOW_STEP_RANGE = builder.comment("Maximum distance in blocks a shadow step can reach a target.")
                .defineInRange("shadow_step_range", 10.0, 1.0, 64.0);

        MULTISHOT_SPREAD = builder.comment(
                        "Maximum fan half-angle in degrees for multishot volleys. Vanilla multishot uses 10.")
                .defineInRange("multishot_spread", 3.0, 0.0, 45.0);

        MULTISHOT_X_PATTERN = builder.comment(
                        "Arrange multishot volleys in an X instead of the vanilla horizontal fan.")
                .define("multishot_x_pattern", true);

        EXTRA_JUMP_HEIGHT = builder.comment("Height of mid-air extra jumps as a multiple of a normal jump.")
                .defineInRange("extra_jump_height", 2.0, 0.5, 8.0);

        SHIELD_STUN_COOLDOWN = builder.comment("Cooldown in ticks between shield stun procs, to stop stun locking.")
                .defineInRange("shield_stun_cooldown", 100, 0, 1728000);

        LIGHTS_SHIELD_COOLDOWN = builder.comment("Internal cooldown in ticks for Light's Shield.")
                .defineInRange("lights_shield_cooldown", 600, 0, 1728000);

        NINJA_COOLDOWN = builder.comment("Cooldown in ticks for the ninja stealth activation.")
                .defineInRange("ninja_cooldown", 1200, 0, 1728000);

        ARCHER_RAMP_PER_STACK = builder.comment("Bonus damage fraction per ramping shots stack.")
                .defineInRange("archer_ramp_per_stack", 0.10, 0.0, 10.0);

        ARCHER_RAMP_TIMEOUT = builder.comment("Ticks without an arrow hit before ramping shots stacks reset.")
                .defineInRange("archer_ramp_timeout", 200, 1, 72000);

        FASTER_STRIKES_MAX_STACKS = builder.comment("Maximum faster strikes attack speed stacks.")
                .defineInRange("faster_strikes_max_stacks", 5, 1, 100);

        FASTER_STRIKES_TIMEOUT = builder.comment("Ticks without an unarmed hit before faster strikes stacks reset.")
                .defineInRange("faster_strikes_timeout", 200, 1, 72000);

        UNARMED_RAMP_MAX_STACKS = builder.comment("Maximum unarmed ramp damage stacks.")
                .defineInRange("unarmed_ramp_max_stacks", 10, 1, 100);

        STUN_DURATION_TICKS = builder.comment("Duration in ticks of the stun applied by shield bash.")
                .defineInRange("stun_duration_ticks", 30, 1, 12000);

        SHIELD_HEAL_INTERVAL_TICKS = builder.comment("Interval in ticks between shield recovery heals.")
                .defineInRange("shield_heal_interval_ticks", 100, 1, 72000);

        GROUND_SLAM_RADIUS = builder.comment("Radius in blocks of the ground slam shockwave.")
                .defineInRange("ground_slam_radius", 4.0, 0.5, 32.0);

        GROUND_SLAM_MIN_FALL = builder.comment("Minimum fall distance in blocks to trigger ground slam.")
                .defineInRange("ground_slam_min_fall", 4.0, 0.0, 256.0);

        LIGHTS_SHIELD_RESISTANCE_TICKS = builder.comment(
                        "Duration in ticks of the resistance granted by Light's Shield.")
                .defineInRange("lights_shield_resistance_ticks", 100, 1, 12000);

        LIGHTS_SHIELD_RESISTANCE_AMPLIFIER = builder.comment(
                        "Amplifier of the resistance granted by Light's Shield (3 = Resistance IV).")
                .defineInRange("lights_shield_resistance_amplifier", 3, 0, 4);

        BLEED_FRACTION = builder.comment("Fraction of max health dealt per bleed tick per stack.")
                .defineInRange("bleed_fraction", 0.01, 0.0, 1.0);

        BLEED_INTERVAL_TICKS = builder.comment("Ticks between bleed damage ticks.")
                .defineInRange("bleed_interval_ticks", 40, 1, 12000);

        BLEED_DURATION_TICKS = builder.comment("Duration in ticks of the bleed effect when applied or refreshed.")
                .defineInRange("bleed_duration_ticks", 120, 1, 72000);

        BLEED_MAX_STACKS = builder.comment("Maximum bleed stacks on a single target.")
                .defineInRange("bleed_max_stacks", 3, 1, 100);

        POWER_SHOT_INTERVAL = builder.comment("Every Nth crossbow shot is a power shot.")
                .defineInRange("power_shot_interval", 3, 2, 100);

        VITAL_SHOT_FRACTION = builder.comment(
                        "Fraction of the target's max health dealt as bonus damage when vital shot procs.")
                .defineInRange("vital_shot_fraction", 0.10, 0.0, 1.0);

        MARKED_DURATION_TICKS = builder.comment("Duration in ticks of the marked for death debuff.")
                .defineInRange("marked_duration_ticks", 200, 1, 72000);

        MARKED_DAMAGE_BONUS = builder.comment("Bonus damage fraction taken by targets marked for death.")
                .defineInRange("marked_damage_bonus", 0.25, 0.0, 10.0);

        RAIN_OF_ARROWS_COUNT = builder.comment("Arrows summoned per rain of arrows proc.")
                .defineInRange("rain_of_arrows_count", 8, 1, 64);

        RAIN_OF_ARROWS_RADIUS = builder.comment("Radius in blocks of the rain of arrows circle.")
                .defineInRange("rain_of_arrows_radius", 2.0, 0.5, 16.0);

        RAIN_OF_ARROWS_HEIGHT = builder.comment("Height in blocks above the target the rain of arrows spawns at.")
                .defineInRange("rain_of_arrows_height", 8.0, 2.0, 32.0);

        PIERCING_STRIKE_INTERVAL = builder.comment("Every Nth sword hit ignores the target's armor.")
                .defineInRange("piercing_strike_interval", 3, 2, 100);

        RIPOSTE_PARRY_WINDOW = builder.comment(
                        "Ticks after raising a sword block during which a blocked melee hit counts as a parry.")
                .defineInRange("riposte_parry_window", 10, 1, 200);

        RIPOSTE_DAMAGE_MULT = builder.comment("Damage multiplier of the sword attack following a riposte parry.")
                .defineInRange("riposte_damage_mult", 2.5, 1.0, 20.0);

        RIPOSTE_BUFF_WINDOW = builder.comment(
                        "Ticks after a riposte parry during which the bonus damage attack can land.")
                .defineInRange("riposte_buff_window", 60, 1, 12000);

        RIPOSTE_COOLDOWN = builder.comment("Cooldown in ticks between riposte parries.")
                .defineInRange("riposte_cooldown", 100, 0, 1728000);

        PIN_DURATION_TICKS = builder.comment("Duration in ticks of the Pinned effect applied by Pinning Strike.")
                .defineInRange("pin_duration_ticks", 40, 1, 12000);

        RICOCHET_RADIUS = builder.comment("Radius in blocks a ricocheting thrown weapon looks for its next target.")
                .defineInRange("ricochet_radius", 8.0, 1.0, 32.0);

        RICOCHET_DAMAGE_FRACTION = builder.comment(
                        "Fraction of the thrown weapon's hit damage dealt to each ricochet target.")
                .defineInRange("ricochet_damage_fraction", 0.5, 0.0, 10.0);

        DOOM_COOLDOWN = builder.comment("Cooldown in ticks between Doom procs.")
                .defineInRange("doom_cooldown", 200, 0, 1728000);

        DOOM_EFFECT_TICKS = builder.comment("Duration in ticks of the blindness and slowness Doom applies.")
                .defineInRange("doom_effect_ticks", 40, 1, 12000);

        DOOM_SLOWNESS_AMPLIFIER = builder.comment("Slowness amplifier Doom applies (3 is Slowness IV).")
                .defineInRange("doom_slowness_amplifier", 3, 0, 9);

        VOLLEY_EXTRA = builder.comment("Extra copies thrown when Volley procs.").defineInRange("volley_extra", 2, 1, 8);

        VOLLEY_SPREAD_DEGREES = builder.comment("Angle in degrees between the copies thrown by Volley.")
                .defineInRange("volley_spread_degrees", 10.0, 1.0, 45.0);

        LANCER_LEAP_HEIGHT = builder.comment("Height in blocks the Lancer leap reaches at its peak.")
                .defineInRange("lancer_leap_height", 30.0, 1.0, 128.0);

        LANCER_FORWARD_SPEED = builder.comment(
                        "Horizontal speed in blocks per tick added along the look direction when the Lancer leap starts.")
                .defineInRange("lancer_forward_speed", 0.3, 0.0, 4.0);

        LANCER_DAMAGE_SCALE = builder.comment(
                        "Multiplier applied to the Lancer landing damage after every other bonus.")
                .defineInRange("lancer_damage_scale", 1.0, 0.0, 10.0);

        LANCER_EDGE_DAMAGE = builder.comment(
                        "Fraction of the Lancer landing damage dealt at the edge of the smash radius.")
                .defineInRange("lancer_edge_damage", 0.5, 0.0, 1.0);

        LANCER_COOLDOWN = builder.comment("Cooldown in ticks between Lancer leaps.")
                .defineInRange("lancer_cooldown", 600, 0, 1728000);

        LANCER_PROTECTION_TICKS = builder.comment(
                        "Ticks after a Lancer leap before its fall protection expires. 0 keeps it until the next fall.")
                .defineInRange("lancer_protection_ticks", 0, 0, 72000);

        SCYTHE_ARC_RADIUS = builder.comment("Radius in blocks of the Reaping Arc.")
                .defineInRange("scythe_arc_radius", 3.0, 0.5, 16.0);

        SCYTHE_ARC_ANGLE = builder.comment("Width in degrees of the Reaping Arc in front of the player.")
                .defineInRange("scythe_arc_angle", 120.0, 1.0, 360.0);

        WITHERING_DURATION_TICKS = builder.comment("Duration in ticks of the Wither applied by Withering Edge.")
                .defineInRange("withering_duration_ticks", 100, 1, 72000);

        WITHERING_MAX_STACKS = builder.comment("Maximum Wither level Withering Edge can stack up to.")
                .defineInRange("withering_max_stacks", 3, 1, 10);

        DEATHS_TOLL_RADIUS = builder.comment("Radius in blocks of the Death's Toll soul burst.")
                .defineInRange("deaths_toll_radius", 4.0, 0.5, 16.0);

        DEATHS_TOLL_COOLDOWN = builder.comment("Cooldown in ticks between Death's Toll bursts.")
                .defineInRange("deaths_toll_cooldown", 100, 0, 1728000);

        ROOTS_POTENCY_CAP = builder.comment("Highest Roots Classic staff potency after the Magic tree bonus is added.")
                .defineInRange("roots_potency_cap", 5, 0, 10);

        ROOTS_LILAC_POTENCY_CAP = builder.comment(
                        "Highest potency for Lilac casts; Roots Classic throws at potency 5 or more.")
                .defineInRange("roots_lilac_potency_cap", 4, 0, 4);

        WITCH_POWER_CAP = builder.comment(
                        "Highest witch power after the Magic tree bonus; Witchery curse rites stop working above 13.")
                .defineInRange("witch_power_cap", 13, 0, 13);

        RITUAL_OWNER_OFFLINE_FULL_COST = builder.comment(
                        "Rituals and machines whose owner is offline pay the base cost instead of the last known skill discount.")
                .define("ritual_owner_offline_full_cost", true);

        builder.pop();

        SPEC = builder.build();
    }

    private CompanionConfig() {}

    public static int netherMinY() {
        return Math.min(NETHER_METEORITE_MIN_Y.get(), NETHER_METEORITE_MAX_Y.get());
    }

    public static int netherMaxY() {
        return Math.max(NETHER_METEORITE_MIN_Y.get(), NETHER_METEORITE_MAX_Y.get());
    }
}
