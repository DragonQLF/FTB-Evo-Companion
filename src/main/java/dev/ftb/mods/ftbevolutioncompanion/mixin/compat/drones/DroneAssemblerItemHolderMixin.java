package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.drones;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "rearth.blocks.controller.ControllerBlockEntity", remap = false)
public abstract class DroneAssemblerItemHolderMixin {
    @ModifyArg(
            method = "assembleDrone(Lnet/minecraft/world/entity/player/Player;Ljava/lang/String;)V",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/item/ItemStack;<init>(Lnet/minecraft/core/Holder;)V"))
    private Holder<Item> ftbevo$registeredDroneHolder(Holder<Item> holder) {
        return BuiltInRegistries.ITEM.wrapAsHolder(holder.value());
    }
}
