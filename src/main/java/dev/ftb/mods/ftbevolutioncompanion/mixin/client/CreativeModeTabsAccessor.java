package dev.ftb.mods.ftbevolutioncompanion.mixin.client;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeModeTabs.class)
public interface CreativeModeTabsAccessor {
    @Accessor("CACHED_PARAMETERS")
    static CreativeModeTab.@Nullable ItemDisplayParameters ftbevo$getCachedParameters() {
        throw new UnsupportedOperationException();
    }
}
