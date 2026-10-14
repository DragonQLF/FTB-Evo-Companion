package dev.ftb.mods.ftbevolutioncompanion.skills;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MiningSkillXp {
    private static final Logger LOGGER = LoggerFactory.getLogger(MiningSkillXp.class);
    private static final String SKILLS_API = "net.puffish.skillsmod.api.SkillsAPI";
    private static final String MINE_BLOCK_SOURCE =
            "net.puffish.skillsmod.experience.source.builtin.MineBlockExperienceSource";
    private static final String MINE_BLOCK_DATA = MINE_BLOCK_SOURCE + "$Data";
    private static final String CALCULATION = "net.puffish.skillsmod.api.calculation.Calculation";
    private static final Identifier MINING_GADGET_BLOCK =
            Identifier.fromNamespaceAndPath("mininggadgets", "renderblock");

    private static volatile boolean resolved;
    private static volatile boolean failureLogged;
    private static Method updateExperienceSources;
    private static Class<?> mineBlockSource;
    private static Method calculation;
    private static Method evaluate;
    private static Constructor<?> data;

    private MiningSkillXp() {}

    public static void onBlockBreak(BreakBlockEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player) || !(event.getLevel() instanceof Level level)) {
            return;
        }
        BlockPos pos = event.getPos();
        if (!MINING_GADGET_BLOCK.equals(
                BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()))) {
            return;
        }
        BlockState state = event.getState();
        if (!EventHooks.doPlayerHarvestCheck(player, state, level, pos)) {
            award(player, state, player.getMainHandItem());
        }
    }

    public static void award(ServerPlayer player, BlockState state, ItemStack tool) {
        if (!resolve()) {
            return;
        }
        try {
            Function<Object, Integer> amount = source -> {
                try {
                    Object calc = calculation.invoke(source);
                    double xp = (double) evaluate.invoke(calc, data.newInstance(player, state, tool));
                    return (int) Math.round(xp);
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException(e);
                }
            };
            updateExperienceSources.invoke(null, player, mineBlockSource, amount);
        } catch (ReflectiveOperationException | RuntimeException e) {
            logFailure(e);
        }
    }

    private static boolean resolve() {
        if (resolved) {
            return updateExperienceSources != null;
        }
        synchronized (MiningSkillXp.class) {
            if (!resolved) {
                try {
                    ClassLoader loader = MiningSkillXp.class.getClassLoader();
                    Class<?> api = Class.forName(SKILLS_API, false, loader);
                    Class<?> source = Class.forName(MINE_BLOCK_SOURCE, false, loader);
                    Class<?> calc = Class.forName(CALCULATION, false, loader);
                    Class<?> dataType = Class.forName(MINE_BLOCK_DATA, false, loader);
                    calculation = source.getMethod("calculation");
                    evaluate = calc.getMethod("evaluate", Object.class);
                    data = dataType.getConstructor(ServerPlayer.class, BlockState.class, ItemStack.class);
                    mineBlockSource = source;
                    updateExperienceSources =
                            api.getMethod("updateExperienceSources", ServerPlayer.class, Class.class, Function.class);
                } catch (ReflectiveOperationException | RuntimeException e) {
                    updateExperienceSources = null;
                    logFailure(e);
                }
                resolved = true;
            }
        }
        return updateExperienceSources != null;
    }

    private static void logFailure(Exception e) {
        if (!failureLogged) {
            failureLogged = true;
            LOGGER.warn("Could not give Mining skill experience", e);
        }
    }
}
