package dev.ftb.mods.ftbevolutioncompanion.skills;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SkillXpRemainder {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkillXpRemainder.class);
    private static final String CALCULATION = "net.puffish.skillsmod.api.calculation.Calculation";
    private static final double ROUNDS_TO_ZERO = 0.5;

    private static volatile boolean failureLogged;

    private SkillXpRemainder() {}

    public static Object carryAll(Object calculation) {
        return wrap(calculation, false);
    }

    public static Object carryRoundedAway(Object calculation) {
        return wrap(calculation, true);
    }

    private static Object wrap(Object calculation, boolean roundedAwayOnly) {
        if (calculation == null || Proxy.isProxyClass(calculation.getClass())) {
            return calculation;
        }
        try {
            Class<?> type =
                    Class.forName(CALCULATION, false, calculation.getClass().getClassLoader());
            return Proxy.newProxyInstance(
                    type.getClassLoader(), new Class<?>[] {type}, new Carry(calculation, roundedAwayOnly));
        } catch (ReflectiveOperationException | RuntimeException e) {
            logFailure(e);
            return calculation;
        }
    }

    private static void logFailure(Exception e) {
        if (!failureLogged) {
            failureLogged = true;
            LOGGER.warn("Could not carry skill XP fractions", e);
        }
    }

    private static final class Carry implements InvocationHandler {
        private final Object delegate;
        private final boolean roundedAwayOnly;
        private final Map<UUID, Double> remainders = new ConcurrentHashMap<>();
        private volatile Method playerAccessor;

        private Carry(Object delegate, boolean roundedAwayOnly) {
            this.delegate = delegate;
            this.roundedAwayOnly = roundedAwayOnly;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                return switch (method.getName()) {
                    case "equals" -> proxy == args[0];
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "toString" -> "SkillXpRemainder[" + delegate + "]";
                    default -> call(method, args);
                };
            }
            Object result = call(method, args);
            if (!"evaluate".equals(method.getName()) || args == null || args.length != 1) {
                return result;
            }
            if (!(result instanceof Double value) || !(value > 0.0) || value.isInfinite()) {
                return result;
            }
            if (roundedAwayOnly && value >= ROUNDS_TO_ZERO) {
                return result;
            }
            UUID player = player(args[0]);
            if (player == null) {
                return result;
            }
            double total = value + remainders.getOrDefault(player, 0.0);
            double whole = Math.floor(total);
            remainders.put(player, total - whole);
            return whole;
        }

        private Object call(Method method, Object[] args) throws Throwable {
            try {
                return method.invoke(delegate, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }

        private UUID player(Object data) {
            try {
                Method accessor = playerAccessor;
                if (accessor == null) {
                    accessor = data.getClass().getMethod("player");
                    playerAccessor = accessor;
                }
                return accessor.invoke(data) instanceof Entity entity ? entity.getUUID() : null;
            } catch (ReflectiveOperationException | RuntimeException e) {
                logFailure(e);
                return null;
            }
        }
    }
}
