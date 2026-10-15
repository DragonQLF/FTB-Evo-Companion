package dev.ftb.mods.ftbevolutioncompanion;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.Config;
import org.spongepowered.asm.service.MixinService;

public class CompanionMixinPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanionMixinPlugin.class);

    private static final String KUBEJS_CONFIG = "kubejs.mixins.json";
    private static final String KUBEJS_WORLD_LOADER_MIXIN = "dev.latvian.mods.kubejs.core.mixin.WorldLoaderMixin";
    private static final String WORLD_LOADER_REPLACEMENT_MIXIN =
            "dev.ftb.mods.ftbevolutioncompanion.mixin.compat.kubejs.WorldLoaderRegistryContextMixin";
    private static final String OLD_REGISTRY_LOAD =
            "Lnet/minecraft/resources/RegistryDataLoader;load(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/List;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;";

    private static final String GROWTH_ACCELERATOR_CONFIG = "growthacceleratortiers.mixins.json";
    private static final String GROWTH_ACCELERATOR_GEORE_MIXIN =
            "dev.satherov.growthacceleratortiers.mixins.BuddingGeoreBlockMixin";
    private static final String GEORE_BUDDING_BLOCK = "com.shynieke.geore.block.BuddingGeoreBlock";
    private static final String INJECT = "Lorg/spongepowered/asm/mixin/injection/Inject;";
    private static final String RANDOM_SOURCE = "net/minecraft/util/RandomSource";
    private static final String NEXT_INT = "L" + RANDOM_SOURCE + ";nextInt(I)I";

    private Boolean replaceKubeJSWorldLoader;

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return !mixinClassName.equals(WORLD_LOADER_REPLACEMENT_MIXIN) || replaceKubeJSWorldLoader();
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        if (replaceKubeJSWorldLoader()) {
            removeKubeJSWorldLoaderMixin();
        }
        if (growthAcceleratorNeedsGeOreRetarget()) {
            retargetGrowthAcceleratorGeOreMixin();
        }
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    private boolean replaceKubeJSWorldLoader() {
        if (replaceKubeJSWorldLoader == null) {
            replaceKubeJSWorldLoader = kubeJSTargetsOldRegistryLoad();
        }
        return replaceKubeJSWorldLoader;
    }

    private static boolean kubeJSTargetsOldRegistryLoad() {
        try {
            ClassNode node = MixinService.getService().getBytecodeProvider().getClassNode(KUBEJS_WORLD_LOADER_MIXIN);
            for (MethodNode method : node.methods) {
                if (method.visibleAnnotations != null && containsString(method.visibleAnnotations, OLD_REGISTRY_LOAD)) {
                    return true;
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            return false;
        }
        return false;
    }

    private static boolean containsString(Object value, String expected) {
        if (value instanceof String string) {
            return string.equals(expected);
        }
        if (value instanceof AnnotationNode annotation) {
            return annotation.values != null && containsString(annotation.values, expected);
        }
        if (value instanceof List<?> list) {
            for (Object entry : list) {
                if (containsString(entry, expected)) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static void removeKubeJSWorldLoaderMixin() {
        IMixinConfig config =
                Config.create(KUBEJS_CONFIG, (MixinEnvironment) null).getConfig();
        try {
            var field = config.getClass().getDeclaredField("mixinMapping");
            field.setAccessible(true);
            int removed = 0;
            for (List<? extends IMixinInfo> mixins :
                    ((Map<String, List<? extends IMixinInfo>>) field.get(config)).values()) {
                int before = mixins.size();
                mixins.removeIf(info -> KUBEJS_WORLD_LOADER_MIXIN.equals(info.getClassName()));
                removed += before - mixins.size();
            }
            LOGGER.info(
                    "Replaced KubeJS WorldLoaderMixin on {} target(s) with the NeoForge 26.1.2.110+ version", removed);
        } catch (ReflectiveOperationException e) {
            LOGGER.error("Could not remove KubeJS WorldLoaderMixin", e);
        }
    }

    private static boolean growthAcceleratorNeedsGeOreRetarget() {
        try {
            ClassNode block = MixinService.getService().getBytecodeProvider().getClassNode(GEORE_BUDDING_BLOCK);
            ClassNode mixin =
                    MixinService.getService().getBytecodeProvider().getClassNode(GROWTH_ACCELERATOR_GEORE_MIXIN);
            return countNextIntCalls(block, "randomTick") == 1 && retargetNextIntOrdinal(mixin, false) > 0;
        } catch (ClassNotFoundException | IOException e) {
            return false;
        }
    }

    private static int countNextIntCalls(ClassNode node, String methodName) {
        int calls = 0;
        for (MethodNode method : node.methods) {
            if (method.name.equals(methodName)) {
                for (AbstractInsnNode insn : method.instructions) {
                    if (insn instanceof MethodInsnNode call
                            && call.owner.equals(RANDOM_SOURCE)
                            && call.name.equals("nextInt")
                            && call.desc.equals("(I)I")) {
                        calls++;
                    }
                }
            }
        }
        return calls;
    }

    private static int retargetNextIntOrdinal(ClassNode mixin, boolean apply) {
        int matches = 0;
        for (MethodNode method : mixin.methods) {
            if (method.visibleAnnotations == null) {
                continue;
            }
            for (AnnotationNode annotation : method.visibleAnnotations) {
                if (!annotation.desc.equals(INJECT) || annotation.values == null) {
                    continue;
                }
                for (int i = 0; i + 1 < annotation.values.size(); i += 2) {
                    if ("at".equals(annotation.values.get(i)) && annotation.values.get(i + 1) instanceof List<?> ats) {
                        for (Object at : ats) {
                            if (at instanceof AnnotationNode atNode && retargetAt(atNode, apply)) {
                                matches++;
                            }
                        }
                    }
                }
            }
        }
        return matches;
    }

    private static boolean retargetAt(AnnotationNode at, boolean apply) {
        if (at.values == null || !at.values.contains(NEXT_INT)) {
            return false;
        }
        for (int i = 0; i + 1 < at.values.size(); i += 2) {
            if ("ordinal".equals(at.values.get(i)) && Integer.valueOf(1).equals(at.values.get(i + 1))) {
                if (apply) {
                    at.values.set(i + 1, 0);
                }
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static void retargetGrowthAcceleratorGeOreMixin() {
        IMixinConfig config = Config.create(GROWTH_ACCELERATOR_CONFIG, (MixinEnvironment) null)
                .getConfig();
        try {
            var mappingField = config.getClass().getDeclaredField("mixinMapping");
            mappingField.setAccessible(true);
            for (List<? extends IMixinInfo> mixins :
                    ((Map<String, List<? extends IMixinInfo>>) mappingField.get(config)).values()) {
                for (IMixinInfo info : mixins) {
                    if (GROWTH_ACCELERATOR_GEORE_MIXIN.equals(info.getClassName())) {
                        int changed = retargetNextIntOrdinal(mixinState(info), true);
                        LOGGER.info(
                                "Moved {} Growth Accelerator Tiers GeOre injection(s) to the GeOre 8.1.0+ randomTick",
                                changed);
                        return;
                    }
                }
            }
            LOGGER.warn("Growth Accelerator Tiers BuddingGeoreBlockMixin was not found in its config");
        } catch (ReflectiveOperationException e) {
            LOGGER.error("Could not update Growth Accelerator Tiers BuddingGeoreBlockMixin", e);
        }
    }

    private static ClassNode mixinState(IMixinInfo info) throws ReflectiveOperationException {
        for (String stateName : new String[] {"pendingState", "state"}) {
            var stateField = info.getClass().getDeclaredField(stateName);
            stateField.setAccessible(true);
            Object state = stateField.get(info);
            if (state != null) {
                var nodeField = state.getClass().getDeclaredField("classNode");
                nodeField.setAccessible(true);
                return (ClassNode) nodeField.get(state);
            }
        }
        throw new NoSuchFieldException("state");
    }
}
