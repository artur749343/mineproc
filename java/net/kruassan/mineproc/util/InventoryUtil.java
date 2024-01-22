package net.kruassan.mineproc.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryUtil{
    public static boolean hasPlayerStackInInventory(PlayerEntity player, Item item) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.isOf(item)) {
                return true;
            }
        }
        return false;
    }
    public static int getFirstIndex(PlayerEntity player, Item item){
        for(int i=0;i<player.getInventory().size();i++){
            ItemStack stack=player.getInventory().getStack(i);
            if (stack.isEmpty() && stack.isOf(item)){
                return i;
            }
        }
        return -1;
    }
}
