package net.kruassan.mineproc.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;

public class ModRecipeProvider extends FabricRecipeProvider {
//    private static final List<ItemConvertible> RUBY_SMELTABLES = List.of(ModItem.RAW_RUBY,
//            ModBlock.RUBY_ORE, ModBlock.DEEPSLATE_RUBY_ORE, ModBlock.NETHER_RUBY_ORE, ModBlock.END_STONE_RUBY_ORE);

    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
//        offerSmelting(exporter, RUBY_SMELTABLES, RecipeCategory.MISC, ModItem.RUBY,
//                0.7f, 200, "ruby");
//        offerBlasting(exporter, RUBY_SMELTABLES, RecipeCategory.MISC, ModItem.RUBY,
//                0.7f, 100, "ruby");
//
//        offerReversibleCompactingRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, ModItem.RUBY, RecipeCategory.DECORATIONS,
//                ModBlock.RUBY_BLOCK);
//
//        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItem.RAW_RUBY, 1)
//                .pattern("SSS")
//                .pattern("SRS")
//                .pattern("SSS")
//                .input('S', Items.STONE)
//                .input('R', ModItem.RUBY)
//                .criterion(hasItem(Items.STONE), conditionsFromItem(Items.STONE))
//                .criterion(hasItem(ModItem.RUBY), conditionsFromItem(ModItem.RUBY))
//                .offerTo(exporter, new Identifier(getRecipeName(ModItem.RAW_RUBY)));
    }
}