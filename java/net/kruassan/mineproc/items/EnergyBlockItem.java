package net.kruassan.mineproc.items;


import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnergyBlockItem extends Item {
    private static final int ITEM_BAR_COLOR = MathHelper.packRgb(0.4f, 0.4f, 1.0f);

    public EnergyBlockItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (!stack.hasNbt()){
            NbtCompound nbt=stack.getOrCreateNbt();
            nbt.putInt("energy", 0);
            nbt.putInt("max_energy", 1000);
            stack.setNbt(nbt);
        }
        tooltip.add(Text.literal(stack.getNbt().getInt("energy")+"/"+stack.getNbt().getInt("max_energy")));
        super.appendTooltip(stack, world, tooltip, context);
    }


    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return stack.getNbt().getInt("energy")>0;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.min(1 + 12 * stack.getNbt().getInt("energy")/stack.getNbt().getInt("max_energy"), 13);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return ITEM_BAR_COLOR;
    }
}
