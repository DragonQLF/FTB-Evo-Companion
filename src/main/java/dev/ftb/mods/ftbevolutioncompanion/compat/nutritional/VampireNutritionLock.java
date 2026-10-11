package dev.ftb.mods.ftbevolutioncompanion.compat.nutritional;

import com.breakinblocks.nutritional.data.attachment.NutritionalAttachments;
import com.breakinblocks.nutritional.data.attachment.PlayerNutritionData;
import com.breakinblocks.nutritional.data.registry.NutritionalDatapack;
import com.breakinblocks.nutritional.net.NutritionalNetwork;
import dev.sterner.witchery.feature.affliction.vampire.VampirePlayerAttachment;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class VampireNutritionLock {
    private static final float LOCKED_VALUE = 50.0F;
    private static final int INTERVAL = 20;

    private VampireNutritionLock() {}

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % INTERVAL != 0) return;
        if (VampirePlayerAttachment.getData(player).getLevel() <= 0) return;
        PlayerNutritionData current = player.getData(NutritionalAttachments.PLAYER_NUTRITION);
        Map<Identifier, Float> updates = new HashMap<>();
        NutritionalDatapack.nutrients(player.level().registryAccess())
                .listElements()
                .forEach(ref -> {
                    Identifier id = ref.key().identifier();
                    if (current.get(id, ref.value().defaultValue()) != LOCKED_VALUE) updates.put(id, LOCKED_VALUE);
                });
        if (updates.isEmpty()) return;
        player.setData(NutritionalAttachments.PLAYER_NUTRITION, current.withValues(updates));
        NutritionalNetwork.sendDelta(player, updates);
    }
}
