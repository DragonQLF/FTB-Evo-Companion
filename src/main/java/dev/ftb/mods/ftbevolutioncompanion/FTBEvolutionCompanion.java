package dev.ftb.mods.ftbevolutioncompanion;

import dev.ftb.mods.ftbevolutioncompanion.athletics.AthleticsAbilities;
import dev.ftb.mods.ftbevolutioncompanion.athletics.AthleticsRegistry;
import dev.ftb.mods.ftbevolutioncompanion.athletics.network.AthleticsPayloads;
import dev.ftb.mods.ftbevolutioncompanion.challenge.ChallengeBoardCommand;
import dev.ftb.mods.ftbevolutioncompanion.challenge.ChallengeLeaderboard;
import dev.ftb.mods.ftbevolutioncompanion.challenge.ChallengeRegistry;
import dev.ftb.mods.ftbevolutioncompanion.challenge.network.ChallengePayloads;
import dev.ftb.mods.ftbevolutioncompanion.client.AthleticsClientHandler;
import dev.ftb.mods.ftbevolutioncompanion.client.AthleticsKeys;
import dev.ftb.mods.ftbevolutioncompanion.client.BeastTrophyClient;
import dev.ftb.mods.ftbevolutioncompanion.client.ChallengeBoardClient;
import dev.ftb.mods.ftbevolutioncompanion.client.FabricatorClient;
import dev.ftb.mods.ftbevolutioncompanion.client.MoltenMetalsClient;
import dev.ftb.mods.ftbevolutioncompanion.client.PyramidClient;
import dev.ftb.mods.ftbevolutioncompanion.client.RecipeSyncClient;
import dev.ftb.mods.ftbevolutioncompanion.client.SkillsClientHandler;
import dev.ftb.mods.ftbevolutioncompanion.client.SkillsKeys;
import dev.ftb.mods.ftbevolutioncompanion.client.WingTooltips;
import dev.ftb.mods.ftbevolutioncompanion.compat.apotheosis.BossInvaderHandler;
import dev.ftb.mods.ftbevolutioncompanion.compat.curios.CuriosReloadFix;
import dev.ftb.mods.ftbevolutioncompanion.compat.gateways.TreasureGoblinEvent;
import dev.ftb.mods.ftbevolutioncompanion.compat.hats.GiveHatCommand;
import dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus.MoltenMetals;
import dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus.ToolTraits;
import dev.ftb.mods.ftbevolutioncompanion.compat.iceandfire.IceAndFireClaimProtection;
import dev.ftb.mods.ftbevolutioncompanion.compat.iris.IrisGeckoGlow;
import dev.ftb.mods.ftbevolutioncompanion.compat.jei.RecipeSyncTypes;
import dev.ftb.mods.ftbevolutioncompanion.compat.nutritional.VampireNutritionLock;
import dev.ftb.mods.ftbevolutioncompanion.compat.oceanmobs.RiftArena;
import dev.ftb.mods.ftbevolutioncompanion.compat.powerarmor.PowerArmorRecipeSync;
import dev.ftb.mods.ftbevolutioncompanion.compat.removed.RemovedMods;
import dev.ftb.mods.ftbevolutioncompanion.compat.starcatcher.StarcatcherPayloads;
import dev.ftb.mods.ftbevolutioncompanion.compat.streams.StreamsRemoval;
import dev.ftb.mods.ftbevolutioncompanion.compat.thaumaturge.ThaumaturgePlaceholders;
import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;
import dev.ftb.mods.ftbevolutioncompanion.content.CompanionContent;
import dev.ftb.mods.ftbevolutioncompanion.fabricator.FabricatorRegistry;
import dev.ftb.mods.ftbevolutioncompanion.gunnery.GunneryHooks;
import dev.ftb.mods.ftbevolutioncompanion.gunnery.GunneryRegistry;
import dev.ftb.mods.ftbevolutioncompanion.gunnery.LtxGunnery;
import dev.ftb.mods.ftbevolutioncompanion.magic.MagicRegistry;
import dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.HedgeCraftMagic;
import dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy.HemomancyMagic;
import dev.ftb.mods.ftbevolutioncompanion.magic.otherworld.OtherworldMagic;
import dev.ftb.mods.ftbevolutioncompanion.magic.sorcery.SorceryMagic;
import dev.ftb.mods.ftbevolutioncompanion.pyramid.LaunchTask;
import dev.ftb.mods.ftbevolutioncompanion.pyramid.PyramidRegistry;
import dev.ftb.mods.ftbevolutioncompanion.pyramid.network.PyramidPayloads;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsAbilities;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsCommand;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsRegistry;
import dev.ftb.mods.ftbevolutioncompanion.skills.handler.CombatTicker;
import dev.ftb.mods.ftbevolutioncompanion.skills.handler.IncomingDamage;
import dev.ftb.mods.ftbevolutioncompanion.skills.handler.OutgoingDamage;
import dev.ftb.mods.ftbevolutioncompanion.skills.network.SkillsPayloads;
import dev.ftb.mods.ftbevolutioncompanion.spawn.PyramidBiome;
import dev.ftb.mods.ftbevolutioncompanion.spawn.WorldSpawnEnforcer;
import dev.ftb.mods.ftbevolutioncompanion.worldgen.FixedChunkPlacement;
import dev.ftb.mods.ftbevolutioncompanion.worldgen.MeteoriteSpacing;
import dev.ftb.mods.ftbevolutioncompanion.worldgen.SpawnPyramidStructure;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(FTBEvolutionCompanion.MOD_ID)
public class FTBEvolutionCompanion {
    public static final String MOD_ID = "ftbevolutioncompanion";

    private static final Logger LOGGER = LoggerFactory.getLogger(FTBEvolutionCompanion.class);

    public FTBEvolutionCompanion(IEventBus eventBus, ModContainer container, Dist dist) {
        container.registerConfig(ModConfig.Type.COMMON, CompanionConfig.SPEC);
        FabricatorRegistry.register(eventBus);
        CompanionSounds.SOUND_EVENTS.register(eventBus);

        NeoForge.EVENT_BUS.addListener(MeteoriteSpacing::onServerAboutToStart);
        IceAndFireClaimProtection.register();

        CompanionContent.BLOCKS.register(eventBus);
        CompanionContent.ITEMS.register(eventBus);
        CompanionContent.FTB_BLOCKS.register(eventBus);
        CompanionContent.FTB_ITEMS.register(eventBus);
        CompanionContent.FTB_BLOCK_ENTITIES.register(eventBus);
        MoltenMetals.register(eventBus);
        ToolTraits.register();
        eventBus.addListener(CompanionContent::onBuildCreativeTabs);
        ChallengeRegistry.BLOCK_ENTITIES.register(eventBus);
        eventBus.addListener(ChallengePayloads::register);
        NeoForge.EVENT_BUS.addListener(ChallengeLeaderboard::onServerTick);
        NeoForge.EVENT_BUS.addListener(ChallengeLeaderboard::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(ChallengeLeaderboard::onServerStopped);
        NeoForge.EVENT_BUS.addListener(ChallengeBoardCommand::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(GiveHatCommand::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(VampireNutritionLock::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, BossInvaderHandler::onFinalizeSpawn);
        NeoForge.EVENT_BUS.addListener(BossInvaderHandler::onEntityJoinLevel);
        NeoForge.EVENT_BUS.addListener(RiftArena::onSpawnPlacementCheck);
        NeoForge.EVENT_BUS.addListener(RecipeSyncTypes::onDatapackSync);
        StreamsRemoval.register(eventBus);
        NeoForge.EVENT_BUS.addListener(TreasureGoblinEvent::onDamage);
        NeoForge.EVENT_BUS.addListener(TreasureGoblinEvent::onWaveEntitySpawned);
        NeoForge.EVENT_BUS.addListener(TreasureGoblinEvent::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(TreasureGoblinEvent::onEntityInteract);
        NeoForge.EVENT_BUS.addListener(TreasureGoblinEvent::onEntityInteractSpecific);
        NeoForge.EVENT_BUS.addListener(TreasureGoblinEvent::onServerTick);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, CuriosReloadFix::onDatapackSync);
        eventBus.addListener(PowerArmorRecipeSync::register);
        NeoForge.EVENT_BUS.addListener(PowerArmorRecipeSync::onDatapackSync);
        if (ModList.get().isLoaded("starcatcher")) {
            eventBus.addListener(StarcatcherPayloads::register);
        }
        PyramidRegistry.BLOCK_ENTITIES.register(eventBus);
        eventBus.addListener(PyramidRegistry::onRegisterCapabilities);
        eventBus.addListener(PyramidPayloads::register);
        LaunchTask.register();

        FixedChunkPlacement.PLACEMENT_TYPES.register(eventBus);
        SpawnPyramidStructure.STRUCTURE_TYPES.register(eventBus);

        WorldSpawnEnforcer.ATTACHMENTS.register(eventBus);
        NeoForge.EVENT_BUS.addListener(WorldSpawnEnforcer::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(WorldSpawnEnforcer::onServerStarted);
        NeoForge.EVENT_BUS.addListener(WorldSpawnEnforcer::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(WorldSpawnEnforcer::onRespawnPosition);
        NeoForge.EVENT_BUS.addListener(PyramidBiome::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(PyramidBiome::onLevelUnload);

        AthleticsRegistry.ATTRIBUTES.register(eventBus);
        AthleticsRegistry.ATTACHMENTS.register(eventBus);
        TreasureGoblinEvent.ATTACHMENTS.register(eventBus);
        eventBus.addListener(AthleticsRegistry::onEntityAttributeModification);

        SkillsRegistry.ATTRIBUTES.register(eventBus);
        SkillsRegistry.EFFECTS.register(eventBus);
        SkillsRegistry.ATTACHMENTS.register(eventBus);
        MagicRegistry.ATTRIBUTES.register(eventBus);
        ThaumaturgePlaceholders.register(eventBus);
        RemovedMods.register(eventBus);
        MagicRegistry.STATS.register(eventBus);
        eventBus.addListener(MagicRegistry::onEntityAttributeModification);
        eventBus.addListener(MagicRegistry::onCommonSetup);
        SorceryMagic.register(eventBus);
        HedgeCraftMagic.register(eventBus);
        HemomancyMagic.register(eventBus);
        OtherworldMagic.register(eventBus);
        GunneryRegistry.ATTRIBUTES.register(eventBus);
        eventBus.addListener(GunneryRegistry::onEntityAttributeModification);
        GunneryHooks.register();
        LtxGunnery.register();
        eventBus.addListener(SkillsRegistry::onEntityAttributeModification);
        NeoForge.EVENT_BUS.addListener(AttributePersistence::onPlayerClone);
        eventBus.addListener(SkillsPayloads::register);
        NeoForge.EVENT_BUS.addListener(SkillsAbilities::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(SkillsAbilities::onPlayerRespawn);
        NeoForge.EVENT_BUS.addListener(SkillsAbilities::onPlayerChangedDimension);
        NeoForge.EVENT_BUS.addListener(SkillsCommand::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(OutgoingDamage::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(IncomingDamage::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(IncomingDamage::onDamagePre);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onDamagePost);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onLivingExperienceDrop);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onProjectileImpact);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onShieldBlock);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onLivingFall);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onLivingVisibility);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onAttackEntity);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onLivingDrops);
        NeoForge.EVENT_BUS.addListener(CombatTicker::onEntityJoin);
        eventBus.addListener(AthleticsPayloads::register);
        NeoForge.EVENT_BUS.addListener(AthleticsAbilities::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(AthleticsAbilities::onPlayerRespawn);
        NeoForge.EVENT_BUS.addListener(AthleticsAbilities::onPlayerChangedDimension);
        NeoForge.EVENT_BUS.addListener(AthleticsAbilities::onPlayerTick);

        if (dist == Dist.CLIENT) {
            FabricatorClient.register(eventBus);
            eventBus.addListener(MoltenMetalsClient::onRegisterFluidModels);
            eventBus.addListener(AthleticsKeys::onRegisterKeyMappings);
            NeoForge.EVENT_BUS.addListener(AthleticsClientHandler::onClientTick);
            eventBus.addListener(SkillsKeys::onRegisterKeyMappings);
            NeoForge.EVENT_BUS.addListener(SkillsClientHandler::onClientTick);
            NeoForge.EVENT_BUS.addListener(SkillsClientHandler::onLeftClickEmpty);
            NeoForge.EVENT_BUS.addListener(SkillsClientHandler::onLeftClickBlock);
            NeoForge.EVENT_BUS.addListener(WingTooltips::onItemTooltip);
            eventBus.addListener(ChallengeBoardClient::onRegisterRenderers);
            NeoForge.EVENT_BUS.addListener(ChallengeBoardClient::onLoggingOut);
            NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, RecipeSyncClient::onRecipesReceived);
            eventBus.addListener(PyramidClient::onRegisterRenderers);
            eventBus.addListener(BeastTrophyClient::onRegisterRenderers);
            eventBus.<FMLClientSetupEvent>addListener(event -> clientSetup(event, eventBus));
        }
    }

    private void clientSetup(FMLClientSetupEvent event, IEventBus eventBus) {
        if (ModList.get().isLoaded("iris")) {
            event.enqueueWork(IrisGeckoGlow::register);
        }
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
