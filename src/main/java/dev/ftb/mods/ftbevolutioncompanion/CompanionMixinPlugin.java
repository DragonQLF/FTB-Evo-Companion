package dev.ftb.mods.ftbevolutioncompanion;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
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
}
