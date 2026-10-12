package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hats.client;

import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "com.astryxion.hats.client.network.HatClientPackets", remap = false)
public abstract class HatClientPacketsMixin {
    @Redirect(
            method = "sendEquipToServer(Ljava/lang/String;)V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/network/FriendlyByteBuf;writeByte(B)Lnet/minecraft/network/FriendlyByteBuf;"))
    private static FriendlyByteBuf ftbevo$omitDuplicateEquipType(FriendlyByteBuf buffer, byte typeId) {
        // HatsC2SPayload already carries the type; PacketEquipHat.decode expects UTF first.
        return buffer;
    }
}
