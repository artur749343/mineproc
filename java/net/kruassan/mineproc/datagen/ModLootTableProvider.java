package net.kruassan.mineproc.datagen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
//        addDrop(ModBlock.RUBY_BLOCK);
//        addDrop(ModBlock.RAW_RUBY_BLOCK);
//        addDrop(ModBlock.SOUND_BLOCK);
//
//        addDrop(ModBlock.RUBY_ORE, copperLikeOreDrops(ModBlock.RUBY_ORE, ModItems.RAW_RUBY));
//        addDrop(ModBlock.DEEPSLATE_RUBY_ORE, copperLikeOreDrops(ModBlock.DEEPSLATE_RUBY_ORE, ModItem.RAW_RUBY));
//        addDrop(ModBlock.NETHER_RUBY_ORE, copperLikeOreDrops(ModBlock.NETHER_RUBY_ORE, ModItem.RAW_RUBY));
//        addDrop(ModBlock.END_STONE_RUBY_ORE, copperLikeOreDrops(ModBlock.END_STONE_RUBY_ORE, ModItem.RAW_RUBY));
    }

    public LootTable.Builder copperLikeOreDrops(Block drop, Item item) {
        return BlockLootTableGenerator.dropsWithSilkTouch(drop, (LootPoolEntry.Builder)this.applyExplosionDecay(drop,
                ((LeafEntry.Builder)
                        ItemEntry.builder(item)
                                .apply(SetCountLootFunction
                                        .builder(UniformLootNumberProvider
                                                .create(2.0f, 5.0f))))
                        .apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))));
    }
}