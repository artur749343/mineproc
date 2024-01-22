package net.kruassan.mineproc.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
//        getOrCreateTagBuilder(ModTags.Blocks.METAL_DETECTOR_DETECTABLE_BLOCKS)
//                .add(ModBlock.RUBY_ORE)
//                .forceAddTag(BlockTags.GOLD_ORES)
//                .forceAddTag(BlockTags.EMERALD_ORES)
//                .forceAddTag(BlockTags.REDSTONE_ORES)
//                .forceAddTag(BlockTags.LAPIS_ORES)
//                .forceAddTag(BlockTags.DIAMOND_ORES)
//                .forceAddTag(BlockTags.IRON_ORES)
//                .forceAddTag(BlockTags.COPPER_ORES)
//                .forceAddTag(BlockTags.COAL_ORES);
//
//        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
//                .add(ModBlock.RAW_RUBY_BLOCK)
//                .add(ModBlock.RUBY_BLOCK)
//                .add(ModBlock.RUBY_ORE)
//                .add(ModBlock.DEEPSLATE_RUBY_ORE)
//                .add(ModBlock.NETHER_RUBY_ORE)
//                .add(ModBlock.END_STONE_RUBY_ORE)
//                .add(ModBlock.SOUND_BLOCK);
//
//        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
//                .add(ModBlock.RUBY_BLOCK);
//
//        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
//                .add(ModBlock.RAW_RUBY_BLOCK)
//                .add(ModBlock.RUBY_ORE);
//
//        getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
//                .add(ModBlock.DEEPSLATE_RUBY_ORE);
//
//        getOrCreateTagBuilder(TagKey.of(RegistryKeys.BLOCK, new Identifier("fabric", "needs_tool_level_4")))
//                .add(ModBlock.END_STONE_RUBY_ORE);
    }
}