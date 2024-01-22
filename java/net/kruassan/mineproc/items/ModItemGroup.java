package net.kruassan.mineproc.items;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.kruassan.mineproc.Mineproc;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroup {

    public static final RegistryKey<ItemGroup> Mineproc_group = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(Mineproc.MOD_ID, "mineproc_group"));

}
