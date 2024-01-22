package net.kruassan.mineproc.datagen;


import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.kruassan.mineproc.block.ModBlock;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlock.Computer);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlock.Monitor);


    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
//        itemModelGenerator.register(ModItem.RUBY, Models.GENERATED);
//        itemModelGenerator.register(ModItem.RAW_RUBY, Models.GENERATED);
//
//        itemModelGenerator.register(ModItem.COAL_BRIQUETTE, Models.GENERATED);
//        itemModelGenerator.register(ModItem.TOMATO, Models.GENERATED);
//        itemModelGenerator.register(ModItem.METAL_DETECTOR, Models.GENERATED);
    }
}