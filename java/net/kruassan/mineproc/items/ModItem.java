package net.kruassan.mineproc.items;


import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.kruassan.mineproc.Mineproc;
import net.kruassan.mineproc.block.ModBlock;
import net.kruassan.mineproc.items.custom.UpdaterItem;
import net.kruassan.mineproc.util.NbtMemory;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class ModItem {
    public static final Item Memorycell1=registerItem("register", new NbtMemory(new FabricItemSettings()));
    public static final Item Memorycell2=registerItem("micro_memory", new NbtMemory(new FabricItemSettings()));
    public static final Item Memorycell3=registerItem("updated_micro_memory", new NbtMemory(new FabricItemSettings()));
    public static final Item Memorycell4=registerItem("advanced_micro_memory", new NbtMemory(new FabricItemSettings()));
    public static final Item Memorycell5=registerItem("mini_memory", new NbtMemory(new FabricItemSettings()));
    public static final Item Memorycell6=registerItem("updated_mini_memory", new NbtMemory(new FabricItemSettings()));
    public static final Item Memorycell7=registerItem("advanced_mini_memory", new NbtMemory(new FabricItemSettings()));
    public static final Item Memorycell8=registerItem("memory_cell", new NbtMemory(new FabricItemSettings()));
    public static final Item Memorycell9=registerItem("updated_memory_cell", new NbtMemory(new FabricItemSettings()));
    public static final Item Memorycell10=registerItem("advanced_memory_cell", new NbtMemory(new FabricItemSettings()));
    public static final Item Video_card=registerItem("video_card", new Item(new FabricItemSettings().maxCount(1)));
    public static final Item Processor=registerItem("processor", new ProcessorItem(new FabricItemSettings().maxCount(1)));
    public static final Item Transistor=registerItem("transistor", new Item(new FabricItemSettings()));
    public static final Item Energy_block=registerItem("energy_block", new EnergyBlockItem(new FabricItemSettings().maxCount(1)));
    public static final Item Propeller=registerItem("propeller", new Item(new FabricItemSettings()));
    public static final Item Updater_block=registerItem("updater", new UpdaterItem(ModBlock.Updater, new FabricItemSettings()));

    private static void addItemsToIngredientTabItemGroup(FabricItemGroupEntries entries){
        entries.add(Memorycell1);
        entries.add(Memorycell2);
        entries.add(Memorycell3);
        entries.add(Memorycell4);
        entries.add(Memorycell5);
        entries.add(Memorycell6);
        entries.add(Memorycell7);
        entries.add(Memorycell8);
        entries.add(Memorycell9);
        entries.add(Memorycell10);
        entries.add(Video_card);
        entries.add(Propeller);
        entries.add(Transistor);
        entries.add(Processor);
        entries.add(Energy_block);
        entries.add(ModBlock.Monitor);
        entries.add(ModBlock.Computer);
        entries.add(ModBlock.SunPanel);
        entries.add(ModBlock.Sender);
        entries.add(ModBlock.Getter);
        entries.add(Updater_block);
    }

    public static boolean IsMemory(Item item){
        return item==Memorycell1||item==Memorycell2||item==Memorycell3||item==Memorycell4||item==Memorycell5||item==Memorycell6||item==Memorycell7||item==Memorycell8||item==Memorycell9||item==Memorycell10;
    }
    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(Mineproc.MOD_ID, name), item);
    }
    public static void registerModItems(){
        ItemGroupEvents.modifyEntriesEvent(ModItemGroup.Mineproc_group).register(ModItem::addItemsToIngredientTabItemGroup);
    }
}
