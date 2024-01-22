package net.kruassan.mineproc.items;

import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;




public class ProcessorItem
        extends Item {
    public static final String ITEMS_KEY = "Items";
    public static final int MAX_STORAGE = 1024;
    private static final int PROCESSOR_ITEM_OCCUPANCY = 4;
    private static final int ITEM_BAR_COLOR = MathHelper.packRgb(0.4f, 0.4f, 1.0f);
    public int cores=1;

    public ProcessorItem(Item.Settings settings) {
        super(settings);
    }

    public static float getAmountFilled(ItemStack stack) {
        return (float) net.kruassan.mineproc.items.ProcessorItem.getProcessorOccupancy(stack) / (float)MAX_STORAGE;
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT) {
            return false;
        }
        ItemStack itemStack = slot.getStack();
        if (itemStack.isEmpty()) {
//            this.playRemoveOneSound(player);
            net.kruassan.mineproc.items.ProcessorItem.removeFirstStack(stack).ifPresent(removedStack -> net.kruassan.mineproc.items.ProcessorItem.addToProcessor(stack, slot.insertStack((ItemStack)removedStack)));
        } else if (itemStack.getItem().canBeNested()) {
            int i = (MAX_STORAGE - net.kruassan.mineproc.items.ProcessorItem.getProcessorOccupancy(stack)) / net.kruassan.mineproc.items.ProcessorItem.getItemOccupancy(itemStack.getNbt());
            int j = net.kruassan.mineproc.items.ProcessorItem.addToProcessor(stack, slot.takeStackRange(itemStack.getCount(), i, player));
            if (j > 0) {
//                this.playInsertSound(player);
            }
        }
        return true;
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType != ClickType.RIGHT || !slot.canTakePartial(player)) {
            return false;
        }
        if (otherStack.isEmpty()) {
            net.kruassan.mineproc.items.ProcessorItem.removeFirstStack(stack).ifPresent(itemStack -> {
//                this.playRemoveOneSound(player);
                cursorStackReference.set((ItemStack)itemStack);
            });
        } else {
            int i = net.kruassan.mineproc.items.ProcessorItem.addToProcessor(stack, otherStack);
            if (i > 0) {
//                this.playInsertSound(player);
                otherStack.decrement(i);
            }
        }
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (net.kruassan.mineproc.items.ProcessorItem.dropAllProcessorItems(itemStack, user)) {
//            this.playDropContentsSound(user);
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.success(itemStack, world.isClient());
        }
        return TypedActionResult.fail(itemStack);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return net.kruassan.mineproc.items.ProcessorItem.getProcessorOccupancy(stack) > 0;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.min(1 + 12 * net.kruassan.mineproc.items.ProcessorItem.getProcessorOccupancy(stack) / MAX_STORAGE, 13);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return ITEM_BAR_COLOR;
    }

    private static int addToProcessor(ItemStack Processor, ItemStack stack) {
        if (stack.isEmpty() || !stack.getItem().canBeNested() || !stack.isOf(ModItem.Transistor)) return 0;

        NbtCompound nbtCompound = Processor.getOrCreateNbt();
        if (!nbtCompound.contains(ITEMS_KEY)) nbtCompound.put(ITEMS_KEY, new NbtList());

        int i = net.kruassan.mineproc.items.ProcessorItem.getProcessorOccupancy(Processor);
        int j = net.kruassan.mineproc.items.ProcessorItem.getItemOccupancy(stack.getNbt());
        int k = Math.min(stack.getCount(), (MAX_STORAGE - i) / j);
        if (k == 0) return 0;

        NbtList nbtList = nbtCompound.getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE);
        Optional<NbtElement> optional = ProcessorItem.canMergeStack(stack, nbtList);
        if (optional.isPresent()) {
            NbtCompound nbtCompound2 = (NbtCompound) optional.get();
            nbtCompound2.putInt("Count", nbtCompound2.getInt("Count")+k);
        } else {
            ItemStack itemStack2 = stack.copyWithCount(k);
            NbtCompound nbtCompound3 = new NbtCompound();
            itemStack2.writeNbt(nbtCompound3);
            nbtCompound3.putInt("Count", nbtCompound3.getInt("Count"));
            nbtList.add(0, nbtCompound3);
        }
        return k;
    }

    private static Optional<NbtElement> canMergeStack(ItemStack stack, NbtList items) {
        if (stack.isOf(ModItem.Processor)) return Optional.empty();
        return items.stream().filter(item -> canCombine((NbtCompound) item, stack)).findFirst();
    }

    public static boolean canCombine(NbtCompound nbt, ItemStack otherStack) {
        if (!Objects.equals(nbt.getString("id"), "mineproc:"+otherStack.getItem())) return false;
        if (Objects.equals(nbt.getString("id"), "minecraft:air") && otherStack.isEmpty()) return true;
        return Objects.equals(ItemStack.fromNbt(nbt).getNbt(), otherStack.getNbt());
    }

    private static int getItemOccupancy(NbtCompound nbt) {
        ItemStack stack=ItemStack.fromNbt(nbt);
        NbtCompound nbtCompound;
        if (stack.isOf(ModItem.Processor)) {
            return 4 + net.kruassan.mineproc.items.ProcessorItem.getProcessorOccupancy(stack);
        }
        if ((stack.isOf(Items.BEEHIVE) || stack.isOf(Items.BEE_NEST)) && stack.hasNbt() && (nbtCompound = BlockItem.getBlockEntityNbt(stack)) != null && !nbtCompound.getList("Bees", NbtElement.COMPOUND_TYPE).isEmpty()) {
            return 64;
        }
        return 64 / stack.getMaxCount();
    }

    private static int getProcessorOccupancy(ItemStack stack) {
        return net.kruassan.mineproc.items.ProcessorItem.getProcessorStacks(stack).mapToInt(nbt -> net.kruassan.mineproc.items.ProcessorItem.getItemOccupancy(nbt) * nbt.getInt("Count")).sum();
    }

    private static Optional<ItemStack> removeFirstStack(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains(ITEMS_KEY)) {
            return Optional.empty();
        }
        NbtList nbtList = nbtCompound.getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE);
        if (nbtList.isEmpty()) {
            return Optional.empty();
        }

        NbtCompound nbtCompound2 = nbtList.getCompound(0);
        ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
        if (64<nbtCompound2.getInt("Count")){
            itemStack.setCount(64);
            nbtCompound2.putInt("Count", nbtCompound2.getInt("Count")-64);
            nbtList.set(0, nbtCompound2);
        }
        else {
            nbtList.remove(0);
        }

        if (nbtList.isEmpty()) {
            stack.removeSubNbt(ITEMS_KEY);
        }
        return Optional.of(itemStack);
    }

    private static boolean dropAllProcessorItems(ItemStack stack, PlayerEntity player) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains(ITEMS_KEY)) {
            return false;
        }
        if (player instanceof ServerPlayerEntity) {
            NbtList nbtList = nbtCompound.getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE);
            while (!nbtList.isEmpty()){
                NbtCompound nbtCompound2 = nbtList.getCompound(0);

                ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
                if (64<nbtCompound2.getInt("Count")){
                    itemStack.setCount(64);
                    nbtCompound2.putInt("Count", nbtCompound2.getInt("Count")-64);
                    nbtList.set(0, nbtCompound2);
                }
                else {
                    nbtList.remove(0);
                }

                player.dropItem(itemStack, true);
            }
        }
        stack.removeSubNbt(ITEMS_KEY);
        return true;
    }

    private static Stream<NbtCompound> getProcessorStacks(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound == null) {
            return Stream.empty();
        }
        NbtList nbtList = nbtCompound.getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE);
        return nbtList.stream().map(NbtCompound.class::cast);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        net.kruassan.mineproc.items.ProcessorItem.getProcessorStacks(stack).forEach(nbt->defaultedList.add(new ItemStack(Registries.ITEM.get(new Identifier(nbt.getString("id"))), nbt.getInt("Count"))));
        return Optional.of(new BundleTooltipData(defaultedList, net.kruassan.mineproc.items.ProcessorItem.getProcessorOccupancy(stack)));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal(cores+" cores "+get_processor_speed(stack)+" Gz"));
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.literal(ProcessorItem.getProcessorOccupancy(stack)+"/"+MAX_STORAGE).formatted(Formatting.GRAY));
    }

    public static int get_processor_speed(ItemStack stack){
        int res=0;
        for(int n: getProcessorStacks(stack).map(nbt->nbt.getInt("Count")).toList()){
            res+=n;
        }
        return res;
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        ItemUsage.spawnItemContents(entity, net.kruassan.mineproc.items.ProcessorItem.getProcessorStacks(entity.getStack()).map(ItemStack::fromNbt));
    }

    //    private void playRemoveOneSound(Entity entity) {
//        entity.playSound(SoundEvents.ITEM_Processor_REMOVE_ONE, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
//    }
//
//    private void playInsertSound(Entity entity) {
//        entity.playSound(SoundEvents.ITEM_Processor_INSERT, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
//    }
//
//    private void playDropContentsSound(Entity entity) {
//        entity.playSound(SoundEvents.ITEM_Processor_DROP_CONTENTS, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
//    }
}
