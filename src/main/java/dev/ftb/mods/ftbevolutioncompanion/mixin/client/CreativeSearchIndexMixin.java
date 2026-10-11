package dev.ftb.mods.ftbevolutioncompanion.mixin.client;

import java.lang.ref.WeakReference;
import java.util.List;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.CreativeModeTabSearchRegistry;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeSearchIndexMixin {
    @Unique
    private static WeakReference<CreativeModeTab.@Nullable ItemDisplayParameters> ftbevo$indexedParameters =
            new WeakReference<>(null);

    @Unique
    private static WeakReference<@Nullable SessionSearchTrees> ftbevo$indexedTrees = new WeakReference<>(null);

    @Inject(method = "tryRebuildTabContents", at = @At("RETURN"))
    private void ftbevo$indexTabsBuiltElsewhere(
            @Nullable SessionSearchTrees searchTrees,
            FeatureFlagSet enabledFeatures,
            boolean hasPermissions,
            HolderLookup.Provider holders,
            CallbackInfoReturnable<Boolean> cir) {
        CreativeModeTab.ItemDisplayParameters parameters = CreativeModeTabsAccessor.ftbevo$getCachedParameters();
        if (searchTrees == null || parameters == null) {
            return;
        }
        boolean stale = ftbevo$indexedParameters.get() != parameters || ftbevo$indexedTrees.get() != searchTrees;
        if (!cir.getReturnValue() && stale) {
            CreativeModeTabs.allTabs().stream()
                    .filter(CreativeModeTab::hasSearchBar)
                    .forEach(tab -> {
                        List<ItemStack> list = List.copyOf(tab.getDisplayItems());
                        searchTrees.updateCreativeTooltips(
                                holders, list, CreativeModeTabSearchRegistry.getNameSearchKey(tab));
                        searchTrees.updateCreativeTags(list, CreativeModeTabSearchRegistry.getTagSearchKey(tab));
                    });
        }
        ftbevo$indexedParameters = new WeakReference<>(parameters);
        ftbevo$indexedTrees = new WeakReference<>(searchTrees);
    }
}
