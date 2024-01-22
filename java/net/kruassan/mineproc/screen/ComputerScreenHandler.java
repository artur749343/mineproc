package net.kruassan.mineproc.screen;


import net.kruassan.mineproc.Mineproc;
import net.kruassan.mineproc.block.entity.ComputerEntity;
import net.kruassan.mineproc.items.ModItem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

import java.util.Collection;


public class ComputerScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    public final ComputerEntity blockEntity;

    public ComputerScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf){
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()), new ArrayPropertyDelegate(3));
    }

    public ComputerScreenHandler(int syncId, PlayerInventory PlayerInventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate){
        super(ModScreenHandlers.COMPUTER_SCREEN_HANDLER, syncId);
        checkSize(((Inventory)blockEntity), 7);
        this.inventory=((Inventory)blockEntity);
        inventory.onOpen(PlayerInventory.player);
        this.propertyDelegate=arrayPropertyDelegate;
        this.blockEntity=((ComputerEntity) blockEntity);

        this.addSlot(new Slot(this.inventory, 0, 8, 1) {public boolean canInsert(ItemStack stack) {return ModItem.IsMemory(stack.getItem());}});
        this.addSlot(new Slot(this.inventory, 1, 8, 19) {public boolean canInsert(ItemStack stack) {return ModItem.IsMemory(stack.getItem());}});
        this.addSlot(new Slot(this.inventory, 2, 8, 37) {public boolean canInsert(ItemStack stack) {return ModItem.IsMemory(stack.getItem());}});
        this.addSlot(new Slot(this.inventory, 3, 8, 55) {public boolean canInsert(ItemStack stack) {return ModItem.IsMemory(stack.getItem());}});
        this.addSlot(new Slot(this.inventory, 4, 144, -17) {public boolean canInsert(ItemStack stack) {return ModItem.Processor==stack.getItem();}});
        this.addSlot(new Slot(this.inventory, 5, 144, 15) {public boolean canInsert(ItemStack stack) {return ModItem.Video_card==stack.getItem();}});
        this.addSlot(new Slot(this.inventory, 6,  144, 47) {public boolean canInsert(ItemStack stack) {return ModItem.Energy_block==stack.getItem();}});
        addPlayerInventory(PlayerInventory);
        addPlayerHotbar(PlayerInventory);
        addProperties(arrayPropertyDelegate);
    }
    public int ReadProperty(int index){
        return propertyDelegate.get(index);
    }
    public void SetProperty(int index, int value){
        propertyDelegate.set(index, value);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
    }
}