package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.powerarmor;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.portingdeadmods.power_armor.content.modules.SolarArmorModule;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SolarArmorModule.class, remap = false)
public abstract class SolarModuleChargeAllPiecesMixin {
    @WrapOperation(
            method = "tick",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/item/ItemStack;getCapability(Lnet/neoforged/neoforge/capabilities/ItemCapability;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object ftbevo$useEachPiecesOwnStack(
            ItemStack piece, ItemCapability<?, ?> capability, Object context, Operation<Object> original) {
        return original.call(piece, capability, ItemAccess.forStack(piece));
    }
}
