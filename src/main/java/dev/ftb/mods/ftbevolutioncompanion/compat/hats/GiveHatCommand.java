package dev.ftb.mods.ftbevolutioncompanion.compat.hats;

import com.astryxion.hats.common.capability.HatDataCapability;
import com.astryxion.hats.common.hat.PlayerHatData;
import com.astryxion.hats.common.network.HatPacketHandler;
import com.astryxion.hats.common.registry.HatItemRegistry;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.jspecify.annotations.Nullable;

public final class GiveHatCommand {
    private static final String LANG = "ftbevolutioncompanion.givehat.";
    private static final String HATS = "hats";
    private static final int[] MILESTONES = {1, 10, 100, 200, 300, 332};

    private GiveHatCommand() {}

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher()
                .register(Commands.literal("givehat")
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.literal("random").executes(GiveHatCommand::giveRandom))
                                .then(Commands.argument("hat", IdentifierArgument.id())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(
                                                HatItemRegistry.getAllHats().stream()
                                                        .map(BuiltInRegistries.ITEM::getKey),
                                                builder))
                                        .executes(GiveHatCommand::giveHat))));
    }

    private static int giveHat(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Identifier requested = IdentifierArgument.getId(context, "hat");
        Item hat = findHat(Identifier.fromNamespaceAndPath(HATS, requested.getPath()));
        if (hat == null) {
            context.getSource().sendFailure(Component.translatable(LANG + "unknown_hat", requested.toString()));
            return 0;
        }
        int unlocked = 0;
        for (ServerPlayer player : EntityArgument.getPlayers(context, "targets")) {
            if (unlock(player, hat)) {
                unlocked++;
                context.getSource()
                        .sendSuccess(
                                () -> Component.translatable(LANG + "unlocked", hatName(hat), player.getDisplayName()),
                                true);
            } else {
                context.getSource()
                        .sendFailure(Component.translatable(
                                LANG + "already_unlocked", player.getDisplayName(), hatName(hat)));
            }
        }
        return unlocked;
    }

    private static int giveRandom(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int unlocked = 0;
        for (ServerPlayer player : EntityArgument.getPlayers(context, "targets")) {
            PlayerHatData data = HatDataCapability.get(player).orElse(null);
            List<Item> locked = HatItemRegistry.getAllHats().stream()
                    .filter(item -> data != null && !data.hasHat(BuiltInRegistries.ITEM.getKey(item)))
                    .toList();
            if (locked.isEmpty()) {
                context.getSource().sendFailure(Component.translatable(LANG + "all_unlocked", player.getDisplayName()));
                continue;
            }
            Item hat = locked.get(player.getRandom().nextInt(locked.size()));
            if (unlock(player, hat)) {
                unlocked++;
                context.getSource()
                        .sendSuccess(
                                () -> Component.translatable(LANG + "unlocked", hatName(hat), player.getDisplayName()),
                                true);
            }
        }
        return unlocked;
    }

    private static boolean unlock(ServerPlayer player, Item hat) {
        Identifier id = BuiltInRegistries.ITEM.getKey(hat);
        PlayerHatData data = HatDataCapability.get(player).orElse(null);
        if (data == null || data.hasHat(id)) return false;
        data.unlockHat(id);
        awardMilestones(player, data.getUnlockedHats().size());
        HatPacketHandler.sendHatUnlockedToPlayer(player, new ItemStack(hat));
        HatPacketHandler.sendSyncHatToPlayer(player, data.serializeNBT());
        HatDataCapability.markDirty(player);
        return true;
    }

    private static void awardMilestones(ServerPlayer player, int unlocked) {
        for (int milestone : MILESTONES) {
            if (unlocked < milestone) return;
            AdvancementHolder holder = player.level()
                    .getServer()
                    .getAdvancements()
                    .get(Identifier.fromNamespaceAndPath(HATS, "hats/collect_" + milestone));
            if (holder != null) {
                player.getAdvancements().award(holder, "unlock_via_code");
            }
        }
    }

    @Nullable
    private static Item findHat(Identifier id) {
        return HatItemRegistry.getAllHats().stream()
                .filter(item -> BuiltInRegistries.ITEM.getKey(item).equals(id))
                .findFirst()
                .orElse(null);
    }

    private static Component hatName(Item hat) {
        return new ItemStack(hat).getHoverName();
    }
}
