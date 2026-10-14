package dev.ftb.mods.ftbevolutioncompanion.content;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.challenge.ChallengeBoardAuxBlock;
import dev.ftb.mods.ftbevolutioncompanion.challenge.ChallengeBoardBlock;
import dev.ftb.mods.ftbevolutioncompanion.pyramid.SkylineBlock;
import dev.ftb.mods.ftbevolutioncompanion.pyramid.SkylineItem;
import dev.ftb.mods.ftbevolutioncompanion.pyramid.SkylinePartBlock;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CompanionContent {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FTBEvolutionCompanion.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FTBEvolutionCompanion.MOD_ID);
    public static final DeferredRegister.Blocks FTB_BLOCKS = DeferredRegister.createBlocks("ftb");
    public static final DeferredRegister.Items FTB_ITEMS = DeferredRegister.createItems("ftb");
    public static final DeferredRegister<BlockEntityType<?>> FTB_BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "ftb");

    public static final DeferredBlock<OddBerryBushBlock> ODD_BERRY_BUSH = BLOCKS.registerBlock(
            "odd_berry_bush",
            OddBerryBushBlock::new,
            properties -> properties
                    .mapColor(MapColor.PLANT)
                    .noCollision()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.SWEET_BERRY_BUSH)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY)
                    .ignitedByLava());

    public static final DeferredItem<OddBerryBushItem> ODD_BERRY_BUSH_ITEM = ITEMS.registerItem(
            "odd_berry_bush",
            properties -> new OddBerryBushItem(ODD_BERRY_BUSH.get(), properties),
            properties -> properties.useBlockDescriptionPrefix());

    public static final DeferredBlock<Block> NETHER_BEDROCK = FTB_BLOCKS.registerBlock(
            "nether_bedrock",
            Block::new,
            properties -> properties
                    .mapColor(MapColor.NETHER)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(-1.0F, 3600000.0F)
                    .sound(SoundType.NETHERRACK)
                    .noLootTable()
                    .isValidSpawn((state, level, pos, entityType) -> false));

    public static final DeferredBlock<Block> END_BEDROCK = FTB_BLOCKS.registerBlock(
            "end_bedrock",
            Block::new,
            properties -> properties
                    .mapColor(MapColor.SAND)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(-1.0F, 3600000.0F)
                    .noLootTable()
                    .isValidSpawn((state, level, pos, entityType) -> false));

    public static final DeferredItem<BlockItem> NETHER_BEDROCK_ITEM = FTB_ITEMS.registerItem(
            "nether_bedrock",
            properties -> new BlockItem(NETHER_BEDROCK.get(), properties),
            properties -> properties.useBlockDescriptionPrefix());

    public static final DeferredItem<BlockItem> END_BEDROCK_ITEM = FTB_ITEMS.registerItem(
            "end_bedrock",
            properties -> new BlockItem(END_BEDROCK.get(), properties),
            properties -> properties.useBlockDescriptionPrefix());

    public static final DeferredBlock<ChallengeBoardBlock> CHALLENGE_BOARD = BLOCKS.registerBlock(
            "challenge_board",
            ChallengeBoardBlock::new,
            properties ->
                    properties.mapColor(MapColor.COLOR_BLACK).strength(0.3F).sound(SoundType.METAL));

    public static final DeferredBlock<ChallengeBoardAuxBlock> CHALLENGE_BOARD_AUX = BLOCKS.registerBlock(
            "challenge_board_aux",
            ChallengeBoardAuxBlock::new,
            properties -> properties
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(0.3F)
                    .sound(SoundType.METAL)
                    .noLootTable());

    public static final DeferredItem<BlockItem> CHALLENGE_BOARD_ITEM = ITEMS.registerItem(
            "challenge_board",
            properties -> new BlockItem(CHALLENGE_BOARD.get(), properties),
            properties -> properties.useBlockDescriptionPrefix());

    public static final DeferredBlock<SkylineBlock> SKYLINE =
            BLOCKS.registerBlock("ftb_skyline", SkylineBlock::new, properties -> skylineProperties(properties));

    public static final DeferredBlock<SkylinePartBlock> SKYLINE_PART = BLOCKS.registerBlock(
            "ftb_skyline_part",
            SkylinePartBlock::new,
            properties -> skylineProperties(properties)
                    .dynamicShape()
                    .noLootTable()
                    .overrideDescription("block.ftbevolutioncompanion.ftb_skyline"));

    public static final DeferredItem<SkylineItem> SKYLINE_ITEM = ITEMS.registerItem(
            "ftb_skyline",
            properties -> new SkylineItem(SKYLINE.get(), properties),
            properties -> properties.useBlockDescriptionPrefix().stacksTo(1));

    public static final DeferredBlock<Block> ADAMANTITE_TURBINE_COIL = turbineCoil("adamantite_turbine_coil");
    public static final DeferredBlock<Block> AETERNIUM_TURBINE_COIL = turbineCoil("aeternium_turbine_coil");
    public static final DeferredBlock<Block> AURICHALCUM_TURBINE_COIL = turbineCoil("aurichalcum_turbine_coil");

    public static final DeferredItem<BlockItem> ADAMANTITE_TURBINE_COIL_ITEM =
            FTB_ITEMS.registerSimpleBlockItem(ADAMANTITE_TURBINE_COIL);
    public static final DeferredItem<BlockItem> AETERNIUM_TURBINE_COIL_ITEM =
            FTB_ITEMS.registerSimpleBlockItem(AETERNIUM_TURBINE_COIL);
    public static final DeferredItem<BlockItem> AURICHALCUM_TURBINE_COIL_ITEM =
            FTB_ITEMS.registerSimpleBlockItem(AURICHALCUM_TURBINE_COIL);

    public static final DeferredItem<BeastItem> DYNAMO_MATRIX =
            beastComponent("dynamo_matrix", Rarity.UNCOMMON, "first_tier");
    public static final DeferredItem<BeastItem> TECTONIC_INGOT =
            beastComponent("tectonic_ingot", Rarity.UNCOMMON, "first_tier");
    public static final DeferredItem<BeastItem> LIVING_CULTURE =
            beastComponent("living_culture", Rarity.RARE, "second_tier");
    public static final DeferredItem<BeastItem> ARCANE_CODEX =
            beastComponent("arcane_codex", Rarity.UNCOMMON, "first_tier");
    public static final DeferredItem<BeastItem> COGNITION_ARRAY =
            beastComponent("cognition_array", Rarity.UNCOMMON, "first_tier");
    public static final DeferredItem<BeastItem> STELLAR_CHART =
            beastComponent("stellar_chart", Rarity.RARE, "second_tier");
    public static final DeferredItem<BeastItem> ABYSSAL_LENS =
            beastComponent("abyssal_lens", Rarity.RARE, "second_tier");
    public static final DeferredItem<BeastItem> SOUL_COVENANT =
            beastComponent("soul_covenant", Rarity.RARE, "second_tier");
    public static final DeferredItem<BeastItem> HUNT_STANDARD =
            beastComponent("hunt_standard", Rarity.UNCOMMON, "first_tier");
    public static final DeferredItem<BeastItem> CAUSAL_ANCHOR =
            beastComponent("causal_anchor", Rarity.RARE, "second_tier");

    public static final DeferredItem<BeastItem> HUNTERS_MARK = beastComponent("hunters_mark", Rarity.COMMON, "part");
    public static final DeferredItem<BeastItem> DRAGON_RELIC = beastComponent("dragon_relic", Rarity.COMMON, "part");
    public static final DeferredItem<BeastItem> LOGIC_LATTICE = beastComponent("logic_lattice", Rarity.COMMON, "part");
    public static final DeferredItem<BeastItem> STORAGE_MATRIX =
            beastComponent("storage_matrix", Rarity.COMMON, "part");
    public static final DeferredItem<BeastItem> QUANTUM_ALLOY_LATTICE =
            beastComponent("quantum_alloy_lattice", Rarity.COMMON, "part");
    public static final DeferredItem<BeastItem> COMPOSITE_HULL_PLATING =
            beastComponent("composite_hull_plating", Rarity.COMMON, "part");
    public static final DeferredItem<BeastItem> CHARGE_CELL = beastComponent("charge_cell", Rarity.COMMON, "part");
    public static final DeferredItem<BeastItem> FUEL_CORE = beastComponent("fuel_core", Rarity.COMMON, "part");
    public static final DeferredItem<BeastItem> RUNE_PLATE = beastComponent("rune_plate", Rarity.COMMON, "part");
    public static final DeferredItem<BeastItem> BOUND_ESSENCE = beastComponent("bound_essence", Rarity.COMMON, "part");
    public static final DeferredItem<BeastItem> BEAST_CORE = beastComponent("beast_core", Rarity.EPIC, "assembly");
    public static final DeferredItem<BeastItem> BEAST_MIND = beastComponent("beast_mind", Rarity.EPIC, "assembly");
    public static final DeferredItem<BeastItem> BEAST_SOUL = beastComponent("beast_soul", Rarity.EPIC, "assembly");

    public static final DeferredItem<BeastItem> ULTRADENSE_ANTIMATTER = FTB_ITEMS.registerItem(
            "ultradense_antimatter",
            properties -> new BeastItem(properties, "item.ftb.ultradense_antimatter.tooltip"),
            properties -> properties.rarity(Rarity.EPIC).fireResistant());

    public static final DeferredItem<BeastItem> BEAST_HEART = FTB_ITEMS.registerItem(
            "beast_heart",
            properties -> new BeastItem(properties, "item.ftb.beast_heart.tooltip"),
            properties -> properties.rarity(Rarity.EPIC).stacksTo(1).fireResistant());

    public static final DeferredBlock<BeastTrophyBlock> BEAST_TROPHY = FTB_BLOCKS.registerBlock(
            "beast_trophy",
            BeastTrophyBlock::new,
            properties -> properties
                    .mapColor(MapColor.GOLD)
                    .strength(3.0F, 1200.0F)
                    .sound(SoundType.METAL)
                    .lightLevel(state -> 10)
                    .noOcclusion()
                    .pushReaction(PushReaction.BLOCK)
                    .isValidSpawn((state, level, pos, entityType) -> false));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BeastTrophyBlockEntity>>
            BEAST_TROPHY_BLOCK_ENTITY = FTB_BLOCK_ENTITIES.register(
                    "beast_trophy",
                    () -> new BlockEntityType<>(BeastTrophyBlockEntity::new, Set.of(BEAST_TROPHY.get())));

    public static final DeferredItem<BeastTrophyItem> BEAST_TROPHY_ITEM = FTB_ITEMS.registerItem(
            "beast_trophy",
            properties -> new BeastTrophyItem(BEAST_TROPHY.get(), properties),
            properties -> properties.useBlockDescriptionPrefix().rarity(Rarity.EPIC));

    static {
        BLOCKS.addAlias(FTBEvolutionCompanion.id("evolution_pyramid"), SKYLINE.getId());
        BLOCKS.addAlias(FTBEvolutionCompanion.id("evolution_pyramid_part"), SKYLINE_PART.getId());
        ITEMS.addAlias(FTBEvolutionCompanion.id("evolution_pyramid"), SKYLINE_ITEM.getId());
    }

    private static DeferredItem<BeastItem> beastComponent(String name, Rarity rarity, String tier) {
        return FTB_ITEMS.registerItem(
                name,
                properties -> new BeastItem(properties, "item.ftb.beast_component." + tier + ".tooltip"),
                properties -> properties.rarity(rarity));
    }

    private static DeferredBlock<Block> turbineCoil(String name) {
        return FTB_BLOCKS.registerBlock(
                name,
                Block::new,
                properties -> properties
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                        .requiresCorrectToolForDrops()
                        .strength(5.0F, 6.0F)
                        .sound(SoundType.METAL));
    }

    private static BlockBehaviour.Properties skylineProperties(BlockBehaviour.Properties properties) {
        return properties
                .mapColor(MapColor.QUARTZ)
                .strength(4.0F, 1200.0F)
                .sound(SoundType.METAL)
                .noOcclusion()
                .forceSolidOn()
                .pushReaction(PushReaction.BLOCK)
                .isValidSpawn((state, level, pos, entityType) -> false)
                .isRedstoneConductor((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false);
    }

    public static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(ODD_BERRY_BUSH_ITEM);
        }
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(NETHER_BEDROCK_ITEM);
            event.accept(END_BEDROCK_ITEM);
            event.accept(CHALLENGE_BOARD_ITEM);
            event.accept(SKYLINE_ITEM);
            event.accept(BEAST_TROPHY_ITEM);
            event.accept(ADAMANTITE_TURBINE_COIL_ITEM);
            event.accept(AETERNIUM_TURBINE_COIL_ITEM);
            event.accept(AURICHALCUM_TURBINE_COIL_ITEM);
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(HUNTERS_MARK);
            event.accept(DRAGON_RELIC);
            event.accept(LOGIC_LATTICE);
            event.accept(STORAGE_MATRIX);
            event.accept(QUANTUM_ALLOY_LATTICE);
            event.accept(COMPOSITE_HULL_PLATING);
            event.accept(CHARGE_CELL);
            event.accept(FUEL_CORE);
            event.accept(RUNE_PLATE);
            event.accept(BOUND_ESSENCE);
            event.accept(DYNAMO_MATRIX);
            event.accept(TECTONIC_INGOT);
            event.accept(LIVING_CULTURE);
            event.accept(ARCANE_CODEX);
            event.accept(COGNITION_ARRAY);
            event.accept(STELLAR_CHART);
            event.accept(ABYSSAL_LENS);
            event.accept(SOUL_COVENANT);
            event.accept(HUNT_STANDARD);
            event.accept(CAUSAL_ANCHOR);
            event.accept(ULTRADENSE_ANTIMATTER);
            event.accept(BEAST_CORE);
            event.accept(BEAST_MIND);
            event.accept(BEAST_SOUL);
            event.accept(BEAST_HEART);
        }
    }

    private CompanionContent() {}
}
