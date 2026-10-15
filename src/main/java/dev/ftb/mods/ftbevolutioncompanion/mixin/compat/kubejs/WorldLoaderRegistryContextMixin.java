package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.kubejs;

import com.llamalad7.mixinextras.sugar.Local;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import java.util.concurrent.CompletionStage;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.WorldLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldLoader.class)
public abstract class WorldLoaderRegistryContextMixin {
    @Inject(
            method = "lambda$load$0",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/resources/RegistryDataLoader;load(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/List;)Ljava/util/concurrent/CompletableFuture;",
                            shift = At.Shift.BEFORE))
    private static void ftbevolutioncompanion$setKubeJSRegistryContext(
            CallbackInfoReturnable<CompletionStage<?>> cir,
            @Local(name = "worldgenLoadContext") RegistryAccess.Frozen worldgenLoadContext) {
        RegistryAccessContainer.current = new RegistryAccessContainer(worldgenLoadContext);
    }
}
