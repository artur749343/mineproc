package net.kruassan.mineproc.util;

import net.kruassan.mineproc.items.ModItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NbtMemory extends Item {
    public NbtMemory(Settings settings){
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand){
        return super.use(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(stack.hasNbt()){
            int nbt=(int)Math.pow(2, stack.getNbt().getInt("mineproc.memory_speed"));
            tooltip.add(Text.literal("Memory Speed x"+nbt));
        } else {
            int size=0;
            if (stack.isOf(ModItem.Memorycell1)){
                size=1;
            } else if (stack.isOf(ModItem.Memorycell2)){
                size=4;
            } else if (stack.isOf(ModItem.Memorycell3)){
                size=16;
            } else if (stack.isOf(ModItem.Memorycell4)){
                size=64;
            } else if (stack.isOf(ModItem.Memorycell5)){
                size=256;
            } else if (stack.isOf(ModItem.Memorycell6)){
                size=1024;
            } else if (stack.isOf(ModItem.Memorycell7)){
                size=4096;
            } else if (stack.isOf(ModItem.Memorycell8)){
                size=16384;
            } else if (stack.isOf(ModItem.Memorycell9)){
                size=65536;
            } else if (stack.isOf(ModItem.Memorycell10)){
                size=262144;
            }
            NbtCompound nbt = new NbtCompound();
            nbt.putInt("mineproc.memory_speed", 0);
            nbt.putByteArray("mineproc.data", new byte[size]);
            stack.setNbt(nbt);
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
