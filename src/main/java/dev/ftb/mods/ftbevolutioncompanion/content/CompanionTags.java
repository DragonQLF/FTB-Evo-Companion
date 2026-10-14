package dev.ftb.mods.ftbevolutioncompanion.content;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public final class CompanionTags {
    public static final TagKey<Block> ODD_BERRY_BUSH_SPREADABLE =
            TagKey.create(Registries.BLOCK, FTBEvolutionCompanion.id("odd_berry_bush_spreadable"));

    public static final TagKey<EntityType<?>> DARK_DIRT_BLACKLIST =
            TagKey.create(Registries.ENTITY_TYPE, FTBEvolutionCompanion.id("dark_dirt_blacklist"));

    public static final TagKey<EntityType<?>> SPIKED_PLATE_BLACKLIST =
            TagKey.create(Registries.ENTITY_TYPE, FTBEvolutionCompanion.id("spiked_plate_blacklist"));

    private CompanionTags() {}
}
