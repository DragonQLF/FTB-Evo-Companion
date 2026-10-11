package dev.ftb.mods.ftbevolutioncompanion.compat.hats;

import com.astryxion.hats.common.capability.HatDataAccess;
import com.astryxion.hats.common.capability.HatDataCapability;
import com.astryxion.hats.common.data.ServerHatUnlocks;
import com.astryxion.hats.common.hat.HatAdvancements;
import com.astryxion.hats.common.network.HatPacketHandler;
import com.astryxion.hats.common.network.PacketHatUnlocked;
import com.astryxion.hats.common.network.PacketSetUnlockedHats;
import com.astryxion.hats.common.network.PacketSyncHat;
import com.astryxion.hats.common.registry.HatItemRegistry;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Set;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
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
            Set<Identifier> owned = ServerHatUnlocks.get(player).copy(player);
            List<Item> locked = HatItemRegistry.getAllHats().stream()
                    .filter(item -> !owned.contains(BuiltInRegistries.ITEM.getKey(item)))
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
        ServerHatUnlocks store = ServerHatUnlocks.get(player);
        if (store.copy(player).contains(id) || !store.add(player, id)) return false;
        PacketSetUnlockedHats.send(player);
        HatAdvancements.onUnlockCounts(player, id);
        CompoundTag unlocked = player.getData(HatDataCapability.HAT_DATA).serializeNBT();
        HatDataAccess.apply(player, unlocked);
        HatPacketHandler.sendToPlayer(player, new PacketHatUnlocked(new ItemStack(hat)));
        HatPacketHandler.sendToPlayer(player, new PacketSyncHat(unlocked));
        player.getInventory().setChanged();
        return true;
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
