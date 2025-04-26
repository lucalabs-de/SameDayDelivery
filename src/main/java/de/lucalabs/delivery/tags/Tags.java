package de.lucalabs.delivery.tags;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class Tags {

    public static final TagKey<Block> BARRELS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("c", "wooden_barrels"));

    private Tags() {}
}
