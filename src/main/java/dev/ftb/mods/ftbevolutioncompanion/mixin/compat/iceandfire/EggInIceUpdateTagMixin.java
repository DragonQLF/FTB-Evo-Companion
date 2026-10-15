package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.iceandfire;

import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.item.block.entity.EggInIceBlockEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EggInIceBlockEntity.class)
public abstract class EggInIceUpdateTagMixin {
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        if (((EggInIceBlockEntity) (Object) this).type == null) {
            return new CompoundTag();
        }
        return ((BlockEntity) (Object) this).saveCustomOnly(registries);
    }

    @WrapOperation(
            method = "loadAdditional",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lcom/iafenvoy/iceandfire/data/DragonColor;getById(Ljava/lang/String;)Lcom/iafenvoy/iceandfire/data/DragonColor;"))
    private DragonColor ftbevo$unknownColorAsNone(String id, Operation<DragonColor> original) {
        if (id.isEmpty()) {
            return null;
        }
        try {
            return original.call(id);
        } catch (RuntimeException e) {
            return null;
        }
    }
}
