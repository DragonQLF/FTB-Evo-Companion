package dev.ftb.mods.ftbevolutioncompanion.fabricator;

import com.mojang.serialization.Codec;
import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import java.util.List;
import java.util.Set;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FabricatorRegistry {
    private static final String MOD_ID = FTBEvolutionCompanion.MOD_ID;
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MOD_ID);
    private static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, MOD_ID);
    private static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MOD_ID);

    public static final DeferredBlock<FabricatorBlock> BLOCK = BLOCKS.registerBlock(
            "ftb_fabricator",
            FabricatorBlock::new,
            properties -> properties
                    .mapColor(MapColor.METAL)
                    .strength(4.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .pushReaction(PushReaction.BLOCK)
                    .lightLevel(state -> state.getValue(FabricatorBlock.WORKING) ? 8 : 0));
    public static final DeferredItem<FabricatorItem> ITEM = ITEMS.registerItem(
            "ftb_fabricator",
            properties -> new FabricatorItem(BLOCK.get(), properties),
            properties -> properties.useBlockDescriptionPrefix());
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FabricatorBlockEntity>> BLOCK_ENTITY =
            ENTITIES.register(
                    "ftb_fabricator", () -> new BlockEntityType<>(FabricatorBlockEntity::new, Set.of(BLOCK.get())));
    public static final DeferredHolder<MenuType<?>, MenuType<FabricatorMenu>> MENU =
            MENUS.register("ftb_fabricator", () -> IMenuTypeExtension.create(FabricatorMenu::new));
    public static final DeferredHolder<RecipeType<?>, RecipeType<FabricatorRecipe>> RECIPE_TYPE =
            TYPES.register("fabricating", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return MOD_ID + ":fabricating";
                }
            });
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FabricatorRecipe>> SERIALIZER =
            SERIALIZERS.register(
                    "fabricating", () -> new RecipeSerializer<>(FabricatorRecipe.CODEC, FabricatorRecipe.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<SimpleFluidContent>>> FLUIDS =
            COMPONENTS.registerComponentType(
                    "fabricator_fluids",
                    builder -> builder.persistent(SimpleFluidContent.CODEC.listOf())
                            .networkSynchronized(SimpleFluidContent.STREAM_CODEC.apply(ByteBufCodecs.list())));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY =
            COMPONENTS.registerComponentType(
                    "fabricator_energy",
                    builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    private FabricatorRegistry() {}

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        MENUS.register(bus);
        TYPES.register(bus);
        SERIALIZERS.register(bus);
        COMPONENTS.register(bus);
        bus.addListener(FabricatorRegistry::capabilities);
        bus.addListener(FabricatorRegistry::creativeTab);
        bus.addListener(FabricatorPayloads::register);
        NeoForge.EVENT_BUS.addListener(FabricatorRegistry::syncRecipes);
    }

    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                BLOCK_ENTITY.get(),
                (machine, side) -> machine.canConnect(side) ? machine.automationItems() : null);
        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                BLOCK_ENTITY.get(),
                (machine, side) -> machine.canConnect(side) ? machine.automationFluids() : null);
        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                BLOCK_ENTITY.get(),
                (machine, side) -> machine.canConnect(side) ? machine.energy() : null);
    }

    private static void creativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) event.accept(ITEM);
    }

    private static void syncRecipes(OnDatapackSyncEvent event) {
        event.sendRecipes(RECIPE_TYPE.get());
    }
}
